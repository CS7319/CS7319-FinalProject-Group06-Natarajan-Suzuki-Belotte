FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY search-service/pom.xml ./search-service/pom.xml

RUN mvn dependency:go-offline -pl search-service -q

COPY search-service/src ./search-service/src
RUN mvn clean package -pl search-service -DskipTests -q

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

COPY --from=build /app/search-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
