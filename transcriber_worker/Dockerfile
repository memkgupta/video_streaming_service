# ──────────────────────────────────────────────────────────────────────
# Dockerfile for Transcriber Worker
# ──────────────────────────────────────────────────────────────────────

# ── Stage 1: Builder ──────────────────────────────────────────────────
FROM python:3.11-slim AS builder

WORKDIR /app

# Install build dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    gcc \
    g++ \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Install uv for fast, reliable package installations
RUN pip install --no-cache-dir --upgrade pip uv

# Copy configuration files first for optimal layer caching
COPY pyproject.toml uv.lock ./

# Compile dependencies to a standard requirements.txt and install them
RUN uv pip compile pyproject.toml -o requirements.txt \
    && uv pip install --system --no-cache -r requirements.txt

# ── Stage 2: Runtime ──────────────────────────────────────────────────
FROM python:3.11-slim AS runtime

WORKDIR /app

# Install runtime dependencies:
# - ffmpeg: Required for audio extraction and conversion
# - libsndfile1: Required by various audio libraries (e.g. soundfile)
# - curl: For health checks and debugging
RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    libsndfile1 \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy installed packages and binaries from builder
COPY --from=builder /usr/local/lib/python3.11/site-packages /usr/local/lib/python3.11/site-packages
COPY --from=builder /usr/local/bin /usr/local/bin

# Environment configuration
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    HF_HOME=/app/models

# Create a non-root user and setup directories
RUN groupadd --gid 1001 worker \
    && useradd --uid 1001 --gid worker --shell /bin/bash --create-home worker \
    && mkdir -p /app/logs /app/temp /app/models \
    && chown -R worker:worker /app

# Copy application source code
COPY --chown=worker:worker src/ ./src/
COPY --chown=worker:worker pyproject.toml .

# Switch to the non-root user
USER worker

# Pre-download the Whisper 'small' model during build time
# to avoid runtime latency and external dependencies on startup
RUN python -c "from faster_whisper import WhisperModel; WhisperModel('small', device='cpu', compute_type='float32')"

# Start the worker
CMD ["python", "-m", "src.main"]