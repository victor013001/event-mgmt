variable "region" {
  description = "AWS region to deploy resources."
  type        = string
  default     = "us-east-1"
}

variable "app_name" {
  description = "Base name for application resources."
  type        = string
  default     = "event-mgmt"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "ecr_repository_name" {
  description = "The name of the ECR repository."
  type        = string
  default     = "event-mgmt"
}

variable "ecr_image_tag" {
  description = "The tag of the ECR image to use."
  type        = string
  default     = "latest"
}

variable "active_profile" {
  description = "Spring Boot profile to activate."
  type        = string
  default     = "dev"
}

variable "ecs_cpu" {
  description = "CPU for the ECS Fargate task."
  type        = string
  default     = "256"
}

variable "ecs_memory" {
  description = "Memory for the ECS Fargate task."
  type        = string
  default     = "512"
}

variable "ecs_desired_count" {
  description = "Desired number of ECS tasks."
  type        = number
  default     = 1
}

variable "ecs_container_port" {
  description = "Port the application listens on."
  type        = number
  default     = 8080
}

variable "ecs_log_retention_days" {
  description = "Retention days for ECS task logs."
  type        = number
  default     = 1
}

variable "apigw_log_retention_days" {
  description = "Retention days for API Gateway logs."
  type        = number
  default     = 1
}

variable "aws_dynamodb_endpoint" {
  description = "DynamoDB endpoint URL."
  type        = string
  default     = ""
}

variable "adapter_sqs_endpoint" {
  description = "SQS adapter endpoint URL."
  type        = string
  default     = ""
}

variable "entrypoint_sqs_endpoint" {
  description = "SQS entrypoint endpoint URL."
  type        = string
  default     = ""
}

variable "entrypoint_sqs_wait_time_seconds" {
  description = "SQS wait time seconds."
  type        = number
  default     = 20
}

variable "entrypoint_sqs_max_number_of_messages" {
  description = "SQS max number of messages."
  type        = number
  default     = 10
}

variable "entrypoint_sqs_visibility_timeout_seconds" {
  description = "SQS visibility timeout seconds."
  type        = number
  default     = 30
}

variable "entrypoint_sqs_number_of_threads" {
  description = "SQS number of threads."
  type        = number
  default     = 1
}

variable "cors_allowed_origins" {
  description = "CORS allowed origins."
  type        = string
  default     = "https://localhost:3000"
}

variable "scheduler_ticket_expiration_fixed_delay" {
  description = "Scheduler fixed delay in milliseconds."
  type        = number
  default     = 300000
}
