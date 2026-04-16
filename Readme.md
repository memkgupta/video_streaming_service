# 🎥 Video Streaming Service

A video streaming backend supporting both **Video on Demand (VOD)** and **Live Streaming**.

This project provides APIs that allow platforms to seamlessly integrate video ingestion, processing, and delivery into their applications.

---

## 🚧 Project Status

This project is currently under active development.  
You may encounter bugs or incomplete edge-case handling.

---

## 🧠 Architecture & Design

For a detailed breakdown of the system design and architecture, check out this article:

👉 https://mkguptaweb.hashnode.dev/designing-a-video-processing-pipeline

---

## ⚙️ Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/memkgupta/video_streaming_service
cd video_streaming_service
```

## 2. Start services using Docker

```bash
docker-compose up -d --build
```

## 3. Initial Setup
1. Create a User
2. Create an Organization
3. Generate Access Key and Secret
4. Use these credentials to authenticate API requests

## 🧪 Demo Application
A sample frontend integration (Next.js) is available here:

👉 https://github.com/memkgupta/video_streaming_service_demo_application

## 💡 Features 
1. Live Stream Ingestion
2. Video processing pipeline
3. Chunk-based streaming support
4. Scalable worker architecture
5. API-first design for integrations

## Planned
1. Scaling Live Streaming Ingestion
2. Security in Streaming using AES-128 Encryption
3. Refactor code for deployment
