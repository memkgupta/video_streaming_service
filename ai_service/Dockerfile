FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
# copy both projects from root context
COPY common-lib ./common-lib
COPY ai_service ./ai_service
# build common lib first
WORKDIR /app/common-lib
RUN mvn clean install -DskipTests
# build main service
WORKDIR /app/ai_service
RUN mvn clean package -DskipTests
# runtime image
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/ai_service/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]