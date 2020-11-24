resource "google_compute_subnetwork" "striim-vpc-subnet" {
//  project       = google_project.striim_project.id
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
//  project                 = google_project.striim_project.id
  provider = google-beta
  name                    = "striim-vpc"
  auto_create_subnetworks = false
}

resource "google_compute_global_address" "striim_private_ip_address" {
  provider = google-beta

  name          = "striim-private-ip-address"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 16
  network       = google_compute_network.striim-vpc.id
}

resource "google_service_networking_connection" "striim_private_vpc_connection" {
  provider = google-beta

  network                 = google_compute_network.striim-vpc.id
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.striim_private_ip_address.name]
}