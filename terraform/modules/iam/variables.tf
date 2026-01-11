variable "app_name" {
  type = string
}

variable "dynamodb_table_arns" {
  type = list(string)
}

variable "kms_key_arn" {
  type = string
}

variable "sqs_queue_arn" {
  type = string
}
