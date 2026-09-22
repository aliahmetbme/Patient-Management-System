# Patient Management System

A backend-focused **Patient Management System** built with **Java 17 and Spring Boot 4.1**.

The project demonstrates the design and implementation of a structured REST API with layered architecture, validation, centralized exception handling, asynchronous processing, concurrency control, real-time WebSocket communication, automated testing, and commonly used software design patterns.

The system manages patient admission processes and medical test orders while simulating interactions between different hospital departments.

---

## Overview

The application provides two main business flows:

1. **Patient Admission**
   - Patients are registered through the REST API.
   - Admission rules are evaluated according to clinic, insurance, medical history, and consent information.
   - Clinic-specific admission logic is handled through dedicated factories.

2. **Medical Test Processing**
   - Medical test orders are created for existing patients.
   - Orders initially enter the `QUEUED` state.
   - Tests are executed asynchronously through a dedicated thread pool.
   - Test status changes are published as application events.
   - Clients can receive real-time status updates through WebSocket/STOMP.

---

## Tech Stack

| Technology | Usage |
|---|---|
| Java 17 | Main programming language |
| Spring Boot 4.1 | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Persistence layer |
| Hibernate | ORM |
| H2 Database | Development and testing database |
| Jakarta Validation | Request validation |
| Spring Async | Asynchronous test execution |
| Spring WebSocket / STOMP | Real-time status updates |
| Spring Events | Observer-based event communication |
| Springdoc OpenAPI | Swagger / API documentation |
| Maven | Dependency and build management |
| JUnit | Automated testing |
| MockMvc | REST API integration testing |
| SLF4J | Application logging |

---

## Architecture

The project follows a layered backend structure:

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
Database
```

Additional components are used for business rules, command execution, asynchronous processing, domain events, notifications, and real-time communication.

A simplified application flow:

```text
Patient Request
      |
      v
PatientController
      |
      v
PatientService
      |
      v
PatientAdmissionFacade
      |
      v
ClinicFormFactoryResolver
      |
      +--------------------+
      |                    |
      v                    v
Clinic Factory        Admission Rules
      |
      v
PatientRepository
```

Medical test execution follows a separate flow:

```text
TestOrderController
        |
        v
TestOrderService
        |
        v
TestCommandInvoker
        |
        +-------------------------+
        |                         |
        v                         v
RadiologyDepartment       LaboratoryDepartment
        |
        v
Status Changed Event
        |
        +------------------+----------------------+
        |                  |                      |
        v                  v                      v
Logging Listener   Patient Notification   WebSocket Listener
```

---

## Main Features

- Patient registration
- Clinic-based admission evaluation
- Insurance validation
- Medical history validation
- Patient consent validation
- Medical test order creation
- Test order lifecycle management
- Asynchronous test execution
- Custom thread pool configuration
- Concurrency protection
- Pessimistic database locking
- Test failure handling
- Application event publishing
- Real-time WebSocket notifications
- Centralized exception handling
- Request validation
- Structured API error responses
- OpenAPI / Swagger documentation
- REST API integration testing
- WebSocket integration testing
- Concurrency testing
- CORS configuration for frontend integration
- Structured application logging with SLF4J

---

# Design Patterns

The project intentionally uses multiple software design patterns to separate responsibilities and demonstrate maintainable backend design.

## Abstract Factory

Clinic-specific patient admission rules are created through dedicated factories.

Supported clinics:

- Cardiology
- Orthopedics
- Endocrinology

Each clinic can apply different admission requirements based on:

- Insurance type
- Medical history
- Patient consent

The factory resolver selects the appropriate clinic factory according to the patient's clinic selection.

---

## Facade

`PatientAdmissionFacade` provides a simplified interface for the patient admission process.

Instead of exposing multiple admission validation components directly to the service layer, the facade coordinates the complete admission evaluation process.

```text
PatientService
      |
      v
PatientAdmissionFacade
      |
      +--> Clinic Factory
      +--> Insurance Validation
      +--> Medical History Validation
      +--> Consent Validation
```

---

## Command

Medical test operations are represented as commands.

`TestCommandInvoker` selects and executes the correct operation according to the requested medical test type.

Supported test types:

```text
EKG
X_RAY
CARDIOLOGY_BLOOD_TEST
ENDOCRINOLOGY_BLOOD_TEST
```

This separates the request for an operation from the component that performs it.

---

## Observer

Spring application events are used to react to test order status changes.

When a test order changes state, a `TestOrderStatusChangedEvent` is published.

Multiple listeners react independently:

```text
TestOrderStatusChangedEvent
        |
        +--> TestOrderStatusLogListener
        |
        +--> PatientNotificationListener
        |
        +--> TestOrderStatusWebSocketListener
```

This allows new event consumers to be added without modifying the main test execution logic.

---

## Singleton

Spring-managed service beans use singleton scope.

For example, `RadiologyDepartment` is managed as a singleton component and shared across the application context.

This demonstrates how Spring's dependency injection container manages shared application services.

---

# Patient Admission

Patients are registered through:

```http
POST /api/patients
```

The request contains:

```json
{
  "fullName": "Ayse Kaya",
  "email": "ayse@example.com",
  "phone": "05551112233",
  "clinicType": "CARDIOLOGY",
  "insuranceType": "GOVERNMENT",
  "medicalHistory": "Previous medical history",
  "consentGiven": true
}
```

Possible admission statuses:

```text
PENDING
ADMITTED
REJECTED
```

The application evaluates the patient through the clinic-specific admission process and stores the result together with an admission reason.

---

# Medical Test Orders

A medical test can be created for an existing patient through:

```http
POST /api/test-orders
```

Example request:

```json
{
  "patientId": 1,
  "testType": "X_RAY"
}
```

A newly created order starts with:

```text
QUEUED
```

---

## Test Order Lifecycle

```text
             +-------------+
             |   QUEUED    |
             +-------------+
                    |
                    | Execute
                    v
             +-------------+
             | PROCESSING  |
             +-------------+
                /       \
               /         \
              v           v
     +-------------+   +---------+
     |  COMPLETED  |   | FAILED  |
     +-------------+   +---------+
```

Only a `QUEUED` test order can be submitted for execution.

---

# Asynchronous Processing

Medical tests are executed asynchronously using Spring's `@Async` support.

The application defines a dedicated `ThreadPoolTaskExecutor`.

Configuration:

```text
Core Pool Size : 2
Max Pool Size  : 4
Queue Capacity : 20
```

Example:

```text
HTTP Request Thread
        |
        v
prepareForExecution()
        |
        v
PROCESSING
        |
        v
Async Worker Thread
        |
        v
Test Execution
        |
        v
COMPLETED / FAILED
```

This prevents long-running medical test operations from blocking HTTP request threads.

---

# Concurrency Control

Test execution is protected against concurrent requests.

When an order is prepared for execution, the application retrieves it using a pessimistic database lock.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

This prevents two concurrent requests from starting the same `QUEUED` order simultaneously.

The state transition is therefore protected:

```text
QUEUED -> PROCESSING
```

Only one request can successfully perform this transition.

---

# WebSocket / Real-Time Updates

The backend provides real-time test order status updates using **WebSocket and STOMP**.

WebSocket endpoint:

```text
/ws
```

Clients subscribe to:

```text
/topic/test-orders
```

Example flow:

```text
Test Execution
      |
      v
Status Changed
      |
      v
Application Event
      |
      v
WebSocket Listener
      |
      v
/topic/test-orders
      |
      v
Connected Clients
```

Clients can receive updates for statuses such as:

```text
PROCESSING
COMPLETED
FAILED
```

without repeatedly polling the REST API.

---

# REST API

## Patients

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/patients` | Register and evaluate a patient |
| GET | `/api/patients` | Get all patients |
| GET | `/api/patients/{id}` | Get patient by ID |

---

## Test Orders

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/test-orders` | Create a medical test order |
| GET | `/api/test-orders` | Get all test orders |
| GET | `/api/test-orders/{id}` | Get test order by ID |
| POST | `/api/test-orders/{id}/execute` | Start asynchronous test execution |

---

## System

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Check backend application health |

---

# Validation

Incoming API requests are validated using Jakarta Validation.

Examples include:

```text
@NotBlank
@NotNull
@Email
```

Validation errors use a structured response format.

Example:

```json
{
  "errors": {
    "email": "Email format is invalid",
    "fullName": "Full name is required"
  }
}
```

---

# Exception Handling

The project uses centralized exception handling through:

```java
@RestControllerAdvice
```

Custom exceptions include cases such as:

```text
ResourceNotFoundException
InvalidTestOrderStateException
```

Example not-found response:

```json
{
  "error": "Resource not found",
  "message": "Patient not found: 100"
}
```

Example state conflict:

```json
{
  "error": "Test order state conflict",
  "message": "Only QUEUED test orders can be executed. Current status: COMPLETED"
}
```

HTTP status codes used by the API include:

```text
200 OK
201 Created
202 Accepted
400 Bad Request
404 Not Found
409 Conflict
```

---

# API Documentation

Swagger UI is available while the backend is running:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

The API documentation includes:

- Endpoint descriptions
- Request schemas
- Response schemas
- Validation responses
- Error responses
- HTTP status codes
- Patient operations
- Test order operations
- System health endpoint

---

# CORS

The backend is configured to accept REST requests from the local frontend development environment:

```text
http://localhost:3000
```

The Spring Boot backend runs on:

```text
http://localhost:8080
```

CORS is applied to:

```text
/api/**
```

---

# Database

The project currently uses an **H2 in-memory database**.

This makes the application simple to run locally without installing an external database server.

Because the database is in-memory, stored data is recreated when the application restarts.

---

# Running the Project

## Requirements

Make sure Java 17 is installed:

```bash
java -version
```

---

## Start the Backend

From the project root:

```bash
./mvnw spring-boot:run
```

The application starts at:

```text
http://localhost:8080
```

Health check:

```text
http://localhost:8080/api/health
```

---

# Testing

Run the complete automated test suite with:

```bash
./mvnw test
```

For a clean build:

```bash
./mvnw clean test
```

Current test suite:

```text
Tests run: 34
Failures: 0
Errors: 0
Skipped: 0
```

The test suite covers:

- Application context
- Patient admission rules
- Abstract Factory behavior
- Facade behavior
- Singleton scope
- Command execution
- Patient REST API
- Test Order REST API
- Request validation
- Exception handling
- Asynchronous execution
- Failure scenarios
- Concurrent execution
- Database locking
- WebSocket communication

---

# Project Structure

A simplified package structure:

```text
src/main/java/com/aliahmet/pms
│
├── config
│   ├── AsyncConfig
│   ├── CorsConfig
│   ├── OpenApiConfig
│   └── WebSocketConfig
│
├── exception
│   ├── ApiErrorResponse
│   ├── ValidationErrorResponse
│   ├── GlobalExceptionHandler
│   ├── ResourceNotFoundException
│   └── InvalidTestOrderStateException
│
├── patient
│   ├── Patient
│   ├── PatientController
│   ├── PatientService
│   ├── PatientRepository
│   ├── dto
│   ├── factory
│   └── facade
│
└── testorder
    ├── TestOrder
    ├── TestOrderController
    ├── TestOrderService
    ├── TestOrderRepository
    ├── command
    ├── department
    ├── execution
    ├── event
    └── websocket
```

---

# Key Backend Concepts Demonstrated

This project demonstrates practical usage of:

```text
REST API Design
Layered Architecture
Dependency Injection
Spring IoC Container
DTO Pattern
Jakarta Validation
Centralized Exception Handling
JPA / ORM
Database Transactions
Pessimistic Locking
Concurrency Control
Thread Pools
Asynchronous Processing
Application Events
WebSocket / STOMP
OpenAPI Documentation
CORS
Unit Testing
Integration Testing
Concurrency Testing
```

It also demonstrates the practical implementation of:

```text
Abstract Factory Pattern
Facade Pattern
Command Pattern
Observer Pattern
Singleton Pattern
```

---

## Status

Backend development is complete and covered by automated tests.

```text
Build Status: PASSING
Tests: 34 / 34
REST API: READY
WebSocket: READY
Swagger: READY
```
