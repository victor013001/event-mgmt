resource "aws_api_gateway_rest_api" "this" {
  name        = "${var.app_name}-api"
  description = "API Gateway for ${var.app_name}"

  endpoint_configuration {
    types = ["REGIONAL"]
  }
}

resource "aws_api_gateway_resource" "api" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_rest_api.this.root_resource_id
  path_part   = local.api_base_first
}

resource "aws_api_gateway_resource" "api_v1" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.api.id
  path_part   = local.api_base_second
}

resource "aws_api_gateway_resource" "event" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.api_v1.id
  path_part   = local.event_path
}

resource "aws_api_gateway_resource" "event_id" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.event.id
  path_part   = local.event_id_param
}

resource "aws_api_gateway_resource" "availability" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.event_id.id
  path_part   = local.availability_path
}

resource "aws_api_gateway_resource" "ticket" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.event_id.id
  path_part   = local.ticket_path
}

resource "aws_api_gateway_resource" "ticket_root" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.api_v1.id
  path_part   = local.ticket_path
}

resource "aws_api_gateway_resource" "ticket_id" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.ticket_root.id
  path_part   = local.ticket_id_param
}

resource "aws_api_gateway_method" "create_event" {
  rest_api_id      = aws_api_gateway_rest_api.this.id
  resource_id      = aws_api_gateway_resource.event.id
  http_method      = local.method_post
  authorization    = local.authorization_none
  api_key_required = true
}

resource "aws_api_gateway_method" "get_events" {
  rest_api_id      = aws_api_gateway_rest_api.this.id
  resource_id      = aws_api_gateway_resource.event.id
  http_method      = local.method_get
  authorization    = local.authorization_none
  api_key_required = true
}

resource "aws_api_gateway_method" "get_availability" {
  rest_api_id      = aws_api_gateway_rest_api.this.id
  resource_id      = aws_api_gateway_resource.availability.id
  http_method      = local.method_get
  authorization    = local.authorization_none
  api_key_required = true
}

resource "aws_api_gateway_method" "place_ticket" {
  rest_api_id      = aws_api_gateway_rest_api.this.id
  resource_id      = aws_api_gateway_resource.ticket.id
  http_method      = local.method_post
  authorization    = local.authorization_none
  api_key_required = true
}

resource "aws_api_gateway_method" "get_ticket" {
  rest_api_id      = aws_api_gateway_rest_api.this.id
  resource_id      = aws_api_gateway_resource.ticket_id.id
  http_method      = local.method_get
  authorization    = local.authorization_none
  api_key_required = true
}

resource "aws_api_gateway_integration" "create_event" {
  rest_api_id             = aws_api_gateway_rest_api.this.id
  resource_id             = aws_api_gateway_resource.event.id
  http_method             = aws_api_gateway_method.create_event.http_method
  type                    = local.integration_type
  integration_http_method = local.method_post
  uri                     = "http://${var.alb_dns_name}/${local.api_base_first}/${local.api_base_second}/${local.event_path}"
}

resource "aws_api_gateway_integration" "get_events" {
  rest_api_id             = aws_api_gateway_rest_api.this.id
  resource_id             = aws_api_gateway_resource.event.id
  http_method             = aws_api_gateway_method.get_events.http_method
  type                    = local.integration_type
  integration_http_method = local.method_get
  uri                     = "http://${var.alb_dns_name}/${local.api_base_first}/${local.api_base_second}/${local.event_path}"
}

resource "aws_api_gateway_integration" "get_availability" {
  rest_api_id             = aws_api_gateway_rest_api.this.id
  resource_id             = aws_api_gateway_resource.availability.id
  http_method             = aws_api_gateway_method.get_availability.http_method
  type                    = local.integration_type
  integration_http_method = local.method_get
  uri                     = "http://${var.alb_dns_name}/${local.api_base_first}/${local.api_base_second}/${local.event_path}/${local.event_id_param}/${local.availability_path}"
}

resource "aws_api_gateway_integration" "place_ticket" {
  rest_api_id             = aws_api_gateway_rest_api.this.id
  resource_id             = aws_api_gateway_resource.ticket.id
  http_method             = aws_api_gateway_method.place_ticket.http_method
  type                    = local.integration_type
  integration_http_method = local.method_post
  uri                     = "http://${var.alb_dns_name}/${local.api_base_first}/${local.api_base_second}/${local.event_path}/${local.event_id_param}/${local.ticket_path}"
}

resource "aws_api_gateway_integration" "get_ticket" {
  rest_api_id             = aws_api_gateway_rest_api.this.id
  resource_id             = aws_api_gateway_resource.ticket_id.id
  http_method             = aws_api_gateway_method.get_ticket.http_method
  type                    = local.integration_type
  integration_http_method = local.method_get
  uri                     = "http://${var.alb_dns_name}/${local.api_base_first}/${local.api_base_second}/${local.ticket_path}/${local.ticket_id_param}"
}

resource "aws_api_gateway_api_key" "this" {
  name = "${var.app_name}-api-key"
}

resource "aws_api_gateway_usage_plan" "this" {
  name = "${var.app_name}-usage-plan"

  api_stages {
    api_id = aws_api_gateway_rest_api.this.id
    stage  = aws_api_gateway_deployment.this.stage_name
  }

  quota_settings {
    limit  = 1000
    period = "DAY"
  }

  throttle_settings {
    rate_limit  = 100
    burst_limit = 200
  }
}

resource "aws_api_gateway_usage_plan_key" "this" {
  key_id        = aws_api_gateway_api_key.this.id
  key_type      = "API_KEY"
  usage_plan_id = aws_api_gateway_usage_plan.this.id
}

resource "aws_api_gateway_deployment" "this" {
  rest_api_id = aws_api_gateway_rest_api.this.id

  depends_on = [
    aws_api_gateway_method.create_event,
    aws_api_gateway_method.get_events,
    aws_api_gateway_method.get_availability,
    aws_api_gateway_method.place_ticket,
    aws_api_gateway_method.get_ticket,
    aws_api_gateway_integration.create_event,
    aws_api_gateway_integration.get_events,
    aws_api_gateway_integration.get_availability,
    aws_api_gateway_integration.place_ticket,
    aws_api_gateway_integration.get_ticket
  ]

  lifecycle {
    create_before_destroy = true
  }
}
