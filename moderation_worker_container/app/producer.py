# app/producer.py
from kafka import KafkaProducer
import json
from app.dto.video_update import UpdateRequestDTO,UpdateType
from app.dto.moderation_result import ModerationResult
from app.config import KAFKA_HOST
producer = KafkaProducer(
    bootstrap_servers=KAFKA_HOST,
    value_serializer=lambda v: json.dumps(v).encode("utf-8")
)

def send_moderation_event(video_id,report):
   producer.send(
      "video-updates",
    UpdateRequestDTO(
       video_id=video_id,
      type="MODERATION",
      moderation_result=report
    )
   )
