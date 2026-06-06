FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# copy both projects
COPY common-lib ./common-lib
COPY event_service ./event_service

# build common lib first
RUN cd common-lib && mvn clean install

# then build main service
RUN cd event_service && mvn clean package -DskipTests

# runtime image
FROM eclipse-temurin:17

WORKDIR /app

COPY --from=build /app/event_service/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]