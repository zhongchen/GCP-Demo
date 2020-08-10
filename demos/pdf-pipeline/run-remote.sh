#!/usr/bin/env bash

mvn compile exec:java \
  -Dexec.mainClass=com.google.pso.PdfProcessingPipeline \
  -Dexec.args="--project=zhong-gcp \
  --stagingLocation=gs://zhong-gcp/staging/ \
  --gcpTempLocation=gs://zhong-gcp/temp/ \
  --runner=DataflowRunner \
  --region=us-west1"
