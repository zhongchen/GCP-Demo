locals {
  repo = "github_zhongchen_gcp-demo"
}
resource "google_cloudbuild_trigger" "file_update" {
  description = "trigger for file update"

  trigger_template{
    branch_name = ".*"
    repo_name = local.repo
  }

  included_files = ["data/input*"]
  filename = "data/cloudbuild.yaml"
}

resource "google_cloudbuild_trigger" "build_image" {
  description = "build docker image"

  trigger_template {
    branch_name = "^master$"
    repo_name = local.repo
  }

  # why can't just point to the dockerfile directly?
  # it is an option on the UI. 
  included_files = ["docker/Dockerfile"]
  filename = "docker/cloudbuild.yaml"
}