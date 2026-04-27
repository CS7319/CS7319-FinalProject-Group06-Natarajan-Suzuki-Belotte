FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY vendor-service/pom.xml ./vendor-service/pom.xml

RUN mvn dependency:go-offline -pl vendor-service -q

COPY vendor-service/src ./vendor-service/src
RUN mvn clean package -pl vendor-service -DskipTests -q

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

COPY --from=build /app/vendor-service/target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
