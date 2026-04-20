# Eventual — Event Management Platform

**CS7319 Final Project — Group 06**  
Harini Natarajan · Zachary Suzuki · Fred Belotte

---

## Overview

Eventual is a full-featured event management platform built and evaluated under two architectural styles:

| Directory | Architecture | Description |
|-----------|-------------|-------------|
| `selected/` | **Layered (N-Tier)** | Single Spring Boot monolith with one shared PostgreSQL database (`eventual`), Elasticsearch (hybrid search), and Ollama (semantic embeddings) |
| `unselected/` | **Microservices** | API Gateway + six independent services (User, Event, Search, Notification, Vendor, Support), each with its own PostgreSQL database, communicating via REST and Kafka |

---

## Selected Architecture — Layered (N-Tier)

### Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Docker Desktop | 4.x or later | Runs Elasticsearch, Ollama, and the app |
| Docker Compose | v2 (bundled with Docker Desktop) | |
| PostgreSQL | 14+ | Must be running **locally on your machine** |

> No local JDK or Maven installation is required — the app builds and runs inside Docker.

#### Local PostgreSQL setup

The selected architecture uses a **single database** (`eventual`) shared by all layers of the monolith.

```bash
# 1. Connect to PostgreSQL as the superuser
psql -U postgres -d postgres

# 2. Create the database (run inside the psql prompt, then exit)
CREATE DATABASE eventual;
\q

# 3. Load the schema DDL
psql -U postgres -d eventual -f selected/eventual.database/schema.sql

# 4. (Optional) Load seed data
psql -U postgres -d eventual -f selected/eventual.database/db_dml.sql
```

The app container connects to your local PostgreSQL via `host.docker.internal:5432` (Docker's built-in hostname that resolves to your host machine).

---

### Quick Start

```bash
# 1. Clone the repo and enter the selected directory
cd CS7319-FinalProject-Group06-Natarajan-Suzuki-Belotte/selected

# 2. Build and start all services
docker compose up --build

# 3. Wait for the health checks to pass (≈ 60–90 s on first run)
#    You will see: "Started EventualApplication in X seconds"

# 4. The API is now available at
#    http://localhost:8080
#    Swagger UI: http://localhost:8080/swagger-ui/index.html
```

To stop everything:

```bash
docker compose down
```

To stop and wipe all persisted data (volumes):

```bash
docker compose down -v
```

---

### Authentication — JWT

All API endpoints except **Register** and **Login** require a valid JWT token.

#### Step 1 — Register a user

```
POST http://localhost:8080/api/users/register
Content-Type: multipart/form-data

email=alice@example.com
password=secret123
firstName=Alice
lastName=Example
role=ORGANIZER        ← valid values: ORGANIZER | PARTICIPANT
```

#### Step 2 — Log in to get a token

```
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "email": "alice@example.com",
  "password": "secret123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Step 3 — Send the token with every subsequent request

Add an `Authorization` header to every request:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

| Client | Where to set it |
|--------|----------------|
| Insomnia | Request → **Auth** tab → **Bearer Token** → paste the token |
| Postman | Request → **Authorization** tab → Type: **Bearer Token** → paste the token |
| curl | `-H "Authorization: Bearer <token>"` |
| Swagger UI | Click **Authorize** (🔒) at the top → enter `Bearer <token>` |

> **Using the provided collections**: The Postman collection (`eventual_selected.postman.json`) automatically saves the token from the login response to a collection variable and attaches it to all subsequent requests. In Insomnia, set the `token` environment variable after logging in.

---

### Service Ports

| Service | Port | URL | Runs in |
|---------|------|-----|---------|
| Spring Boot App | 8080 | http://localhost:8080 | Docker |
| PostgreSQL | 5432 | `jdbc:postgresql://localhost:5432/eventual` | **Local** |
| Elasticsearch (es01) | — | internal only | Docker |
| Elasticsearch (es02) | — | internal only | Docker |
| Elasticsearch (es03) | — | internal only | Docker |
| Ollama | — | internal only | Docker |
| Kibana *(optional)* | 5601 | http://localhost:5601 | Docker |

---

### Connecting to PostgreSQL

PostgreSQL runs on your local machine. Connect using your normal local credentials.

#### psql (command line)

```bash
psql -h localhost -p 5432 -U postgres -d eventual
```

#### pgAdmin / DBeaver / TablePlus

| Field | Value |
|-------|-------|
| Host | `localhost` |
| Port | `5432` |
| Database | `eventual` |
| Username | `postgres` |
| Password | `postgres1` *(or your local password)* |

---

### Setting Up Ollama (Semantic Search / Recommendations)

Ollama starts automatically with `docker compose up`, but the embedding model must be pulled **once** after the first startup:

```bash
docker exec eventual-ollama ollama pull nomic-embed-text
```

Verify the model is ready:

```bash
docker exec eventual-ollama ollama list
# Should show: nomic-embed-text
```

Ollama's port is not published to the host, so it won't conflict if you also have Ollama running locally.

---

### Populating Elasticsearch (Reindex)

After starting the app and loading the PostgreSQL schema, Elasticsearch will be empty. Any events and groups already in the database need to be ingested manually.

#### Trigger the reindex

```
POST http://localhost:8080/api/search/reindex
Authorization: Bearer <your-token>
```

The endpoint returns **202 Accepted** immediately and runs in the background. Poll for progress:

```
GET http://localhost:8080/api/search/reindex/status
Authorization: Bearer <your-token>
```

**Example status response:**

```json
{
  "state": "RUNNING",
  "eventsIndexed": 42,
  "groupsIndexed": 8,
  "eventsFailed": 0,
  "groupsFailed": 0,
  "totalEvents": 150,
  "totalGroups": 20,
  "startedAt": "2025-04-18T10:30:00",
  "completedAt": null,
  "error": null
}
```

| State | Meaning |
|-------|---------|
| `IDLE` | No reindex has been run yet |
| `RUNNING` | Reindex is in progress |
| `COMPLETED` | All records indexed successfully |
| `FAILED` | An unrecoverable error occurred (`error` field has details) |

> **Note:** If Ollama is unavailable, indexing still proceeds — records are stored with lexical fields only, without a semantic embedding vector. Search falls back to keyword-only results.

> **Note:** Only one reindex can run at a time. A second `POST` while one is running returns `409 Conflict`.

---

### Optional — Kibana (Inspect Elasticsearch Indices)

Kibana is included but disabled by default. Start it with:

```bash
docker compose --profile kibana up
```

Then open http://localhost:5601 and navigate to **Dev Tools** to query indices:

```json
GET /events/_search
{ "query": { "match_all": {} } }
```

---

## Unselected Architecture — Microservices

### Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Docker Desktop | 4.x or later | Runs all services and infrastructure |
| Docker Compose | v2 (bundled with Docker Desktop) | |
| PostgreSQL | 14+ | Must be running **locally on your machine** |

#### Local PostgreSQL setup

Each microservice owns its own isolated database — there is no shared database. Create all five databases and load their schemas before starting Docker.

```bash
# 1. Connect as the superuser and create all five databases
psql -U postgres -d postgres <<'SQL'
CREATE DATABASE eventual_users;
CREATE DATABASE eventual_events;
CREATE DATABASE eventual_notifications;
CREATE DATABASE eventual_vendors;
CREATE DATABASE eventual_support;
SQL

# 2. Load each service's schema
psql -U postgres -d eventual_users        -f unselected/eventual.database/user-service.sql
psql -U postgres -d eventual_events       -f unselected/eventual.database/event-service.sql
psql -U postgres -d eventual_notifications -f unselected/eventual.database/notification-service.sql
psql -U postgres -d eventual_vendors      -f unselected/eventual.database/vendor-service.sql
psql -U postgres -d eventual_support      -f unselected/eventual.database/support-service.sql
```

| Database | Owned by | Tables |
|----------|----------|--------|
| `eventual_users` | user-service | `users`, `categories`, `groups`, `group_join_requests` |
| `eventual_events` | event-service | `events`, `rsvp` |
| `eventual_notifications` | notification-service | `notifications` |
| `eventual_vendors` | vendor-service | `vendors`, `vendor_reviews`, `event_vendors` |
| `eventual_support` | support-service | `support_tickets` |

> Cross-service references (e.g. an event's `organizer_email`, a ticket's `submitted_by`) are stored as plain `varchar` columns — there are **no foreign keys across databases**. Consistency is enforced at the application layer.

---

### Quick Start

```bash
# 1. Enter the unselected directory
cd CS7319-FinalProject-Group06-Natarajan-Suzuki-Belotte/unselected

# 2. Build and start all services
docker compose up --build

# 3. Wait for Kafka and Elasticsearch health checks to pass (≈ 90–120 s on first run)
#    All five Spring Boot services start after infrastructure is ready.

# 4. The API Gateway is available at
#    http://localhost:8080
```

To stop everything:

```bash
docker compose down
```

To stop and wipe all persisted data (volumes):

```bash
docker compose down -v
```

---

### Authentication — JWT

Authentication works the same as the selected architecture. The API Gateway validates the JWT on every incoming request before forwarding it to the appropriate service. Only **Register** and **Login** are unauthenticated.

#### Step 1 — Register a user

```
POST http://localhost:8080/api/users/register
Content-Type: multipart/form-data

email=alice@example.com
password=secret123
firstName=Alice
lastName=Example
role=ORGANIZER        ← valid values: ORGANIZER | PARTICIPANT
```

#### Step 2 — Log in to get a token

```
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "email": "alice@example.com",
  "password": "secret123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Step 3 — Send the token with every subsequent request

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

> **Using the provided collections**: Import `eventual_selected.postman.json` (Postman) or `eventual_selected.insomnia.json` (Insomnia) from the project root. Both collections are pre-configured with auth headers. The Postman collection automatically saves the token after login.

---

### Services

| Service | Container | Port | Responsibility | Runs in |
|---------|-----------|------|---------------|---------|
| API Gateway | `eventual-api-gateway` | 8080 | JWT validation, request routing | Docker |
| User Service | `eventual-user-service` | 8082 | Users, groups, join requests, authentication | Docker |
| Event Service | `eventual-event-service` | 8081 | Events, RSVPs | Docker |
| Search Service | `eventual-search-service` | 8084 | Elasticsearch indexing, hybrid search, recommendations | Docker |
| Notification Service | `eventual-notification-service` | 8083 | In-app notifications via Kafka | Docker |
| Vendor Service | `eventual-vendor-service` | 8085 | Vendors, vendor reviews, event–vendor assignments | Docker |
| Support Service | `eventual-support-service` | 8086 | Support tickets | Docker |
| Kafka | `eventual-kafka` | 9092 | Async messaging (KRaft mode) | Docker |
| Elasticsearch (es01) | `eventual-es01` | 9200 | Search index — node 1 (exposed) | Docker |
| Elasticsearch (es02) | `eventual-es02` | — | Search index — node 2 (internal) | Docker |
| Elasticsearch (es03) | `eventual-es03` | — | Search index — node 3 (internal) | Docker |
| Ollama | `eventual-ollama` | 11434 | Semantic embeddings | Docker |
| PostgreSQL | — | 5432 | Persistent data store | **Local** |
| Kibana *(optional)* | `eventual-kibana` | 5601 | Elasticsearch UI | Docker |

---

### Inter-Service Communication

**Synchronous** calls use REST via Spring `RestClient`. Services call each other by container name (e.g. `http://user-service:8082`) when running in Docker.

**Asynchronous** events flow through Apache Kafka. Topic names are configured in each service's `application.properties` under `kafka.topics.*`.

#### Kafka Topics

| Topic | Published by | Consumed by | Trigger |
|-------|-------------|-------------|---------|
| `event-created` | event-service | notification-service, search-service | New event created |
| `event-updated` | event-service | notification-service, search-service | Event details changed |
| `event-deleted` | event-service | notification-service, search-service | Event removed |
| `rsvp-created` | event-service | notification-service | User RSVPs to an event |
| `rsvp-cancelled` | event-service | notification-service | User cancels their RSVP |
| `group-indexed` | user-service | search-service | New group created or updated |
| `group-deleted` | user-service | search-service | Group removed |
| `join-request-submitted` | user-service | notification-service | User requests to join a group |
| `join-request-approved` | user-service | notification-service | Organizer approves join request |
| `join-request-rejected` | user-service | notification-service | Organizer rejects join request |

Topic names can be overridden via environment variables in `docker-compose.yml`:

```yaml
environment:
  KAFKA_TOPICS_EVENT-CREATED: event-created
  KAFKA_TOPICS_RSVP-CREATED: rsvp-created
  # ... etc.
```

---

### Setting Up Ollama (Semantic Search / Recommendations)

Pull the embedding model once after the first startup:

```bash
docker exec eventual-ollama ollama pull nomic-embed-text
```

---

### Populating Elasticsearch (Reindex)

After starting the services and loading the PostgreSQL schemas, Elasticsearch will be empty. Any events and groups already in the database need to be ingested manually.

#### Trigger the reindex

```
POST http://localhost:8080/api/search/reindex
Authorization: Bearer <your-token>
```

The endpoint returns **202 Accepted** immediately and runs in the background. Poll for progress:

```
GET http://localhost:8080/api/search/reindex/status
Authorization: Bearer <your-token>
```

**Example status response:**

```json
{
  "state": "RUNNING",
  "eventsIndexed": 42,
  "groupsIndexed": 8,
  "eventsFailed": 0,
  "groupsFailed": 0,
  "totalEvents": 150,
  "totalGroups": 20,
  "startedAt": "2025-04-18T10:30:00",
  "completedAt": null,
  "error": null
}
```

| State | Meaning |
|-------|---------|
| `IDLE` | No reindex has been run yet |
| `RUNNING` | Reindex is in progress |
| `COMPLETED` | All records indexed successfully |
| `FAILED` | An unrecoverable error occurred (`error` field has details) |

> **Note:** The reindex calls Ollama to generate semantic embeddings for each record. If Ollama is unavailable, indexing still proceeds — records are stored in Elasticsearch with lexical fields only, without a semantic embedding vector. Search will fall back to keyword-only results.

> **Note:** Only one reindex can run at a time. A second `POST` while one is in progress returns `409 Conflict`.

---

### Optional — Kibana

```bash
docker compose --profile kibana up
```

Then open http://localhost:5601.

---

## Project Structure

```
CS7319-FinalProject-Group06-Natarajan-Suzuki-Belotte/
├── README.md                          ← this file
│
├── selected/                          ← Layered (N-Tier) architecture
│   ├── Dockerfile                     ← Multi-stage Maven build + JRE runtime
│   ├── docker-compose.yml             ← Elasticsearch + Ollama + App (PostgreSQL is local)
│   ├── eventual.backend/              ← Spring Boot monolith source
│   │   ├── pom.xml
│   │   └── src/
│   │       └── main/
│   │           ├── java/
│   │           │   └── com/CS7319/Group06/eventual/
│   │           │       ├── controller/
│   │           │       ├── service/
│   │           │       ├── dao/
│   │           │       ├── model/
│   │           │       └── config/
│   │           └── resources/
│   │               └── application.properties
│   └── eventual.database/
│       ├── schema.sql                 ← DDL (auto-loaded on first start)
│       └── db_dml.sql                 ← Seed data (auto-loaded on first start)
│
└── unselected/                        ← Microservices architecture
    ├── docker-compose.yml             ← Kafka + Elasticsearch + Ollama + all 7 services
    ├── eventual.database/
    │   ├── user-service.sql           ← Schema for eventual_users
    │   ├── event-service.sql          ← Schema for eventual_events
    │   ├── notification-service.sql   ← Schema for eventual_notifications
    │   ├── vendor-service.sql         ← Schema for eventual_vendors
    │   └── support-service.sql        ← Schema for eventual_support
    └── eventual.backend/
        ├── pom.xml                    ← Parent POM (reactor build)
        ├── api-gateway/               ← Spring Cloud Gateway (port 8080)
        │   ├── Dockerfile
        │   └── src/main/resources/
        │       ├── application.yml        ← local dev config
        │       └── application-docker.yml ← Docker route overrides
        ├── user-service/              ← Users, Groups, Join Requests (port 8082)
        │   └── Dockerfile
        ├── event-service/             ← Events, RSVPs (port 8081)
        │   └── Dockerfile
        ├── search-service/            ← Elasticsearch, Hybrid Search, Recommendations (port 8084)
        │   └── Dockerfile
        ├── notification-service/      ← In-app Notifications via Kafka (port 8083)
        │   └── Dockerfile
        ├── vendor-service/            ← Vendors, Reviews, Event–Vendor links (port 8085)
        │   └── Dockerfile
        └── support-service/           ← Support Tickets (port 8086)
            └── Dockerfile
```

---

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 23 |
| Framework | Spring Boot 3.4.4 |
| Database | PostgreSQL 16 |
| Search | Elasticsearch 8.13.4 |
| Embeddings | Ollama + nomic-embed-text |
| Messaging | Apache Kafka |
| Auth | JWT (jjwt 0.12.6) |
| Build | Maven 3.9 |
| Container | Docker + Docker Compose |
