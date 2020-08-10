terraform {
  backend "gcs" {
    bucket = "zhong-gcp"
    prefix = "terraform/state"
  }
}

provider "google" {
  version = "~> 2.20"
  project = "zhong-gcp"
}


resource "google_pubsub_topic" "gcs-notification" {
  name = "gcs-notification"
  labels = {
    category = "pdf-pipeline"
  }
}


resource "google_pubsub_subscription" "gcs-notification-subscription" {
  name = "gcs-notification-subscription"
  topic = google_pubsub_topic.gcs-notification.name

  ack_deadline_seconds = 60

  labels = {
    category = "pdf-pipeline"
  }
}

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
