resource "google_cloudbuild_trigger" "file_update" {
  description = "trigger for file update"

  trigger_template {
    branch_name = "master"
    repo_name = "GCP-Demo"
  }

  included_files = ["*input*"]
  filename = "/data/cloudbuild.yaml"
}