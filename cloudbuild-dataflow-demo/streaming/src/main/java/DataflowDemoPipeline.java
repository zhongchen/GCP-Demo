import jdk.internal.jline.internal.Log;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataflowDemoPipeline {
    private static final Logger LOG = LoggerFactory.getLogger(DataflowDemoPipeline.class);
    
    public static void main(String[] args){

        // Register Options class for our pipeline with the factory
        PipelineOptionsFactory.register(DemoPipelineOptions.class);

        DemoPipelineOptions options = PipelineOptionsFactory.fromArgs(args)
                .withValidation()
                .as(DemoPipelineOptions.class);

        final String GCP_PROJECT_NAME = options.getProject();
        final String PUBSUB_SUBSCRIPTION = "projects/" +GCP_PROJECT_NAME+"/subscriptions/"
                +options.getSubscription();
        final String BUILD_NUMBER = options.getBuildNumber();

        LOG.info(String.format("Creating the pipeline. The build number is %s", BUILD_NUMBER));

        Pipeline p = Pipeline.create(options);


        // 1. Read messages from Pub/sub
        PCollection<PubsubMessage> pubsubMessagePCollection = p.apply("Read PubSub Messages",
                PubsubIO.readMessagesWithAttributes()
                        .fromSubscription(PUBSUB_SUBSCRIPTION)
        );

        pubsubMessagePCollection.apply("Dummy Transformation", ParDo.of(new DummyTransformation()));

        p.run();
    }
}

class DummyTransformation extends DoFn<PubsubMessage, PubsubMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(DummyTransformation.class);

    @ProcessElement
    public void process(ProcessContext context) {
        LOG.info(String.format("Received message %s", new String(context.element().getPayload())));
        PubsubMessage msg = context.element();
        context.output(msg);
    }
}
