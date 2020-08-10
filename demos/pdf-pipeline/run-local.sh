mvn compile exec:java \
  -Dexec.mainClass=com.google.pso.PdfProcessingPipeline \
  -Dexec.args="--project=zhong-gcp \
  --stagingLocation=gs://zhong-gcp/staging/ \
  --gcpTempLocation=gs://zhong-gcp/temp/ \
  --runner=DirectRunner \
  --region=us-west1"
