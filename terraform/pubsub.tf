resource "google_pubsub_topic" "dataflow_demo" {
  name = "dataflow_demo"
}

resource "google_pubsub_subscription" "dataflow_demo_subscription" {
  name = "dataflow_demo_subscription"
  topic = google_pubsub_topic.dataflow_demo.name
}