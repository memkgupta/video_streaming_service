const path = require('path');

require('dotenv').config({ path: path.resolve(__dirname, '../.env') });
const NodeMediaServer = require('node-media-server');
const { spawnContainer } = require('./docker');
const { default: axios } = require('axios');
const { initKafkaConsumer } = require('./kafkaClient');
const Secrets = require('./secrets')
const config = {
  logType: 3,
  rtmp: {
    port: 1935,
  },
  http: {
    port: 8000,
    mediaroot: './media',
    allow_origin: '*',
  },
};

const nms = new NodeMediaServer(config);
const containerMap = new Map();
const mediaIdToSessionId = new Map();
const blockedMediaIds = new Set();

async function startServer() {
  nms.run();

  initKafkaConsumer(async (mediaId) => {
    console.log(`[BlockMedia] Received block request for mediaId: ${mediaId}`);
    
    // Add to permanently blocked list
    blockedMediaIds.add(mediaId);

    // Stop RTMP connection instantly
    const sessionId = mediaIdToSessionId.get(mediaId);
    if (sessionId) {
      const session = nms.getSession(sessionId);
      if (session) {
        console.log(`[BlockMedia] Rejecting RTMP session ${sessionId}`);
        session.reject();
      }
      mediaIdToSessionId.delete(mediaId);
    }

    // Stop the running container mapped with this mediaId
    const container = containerMap.get(mediaId);
    if (container) {
      containerMap.delete(mediaId);
      await container.stop().catch((e) =>
        console.error('[BlockMedia] Failed to stop container', e.message)
      );
    }
  });

  nms.on('prePublish', (id, StreamPath, args) => {
    const mediaId = StreamPath.split('/')[2];
    
    if (mediaId && blockedMediaIds.has(mediaId)) {
      console.log(`[prePublish] Rejecting blocked mediaId: ${mediaId}`);
      const session = nms.getSession(id);
      if (session) {
        session.reject();
      }
    }
  });

  nms.on('postPublish', async (id, StreamPath, args) => {
    // StreamPath: /{app}/{mediaId}   query: ?pushKey=xxx
    const mediaId = StreamPath.split('/')[2];
    const pushKey = args?.pushKey;

    if (!mediaId || !pushKey) {
      console.error('[postPublish] Missing mediaId or pushKey', { StreamPath, args });
      return;
    }

    mediaIdToSessionId.set(mediaId, id);

    console.log('[NodeEvent on postPublish]', `id=${id} StreamPath=${StreamPath}`);

    if (containerMap.has(mediaId)) return;

    try {
      const { data: liveStart } = await axios.post(
        `${Secrets.LIVE_SERVER_URL}/${mediaId}`,
        { mediaId },
        { headers: { 'X-PUSH-KEY': pushKey } }
      );

      console.log('[postPublish] liveStart', liveStart);

      const container = await spawnContainer(mediaId, liveStart.assetId, liveStart.encryptionKey, liveStart.moderationEnabled);
      containerMap.set(mediaId, container);
    } catch (err) {
      console.error('[postPublish] Failed to start live stream', err?.response?.data ?? err.message);
    }
  });

  nms.on('donePublish', async (id, StreamPath, args) => {
    // StreamPath: /{app}/{mediaId}   query: ?pushKey=xxx
    const mediaId = StreamPath.split('/')[2];
    const pushKey = args?.pushKey;

    if (!mediaId || !pushKey) {
      console.error('[donePublish] Missing mediaId or pushKey', { StreamPath, args });
      return;
    }

    console.log('[NodeEvent on donePublish]', `id=${id} StreamPath=${StreamPath}`);

    mediaIdToSessionId.delete(mediaId);

    const container = containerMap.get(mediaId);
    if (!container) {
      console.error('[donePublish] No container found for mediaId:', mediaId);
      return;
    }

    try {
      await axios.put(
        `${Secrets.LIVE_SERVER_URL}/end/${mediaId}`,
        {},
        { headers: { 'X-PUSH-KEY': pushKey } }
      );
    } catch (err) {
      console.error('[donePublish] Failed to call live end API', err?.response?.data ?? err.message);
    } finally {
      containerMap.delete(mediaId);
      await container.stop().catch((e) =>
        console.error('[donePublish] Failed to stop container', e.message)
      );
    }
  });
}

startServer();