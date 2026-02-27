# app/producer.py
from kafka import KafkaProducer
import json
from dto.video_update import UpdateRequestDTO,UpdateType
from dto.moderation_result import ModerationResult
from config import KAFKA_HOST
print(KAFKA_HOST)
from kafka import KafkaProducer
import json

_producer = None

def get_producer():
    global _producer
    if _producer is None:
        _producer = KafkaProducer(
            bootstrap_servers=KAFKA_HOST,
            value_serializer=lambda v: json.dumps(v.to_dict()).encode("utf-8"),
            retries=5,
            retry_backoff_ms=2000,
            request_timeout_ms=30000,
        )
    return _producer


def send_moderation_event(video_id,report):
   producer = get_producer()
   print( UpdateRequestDTO(
       videoId=video_id,
      type="MODERATION_UPDATE",
      moderation_result=report
    ))
   future = producer.send(
      "video-updates",
    UpdateRequestDTO(
       videoId=video_id,
      type="MODERATION_UPDATE",
      moderation_result=report
    )
   )
   metadata = future.get(timeout=10)

   print("✅ Event sent successfully")
   print("Topic:", metadata.topic)
   print("Partition:", metadata.partition)
   print("Offset:", metadata.offset)

