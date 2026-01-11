output "api_endpoint" {
  value = aws_api_gateway_rest_api.this.execution_arn
}

output "api_url" {
  value = "https://${aws_api_gateway_rest_api.this.id}.execute-api.${data.aws_region.current.name}.amazonaws.com/${aws_api_gateway_deployment.this.stage_name}"
}

output "api_key_id" {
  value = aws_api_gateway_api_key.this.id
}

output "api_key_value" {
  value = aws_api_gateway_api_key.this.value
  sensitive = true
}

output "defined_application_routes" {
  description = "List of application routes (METHOD /path) exposed via API Gateway."
  value = [
    "POST /api/v1/event",
    "GET /api/v1/event",
    "GET /api/v1/event/{eventId}/availability",
    "POST /api/v1/event/{eventId}/ticket",
    "GET /api/v1/ticket/{ticketId}"
  ]
}
