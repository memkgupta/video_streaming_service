const { Kafka } = require('kafkajs');

const kafka = new Kafka({
  clientId: 'rtmp-server-consumer',
  brokers: process.env.KAFKA_BROKERS ? process.env.KAFKA_BROKERS.split(',') : ['localhost:29092']
});

const consumer = kafka.consumer({ groupId: 'rtmp-block-group' });

async function initKafkaConsumer(onBlockMedia) {
  try {
    await consumer.connect();
    console.log('[Kafka] Consumer connected');

    await consumer.subscribe({ topic: 'block-media', fromBeginning: false });

    await consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        if (topic === 'block-media') {
          try {
            const payload = JSON.parse(message.value.toString());
            const { mediaId, timestamp } = payload;

            if (mediaId && onBlockMedia) {
              await onBlockMedia(mediaId);
            }
          } catch (e) {
            console.error('[Kafka] Error parsing message:', e);
          }
        }
      },
    });
  } catch (error) {
    console.error('[Kafka] Consumer initialization failed:', error);
  }
}

module.exports = { initKafkaConsumer };
