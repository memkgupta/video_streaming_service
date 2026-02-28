FROM python:3.11-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

RUN apt-get update && apt-get install -y \
    build-essential \
    git \
    curl \
    ffmpeg \
    ca-certificates \
    libglib2.0-0 \
    libgl1 \
    libsm6 \
    libxrender1 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*

# upgrade pip
#fixing the version of setuptools worked 
RUN pip install --no-cache-dir --upgrade setuptools==69.5.1 wheel

# install torch first
RUN pip install --no-cache-dir \
    torch==2.1.2 torchvision==0.16.2 torchaudio==2.1.2 \
    --index-url https://download.pytorch.org/whl/cpu

RUN pip install --no-cache-dir ftfy regex tqdm

# install CLIP
RUN pip install --no-cache-dir git+https://github.com/openai/CLIP.git

# install app requirements
COPY app/requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

# copy app
COPY app/ app/

# copy model
COPY yolov8n.pt /app/yolov8n.pt

# security
RUN useradd -m workeruser
USER workeruser

CMD ["python", "app/main.py"]