// config/secrets.js

const Secrets = {
    AWS_SECRET_KEY:                    process.env.AWS_SECRET_KEY,
    AWS_ACCESS_KEY_ID:                 process.env.AWS_ACCESS_KEY_ID,
    AWS_RAW_BUCKET_NAME:               process.env.AWS_RAW_BUCKET_NAME,
    AWS_TRANSCODED_BUCKET_NAME:        process.env.AWS_TRANSCODED_BUCKET_NAME,
    DOCKER_TRANSCODER_CONTAINER_IMAGE: process.env.DOCKER_TRANSCODER_CONTAINER_IMAGE,
    CLOUD_FRONT_URL:                   process.env.CLOUD_FRONT_URL,
};

// // Validate all secrets are present on startup
// const missing = Object.entries(Secrets)
//     .filter(([_, value]) => !value)
//     .map(([key]) => key);

// if (missing.length > 0) {
//     throw new Error(`Missing required environment variables: ${missing.join(', ')}`);
// }

module.exports = Secrets;