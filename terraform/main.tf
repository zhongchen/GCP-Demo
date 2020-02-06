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

# Use this data source to get project details. For more information see API.
# https://www.terraform.io/docs/providers/google/d/google_project.html
data "google_project" "project" {}
