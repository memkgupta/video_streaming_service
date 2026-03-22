const client = require("../api/apiClient");
const handleError = require("../error");
const apiClient = client("asset_onboarding","/v1")
/**
 * Build auth headers for API key protected routes
 * @param {string} accessKey    - X-ACCESS-KEY header value
 * @param {string} accessSecret - X-ACCESS-SECRET header value
 */
function authHeaders(accessKey, accessSecret) {
  return {
    "X-ACCESS-KEY": accessKey,
    "X-SECRET-KEY": accessSecret,
  };
}
 
/**
 * POST /media
 * Create a new media entry
 * @param {string} accessKey    - X-ACCESS-KEY
 * @param {string} accessSecret - X-ACCESS-SECRET
 * @param {Object} mediaCreateRequestDTO
 * @param {string} mediaCreateRequestDTO.title       - Title of the media
 * @param {string} mediaCreateRequestDTO.description - Description of the media
 * @param {string} mediaCreateRequestDTO.type        - Media type e.g. "STATIC" | "LIVE"
 * @param {boolean} mediaCreateRequestDTO.isModerationEnabled - Enable moderation
 * @returns {MediaDTO}
 */
async function createMedia(accessKey, accessSecret, mediaCreateRequestDTO) {
  try {
    const response = await apiClient.post("/media", mediaCreateRequestDTO, {
      headers: authHeaders(accessKey, accessSecret),
    });
    console.log("createMedia response:", response.data);
    return response.data;
  } catch (error) {
    console.error(error.response)
    handleError("createMedia", error);
  }
}
 
/**
 * GET /media/{id}
 * Fetch a single media by UUID
 * @param {string} accessKey    - X-ACCESS-KEY
 * @param {string} accessSecret - X-ACCESS-SECRET
 * @param {string} id           - Media UUID
 * @returns {MediaDTO}
 */
async function getMedia(accessKey, accessSecret, id) {
  try {
    const response = await apiClient.get(`/media/${id}`, {
      headers: authHeaders(accessKey, accessSecret),
    });
    console.log("getMedia response:", response.data);
    return response.data;
  } catch (error) {
    handleError("getMedia", error);
  }
}
 
/**
 * GET /media?page=1&limit=10
 * Fetch all media in a paginated way
 * @param {string} accessKey    - X-ACCESS-KEY
 * @param {string} accessSecret - X-ACCESS-SECRET
 * @param {Object} params       - Query params
 * @param {number} params.page  - Page number (required, 1-based)
 * @param {number} [params.limit=10] - Items per page (optional, default 10)
 * @returns {PageResponseDTO<MediaDTO>} - { total, hasNext, hasPrevious, data: [] }
 */
async function getAllMedia(accessKey, accessSecret, params = { page: 1, limit: 10 }) {
  try {
    const response = await apiClient.get("/media", {
      headers: authHeaders(accessKey, accessSecret),
      params,
    });
    console.log("getAllMedia response:", response.data);
    return response.data;
  } catch (error) {
    handleError("getAllMedia", error);
  }
}
 
/**
 * DELETE /media/{id}
 * Delete a media by UUID
 * @param {string} accessKey    - X-ACCESS-KEY
 * @param {string} accessSecret - X-ACCESS-SECRET
 * @param {string} id           - Media UUID
 * @returns {204} No content on success
 */
async function deleteMedia(accessKey, accessSecret, id) {
  try {
    const response = await apiClient.delete(`/media/${id}`, {
      headers: authHeaders(accessKey, accessSecret),
    });
    console.log("deleteMedia response: 204 No Content (success)");
    return response.status;
  } catch (error) {
    handleError("deleteMedia", error);
  }
}
 
/**
 * PUT /media/{id}/thumbnail
 * Update the thumbnail of a media — returns a pre-signed S3 URL for direct upload
 * @param {string} accessKey    - X-ACCESS-KEY
 * @param {string} accessSecret - X-ACCESS-SECRET
 * @param {string} id           - Media UUID
 * @param {Object} fileMetaData
 * @param {string} fileMetaData.fileName  - Name of the thumbnail file
 * @param {string} fileMetaData.fileType  - MIME type e.g. "image/jpeg"
 * @param {number} fileMetaData.fileSize  - File size in bytes
 * @returns {{ preSignedURL: string }}
 */
async function updateMediaThumbnail(accessKey, accessSecret, id, fileMetaData) {
  try {
    const response = await apiClient.put(`/media/${id}/thumbnail`, fileMetaData, {
      headers: authHeaders(accessKey, accessSecret),
    });
    console.log("updateMediaThumbnail response:", response.data);
    // { preSignedURL: "https://..." }
    return response.data;
  } catch (error) {
    handleError("updateMediaThumbnail", error);
  }
}
 
/**
 * PUT /media/{id}/video
 * Start a multipart video upload for static media
 * Returns the upload metadata and push key needed for chunk uploading
 * @param {string} accessKey    - X-ACCESS-KEY
 * @param {string} accessSecret - X-ACCESS-SECRET
 * @param {string} id           - Media UUID
 * @param {Object} fileMetaData
 * @param {string} fileMetaData.fileName  - Name of the video file
 * @param {string} fileMetaData.fileType  - MIME type e.g. "video/mp4"
 * @param {number} fileMetaData.fileSize  - File size in bytes
 * @returns {{ key: string, pushKey: string, uploadId: string, assetId: string }}
 */
async function startVideoUpload(accessKey, accessSecret, id, fileMetaData) {
  try {
    const response = await apiClient.put(`/media/${id}/video`, fileMetaData, {
      headers: authHeaders(accessKey, accessSecret),
    });
    console.log("startVideoUpload response:", response.data);
    // { key: "...", pushKey: "...", uploadId: "...", assetId: "..." }
    return response.data;
  } catch (error) {
    handleError("startVideoUpload", error);
  }
}
 
/**
 * GET /media/{id}/generate-tokens?userId=xxx
 * Generate access + refresh tokens for an end user to access protected media
 * @param {string} accessKey    - X-ACCESS-KEY
 * @param {string} accessSecret - X-ACCESS-SECRET
 * @param {string} id           - Media UUID
 * @param {string} userId       - The end user's ID to generate tokens for
 * @returns {{ access_token: string, refresh_token: string }}
 */
async function generateTokens(accessKey, accessSecret, id, userId) {
  try {
    const response = await apiClient.get(`/media/${id}/generate-tokens?userId=${userId}`, {
      headers: authHeaders(accessKey, accessSecret),
      params: { userId },
    });
    console.log("generateTokens response:", response.data);
    // { access_token: "...", refresh_token: "..." }
    return response.data;
  } catch (error) {
    handleError("generateTokens", error);
  }
}
module.exports = {
  createMedia,
  getMedia,
  getAllMedia,
  deleteMedia,
  updateMediaThumbnail,
  startVideoUpload,
  generateTokens,
};