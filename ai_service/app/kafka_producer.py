from kafka import KafkaProducer
import json

producer = KafkaProducer(
    bootstrap_servers="localhost:9092",
    value_serializer=lambda v: json.dumps(v).encode("utf-8"),
    retries=5
)

topic = "video-transcripts"

def sendMessage(message):

    producer.send(topic, message)
    producer.flush()

print("Message sent to Kafka")
