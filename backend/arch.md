# Architecture Guide — Hexagonal (Ports & Adapters)

## Overview

This project uses a **hexagonal architecture** (also called ports & adapters). The core idea is to **isolate business logic from infrastructure**, so the domain can be tested, understood, and evolved without being tied to frameworks like Spring, JPA, or HTTP.

```
         ┌───────────────────────┐
         │     Controller        │  HTTP / REST layer
         │  (inbound adapter)    │
         └───────────┬───────────┘
                     │ depends on (interface only)
         ┌───────────▼───────────┐
         │   Inbound Port        │  Use-case contract (interface)
         │  application/port/in  │
         └───────────┬───────────┘
                     │ implements
         ┌───────────▼───────────┐
         │  Application Service  │  Use-case logic + orchestration
         │ application/service   │
         └───────────┬───────────┘
                     │ depends on (interface only)
         ┌───────────▼───────────┐
         │   Outbound Port       │  Contract to the outside world
         │  domain/port/out      │  (DB, external APIs, security)
         └───────────┬───────────┘
                     │ implements
         ┌───────────▼───────────┐
         │   Infrastructure      │  Real adapters (JPA, JWT, HTTP clients)
         └───────────────────────┘
```

Dependency arrow always points **inward**. The domain never knows about infrastructure.

---

## Why This Architecture?

| Goal | How it helps |
|------|-------------|
| **Testability** | Domain logic can be unit-tested without Spring, DB, or HTTP |
| **Swappable infrastructure** | Change DB from SQLite to PostgreSQL by swapping one adapter; business logic untouched |
| **Framework independence** | The domain layer has zero Spring/JPA annotations — it's pure Java |
| **Clear boundaries** | You always know where new code goes by what it does |

---

## Layer-by-layer breakdown

### 1. Controller (`controller/`)

**What it is**: The HTTP-facing layer. It receives REST requests and sends back responses.

**What it does**:
- Parses incoming JSON into DTOs (e.g. `CreateUserRequest`, `LoginRequest`, `ChatRequest`)
- Validates input (`@Valid`, `@NotBlank`, etc.)
- Calls an **inbound port** (never an implementation directly)
- Maps returned domain objects into response DTOs (e.g. `UserResponse`, `CartItemResponse`, `ProductResponse`)

**What it NEVER does**:
- Contains business logic or if/else decisions
- Accesses repositories or JPA directly
- Knows about database tables or passwords

**Example** (`UserController.java:47-50`):
```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public UserResponse register(@Valid @RequestBody CreateUserRequest request) {
    User user = new User(null, request.username(), request.email(), request.password(), null);
    User created = userUseCase.register(user);   // <— inbound port call
    return UserResponse.from(created);
}
```

---

### 2. Application Layer (`application/`)

This layer is split into two sub-packages:

#### 2a. Inbound Ports (`application/port/in/`)

**What it is**: An **interface** that declares what the application *can do* — the use-case menu.

**What it does**: Each interface represents a cohesive set of operations:

| Interface | Operations |
|-----------|-----------|
| `ProductUseCase` | getAllProducts, getProductById, getProductsByCategory, searchProducts, getCategories |
| `ProductImageUseCase` | getImage |
| `CartUseCase` | getCart, addToCart, removeFromCart |
| `UserUseCase` | register, login |
| `ChatUseCase` | chat |

**Why**: Controllers depend on these interfaces, not concrete classes. You could swap the entire implementation without touching a controller.

**Rule**: Controllers MUST inject inbound ports, never `application/service` classes directly.

#### 2b. Application Services (`application/service/`)

**What it is**: The **implementation** of the inbound ports. This is where use-case orchestration lives.

**What it does**:
- Orchestrates domain objects and outbound ports to fulfill a use case
- Manages transactions (`@Transactional`)
- Handles authorization (e.g., ownership checks via `SecurityContextProvider`)
- Coordinates multiple domain services or repositories

**Examples of orchestration logic**:
- `CartUseCaseService.addToCart()`: gets current userId from security context → looks up product via ProductUseCase → checks if product already in cart → adds new item or increments quantity
- `UserUseCaseService.register()`: delegates uniqueness validation to `UserRegistrationService` (domain service) → encrypts password via `PasswordEncryption` → saves via `UserRepository`

**What it NEVER does**:
- Contains HTTP-related code (request parsing, response mapping)
- Directly depends on infrastructure implementations (it depends only on port interfaces)
- Owns core business invariants (those go in domain)

---

### 3. Domain Layer (`domain/`)

This is the **heart of the application**. It must be **framework-agnostic** — no Spring annotations, no JPA annotations.

#### 3a. Domain Models (`domain/model/`)

**What it is**: Core business entities — pure Java objects.

| Model | Fields |
|-------|--------|
| `Product` | id, name, description, price, category, imageUrl, stock, rating |
| `User` | id, username, email, password, createdAt |
| `CartItem` | id, userId, productId, productName, unitPrice, quantity |
| `Authentication` | user, token |
| `ImageData` | data, mimeType |
| `ChatMessage` | role, content, toolCallId |
| `ChatResult` | reply, toolsUsed |

**What they contain**:
- Data fields with getters/setters
- Business methods (e.g., `CartItem.getSubtotal()`, `Product.builder()`)
- `equals` / `hashCode` based on identity

**What they do NOT contain**:
- JPA annotations (`@Entity`, `@Column`, etc.)
- Spring annotations (`@Component`, etc.)
- Any infrastructure knowledge

#### 3b. Domain Services (`domain/service/`)

**What it is**: Pure business logic that spans multiple models or needs outbound port access.

**Example**: `UserRegistrationService` — checks that username and email are unique before allowing registration. This is a business rule, not orchestration.

**Rules**:
- No Spring annotations — instantiated manually via `@Configuration` (see `DomainServiceConfig`)
- May depend on outbound port interfaces (`domain/port/out/`)
- Must NOT depend on other domain services (no circularity)

#### 3c. Domain Exceptions (`domain/exception/`)

**What it is**: Business-meaningful exceptions.

| Exception | When thrown | HTTP status |
|-----------|-----------|-------------|
| `DuplicateUserException` | Username or email already taken | 409 |
| `InvalidCredentialsException` | Wrong email or password | 401 |
| `AiServiceException` | AI service unavailable or all models failed | 502 |

These are thrown by the application/domain layer and caught by `GlobalExceptionHandler`.

#### 3d. Outbound Ports (`domain/port/out/`)

**What it is**: **Interfaces** that define contracts for infrastructure concerns.

| Interface | Purpose |
|-----------|---------|
| `ProductRepository` | Persist and retrieve Product entities |
| `ProductImageRepository` | Retrieve product image binary data |
| `CartRepository` | Persist and retrieve CartItem entities |
| `UserRepository` | Persist and retrieve User entities |
| `AiClient` | Send messages to the LLM and receive responses |
| `ChatToolExecutor` | Execute tool calls from the AI assistant (search, cart operations) |
| `CvDataProvider` | Load CV data for the AI chatbot to answer developer questions |
| `PasswordEncryption` | Hash and verify passwords (BCrypt) |
| `TokenService` | Generate and validate JWT tokens |
| `SecurityContextProvider` | Get the current authenticated user ID |

**Key insight**: The domain defines *what* needs to happen (interfaces), and infrastructure defines *how* (implementations).

---

### 4. Infrastructure Layer (`infrastructure/`)

This is where **real implementations** live. Infrastructure is allowed to use Spring, JPA, Feign, Kafka, etc.

#### 4a. Persistence (`infrastructure/persistence/`)

Sub-layers:

| Sub-package | Role |
|-------------|------|
| `entity/` | JPA entities (`ProductEntity`, `UserEntity`, `CartItemEntity`, `ProductImageEntity`) — annotated with `@Entity`, `@Table`, `@Column` |
| `repository/` | Spring Data JPA interfaces (`JpaProductRepository extends JpaRepository`, `JpaUserRepository`, `JpaCartItemRepository`, `JpaProductImageRepository`) **and** adapters (`ProductRepositoryImpl implements ProductRepository`, `UserRepositoryImpl`, `CartRepositoryImpl`, `ProductImageRepositoryImpl`) that map between domain and entity |
| `mapper/` | Converts between domain models and JPA entities (e.g. `ProductMapper.toDomain()` / `toEntity()`, `UserMapper`, `CartItemMapper`) |

**Why the split?** Domain models have no JPA annotations. JPA entities have them. Mappers bridge the two so you can swap persistence without touching domain.

**Adapter pattern**: `ProductRepositoryImpl` implements `ProductRepository` (the domain port) by delegating to `JpaProductRepository` and using `ProductMapper` to convert.

#### 4b. Security (`infrastructure/security/`)

| Class | Role |
|-------|------|
| `JwtTokenProvider` | Implements `TokenService` — generates and validates JWTs |
| `JwtAuthenticationFilter` | A `OncePerRequestFilter` that extracts JWT from `Authorization` header and sets `SecurityContext` |
| `SecurityContextProviderImpl` | Implements `SecurityContextProvider` — reads userId from Spring's `SecurityContextHolder` |

#### 4c. Client (`infrastructure/client/`)

| Class | Role |
|-------|------|
| `OpenRouterClient` | Implements `AiClient` — sends chat messages to OpenRouter API via `RestClient`, handles model fallback chain with sticky failover, parses tool calls from LLM responses |
| `OpenRouterProperties` | `@ConfigurationProperties(prefix = "openrouter")` — holds API key, base URL, primary model, and fallback model list |

The AI chatbot uses a **tool-calling agentic loop**: the LLM can request tool executions (`search_products`, `add_to_cart`, `view_cart`, etc.) which are dispatched to `ChatToolExecutorImpl`, which in turn calls back into inbound ports (`ProductUseCase`, `CartUseCase`).

#### 4d. Config (`infrastructure/config/`)

| Class | Role |
|-------|------|
| `SecurityConfig` | Spring Security setup — CSRF disabled, stateless sessions, JWT filter registration, endpoint rules |
| `GlobalExceptionHandler` | `@RestControllerAdvice` that maps domain exceptions to HTTP responses |
| `DomainServiceConfig` | `@Configuration` that instantiates domain services (since they're plain Java, not Spring beans) |
| `PasswordEncryptionImpl` | Implements `PasswordEncryption` using BCrypt |
| `OpenApiConfig` | Swagger/OpenAPI configuration with JWT bearer auth scheme |
| `CorsConfig` | CORS configuration — allows `http://localhost:4200` (Angular dev server) on `/api/**` |
| `CvDataConfig` | Loads `cv-data.md` from classpath and provides it as a `CvDataProvider` bean for the AI chatbot |
| `ChatToolExecutorImpl` | Implements `ChatToolExecutor` — dispatches AI tool calls to the appropriate use-case services |
| `DotEnvEnvironmentPostProcessor` | Custom `ApplicationListener` that reads `.env` files and injects properties into Spring's environment |

---

## Data Flow Example: Add Product to Cart

```
Client POST /api/cart?productId=5&quantity=2  (with JWT Bearer token)
  │
  ▼
CartController.addToCart(productId=5, quantity=2)
  │  Delegates to inbound port
  ▼
CartUseCase.addToCart(productId, quantity)  [interface — application/port/in]
  │
  ▼
CartUseCaseService.addToCart(productId, quantity)  [implementation — application/service]
  │  1. Calls securityContextProvider.getCurrentUserId()  ─→ outbound port ─→ SecurityContext
  │  2. Calls productUseCase.getProductById(productId)   ─→ inbound port  ─→ ProductUseCaseService
  │  3. Calls cartRepository.findByUserAndProduct(userId, productId) ─→ outbound port ─→ JPA adapter
  │  4. If product already in cart → calls cartRepository.updateQuantity()
  │     Otherwise → calls cartRepository.addItem(userId, productId, name, price, quantity)
  │  5. Returns saved CartItem domain object
  ▼
CartController maps CartItem → CartItemResponse (DTO)
  │
  ▼
HTTP 200 { "id":12, "productId":5, "productName":"Mechanical Keyboard", "unitPrice":79.99, "quantity":2, "subtotal":159.98 }
```

---

## Dependency Flow Diagram

```
Controller ──→ Inbound Port (interface)
                     │
                     ▼ (implements)
              Application Service
                     │
          ┌──────────┼──────────┐
          ▼          ▼          ▼
      Domain       Outbound    Domain
      Model        Port I/F    Service
                     │
                     ▼ (implements)
              Infrastructure
              (JPA, JWT, BCrypt, ...)
```

---

## What Goes Where — Quick Reference

| I have code that... | Put it in |
|--------------------|-----------|
| Handles HTTP requests/responses, parses JSON | `controller/` |
| Declares what a use case does | `application/port/in/` |
| Implements a use case, orchestrates calls | `application/service/` |
| Holds business data (no JPA/Spring) | `domain/model/` |
| Contains a pure business rule | `domain/service/` |
| Declares a persistence/external contract | `domain/port/out/` |
| Represents a business error | `domain/exception/` |
| Is a JPA entity (`@Entity`) | `infrastructure/persistence/entity/` |
| Maps between domain and entity | `infrastructure/persistence/mapper/` |
| Implements a repository contract | `infrastructure/persistence/repository/` |
| Spring Data JPA interface (`JpaRepository`) | `infrastructure/persistence/repository/` |
| Talks to an external API | `infrastructure/client/` |
| Is Spring configuration | `infrastructure/config/` |
| Is security (JWT, filters) | `infrastructure/security/` |
| Defines API request/response shape | `controller/dto/` |

---

## Key Principles

1. **Dependency points inward** — domain has no dependencies on infrastructure
2. **Programming to interfaces** — ports are interfaces; infrastructure implements them
3. **Separate domain from persistence** — domain models vs JPA entities, always mapped
4. **Transactions at the service layer** — `@Transactional` only in `application/service`
5. **Constructor injection** — no field injection, no `@Autowired` on fields
6. **No business logic in controllers** — controllers only translate HTTP ↔ domain
7. **DTOs at API boundaries** — never expose domain models directly in HTTP responses
