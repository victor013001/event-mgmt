locals {
  api_base_first  = "api"
  api_base_second = "v1"
  
  # Resource path parts
  event_path = "event"
  ticket_path = "ticket"
  availability_path = "availability"
  
  # Path parameters
  event_id_param = "{eventId}"
  ticket_id_param = "{ticketId}"
  
  # HTTP methods
  method_get = "GET"
  method_post = "POST"
  
  # Integration settings
  integration_type = "HTTP_PROXY"
  authorization_none = "NONE"
  stage_name = "prod"
}