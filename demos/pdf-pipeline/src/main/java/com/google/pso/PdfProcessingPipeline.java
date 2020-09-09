package com.google.pso;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.cloud.automl.v1.AutoMlClient;
import com.google.cloud.automl.v1.ModelName;
import com.google.protobuf.Empty;
import com.google.pso.transformations.PdfTransformation;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.extensions.protobuf.ProtoCoder;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TypeDescriptor;
import com.google.cloud.automl.v1.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PdfProcessingPipeline {
    public static void main(String[] args) {
        String projectId = "zhong-gcp";
        String modelId = "ICN6041757021001220096";

        System.out.println("start the pipeline");

        // Register Options class for our pipeline with the factory
        PipelineOptionsFactory.register(PdfProcessingPipelineOptions.class);

        PdfProcessingPipelineOptions options = PipelineOptionsFactory.fromArgs(args)
                .withValidation()
                .as(PdfProcessingPipelineOptions.class);

        final String GCP_PROJECT_NAME = options.getProject();
        final String input_pubsub_subscription = "projects/" + GCP_PROJECT_NAME + "/subscriptions/"
                + options.getSubscription();
        final String out_pubsub_subscription = "projects/" + GCP_PROJECT_NAME + "/subscriptions/"
                + options.getSubscription();


        TableReference filesTableReference = new TableReference().
                setProjectId(options.getProject()).
                setDatasetId(options.getPipelineDatasetName()).
                setTableId(options.getFilesTableName());

        TableReference pagesTableReference = new TableReference().
                setProjectId(options.getProject()).
                setDatasetId(options.getPipelineDatasetName()).
                setTableId(options.getPagesTableName());

        Pipeline p = Pipeline.create(options);
        CoderRegistry registry = p.getCoderRegistry();
        registry.registerCoderForClass(Document.class, ProtoCoder.of(Document.class));
        registry.registerCoderForClass(Page.class, ProtoCoder.of(Page.class));

        PCollectionTuple documentsAndPages = p.apply("Process Newly Uploaded PDF file",
                PubsubIO.readMessagesWithAttributes()
                        .fromSubscription(input_pubsub_subscription)).
                apply("Process a single document",
                        new PdfTransformation("zhong-gcp", "images"));


        documentsAndPages.get(PdfTransformation.documentTupleTag).setCoder(ProtoCoder.of(Document.class)).
                apply( "map doc element to bq row", MapElements.into(TypeDescriptor.of(TableRow.class))
                        .via(doc -> mapDocToTableRow(doc))).
                apply("write a document to BQ", BigQueryIO.writeTableRows().to(filesTableReference).
                        withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER).
                        withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));

        documentsAndPages.get(PdfTransformation.pageTupleTag).setCoder(ProtoCoder.of(Page.class)).
                apply( "map page element to bq row", MapElements.into(TypeDescriptor.of(TableRow.class))
                .via(page -> mapPageToTableRow(page))).
                apply("write pages to BQ", BigQueryIO.writeTableRows().to(pagesTableReference).
                        withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER).
                        withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
        p.run();
    }

    private static TableRow mapDocToTableRow(Document document) {

        DateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        simple.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date(System.currentTimeMillis());

        TableRow row = new TableRow();
        row.set("file_md5", document.getDocHash());
        row.set("file_path", document.getDocPath());
        row.set("filename", document.getDocName());
        row.set("num_pages", document.getNumPages());
        row.set("status", "processing");
        row.set("inserted_at", simple.format(date));
        return row;
    }

    private static TableRow mapPageToTableRow(Page page) {
        DateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        simple.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date(System.currentTimeMillis());

        TableRow row = new TableRow();
        row.set("file_id", page.getFileId());
        row.set("page_index", page.getPageIndex());
        row.set("page_md5", page.getPageMd5());
        row.set("status", "processing");
        row.set("inserted_at", simple.format(date));
        return row;
    }
}

