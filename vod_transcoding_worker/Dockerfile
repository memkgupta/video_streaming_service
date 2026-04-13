# Use OpenJDK base image
FROM eclipse-temurin:17-jdk

# Install ffmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean

# Create app directory inside container
WORKDIR /app

# Copy your JAR file into the container
COPY target/vod_transcoding_worker-1.0.0.jar app.jar

# Expose a port (optional - if your app serves HTTP)
# EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]