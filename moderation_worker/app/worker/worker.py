import json
import os
import logging
from typing import Any

from messaging.consumer import RabbitMQConsumer
from messaging.producer import ModerationResultProducer
from pipeline.pipeline import ModerationPipeline
from dtos.dtos import ModerationJob

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class ModerationWorker:
    def __init__(self):
        # ------------------------
        # RabbitMQ Config
        # ------------------------
        rabbitmq_host = os.environ.get("RABBITMQ_HOST", "rabbitmq")
        rabbitmq_port = os.environ.get("RABBITMQ_PORT", "5672")
        rabbitmq_user = os.environ.get("RABBITMQ_USERNAME", "guest")
        rabbitmq_pass = os.environ.get("RABBITMQ_PASSWORD", "guest")

        self.rabbitmq_queue = os.environ.get("RABBITMQ_QUEUE", "moderation_queue")

        # Build URL (standardized)
        self.rabbitmq_url = f"amqp://{rabbitmq_user}:{rabbitmq_pass}@{rabbitmq_host}:{rabbitmq_port}/"

        # ------------------------
        # Kafka Config
        # ------------------------
        self.kafka_bootstrap_servers = os.environ.get(
            "KAFKA_BOOTSTRAP_SERVERS", "kafka:9092"
        )

        self.kafka_topic = os.environ.get(
            "KAFKA_TOPIC", "media-moderation-update"
        )

        # ------------------------
        # Clients
        # ------------------------
        self.consumer = RabbitMQConsumer(
            url=self.rabbitmq_url,
            queue=self.rabbitmq_queue
        )

        self.producer = ModerationResultProducer(
            broker=self.kafka_bootstrap_servers,
            topic=self.kafka_topic
        )

        self.pipeline = ModerationPipeline()

    def process_message(self, ch, method, properties, body):
        try:
            # Parse message
            message_dict = json.loads(body)
            logger.info(f"Received message: {message_dict}")

            job = ModerationJob(**message_dict)

            video_url = job.content_url

            # Run pipeline
            logger.info(f"Running pipeline for job {job.job_id}, content {job.asset_id}")
            result = self.pipeline.run(
                video_id=job.asset_id,
                video_url=video_url,
                job_id=job.job_id,
                asset_id=job.asset_id,
                content_type=job.content_type
            )

            # Push to Kafka
            kafka_event = result.to_kafka_event()
            logger.info(f"Pushing result to Kafka for content {job.asset_id}")

            self.producer.send(
                message=kafka_event,
                key=job.asset_id
            )

            # Acknowledge message
            ch.basic_ack(delivery_tag=method.delivery_tag)

        except Exception as e:
            logger.error(f"Error processing message: {e}")

            # Send to DLQ / drop
            ch.basic_nack(
                delivery_tag=method.delivery_tag,
                requeue=False
            )

    def start(self):
        logger.info("Starting Moderation Worker...")
        self.consumer.start(callback=self.process_message)


if __name__ == "__main__":
    worker = ModerationWorker()
    worker.start()