// ─────────────────────────────────────────────────────────────
// testcases/auth-and-token-refresh.js
//
// Simpler example test case to demonstrate the format:
//   1. Register a new user
//   2. Login
//   3. Refresh the access token
//   4. Verify the new token works by calling GET /{userId}
// ─────────────────────────────────────────────────────────────

const { register, login } = require("../user/auth");
const { refreshAccessToken } = require("../user/token");
const { getUserById } = require("../user/user");

module.exports = {
  name: "Auth & Token Refresh",
  description: "Register → Login → Refresh token → Verify token by fetching user",
  config: {},

  steps: [

    {
      name: "Register new test user",
      critical: false, // may already exist — don't abort if 400
      async run(ctx) {
        const { email, password } = ctx.config;
        const data = await register({ firstName: "Test", lastName: "User", email, password });
        ctx.userId = data?.userId ?? data?.id;
        return { summary: `userId: ${ctx.userId}` };
      },
    },

    {
      name: "Login with credentials",
      async run(ctx) {
        const { email, password } = ctx.config;
        const data = await login(email, password);
        ctx.bearerToken  = data.tokens.accessToken;
        ctx.refreshToken = data.tokens.refreshToken;
        ctx.userId       = ctx.userId ?? data.user.id ;
        return { summary: `token: ${ctx.bearerToken.slice(0, 24)}…` };
      },
    },

    {
      name: "Refresh access token",
      async run(ctx) {
        const data = await refreshAccessToken(ctx.refreshToken);
        ctx.bearerToken = data.accessToken;
        return { summary: `new token: ${ctx.bearerToken.slice(0, 24)}…` };
      },
    },

    {
      name: "Verify new token by fetching user profile",
      async run(ctx) {
        const user = await getUserById(ctx.userId,ctx.bearerToken);
        return { summary: `email: ${user.email}` };
      },
    },

  ],
};