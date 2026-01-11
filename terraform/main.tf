data "aws_caller_identity" "current" {
}

locals {
  dynamic_image_url = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${var.region}.amazonaws.com/${var.ecr_repository_name}:${var.ecr_image_tag}"
}

module "kms" {
  source   = "./modules/kms"
  app_name = var.app_name
}

module "sqs" {
  source     = "./modules/sqs"
  queue_name = "tickets-queue"
}

module "events_table" {
  source     = "./modules/dynamodb"
  table_name = "events"
  hash_key   = "id"
  kms_key_id = module.kms.key_id
  attributes = [
    { name = "id", type = "S" },
    { name = "place", type = "S" }
  ]
  global_secondary_indexes = [
    {
      name            = "place-index"
      hash_key        = "place"
      range_key       = null
      projection_type = "ALL"
      read_capacity   = 5
      write_capacity  = 5
    }
  ]
}

module "inventory_table" {
  source     = "./modules/dynamodb"
  table_name = "inventory"
  hash_key   = "eventId"
  kms_key_id = module.kms.key_id
  attributes = [
    { name = "eventId", type = "S" }
  ]
}

module "tickets_table" {
  source     = "./modules/dynamodb"
  table_name = "tickets"
  hash_key   = "ticketId"
  kms_key_id = module.kms.key_id
  attributes = [
    { name = "ticketId", type = "S" },
    { name = "status", type = "S" }
  ]
  global_secondary_indexes = [
    {
      name            = "status-index"
      hash_key        = "status"
      range_key       = null
      projection_type = "ALL"
      read_capacity   = 5
      write_capacity  = 5
    }
  ]
}

module "logging" {
  source                   = "./modules/cloudwatch"
  app_name                 = var.app_name
  ecs_log_retention_days   = var.ecs_log_retention_days
  apigw_log_retention_days = var.apigw_log_retention_days
}

module "network" {
  source     = "./modules/network"
  app_name   = var.app_name
  cidr_block = var.vpc_cidr
}

module "security_groups" {
  source         = "./modules/security_groups"
  app_name       = var.app_name
  vpc_id         = module.network.vpc_id
  container_port = var.ecs_container_port
  alb_http_port  = 80
}

module "iam" {
  source   = "./modules/iam"
  app_name = var.app_name
  dynamodb_table_arns = [
    module.events_table.table_arn,
    module.inventory_table.table_arn,
    module.tickets_table.table_arn
  ]
  kms_key_arn = module.kms.key_arn
  sqs_queue_arn = module.sqs.queue_arn
}

module "alb" {
  source            = "./modules/alb"
  app_name          = var.app_name
  vpc_id            = module.network.vpc_id
  public_subnet_ids = module.network.public_subnets
  alb_sg_id         = module.security_groups.alb_sg_id
  ecs_target_port   = var.ecs_container_port
  health_check_path = "/actuator/health"
  health_check_matcher = "200"
}

module "ecs" {
  source             = "./modules/ecs"
  app_name           = var.app_name
  region             = var.region
  image_url          = local.dynamic_image_url
  ecs_cpu            = var.ecs_cpu
  ecs_memory         = var.ecs_memory
  desired_count      = var.ecs_desired_count
  container_port     = var.ecs_container_port
  execution_role_arn = module.iam.ecs_task_execution_role_arn
  task_role_arn      = module.iam.ecs_task_role_arn
  subnet_ids         = module.network.private_subnets
  ecs_tasks_sg_id    = module.security_groups.ecs_tasks_sg_id
  target_group_arn   = module.alb.target_group_arn
  alb_listener       = module.alb.listener_arn
  ecs_log_group_name = module.logging.ecs_log_group_name
  environment_variables = [
    { name = "SPRING_PROFILES_ACTIVE", value = var.active_profile },
    { name = "AWS_REGION", value = var.region },
    { name = "AWS_DYNAMODB_ENDPOINT", value = var.aws_dynamodb_endpoint },
    { name = "AWS_DYNAMODB_EVENT_TABLE_NAME", value = module.events_table.table_name },
    { name = "AWS_DYNAMODB_INVENTORY_TABLE_NAME", value = module.inventory_table.table_name },
    { name = "AWS_DYNAMODB_TICKET_TABLE_NAME", value = module.tickets_table.table_name },
    { name = "ADAPTER_SQS_REGION", value = var.region },
    { name = "ADAPTER_SQS_QUEUE_URL", value = module.sqs.queue_url },
    { name = "ADAPTER_SQS_ENDPOINT", value = var.adapter_sqs_endpoint },
    { name = "ENTRYPOINT_SQS_REGION", value = var.region },
    { name = "ENTRYPOINT_SQS_ENDPOINT", value = var.entrypoint_sqs_endpoint },
    { name = "ENTRYPOINT_SQS_QUEUE_URL", value = module.sqs.queue_url },
    { name = "ENTRYPOINT_SQS_WAIT_TIME_SECONDS", value = tostring(var.entrypoint_sqs_wait_time_seconds) },
    { name = "ENTRYPOINT_SQS_MAX_NUMBER_OF_MESSAGES", value = tostring(var.entrypoint_sqs_max_number_of_messages) },
    { name = "ENTRYPOINT_SQS_VISIBILITY_TIMEOUT_SECONDS", value = tostring(var.entrypoint_sqs_visibility_timeout_seconds) },
    { name = "ENTRYPOINT_SQS_NUMBER_OF_THREADS", value = tostring(var.entrypoint_sqs_number_of_threads) },
    { name = "CORS_ALLOWED_ORIGINS", value = var.cors_allowed_origins },
    { name = "SCHEDULER_TICKET_EXPIRATION_FIXED_DELAY", value = tostring(var.scheduler_ticket_expiration_fixed_delay) }
  ]
}

module "apigw" {
  source       = "./modules/apigw"
  app_name     = var.app_name
  alb_dns_name = module.alb.alb_dns_name
}
