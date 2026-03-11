# IITBase Backend

REST API powering [IITBase](https://iitbase.com) — a curated job board and professional network for IIT graduates.

Built with Spring Boot 3.2, PostgreSQL, and Redis. Designed as a modular monolith with clean package boundaries so individual domains can be extracted into microservices without major refactoring.

---

## Tech Stack

| Layer | Choice | Reason                                   |
|---|---|------------------------------------------|
| Runtime | Java 21 | Virtual threads, pattern matching        |
| Framework | Spring Boot 3.2.1 | Production-grade ecosystem               |
| Auth | Spring Security + JWT + Redis | Stateless tokens, Redis for whitelisting |
| ORM | Spring Data JPA + Hibernate 6 | Type-safe queries, auditing              |
| Database | PostgreSQL 14+ | JSONB, full-text search, mature tooling  |
| Cache / Sessions | Redis | OTP rate limiting, token blacklist       |
| Email | SendGrid | Transactional OTP delivery               |
| Build | Maven | Dependency management, CI compatibility  |

---

## Project Structure

```
src/main/java/com/iitbase/
├── auth/           # JWT issuance, login, signup flows
├── user/           # Identity, roles, profile management
├── job/            # Core job listing domain
├── jobseeker/      # Job seeker profiles and preferences
├── report/         # Community-driven job flagging
├── removal/        # Recruiter-initiated removal requests
├── admin/          # Moderation — jobs, reports, removals
├── email/          # OTP generation, delivery, rate limiting
├── feedback/       # Platform feedback collection
├── config/         # Security, CORS, Redis, JWT, SendGrid
├── common/         # Shared base entity, API response wrapper
└── exception/      # Global exception handling
```

Each module owns its own controller, service, repository, and DTOs. Cross-module calls go through service interfaces only — no repository leakage across boundaries.

---

## API Overview

### Public (no auth required)

```
POST   /api/auth/signup                     Register
POST   /api/auth/login                      Login
POST   /api/auth/verify-otp                 Verify email OTP
POST   /api/auth/resend-otp                 Resend OTP

GET    /api/public/jobs                     List approved jobs (filterable)
GET    /api/public/jobs/{id}                Job detail
POST   /api/public/jobs/{id}/report         Flag a job
POST   /api/public/jobs/{id}/removal-request  Request removal
```

### Authenticated

```
POST   /api/jobs/submit                     Submit job for review
GET    /api/jobs/my                         My submitted jobs
GET    /api/jobs/my/stats                   Submission stats

GET    /api/users/me                        Own profile
PUT    /api/users/me                        Update profile
POST   /api/users/me/change-password        Change password

GET    /api/jobseeker/preferences           Get job preferences
PUT    /api/jobseeker/preferences           Update preferences
```

### Admin only

```
GET    /api/admin/jobs/pending              Jobs awaiting review
GET    /api/admin/jobs/reported             Flagged jobs
POST   /api/admin/jobs/{id}/approve         Approve listing
POST   /api/admin/jobs/{id}/reject          Reject listing
POST   /api/admin/jobs/{id}/mark-expired    Expire listing

GET    /api/admin/reports                   All reports
POST   /api/admin/reports/{id}/resolve      Resolve a report

GET    /api/admin/removals                  Pending removal requests
POST   /api/admin/removals/{id}/approve     Approve removal
POST   /api/admin/removals/{id}/reject      Reject removal
```

---

## Job Lifecycle

```
                    ┌─────────┐
              ┌────▶│ REJECTED │
              │     └─────────┘
┌─────────┐   │     ┌──────────┐
│ PENDING │───┼────▶│ APPROVED │────────────────┐
└─────────┘   │     └──────────┘                │
              │           │                     │
              │    (N reports hit threshold)     │
              │           ▼                     ▼
              │     ┌────────────┐        ┌─────────┐
              └────▶│UNDER_REVIEW│        │ EXPIRED │
                    └────────────┘        └─────────┘
                          │
                          │ (admin clears)
                          ▼
                     ┌──────────┐
                     │ APPROVED │ (re-approved)
                     └──────────┘
```

Recruiters can request removal of their own listings. Admin reviews and approves/rejects.

---

## Local Development

### Prerequisites

- Java 21
- PostgreSQL 14+
- Redis 7+
- Maven 3.8+

### Database

```sql
CREATE DATABASE iitbase;
```

Run Flyway migrations on startup (auto-configured).

### Environment

Set the following in your IDE run configuration or export them in your shell:

```
SPRING_PROFILES_ACTIVE=dev
PGHOST=localhost
PGPORT=5432
PGDATABASE=iitbase
PGUSER=postgres
PGPASSWORD=your_password
REDISHOST=localhost
REDISPORT=6379
REDISPASSWORD=
JWT_SECRET=minimum-32-character-secret-key-here
SENDGRID_API_KEY=SG.your_key
PORT=8080
```

### Run

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

Server starts at `http://localhost:8080`.

---

## Security Model

**Authentication** — Email/password login with OTP verification. JWT tokens are issued on successful login. Tokens are stateless (no server-side session), with Redis used for blacklisting on logout.

**Authorization** — Three roles: `ADMIN`, `RECRUITER`, `JOB_SEEKER`. Role checks are enforced at the service layer, not just the controller, so internal calls are also protected.

**Rate limiting** — Bucket4j applied on auth endpoints (`/api/auth/**`) — 10 requests per hour per IP. OTP resend has its own independent rate limit tracked in Redis.

**Password storage** — BCrypt with default cost factor (10 rounds).

---

## Design Decisions

**Modular monolith over microservices** — At early stage, the operational overhead of microservices (distributed tracing, service discovery, network latency between calls) isn't worth it. The package structure enforces the same boundaries a microservice split would require, so the extraction path is straightforward if traffic demands it.

**JWT + Redis blacklist** — Pure stateless JWT can't support logout or token revocation. Storing a blacklist in Redis gives us revocation without the overhead of fully server-side sessions. Token TTL matches the Redis key TTL so blacklist entries clean themselves up.

**Community moderation with threshold** — Rather than auto-approving all jobs or relying solely on admin review, community reports trigger a review workflow once a configurable threshold is hit (`app.report-threshold` in config). This surfaces bad listings faster than pure admin review while avoiding false positives from single reports.

**DTO pattern throughout** — Entities never leave the service layer. Every API response goes through a DTO, which means schema changes in the DB don't silently break API contracts.

---

## Deployment

Hosted on [Railway](https://railway.app). Postgres and Redis run as Railway plugins linked to the backend service. Environment variables are injected at the platform level — no secrets in the repository.

CI/CD via Railway's GitHub integration — push to `main` triggers a build and deploy.

---

## Contributing

This is a private platform for IIT alumni. If you've spotted a bug or have a suggestion, open an issue or reach out at [hello@iitbase.com](mailto:hello@iitbase.com).