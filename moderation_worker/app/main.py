import json
import traceback

from pipeline.pipeline import ModerationPipeline
from messaging.consumer import RabbitMQConsumer
from messaging.producer import ModerationResultProducer


RABBITMQ_URL = "amqp://guest:guest@rabbitmq_q4:5672/"
QUEUE_NAME = "moderation_jobs"

KAFKA_BROKER = "kafka_q4:9092"
KAFKA_TOPIC = "media-moderation-updates"


pipeline = ModerationPipeline()
producer = ModerationResultProducer(
    broker=KAFKA_BROKER,
    topic=KAFKA_TOPIC
)


def process_message(ch, method, properties, body):
    try:
        data = json.loads(body)

        video_id = data["jobId"]
        video_url = data["video_url"]

        print(f"\n Processing video: {video_id}")


        result = pipeline.run(
            video_id=video_id,
            video_url=video_url
        )

        output = {
            "video_id": video_id,
            "result": result
        }

      
        producer.send(output, key=video_id)

        print(f" Completed: {video_id}")

        ch.basic_ack(delivery_tag=method.delivery_tag)

    except Exception as e:
        print(" Error processing message")
        traceback.print_exc()

        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)



if __name__ == "__main__":
    consumer = RabbitMQConsumer(
        url=RABBITMQ_URL,
        queue=QUEUE_NAME
    )

    consumer.start(process_message)