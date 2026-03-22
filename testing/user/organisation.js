const client = require("../api/apiClient");
const handleError = require("../error");
/**
 * POST /v1/organisation/api-key
 * Generate an API key for the organisation associated with the given user
 * @param {string} bearerToken - JWT access token for Authorization header
 * @param {string} userId - The admin user's ID sent in X-USER-ID header
 */
const apiClient = client("user")
async function generateAPIKey(bearerToken, userId) {
  try {
    const response = await apiClient.post(
      "/v1/organisation/api-key",
      {}, 
      {
        headers: {
          Authorization: `Bearer ${bearerToken}`,
          "X-USER-ID": userId,
        },
      }
    );
    console.log("generateAPIKey response:", response.data);
  
    return response.data;
  } catch (error) {
    handleError("generateAPIKey", error);
  }
} 
/**
 * POST /v1/organisation
 * Create a new organisation
 * @param {string} bearerToken - JWT access token for Authorization header
 * @param {string} userId - The admin user's ID sent in X-USER-ID header
 * @param {Object} organisationDTO - Organisation data
 * @param {string} organisationDTO.name - Name of the organisation
 * @param {string} organisationDTO.adminId - Admin user ID for the organisation
 */
async function createOrganisation(bearerToken, userId, organisationDTO) {
  try {
    const response = await apiClient.post(
      "/v1/organisation",
      organisationDTO,
      {
        headers: {
          Authorization: `Bearer ${bearerToken}`,
          "X-USER-ID": userId,
        },
      }
    );
    console.log("createOrganisation response:", response.data);
    // { id: "...", name: "...", adminId: "..." }
    return response.data;
  } catch (error) {
    handleError("createOrganisation", error);
  }
}
module.exports = {createOrganisation,generateAPIKey}