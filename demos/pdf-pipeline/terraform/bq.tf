resource "google_bigquery_dataset" "pdf-pipeline" {
  dataset_id = "pdf_pipeline"
  location = "US"

  labels = {
    category = "pdf-pipeline"
  }
}

resource "google_bigquery_table" "files" {
  dataset_id = google_bigquery_dataset.pdf-pipeline.dataset_id
  table_id = "files"

  schema = file("schema/files.json")
}

resource "google_bigquery_table" "pages" {
  dataset_id = google_bigquery_dataset.pdf-pipeline.dataset_id
  table_id = "pages"

  schema = file("schema/pages.json")
}
