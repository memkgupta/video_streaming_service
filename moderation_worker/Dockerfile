FROM python:3.11-slim

# Install system dependencies required for OpenCV and ffmpeg
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libsm6 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/app

# Copy and install python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application source code
COPY app/ ./app/

# Set the Python path so local modules inside 'app' can be found
ENV PYTHONPATH=/usr/src/app/app

# Command to run the worker
CMD ["python", "app/worker/worker.py"]
