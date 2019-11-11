resource "google_dataproc_cluster" "spark" {
    name = "sparkcluster"
    region = "us-central1"

    labels = {
        env = "dev"
    }

    cluster_config {
        staging_bucket = "zhong-gcp"

        master_config {
            num_instances = 1
            machine_type = "n1-standard-1"
            disk_config {
                boot_disk_size_gb = 20
                boot_disk_type = ""
            }

        }
    }


    initialization_action {
            script      = "gs://dataproc-initialization-actions/stackdriver/stackdriver.sh"
            timeout_sec = 500
        }
}
