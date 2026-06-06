import json
import logging
import sys
import shutil
import pika
import os
import tempfile
from src.config import settings
from src.models.job import TranscriptionJob
from src.utils.AudioExtractor import AudioExtractor
from src.transcription import whisper_service
from src.utils.transcript_generator import TranscriptEventFactory
from src.storage.s3_service import S3Service
from src.producer.producer import TranscriptProducer
import httpx
import asyncio
from pathlib import Path
# Configure logging to stdout
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s - %(message)s",
    handlers=[logging.StreamHandler(sys.stdout)]
)
logger = logging.getLogger("transcriber_worker")

logger.info("Starting Transcriber Worker...")

# Establish a connection to RabbitMQ
logger.info(f"Connecting to RabbitMQ at {settings.queue_server}:{settings.queue_port}...")
connection = pika.BlockingConnection(
    pika.ConnectionParameters(
        host=settings.queue_server,
        port=int(settings.queue_port)
    )
)
logger.info("Connection established with RabbitMQ")

channel = connection.channel()
channel.queue_declare(queue=settings.queue_job_name, durable=True)

logger.info("Initializing WhisperService (loading model)...")
whisper = whisper_service.WhisperService()
logger.info("WhisperService initialized successfully.")

logger.info("Initializing S3Service...")
s3_service = S3Service()
logger.info("S3Service initialized successfully.")

logger.info("Initializing TranscriptProducer...")
producer = TranscriptProducer()
logger.info("TranscriptProducer initialized successfully.")
bucket = settings.bucket_name

async def download_audio_chunk(url: str, output_path: str) -> Path:
    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)

    async with httpx.AsyncClient() as client:
        async with client.stream("GET", url) as response:
            response.raise_for_status()
            with open(output, "wb") as f:
                async for chunk in response.aiter_bytes(chunk_size=8192):
                    f.write(chunk)

    return output

async def process_job(job):
    asset_id = "unknown"
    temp_dir = None
    try:
        
        asset_id = job.asset_id
        logger.info(
            f"Received job - JobID: {job.asset_id}, VideoID: {job.media_id}, ChunkNumber: {job.chunk_number} , chunk_url :{job.chunk_url}"
        )
        temp_dir = tempfile.mkdtemp()
        # Create local temp directory
        filename  = Path(job.asset_id,f"{job.chunk_number}").name                        # 0_30_000.mp3
        audio_path = Path(temp_dir) / filename                      # /tmp/abc123/0_30_000.mp3

        await download_audio_chunk(url=job.chunk_url, output_path=str(audio_path))

# 2. Transcribe using Whisper
        logger.info(f"[{job.asset_id}] Running Whisper transcription...")
        segments = whisper.transcribe(audio_path=str(audio_path))
        logger.info(f"[{job.asset_id}] Whisper transcription completed. Generated {len(segments)} segments.")

        # 4. Create transcript event
        logger.info(f"[{job.asset_id}] Creating transcript event...")
        transcript = TranscriptEventFactory.create(job, segments)

        # Save this chunk's transcript JSON to S3 for consolidation later
        chunk_key = f"transcripts/{job.asset_id}/chunk_{job.chunk_number}.json"
        chunk_data = {
            "transcript": transcript.transcript,
            "segments": [
                {
                    "start": round(segment.start + job.start_time, 3),
                    "end": round(segment.end + job.start_time, 3),
                    "text": segment.text.strip()
                }
                for segment in segments
            ]
        }
        s3_service.upload_json(bucket, chunk_key, chunk_data)


        # 5. Publish transcript event to Kafka
        logger.info(f"[{job.asset_id}] Publishing transcript event to Kafka...")
        producer.publish(transcript)

        # 6. Acknowledge RabbitMQ message
      
        logger.info(f"[{job.asset_id}] Message acknowledged successfully.")

    except Exception as e:
        logger.exception(f"[{asset_id}] Transcription failed processing job")
        # Nack the message so it can be retried or sent to a dead-letter queue
       
    finally:
        if temp_dir:
            try:
                shutil.rmtree(temp_dir)
                logger.info(f"[{asset_id}] Cleaned up temp directory: {temp_dir}")
            except Exception as e:
                logger.warning(f"[{asset_id}] Failed to clean up temp directory {temp_dir}: {e}")
# Configure QoS (prefetch count = 1)

logger.info(f"Waiting for jobs on queue '{settings.queue_job_name}'...")
def process_message(ch, method, properties, body):      
    asset_id = "unknown"
    try:
        job_dict = json.loads(body.decode("utf-8"))
        job      = TranscriptionJob.model_validate(job_dict)
        asset_id = job.asset_id

        logger.info(f"[{asset_id}] Received job. media_id={job.media_id}, chunk={job.chunk_number}")

        asyncio.run(process_job(job))                       # ✅ bridge sync → async

        ch.basic_ack(delivery_tag=method.delivery_tag)
        logger.info(f"[{asset_id}] Message acknowledged.")

    except Exception as e:
        logger.exception(f"[{asset_id}] Job failed")
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)
        logger.warning(f"[{asset_id}] Message nacked and requeued.")



channel.basic_qos(prefetch_count=1)
channel.basic_consume(
    queue=settings.queue_job_name,
    on_message_callback=process_message,
    auto_ack=False
)

logger.info(f"Waiting for jobs on queue '{settings.queue_job_name}'...")
channel.start_consuming()
