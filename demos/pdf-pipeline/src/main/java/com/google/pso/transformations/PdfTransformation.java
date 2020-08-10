package com.google.pso.transformations;

import com.google.pso.Document;
import com.google.pso.Page;
import com.google.pso.util.PdfProcessor;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PdfTransformation extends PTransform<
        PCollection<PubsubMessage>, PCollectionTuple> {

    public static final TupleTag<Document> documentTupleTag =
            new TupleTag<>();
    public static final TupleTag<Page> pageTupleTag =
            new TupleTag<>();
    private final Logger Log = LoggerFactory.getLogger(PdfTransformation.class);

    private String bucket;
    private String pathPrefix;
    private PdfProcessor processor;

    public PdfTransformation(String bucket, String pathPrefix) {
        this.bucket = bucket;
        this.pathPrefix = pathPrefix;
        this.processor = new PdfProcessor(bucket, pathPrefix);
    }

    @Override
    public PCollectionTuple expand(PCollection<PubsubMessage> input) {
        Log.info("start processing");
        return input.apply("process single pdf", ParDo.of(
                new ProcessSinglePDF()
        ).withOutputTags(documentTupleTag, TupleTagList.of(pageTupleTag)));
    }

    class ProcessSinglePDF extends DoFn<PubsubMessage, Document> {
        @ProcessElement
        public void process(ProcessContext context) {
            PubsubMessage msg = context.element();
            KV<Document, Iterable<Page>> results = processor.process(
                    msg.getAttribute("bucketId"), msg.getAttribute("objectId"));

            for (Page page: results.getValue()) {
                context.output(pageTupleTag, page);
            }
            context.output(documentTupleTag, results.getKey());
        }
    }
}

