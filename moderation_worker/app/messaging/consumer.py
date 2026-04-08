import pika


class RabbitMQConsumer:
    def __init__(self, url: str, queue: str):
        self.url = url
        self.queue = queue

        params = pika.URLParameters(self.url)
        self.connection = pika.BlockingConnection(params)
        self.channel = self.connection.channel()

        self.channel.queue_declare(queue=self.queue, durable=True)

        # fair dispatch
        self.channel.basic_qos(prefetch_count=1)

    def start(self, callback):
        self.channel.basic_consume(
            queue=self.queue,
            on_message_callback=callback
        )

        print("🚀 Waiting for messages...")
        self.channel.start_consuming()