
const client = require("../api/apiClient");
const apiClient = client("user","/v1/user")
/**
 * GET /all?ids=id1,id2,...
 * Fetch multiple users by their IDs
 * @param {string[]} ids - Array of user IDs
 */
async function getAllUsers(ids) {
  try {
    const response = await apiClient.get("/all", {
      params: {
        ids: ids.join(","),
      },
    });
    console.log("getAllUsers response:", response.data);
    return response.data;
  } catch (error) {
    handleError("getAllUsers", error);
  }
}
/**
 * GET /{id}
 * Fetch a single user by ID
 * @param {string} id - User ID
 */
async function getUserById(id,access_token) {
  try {
    const response = await apiClient.get(`/${id}`,{headers:{
      "Authorization":`Bearer ${access_token}`
    }});
    console.log("getUserById response:", response.data);
    return response.data;
  } catch (error) {
    handleError("getUserById", error);
  }
}
/**
 * PATCH /{id}
 * Update a user by ID
 * @param {string} id - User ID
 * @param {Object} userDTO - User data to update
 * @param {string} [userDTO.id]
 * @param {string} [userDTO.name]
 * @param {string} [userDTO.email]
 */
async function updateUser(id, userDTO) {
  try {
    const response = await apiClient.patch(`/${id}`, userDTO);
    console.log("updateUser response:", response.data);
    return response.data;
  } catch (error) {
    handleError("updateUser", error);
  }
}
/**
 * PUT /reset-password?token=<jwt_token>
 * Reset user password using a JWT token
 * @param {string} token - JWT token from reset password email
 * @param {string} newPass - New password to set
 */
async function resetPassword(token, newPass) {
  try {
    const response = await apiClient.put(
      "/reset-password",
      { newPass },
      {
        params: { token },
      }
    );
    console.log("resetPassword response: 204 No Content (success)");
    return response.status;
  } catch (error) {
    handleError("resetPassword", error);
  }
}
/**
 * Centralized error handler
 */
function handleError(fnName, error) {
  if (error.response) {
    console.error(`[${fnName}] Server error ${error.response.status}:`, error.response.data);
  } else if (error.request) {
    console.error(`[${fnName}] No response received:`, error.message);
  } else {
    console.error(`[${fnName}] Request setup error:`, error.message);
  }
  throw error;
}




module.exports = { getAllUsers, getUserById, updateUser, resetPassword };