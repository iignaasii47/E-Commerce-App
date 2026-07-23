# Sprint — Term-Shop E-Commerce Platform

> **Sprint Goal:** Expand platform functionality, harden security, and improve API design to demonstrate professional full-stack development workflows.
> **Branching:** One branch per task (`feature/DEV-XX-...` or `fix/FIX-XX-...`), PR to `main`, CI pipeline must pass before merge.

---

## New Developments

---

### (DONE) DEV-01: Enforce Password Strength Policy

**As a** platform administrator,
**I want** password complexity rules enforced at registration and password changes,
**so that** user accounts are protected against weak passwords.

**Requirements:**

- Reject passwords shorter than 8 characters.
- Reject passwords that are purely numeric or purely alphabetic.
- Make the policy configurable via environment variables (`PASSWORD_MIN_LENGTH`, `PASSWORD_REQUIRE_MIXED`).

**Acceptance Criteria:**

- [ ] Registration with password `12345678` returns `400 Bad Request` with descriptive error message.
- [ ] Registration with password `abcdefgh` returns `400 Bad Request`.
- [ ] Registration with password `Pass1234!` returns `201 Created`.
- [ ] Unit tests cover all validation paths in `UserRegistrationService`.
- [ ] Policy flags are read from `application.properties` / `.env`.

**Branch:** `feature/DEV-01-password-strength-policy`

---

### (DONE) DEV-02: Rate Limiting on Auth & Chat Endpoints

**As a** platform administrator,
**I want** rate limiting on login, registration, token refresh, and chat endpoints,
**so that** the API is protected against brute-force attacks and abuse.

**Requirements:**

- Login (`POST /api/users/login`): max 5 requests per minute per IP.
- Registration (`POST /api/users`): max 3 requests per minute per IP.
- Token refresh (`POST /api/auth/refresh`): max 10 requests per minute per IP.
- Chat (`POST /api/chat`): max 30 requests per minute per authenticated user.
- Exceeded limits return `429 Too Many Requests` with `Retry-After` header.

**Acceptance Criteria:**

- [ ] 6th login attempt within 1 minute returns `429`.
- [ ] Rate limiter uses in-memory storage (no Redis dependency).
- [ ] Limits are configurable via environment variables.
- [ ] Integration test verifies rate limit headers and 429 response.

**Branch:** `feature/DEV-02-rate-limiting`

---

### DEV-03: Paginated Product Listing with Sorting

**As a** customer,
**I want** to browse products with pagination and sorting options,
**so that** I can efficiently navigate a large product catalog.

**Requirements:**

- `GET /api/products` accepts query params: `page` (default 0), `size` (default 10, max 50), `sort` (e.g., `price,asc` or `name,desc`).
- Response includes: `content`, `totalElements`, `totalPages`, `currentPage`, `pageSize`.
- Existing `search` and `category` filters must still work with pagination.
- Frontend product list component must render page navigation controls.

**Acceptance Criteria:**

- [ ] `GET /api/products?page=1&size=5` returns second page with 5 items and correct pagination metadata.
- [ ] `GET /api/products?sort=price,desc` returns products sorted by price descending.
- [ ] Sorting works combined with `search` and `category` filters.
- [ ] Frontend shows "Page X of Y" with Previous/Next buttons.
- [ ] Response body follows Spring Data's `Page<T>` JSON structure.

**Branch:** `feature/DEV-03-paginated-products`

---

### DEV-04: Order History Endpoint

**As a** customer,
**I want** to view all my past orders,
**so that** I can track my purchase history.

**Requirements:**

- `GET /api/orders` returns paginated list of orders for the authenticated user.
- Each order summary includes: `id`, `total`, `status`, `createdAt`, `itemCount`.
- Supports pagination params: `page`, `size`, `sort`.
- Orders returned sorted by `createdAt` descending by default.

**Acceptance Criteria:**

- [ ] Authenticated user with 3 orders receives all 3 in the response.
- [ ] User A cannot see User B's orders (returns only the caller's orders).
- [ ] Empty result when user has no orders returns `200` with empty `content` array.
- [ ] New `OrderRepository.findByUserId` method with pageable support.
- [ ] Unit and integration tests cover authorization filtering and pagination.

**Branch:** `feature/DEV-04-order-history`

---

### DEV-05: User Profile Management

**As a** registered user,
**I want** to update my profile information (email, username, password),
**so that** I can manage my account details.

**Requirements:**

- `PATCH /api/users/me` updates email and/or username (body: `{ "email": "...", "username": "..." }`).
- `PATCH /api/users/me/password` changes password (body: `{ "currentPassword": "...", "newPassword": "..." }`).
- Email and username uniqueness validated on update (must not conflict with another user).
- Password change requires current password verification.

**Acceptance Criteria:**

- [ ] `PATCH /api/users/me` with new email returns `200` and updated user DTO.
- [ ] Attempting to use an email already taken by another user returns `409 Conflict`.
- [ ] Password change with incorrect current password returns `401 Unauthorized`.
- [ ] Password change with correct current password updates the password and returns `200`.
- [ ] Unauthenticated requests to `/api/users/me` return `401`.

**Branch:** `feature/DEV-05-user-profile-management`

---

### DEV-06: Admin Role & Product CRUD

**As a** platform administrator,
**I want** to manage products via the API,
**so that** I can maintain the catalog without direct database access.

**Requirements:**

- Add `ROLE_USER` and `ROLE_ADMIN` roles to the `User` domain model and database.
- Seed one admin user (`admin / admin123`) in `R__seed_data.sql`.
- JWT claims include user role; `SecurityContextProvider` exposes role.
- `POST /api/products` — create product (admin only).
- `PUT /api/products/{id}` — update product (admin only).
- `DELETE /api/products/{id}` — delete product (admin only).
- Non-admin users receive `403 Forbidden`.

**Acceptance Criteria:**

- [ ] Admin JWT can create a product via `POST /api/products`, returns `201`.
- [ ] Regular user JWT attempting `POST /api/products` returns `403`.
- [ ] Product update preserves fields not included in the request body (partial update).
- [ ] `@PreAuthorize("hasRole('ADMIN')")` applied to controller methods.
- [ ] Flyway migration adds `role` column to `users` table with default `USER`.
- [ ] `CreateProductRequest` and `UpdateProductRequest` DTOs with validation.

**Branch:** `feature/DEV-06-admin-product-crud`

---

### DEV-07: Batch Cart Clear Endpoint

**As a** customer,
**I want** to clear my entire cart with a single request,
**so that** I don't need to remove items one by one.

**Requirements:**

- `DELETE /api/cart` removes all items from the authenticated user's cart.
- Returns `204 No Content` on success.
- Must be transactional — all items removed atomically.

**Acceptance Criteria:**

- [ ] Cart with 5 items: `DELETE /api/cart` returns `204`, subsequent `GET /api/cart` returns empty.
- [ ] Empty cart: `DELETE /api/cart` returns `204` (idempotent, no error).
- [ ] Frontend calls batch endpoint instead of looping individual DELETEs in `clearCart()`.
- [ ] `CartRepository.deleteByUserId` method added.
- [ ] Test verifies idempotent behavior on empty and non-empty carts.

**Branch:** `feature/DEV-07-batch-cart-clear`

---

### DEV-08: Mock Payment Processing

**As a** customer,
**I want** a simulated payment step during checkout,
**so that** the order flow feels complete and realistic.

**Requirements:**

- `POST /api/orders/{id}/pay` simulates payment processing for a pending order.
- Validates the order belongs to the authenticated user and status is `PENDING`.
- Randomly succeeds (~90% success rate by default, configurable).
- Successful payment transitions order to `PAID`.
- Failed payment returns `402 Payment Required` with a reason message.
- New `OrderStatus` value: `PAID`.

**Acceptance Criteria:**

- [ ] Paying your own pending order returns `200` with updated order status `PAID`.
- [ ] Paying another user's order returns `403`.
- [ ] Paying an already paid order returns `409 Conflict`.
- [ ] Integration test uses deterministic mock to test both success and failure paths.
- [ ] Flyway migration updates `orders.status` enum to include `PAID`.
- [ ] Success rate configurable via `PAYMENT_SUCCESS_RATE` env var.

**Branch:** `feature/DEV-08-mock-payment`

---

### DEV-09: Product Reviews

**As a** customer,
**I want** to submit reviews and ratings for products I've purchased,
**so that** I can share my experience with other shoppers.

**Requirements:**

- `POST /api/products/{id}/reviews` — submit review (title, comment, rating 1-5).
- User must have at least one completed order containing the product.
- One review per user per product (duplicate returns `409`).
- `GET /api/products/{id}/reviews` — list reviews for a product (paginated).
- Product `rating` field is recalculated as the average of all reviews.

**Acceptance Criteria:**

- [ ] User who bought product submits review: `201 Created`.
- [ ] User who never bought product submits review: `403 Forbidden`.
- [ ] Duplicate review attempt: `409 Conflict` with descriptive message.
- [ ] Rating outside 1-5 range returns `400 Bad Request` (Bean Validation).
- [ ] New `reviews` table with Flyway migration, domain model, JPA entity, repository.
- [ ] Product average rating updates after each new review.

**Branch:** `feature/DEV-09-product-reviews`

---

### DEV-10: Request Correlation IDs & Structured Logging

**As a** developer,
**I want** every HTTP request to carry a correlation ID propagated through all layers,
**so that** I can trace requests across logs for debugging.

**Requirements:**

- `X-Correlation-ID` header read from incoming requests; generated UUID if absent.
- Correlation ID added to all log statements via MDC (Mapped Diagnostic Context).
- Correlation ID returned in all API responses as `X-Correlation-ID` header.
- `LogstashEncoder` configured to include `correlation_id` in JSON logs.
- Frontend `authInterceptor` propagates correlation ID or generates one per session.

**Acceptance Criteria:**

- [ ] Request without `X-Correlation-ID`: response includes a generated UUID in the header.
- [ ] Request with `X-Correlation-ID`: same value returned in response header.
- [ ] All log lines for a given request share the same `correlation_id` field in JSON output.
- [ ] `OncePerRequestFilter` handles ID extraction, generation, MDC set/clear.
- [ ] Unit test verifies filter behavior with and without header present.
- [ ] Filter implements `Ordered` to ensure it runs first.

**Branch:** `feature/DEV-10-correlation-ids`

---

## Bug Fixes & Improvements

---

### FIX-01: Change addToCart from Query Params to Request Body

**Priority:** High | **Type:** API Design Fix

**As a** frontend developer,
**I want** the `POST /api/cart` endpoint to accept a JSON request body instead of query parameters,
**so that** the API follows REST conventions and supports complex payloads.

**Requirements:**

- Accept `{ "productId": 5, "quantity": 2 }` as JSON body.
- Remove `@RequestParam productId` and `@RequestParam quantity` from controller.
- Create an `AddToCartRequest` DTO with `@NotNull` and `@Min(1)` validations.

**Acceptance Criteria:**

- [ ] `POST /api/cart` with JSON body `{"productId": 1, "quantity": 3}` returns `200 OK`.
- [ ] Missing `productId` returns `400 Bad Request` with field error.
- [ ] Quantity 0 or negative returns `400 Bad Request`.
- [ ] Frontend `CartService.addToCart()` sends JSON body instead of query params.
- [ ] Backward compatibility not required (frontend and backend deployed together).

**Branch:** `fix/FIX-01-cart-request-body`

---

### FIX-02: Add PATCH Endpoint for Cart Item Quantity

**Priority:** High | **Type:** Improvement

**As a** customer,
**I want** to update the quantity of a cart item directly,
**so that** the frontend doesn't need to delete and re-add items to change quantity.

**Requirements:**

- `PATCH /api/cart/{cartItemId}` with body `{ "quantity": 5 }`.
- Validates the item belongs to the authenticated user.
- Returns updated `CartItem` DTO.

**Acceptance Criteria:**

- [ ] PATCH with quantity 3 on existing item updates quantity and returns `200` with updated DTO.
- [ ] PATCH on non-existent item returns `404`.
- [ ] PATCH on another user's item returns `403`.
- [ ] Frontend `CartService.syncQuantity()` uses PATCH instead of delete + re-add.
- [ ] `CartRepository.updateQuantity(cartItemId, quantity)` method added.

**Branch:** `fix/FIX-02-cart-patch-quantity`

---

### FIX-03: Fix Stock Race Condition in Order Placement

**Priority:** Critical | **Type:** Bug Fix

**As a** platform operator,
**I want** inventory to never go negative when concurrent orders are placed,
**so that** we never sell more stock than available.

**Requirements:**

- Use `SELECT ... FOR UPDATE` pessimistic lock when reading product stock during order placement.
- Alternatively, use `@Version` optimistic locking on `ProductEntity` with retry.
- `OrderUseCaseService.placeOrder()` must use `@Transactional` with proper isolation.
- If stock is insufficient at commit time, transaction rolls back and user gets `409 Conflict`.

**Acceptance Criteria:**

- [ ] Concurrent requests for the last unit of stock: exactly one succeeds, the other gets `409`.
- [ ] Integration test spawns 2 threads placing orders for the same product simultaneously.
- [ ] Stock never goes below 0 in the database after concurrent test.
- [ ] `ProductRepository.findByIdForUpdate(Long id)` added with `@Lock(PESSIMISTIC_WRITE)`.
- [ ] `ProductEntity` has `@Version` field for optimistic locking.

**Branch:** `fix/FIX-03-stock-race-condition`

---

### FIX-04: Sanitize .env.template to Remove Real Secrets

**Priority:** High | **Type:** Security Fix

**As a** developer onboarding to the project,
**I want** the `.env.template` to contain only placeholder values,
**so that** no real secrets are accidentally exposed or committed.

**Requirements:**

- Replace all real-looking keys/tokens in `.env.template` with `<your-key-here>` placeholders.
- Ensure `.env` is listed in `.gitignore` and is NOT tracked by git.
- Add a CI pipeline step that scans for potential secrets in committed files.

**Acceptance Criteria:**

- [ ] `.env.template` contains no real API keys, tokens, or passwords.
- [ ] `git ls-files` does not show `.env` in the tracked files.
- [ ] CI pipeline includes a secret scanning step (trufflehog, gitleaks, or simple grep).
- [ ] `.env.template` has clear comments explaining where to obtain each key.

**Branch:** `fix/FIX-04-sanitize-env-template`

---

### FIX-05: Standardize Error Response Format Across All Exceptions

**Priority:** Medium | **Type:** Improvement

**As a** frontend developer,
**I want** all API error responses to follow a consistent structure,
**so that** I can write a single error-handling utility on the client.

**Requirements:**

- All error responses must use: `{ "error": "ERROR_CODE", "message": "Human-readable message", "timestamp": "ISO-8601", "path": "/api/..." }`.
- Define an `ErrorCode` enum: `PRODUCT_NOT_FOUND`, `DUPLICATE_USER`, `INVALID_CREDENTIALS`, `INSUFFICIENT_STOCK`, `CART_ITEM_NOT_FOUND`, `EMPTY_CART`, `VALIDATION_ERROR`, `AI_SERVICE_ERROR`, `GENERAL_ERROR`.
- `GlobalExceptionHandler` maps all domain exceptions to this format.
- Validation errors include a `fieldErrors` array.

**Acceptance Criteria:**

- [ ] `GET /api/products/999` returns `{ "error": "PRODUCT_NOT_FOUND", "message": "...", "timestamp": "...", "path": "/api/products/999" }`.
- [ ] `POST /api/users` with missing fields returns `{ "error": "VALIDATION_ERROR", "fieldErrors": [...] }`.
- [ ] All existing exception handler tests are updated to assert the new format.
- [ ] Frontend `NotificationService` parses the standardized error format.

**Branch:** `fix/FIX-05-standardize-error-responses`

---

### FIX-06: Add Email Format Validation on Registration

**Priority:** Medium | **Type:** Bug Fix

**As a** platform administrator,
**I want** email addresses validated for correct format during registration,
**so that** invalid emails don't get stored in the database.

**Requirements:**

- Add `@Email` annotation to `email` field in `CreateUserRequest` DTO.
- Ensure domain layer also validates email format in `UserRegistrationService`.
- Backend returns `400` with field error for invalid email.

**Acceptance Criteria:**

- [ ] Registration with `notanemail` returns `400` with error: `must be a well-formed email address`.
- [ ] Registration with `user@domain.com` returns `201`.
- [ ] Frontend registration form shows inline validation error for invalid email format.
- [ ] Unit tests cover valid and invalid email scenarios.

**Branch:** `fix/FIX-06-email-validation`

---

### FIX-07: Prevent Duplicate Products in Cart

**Priority:** Medium | **Type:** Bug Fix

**As a** customer,
**I want** adding a product already in my cart to increment its quantity,
**so that** I don't end up with duplicate line items for the same product.

**Requirements:**

- When adding a product that already exists in the user's cart, increment the quantity instead of inserting a new row.
- `POST /api/cart` with `{ "productId": 1, "quantity": 2 }` when productId 1 already has quantity 3 results in quantity 5.
- Response includes the updated item with combined quantity.
- Database unique constraint on `(user_id, product_id)` in `cart_items` table via Flyway migration.

**Acceptance Criteria:**

- [ ] Adding product A (qty 2), then adding product A again (qty 3): cart shows one item with quantity 5.
- [ ] Adding product A (qty 2), then product B (qty 1): cart shows two distinct items.
- [ ] Unique constraint prevents duplicate inserts at the database level.
- [ ] Race condition handled with `ON CONFLICT` or merge logic in repository.
- [ ] Flyway migration adds unique constraint on `(user_id, product_id)`.

**Branch:** `fix/FIX-07-prevent-duplicate-cart-items`

---

### FIX-08: Add Timeout & Retry for OpenRouter AI Client

**Priority:** Medium | **Type:** Improvement

**As a** customer using the AI chatbot,
**I want** reasonable timeout handling when the AI service is slow,
**so that** I don't wait indefinitely for a response.

**Requirements:**

- Set configurable connection timeout (default 10s) and read timeout (default 30s) on the `RestClient`.
- After timeout, try up to 2 fallback models (total 3 attempts max including primary).
- If all models fail, return `503 Service Unavailable` with user-friendly message.
- Log each model attempt and failure for debugging.

**Acceptance Criteria:**

- [ ] When OpenRouter primary model hangs: after timeout, a fallback model is attempted.
- [ ] All 3 attempts fail: returns `503 Service Unavailable` with message: "The AI assistant is currently unavailable. Please try again later."
- [ ] Timeout values configurable via `OPENROUTER_CONNECT_TIMEOUT_MS` and `OPENROUTER_READ_TIMEOUT_MS`.
- [ ] Unit test mocks `RestClient` with delayed responses to verify timeout + fallback behavior.
- [ ] Log output includes model name and failure reason for each attempt.

**Branch:** `fix/FIX-08-ai-timeout-retry`

---

### FIX-09: Add Input Sanitization for Product Search

**Priority:** Low | **Type:** Improvement

**As a** security-conscious developer,
**I want** to sanitize search input parameters to prevent injection attacks and ensure clean queries,
**so that** the API remains secure.

**Requirements:**

- Strip HTML/script tags from `search` and `category` query parameters.
- Trim leading/trailing whitespace.
- Limit search string length to 200 characters.
- Escape special LIKE characters (`%`, `_`) in JPA queries.

**Acceptance Criteria:**

- [ ] Search with `<script>alert(1)</script>` is sanitized before reaching the repository layer.
- [ ] Search string > 200 characters returns `400 Bad Request`.
- [ ] Search with `%` character searches for literal `%`, not SQL wildcard.
- [ ] Unit test for the sanitizer utility method covers all edge cases.
- [ ] Sanitization applied consistently to all string query parameters.

**Branch:** `fix/FIX-09-input-sanitization`

---

### FIX-10: Make CORS Allowed Origins Configurable via Environment

**Priority:** Low | **Type:** Improvement

**As a** DevOps engineer,
**I want** CORS allowed origins to be configurable via environment variables,
**so that** I can deploy to staging/production with different frontend URLs without rebuilding.

**Requirements:**

- `CorsConfig` reads `CORS_ALLOWED_ORIGINS` env var (comma-separated list).
- Default value: `http://localhost:4200`.
- Docker Compose passes the env var to the backend container.
- `.env.template` documents this variable.

**Acceptance Criteria:**

- [ ] Setting `CORS_ALLOWED_ORIGINS=http://localhost:4200,https://staging.example.com` allows both origins.
- [ ] Missing env var defaults to `http://localhost:4200`.
- [ ] Preflight `OPTIONS` requests from allowed origin return `200` with proper CORS headers.
- [ ] Requests from disallowed origin do not include CORS headers.
- [ ] Unit test verifies custom origins and default fallback.

**Branch:** `fix/FIX-10-configurable-cors`

---

## Sprint Workflow

All tasks follow this branching and review process:

```
main
  │
  ├── feature/DEV-01-password-strength-policy
  ├── feature/DEV-02-rate-limiting
  ├── feature/DEV-03-paginated-products
  ├── feature/DEV-04-order-history
  ├── feature/DEV-05-user-profile-management
  ├── feature/DEV-06-admin-product-crud
  ├── feature/DEV-07-batch-cart-clear
  ├── feature/DEV-08-mock-payment
  ├── feature/DEV-09-product-reviews
  ├── feature/DEV-10-correlation-ids
  ├── fix/FIX-01-cart-request-body
  ├── fix/FIX-02-cart-patch-quantity
  ├── fix/FIX-03-stock-race-condition
  ├── fix/FIX-04-sanitize-env-template
  ├── fix/FIX-05-standardize-error-responses
  ├── fix/FIX-06-email-validation
  ├── fix/FIX-07-prevent-duplicate-cart-items
  ├── fix/FIX-08-ai-timeout-retry
  ├── fix/FIX-09-input-sanitization
  └── fix/FIX-10-configurable-cors
```

**Per task:**

1. **Branch** from `main`: `feature/DEV-XX-short-description` or `fix/FIX-XX-short-description`
2. **Implement** backend (Java + tests) and/or frontend (TypeScript + tests)
3. **Verify** locally: `mvn verify` (backend) and `npm run ci:test` (frontend)
4. **Open PR** to `main` with description linking the user story
5. **CI checks** must pass: backend CI, frontend CI, OpenCode review
6. **Merge** (squash-merge to `main`, delete feature branch)
