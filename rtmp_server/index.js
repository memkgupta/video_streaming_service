require('dotenv').config();
const NodeMediaServer = require('node-media-server');
const { Kafka } = require('kafkajs');
const { spawnContainer } = require('./docker');


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
    const container = await spawnContainer(streamId);
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
    containerMap.delete(streamId);
    await container.stop();
    
  console.log('[NodeEvent on donePublish]', `id=${id} StreamPath=${StreamPath}`);
});
  
}

startServer();