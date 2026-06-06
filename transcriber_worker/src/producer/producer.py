import json
import logging

from kafka import KafkaProducer

from src.models.transcript import TranscriptEvent
from src.config import settings

logger = logging.getLogger(__name__)


class TranscriptProducer:

    def __init__(self):
        self.producer = KafkaProducer(
            bootstrap_servers=settings.kafka_bootstrap_servers,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            acks="all",
            retries=5
        )

    def publish(self, event: TranscriptEvent):

        future = self.producer.send(
            settings.kafka_transcript_topic,
            key=event.media_id.encode("utf-8"),
            value=event.model_dump()
        )

        metadata = future.get(timeout=10)

        logger.info(
            "Transcript event published",
            extra={
                "topic": metadata.topic,
                "partition": metadata.partition,
                "offset": metadata.offset,
                "video_id": event.media_id
            }
        )

    def close(self):
        self.producer.flush()
        self.producer.close()