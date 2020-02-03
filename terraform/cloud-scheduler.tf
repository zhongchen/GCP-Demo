resource "google_cloud_scheduler_job" "scheduler" {
  name = "scheduler"
  schedule = "0 0 * * *"
  region = "us-central"

  http_target {
    http_method = "POST"
    uri = "https://google.com"
    oauth_token {
      service_account_email = "zhong-service-account@zhong-gcp.iam.gserviceaccount.com"
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