# Backend Code Review — Fix List

## CRITICAL (Fix before showing to anyone)

- [ ] **Cross-user cart manipulation** — `CartUseCaseService.removeFromCart()` has zero ownership validation. Any authenticated user can delete any other user's cart items by guessing the cartItemId. Fix: pass `userId` from the inbound port through to the repository layer.
  - `CartUseCaseService.java:61`
  - `CartRepositoryImpl.java:40-41`
  - `CartController.java:65-77`

- [ ] **Missing foreign keys on `cart_items`** — `user_id` and `product_id` have no `REFERENCES` constraints. Orphan records can reference deleted users/products.
  - `V1__create_tables.sql:31-32`

- [ ] **`JpaCartItemRepository.deleteByUserId` missing `@Modifying`** — Spring Data may load all entities individually instead of executing a bulk DELETE (N+1 performance bug).
  - `JpaCartItemRepository.java:18`

- [ ] **No log rotation** — Uses `FileAppender` instead of `RollingFileAppender`. `logs/errors.log` grows without limit until disk space is exhausted.
  - `logback-spring.xml:9-12`

- [ ] **Dockerfile: runs as root + single-stage build** — No `USER` directive (security risk). Single-stage build ships full JDK (~200MB wasted). No `HEALTHCHECK`.
  - `Dockerfile`

---

## HIGH (Strongly recommended fix)

### Security & Data Integrity

- [ ] **Missing `UNIQUE` constraint on `cart_items(user_id, product_id)`** — Enables duplicate entries + race condition in `addToCart()`.
  - `V1__create_tables.sql:29-36`

- [ ] **GET `/api/products/{id}` returns HTTP 200 with null body when not found** — Should return 404.
  - `ProductController.java:71-73`

- [ ] **Wrong exception type for "product not found"** — `AiServiceException` thrown instead of a proper domain exception (maps to 502 instead of 404).
  - `CartUseCaseService.java:44`

- [ ] **No rate limiting** on login/refresh endpoints (brute-force vulnerable).
  - `SecurityConfig.java` — no rate limiting configured anywhere

- [ ] **No `@Positive`/`@Min(1)` validation on cart `quantity` parameter** — Negative or zero quantities accepted.
  - `CartController.java:56-63`

- [ ] **NPE if `history` parameter is null** — `new ArrayList<>(history)` in ChatUseCaseService.
  - `ChatUseCaseService.java:86`

### Database & Queries

- [ ] **Missing indexes** on `cart_items.user_id`, `cart_items.product_id`, `refresh_tokens.user_id`, `refresh_tokens.expiry_date` — Full table scans on frequently queried columns.
  - `V1__create_tables.sql`
  - `V2__add_refresh_tokens_table.sql`

### Production Readiness

- [ ] **Missing Spring Boot Actuator** — No health check, metrics, or liveness/readiness probes.
  - `pom.xml` — missing `spring-boot-starter-actuator` dependency

- [ ] **Backend service missing `HEALTHCHECK`** in `docker-compose.yml`.
  - `docker-compose.yml:19-35`

- [ ] **Flyway Maven plugin hardcodes DB credentials** — Will fail in CI/CD.
  - `pom.xml:146-149`

---

## MEDIUM (Good practice / polish)

### Architecture Compliance

- [ ] **`StatusController` injects `DataSource` directly** instead of going through an inbound port interface. Violates hexagonal architecture.
  - `StatusController.java:23`

- [ ] **`@Transactional` on infrastructure repository implementations** — 8 occurrences in `RefreshTokenRepositoryImpl` and `CartRepositoryImpl`. Should only be in application layer.
  - `RefreshTokenRepositoryImpl.java:24,31,37,43`
  - `CartRepositoryImpl.java:31,39,52,61`

### Domain Model

- [ ] **Anemic `Product` model** — No validation in Builder. Null name, negative stock, rating outside [1,5] all pass.
  - `Product.java:17-73`

- [ ] **No validation in `CartItem`** — quantity can be negative, unitPrice can be null (NPE in `getSubtotal()`).
  - `CartItem.java:15-23`

- [ ] **Broken `CartItem.equals/hashCode`** — Based on `id` only; all unsaved items (id=null) are equal.
  - `CartItem.java:54-64`

### Controllers & DTOs

- [ ] **Password policy too weak** — Only `@Size(min=8)`, no complexity requirements (uppercase, digit, etc.).
  - `CreateUserRequest.java:17`

- [ ] **Missing `@Size(max=...)`** on username/email/password in CreateUserRequest. Large payloads cause BCrypt DoS.
  - `CreateUserRequest.java:10-19`

- [ ] **Chat history lacks validation** — No `@Valid` on list items, no `@NotBlank` on role/content in MessageDto.
  - `ChatRequest.java:17-18`
  - `MessageDto.java:7-11`

- [ ] **Open redirect via product image** — `ProductController` issues 302 to arbitrary `imageUrl`.
  - `ProductController.java:102-106`

### Infrastructure

- [ ] **`logback-spring.xml` missing `AsyncAppender`** — Synchronous file I/O can block under high load.
  - `logback-spring.xml`

- [ ] **`logback-spring.xml` missing `<springProfile>` sections** — No different log levels for dev vs prod.
  - `logback-spring.xml`

- [ ] **BCrypt strength not configurable** — Hardcoded to default (10).
  - `PasswordEncryptionImpl.java:11`

- [ ] **CORS origin hardcoded** to `localhost:4200`. Should be configurable.
  - `CorsConfig.java:17`

- [ ] **`JpaRefreshTokenRepository.revokeAllForUser` missing `@Param("userId")`** — Fragile without `-parameters` compiler flag.
  - `JpaRefreshTokenRepository.java:20`

- [ ] **PostgreSQL port exposed on `0.0.0.0:5432`** — Should bind to `127.0.0.1:5432`.
  - `docker-compose.yml:8-9`

- [ ] **PostgreSQL healthcheck uses hardcoded user** — Should use `${DB_USERNAME:-postgres}`.
  - `docker-compose.yml:13`

---

## LOW (Nice to have)

- [ ] **Missing tests** — AuthController, ProductImageUseCaseService, RefreshTokenRepositoryImpl, CartItemResponse DTO, RefreshTokenException, RefreshTokenEntity, ImageData model, JpaProductImageRepository, multiple config classes.

- [ ] **`ECommerceApiApplicationTests.contextLoads()` has zero assertions** — No-op test.
  - `ECommerceApiApplicationTests.java:10`

- [ ] **Empty `<name>`, `<description>`, `<url>`** in `pom.xml`.
  - `pom.xml:14-16`

- [ ] **No `maven-failsafe-plugin`** for separating unit vs integration tests.

- [ ] **No static analysis plugins** (checkstyle, PMD, SpotBugs) in `pom.xml`.

- [ ] **`GlobalExceptionHandlerTest` missing coverage** for `handleRefreshToken()` and `handleValidation()`.

- [ ] **Redundant `spring-security-crypto`** explicit dependency (already transitive via starter-security).
  - `pom.xml:57-59`

- [ ] **Swagger docs publicly exposed in all profiles** — Should be disabled in prod.
  - `SecurityConfig.java:40-43`

- [ ] **`UserUseCaseService.refreshToken()` integer division risk** — If `refreshExpirationMs < 1000`, yields 0 seconds.
  - `UserUseCaseService.java:43`

- [ ] **OpenRouter sticky fallback never resets** to primary model after temporary failure.
  - `OpenRouterClient.java:33,78-79`

- [ ] **`ProductMapper.toDomain` does not map image data** — Stored images are inaccessible through the product domain model.
  - `ProductMapper.java:11-25`

---

## What's already well done

- Constructor injection used consistently everywhere (zero field injection)
- Clean hexagonal architecture for all controllers except StatusController
- Zero Spring/JPA annotations in domain layer
- JWT token type checks prevent cross-type token usage
- Refresh token rotation with replay detection
- `.env` properly excluded from git in both root and backend `.gitignore`
- 48 test files covering controllers, services, domain models, and infrastructure
- Good AssertJ assertion style throughout
- Clean no-unused-imports record
- TODO/FIXME/HACK-free codebase
