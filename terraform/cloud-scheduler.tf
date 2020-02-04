resource "google_cloud_scheduler_job" "scheduler" {
  name = "scheduler"
  schedule = "0 0 * * *"
  region = "us-central"

  http_target {
    http_method = "POST"
    uri = "https://google.com"
    oauth_token {
      service_account_email = google_service_account.cloud-scheduler-demo.email
    }

    body = base64encode(<<-EOT
    {
      "jobName": "test-cloud-scheduler",
      "parameters": {
        "region": "us-west1",
        "autoscalingAlgorithm": "THROUGHPUT_BASED",
      },
      "environment": {
        "maxWorkers": "10",
        "tempLocation": "gs://zhong-gcp/temp",
        "zone": "us-west1-a"
      }
    }
EOT
    )
  }
}

resource "google_service_account" "cloud-scheduler-demo" {
  account_id = "cloud-scheduler-demo"
  display_name = "A service account for importing Benchling data"
}

resource "google_project_iam_member" "cloud-scheduler-dataflow" {
  project = var.project_id
  role = "roles/dataflow.admin"
  member = "serviceAccount:${google_service_account.cloud-scheduler-demo.email}"
}

resource "google_project_iam_member" "cloud-scheduler-gcs" {
  project = var.project_id
  role = "roles/compute.storageAdmin"
  member = "serviceAccount:${google_service_account.cloud-scheduler-demo.email}"
}

