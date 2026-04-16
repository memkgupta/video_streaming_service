const Docker = require('dockerode');
const Secrets = require('./secrets');
const docker = new Docker(); // connects to /var/run/docker.sock

async function spawnContainer(streamId, assetId, encryptionKey, moderation) {
    // Pull image first


    // Create and start container
    const container = await docker.createContainer({
        Image: 'transcoding_container:latest',
        name: `live_transcoder-${streamId}`,

        // VERY IMPORTANT
        AttachStdin: true,
        AttachStdout: true,
        AttachStderr: true,
        OpenStdin: true,
        Tty: false,

        Env: [
            `MEDIA_ID=${streamId}`,
            `ENCRYPTION_KEY=${encryptionKey}`,
            `MEDIA_TYPE=LIVE`,
            `ASSET_ID=${assetId}`,
            `MODERATION=${moderation}`,
            `FILE_KEY=${Secrets.SERVER_URL}/live/${streamId}`,
            `BUCKET_NAME=${Secrets.AWS_RAW_BUCKET_NAME}`,
            `TRANSCODED_BUCKET_NAME=${Secrets.AWS_TRANSCODED_BUCKET_NAME}`,
            `ACCESS_KEY=${Secrets.AWS_ACCESS_KEY_ID}`,
            `SECRET_KEY=${Secrets.AWS_SECRET_KEY}`,
            `KAFKA_BROKERS=${Secrets.KAFKA_BROKERS}`,
            `PUBLIC_KEY_URL=http://${Secrets.KEY_SERVER_URL}/v1/key/${assetId}`,
            `UPDATE_TOPIC_NAME=asset-transcoding-updates`,
            `CLOUDFRONT_URL=${Secrets.CLOUD_FRONT_URL}`,
        ],

        HostConfig: {
            NetworkMode: 'q4-video_app-net',

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



module.exports = { spawnContainer }