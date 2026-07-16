# Backend Portfolio Review TODO

Overall backend portfolio score: **5.2/10**

This repository is stronger than a basic CRUD portfolio app because it demonstrates Spring Boot, hexagonal architecture, DTOs, tests, PostgreSQL/JPA, JWT generation, AI client integration, JaCoCo, and SonarQube awareness. It does not yet read as production-grade because security, deployment, API semantics, database migrations, CI/CD, and documentation are incomplete.

## Scores

| Category | Score | Conclusion |
|---|---:|---|
| Architecture | 7/10 | Clear ports/adapters package structure, DTO separation, and domain/infrastructure split. Some overengineering for a small app, and the business domain is still thin. |
| Maintainability | 6/10 | Mostly readable, with constructor injection and small classes. Hurt by copied/inaccurate docs, manual JWT extraction, duplicated auth concerns, and mixed responsibility boundaries. |
| Production readiness | 3/10 | Hardcoded DB/JWT config, `ddl-auto=update`, `data.sql` truncation, Flyway disabled, no deployment profile, and limited operational readiness. |
| Testing | 6.5/10 | Good number of unit, web, client, and JPA tests. Missing CI enforcement, coverage gate, Testcontainers, full API integration tests, security tests, and real PostgreSQL migration path. |
| API design | 5/10 | Basic REST endpoints exist. Weak spots include `200 null` for missing products, cart mutations using query params, no pagination, no OpenAPI, no versioning, and no consistent error model. |
| Security | 2/10 | Tracked frontend `.env` contains a Sonar token, JWT secret is hardcoded, auth is manually parsed, no Spring Security filter chain was found, and there is no rate limiting or token lifecycle story. |
| DevOps | 2.5/10 | Maven wrapper, scripts, JaCoCo, and SonarQube awareness are positives. Missing Docker, Compose, GitHub Actions, artifact publishing, deployment docs, and secret scanning. |
| Documentation | 3.5/10 | Root `README.md` is empty except for the title. `arch.md` is detailed but partially copied from a Trello/board app, which damages credibility. No API docs or setup guide. |

## Highest-Priority Conclusions

1. (DONE) Rotate the leaked Sonar token, remove tracked `.env` files, and clean git history if this repo was pushed.
2. Add CI, Docker Compose, Flyway migrations, Spring Security, OpenAPI, and a real README before presenting this as a backend portfolio project.
3. Fix production config immediately: no hardcoded DB credentials, no hardcoded JWT secret, no `ddl-auto=update`, no always-running destructive seed SQL.
4. Improve API correctness: return proper `404`s, use request DTOs for mutations, validate inputs, add pagination, and standardize error responses.
5. Strengthen the domain story with an order/checkout flow, stock validation, ownership checks, and integration tests against PostgreSQL.

## Top 30 Improvements

| Rank | Improvement | Recruiter Impact | Difficulty | Est. Time |
|---:|---|---|---|---|
| 1 | (DONE) Rotate leaked Sonar token, remove tracked `.env`, and add secret-scanning guidance | Very High | Medium | 1-3h plus history cleanup |
| 2 | (DONE) Add GitHub Actions CI for backend tests, frontend tests, build, and coverage upload | Very High | Medium | 3-6h |
| 3 | Add Dockerfile and `docker-compose.yml` for API, PostgreSQL, and frontend | Very High | Medium | 4-8h |
| 4 | (DONE) Replace `ddl-auto=update` and `data.sql` truncation with Flyway migrations and seed scripts | Very High | Medium | 4-8h |
| 5 | Move DB credentials and JWT secret to environment variables with prod/dev profiles | Very High | Low | 1-3h |
| 6 | (DONE) Implement Spring Security `SecurityFilterChain` and JWT auth filter | Very High | Medium | 5-10h |
| 7 | (DONE) Add OpenAPI/Swagger docs with examples and auth scheme | Very High | Low-Medium | 2-5h |
| 8 | (DONE) Rewrite root README with setup, architecture, API, screenshots, test commands, and demo flow | Very High | Low | 2-4h |
| 9 | (DONE) Fix `arch.md` so it describes the e-commerce domain, not Trello/board examples | High | Low | 1-2h |
| 10 | Add Testcontainers integration tests against PostgreSQL | High | Medium | 5-10h |
| 11 | Add coverage thresholds via JaCoCo and publish badge/report | High | Low-Medium | 2-4h |
| 12 | Add proper `404 Not Found` behavior for missing products, cart items, and users | High | Low | 1-3h |
| 13 | Replace cart query params with request DTOs and validation | High | Low | 1-3h |
| 14 | Add a consistent API error response type instead of raw `Map<String,Object>` | High | Low-Medium | 2-4h |
| 15 | Add pagination, sorting, and filtering for product listing | High | Medium | 3-6h |
| 16 | Add order/checkout domain flow: create order, reserve stock, and clear cart | Very High | Medium-High | 1-2 days |
| 17 | Add stock validation and prevent negative or invalid cart quantities | High | Low-Medium | 2-4h |
| 18 | Prevent users from deleting or modifying cart items they do not own | Very High | Low-Medium | 2-4h |
| 19 | Add role model and admin product management endpoints | High | Medium | 1 day |
| 20 | Add rate limiting for login and chat endpoints | High | Medium | 4-8h |
| 21 | Add structured request logging with correlation IDs | Medium-High | Medium | 4-8h |
| 22 | Add Actuator health, readiness, and liveness endpoints | Medium-High | Low | 1-2h |
| 23 | Add dependency/security scanning in CI, such as OWASP Dependency-Check or Snyk | High | Low-Medium | 2-5h |
| 24 | Add deployment example for Render, Fly.io, Railway, or AWS with env var docs | High | Medium | 4-8h |
| 25 | Add API versioning strategy, such as `/api/v1/...` | Medium | Low | 1-2h |
| 26 | Add refresh token or short-lived token strategy | Medium-High | Medium | 1 day |
| 27 | Add end-to-end API tests using REST Assured or Spring Boot integration tests | High | Medium | 1 day |
| 28 | Add ADRs explaining hexagonal architecture, JWT choice, and DB migration strategy | Medium-High | Low | 2-4h |
| 29 | Clean generated/build artifacts from the repository and ensure root `.gitignore` covers them | Medium | Low | 1h |
| 30 | Add a polished backend highlights section in README: architecture diagram, CI badge, coverage, security, and Docker demo | Very High | Low | 2-3h |

## Recommended First Sprint

1. Security cleanup: rotate token, untrack `.env`, add `.gitignore` coverage, and document secret handling.
2. Production baseline: env-based config, Flyway migrations, Docker Compose, and Actuator health checks.
3. Portfolio polish: README rewrite, OpenAPI docs, CI badges, and corrected architecture documentation.
4. Backend depth: Spring Security JWT filter, API error model, 404 handling, cart validation, and ownership checks.
5. Differentiator feature: implement checkout/orders with stock reservation and PostgreSQL-backed integration tests.
