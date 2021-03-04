resource "google_compute_subnetwork" "striim-vpc-subnet" {
  project       = google_project.striim_project.id
  name          = "striim-subnetwork"
  ip_cidr_range = "10.2.0.0/16"
  region        = var.striim-region
  network       = google_compute_network.striim-vpc.id
  secondary_ip_range {
    range_name    = "striim-subnetwork-range"
    ip_cidr_range = "192.168.10.0/24"
  }
}

resource "google_compute_network" "striim-vpc" {
  project                 = google_project.striim_project.id
  name                    = "striim-vpc"
  auto_create_subnetworks = false
}