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

resource "google_composer_environment" "composer-demo" {
  name = "composer-demo"
  region = "us-central1"
  config {
    node_count = 4

    node_config {
      zone         = "us-central1-a"
      machine_type = "n1-standard-1"

      network    = google_compute_network.composer.self_link
      subnetwork = google_compute_subnetwork.composer.self_link

      service_account = google_service_account.composer.name
    }
  }

  depends_on = [google_project_iam_member.composer-worker]
}

resource "google_compute_network" "composer" {
  name                    = "composer-test-network"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "composer" {
  name          = "composer-test-subnetwork"
  ip_cidr_range = "10.2.0.0/16"
  region        = "us-central1"
  network       = google_compute_network.composer.self_link
}

resource "google_service_account" "composer" {
  account_id   = "composer-env-account"
  display_name = "Test Service Account for Composer Environment"
}

resource "google_project_iam_member" "composer-worker" {
  role   = "roles/composer.worker"
  member = "serviceAccount:${google_service_account.composer.email}"
}