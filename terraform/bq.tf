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

  schema = file("schema/sample.json")
}

resource "google_bigquery_table" "data_clustering" {
  dataset_id = google_bigquery_dataset.samples.dataset_id
  table_id = "data_clustering"

  labels = {
    env = "dev"
  }

  schema = file("schema/cluster.json")
  time_partitioning {
    type = "DAY"
    field = "inserted_at"
  }
  clustering = ["source"]
}