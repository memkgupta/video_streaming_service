// ─────────────────────────────────────────────────────────────
// runner/config.js  –  Global credentials & endpoints
// All test cases inherit from this. Override per-test-case if needed.
// ─────────────────────────────────────────────────────────────

module.exports = {
  // ── Server ────────────────────────────────────────────────
  baseUrl: process.env.BASE_URL || "http://localhost:8080",

  // ── Platform API keys (from Organisation controller) ──────
  accessKey:    process.env.ACCESS_KEY    || "",
  accessSecret: process.env.ACCESS_SECRET || "",

  // ── Auth credentials (used by login / register) ───────────
  email:    process.env.TEST_EMAIL    || "testuser@vsnt.dev",
  password: process.env.TEST_PASSWORD || "TestPass@123",

  // ── Tokens (populated at runtime by login step) ───────────
  bearerToken:  process.env.BEARER_TOKEN  || "",
  refreshToken: process.env.REFRESH_TOKEN || "",

  // ── Push key for upload / live endpoints ──────────────────
  pushKey: process.env.PUSH_KEY || "",

  // ── Retry / timing defaults ───────────────────────────────
  retries:    3,
  retryDelay: 3000,   // ms between retries
  watchRetryDelay: 5000, // ms between "not ready yet" retries for watch

  // ── File upload defaults ──────────────────────────────────
  // Point these to a real local file for full end-to-end testing
  testVideoPath: process.env.TEST_VIDEO || "./samples/test.mp4",
  chunkSizeMB:   5,   // each chunk size in MB

  // ── ffplay/ffprobe ────────────────────────────────────────
  // Set to false to skip all ffprobe/ffplay steps in CI
  enableFfmpeg: process.env.ENABLE_FFMPEG !== "false",
};