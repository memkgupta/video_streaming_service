const Docker = require('dockerode');
const Secrets = require('./secrets');
const docker = new Docker(); // connects to /var/run/docker.sock

async function spawnContainer(streamId,assetId,encryptionKey ) {
  // Pull image first


  // Create and start container
   const container = await docker.createContainer({
        Image: 'transcoding_container:latest',
        name: `live_transcoder-${streamId}`,

        // VERY IMPORTANT
        AttachStdin:  true,
        AttachStdout: true,
        AttachStderr: true,
        OpenStdin:    true,
        Tty:          false,

        Env: [
            `MEDIA_ID=${streamId}`,
            `ENCRYPTION_KEY=${encryptionKey}`,
            `MEDIA_TYPE=LIVE`,
            `ASSET_ID=${assetId}`,
            `BUCKET_NAME=springbucketdemo`,
            `FILE_KEY=rtmp://host.docker.internal:1935/live/${streamId}`,
            `TRANSCODED_BUCKET_NAME=${Secrets.AWS_TRANSCODED_BUCKET_NAME}`,
            `ACCESS_KEY=${Secrets.AWS_ACCESS_KEY_ID}`,
            `SECRET_KEY=${Secrets.AWS_SECRET_KEY}`,
            `KAFKA_BROKERS=kafka:9092`,
            `PUBLIC_KEY_URL=http://host.docker.internal:8081/v1/key/${assetId}`,
            `UPDATE_TOPIC_NAME=asset-transcoding-updates`,
            `CLOUDFRONT_URL=${Secrets.CLOUD_FRONT_URL}`,
        ],

        HostConfig: {
            NetworkMode: 'url_shortener_backend_app-net',
            AutoRemove:  true,
        },
    });

  await container.start();
  console.log('Container started:', container.id);

  // Get logs
  const logs = await container.logs({ stdout: true, stderr: true, tail: 50 });
  console.log(logs.toString());

  // Stop and remove
 return container;
}



module.exports = {spawnContainer}