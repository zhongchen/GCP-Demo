terraform {
  backend "gcs" {
    bucket = "zhong-gcp"
    prefix = "terraform/state"
  }
}

