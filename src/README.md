# IITBase Backend

Production-grade Spring Boot backend for a curated job platform targeting Tier-1 college graduates.

## Architecture

**Modular Monolith** - Package-by-feature design for easy microservice extraction if needed.

### Modules

- **auth**: Authentication and JWT token generation
- **user**: User identity and role management
- **job**: Core job listing domain logic
- **report**: Community-driven job quality control
- **removal**: Recruiter removal request workflow
- **admin**: Admin moderation operations

### Tech Stack

- Java 21
- Spring Boot 3.2.1
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

## Setup

### Prerequisites

- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### Database Setup

```sql
CREATE DATABASE iitbase;
```

### Configuration

Update `application.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/iitbase
    username: your_username
    password: your_password
```

### Run

```bash
mvn clean install
mvn spring-boot:run
```

Server runs on `http://localhost:8080`

### Default Admin Credentials

```
Email: admin@iitbase.com
Password: admin123
```

## API Endpoints

### Public

- `POST /api/auth/signup` - Create account
- `POST /api/auth/login` - Login
- `GET /api/public/jobs` - List approved jobs
- `GET /api/public/jobs/{id}` - Job details
- `POST /api/public/jobs/{id}/report` - Report job
- `POST /api/public/jobs/{id}/removal-request` - Request removal

### Authenticated

- `POST /api/jobs/submit` - Submit job for review

### Admin Only

- `GET /api/admin/jobs/pending` - Pending jobs
- `GET /api/admin/jobs/reported` - Reported jobs
- `POST /api/admin/jobs/{id}/approve` - Approve job
- `POST /api/admin/jobs/{id}/reject` - Reject job
- `POST /api/admin/jobs/{id}/mark-expired` - Mark expired

## Security

- BCrypt password hashing
- JWT-based stateless authentication
- Role-based access control (ADMIN, JOB_SEEKER, RECRUITER)
- CORS configured for frontend origin

## Job Lifecycle

```
PENDING → APPROVED (visible to public)
        → REJECTED
        → UNDER_REVIEW (auto-flagged after threshold reports)

APPROVED → EXPIRED (admin action)
         → REMOVED_BY_RECRUITER (on approval)
```

## Design Decisions

1. **Modular Monolith**: Start simple, extract microservices only if needed
2. **No cross-module repository calls**: Services communicate via public interfaces
3. **Thin controllers**: Business logic lives in services
4. **DTO pattern**: Clear API contracts, entity encapsulation
5. **Auto moderation**: Community reports trigger review workflow

## Interview Talking Points

- Why modular monolith over microservices initially?
- How JWT stateless auth scales
- Why admin moderation instead of auto-approval
- How report threshold prevents spam while maintaining trust
- Database indexing strategy for job filters