terraform {
  backend "gcs" {}
  required_providers {
    google      = "~> 3.4"
    google-beta = "~> 3.4"
    random      = "~> 2.2"
  }
//  required_version = "~> 0.12.28"
}

provider "google" {}
provider "google-beta" {}
provider "random" {}


module "striim_np" {
  source            = "../striim"
  env               = "np"
  striim-db-type    = "db-f1-micro"
  striim-node-count = "1"
  striim-node-type  = "n2-standard-2"
  striim-region     = "us-central1"
  striim-zone       = "us-central1-a"
  cloud_sql_version = "POSTGRES_11"
  folder_id         = ""
  billing_account   = ""
}
