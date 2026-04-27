# Eventual — Event Management Platform

**CS7319 — Software Architecture & Design | Final Project Report**
Group 06: Harini Natarajan · Zachary Suzuki · Fred Belotte
April 2026

---

> The `selected/` folder contains the **Microservices** architecture and `unselected/` contains the **Layered (N-Tier)** architecture. During initial project proposal phase, Layered was the recommended choice. After implementing both architectures, we reversed its decision — **Microservices is the final selected architecture**.

---

## Section 1: Implementation Platform

### 1.1 Technology Stack

| Component | Technology | Version | Role |
|-----------|-----------|---------|------|
| Backend Language | Java (Eclipse Temurin JDK) | 23 | Server-side business logic |
| Backend Framework | Spring Boot | 3.4.4 | REST API, dependency injection, security |
| API Gateway | Spring Cloud Gateway | 2024.0.1 | JWT validation and request routing (microservices) |
| Build Tool | Apache Maven | 3.9 | Dependency management and compilation (inside Docker) |
| Frontend | Angular | 21.2.0 | Single-page application, served by Nginx in Docker |
| Database | PostgreSQL | 16 | Relational persistence (5 isolated databases in microservices) |
| Search Engine | Elasticsearch | 8.13.4 | Full-text (BM25) and K-NN vector search, 3-node cluster |
| Embedding Server | Ollama — nomic-embed-text | latest | Local semantic embeddings, 768-dimensional vectors |
| Message Broker | Apache Kafka | 3.7 (KRaft) | Asynchronous event messaging (microservices only) |
| Authentication | JWT — jjwt | 0.12.6 | Stateless token-based authentication and authorisation |
| Container Runtime | Docker Desktop | 4.x | Runs all services; the only required runtime prerequisite |
| API Documentation | SpringDoc / Swagger UI | 2.8.3 | Auto-generated interactive API docs at `/swagger-ui` |

> Java, Maven, and the Angular CLI are used exclusively inside Docker build containers and do not need to be installed on the host machine.

### 1.2 Prerequisites

Only two tools need to be installed on the host machine. Everything else — the JVM, Elasticsearch, Kafka, Ollama — is provisioned automatically by Docker Compose.

| Prerequisite | Version | Download |
|-------------|---------|---------|
| Docker Desktop | 4.x or later | https://www.docker.com/products/docker-desktop/ |
| PostgreSQL | 16 | https://www.postgresql.org/download/ |

### 1.3 Installation and Configuration

**Docker Desktop** must be installed and running before any Docker Compose command is issued. The three-node Elasticsearch cluster and Kafka together require at least **6 GB RAM and 4 CPUs**. Set these limits in Docker Desktop → Settings → Resources.

**PostgreSQL 16** must be running on the host machine at the default port 5432. The application containers connect to it via `host.docker.internal`. The expected superuser password is `postgres1`. If a different password is used, update the `SPRING_DATASOURCE_PASSWORD` environment variable in the relevant `docker-compose.yml` before starting the stack.

Verify PostgreSQL is reachable:

```bash
psql -h localhost -p 5432 -U postgres -c "SELECT version();"
```

---

## Section 2: Build Process

Both implementations use a fully Dockerised, multi-stage build pipeline. No local Java, Maven, or Angular CLI installation is required. Docker Compose compiles the source, packages the JAR artefacts, and assembles the runtime images.

### 2.1 Selected Architecture — Microservices (`selected/` folder)

The build is coordinated by a Maven reactor project at `selected/eventual.backend/pom.xml`. Each of the seven services has an independent Dockerfile. Docker Compose builds all seven images.

```bash
cd CS7319-FinalProject-Group06-Natarajan-Suzuki-Belotte/selected
docker compose up --build
```

The first run downloads all Maven dependencies into a Docker layer cache, which takes approximately 5–10 minutes. Subsequent builds reuse the cache and complete significantly faster.

### 2.2 Unselected Architecture — Layered (`unselected/` folder)

The layered implementation is packaged as a single Spring Boot application using a single Dockerfile at `unselected/Dockerfile`.

```bash
cd CS7319-FinalProject-Group06-Natarajan-Suzuki-Belotte/unselected
docker compose up --build
```

The initial build completes in approximately 3–5 minutes.

---

## Section 3: System Execution

### 3.1 Running the Selected Architecture — Microservices (`selected/` folder)

The microservices variant uses a Database-per-Service pattern. Five PostgreSQL databases must be created and their schemas loaded before starting Docker Compose.

**Step 1 — Create databases and load schemas:**

```bash
# Create databases
psql -U postgres -d postgres <<'SQL'
  CREATE DATABASE eventual_users;
  CREATE DATABASE eventual_events;
  CREATE DATABASE eventual_notifications;
  CREATE DATABASE eventual_vendors;
  CREATE DATABASE eventual_support;
SQL

# Load schemas
psql -U postgres -d eventual_users         -f selected/eventual.database/user-service.sql
psql -U postgres -d eventual_events        -f selected/eventual.database/event-service.sql
psql -U postgres -d eventual_notifications -f selected/eventual.database/notification-service.sql
psql -U postgres -d eventual_vendors       -f selected/eventual.database/vendor-service.sql
psql -U postgres -d eventual_support       -f selected/eventual.database/support-service.sql
```

**Step 2 — Start all services:**

```bash
cd selected
docker compose up --build
```

The Kafka broker and Elasticsearch cluster perform health checks before the Spring Boot services initialise. The stack is fully operational within 90–120 seconds on the first run. A successful startup produces log lines such as:

```
eventual-user-service   | Started UserServiceApplication in 8.4 seconds
eventual-event-service  | Started EventServiceApplication in 7.2 seconds
eventual-search-service | Started SearchServiceApplication in 9.1 seconds
```

**Step 3 — Pull the Ollama embedding model (once, on first run):**

```bash
docker exec eventual-ollama ollama pull nomic-embed-text

# Verify:
docker exec eventual-ollama ollama list
```

**Step 4 — Register, authenticate, and populate Elasticsearch:**

```
# Register a user
POST http://localhost:8080/api/users/register
Content-Type: multipart/form-data

email=alice@example.com  |  password=secret123  |  firstName=Alice
lastName=Example  |  role=ORGANIZER   (or PARTICIPANT)

# Log in to obtain a JWT token
POST http://localhost:8080/api/users/login
Content-Type: application/json
{ "email": "alice@example.com", "password": "secret123" }
# Response: { "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }

# Trigger Elasticsearch reindex (required once after schema load)
POST http://localhost:8080/api/search/reindex
Authorization: Bearer <token>

# Check progress
GET  http://localhost:8080/api/search/reindex/status
Authorization: Bearer <token>
```

All subsequent requests require the `Authorization: Bearer <token>` header. The Postman collection (`eventual_selected.postman.json`) automatically saves the token from the login response.

**Service ports:**

| Service | Container | Port | Responsibility |
|---------|-----------|------|---------------|
| API Gateway | eventual-api-gateway | 8080 | JWT validation, routing — single entry point |
| User Service | eventual-user-service | 8082 | Users, groups, join requests, authentication |
| Event Service | eventual-event-service | 8081 | Events, RSVPs, Kafka publishing |
| Search Service | eventual-search-service | 8084 | Hybrid search, recommendations |
| Notification Service | eventual-notification-service | 8083 | In-app notifications via Kafka |
| Vendor Service | eventual-vendor-service | 8085 | Vendors, reviews, event-vendor assignments |
| Support Service | eventual-support-service | 8086 | Support ticket management |
| Kafka | eventual-kafka | 9092 | Async messaging (KRaft, no ZooKeeper) |
| Elasticsearch es01 | eventual-es01 | 9200 | Primary index node |
| Elasticsearch es02/03 | eventual-es02/03 | — | Replica nodes (internal only) |
| Ollama | eventual-ollama | — | Embedding model server (internal only) |
| PostgreSQL | (host) | 5432 | Persistent store — runs outside Docker |
| Kibana (optional) | eventual-kibana | 5601 | `docker compose --profile kibana up` |

To stop: `docker compose down`
To stop and remove all data volumes: `docker compose down -v`

**API exploration:** Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`. Click Authorize and supply the bearer token to invoke endpoints from the browser. Pre-configured collections for Postman (`eventual_selected.postman.json`) and Insomnia (`eventual_selected.insomnia.json`) are included in the project root.

---

### 3.2 Running the Unselected Architecture — Layered (`unselected/` folder)

The layered variant uses a single shared database.

**Step 1 — Create the database and load the schema:**

```bash
psql -U postgres -d postgres -c "CREATE DATABASE eventual;"
psql -U postgres -d eventual -f unselected/eventual.database/schema.sql

# Optional — load sample seed data:
psql -U postgres -d eventual -f unselected/eventual.database/02_data.sql
```

**Step 2 — Start the stack:**

```bash
cd unselected
docker compose up --build
```

The stack is operational within 60–90 seconds. The Ollama model pull and Elasticsearch reindex steps are identical to those in Section 3.1.

**Service ports:**

| Service | Port |
|---------|------|
| Spring Boot Application | 8080 |
| PostgreSQL | 5432 (host) |
| Elasticsearch (3 nodes) | internal |
| Ollama | internal |
| Kibana (optional) | 5601 |

---

## Section 4: Architecture Design — Comparison and Rationale

### 4.1 Candidate Architecture A — Layered (N-Tier) [Unselected]

**Repository location:** `unselected/` folder

The Layered architecture realises the entire Eventual platform as a single Spring Boot application organised into four horizontal layers:

1. **Presentation Layer** — thirteen REST controllers handle all inbound HTTP traffic (UserController, EventController, GroupController, RsvpController, SearchController, RecommendationController, VendorController, NotificationController, CategoryController, SupportController, and others).
2. **Business Logic Layer** — thirteen service interfaces and their implementations enforce domain rules (EventService, GroupService, RsvpService, SearchService, IngestionService, NotificationService, VendorService, EmbeddingService, and others).
3. **Data Access Layer** — over ten DAO interfaces with pure JDBC implementations execute hand-written SQL (JdbcEventDao, JdbcGroupDao, JdbcRsvpDao, ElasticSearchDao, ElasticsearchIngestionDao, and others).
4. **Persistence Layer** — a single PostgreSQL database (`eventual`) shared by all layers, supplemented by a three-node Elasticsearch cluster for search and an Ollama server for semantic embeddings.

Asynchronous work — notification dispatch, Elasticsearch indexing, and batch reindexing — is handled by three named Spring `ThreadPoolTaskExecutor` beans (`notificationExecutor`, `ingestionExecutor`, `reindexExecutor`) running within the same process. No external message broker is required.

**Advantages:**
- Strong ACID transactions — RSVP waitlist promotions, group membership changes, and notification creation are atomic within a single database transaction.
- Single deployable unit — the entire application runs in one Docker container.
- In-process communication — calls between Controller, Service, and DAO are direct Java method invocations with no network overhead.
- Single observability surface — all log events originate from one process, simplifying debugging.
- Centralised data — a query spanning users, events, and notifications is a single SQL JOIN.

**Disadvantages:**
- Single point of failure — an unhandled exception or memory exhaustion in any module terminates the entire platform, including the core event and RSVP subsystems.
- Indivisible scaling — the Ollama embedding workload is disproportionately CPU-intensive, but the entire monolith must be scaled to provide it with additional resources.
- Tight coupling — modifications to shared model classes propagate across controller, service, and DAO layers simultaneously.
- Monolithic growth risk — the codebase becomes progressively harder to navigate and maintain as feature scope expands.

---

### 4.2 Candidate Architecture B — Microservices [Selected]

**Repository location:** `selected/` folder

The Microservices architecture decomposes Eventual into seven independently deployable services, each owning its own database and communicating via synchronous REST and asynchronous Kafka messaging.

**Service decomposition:**

| Service | Port | Database | Responsibility |
|---------|------|---------|---------------|
| API Gateway | 8080 | None | JWT validation, declarative route configuration |
| User Service | 8082 | eventual_users | Users, groups, join requests, authentication |
| Event Service | 8081 | eventual_events | Events, RSVPs, Kafka event publishing |
| Search Service | 8084 | Elasticsearch | Hybrid search, recommendations, index management |
| Notification Service | 8083 | eventual_notifications | In-app notifications via Kafka consumers |
| Vendor Service | 8085 | eventual_vendors | Vendor catalogue, reviews, event-vendor links |
| Support Service | 8086 | eventual_support | Support ticket lifecycle |

**Communication model:**
- **Synchronous REST** — services address one another by Docker network hostname (e.g., `http://user-service:8082`) for operations requiring an immediate response.
- **Asynchronous Kafka** — the Event Service and User Service publish domain events to named Kafka topics; the Notification and Search Services consume these events independently. This decouples producers from consumers and makes the system resilient to transient consumer downtime.

**Kafka topics:**

| Topic | Publisher | Consumer(s) | Business Event |
|-------|-----------|------------|---------------|
| event-created | Event Service | Notification, Search | New event published |
| event-updated | Event Service | Notification, Search | Event details modified |
| event-deleted | Event Service | Notification, Search | Event removed |
| rsvp-created | Event Service | Notification | User RSVPs to an event |
| rsvp-cancelled | Event Service | Notification | User cancels an RSVP |
| group-indexed | User Service | Search | Group created or updated |
| group-deleted | User Service | Search | Group removed |
| join-request-submitted | User Service | Notification | User requests group membership |
| join-request-approved | User Service | Notification | Organiser approves join request |
| join-request-rejected | User Service | Notification | Organiser rejects join request |

Cross-service references (e.g., `organizer_email` on an event record, `event_id` on a notification) are stored as plain VARCHAR columns. No foreign-key constraints cross database boundaries; referential integrity is enforced at the application layer.

**Advantages:**
- **Independent scalability** — each service can be allocated compute resources proportional to its workload. The Search Service (Ollama embedding generation) can be given additional CPU and memory without affecting any other service.
- **Asynchronous performance** — Kafka messaging means RSVP confirmation is returned to the client immediately; notification dispatch and Elasticsearch indexing proceed asynchronously without blocking the HTTP response.
- **Fault containment** — failure in the Vendor or Support Service does not affect Event, User, or Search Services; core platform operations remain available.
- **Durable messaging** — Kafka persists messages to disk. If the Notification Service is temporarily unavailable, it resumes consumption from the last committed offset on recovery with no data loss.
- **Centralised security** — the API Gateway validates JWT authenticity once per request; per-service databases limit the blast radius of any credential compromise to that service's data only.
- **Extensibility** — a new capability (e.g., Analytics, Payments) is added as a new service with a new Gateway route. No existing service code is modified.
- **Independent testability** — each service can be exercised end-to-end against its own isolated database without running the full platform.

**Disadvantages:**
- Operational complexity — 13+ containers must be orchestrated, monitored, and debugged concurrently.
- Eventual consistency — data may be transiently inconsistent across service boundaries.
- Distributed debugging — a failed RSVP transaction spans the Event Service, Kafka, and the Notification Service, requiring log correlation across multiple processes.
- Gateway as single point of entry — an API Gateway failure renders every service unreachable regardless of individual service health.

---

### 4.3 Comparison Table

| Dimension | Layered (Unselected) | Microservices (Selected) |
|-----------|---------------------|------------------------|
| Deployable units | 1 Spring Boot monolith | 7 independent services |
| Databases | 1 shared PostgreSQL | 5 isolated PostgreSQL databases |
| Messaging | In-process Spring Events + thread pools | Apache Kafka (async, durable, disk-backed) |
| Inter-component calls | Direct Java method calls — no network overhead | HTTP REST between services + Kafka async events |
| Transaction scope | Single ACID database transaction | Eventual consistency across service boundaries |
| Fault isolation | None — one failure can take down all features | Per-service — peripheral failures remain contained |
| Scaling | Entire monolith must scale as a unit | Each service scales independently |
| Elasticsearch sync | Immediate, in-process call on write | Asynchronous via Kafka consumer |
| Observability | Single log stream | Distributed — requires cross-service log correlation |
| Infrastructure footprint | Low — 4 containers (app, ES×3, Ollama) | High — 13+ containers including Kafka |
| RSVP response latency | Higher — notification + ES sync block the thread | Lower — Kafka async confirms RSVP immediately |
| Search Service scaling | Cannot isolate — whole monolith must scale | Search container can receive dedicated CPU/RAM |

---

### 4.4 Rationale for Selecting the Microservices Architecture

After implementing both architectures, the team identified seven factors that favour the Microservices design.

**1. Independent Scalability for Compute-Intensive Search**

The Search Service generates 768-dimensional Ollama vector embeddings for every event and group record — a workload significantly more CPU- and memory-intensive than the User, Vendor, or Support services. The Microservices architecture permits the Search Service container to be allocated additional resources in isolation. In the Layered monolith the entire application must be over-provisioned to serve the embedding workload.

**2. Reduced RSVP Response Latency via Asynchronous Messaging**

In the Layered architecture an RSVP request is on the critical path for both notification persistence and Elasticsearch index updates, both of which execute synchronously within the same thread. In the Microservices variant the Event Service publishes a Kafka event and returns the RSVP confirmation immediately; downstream consumers process notifications and index updates asynchronously and in parallel.

**3. Availability Through Fault Containment**

Vendor management and support ticketing are ancillary concerns relative to event discovery and RSVP. In the Layered architecture, resource exhaustion or a defect in either module is capable of terminating the entire Spring Boot process. The Microservices architecture contains such failures: the Vendor and Support Services can be restarted or remain down while User, Event, and Search Services continue to serve traffic.

**4. Durability of Asynchronous Notifications**

Kafka persists messages to disk with configurable retention. If the Notification Service becomes unavailable, pending messages accumulate in the topic and are delivered in order when the service recovers. The in-process thread pool used by the Layered architecture offers no equivalent guarantee — messages queued in memory are lost if the process terminates before they are processed.

**5. Centralised Security Enforcement**

The API Gateway validates JWT authenticity and claims for every inbound request before forwarding it to any downstream service. The Database-per-Service pattern additionally constrains the impact of any breach: a compromised Vendor Service cannot access user credentials, event records, or RSVP data held by other services.

**6. Non-Intrusive Extensibility**

Each addition in the Microservices architecture is a new service with a new route declaration in the API Gateway; no existing service code is modified. In the Layered monolith each new feature adds shared code to a growing codebase and increases the surface area for regression defects.

**7. Independent Verification of Service Behaviour**

Each microservice operates against its own isolated database and can be validated end-to-end without spinning up the full platform. This isolation eliminates test interference between domains and allows parallel development across the team.

---

## Section 5: Deviations from the Project Proposal

### 5.1 Original Recommendation

The Group 06 project proposal evaluated both candidate architectures and recommended the Layered (N-Tier) Architecture on the following grounds:

1. **Transactional integrity for RSVP and waitlist management** — a single shared database allows the available-spots check and RSVP creation to execute within an atomic transaction, eliminating race conditions where two concurrent requests claim the last remaining slot.
2. **Simplified cross-domain queries** — the Vendor and Support modules frequently join against User and Event data. In the Layered architecture this is a single SQL JOIN; in Microservices it requires a synchronous inter-service HTTP call, introducing latency and a network failure mode.
3. **Immediate search index consistency** — the Layered architecture pushes newly created events to Elasticsearch in-process and synchronously; the Kafka-based pipeline introduces a propagation delay before new records become searchable.
4. **Proportionate complexity** — the proposal concluded that the operational overhead of multiple databases, an API gateway, inter-service contracts, and a message broker was not justified by the scale requirements of the project.

### 5.2 Rationale for the Change

Hands-on implementation of both architectures revealed that several of the proposal's foundational assumptions did not hold in practice.

**Kafka operational complexity was overstated.** Running Kafka in KRaft mode — which eliminates the ZooKeeper dependency and reduces the deployment to a single container — proved straightforward within a Docker Compose environment. The overhead of configuring producer and consumer bindings was modest and proportionate to the reliability and decoupling benefits realised.

**RSVP consistency is an application-layer concern in both designs.** The race condition described in the proposal must be addressed at the application layer regardless of whether a shared or distributed database is used. The Microservices variant applies the same available-spots guard and achieves equivalent correctness through application-level concurrency control.

**Embedding workload demands independent resource allocation.** The computational cost of generating 768-dimensional Ollama vector embeddings was substantially underestimated at the proposal stage. In production-representative conditions this workload consumes significantly more CPU and memory than any other service. The inability to allocate resources selectively to the Search Service without over-provisioning the entire monolith represented a material deficiency of the Layered design.

**RSVP latency was measurably worse under the Layered model.** Performance measurements taken during implementation showed that RSVP response times under the Layered architecture were elevated relative to the Microservices implementation. The cause was the synchronous execution of notification persistence and Elasticsearch indexing within the RSVP request thread.

**Single-process failure risk manifested during development.** Defects introduced to the Vendor and Support modules during iterative development required full application restarts, interrupting concurrent work on the event and RSVP subsystems. In the microservices implementation equivalent defects were isolated to the affected container with no disruption to adjacent services.

---

## Section 6: Additional Design Decisions

### 6.1 Raw JDBC in Preference to JPA/Hibernate

Both implementations access PostgreSQL through the Spring JDBC template with hand-written SQL rather than through an ORM framework.

- **Query precision** — the search, recommendation, and RSVP queries involve multi-table joins, conditional predicates, and aggregation patterns that are cumbersome to express in JPQL. Hand-written SQL is more readable, maintainable, and directly optimisable.
- **Elimination of lazy-loading hazards** — JPA lazy loading can silently trigger N+1 query chains when entity graphs are serialised to JSON. With JDBC every database interaction is an explicit method call with no implicit round-trips.
- **Predictable execution plans** — ORM-generated SQL varies with provider version and caching state. Plain SQL queries are stable, directly inspectable in application logs, and easily profiled with `EXPLAIN ANALYZE`.

### 6.2 Hybrid Search: Lexical and Semantic Retrieval

The search subsystem combines two complementary retrieval strategies.

- **Lexical search (BM25)** — Elasticsearch's inverted-index full-text search ranks results by term frequency and inverse document frequency. This excels at precise keyword queries such as "jazz festival downtown".
- **Semantic search (approximate K-NN)** — the Ollama `nomic-embed-text` model converts event and group descriptions into 768-dimensional dense vectors. Elasticsearch's K-NN index finds semantically proximate documents even when vocabulary does not overlap — a query for "outdoor activities" can surface "hiking meetup in the park" without a shared keyword. This capability runs entirely within the Docker environment with no external embedding API required.

The two result sets are merged and re-ranked by a linear combination of BM25 score and cosine similarity.

### 6.3 JWT-Based Stateless Authentication

The platform authenticates all requests through JSON Web Tokens issued by the jjwt 0.12.6 library. Tokens are self-contained: the email and role claims embedded in the signed payload are sufficient for all authorisation decisions, eliminating the need for a shared session store. The system can therefore scale horizontally without session affinity. Role-based access control is enforced declaratively through Spring Security's `@PreAuthorize` annotations at the controller method level, avoiding database lookups on every request.

In the Microservices architecture the API Gateway validates the JWT signature and expiry once per inbound request. Downstream services trust the pre-validated claims forwarded by the gateway, avoiding redundant verification on every inter-service hop.

### 6.4 PostgreSQL as Authoritative Source of Truth

Elasticsearch functions as a read-optimised secondary index rather than a primary data store. All write operations target PostgreSQL; the Elasticsearch index is populated asynchronously by the ingestion subsystem. This has three consequences:

- **Recoverability** — if the Elasticsearch index is corrupted or falls out of synchronisation, it can be rebuilt in full from PostgreSQL by invoking `/api/search/reindex`. No data is permanently lost.
- **Transactional integrity** — RSVP booking, group membership updates, and all other state-changing operations are committed to PostgreSQL under ACID semantics. Elasticsearch's eventual consistency is acceptable for search results, which are inherently approximate by nature.
- **Write path simplicity** — dual-write strategies introduce a failure mode where one store is updated and the other is not. The asynchronous ingest pattern eliminates this class of failure at the cost of a small, bounded propagation delay.

### 6.5 Angular 21 with Standalone Components

The frontend application uses the standalone component model that became the Angular default from version 17 onward. Standalone components declare their own import graphs directly rather than delegating to an NgModule, which makes each component's dependencies explicit and locally visible. Angular 21's reactive signal primitives replace RxJS BehaviorSubject for local component state management, reducing subscription lifecycle complexity and producing more efficient change detection. The application source is organised by feature domain (social-event, search, profile) rather than by artefact type, keeping related components, services, and route declarations co-located.

### 6.6 Database-per-Service Pattern

Each microservice owns exactly one PostgreSQL database. This enforces strict service autonomy: the Event Service can evolve its schema independently of the Notification Service without requiring a coordinated migration. The primary trade-off is that queries requiring data from more than one service domain must be resolved through an HTTP call rather than a SQL JOIN, introducing a small latency cost and a network failure mode. The team determined this cost is acceptable given the scalability, fault-isolation, and independent-deployment benefits the pattern enables.
