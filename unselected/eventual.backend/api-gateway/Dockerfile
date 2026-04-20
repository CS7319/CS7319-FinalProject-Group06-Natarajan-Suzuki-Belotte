FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy parent pom first so the reactor is resolvable
COPY pom.xml ./pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml

# Download dependencies (cached unless pom files change)
RUN mvn dependency:go-offline -pl api-gateway -q

# Copy source and build
COPY api-gateway/src ./api-gateway/src
RUN mvn clean package -pl api-gateway -DskipTests -q

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

COPY --from=build /app/api-gateway/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
