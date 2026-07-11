# java-101

A minimal **API-only** Spring Boot application in the *-101 family. It mirrors the JSON API contract of [fastAPI-101](https://github.com/iammikek/fastAPI-101) with JWT auth, SQLite + JPA/Hibernate + Flyway, and JUnit 5 MockMvc integration tests — but **no server-rendered shop UI**.

## API-only by design

Like [nest-101](https://github.com/iammikek/nest-101), [express-101](https://github.com/iammikek/express-101), [go-101](https://github.com/iammikek/go-101), and [fortran-101](https://github.com/iammikek/fortran-101), this repo has **no server-rendered shop**. Spring Boot can power MVC templates, but this port stays intentionally **JSON API only**. Pair it with [react-101](https://github.com/iammikek/react-101), [vue-101](https://github.com/iammikek/vue-101), or [flutter-101](https://github.com/iammikek/flutter-101).

**Why Spring Boot / Java?** Learn the same *-101 contract with Java 21, Gradle, JPA, Flyway, and Spring Security JWT — the stack many teams use for production APIs.

## What's included

- Spring Boot **3.5** API on port **8009** (Java 21, Gradle)
- SQLite + JPA/Hibernate + Flyway migrations
- JWT authentication (`sub` = email) with bcrypt passwords
- Categories + items CRUD, pagination, filters, stats summary
- Domain errors with `{ detail, code }` responses
- **~47 JUnit 5 + MockMvc integration tests** (fastAPI-101 parity)
- Dockerfile, docker-compose, GitHub Actions CI, Makefile

## Quick start

### Local (SQLite)

Requires **JDK 21**.

```bash
cp .env.example .env
mkdir -p data
./gradlew bootRun
# or: make serve
```

Open **http://127.0.0.1:8009** — you should see:

```json
{"message":"Hello from java-101"}
```

### Docker

```bash
docker compose up --build
```

API on **http://localhost:8009**.

### Tests

```bash
./gradlew test
# or: make test
```

## API endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/` | — | Hello message |
| GET | `/health` | — | Health check (`database: connected`) |
| POST | `/auth/register` | — | Register user |
| POST | `/auth/login` | — | Login (form `username`/`password`) |
| GET | `/auth/me` | JWT | Current user |
| GET | `/categories` | — | List categories |
| GET | `/categories/:id` | — | Show category |
| POST/PATCH/DELETE | `/categories` | JWT | Manage categories |
| GET | `/items` | — | List items (paginated, filterable) |
| GET | `/items/stats/summary` | — | Item statistics |
| GET | `/items/:id` | — | Show item |
| POST/PATCH/DELETE | `/items` | JWT | Manage items |

Write operations require `Authorization: Bearer <token>`.

Login uses OAuth2-style form fields (`username` = email), matching fastAPI-101.

## Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8009` | Listen port |
| `SPRING_DATASOURCE_URL` | `jdbc:sqlite:./data/app.db` | SQLite JDBC URL |
| `JWT_SECRET` | `change-me-in-production` | JWT signing secret |
| `JWT_EXPIRE_MINUTES` | `60` | Token lifetime |

## Project structure

```
java-101/
├── src/main/java/com/example/java101/
│   ├── web/           # Controllers + exception handler
│   ├── service/       # Business logic
│   ├── domain/        # JPA entities
│   ├── repository/    # Spring Data repositories
│   ├── security/      # JWT filter + entry point
│   ├── dto/           # Request/response records
│   └── exception/     # Domain errors
├── src/main/resources/db/migration/   # Flyway
├── src/test/java/                     # MockMvc integration tests
├── Dockerfile
├── docker-compose.yml
└── Makefile
```

## Quick reference

| Goal | Command |
|------|---------|
| Copy env | `cp .env.example .env` |
| Run local | `make serve` → http://127.0.0.1:8009 |
| Run tests | `make test` |
| Docker | `docker compose up --build` |

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

\* go-101 also uses port 8000 — run one backend at a time, or change port in config.

### Other clients

| Repo | Platform | Stack |
|------|----------|-------|
| [flutter-101](https://github.com/iammikek/flutter-101) | Mobile / desktop | Flutter (iOS, macOS, Android) |
| [react-101](https://github.com/iammikek/react-101) | Web browser | React 19, Vite, Vitest |
| [vue-101](https://github.com/iammikek/vue-101) | Web browser | Vue 3, Vite, Pinia |

### Suggested pairing

- **Pair with a client:** [react-101](https://github.com/iammikek/react-101), [vue-101](https://github.com/iammikek/vue-101), or [flutter-101](https://github.com/iammikek/flutter-101)
- **Compare JVM vs Node:** java-101 (8009) vs [nest-101](https://github.com/iammikek/nest-101) (8006) / [express-101](https://github.com/iammikek/express-101) (8007)
- **Compare compiled APIs:** [go-101](https://github.com/iammikek/go-101) or [fortran-101](https://github.com/iammikek/fortran-101)

Catalogue: [automica.io/learning-101](https://automica.io/learning-101.html)
