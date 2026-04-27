FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY support-service/pom.xml ./support-service/pom.xml

RUN mvn dependency:go-offline -pl support-service -q

COPY support-service/src ./support-service/src
RUN mvn clean package -pl support-service -DskipTests -q

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

COPY --from=build /app/support-service/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
