# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (cache optimization)
COPY common-lib ./common-lib
COPY user ./user_service

WORKDIR /app/common-lib
RUN mvn clean install -DskipTests

WORKDIR /app/user_service
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17
WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=build /app/user_service/target/*.jar app.jar

# Set timezone if you want
ENV TZ=Asia/Kolkata

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]
