terraform {
  backend "gcs" {}
  required_providers {
    google      = "~> 3.4"
    google-beta = "~> 3.4"
    random      = "~> 2.2"
  }
//  required_version = "~> 0.12.28"
}

provider "google" {
  project = "zhong-gcp"
}
provider "google-beta" {}
provider "random" {}


resource "random_string" "suffix" {
  length    = 4
  min_lower = 4
  number    = false
  special   = false
}

//resource "google_project" "ips_project" {
//  provider = google-beta
//
//  name       = "ips-${var.env == "p" ? "prod" : "non-prod"}"
//  project_id = "ips-${var.env == "p" ? "prod" : "non-prod"}-${random_string.suffix.result}"
//
//  auto_create_network = false
//
//  labels = {
//    env = "${var.env == "p" ? "prod" : "non-prod"}"
//  }
//}


resource "google_compute_instance" "striim" {
  name         = "striim"
  machine_type = var.striim-node-type
  zone         = var.striim-zone

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-9"
    }
  }

  network_interface {
    network = google_compute_network.striim-vpc.name
    subnetwork = google_compute_subnetwork.striim-vpc-subnet.name

    access_config {
      // Ephemeral IP
    }
  }

  service_account {
    scopes = ["userinfo-email", "compute-ro", "storage-ro"]
  }

  count = var.striim-node-count
}

resource "google_service_account" "striim-sa" {
  account_id   = "striim-service-account"
  display_name = "Striim Service Account"
}