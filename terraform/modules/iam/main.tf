data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

resource "aws_iam_role" "ecs_task_execution" {
  name = "${var.app_name}-ecs-task-execution-role"

  assume_role_policy = jsonencode(local.ecs_assume_role_policy)

  tags = {
    Name = "${var.app_name}-ecs-task-execution-role"
  }
}

resource "aws_iam_role" "ecs_task" {
  name = "${var.app_name}-ecs-task-role"

  assume_role_policy = jsonencode(local.ecs_assume_role_policy)

  tags = {
    Name = "${var.app_name}-ecs-task-role"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy" "dynamodb_access" {
  name = "${var.app_name}-dynamodb-access"
  role = aws_iam_role.ecs_task.id
  policy = jsonencode(local.dynamodb_policy)
}

resource "aws_iam_role_policy" "kms_access" {
  name = "${var.app_name}-kms-access"
  role = aws_iam_role.ecs_task.id
  policy = jsonencode(local.kms_policy)
}

resource "aws_iam_role_policy" "sqs_access" {
  name = "${var.app_name}-sqs-access"
  role = aws_iam_role.ecs_task.id
  policy = jsonencode(local.sqs_policy)
}
