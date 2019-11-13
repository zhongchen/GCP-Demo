resource "google_service_account" "pubsub-account" {
  account_id = "pubsub-access"
  display_name = "a service account to access pubsub"
}

resource "google_project_iam_member" "pubsub-access" {
  project = var.project_id
  member = "serviceAccount:${google_service_account.pubsub-account.email}"
  role = "roles/pubsub.subscriber"
}