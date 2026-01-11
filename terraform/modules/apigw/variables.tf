data "aws_region" "current" {}

variable "app_name" {
  type = string
}

variable "alb_dns_name" {
  type = string
}
