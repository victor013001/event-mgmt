# EventMgmt

Event management and ticket purchasing microservice with concurrency control, asynchronous purchase processing, and
automatic release of expired reservations. Built with Clean/Hex architecture using Bancolombia’s scaffold and runnable
locally with LocalStack.

## Technologies

- Java 25
- Spring Boot 4
- Spring WebFlux (Router/Handler)
- Reactor (Mono/Flux)
- AWS SDK v2 (DynamoDB, SQS)
- Docker, Docker Compose, LocalStack
- Spring Boot Actuator, SpringDoc OpenAPI
- Testing: JUnit 5, Mockito, Reactor Test, ArchUnit, Jacoco, Pitest

## Features

- Create event
- Get events by place (using DynamoDB Query with GSI)
- Get real-time availability by event
- Place ticket (reserve tickets)
- Get ticket status (for X-User-Id user)
- Asynchronous purchase processing via SQS consumer
- Automatic release of expired reservations via scheduler
- Concurrency control to prevent overselling

## Architecture

![Architecture.png](Architecture.png)

### Modules (Bancolombia Scaffold)

- domain-model: Business entities and domain rules
- domain-usecase: Use cases (application logic)
- infrastructure/entry-points
    - reactive-web (WebFlux Router and Handler)
    - sqs-listener (SQS consumer with purchase processing)
        - scheduler (scheduled jobs)
- infrastructure/driven-adapters
    - dynamodb (persistence)
    - sqs (publisher)
    - rest-consumer (optional, only if required)

## Core Flows

![Use Cases.png](Use%20Cases.png)

### Purchase Flow

1. Client calls the place ticket endpoint
2. Service atomically creates ticket and reserves inventory using DynamoDB TransactWriteItems with conditional
   expressions
3. Ticket is created with status RESERVED and an expiration timestamp (10 minutes)
4. Service publishes payment event to SQS with the ticket ID
5. Ticket status transitions to PENDING_CONFIRMATION after SQS publish
6. SQS consumer processes the payment asynchronously
7. If payment is successful, ticket transitions to SOLD and inventory is finalized
8. Scheduler periodically releases expired reservations

**Note**: For complimentary tickets, a coupon validation system is planned to bypass payment processing.

### Ticket State Machine

- RESERVED
- PENDING_CONFIRMATION
- SOLD
- COMPLIMENTARY
- EXPIRED
  ![Ticker Status.png](Ticker%20Status.png)

## API Endpoints

Base path depends on your configuration. Examples below assume /api.

### Required Headers

All API requests require the following headers:

- `X-User-Id`: User identifier provided by API Gateway
- `flow-id`: Flow identifier for tracing across SQS and other services

### Events

- POST /api/v1/event
- GET /api/v1/event
- GET /api/v1/event?place={place} (Query events by place using GSI)
- GET /api/v1/event/{eventId}/availability

### Tickets

- POST /api/v1/event/{eventId}/ticket
- GET /api/v1/ticket/{ticketId}

### Observability

- GET /actuator/health
- GET /actuator/prometheus
- Swagger UI
    - /swagger-ui.html or /swagger-ui/index.html
- OpenAPI
    - /v3/api-docs

TODO Add an endpoints table plus request/response examples.

Suggested location: docs/api.md

## DynamoDB Query Strategy

The service uses DynamoDB Query operations instead of Scan for better performance and cost efficiency:

- **Events by Place**: Uses a Global Secondary Index (GSI) with `place` as partition key
- **Avoids Scan operations**: Scan is not used as it's inefficient for large datasets and consumes more RCUs
- **Query benefits**: More predictable performance, lower cost, and better scalability

## SQS Purchase Processing

The SQS listener implements asynchronous purchase processing with the following components:

- **PurchaseHandler**: Entry point for SQS message handling
- **PurchaseProcessor**: Core business logic for processing purchase events
- **Validation**: Message validation and parameter extraction
- **Error Handling**: Structured logging for business and technical exceptions
- **Utilities**: JSON parsing, field validation, and message cleaning

### Message Processing Flow

1. SQS message received by PurchaseHandler
2. Message validated and parsed by PurchaseProcessor
3. Ticket status updated to SOLD via UpdateTicketUseCase
4. Business exceptions logged but don't fail the message processing
5. Technical exceptions cause message reprocessing

## Concurrency Control

This service prevents overselling using DynamoDB TransactWriteItems with conditional expressions:

- **Atomic Operations**: Ticket creation and inventory reservation happen atomically in a single transaction
- **Conditional Updates**: Inventory is reserved only if `available >= quantity` using condition expressions
- **Transaction Safety**: If any part of the transaction fails, the entire operation is rolled back
- **Idempotent Operations**: Finalization steps are designed to handle duplicate processing
- **SQS At-Least-Once**: The SQS consumer handles duplicate messages gracefully

## Local Setup

### Prerequisites

- Docker and Docker Compose
- Java 25 toolchain

### Environment Variables

Create a .env file at the repository root. Example:

```
ACTIVE_PROFILE=local

AWS_REGION=us-east-1
AWS_ENDPOINT=http://localstack:4566

DYNAMO_EVENTS_TABLE=events
DYNAMO_INVENTORY_TABLE=inventory
DYNAMO_TICKETS_TABLE=tickets

SQS_QUEUE_URL=http://localstack:4566/000000000000/tickets-queue
SQS_ENDPOINT=http://localstack:4566
```

### Run Locally with LocalStack

1. Clone the repository

```
git clone https://github.com/victor013001/event-mgmt.git
cd event-mgmt
```

2. Start services

```
docker-compose up --build
```

3. Access the application

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/v3/swagger-ui.html

## Logging and Sensitive Data Masking

The project uses Logback with a Logstash encoder to output structured JSON logs and apply masking rules for sensitive
data.

- Config file: src/main/resources/logback.xml
- Masking strategy
    - Mask JSON paths such as Authorization, token, apiKey, secret, password

## Tests

Run unit tests

```
./gradlew clean test
```

Coverage report

```
./gradlew jacocoTestReport
```

Mutation tests

```
./gradlew pitest
```

## Author

**[Victor Manuel Osorio Garcia](https://www.linkedin.com/in/victor013001)** - [GitHub](https://github.com/victor013001)
