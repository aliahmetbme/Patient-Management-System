# Patient Management System

A backend-focused Patient Management System developed with Java and Spring Boot.

The project demonstrates REST API development, layered architecture, validation,
exception handling, asynchronous processing, concurrency control, WebSocket-based
real-time updates, and multiple software design patterns.

## Tech Stack

- Java 17
- Spring Boot 4.1
- Spring Data JPA
- H2 Database
- Maven
- Jakarta Validation
- Spring WebSocket / STOMP
- Spring Async
- Springdoc OpenAPI / Swagger
- JUnit
- MockMvc

## Architecture

The application follows a layered backend architecture:

Controller → Service → Repository → Database

Additional components are used for admission rules, test execution,
event handling and real-time notifications.

## Main Features

- Patient registration
- Clinic-based patient admission evaluation
- Medical test order creation
- Asynchronous test execution
- Test order status tracking
- Concurrency protection for test execution
- Real-time WebSocket status updates
- Input validation
- Centralized exception handling
- OpenAPI / Swagger documentation
- CORS configuration for frontend integration

## Design Patterns

### Abstract Factory

Clinic-specific patient admission forms and rules are created through
clinic-specific factories.

Supported clinics:

- Cardiology
- Orthopedics
- Endocrinology

### Facade

`PatientAdmissionFacade` provides a simplified interface for the complete
patient admission process.

### Command

Medical test operations are represented as commands and executed through
`TestCommandInvoker`.

Examples:

- X-Ray
- EKG
- Cardiology Blood Test
- Endocrinology Blood Test

### Observer

Spring application events are used to react to changes in test order status.

Observers handle:

- Status logging
- Patient notifications
- WebSocket notifications

### Singleton

Spring's singleton bean scope is used for shared department services such as
`RadiologyDepartment`.

## Asynchronous Test Execution

Medical tests are executed asynchronously using a Spring-managed thread pool.

Thread pool configuration:

- Core pool size: 2
- Maximum pool size: 4
- Queue capacity: 20

This allows multiple test orders to be processed without blocking HTTP request
threads.

## Concurrency Control

A pessimistic database lock is used when a test order is prepared for execution.

This prevents the same `QUEUED` test order from being started concurrently by
multiple requests.

## Test Order Lifecycle

```text
QUEUED
   |
   v
PROCESSING
   |
   +------> COMPLETED
   |
   +------> FAILED
