const Docker = require('dockerode');
const Secrets = require('./secrets');
const docker = new Docker(); // connects to /var/run/docker.sock

async function spawnContainer(streamId ) {
  // Pull image first


  // Create and start container
   const container = await docker.createContainer({
        Image: 'live_transcoder:latest',
        name: `live_transcoder-${streamId}`,

        // VERY IMPORTANT
        AttachStdin:  true,
        AttachStdout: true,
        AttachStderr: true,
        OpenStdin:    true,
        Tty:          false,

        Env: [
            `STREAM_KEY=${streamId}`,
            `S3_BUCKET=${Secrets.AWS_TRANSCODED_BUCKET_NAME}`,
            `AWS_ACCESS_KEY=${Secrets.AWS_ACCESS_KEY_ID}`,
            `AWS_SECRET_KEY=${Secrets.AWS_SECRET_KEY}`,
            `KAFKA_BOOTSTRAP=kafka:9092`,
            `KAFKA_TOPIC=stream-chunk-updates`,
            `CDN_BASE_URL=${Secrets.CLOUD_FRONT_URL}`,
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