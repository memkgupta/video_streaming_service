import os
from dotenv import load_dotenv
load_dotenv()
# ======================
# RabbitMQ Configuration
# ======================

RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "localhost")
RABBITMQ_PORT = int(os.getenv("RABBITMQ_PORT", 5672))
RABBITMQ_QUEUE = os.getenv("RABBITMQ_QUEUE", "summarization_jobs")
RABBITMQ_USERNAME = os.getenv("RABBITMQ_USERNAME", "guest")
RABBITMQ_PASSWORD = os.getenv("RABBITMQ_PASSWORD", "guest")

# ======================
# Whisper Configuration
# ======================

WHISPER_MODEL = os.getenv("WHISPER_MODEL", "medium")
WHISPER_LANGUAGE = os.getenv("WHISPER_LANGUAGE", "en")

# ======================
# Worker Configuration
# ======================

WORKER_NAME = os.getenv("WORKER_NAME", "python-transcriber")
PREFETCH_COUNT = int(os.getenv("PREFETCH_COUNT", 1))

VIDEO_S3_BUCKET = os.getenv("AWS_TRANSCODED_BUCKET_NAME","bucket")
TRANSCRIPT_S3_BUCKET = os.getenv("AWS_TRANSCODED_BUCKET_NAME")
TRANSCRIPT_CDN_URL = os.getenv("CLOUD_FRONT_URL")
WORKER_IMAGE = "whisper-worker"
CALLBACK_URL = "http://localhost:8000/transcript/callback"
# ======================
# Logging Configuration
# ======================

LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
