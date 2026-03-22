const client = require("../api/apiClient");
const handleError = require("../error");
const apiClient = client("asset_onboarding","/v1")
/**
 * GET /v1/watch/{mediaId}?start=-1
 * Get the content/playlist for a player to play the media (static or live)
 * Media must be in READY or LIVE status
 * @param {string} bearerToken  - JWT access token (Authorization: Bearer)
 * @param {string} mediaId      - UUID of the media to watch
 * @param {string} assetId      - Asset ID tied to the media (X-ASSET-ID header)
 * @param {string} userId       - End user's ID (X-USER-ID header)
 * @param {number} [start=-1]   - Playback offset in milliseconds (-1 = from beginning)
 * @returns {string} - HLS playlist or content string for the player
 */
async function watch(bearerToken, mediaId, assetId, userId, start = -1) {
  try {
    
    const response = await apiClient.get(`/watch/${mediaId}`, {
      headers: {
        "X-ACCESS-TOKEN": bearerToken,
        "X-USER-ID": userId,
      },
      params: { start },
    });
    console.log("watch response:", response.data);
    return response.data;
  } catch (error) {
    handleError("watch", error);
  }
}
 
/**
 * GET /v1/watch/live/{mediaId}/{resolution}/playlist?start=-1
 * Get the HLS playlist for a specific resolution of a live stream
 * Media must be of type LIVE_VIDEO
 * @param {string} bearerToken  - JWT access token (Authorization: Bearer)
 * @param {string} mediaId      - UUID of the live media
 * @param {string} resolution   - Resolution variant e.g. "1080p", "720p", "480p"
 * @param {string} assetId      - Asset ID tied to the media (X-ASSET-ID header)
 * @param {number} [start=-1]   - Playback offset in milliseconds (-1 = from beginning)
 * @returns {string} - HLS resolution-specific playlist content
 */
async function watchLiveResolution(bearerToken, mediaId, resolution, assetId, start = -1) {
  try {
    const response = await apiClient.get(
      `/watch/live/${mediaId}/${resolution}/playlist`,
      {
        headers: {
          Authorization: `Bearer ${bearerToken}`,
          "X-ASSET-ID": assetId,
        },
        params: { start },
      }
    );
    console.log("watchLiveResolution response:", response.data);
    return response.data;
  } catch (error) {
    handleError("watchLiveResolution", error);
  }
}
module.exports = { watch, watchLiveResolution };
 