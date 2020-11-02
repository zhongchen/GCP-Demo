variable "env" {
  description = "development environment"
  type        = string
  default     = "np"
}

variable "striim-node-count" {
  description = "number of the striim nodes"
  type        = string
  default     = "1"
}

variable "striim-node-type" {
  description = "gce instance type for striim nodes"
  type        = string
  default     = "n2-standard-2"
}

variable "striim-zone" {
  description = "gcp zone for striim nodes"
  type        = string
  default     = "us-central1-a"
}

variable "striim-db-type" {
  description = "striim db instance type"
  type        = string
  default     = "db-f1-micro"
}

variable "striim-region" {
  description = "gcp region for striim vpc"
  type        = string
  default     = "us-central1"
}

variable "cloud_sql_version" {
  type    = string
  default = "POSTGRES_11"
}

variable "folder_id" {
  type = string
}

variable "billing_account" {
  type = string
}
