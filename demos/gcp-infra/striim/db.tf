resource "google_sql_database_instance" "striim-db" {
//  project             = google_project.striim_project.id
  name                = "striim-db-3"
  database_version    = var.cloud_sql_version
  region              = var.striim-region
  deletion_protection = false

  settings {
    tier              = var.striim-db-type
    availability_type = "REGIONAL"

    user_labels = {
      automated   = true
      application = "striim"
    }

    ip_configuration {
      ipv4_enabled    = false
      private_network = google_compute_network.striim-vpc.id
    }

    backup_configuration {
      enabled    = true
      start_time = "06:00"
    }
  }

  timeouts {
    create = "30m"
  }

  depends_on = [google_service_networking_connection.striim_private_vpc_connection]

  //  depends_on = [google_project.striim_project]
}

resource "google_sql_user" "striim" {
//  project  = google_project.striim_project.id
  name     = "striim"
  instance = google_sql_database_instance.striim-db.name
  password = "changeme1"
}

resource "google_sql_database" "striim" {
//  project  = google_project.striim_project.id
  name     = "striim"
  instance = google_sql_database_instance.striim-db.name
  # This fake dependency prevents destroy failures -- for whatever reason the db must be deleted
  # before the user
  depends_on = [google_sql_user.striim]
}
