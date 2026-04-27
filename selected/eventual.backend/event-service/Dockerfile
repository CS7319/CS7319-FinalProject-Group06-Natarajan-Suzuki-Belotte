FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY event-service/pom.xml ./event-service/pom.xml

RUN mvn dependency:go-offline -pl event-service -q

COPY event-service/src ./event-service/src
RUN mvn clean package -pl event-service -DskipTests -q

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

RUN mkdir -p /app/uploads

COPY --from=build /app/event-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
