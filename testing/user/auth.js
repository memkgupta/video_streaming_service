const client = require("../api/apiClient");
const handleError = require("../error");
const apiClient = client("user","/v1")
async function login(email, password) {
  try {
    const response = await apiClient.post("/auth/login", { email, password });
    console.log("login response:", response.data);
    // Expected: { accessToken: "...", refreshToken: "...", ... }
    return response.data;
  } catch (error) {
    handleError("login", error);
  }
} 
/**
 * POST /auth/register
 * Registers a new user and creates their channel
 * @param {Object} registerRequest - Registration payload
 * @param {string} registerRequest.email - User's email
 * @param {string} registerRequest.password - User's password
 * @param {string} registerRequest.firstName - User's first name
 * @param {string} registerRequest.lastName - User's last name
 */
async function register(registerRequest) {
  try {
    const response = await apiClient.post("/auth/register", registerRequest);
    console.log("register response:", response.data);
    // Expected: RegisterResponse payload
    return response.data;
  } catch (error) {
    handleError("register", error);
  }
}

module.exports = {login,register}