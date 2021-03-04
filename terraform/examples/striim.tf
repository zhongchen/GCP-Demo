terraform {
  backend "gcs" {}
  required_providers {
    google      = "~> 3.4"
    google-beta = "~> 3.4"
    random      = "~> 2.2"
  }
  required_version = "~> 0.12.28"
}

provider "google" {}
provider "google-beta" {}
provider "random" {}


resource "random_string" "suffix" {
  length    = 4
  min_lower = 4
  number    = false
  special   = false
}

resource "google_project" "striim_project" {
  provider        = google
  folder_id       = var.folder_id
  billing_account = var.billing_account

  name       = "striim-${var.env == "p" ? "prod" : "non-prod"}"
  project_id = "striim-${var.env == "p" ? "prod" : "non-prod"}-${random_string.suffix.result}"

  auto_create_network = false

  labels = {
    env = "${var.env == "p" ? "prod" : "non-prod"}"
  }
}



resource "google_compute_instance_template" "striim_template" {
  project = google_project.striim_project.id

  name_prefix = "striim-template-"
  description = "This template is used to create striim server instances."
  region      = var.striim-region

  tags = ["striim"]

  labels = {
    environment = var.env
  }

  instance_description = "description assigned to instances"
  machine_type         = var.striim-node-type
  can_ip_forward       = false

  scheduling {
    automatic_restart   = false
    on_host_maintenance = "MIGRATE"
  }

  // Create a new boot disk from an image
  disk {
    source_image = "debian-cloud/debian-9"
    auto_delete  = true
    boot         = true
  }

  network_interface {
    network    = google_compute_network.striim-vpc.name
    subnetwork = google_compute_subnetwork.striim-vpc-subnet.name

    access_config {
      // Ephemeral IP
    }
  }

  lifecycle {
    create_before_destroy = true
  }

  service_account {
    scopes = ["userinfo-email", "compute-ro", "storage-ro"]
  }
}


resource "google_compute_instance_group_manager" "striim_instance_group_manager" {
  project = google_project.striim_project.id
  name    = "striim-instance-group-manager"
  version {
    instance_template = google_compute_instance_template.striim_template.id
  }
  base_instance_name = "striim-instance-group-manager"
  zone               = var.striim-zone
  target_size        = var.striim-node-count
}


resource "google_service_account" "striim-sa" {
  project      = google_project.striim_project.id
  account_id   = "striim-service-account"
  display_name = "Striim Service Account"
}