terraform {
  backend "gcs" {
    bucket = "zhong-gcp"
    prefix = "terraform/state"
  }
}

provider "google" {
  version = "~> 2.11"
  project = "zhong-gcp"
}
