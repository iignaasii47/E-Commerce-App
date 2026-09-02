# TOFIX — Code Review Findings

Full scan of `backend/` (Spring Boot 4, hexagonal) and `frontend/` (Angular 22, standalone + signals).
Each entry: **files involved → issue → fix**. Severity: 🔴 high · 🟠 medium · 🟡 low.

---

## Backend (`backend/`)

### Security

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| B-SEC-1 | 🔴 | `src/main/resources/application.properties:18` | Fallback JWT secret hardcoded: `${JWT_SECRET:mySecretKeyForJWTTokenGeneration1234567890}` — if env var is missing the app silently starts with a publicly-known secret (prod profile requires it, default profile doesn't). | Remove the fallback (`${JWT_SECRET}` with no default) in **all** profiles so the app fails fast on startup. |
| B-SEC-2 | 🔴 | `application/service/OrderUseCaseService.java:82-85`, `controller/OrderController.java:65-72` | **IDOR**: `getOrderById` has no ownership check. Any authenticated user can read any order (name, address, zip, purchases) by enumerating IDs. `Order.userId` is never compared to `securityContextProvider.getCurrentUserId()`. | After `findById`, verify `order.getUserId().equals(currentUserId)`, else throw `OrderNotFoundException` (404). |
| B-SEC-3 | 🔴 | `application/service/UserUseCaseService.java:69-71,106-108`, `db/migration/V2__add_refresh_tokens_table.sql` | Refresh tokens stored **in plaintext** in DB. A DB dump hijacks all sessions for 7 days. | Store SHA-256 hash of the token; hash on save, hash-and-compare on lookup. |
| B-SEC-4 | 🟠 | `controller/CartController.java:56-60`, `application/service/CartUseCaseService.java:41-57` | No validation on cart `quantity`: `@RequestParam(defaultValue="1") int quantity` lacks `@Min(1)`/`@Max`. Negative quantity passes through to `decrementStock` → **stock is incremented** by ordering negatives; int overflow possible on `existing.getQuantity() + quantity`. | Add `@Min(1) @Max(999)` to the param, re-validate in the service, cap per-line quantity, guard overflow (long arithmetic). |
| B-SEC-5 | 🟠 | `application/service/UserUseCaseService.java:76-111` | Refresh-token rotation is not atomic (read → check revoked → save revoked → save new). Two concurrent refreshes with the same token both succeed (double spend). | Atomic conditional update: `UPDATE refresh_tokens SET revoked=true WHERE id=? AND revoked=false`; 0 rows updated ⇒ reuse detected ⇒ `revokeAllForUser`. |
| B-SEC-6 | 🟠 | `infrastructure/security/RateLimitingFilter.java:24,50-70` | Rate limiter: `requestTimestamps` map never evicts → unbounded memory growth (DoS via spoofed IPs); keys on `getRemoteAddr()` (behind a proxy all clients share one bucket); exact-path `equals()` matching breaks with context-path/trailing-slash variants. | Use a bounded cache with TTL eviction (Caffeine or Bucket4j); enable `server.forward-headers-strategy=framework`; match paths with `AntPathMatcher`; schedule cleanup. |
| B-SEC-7 | 🟠 | `infrastructure/security/JwtAuthenticationFilter.java:45-54` | Invalid/expired token → `response.sendError(401)` (Tomcat default error page, bypasses the JSON error contract) and blocks **public** endpoints: an expired token on `GET /api/products` returns 401 instead of anonymous 200. | On parse failure: `SecurityContextHolder.clearContext()` and continue the filter chain; emit JSON errors via a custom `AuthenticationEntryPoint`. |
| B-SEC-8 | 🟠 | `infrastructure/config/CorsConfig.java:17-20` | CORS origin `http://localhost:4200` hardcoded in code; `allowedHeaders("*")` with credentials. Any other deployment origin requires a code change. | Externalize to a property (`app.cors.allowed-origins`, comma-separated, per-profile); use `setAllowedOriginPatterns`; restrict headers. |
| B-SEC-9 | 🟠 | `domain/service/UserRegistrationService.java:30-42`, `controller/dto/CreateUserRequest.java:17` | Weak password policy: only rejects fully-numeric (`\d+`) and fully-alphabetic passwords (`1234 6789` passes); DTO only checks `@Size(min=8)`. | Enforce a real policy (Passay or NIST-style): min length 12, deny-list of common passwords; align DTO and domain messages. |
| B-SEC-10 | 🟠 | `GlobalExceptionHandler.java` (missing handler), `domain/service/UserRegistrationService.java` | Register does `existsBy...` then save (TOCTOU). Under a race the DB unique constraint throws `DataIntegrityViolationException` → generic **500** instead of 409. | Add a `@ExceptionHandler(DataIntegrityViolationException.class)` → 409, or catch-and-translate in the repository adapter. |
| B-SEC-11 | 🟡 | `controller/dto/ChatRequest.java:20-25`, `controller/ChatController.java:48-61` | Chat input unbounded (no `@Size`) and client-controlled `role` passes through verbatim — clients can inject `"system"`/`"tool"` messages into the LLM context (prompt injection). History length unbounded → token-cost DoS. | `@Size(max=…)` on message (e.g. 2000 chars) and history (e.g. 20 entries); whitelist roles to `user`/`assistant`; drop/normalize anything else. |
| B-SEC-12 | 🟡 | `controller/StatusController.java:34-40` | `catch (Exception _)` swallows DB errors with **no logging**; also publicly discloses DB up/down status (infra recon). | Catch `SQLException` specifically, log at WARN, and hide the DB detail from unauthenticated callers (or drop it — Actuator gives this for free). |
| B-SEC-13 | 🟡 | `db/migration/R__seed_data.sql:54-56` | `guest` account with committed BCrypt hash in a repeatable migration — permanent default account if the password is guessable. | Move seed users to a dev-only profile/migration, or document + rotate the password. |
| B-SEC-14 | 🟡 | `application.properties:13-16` + `application-prod.properties` | Prod profile (used by docker-compose) inherits dev-flavored base: Swagger (`/docs`, `/v3/api-docs`) public in prod, `spring.jpa.show-sql=true` in prod. | Add `springdoc.api-docs.enabled=false` and `show-sql=false` to prod profile (see also B-CFG-2). |
| B-SEC-15 | 🟡 | root `.env` | Real secrets (`OPENROUTER_API_KEY`, `SONAR_TOKEN`, `NVD_API_KEY`, `JWT_SECRET`) live in the working tree. Not git-tracked and no history leak (verified), but should be treated as exposed once the repo is shared. | Rotate the tokens as a precaution; add a pre-commit secret scanner (gitleaks); keep `.env` gitignored. |

> Verified clean: no SQL injection (all queries parameterized, sort fields whitelisted), no entity leakage in DTOs, no `System.out`/TODO/FIXME leftovers.

### Concurrency / correctness

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| B-CC-1 | 🔴 | `application/service/OrderUseCaseService.java:54-71`, `infrastructure/persistence/repository/ProductRepositoryImpl.java:61-66` | **Oversell race**: order placement does check-then-act (`getStock()` compare → `decrementStock`), and `decrementStock` is a read-modify-write (`findById` → `setStock(stock-qty)` → `save`). Two concurrent orders both pass the check → lost updates / negative stock. Silent no-op if the product vanished mid-flight (`ifPresent`). | Replace with an atomic conditional update: `@Modifying @Query("UPDATE ProductEntity p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")` returning `int`; throw `InsufficientStockException` when 0 rows updated. Remove the app-level stock read. |
| B-CC-2 | 🔴 | `application/service/CartUseCaseService.java:49-56`, `db/migration/V1__create_tables.sql:29-36` | **Duplicate cart rows race**: `addToCart` does check-then-act (`findByUserAndProduct` → insert) and `cart_items` has **no UNIQUE(user_id, product_id)**. Concurrent adds create two rows; later updates touch one arbitrarily. | Add migration `ALTER TABLE cart_items ADD CONSTRAINT uq_cart_user_product UNIQUE (user_id, product_id)`; use `ON CONFLICT ... DO UPDATE SET quantity = quantity + ?` (or pessimistic lock) for the upsert. |
| B-CC-3 | 🟠 | `controller/OrderController.java:38-55`, `application/service/OrderUseCaseService.java:44-71` | No idempotency on `POST /api/orders`: double-submit/retry → two orders + double stock decrement; concurrent placements both read the same cart before either clears it. | Client-supplied `Idempotency-Key` stored with a UNIQUE constraint on orders, or `SELECT ... FOR UPDATE` on the user's cart rows inside the transaction. |

### Architecture

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| B-ARC-1 | 🟠 | `controller/StatusController.java:23-40` | Layer skipping: controller injects `DataSource` and opens a JDBC `Connection` directly (AGENTS.md forbids persistence in controllers). | Delete and use Spring Boot Actuator health endpoint, or move the check behind an outbound port + use case. |
| B-ARC-2 | 🟠 | `infrastructure/config/GlobalExceptionHandler.java:111-116` | Generic `@ExceptionHandler(Exception.class)` hijacks framework exceptions → malformed JSON, bad path-variable types (`/api/products/abc`), unknown URLs, missing params, wrong methods all return **500** instead of 400/404/405. | Add handlers for `HttpMessageNotReadableException` (400), `MethodArgumentTypeMismatchException` (400), `NoResourceFoundException` (404), `MissingServletRequestParameterException` (400), `HttpRequestMethodNotSupportedException` (405); keep `Exception` as last resort. |
| B-ARC-3 | 🟠 | `application/service/CartUseCaseService.java:22,43` | Use case depends on another use case's **inbound** port (`ProductUseCase`) to fetch a product — application-to-application coupling; hexagonal purists route through the outbound port. | Inject `ProductRepository` (outbound port) instead of `ProductUseCase`. |
| B-ARC-4 | 🟠 | `infrastructure/config/PasswordEncryptionImpl.java`, `infrastructure/config/ChatToolExecutorImpl.java` | Misplaced classes: a security adapter and an AI tool adapter live in `infrastructure/config/` (a grab-bag), while `arch.md` documents `security/` and `client/` as their homes. | Move `PasswordEncryptionImpl` → `infrastructure/security/`, `ChatToolExecutorImpl` → `infrastructure/client/`. |
| B-ARC-5 | 🟠 | `domain/port/out/SecurityContextProvider.java`, `TokenService.java`, `PasswordEncryption.java`, `ChatToolExecutor.java`, `domain/port/out/AiClient.java:11` | Port placement inconsistent: security ports and an application-orchestration port (`ChatToolExecutor`) sit in the *domain* layer while inbound ports live in `application/port/in`. `AiClient.sendMessage(..., List<Map<String,Object>> tools)` leaks the OpenRouter wire format into a domain contract. | Move security/orchestration ports to `application/port/out`; define a typed tool-definition model instead of `Map<String,Object>`. |
| B-ARC-6 | 🟡 | `domain/model/User.java`, `Order.java`, `RefreshToken.java:32-34`, `application/service/*` | Anemic domain: stock validation, quantity accumulation, and token-rotation state machine live in application services. `RefreshToken.isValid()` exists but is never used (expiry re-implemented inline). | Push invariants into models (`RefreshToken.assertUsable()`, `Order.place(...)` factory) and actually use `isValid()`. |
| B-ARC-7 | 🟡 | `application/service/UserUseCaseService.java:93`, `infrastructure/persistence/repository/JpaRefreshTokenRepository.java:22-24` | Housekeeping as a use-case side effect: `deleteExpired()` runs a table-wide DELETE on **every** token refresh. | Move to a `@Scheduled` job (hourly) or DB-level cleanup. |
| B-ARC-8 | 🟡 | `backend/AGENTS.md:23` | Doc drift: documents package root `com.example.app`; actual root is `com.iignaasii47.e_commerce_api`. | Update AGENTS.md. |

### Code smells

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| B-SMELL-1 | 🟠 | `application/port/in/ProductUseCase.java:11,15`, `ProductUseCaseService.java:23-39`, `domain/model/ChatMessage.java:25-31`, `RefreshToken.java:32-34`, `ChatResult.java:15-17`, `OrderMapper.java:37-50`, `domain/port/out/UserRepository.java:11` + impl chain, `ProductEntity.java:44-46` | Dead code (verified by cross-reference): `getAllProducts`/`getProductsByCategory` port methods + the `ProductRepository.findAll/findByCategory` chain have **no callers**; `ChatMessage.assistant()/system()`, `RefreshToken.isValid()`, `ChatResult.of()`, `OrderMapper.toEntity()` unused; `UserRepository.findByUsername` chain unused; `ProductEntity.image` association never read. | Delete them (or wire them up). Also rename `ProductController.getAllProducts` — it actually calls `getProducts(...)` and shadows the dead port method's name. |
| B-SMELL-2 | 🟠 | `UserUseCaseService.java:68,105`, `RefreshToken.java:25`, `RefreshTokenRepositoryImpl.java:45`, `UserEntity.java:43`, `OrderEntity.java:53`, `GlobalExceptionHandler.java:121` | "Now in UTC" spelled 5 different ways; no injected `Clock` → time-dependent behavior untestable. | Inject `java.time.Clock` (bean with `Clock.systemUTC()`), single helper for `Instant.now(clock)`. Also extract the duplicated revoke-copy logic (`UserUseCaseService.java:98-100,119-122`) into `RefreshToken.revoke()`. |
| B-SMELL-3 | 🟠 | `infrastructure/client/OpenRouterClient.java:120-124,194,240-242` | Broad catches: `if (isNonRetryable(...)) throw X; throw X;` — both branches identical, so 401/402/403 errors are retried against **every** fallback model (wasteful); `catch (Exception _)` on JSON parse returns empty map → downstream tool fails cryptically; manual `indexOf("\"message\":\"")` string parsing breaks on escaped quotes. | Honor `isNonRetryable` (abort fallback chain on auth/credit errors); parse error bodies with Jackson `readTree`; narrow catches to expected types. |
| B-SMELL-4 | 🟡 | `infrastructure/client/OpenRouterClient.java:31,66-69,190-198`, `infrastructure/persistence/repository/CartRepositoryImpl.java:51-55` | Null-as-control-flow: `trySend` returns `null` on failure; `findByUserAndProduct` maps `Optional → orElse(null)` forcing null checks upstream. `RestClient` field is package-private non-final (test seam). `new ObjectMapper()` created per tool-call argument parse. | Return `Optional<...>` from ports; make `restClient` `private final` (inject for tests); share a static/injected `ObjectMapper`. |
| B-SMELL-5 | 🟡 | `controller/dto/LoginResponse.java:17`, DTO classes generally | `LoginResponse extends UserResponse` — DTO inheritance couples JSON shape to base-class changes; mixed DTO styles (records vs Lombok classes). | Flatten `LoginResponse` (composition: `user` + tokens); standardize DTO style (prefer records for request/response payloads). |
| B-SMELL-6 | 🟡 | `infrastructure/config/DotEnvEnvironmentPostProcessor.java:19` | Misnamed: implements `ApplicationListener<ApplicationEnvironmentPreparedEvent>`, not `EnvironmentPostProcessor` (registered via `spring.factories`). | Rename to reflect mechanism or implement `EnvironmentPostProcessor` directly (see B-CFG-1). |
| B-SMELL-7 | 🟡 | `controller/OrderController.java:69-70`, `controller/ProductController.java:54,139`, `infrastructure/config/ChatUseCaseService.java` (`@SuppressWarnings("unchecked")` unnecessary), `SecurityConfig.java:34` (unexplained `@SuppressWarnings`), `RateLimitProperties.java:13-16` (no in-code defaults → filter silently limits nothing if properties missing), `pom.xml:14-28` (empty `<name/>` etc.) | Misc small smells. | Inline import instead of FQN; type the `ResponseEntity`s; drop dead suppression; give `RateLimitProperties` in-code defaults; remove empty POM elements. |

### Performance / resources

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| B-PERF-1 | 🟠 | `infrastructure/persistence/repository/ProductRepositoryImpl.java:49-53`, `infrastructure/config/ChatToolExecutorImpl.java:56-60` | Unbounded product lists: `searchByNameOrDescription` returns **all** matching rows; the chat tool feeds an LLM-controlled query into it and formats everything into one giant string (memory + token blowup). | Cap results (`PageRequest.of(0, 10)`) in the chat tool path and in the port method. |
| B-PERF-2 | 🟠 | `ProductImageEntity` / `V1__create_tables.sql:12-19`, `domain/model/ImageData.java:9,14`, `controller/ProductController.java:140-155` | Product images stored as `byte[]` in Postgres and fully loaded into heap; `ImageData` defensively clones the array **twice** per request. | Stream the image (`StreamingResponseBody`/`InputStream`); single defensive copy; longer term, move blobs to object storage/CDN. |
| B-PERF-3 | 🟡 | `infrastructure/client/OpenRouterClient.java:52-56` | No connect/read timeouts on the RestClient → a hung OpenRouter connection pins servlet threads. | Configure `ClientHttpRequestFactorySettings` with connect/read timeouts (e.g. 5s/30s). |
| B-PERF-4 | 🟡 | `application/service/CartUseCaseService.java:52-53` | After `updateQuantity`, re-queries `findByUserAndProduct` just to return the row. | Have `updateQuantity` return the updated item. |

### API design

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| B-API-1 | 🔴 | `GlobalExceptionHandler.java`, `controller/ProductController.java:67-89` (plain-string error bodies), `infrastructure/security/RateLimitingFilter.java:65` (`{error:…}` shape), `JwtAuthenticationFilter.java:52` (Tomcat default) | **Four different error payload shapes** across the API; Swagger documents only one. | Route every error through `GlobalExceptionHandler` (throw domain exceptions / `ResponseStatusException` from filters via an `AuthenticationEntryPoint` + `AccessDeniedHandler`) — one JSON contract. |
| B-API-2 | 🟡 | `controller/CartController.java:65-77` | `DELETE /api/cart/{id}` returns 200 + `{"status":"removed"}`. | Return `204 No Content`. |
| B-API-3 | 🟡 | `controller/ProductController.java:146-155` | Image endpoint: redirect target straight from DB `image_url`; `MediaType.parseMediaType(img.getMimeType())` throws → 500 on dirty DB data. | Validate/sanitize stored MIME type, fall back to `application/octet-stream`. |
| B-API-4 | 🟡 | `controller/OrderController.java`, `controller/UserController.java` | `201 Created` responses carry no `Location` header. | Add `Location: /api/orders/{id}` etc. |

### Configuration

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| B-CFG-1 | 🟠 | `infrastructure/config/DotEnvEnvironmentPostProcessor.java:24-65` | `.env` values inserted with `addFirst` → **override command-line args and OS env** (12-factor violation); values not unquoted (`KEY="v"` keeps quotes); all keys become global Spring properties. | Implement `EnvironmentPostProcessor` properly; parse/unquote properly; insert below OS env (`addLast`); namespace keys. |
| B-CFG-2 | 🟠 | `application.properties` vs `application-prod.properties` (9-line patch) | Prod profile inherits dev-flavored base (swagger on, show-sql on, localhost CORS, devtools). docker-compose sets `SPRING_PROFILES_ACTIVE=prod`, so prod runs with all of it. | Invert: make the **base** prod-safe; put dev conveniences in `application-dev.properties`. |
| B-CFG-3 | 🟡 | `pom.xml:160-166` (Flyway plugin hardcodes localhost/DB creds), rate-limit paths duplicated across `application.properties`, test properties, and `RateLimitProperties`; model list duplicated in base + test properties | Config duplication → drift risk. | Single source of truth: point Flyway plugin at env vars; keep path defaults in one place. |
| B-CFG-4 | 🟡 | `src/main/resources/logback-spring.xml:9-17` | `ERROR_FILE` appender is a root INFO appender → `logs/errors.log` receives **all** logs, not errors (name lies, AGENTS.md §8 sends agents to the wrong file); no rolling policy → unbounded growth. | Add `ThresholdFilter` (ERROR) + `RollingFileAppender` with size/time policy. |
| B-CFG-5 | 🟡 | `src/main/resources/cv-data.md` | Committed template with "Your Name" placeholder — the AI assistant recites placeholder content. | Fill in real content or remove the feature. |

### Testing

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| B-TEST-1 | 🟠 | (missing) `OrderUseCaseServiceTest` | The most business-critical, concurrency-prone use case (stock check, totals, cart clearing) has **zero** unit tests — only controller-level with mocked port. | Add `OrderUseCaseServiceTest`: stock exhaustion, totals math, cart clearing, negative quantity. |
| B-TEST-2 | 🟠 | `src/test/resources/application.properties` | `spring.flyway.enabled=false` + H2 → **migrations never validated** in CI; H2≠Postgres dialect differences can hide bugs. | Run integration tests against real Postgres (Testcontainers) with Flyway enabled. |
| B-TEST-3 | 🟡 | 9 DTO test classes (`ChatRequestTest`, `LoginResponseTest`, `MessageDtoTest`, …) | Lombok getter/setter tests — near-zero value, inflate coverage. | Delete; cover real logic instead. |
| B-TEST-4 | 🟡 | `RateLimitingIntegrationTest.java:28-62` | Depends on shared singleton limiter state + nonexistent user (`test@example.com`) → brittle; no chat-limit or `Retry-After` test. | Isolate state per test; add chat-path and header assertions. |
| B-TEST-5 | 🟡 | Missing: `CorsConfig`, `DotEnvEnvironmentPostProcessor`, `SecurityConfig` endpoint rules, `ProductImageUseCaseService`, `GlobalExceptionHandler` generic/validation handlers, refresh-token reuse (revoked → revoke-all) integration | Coverage gaps. | Add targeted tests, esp. security-config rules and the refresh-reuse path. |

### Build / Docker

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| B-DOCKER-1 | 🟠 | `backend/Dockerfile:1-14` | Runs as **root** (no `USER`); single-stage build ships JDK + Maven caches in the runtime image (`26-jdk` instead of JRE/distroless); no `HEALTHCHECK`; built with `-DskipTests`. | Multi-stage: builder stage → `eclipse-temurin:26-jre` (or distroless) runtime, non-root `USER`, layered jar extraction for caching, healthcheck. |
| B-DOCKER-2 | 🟡 | `backend/pom.xml:33-34` | Silent version overrides of Spring Boot BOM (`tomcat.version`, `postgresql.version`) — Renovate will fight these. | Remove or document why they're pinned. |
| B-CI-1 | 🟡 | `.github/workflows/backend-ci.yml` | `runs-on: self-hosted` with Sonar at `http://localhost:9000` — CI correctness depends on a colocated SonarQube; actions not pinned by SHA. | Pin action versions/SHAs; make Sonar URL a configurable secret/variable. |

---

## Frontend (`frontend/`)

### Architecture

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| F-ARC-1 | 🔴 | `src/environments/environment.ts:2`, `angular.json` (prod config, no `fileReplacements`), all services (`auth.service.ts:46-47`, `cart.service.ts:13`, `product.service.ts:53,95,104`, `order.service.ts:10`, `chatbot.service.ts:22`, `status.service.ts:49`) | Hardcoded `http://localhost:8080` API URL shipped to production — **no prod environment file exists**; every service builds absolute URLs off it. | Add `environment.production.ts` + `fileReplacements` in angular.json — better: use relative `/api` paths behind a reverse proxy so the build is environment-agnostic. |
| F-ARC-2 | 🔴 | `services/auth.interceptor.ts:11-12,37,40,46,54-58`, `services/auth.service.ts:84-86` | **Interceptor refresh logic has two deadlocks**: (1) on refresh failure the `refreshTokenSubject` is set to `null` and never errored → queued concurrent 401 requests **hang forever**; (2) `isRefreshing` is module-level and never reset if `refreshToken()` throws synchronously ("No refresh token available") → all future 401s queue forever. Also line 40 uses `auth.getAccessToken()!` (can be null after failed refresh). | Error/complete the subject on failure so waiters receive the error; reset `isRefreshing` in `finalize`; null-check the token; consider a `BehaviorSubject` + `exhaustMap` pattern. |
| F-ARC-3 | 🔴 | `tsconfig.json:5-21` | **`strict` mode disabled** — no `noImplicitAny`, `strictNullChecks`, `strictPropertyInitialization`, no `strictTemplates`. The non-null assertions and silent-undefined bugs below (F-SMELL-2, F-UX-4) are exactly what strict mode surfaces. | Enable `"strict": true`, `strictTemplates`, `noUnusedLocals`, `noUnusedParameters`; fix resulting errors. |
| F-ARC-4 | 🔴 | `frontend/Dockerfile:1-12` | "Production" container runs `npx ng serve` — the **dev server** (no optimization, source maps, hot reload), on unpinned `node:24`, as **root**, no healthcheck. | Multi-stage: `ng build` (prod config) → `nginx:alpine` serving `dist/` with gzip + SPA fallback; non-root user; healthcheck. |
| F-ARC-5 | 🟠 | `services/auth.service.ts:59-70` | Register auto-logs-in **without tokens**: `RegisterResponse` has no `accessToken`/`refreshToken`, but `currentUser` is set → `isLoggedIn()===true` with zero tokens → every API call 401s → refresh throws synchronously (see F-ARC-2). | Navigate to `/login` after register (don't set `currentUser`), or have the backend return tokens on register. |
| F-ARC-6 | 🟠 | `services/cart.service.ts:32-34`, `services/product.service.ts:31-34`, `services/status.service.ts:43-45` | Root services fire HTTP **in constructors** at app bootstrap — anonymous users on `/login` trigger cart/products/status calls → guaranteed 401s → refresh churn → logout + redirect fighting the router. | Lazy `ensureLoaded()` called from page `ngOnInit`; skip loads when `!auth.isLoggedIn()`. |
| F-ARC-7 | 🟠 | `services/product.service.ts:62-66`, `services/cart.service.ts:36-41`, `pages/products/products.component.ts:69-74`, `pages/cart/cart.component.ts:58-67` | Services **swallow errors** and clear state: backend outage renders as "no matching products found" and "Your cart is empty". No `loading`/`error` signals anywhere (only `status.service` has an error state). | Add `loading` + `error` signals to ProductService/CartService; render explicit error states in pages. |
| F-ARC-8 | 🟠 | `services/cart.service.ts:104-110` | `clearCart()`: optimistic wipe (`items.set([])`) **before** fire-and-forget DELETEs with no error handlers → server cart keeps items while UI shows empty. Method is also **dead** (no component calls it). | Fix with `forkJoin` + error handling — or delete the method. |
| F-ARC-9 | 🟡 | `services/auth.service.ts:100` | `logout()` fire-and-forget POST with no error observer → console noise when backend is down (401 is excluded from refresh). | Add `subscribe({ error: () => {} })` or `.catchError(() => EMPTY)`. |
| F-ARC-10 | 🟡 | `app.routes.ts:51-59`, `services/auth.guard.ts:5-14`, `pages/auth/login/login.component.ts:96` | Guard gaps: no inverse guard on `/login`//`register` (authenticated users can revisit them via URL); no `returnUrl` preserved → post-login always lands on `/`, dropping deep links. | Add a `guestGuard`; carry `returnUrl` query param through the guard and login redirect. |
| F-ARC-11 | 🟡 | `pages/order-confirmation/order-confirmation.component.ts:196` | Uses `route.snapshot.paramMap` → navigating `/order/1` → `/order/2` reuses the component with stale data. | Subscribe to `paramMap` (or `toSignal(paramMap)`). |
| F-ARC-12 | 🟡 | `models/chat-message.model.ts:2`, `services/chatbot.service.ts:9`, `services/chatbot.service.spec.ts:51` | Role contract drift: model says `'user' \| 'bot'`, service types history as loose `{ role: string; content: string }`, spec uses `'assistant'`. | Tighten the union type end-to-end and map to backend/AI roles in one place. |
| F-ARC-13 | 🟡 | `utils.ts:7-29` | `handleHttpError` exists but is used by only 2 components; `checkout.component.ts:221`, `chatbot.component.ts:394`, `order-confirmation.component.ts:210`, `cart.service.ts:58-80` re-implement error extraction inconsistently. | Standardize on one helper (extend it for `err.error?.error` and 429 payloads). |

### Security

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| F-SEC-1 | 🟠 | `services/auth.service.ts:112-115` (writes), `:83,97,108-110,132` (reads) | JWTs (including the 7-day refresh token) stored in `localStorage` — any XSS ⇒ full account takeover. | Move refresh token to httpOnly cookie (backend change); keep access token in memory only. Minimum: `sessionStorage` + short TTL. |
| F-SEC-2 | 🟡 | `pages/chatbot/chatbot.component.ts:394`, `pages/checkout/checkout.component.ts:221`, `pages/order-confirmation/order-confirmation.component.ts:210` | Raw backend/transport error messages (`err.message` includes `Http failure response for http://localhost:8080/...: 0 Unknown Error`) surfaced verbatim in UI. | Route through `handleHttpError` with whitelisted fallbacks. |
| F-SEC-3 | 🟡 | `pages/checkout/checkout.component.ts:52-61,177-178` | Card number/expiry collected into signals, never validated, never sent — implies real payment collection without doing anything. | Either remove the fields or validate (Luhn/expiry) and wire to a real PSP. |

> Verified clean: **no XSS vectors** — no `innerHTML`/`DomSanitizer` bypass anywhere; chat renders via interpolation only. No `console.log` leftovers, no `any` in production code, no TODO/FIXME.

### UX / state bugs

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| F-UX-1 | 🟠 | `services/cart.service.ts:25-30` | `ngOnDestroy` **drops** pending debounced syncs instead of flushing — a quantity change <1s before navigating/refreshing is never persisted (server keeps old value, UI showed the new one). | Flush pending syncs on destroy (and on `beforeunload`). |
| F-UX-2 | 🟠 | `services/cart.service.ts:118-121` | Quantity update implemented as **DELETE + POST** (non-atomic): POST failure after DELETE succeeded destroys the item server-side; interleaving with `removeFromCart` can double-DELETE → 404 toast. | Add a `PATCH /api/cart/{id}` endpoint on the backend (preferred), or sequence per-item requests (`exhaustMap`/per-item lock). |
| F-UX-3 | 🟠 | `pages/products/products.component.ts:187-190`, `services/product.service.ts:52-67` | Search fires on **every keystroke** with no debounce and no cancellation → slow earlier responses can overwrite newer ones; heavy typing also trips the 429 rate limiter. | `debounceTime(300)` + `switchMap` (with `takeUntilDestroyed`), or a signal-effect with request-id guard. |
| F-UX-4 | 🟠 | `pages/products/product-detail/product-detail.component.ts:259-281` | Invalid product id (`/products/abc` → `NaN`): `if (id)` is falsy → `loading` never set false → **infinite spinner**. | Handle `NaN`/`<= 0` with the not-found branch. |
| F-UX-5 | 🟡 | `components/layout/terminal-statusbar/terminal-statusbar.component.ts:13-14` | Statusbar hardcodes green dot + "connected", ignoring `StatusService.apiStatus()` which exists for this. | Bind to `apiStatus()`; show red/degraded when API is down. |
| F-UX-6 | 🟡 | `services/rate-limit.interceptor.ts:11-14` + component handlers (e.g. chatbot) | Double error feedback on 429: global toast + component-level error message for the same event. | Suppress component-level handling when the interceptor already notified (e.g. mark the rethrown error). |
| F-UX-7 | 🟡 | `pages/products/products.component.ts:174-185` | `?q=` query param read only once in `ngOnInit` and never re-read (navigation to `/products?q=x` while on the page does nothing); nothing in the app ever links with `?q=`. | React to `queryParamMap` changes; add search-synchronization if the param is meant to be shareable. |
| F-UX-8 | 🟡 | `pages/checkout/checkout.component.ts:27-45` | Validation errors only cleared on next submit, not while typing. | Clear per-field errors on value changes. |
| F-UX-9 | 🟡 | `pages/chatbot/chatbot.component.ts:377,397`, template line 22 | Error messages are appended to chat history as `'[error] …'` **strings** (stringly-typed protocol parsed back in the template) and then sent to the LLM as context — pollutes AI conversation. | Add an `isError` flag to `ChatMessage`; exclude error entries from history sent to backend. |
| F-UX-10 | 🟡 | `pages/chatbot/chatbot.component.ts:21` | `track msg.timestamp` — two messages in the same millisecond collide as duplicate track keys. | Use a monotonically increasing id on `ChatMessage`. |

### Code smells

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| F-SMELL-1 | 🟠 | `pages/chatbot/chatbot.component.ts` (468 lines) | Fat component: typewriter engine (404-447), spinner effect, scroll effect, command-history shell, cursor tracking, timestamp formatting, cart-reload side effect all in one component. | Extract a `ChatStore` service + typewriter util; component only renders. (Runners-up: `product-detail.component.ts` 303, `cart.component.ts` 240, `checkout.component.ts` 226.) |
| F-SMELL-2 | 🟠 | `pages/order-confirmation/order-confirmation.component.ts:28,32,36,44,54,63,67,71`, `services/auth.interceptor.ts:40`, `chatbot.component.ts:436` | Non-null assertions (`!`) — nine `order()!...` in one template. | Use `@if (order(); as o)` in templates; null-check in code. (Will be surfaced by enabling strict mode, F-ARC-3.) |
| F-SMELL-3 | 🟠 | `services/product.service.ts:98-100`, `services/cart.service.ts:104-110`, `models/product.model.ts:7` (`image` — no `<img>` exists anywhere in the app), `pages/checkout/checkout.component.ts:177-178` | Dead code (verified): `getProductById()`, `clearCart()`, `Product.image` (products never render images), card/exp signals collected but never used. | Delete or wire up. Deciding on `Product.image` also resolves the missing-product-images UX gap. |
| F-SMELL-4 | 🟡 | `chatbot.component.ts:276,296,397,22`, `cart.service.ts:99`, `notification.service.ts:21`, `chatbot.service.ts:40`, `terminal-card.component.ts:15` + `product-detail.component.ts:27` | Magic numbers/strings: `'[error]'` protocol, typewriter 10ms, debounce 1000ms, notification TTL 3000ms, greeting delay 600ms, spinner 150ms, low-stock threshold `stock < 10` duplicated in two files. | Extract constants/config; type the error flag (see F-UX-9); share the low-stock constant. |
| F-SMELL-5 | 🟡 | `cart.component.ts:120-137` vs `product-detail.component.ts:190-208` (`.qty-btn`), `terminal-card.component.ts:122-127` vs `product-detail.component.ts:132-135` (`.star`), `products.component.ts:117-137` vs `terminal-pagination.component.ts:40-61` (buttons), `chatbot.component.ts:55-76,187-258` vs `TerminalInputComponent` | Duplicated template/style logic; chatbot rebuilds its own terminal input instead of reusing the shared component (contradicts AGENTS.md §10.8). | Extract shared SCSS mixins/partial; reuse `TerminalInputComponent` in chatbot. |
| F-SMELL-6 | 🟡 | `app.ts` / class `App` (vs `*.component.ts` + `XxxComponent` everywhere), `components/notification/` (missing `terminal-` prefix), `cart.service.ts:112-127` (async/await among subscribe-based services), `product.service.ts:29` (`filters` plain field among signals), `auth.service.ts` (direct localStorage reads at 83,97,102-104 vs `getAccessToken()` abstraction) | Inconsistent naming/patterns. | Pick one convention per category and align. |
| F-SMELL-7 | 🟡 | `frontend/tsconfig.app.tsbuildinfo` (tracked in git, not in `.gitignore`) | Committed build artifact. | Add `*.tsbuildinfo` to `.gitignore`, `git rm --cached`. |
| F-SMELL-8 | 🟡 | `terminal-button.component.ts:9` | Redundant double class binding: static `class="t-btn"` + `[class]="'t-btn t-btn--' + variant()"`. | Keep only the dynamic binding. |

### Performance

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| F-PERF-1 | 🟠 | All 18 components (grep-verified) | **Zero `OnPush` change detection** — the chatbot's 10ms typewriter tick triggers app-wide CD. | Add `changeDetection: ChangeDetectionStrategy.OnPush` everywhere (signals make this safe here). |
| F-PERF-2 | 🟡 | `chatbot.component.ts:434-442` | Typewriter copies and replaces the whole messages array every 10ms per character. | Accumulate locally, flush per frame (`requestAnimationFrame`) or per word. |
| F-PERF-3 | 🟡 | `chatbot.component.ts` / `product-detail.component.ts` inline styles (~3.5-4 KB each) | Brushing the `anyComponentStyle` 4 kB warning budget in angular.json. | Extract to SCSS partials or raise/keep budget consciously. |

> Verified clean: all `@for` loops have `track`; all routes lazy-loaded via `loadComponent`; pagination logic correct (clamped, server-driven correction).

### Configuration

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| F-CFG-1 | 🟠 | `package.json:10` (`"typecheck": "tsc --noEmit"`) vs `tsconfig.json:22` (`"files": []`) | **CI typecheck is likely a no-op**: solution-style tsconfig has `files: []`, so nothing is type-checked (references need `tsc -b`). | Change to `tsc -b --noEmit` or run against `tsconfig.app.json`. |
| F-CFG-2 | 🟠 | `package.json:29` | CLI version skew: `@angular/cli ^21.0.4` while all runtime deps are `^22.0.0`. | Align CLI to v22. |
| F-CFG-3 | 🟡 | `tsconfig.json:12` | `experimentalDecorators: true` — legacy, not needed by Angular 22. | Remove. |
| F-CFG-4 | 🟡 | `sonar-project.properties:3-6` | `sonar.sources=src` without excluding `*.spec.ts` while also declaring test inclusions → specs double-counted as source + test (metrics skewed). | Add `sonar.exclusions=**/*.spec.ts` (or scope `sonar.sources`). |

### Testing

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| F-TEST-1 | 🟠 | (missing) `order.service.spec.ts`, `rate-limit.interceptor.spec.ts`, `utils` spec | Untested: order service, the 429 notification path, error-extraction util. | Add specs. |
| F-TEST-2 | 🟠 | `auth.interceptor.spec.ts:15-27` | Module-level interceptor state (`isRefreshing`, `refreshTokenSubject`) **never reset between tests** → order-dependent suite; the refresh-failure hang (F-ARC-2) is untested and invisible. | Reset state in `beforeEach`; add tests for refresh failure with queued requests and sync-throw case. |
| F-TEST-3 | 🟡 | `cart.service.spec.ts:23-24,119,127,130`, `chatbot.component.spec.ts:345`, `login.component.spec.ts:55-57`, `register.component.spec.ts:90`, `terminal-titlebar.component.spec.ts:49-75`, `checkout.component.spec.ts:97,163`, `home.component.spec.ts:53`, `auth.guard.spec.ts:32,54`, `chatbot.service.spec.ts:63` | Brittle private-state access via `as any` / bracket access. | Expose testable APIs or use TestBed-level signals. |
| F-TEST-4 | 🟡 | `cart.service.spec.ts:106-111`, `products.component.spec.ts:158-168`, `product-detail.component.spec.ts:234-249` (name says 5, asserts 6), `checkout.component.spec.ts:156-171`, `chatbot.component.spec.ts:207-212` | Tests that don't assert what their names claim / never flush HTTP. | Fix assertions or rename. |
| F-TEST-5 | 🟡 | `README.md:47-55` vs repo | README documents `ng e2e`, but no e2e tooling exists. | Add e2e (Playwright/Cypress) or drop the doc claim. |

### Accessibility (brief)

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| F-A11Y-1 | 🟠 | `notification.component.ts:10-19`, `terminal-titlebar.component.ts:20`, `chatbot.component.ts:20,60-74,244` | Zero ARIA in the whole app (grep-verified): toasts not `aria-live`, logout is an `<a>` without `href` (not keyboard-operable — use `<button>`), chat log has no `role="log"`, input has no label, fake cursor hides the real caret. | Add roles/labels/keyboard handlers; use `<button>` for logout. |
| F-A11Y-2 | 🟡 | `terminal-input.component.ts:10`, `pages/auth/register/register.component.ts:27-48` | `<label [for]="id()">` with `id` defaulting to `''` → register labels never associate (login passes ids, register doesn't). | Auto-generate a unique id when not supplied. |
| F-A11Y-3 | 🟡 | `app.routes.ts` | No route `title`s → document title never changes from "TERM-SHOP // terminal commerce". | Add `title` per route. |

---

## Cross-cutting / repo-level

| # | Sev | Files | Issue | Fix |
|---|-----|-------|-------|-----|
| X-1 | 🔴 | `frontend/Dockerfile`, `backend/Dockerfile` | Both containers run as root; frontend ships a dev server to "production". | See B-DOCKER-1 / F-ARC-4. |
| X-2 | 🟠 | `docker-compose.yml` | Frontend container mounts source for hot-reload in the compose stack used as "the" deployment; frontend calls the backend at `localhost:8080` from the *browser* — breaks outside localhost. | Decide dev vs prod compose files; with relative `/api` paths (F-ARC-1), add a reverse-proxy (nginx/Caddy) routing `/api` → backend. |
| X-3 | 🟠 | Cart API contract (frontend `cart.service.ts:118-121` vs backend `CartController`) | Frontend emulates quantity updates with DELETE+POST because the backend lacks `PATCH`; this is the root cause of F-UX-2. | Add `PATCH /api/cart/{cartItemId}` with quantity validation on the backend; simplify the frontend to one atomic call. |
| X-4 | 🟡 | `README.md`, `frontend/AGENTS.md` conventions vs code | Minor drift: `ng e2e` documented but absent; AGENTS.md package-root wrong (B-ARC-8); component naming conventions not universally followed (F-SMELL-6). | Update docs to match reality. |

---

## Suggested fix order (top 15 by impact)

1. **B-SEC-1** — remove default JWT secret (fail fast). *(2-min fix, critical)*
2. **B-CC-1** — atomic conditional stock decrement (oversell).
3. **B-SEC-2** — order ownership check (IDOR).
4. **F-ARC-3** — enable `strict` + `strictTemplates` (surfaces several bugs below).
5. **F-ARC-1** — prod environment / relative API base (site is broken off-localhost).
6. **F-ARC-2** — fix interceptor refresh deadlocks + reset `isRefreshing` via `finalize`.
7. **B-CC-2** — UNIQUE(user_id, product_id) + atomic cart upsert.
8. **B-SEC-4** — cart quantity validation (negative-quantity stock exploit).
9. **B-SEC-3 + B-SEC-5** — hash refresh tokens + atomic rotation.
10. **B-API-1 / B-ARC-2** — unified error contract + missing MVC exception handlers.
11. **F-ARC-4 / B-DOCKER-1** — real production Docker images (nginx + JRE, non-root).
12. **F-ARC-6 + F-ARC-7** — stop bootstrap HTTP storm; add loading/error states.
13. **B-SEC-6** — bound the rate limiter; **B-PERF-1** — cap chat search; **B-PERF-3** — HTTP timeouts.
14. **B-SEC-14 / B-CFG-2** — prod profile hardening (swagger off, show-sql off).
15. **B-TEST-1 / F-TEST-2** — tests for the critical paths that currently have none.
