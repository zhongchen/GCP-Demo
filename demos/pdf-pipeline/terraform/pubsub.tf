resource "google_pubsub_topic" "input_pdf_topic" {
  name = "input-pdf-topic"
  labels = {
    category = "pdf-pipeline"
  }
}


resource "google_pubsub_subscription" "input_pdf_subscription" {
  name = "input-pdf-subscription"
  topic = google_pubsub_topic.input_pdf_topic.name

  ack_deadline_seconds = 60

  labels = {
    category = "pdf-pipeline"
  }
}

resource "google_pubsub_topic" "images_buffer_topic" {
  name = "images-buffer-topic"
  labels = {
    category = "pdf-pipeline"
  }
}

resource "google_pubsub_subscription" "images_buffer_subscription" {
  name = "images-buffer-subscription"
  topic = google_pubsub_topic.images_buffer_topic.name

  ack_deadline_seconds = 60

  labels = {
    category = "pdf-pipeline"
  }
}

