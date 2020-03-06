locals {
  function_name = "upload-sample-table"
  source_code_path = "../upload-sample-table/"
   // we append the app hash to the filename as a temporary workaround for https://github.com/terraform-providers/terraform-provider-google/issues/1938
  filename_on_gcs = "${local.function_name}-${lower(replace(base64encode(data.archive_file.upload-sample-table.output_md5), "=", ""))}.zip"
}

data "archive_file" "upload-sample-table" {
  type = "zip"
  source_dir = local.source_code_path
  output_path = "dist/${local.function_name}.zip"
}

resource "google_storage_bucket_object" "upload-sample-table" {
  source = data.archive_file.upload-sample-table.output_path
  bucket = google_storage_bucket.gcs-cloudfunction-demo.name
  name = "cloudfunctions/${local.filename_on_gcs}"
}

resource "google_cloudfunctions_function" "upload-sample-table" {
  name =  local.function_name
  runtime = "python37"
  region = "us-central1"
  available_memory_mb = 256
  source_archive_bucket = google_storage_bucket.gcs-cloudfunction-demo.name
  source_archive_object = google_storage_bucket_object.upload-sample-table.name
  event_trigger {
    event_type = "google.storage.object.finalize"
    resource = "zhong-gcp"
  }
  entry_point = "upload_sample_table"
}