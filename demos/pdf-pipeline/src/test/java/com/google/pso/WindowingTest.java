package com.google.pso;

import org.apache.beam.sdk.testing.NeedsRunner;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.Serializable;

import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.testing.*;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.transforms.windowing.*;
import org.apache.beam.sdk.values.*;
import org.joda.time.*;
import org.junit.*;
import org.slf4j.*;

@RunWith(JUnit4.class)
@Category(NeedsRunner.class)
public class WindowingTest implements Serializable {

    @Before
    public void setUp() throws Exception {
    }

    static Logger LOG = LoggerFactory.getLogger(WindowingTest.class);

    /* The Data that is expected in the Pipeline. You may use
     * generated Avro objects (or your normal data objects), but this
     * is a custom made one. Serializable, equals(), and toString() are
     * all required.
     */
    static class MyData implements Serializable {
        final private String key;
        final private String data;
        final private Instant timestamp;

        MyData(String key, String data, Instant timestamp) {
            this.key = key;
            this.data = data;
            this.timestamp = timestamp;
        }

        public String toString() {
            return key + "," + data + "," + timestamp.toString();
        }

        public boolean equals(Object x) {
            MyData o = (MyData) x;
            return (key.equals(key) && data.equals(o.data) && timestamp.equals(timestamp));
        }
    }

    /* TimedMyData is used for the test stream of data. It is annotated with
     * a timestamp for purposes of processing. Here we include the same
     * timestamp on the data itself.
     */
    static class TimedMyData extends TimestampedValue<MyData> {
        protected TimedMyData(MyData value, Instant timestamp) {
            super(value, timestamp);
        }
    }

    // Helper to create data
    static TimedMyData getData(String key, String value, Instant i) {
        return new TimedMyData(new MyData(key, value, i), i);
    }

    // Test pipeline
    @Rule
    public final transient TestPipeline pipe = TestPipeline.create();

    @Test
    public void streamingTest() {

        /* Create the stream of events for our test. This contains a stream of timestamped data,
         * advance-processing-time directives, and advance-watermark directives. By using base
         * at instant 0L (ie, Jan 1st, 1970) this gives us a consistent way to measure our data.
         */
        Instant base = new Instant(0L);
        TestStream<MyData> infos = TestStream.create(SerializableCoder.of(MyData.class))
                .addElements(
                        getData("mem1", "v1", base),
                        getData("mem2", "v2a", base.plus(4000)))
                .advanceProcessingTime(Duration.standardSeconds(4))
                .addElements(
                        getData("mem2", "v2b", base.plus(4100)),
                        getData("mem3", "v3", base.plus(5000)),
                        getData("mem4", "v4", base.plus(8000)),
                        getData("mem5", "v5a", base.plus(9000)),
                        getData("mem6", "v6", base.plus(10000)),
                        getData("mem7", "v7", base.plus(11500)))
                .advanceWatermarkTo(base.plus(20000))
                .advanceProcessingTime(Duration.standardSeconds(20))
                .addElements(
                        getData("mem2", "v2c", base.plus(20000)),
                        getData("mem5", "v5b", base.plus(23000)),
                        getData("mem8", "v8", base.plus(24000)))
                .advanceProcessingTime(Duration.standardSeconds(6))
                .addElements(
                        getData("mem10", "v10", base.plus(25000)),
                        getData("mem11", "v11", base.plus(25000)),
                        getData("mem2", "v2d", base.plus(25000)))
                .advanceProcessingTime(Duration.standardSeconds(30))
                .advanceWatermarkToInfinity();

        /* Create our pipeline from the stream.
         *
         * We will apply the normal steps would we do in production.
         */
        PCollection<MyData> p = pipe.apply(infos);

        // Extract the key from the data for grouping
        PCollection<KV<String, MyData>> g = p.apply("Split", ParDo.of(new DoFn<MyData, KV<String, MyData>>() {
            @ProcessElement
            public void processElement(ProcessContext e) {
                e.output(KV.of(e.element().key, e.element()));
            }
        }));

        // Apply windowing
        g = g.apply("Window", Window.<KV<String, MyData>>into(
                (Sessions.withGapDuration(Duration.standardSeconds(10))))
                .triggering(
                        Repeatedly.forever(AfterPane.elementCountAtLeast(1))
                )
                .accumulatingFiredPanes()
                .withAllowedLateness(Duration.standardSeconds(1))
        );

        // Before we group, let's apply a step that just does logging with lots of detail.
        // This is more for development rather than testing, but once we have what we want
        // we can turn it into a unit test.
        g = g.apply("DebugEarly", ParDo.of(new DoFn<KV<String, MyData>, KV<String, MyData>>() {
            @ProcessElement
            public void processElement(ProcessContext e, BoundedWindow w) {
                LOG.info("PriorElement: " + e.element().getKey() + "->" + e.element().getValue().data + "@" + e.timestamp() + ", " +
                        e.pane().toString() + ", " +
                        e.pane().getTiming() + ", " + w.maxTimestamp());
                e.output(e.element());
            }
        }));

        // Group by the key, and define our combiner which will simply list all of the data values
        // This is useful for development as we can see what is going together in the group depending
        // on the windowing configuration.
        g = g.apply("Group", Combine.perKey((x) -> {
            StringBuilder sb = new StringBuilder();
            long t = Long.MAX_VALUE;
            for (MyData d : x) {
                sb.append(d.data);
                if (d.timestamp.getMillis() < t) {
                    t = d.timestamp.getMillis();
                }
            }
            return new MyData("", "(" + sb.toString() + ")", new Instant(t));
        }));

        // Remove the redundant key.
        PCollection<MyData> f = g.apply("Convert", ParDo.of(new DoFn<KV<String, MyData>, MyData>() {
            @ProcessElement
            public void processElement(ProcessContext e, BoundedWindow w) {
                e.output(new MyData(e.element().getKey(), e.element().getValue().data, e.element().getValue().timestamp));
            }
        }));

        // Finally, as the last step let's debug again.
        //
        // You can use methods like PAssert.that(f).containsInAnyOrder(1,2) to make assertions
        // of the data that you expect here.
        //
        f = f.apply("Debug", ParDo.of(new DoFn<MyData, MyData>() {
            @ProcessElement
            public void processElement(ProcessContext e, BoundedWindow w) {
                LOG.info("FinalElement: " + e.element().key + "->" + e.element().data + "@" + e.element().timestamp + ", " +
                        e.pane().getIndex() + ", " +
                        e.pane().getTiming() + ", " + e.timestamp() + ":" + w.maxTimestamp());
                e.output(e.element());
            }
        }));

        // Run the pipe with debugging, assertions, and all.
        pipe.run();
    }
}
