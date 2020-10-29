resource "google_compute_subnetwork" "striim-vpc-subnet" {
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
  name                    = "striim-vpc"
  auto_create_subnetworks = false
}