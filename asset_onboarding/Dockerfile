FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# copy both projects
COPY common-lib ./common-lib
COPY asset_onboarding ./asset_onboarding

# build common lib first
RUN cd common-lib && mvn clean install

# then build main service
RUN cd asset_onboarding && mvn clean package -DskipTests

# runtime image
FROM eclipse-temurin:17

WORKDIR /app

COPY --from=build /app/asset_onboarding/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]