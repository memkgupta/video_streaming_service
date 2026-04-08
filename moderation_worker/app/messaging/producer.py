import json
from kafka import KafkaProducer


class ModerationResultProducer:
    def __init__(self, broker: str = "localhost:9092", topic: str = "moderation_results"):
        self.topic = topic

        self.producer = KafkaProducer(
            bootstrap_servers=broker,
            value_serializer=lambda v: json.dumps(v).encode("utf-8")
        )

    def send(self, message: dict, key: str = None):
        try:
            if key:
                self.producer.send(
                    self.topic,
                    key=key.encode("utf-8"),
                    value=message
                )
            else:
                self.producer.send(self.topic, message)

            self.producer.flush()

        except Exception as e:
            print(" Kafka send failed:", e)