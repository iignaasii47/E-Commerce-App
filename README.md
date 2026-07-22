# E-Commerce App

A full-stack e-commerce platform with an AI-powered shopping assistant. Built with **Spring Boot 4** (Java 26) on the backend and **Angular 22** (TypeScript 6) on the frontend, following clean architecture principles throughout.

---

## Highlights

- **Hexagonal Architecture** (Ports & Adapters) on the backend with strict dependency inversion
- **Standalone Angular components** with signals-based state management (no NgRx)
- **AI Chatbot** with agentic tool-calling loop backed by OpenRouter LLMs
- **JWT authentication** with stateless security and role-based access control
- **Pre-push hook** — Automated SonarQube scans and AI code review via OpenCode on every non-main push
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
- Seed data: 10 tech peripherals pre-loaded via Flyway

### Shopping Cart
- Add/remove products, quantity management
- Optimistic UI updates with debounced API sync
- JWT-protected per-user cart persistence

### Authentication
- User registration and login with BCrypt password hashing
- Stateless JWT Bearer token authentication (24h expiry)
- Auto-logout on 401 responses with redirect to login

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
| `GET` | `/api/cart` | Authenticated | View cart |
| `POST` | `/api/cart` | Authenticated | Add to cart |
| `DELETE` | `/api/cart/{id}` | Authenticated | Remove from cart |
| `POST` | `/api/chat` | Authenticated | Chat with AI assistant |

---

## Pre-Push Hook (CI/CD)

A local Git pre-push hook runs two automated checks before any push to a non-`main` branch. The push is **blocked** if either check fails.

| Stage | What it does |
|---|---|
| **SonarQube Scan** | Runs backend (`mvnw verify sonar:sonar`) and frontend (`npm test` + `sonar-scanner`) static analysis against a local SonarQube instance |
| **Opencode Review** | AI-powered code review of the full diff vs `main` using `opencode/big-pickle` |

### One-Time Setup

1. **Install the hook** — run this once from the project root:

```bash
git config core.hooksPath hooks/
```

2. **Ensure required environment variables are set** (export them or keep them in `.env`):

| Variable | Purpose |
|---|---|
| `SONAR_TOKEN` | SonarQube authentication token |
| `NVD_API_KEY` | NVD vulnerability database API key |
| `OPENCODE_API_KEY` | OpenCode Zen API key (from [opencode.ai/zen](https://opencode.ai/zen)) |

3. **Start SonarQube** at `localhost:9000` before pushing.

### How It Works

```
git push → pre-push hook fires
  ├── On main? → skip (allow push)
  ├── SonarQube unreachable? → block push
  ├── Backend scan fails? → block push
  ├── Frontend scan fails? → block push
  ├── Opencode finds issues? → block push
  └── All checks pass → allow push
```

---

## Testing

### Backend (49 test classes)

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

## Running Locally

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
│   └── src/test/                     # 49 test classes
├── frontend/                         # Angular SPA
│   └── src/app/
│       ├── components/               # Shared UI components
│       ├── pages/                    # Route page components
│       ├── services/                 # Injectable services
│       └── models/                   # TypeScript interfaces
├── hooks/                            # Git hooks (tracked)
│   └── pre-push                      # SonarQube + Opencode review hook
├── serve.ps1                         # One-command startup script
└── run-sonarqube.ps1                 # SonarQube analysis script
```
