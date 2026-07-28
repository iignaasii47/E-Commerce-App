# E-Commerce App

A full-stack e-commerce platform with an AI-powered shopping assistant. Built with **Spring Boot 4** (Java 26) on the backend and **Angular 22** (TypeScript 6) on the frontend, following clean architecture principles throughout.

---

## Highlights

- **Hexagonal Architecture** (Ports & Adapters) on the backend with strict dependency inversion
- **Standalone Angular components** with signals-based state management (no NgRx)
- **AI Chatbot** with agentic tool-calling loop backed by OpenRouter LLMs
- **JWT access/refresh token authentication** with stateless security
- **CI/CD** — GitHub Actions workflows with automated build, test, and SonarQube analysis
- **SonarQube integration** for static analysis on both projects

---

## Tech Stack

### Backend — `backend/`

| Layer | Technology |
|---|---|
| Language | Java 26 |
| Framework | Spring Boot 4.1.0 |
| Build | Maven 3.9.16 (wrapper) |
| Database | PostgreSQL |
| Migrations | Flyway |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| AI Integration | OpenRouter API (Llama 3.3 70B + fallback models) |
| Logging | Logback + Logstash JSON encoder |
| Testing | JUnit 5, Mockito, AssertJ, H2 (test DB) |
| Coverage | JaCoCo 0.8.15 |
| Quality | SonarQube |

### Frontend — `frontend/`

| Layer | Technology |
|---|---|
| Language | TypeScript ~6.0 |
| Framework | Angular 22 (standalone components) |
| State | Angular Signals (signal, computed, effect) |
| Routing | @angular/router with view transitions |
| Forms | Reactive & template-driven forms |
| Styling | SCSS with CSS custom properties |
| Font | JetBrains Mono |
| Testing | Vitest 4.0 + jsdom |
| Coverage | @vitest/coverage-v8 |
| Formatting | Prettier |
| Quality | SonarQube |

---

## Architecture

### Backend — Hexagonal (Ports & Adapters)

```
Controller (inbound adapter)
    └── Inbound Port (interface)
            └── Application Service (use-case)
                    └── Domain (model, exceptions, port/out interfaces)
                            └── Infrastructure (persistence, security, clients)
```

- **Controllers** handle HTTP concerns only — no business logic
- **Application Services** orchestrate use cases within `@Transactional` boundaries
- **Domain** is framework-agnostic — no Spring or JPA annotations
- **Infrastructure** implements outbound ports (JPA repositories, JWT, external APIs)
- Dependency arrows point strictly inward — domain has zero framework dependencies

### Frontend — Standalone Component Architecture

```
src/app/
├── components/       # Shared & layout components (Terminal UI kit)
├── pages/            # Route-level page components (lazy-loaded)
├── services/         # Injectable services with signal-based state
├── models/           # TypeScript interfaces
├── app.routes.ts     # Route definitions with auth guards
└── app.config.ts     # App bootstrap configuration
```

- **100% standalone components** — no NgModules
- **Signal-based state** in services (`signal()`, `computed()`, `effect()`)
- **Lazy-loaded routes** via `loadComponent()` on every page
- **Functional guards and interceptors** (`authGuard`, `authInterceptor`)
- **New Angular control flow** (`@if`, `@for`, `@switch`)

---

## Features

### Product Catalog
- Browse, search, and filter products by category
- Product detail pages with image serving (binary + external redirect fallback)
- Seed data: 51 products across 5 categories pre-loaded via Flyway

### Shopping Cart
- Add/remove products, quantity management
- Optimistic UI updates with debounced API sync
- JWT-protected per-user cart persistence

### Authentication
- User registration and login with BCrypt password hashing
- Stateless JWT access/refresh token authentication (15-minute access / 7-day refresh tokens)
- Transparent token refresh on 401, auto-logout with redirect to login if refresh fails

### AI Shopping Assistant
- Conversational chatbot powered by OpenRouter (free LLM models)
- **Agentic tool-calling loop** (max 5 iterations) — the AI can:
  - `search_products`, `get_product`, `list_categories`
  - `add_to_cart`, `remove_from_cart`, `view_cart`
- 10 model fallback chain with sticky fallback on failure
- CV data provider for developer Q&A
- Typewriter animation, command history, and terminal-style UI

### API Documentation
- Swagger UI at `/docs` with JWT Bearer auth scheme
- Full OpenAPI 3 spec at `/v3/api-docs`
- Annotated with `@Tag`, `@Operation`, `@ApiResponse` across all endpoints

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/status` | Public | Health check (API + DB) |
| `GET` | `/api/products` | Public | List/search products |
| `GET` | `/api/products/{id}` | Public | Get product by ID |
| `GET` | `/api/products/categories` | Public | List categories |
| `GET` | `/api/products/{id}/image` | Public | Get product image |
| `POST` | `/api/users` | Public | Register |
| `POST` | `/api/users/login` | Public | Login (returns JWT) |
| `POST` | `/api/auth/refresh` | Public | Refresh access token |
| `POST` | `/api/auth/logout` | Public | Logout (invalidates refresh token) |
| `GET` | `/api/cart` | Authenticated | View cart |
| `POST` | `/api/cart` | Authenticated | Add to cart |
| `DELETE` | `/api/cart/{cartItemId}` | Authenticated | Remove from cart |
| `POST` | `/api/orders` | Authenticated | Place an order |
| `GET` | `/api/orders/{orderId}` | Authenticated | Get order by ID |
| `POST` | `/api/chat` | Authenticated | Chat with AI assistant |

---

## CI/CD (GitHub Actions)

CI workflows run on a self-hosted runner. Both pipelines build, test, and run a SonarQube quality gate analysis.

| Workflow | Triggers | Steps |
|---|---|---|
| **backend-ci.yml** | Push/PR to `main`, `backend/**` paths | Maven verify → SonarQube scan (with OWASP dep check) |
| **frontend-ci.yml** | Push/PR to `main`, `frontend/**` paths | Typecheck → Vitest with coverage → Build → SonarQube scan |
| **renovate.yml** | Weekly (Mon 9am) + manual | Automated dependency updates via Renovate |

Secrets required: `SONAR_TOKEN`, `NVD_API_KEY`, `RENOVATE_TOKEN`.

SonarQube dashboards:
- Backend: `http://localhost:9000/dashboard?id=ecommerce-api`
- Frontend: `http://localhost:9000/dashboard?id=ecommerce-web`

---

## Testing

### Backend (60+ test classes)

| Layer | Approach |
|---|---|
| Controller | MockMvc integration tests |
| Application Service | Mockito unit tests |
| Domain | Pure JUnit 5 + AssertJ |
| Infrastructure | JPA tests with H2 (PostgreSQL mode), JWT tests |
| Persistence | Spring Data JPA Test + repository integration |

### Frontend

| Layer | Approach |
|---|---|
| Services | Vitest unit tests with jsdom |
| Components | Vitest unit tests |
| Coverage | @vitest/coverage-v8 (lcov reports) |

---

## Running with Docker (Recommended)

No need to install Java, Node, or PostgreSQL — everything runs in containers.

### Prerequisites
- Docker
- Docker Compose

### 1. Configure Environment Variables

Copy `.env.template` to `.env` and fill in the required values:

```bash
cp .env.template .env
```

| Variable | Required | Default | Description |
|---|---|---|---|
| `JWT_SECRET` | **Yes** | — | JWT signing secret (at least 256 bits) |
| `OPENROUTER_API_KEY` | **Yes** | — | OpenRouter API key for AI chatbot |
| `DB_USERNAME` | No | `postgres` | PostgreSQL user |
| `DB_PASSWORD` | No | `postgres` | PostgreSQL password |
| `JWT_ACCESS_TOKEN_EXPIRATION_MS` | No | `900000` | Access token lifetime (ms) |
| `JWT_REFRESH_TOKEN_EXPIRATION_MS` | No | `604800000` | Refresh token lifetime (ms) |

### 2. Start Everything

```bash
# Option A — helper script (auto-starts Docker daemon if needed):
./serve.sh

# Option B — directly:
docker compose up --build
```

This starts three containers:
- **PostgreSQL 16** on port `5432` (data persisted via `pgdata` volume)
- **Spring Boot backend** on port `8080` (waits for DB healthcheck)
- **Angular frontend** on port `4200` (hot-reload via volume mount)

### 3. Access

| Service | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080 |
| Swagger Docs | http://localhost:8080/docs |

### Services

| Container | Port | Healthcheck | Restart |
|---|---|---|---|
| `postgres` | 5432 | `pg_isready` every 5s | `unless-stopped` |
| `backend` | 8080 | depends on postgres healthy | `unless-stopped` |
| `frontend` | 4200 | depends on backend | `unless-stopped` |

---

## Running Locally (Without Docker)

### Prerequisites
- Java 26+
- Node.js 22+
- PostgreSQL

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
ng serve
```

### All-in-One (Windows)

```powershell
./serve.ps1
```

Starts PostgreSQL, backend (port 8080), and frontend (port 4200) in parallel.

---

## Project Structure

```
E-Commerce-App/
├── backend/                          # Spring Boot API
│   ├── src/main/java/.../
│   │   ├── controller/               # REST endpoints + DTOs
│   │   ├── application/              # Use-case interfaces + services
│   │   ├── domain/                   # Models, exceptions, outbound ports
│   │   └── infrastructure/           # JPA, security, clients, config
│   └── src/test/                     # 60+ test classes
├── frontend/                         # Angular SPA
│   └── src/app/
│       ├── components/               # Shared UI components (layout, shared, notification)
│       ├── pages/                    # Route page components (lazy-loaded)
│       │   ├── auth/                 # Login, register
│       │   ├── cart/
│       │   ├── chatbot/
│       │   ├── checkout/
│       │   ├── home/
│       │   ├── not-found/
│       │   ├── order-confirmation/
│       │   └── products/             # Product list, product detail
│       ├── services/                 # Injectable services
│       └── models/                   # TypeScript interfaces
├── docker-compose.yml                # Docker Compose orchestration
├── .env.template                     # Environment variable template
├── serve.sh                          # Docker startup script (Linux/Mac)
├── serve.ps1                         # Native startup script (Windows)
└── run-sonarqube.ps1                 # SonarQube analysis script
```
