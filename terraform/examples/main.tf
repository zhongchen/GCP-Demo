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

module "join_cwow" {
  source = "git::https://gitlab.gcp.davita.com/cwow/cwow-program.git?ref=v2.5.0"
  cloud  = "gcp"
  region = "us-central1"
  env    = terraform.workspace == "default" ? "prod" : "np"
}

module "join_cwow_np" {
  source = "git::https://gitlab.gcp.davita.com/cwow/cwow-program.git?ref=v2.5.0"
  cloud  = "gcp"
  region = "us-central1"
  env    = "np"
}

// uncomment when it is ready to rollout to production
//module "striim_prod" {
//  source            = "../modules/striim"
//  env               = terraform.workspace == "default" ? "prod" : "np"
//  striim-db-type    = ""
//  striim-node-count = ""
//  striim-node-type  = ""
//  striim-region     = ""
//  striim-zone       = ""
//  cloud_sql_version = "POSTGRES_11"
//}

module "striim_np" {
  source            = "../modules/striim"
  env               = terraform.workspace == "default" ? "prod" : "np"
  striim-db-type    = "db-f1-micro"
  striim-node-count = "1"
  striim-node-type  = "n2-standard-2"
  striim-region     = "us-central1"
  striim-zone       = "us-central1-a"
  cloud_sql_version = "POSTGRES_11"
  folder_id         = var.gcp_folder_name
  billing_account   = module.join_cwow.env.billing_account
}
