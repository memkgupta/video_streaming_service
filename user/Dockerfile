# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (cache optimization)
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=build /app/target/*.jar app.jar

# Set timezone if you want
ENV TZ=Asia/Kolkata

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]
