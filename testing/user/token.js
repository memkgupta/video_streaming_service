const client = require("../api/apiClient");
const handleError = require("../error");
const apiClient = client("user","/v1")
async function refreshAccessToken(refreshToken) {
  try {
    const response = await apiClient.get("/token/refresh-token", {
      headers: {
        "X-REFRESH-TOKEN": refreshToken,
      },
    });
    console.log("refreshAccessToken response:", response.data);
    return response.data; // { accessToken: "..." }
  } catch (error) {
    handleError("refreshAccessToken", error);
  }
}
module.exports = {refreshAccessToken}