resource "google_sql_database_instance" "striim-db" {
  name             = "striim-db"
  database_version = "POSTGRES_11"
  region = var.striim-region

  settings {
    tier = var.striim-db-type

    ip_configuration {

      dynamic "authorized_networks" {
        for_each = google_compute_instance.striim
        iterator = striim

        content {
          name  = striim.value.name
          value = striim.value.network_interface.0.access_config.0.nat_ip
        }
      }
    }
  }
}