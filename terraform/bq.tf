resource "google_bigquery_dataset" "samples" {
  dataset_id = "samples"

  labels = {
    env = "dev"
  }
}

resource "google_bigquery_table" "data" {
  dataset_id = google_bigquery_dataset.samples.dataset_id
  table_id = "data"

  labels = {
    env = "dev"
  }

  schema = file("/workspace/terraform/schema/sample.json")
}