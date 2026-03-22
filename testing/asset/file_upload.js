const client = require("../api/apiClient");
const handleError = require("../error");
const apiClient = client("asset_onboarding","/v1")
/**
 * POST /upload-chunk
 * Uploads a single chunk of a multipart upload
 * @param {string} pushKey       - Media push key (X-PUSH-KEY header)
 * @param {string} userId        - User ID (X-USER-ID header)
 * @param {Object} chunkUploadRequest
 * @param {string} chunkUploadRequest.uploadId    - The multipart upload session ID
 * @param {string} chunkUploadRequest.assetId     - The asset/media ID
 * @param {number} chunkUploadRequest.partNumber  - The chunk part number (1-based)
 * @param {string} chunkUploadRequest.key         - The S3/storage object key
 * @returns {{ url: string }} - Pre-signed URL for the chunk
 */
async function uploadChunk(pushKey, userId, chunkUploadRequest) {
  try {
    const response = await apiClient.post("/file/upload-chunk", chunkUploadRequest, {
      headers: {
        "X-PUSH-KEY": pushKey,
        "X-USER-ID": userId,
      },
    });
    console.log("uploadChunk response:", response.data);
    // { url: "https://..." }
    return response.data;
  } catch (error) {
    console.error(error)
    handleError("uploadChunk", error);
  }
}
 
/**
 * POST /complete-upload
 * Finalizes/completes the multipart upload
 * @param {string} pushKey  - Media push key (X-PUSH-KEY header)
 * @param {string} userId   - User ID (X-USER-ID header)
 * @param {Object} finalizeUploadRequest
 * @param {string} finalizeUploadRequest.uploadId  - The multipart upload session ID
 * @param {string} finalizeUploadRequest.assetId   - The asset/media ID
 * @param {string} finalizeUploadRequest.key       - The S3/storage object key
 * @param {Object} finalizeUploadRequest.etagMap   - Map of part numbers to ETags e.g. { "1": "etag1", "2": "etag2" }
 * @returns {204} No content on success
 */
async function completeUpload(pushKey, userId, finalizeUploadRequest) {
  try {
    const response = await apiClient.post("/file/complete-upload", finalizeUploadRequest, {
      headers: {
        "X-PUSH-KEY": pushKey,
        "X-USER-ID": userId,
      },
    });
    console.log("completeUpload response: 204 No Content (success)");
    return response.status;
  } catch (error) {
    handleError("completeUpload", error);
  }
}
 
/**
 * POST /pause-upload
 * Pauses an ongoing multipart upload session
 * @param {string} pushKey  - Media push key (X-PUSH-KEY header)
 * @param {string} userId   - User ID (X-USER-ID header)
 * @param {Object} uploadPauseToggleRequest
 * @param {string} uploadPauseToggleRequest.assetId  - The asset/media ID
 * @param {Object} uploadPauseToggleRequest.etagMap  - Map of already uploaded part ETags e.g. { "1": "etag1" }
 * @returns {boolean} - true if paused successfully
 */
async function pauseUpload(pushKey, userId, uploadPauseToggleRequest) {
  try {
    const response = await apiClient.post("/file/pause-upload", uploadPauseToggleRequest, {
      headers: {
        "X-PUSH-KEY": pushKey,
        "X-USER-ID": userId,
      },
    });
    console.log("pauseUpload response:", response.data);
    return response.data; // boolean
  } catch (error) {
    handleError("pauseUpload", error);
  }
}
 
/**
 * POST /resume-upload
 * Resumes a previously paused multipart upload session
 * @param {string} pushKey  - Media push key (X-PUSH-KEY header)
 * @param {string} userId   - User ID (X-USER-ID header)
 * @param {Object} uploadPauseToggleRequest
 * @param {string} uploadPauseToggleRequest.assetId  - The asset/media ID
 * @param {Object} uploadPauseToggleRequest.etagMap  - Map of already uploaded part ETags (may be empty on resume)
 * @returns {boolean} - true if resumed successfully
 */
async function resumeUpload(pushKey, userId, uploadPauseToggleRequest) {
  try {
    const response = await apiClient.post("/file/resume-upload", uploadPauseToggleRequest, {
      headers: {
        "X-PUSH-KEY": pushKey,
        "X-USER-ID": userId,
      },
    });
    console.log("resumeUpload response:", response.data);
    return response.data; // boolean
  } catch (error) {
    handleError("resumeUpload", error);
  }
}

module.exports = {uploadChunk,completeUpload,pauseUpload,resumeUpload}