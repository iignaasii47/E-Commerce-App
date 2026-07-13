# Project Architecture Guide (Spring Boot API)

This document defines the architectural rules, boundaries, and conventions for this codebase. Any automated agent or contributor MUST follow these rules when generating or modifying code.

---

# 1. Architecture Overview

This is a Spring Boot application following a hexagonal (ports & adapters) architecture.

System structure:

Controller (inbound adapter) → Inbound Port (interface) → Application Service (use-case) → Outbound Port (interface)
                                                                                              ↓
                                                                               Infrastructure Adapter (DB, external APIs)

Dependency flow is strictly inward only. Controllers depend on inbound port interfaces, never on concrete implementations.

---

# 2. Package Structure

com.example.app
├── controller              # HTTP layer (inbound adapter / REST APIs)
├── application
│   ├── port
│   │   └── in              # Inbound port interfaces (use-case contracts)
│   └── service             # Use-case implementations (application orchestration)
├── domain
│   ├── model               # Core business entities
│   ├── exception           # Domain-specific exceptions
│   ├── service             # Domain services encapsulating business rules
│   └── port
│       └── out             # Outbound port interfaces (repository/service contracts)
├── infrastructure
│   ├── persistence         # JPA / DB implementations (outbound adapters)
│   ├── client              # External API clients
│   ├── messaging           # Messaging systems (Kafka, etc.)
│   └── config              # Spring configuration
└── Application.java

---

# 3. Layer Responsibilities

## Controller Layer
- Handles HTTP requests/responses
- Performs lightweight validation
- Maps DTOs to service calls
- MUST NOT contain business logic

Forbidden:
- Business rules
- Direct repository access
- JPA usage

---

## Application Layer (application/port/in + application/service)
- Defines inbound port interfaces that declare the available use cases
- Implements use cases by orchestrating domain objects and outbound ports
- Defines transaction boundaries (@Transactional)
- Each inbound port → implementation pair represents an explicit application capability

Rules:
- Controllers MUST depend on inbound port interfaces, not concrete implementations
- No HTTP concepts
- No direct infrastructure dependency
- Depends only on outbound port interfaces

---

## Domain Layer
- Core business logic and invariants
- Entities and value objects
- Domain services (domain/service) hold business rules that span aggregates or require outbound port access

STRICT RULES:
- NO Spring annotations (@Component, @Service, etc.)
- NO JPA annotations (@Entity, etc.)
- NO infrastructure dependencies
- Must be framework-agnostic
- Domain services MUST NOT depend on other domain services (no circularity)
- Domain services MAY depend on domain/port/out interfaces

---

## Repository Layer (Ports)
- Defines persistence contracts (interfaces only)

Rules:
- No implementation logic
- No Spring Data or JPA code in interfaces

---

## Infrastructure Layer
- Implements technical details:
  - JPA repositories
  - External API clients
  - Messaging systems

Rules:
- Allowed to use Spring, JPA, Feign, etc.
- Must implement domain repository interfaces
- Must NOT contain business logic

---

# 4. DTO Rules

- DTOs are only for API boundaries
- Never expose domain models directly in controllers
- Mapping must occur in controller or dedicated mapper

Examples:
- CreateUserRequest
- UserResponse

---

# 5. Dependency Rules (STRICT)

Allowed:
- controller → application/port/in (inbound port interfaces)
- application/service → domain + domain/port/out (outbound port interfaces)
- domain/service → domain/model + domain/port/out
- infrastructure → domain/port/out + domain
- domain/model → nothing

Forbidden:
- domain → Spring/JPA
- controller → application/service directly (must go through inbound port)
- application/service → infrastructure implementations
- domain/service → another domain/service
- controller → repository directly

---

# 6. Spring Usage Rules

Allowed:
- @RestController → controller layer
- @Service → application layer (use-case implementations)
- @Repository → infrastructure layer
- @Component only for infrastructure utilities

Rules:
- Use constructor injection only
- Avoid field injection

---

# 7. Persistence Rules

- JPA entities belong in infrastructure.persistence
- Domain model must NOT be JPA entities (unless explicitly decided otherwise)
- Mapping between domain and persistence models is required

---

# 8. Error Handling

- Domain exceptions → domain.exception
- Global exception handler → controller or infrastructure.config
- Never expose internal stack traces to clients
- API errors are logged to `logs/trello.json`. When investigating errors, check this file first — the most recent log entries usually correspond to the error being investigated.

---

# 9. Transaction Rules

- @Transactional only in application layer (use-case implementations)
- Never in controller or domain
- Keep transactions short and focused

---

# 10. External Systems

- External APIs, messaging, and file systems MUST be wrapped in infrastructure adapters
- Must expose domain-facing interfaces

---

# 11. Code Style Rules

- Prefer immutability
- Keep business logic in application/domain (not controllers)
- Avoid anemic domain models where possible
- Keep methods small and focused

---

# 12. Agent Instructions (CRITICAL)

When generating or modifying code:

1. Respect package boundaries strictly
2. Never bypass inbound port interfaces from controllers — always inject interfaces from application/port/in, not concrete classes from application/service
3. Never introduce Spring or JPA into domain
4. Prefer interfaces over concrete coupling (inbound ports + outbound ports)
5. Default business rules to domain layer (model behavior or domain/service), orchestration logic to application/service
6. Maintain strict separation between DTOs and domain models
7. Do not optimize for brevity at the expense of architecture
8. Never leave unused imports — these are recurrent SonarQube issues (java:S1128)
9. When using AssertJ, chain multiple assertions on the same subject into a single chain (java:S5853). E.g., prefer `assertThat(x).isEqualTo(y).hasSameHashCodeAs(y)` over two separate `assertThat(x)` statements
10. Extract domain objects from `assertThatThrownBy` lambdas so the lambda contains only a single invocation (java:S5778). E.g., extract the `new Checklist(...)` to a local variable before the lambda

---

# 13. SonarQube Analysis

When referring to SonarQube analysis results, fetch them from the SonarQube server at:

```
http://localhost:9000
```

Use the SonarQube API to retrieve open issues for this project. The project key is `com.example:trello`.

Maven command to run a fresh analysis:
```
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.projectKey=com.example:trello
```

The SonarQube token is stored in `run-sonarqube.ps1`. Use the script or extract the token from it when making API calls.

API calls to SonarQube require Basic authentication with the token as username and an empty password:
```
Authorization: Basic <base64(token:)>
```

---

# 14. Architectural Philosophy

This project prioritizes:

- Maintainability over speed of implementation
- Clear boundaries over convenience
- Testability over tight coupling
- Explicit architecture over Spring magic