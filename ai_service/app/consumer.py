import json
import pika
import logging
from app.config import (
    RABBITMQ_HOST,
    RABBITMQ_PORT,
    RABBITMQ_QUEUE,
    RABBITMQ_USERNAME,
    RABBITMQ_PASSWORD,
    PREFETCH_COUNT
)
from app.services.transcriber import transcribe_video
from app.DockerSpawnerFactory import DockerSpawnerFactory
logger = logging.getLogger(__name__)


def start_consumer():
    """Starts RabbitMQ consumer and blocks forever"""

    credentials = pika.PlainCredentials(
        RABBITMQ_USERNAME,
        RABBITMQ_PASSWORD
    )

    parameters = pika.ConnectionParameters(
        host=RABBITMQ_HOST,
        port=RABBITMQ_PORT,
        credentials=credentials
    )

    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()

    # Ensure queue exists
    channel.queue_declare(
        queue=RABBITMQ_QUEUE,
        durable=True
    )

    # Process only one message at a time
    channel.basic_qos(prefetch_count=PREFETCH_COUNT)

    def callback(ch, method, properties, body):
        try:
            job = json.loads(body)
            logger.info(f"Received job: {job}")

            # Business logic
            spawner = DockerSpawnerFactory.get_spawner()
            spawner.spawn(job)

            # ACK only after success
            ch.basic_ack(delivery_tag=method.delivery_tag)
            logger.info("Job processed successfully")

        except Exception as e:
            logger.exception("Job processing failed")

            # Requeue message (retry)
            ch.basic_nack(
                delivery_tag=method.delivery_tag,
                requeue=True
            )

    channel.basic_consume(
        queue=RABBITMQ_QUEUE,
        on_message_callback=callback
    )

    logger.info("Python RabbitMQ consumer started")
    channel.start_consuming()
