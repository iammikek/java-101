# Getting Fast at Spring Boot

A step-by-step **Spring Boot + JPA** port of [fastAPI-101](https://github.com/iammikek/fastAPI-101) — same items/categories JSON API as [laravel-101](https://github.com/iammikek/laravel-101), with the same service-layer split you already know from `app/Services/`.

**Audience:** Laravel developers learning JVM backends, or anyone comparing how Spring Boot delivers the same *-101 contract without Blade.

**API-only:** Unlike [laravel-101](https://github.com/iammikek/laravel-101), this repo has **no `/shop`**. Spring Boot can render Thymeleaf, but this port stays **JSON API only** — pair it with [laravel-101's shop](https://github.com/iammikek/laravel-101) on another port, or with [react-101](https://github.com/iammikek/react-101) / [vue-101](https://github.com/iammikek/vue-101) / [alpine-101](https://github.com/iammikek/alpine-101).

---

## What's Included

1. **Spring Boot 3.5** — same URL shape as laravel-101 (no `/api` prefix)
2. **JPA entities** — `User`, `Category`, `Item` (Eloquent equivalents in `domain/`)
3. **Service layer** — `service/` mirrors `app/Services/` in laravel-101
4. **JWT auth** — Bearer tokens on write endpoints (`sub` = email, like jwt-auth)
5. **Pagination** — `{ items, total, skip, limit }` (not Laravel's `links`/`meta` envelope)
6. **Filtering** — `min_price`, `max_price`, `category_id`, `name_contains`
7. **Item stats** — `GET /items/stats/summary`
8. **Domain errors** — `{ detail, code }` (like your API exception responses)
9. **Flyway migrations** — `database/migrations` equivalent, versioned SQL
10. **SQLite locally** — port **8009**
11. **Tests** — 50 JUnit 5 + MockMvc integration tests (fastAPI-101 parity)
12. **CI** — GitHub Actions

---

## Laravel → Spring Boot (cheat sheet)

If you've shipped [laravel-101](https://github.com/iammikek/laravel-101), most concepts map cleanly:

| Laravel | Spring Boot (this repo) |
|---------|-------------------------|
| `php artisan serve` | `./gradlew bootRun` or `make serve` |
| `composer install` | `./gradlew build` (Gradle downloads deps) |
| `.env` + `config/` | `.env` + `application.properties` |
| `routes/api.php` | `@RestController` + `@RequestMapping` in `web/` |
| `app/Http/Controllers` | `web/AuthController`, `ItemController`, … |
| `app/Services/ItemService` | `service/ItemService.java` |
| Eloquent models | JPA entities in `domain/` |
| Eloquent scopes / `where()` chains | Criteria API in `ItemService.list()` |
| `FormRequest` validation | Jakarta `@Valid` on DTO records in `dto/` |
| API Resources / `ApiSerializer` | `dto/` + `ApiMapper` |
| `database/migrations` | Flyway `src/main/resources/db/migration/` |
| `php artisan migrate` | Flyway runs on startup |
| Sanctum / jwt-auth middleware | `security/JwtAuthenticationFilter` |
| `Handler` / `render()` exceptions | `web/GlobalExceptionHandler` |
| `bcrypt` password hashing | `BCryptPasswordEncoder` in `SecurityConfig` |
| `php artisan test` | `./gradlew test` or `make test` |
| `User::where('email', …)` | `UserRepository.findByEmail()` |
| `Item::with('category')` | `ItemRepository.findByIdWithCategory()` |

**Same JSON contract:** register/login/me, category CRUD, item CRUD + PATCH, filters, stats, validation errors as `{ detail, code }`. Login still uses form fields `username` (email) + `password`, matching fastAPI-101 and laravel-101.

---

## Quick Start

### Local (SQLite)

Requires **JDK 21** (or use Docker below).

```bash
cd java-101
cp .env.example .env
mkdir -p data
./gradlew bootRun
# or: make serve
```

Open **http://127.0.0.1:8009/** — hello message  
**http://127.0.0.1:8009/items** — JSON list

No `php artisan key:generate` — set `JWT_SECRET` in `.env` (like `JWT_SECRET` in laravel-101). Flyway applies migrations automatically on boot (no `php artisan migrate` step).

### Docker (no JDK install)

```bash
docker compose up --build
```

API on **http://localhost:8009** (laravel-101 = 8003, nest-101 = 8006).

### Tests

```bash
./gradlew test
# or: make test
```

PHPUnit feature tests in laravel-101 ≈ MockMvc integration tests here — full HTTP round-trips, not mocked controllers.

---

## Project Structure

```
java-101/
├── src/main/java/com/example/java101/
│   ├── web/              # Controllers (app/Http/Controllers)
│   ├── service/          # Business logic (app/Services)
│   ├── domain/           # Eloquent-style models (JPA entities)
│   ├── repository/       # Spring Data (Eloquent query layer)
│   ├── security/         # JWT filter + entry point (middleware)
│   ├── dto/                # Request/response shapes (Form Requests + API Resources)
│   ├── config/             # SecurityConfig (like AppServiceProvider boot)
│   └── exception/          # Domain exceptions (app/Exceptions)
├── src/main/resources/
│   ├── application.properties   # config/*.php + .env
│   └── db/migration/            # database/migrations (Flyway)
├── src/test/java/               # Feature tests (tests/Feature)
├── Dockerfile
├── docker-compose.yml
└── Makefile
```

---

## Why No Shop?

[laravel-101](https://github.com/iammikek/laravel-101) ships a Blade shop at `/shop` with session auth for browsers and JWT for the JSON API. That's the idiomatic Laravel monolith split.

Spring Boot in enterprise teams is usually **API-first** — JSON for SPAs, mobile apps, or other services. Adding a shop here would mean bolting on Thymeleaf/JSP, which isn't how most Spring APIs are structured. This port keeps the **same JSON contract** and skips the browser UI.

**Want a shop?** Run laravel-101 on **8003** for `/shop`, java-101 on **8009** for the JVM take on the same endpoints — or point [react-101](https://github.com/iammikek/react-101) at either backend.

---

## Quick Reference

| Goal | Laravel habit | java-101 |
|------|---------------|----------|
| Copy env | `cp .env.example .env` | same |
| Install / build | `composer install` | `./gradlew build` |
| Migrate | `php artisan migrate` | automatic (Flyway on boot) |
| Run local | `make serve` → :8003 | `make serve` → :8009 |
| Raw JSON items | http://127.0.0.1:8003/items | http://127.0.0.1:8009/items |
| Run tests | `php artisan test` | `make test` |
| Docker | `docker compose up --build` | same |

### API endpoints

| Path | Method | Auth | Purpose |
|------|--------|------|---------|
| `/` | GET | — | Hello message |
| `/health` | GET | — | Health check (`database: connected`) |
| `/auth/register` | POST | — | Create user |
| `/auth/login` | POST | — | Get JWT (`username` = email) |
| `/auth/me` | GET | JWT | Current user |
| `/categories` | GET/POST | JWT on POST | List/create |
| `/categories/{id}` | GET/PATCH/DELETE | JWT on writes | CRUD |
| `/items` | GET/POST | JWT on POST | List/create (paginated, filterable) |
| `/items/stats/summary` | GET | — | Statistics |
| `/items/{id}` | GET/PATCH/DELETE | JWT on writes | CRUD |

Write operations need `Authorization: Bearer <token>`.

---

## Environment variables

| Variable | Default | Laravel parallel |
|----------|---------|------------------|
| `SERVER_PORT` | `8009` | `APP_PORT` / `php artisan serve --port` |
| `SPRING_DATASOURCE_URL` | `jdbc:sqlite:./data/app.db` | `DB_DATABASE` (SQLite path) |
| `JWT_SECRET` | `change-me-in-production` | `JWT_SECRET` in laravel-101 |
| `JWT_EXPIRE_MINUTES` | `60` | `JWT_TTL` |

---

## Compare with laravel-101

| | laravel-101 | java-101 |
|--|-------------|----------|
| Port | 8003 | 8009 |
| ORM | Eloquent | JPA / Hibernate |
| Shop UI | Blade at `/shop` | none (API-only) |
| API auth | jwt-auth Bearer | Spring Security JWT |
| Services | `app/Services/` | `service/` |
| Migrations | Laravel migrations | Flyway SQL |
| Validation | Form Requests | Jakarta `@Valid` DTOs |
| Tests | 28 PHPUnit feature | 50 MockMvc integration |
| Package manager | Composer | Gradle |

Same response shapes. Same filters and pagination. Same `{ detail, code }` errors.

---

## *-101 Family

### API backends

| Repo | Port | Type | Stack |
|------|------|------|-------|
| [fastAPI-101](https://github.com/iammikek/fastAPI-101) | 8000 | API-only | FastAPI, SQLAlchemy |
| [django-101](https://github.com/iammikek/django-101) | 8001 | Monolith | Django + DRF + shop |
| [symfony-101](https://github.com/iammikek/symfony-101) | 8002 | Monolith | Symfony + shop |
| [laravel-101](https://github.com/iammikek/laravel-101) | 8003 | Monolith | Laravel + shop |
| [framework-x-101](https://github.com/iammikek/framework-x-101) | 8004 | Monolith | Framework X + shop |
| [orchestr-101](https://github.com/iammikek/orchestr-101) | 8005 | Monolith | Orchestr + shop |
| [nest-101](https://github.com/iammikek/nest-101) | 8006 | API-only | NestJS, TypeScript |
| [express-101](https://github.com/iammikek/express-101) | 8007 | API-only | Express, Vitest |
| [go-101](https://github.com/iammikek/go-101) | 8000* | API-only | Gin, GORM |
| [fortran-101](https://github.com/iammikek/fortran-101) | 8008 | API-only | Fortran, fpm |
| [**java-101**](https://github.com/iammikek/java-101) | **8009** | API-only | Spring Boot, JPA, Flyway |
| [dotNet-101](https://github.com/iammikek/dotNet-101) | 8010 | API-only | ASP.NET Core, xUnit |
| [flask-101](https://github.com/iammikek/flask-101) | 8011 | API-only | Flask, pytest |
| [rails-101](https://github.com/iammikek/rails-101) | 8012 | Monolith | Rails + shop |
\* go-101 also uses port 8000 — run one backend at a time, or change port in config.

### Other clients

| Repo | Platform | Stack |
|------|----------|-------|
| [flutter-101](https://github.com/iammikek/flutter-101) | Mobile / desktop | Flutter (iOS, macOS, Android) |
| [react-101](https://github.com/iammikek/react-101) | Web browser | React 19, Vite, Vitest |
| [vue-101](https://github.com/iammikek/vue-101) | Web browser | Vue 3, Vite, Pinia |
| [alpine-101](https://github.com/iammikek/alpine-101) | Web browser | Alpine.js, Vite, Vitest |

### Suggested pairing

- **From Laravel to JVM:** laravel-101 (8003) → java-101 (8009) — same API, different stack
- **Shop + JVM API:** laravel-101 `/shop` + java-101 JSON on 8009
- **Pair with a client:** [react-101](https://github.com/iammikek/react-101), [vue-101](https://github.com/iammikek/vue-101), [alpine-101](https://github.com/iammikek/alpine-101), or [flutter-101](https://github.com/iammikek/flutter-101)
- **Compare API-only backends:** java-101 (8009) vs [nest-101](https://github.com/iammikek/nest-101) (8006) / [express-101](https://github.com/iammikek/express-101) (8007)
- **Compare JVM vs .NET:** java-101 (8009) vs [dotNet-101](https://github.com/iammikek/dotNet-101) (8010)

Catalogue: [automica.io/learning-101](https://automica.io/learning-101.html)
