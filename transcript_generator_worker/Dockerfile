FROM python:3.10-slim

# Install ffmpeg (required for audio extraction)
RUN apt-get update && apt-get install -y ffmpeg

# Set working directory
WORKDIR /app

# Copy worker code
COPY worker.py .

# Install dependencies inside container
RUN pip install --no-cache-dir openai-whisper \
    && pip install --no-cache-dir torch --index-url https://download.pytorch.org/whl/cpu \
    && pip install --no-cache-dir boto3 requests

# Environment-safe temp folder
RUN mkdir -p /tmp/chunks

# Run worker
ENTRYPOINT ["python", "worker.py"]
