const client = require("../api/apiClient");
const handleError = require("../error");
const apiClient = client("asset_onboarding","/v1")
/**
 * POST /live/{mediaId}
 * Starts a live stream — generates encryption key, moderation flag, and asset metadata
 * Called by RTMP servers to spawn a transcoding container
 * @param {string} mediaId  - UUID of the media to start live streaming
 * @param {string} pushKey  - Media push key (X-PUSH-KEY header)
 * @param {Object} metadata - LiveVideoAssetCreationRequestDTO payload
 * @param {string} [metadata.resolution]   - e.g. "1080p"
 * @param {string} [metadata.bitrate]      - e.g. "4000k"
 * @param {string} [metadata.codec]        - e.g. "h264"
 * @returns {{ encryptionKey: string, assetId: string, isModerationEnabled: boolean }}
 */
async function startLive(mediaId, pushKey, metadata) {
  try {
    const response = await apiClient.post(`/live/${mediaId}`, metadata, {
      headers: {
        "X-PUSH-KEY": pushKey,
      },
    });
    console.log("startLive response:", response.data);
    // { encryptionKey: "base64...", assetId: "uuid", isModerationEnabled: true/false }
    return response.data;
  } catch (error) {
    handleError("startLive", error);
  }
}
 
/**
 * PUT /live/end/{mediaId}
 * Ends an active live stream and triggers final playlist generation in the background
 * Called by RTMP servers when the stream ends
 * @param {string} mediaId - UUID of the media to end
 * @param {string} pushKey - Media push key (X-PUSH-KEY header)
 * @returns {200} Empty OK response on success
 */
async function endLive(mediaId, pushKey) {
  try {
    const response = await apiClient.put(`/live/end/${mediaId}`, {}, {
      headers: {
        "X-PUSH-KEY": pushKey,
      },
    });
    console.log("endLive response: 200 OK (success)");
    return response.status;
  } catch (error) {
    handleError("endLive", error);
  }
}
 
module.exports = {endLive,startLive}