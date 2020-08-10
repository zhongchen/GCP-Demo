package com.google.pso;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.automl.v1.*;
import com.google.protobuf.Empty;

public class TestAutoML {
    public static void main(String[] args) {
        String projectId = "zhong-gcp";
        String modelId = "ICN6041757021001220096";

        System.out.println("start the pipeline");

        try (AutoMlClient client = AutoMlClient.create()) {
            ModelName modelFullId = ModelName.of(projectId, "us-central1", modelId);

            GetModelRequest modelRequest = GetModelRequest.newBuilder().setName(
                    modelFullId.toString()).build();

            Model model = client.getModel(modelRequest);

            System.out.println(model.getImageClassificationModelMetadata().getNodeCount());

            ImageClassificationModelDeploymentMetadata metadata =
                    ImageClassificationModelDeploymentMetadata.newBuilder().setNodeCount(2).build();
            DeployModelRequest request =
                    DeployModelRequest.newBuilder()
                            .setName(modelFullId.toString())
                            .setImageClassificationModelDeploymentMetadata(metadata)
                            .build();

            OperationFuture<Empty, OperationMetadata> future = client.deployModelAsync(request);

            System.out.println(future.getName());
            System.out.println("Model deployment finished");

            model = client.getModel(modelRequest);
            System.out.println(model.getImageClassificationModelMetadata().getNodeCount());
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
