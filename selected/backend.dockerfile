# stage: build
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app
COPY eventual.backend/pom.xml .
RUN mvn dependency:go-offline
COPY eventual.backend/src ./src

RUN mvn clean package -DskipTests

# stage: serve
FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
