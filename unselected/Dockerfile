# ──────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build the Spring Boot fat-jar with Maven
# ──────────────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-23 AS build

WORKDIR /app

# Copy Maven manifest first so dependency layer is cached separately
COPY eventual.backend/pom.xml ./pom.xml

# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -q

# Copy source and build
COPY eventual.backend/src ./src
RUN mvn clean package -DskipTests -q

# ──────────────────────────────────────────────────────────────────────────────
# Stage 2 — Slim runtime image
# ──────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:23-jre-noble

WORKDIR /app

# Create uploads directory for profile/event pictures
RUN mkdir -p /app/uploads

# Copy the fat-jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
