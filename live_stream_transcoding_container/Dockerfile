FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && \
    apt-get install -y ffmpeg && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/live_stream_transcoding_container-1.0.0.jar app.jar

RUN mkdir /output

ENTRYPOINT ["java", "-jar", "app.jar"]
