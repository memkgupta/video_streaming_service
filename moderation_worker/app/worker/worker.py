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
        self.rabbitmq_url = os.environ.get("RABBITMQ_URL", "amqp://guest:guest@rabbitmq_q4:5672/")
        self.rabbitmq_queue = os.environ.get("RABBITMQ_QUEUE", "moderation_queue")
        
        self.kafka_broker = os.environ.get("KAFKA_BROKER", "kafka_q4:9092")
        self.kafka_topic = os.environ.get("KAFKA_TOPIC", "media-moderation-update")
        
        self.consumer = RabbitMQConsumer(url=self.rabbitmq_url, queue=self.rabbitmq_queue)
        self.producer = ModerationResultProducer(broker=self.kafka_broker, topic=self.kafka_topic)
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
            # print("result", result)
            # Push to Kafka
            kafka_event = result.to_kafka_event()
            logger.info(f"Pushing result to Kafka for content {job.asset_id}")
            self.producer.send(message=kafka_event, key=job.asset_id)
            
            # Acknowledge message
            ch.basic_ack(delivery_tag=method.delivery_tag)
            
        except Exception as e:
            logger.error(f"Error processing message: {e}")
            # Requeue=False sends it to DLQ if configured, otherwise drops it.
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

    def start(self):
        logger.info("Starting Moderation Worker...")
        self.consumer.start(callback=self.process_message)

if __name__ == "__main__":
    worker = ModerationWorker()
    worker.start()
