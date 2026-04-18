# Eventual — Event Management Platform

**CS7319 Final Project — Group 06**  
Harini Natarajan · Zachary Suzuki · Fred Belotte

---

## Overview

Eventual is a full-featured event management platform built and evaluated under two architectural styles:

| Directory | Architecture | Description |
|-----------|-------------|-------------|
| `selected/` | **Layered (N-Tier)** | Single Spring Boot monolith with PostgreSQL, Elasticsearch (hybrid search), and Ollama (semantic embeddings) |
| `unselected/` | **Microservices** | API Gateway + four independent services (User, Event, Search, Notification) communicating via REST and Kafka |

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

Make sure your local PostgreSQL instance has the `eventual` database and schema loaded before starting Docker:

```bash
# Create the database (if it doesn't exist yet)
psql -U postgres -c "CREATE DATABASE eventual;"

# Load schema and seed data
psql -U postgres -d eventual -f selected/eventual.database/schema.sql
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

#### IntelliJ IDEA / DataGrip

1. Open **Database** tool window → **+** → **Data Source** → **PostgreSQL**
2. Fill in the fields from the table above
3. Click **Test Connection** — it should succeed as long as your local PostgreSQL is running

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

```bash
# Create the database (if it doesn't exist yet)
psql -U postgres -c "CREATE DATABASE eventual;"

# Load schema and seed data
psql -U postgres -d eventual -f unselected/eventual.database/schema.sql
psql -U postgres -d eventual -f unselected/eventual.database/db_dml.sql
```

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

### Services

| Service | Container | Port | Responsibility | Runs in |
|---------|-----------|------|---------------|---------|
| API Gateway | `eventual-api-gateway` | 8080 | JWT validation, request routing | Docker |
| User Service | `eventual-user-service` | 8082 | Users, groups, authentication | Docker |
| Event Service | `eventual-event-service` | 8081 | Events, RSVPs | Docker |
| Search Service | `eventual-search-service` | 8084 | Elasticsearch indexing, hybrid search, recommendations | Docker |
| Notification Service | `eventual-notification-service` | 8083 | In-app notifications via Kafka | Docker |
| Kafka | `eventual-kafka` | 9092 | Async messaging (KRaft mode) | Docker |
| Elasticsearch (es01) | `eventual-es01` | 9200 | Search index — node 1 (exposed) | Docker |
| Elasticsearch (es02) | `eventual-es02` | — | Search index — node 2 (internal) | Docker |
| Elasticsearch (es03) | `eventual-es03` | — | Search index — node 3 (internal) | Docker |
| Ollama | `eventual-ollama` | 11434 | Semantic embeddings | Docker |
| PostgreSQL | — | 5432 | Persistent data store | **Local** |
| Kibana *(optional)* | `eventual-kibana` | 5601 | Elasticsearch UI | Docker |

---

### Communication

- **Synchronous**: REST via Spring `RestClient` (service-to-service internal calls through container names)
- **Asynchronous**: Apache Kafka topics (`event-created`, `event-updated`, `event-deleted`, `group-indexed`, `join-request-submitted`, `join-request-approved`)

---

### Setting Up Ollama (Semantic Search / Recommendations)

Pull the embedding model once after the first startup:

```bash
docker exec eventual-ollama ollama pull nomic-embed-text
```

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
    ├── docker-compose.yml             ← Kafka + Elasticsearch + Ollama + all 5 services
    └── eventual.backend/
        ├── pom.xml                    ← Parent POM (reactor build)
        ├── api-gateway/               ← Spring Cloud Gateway (port 8080)
        │   ├── Dockerfile
        │   └── src/main/resources/
        │       ├── application.yml        ← local dev config
        │       └── application-docker.yml ← Docker route overrides
        ├── user-service/              ← Users + Groups (port 8082)
        │   └── Dockerfile
        ├── event-service/             ← Events + RSVP (port 8081)
        │   └── Dockerfile
        ├── search-service/            ← Elasticsearch + Recommendations (port 8084)
        │   └── Dockerfile
        └── notification-service/      ← Notifications via Kafka (port 8083)
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
