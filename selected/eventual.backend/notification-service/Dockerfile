FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml

RUN mvn dependency:go-offline -pl notification-service -q

COPY notification-service/src ./notification-service/src
RUN mvn clean package -pl notification-service -DskipTests -q

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

COPY --from=build /app/notification-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
