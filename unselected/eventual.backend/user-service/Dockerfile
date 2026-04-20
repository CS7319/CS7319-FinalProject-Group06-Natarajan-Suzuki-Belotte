FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY user-service/pom.xml ./user-service/pom.xml

RUN mvn dependency:go-offline -pl user-service -q

COPY user-service/src ./user-service/src
RUN mvn clean package -pl user-service -DskipTests -q

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

RUN mkdir -p /app/uploads

COPY --from=build /app/user-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
