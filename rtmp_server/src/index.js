require('dotenv').config();
const NodeMediaServer = require('node-media-server');
const { Kafka } = require('kafkajs');
const { spawnContainer } = require('./docker');
const { default: axios } = require('axios');


console.log(process.env)
// const producer = kafka.producer();

const config = {

  logType: 3,

  rtmp: {
    port: 1935,
  },

  http: {
    port: 8000,
    mediaroot: './media',
    allow_origin: '*'
  },



};

const nms = new NodeMediaServer(config);
const containerMap = new Map();


async function startServer() {


  nms.run();
  nms.on('postPublish', async(id, StreamPath, args) => {
    const streamId = StreamPath.split("/")[2];
   if(!containerMap.has(streamId))
   {
    const req = await axios.post(`http://localhost:8081/live/${streamId}`,{mediaId:streamId});
    const liveStart = req.data;
    
    const container = await spawnContainer(streamId,liveStart.assetId , liveStart.encryptionKey);
    containerMap.set(streamId,container)
   }
  console.log('[NodeEvent on postPublish]', `id=${id} StreamPath=${StreamPath}`);
});

nms.on('donePublish', async(id, StreamPath, args) => {
    const streamId = StreamPath.split("/")[2];

   const container =  containerMap.get(streamId);
    if(!container)
    {
      throw new Error("Container not found")
    }
     const req = await axios.put(`http://localhost:8081/live/end/${streamId}`);
    containerMap.delete(streamId);
    await container.stop();
    
  console.log('[NodeEvent on donePublish]', `id=${id} StreamPath=${StreamPath}`);
});
  
}

startServer();