output "apigw_url" {
  description = "API Gateway URL."
  value       = module.apigw.api_url
}

output "api_key_id" {
  description = "API Gateway API Key ID."
  value       = module.apigw.api_key_id
}

output "api_key_value" {
  description = "API Gateway API Key Value."
  value       = module.apigw.api_key_value
  sensitive   = true
}

output "application_api_endpoints_exposed" {
  description = "List of application API endpoints (METHOD /path) exposed through API Gateway."
  value       = module.apigw.defined_application_routes
}
