resource "aws_kms_key" "this" {
  description             = "KMS key for ${var.app_name}"
  deletion_window_in_days = var.deletion_window_in_days
  key_usage               = "ENCRYPT_DECRYPT"
  
  tags = {
    Name = "${var.app_name}-kms-key"
  }
}

resource "aws_kms_alias" "this" {
  name          = "alias/${var.app_name}-key"
  target_key_id = aws_kms_key.this.key_id
}