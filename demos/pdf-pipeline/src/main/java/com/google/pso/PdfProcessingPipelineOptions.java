package com.google.pso;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;

public interface PdfProcessingPipelineOptions extends DataflowPipelineOptions {
    @Description("Subscription name")
    @Default.String("gcs-notification-subscription")
    String getSubscription();
    void setSubscription(String subscription);

    @Description("BQ table to store all files info")
    @Default.String("files")
    String getFilesTableName();
    void setFilesTableName(String tableName);

    @Description("BQ table to store all pages info")
    @Default.String("pages")
    String getPagesTableName();
    void setPagesTableName(String tableName);

    @Description("BQ dataset to pdf pipeline info")
    @Default.String("pdf_pipeline")
    String getPipelineDatasetName();
    void setPipelineDatasetName(String datasetName);
}
