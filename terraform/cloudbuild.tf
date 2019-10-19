resource "google_cloudbuild_trigger" "file_update" {
  description = "trigger for file update"

  trigger_template{
    branch_name = ".*"
    repo_name = "github_zhongchen_gcp-demo"
  }

  included_files = ["data/input*"]
  filename = "data/cloudbuild.yaml"
}