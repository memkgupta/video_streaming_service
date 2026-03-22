// ─────────────────────────────────────────────────────────────
// testcases/static-video-upload-and-watch.js
//
// End-to-end test:
//   1. Login  →  get bearer token
//   2. Create media
//   3. Start multipart video upload  →  get uploadId, key, pushKey
//   4. Read local file, split into chunks, upload each chunk
//   5. Complete the multipart upload
//   6. Generate delivery tokens
//   7. Poll watch endpoint until media is READY (with retries)
//   8. Probe the HLS stream with ffprobe
//   9. Open stream in ffplay (interactive, skipped in CI)
// ─────────────────────────────────────────────────────────────

const fs   = require("fs");
const path = require("path");
const { withRetry, probeHLS, openInFfplay, sleep } = require("../engine");

// ── Import the API functions we built ────────────────────────
const { login }           = require("../user/auth");
const { createMedia, startVideoUpload, generateTokens } = require("../asset/media");
const { uploadChunk, completeUpload }                   = require("../asset/file_upload");
const { watch }           = require("../asset/watch");
const { accessSecret, testVideoPath, watchRetryDelay, enableFfmpeg } = require("../config");

// ─────────────────────────────────────────────────────────────
// TestCase definition
// ─────────────────────────────────────────────────────────────
module.exports = {
  // ── Human-readable name shown in the terminal ─────────────
  name: "Static Video Upload & Watch",

  // ── One-liner shown under the name ────────────────────────
  description:
    "Login → create media → multipart upload → complete → generate tokens → poll until ready → probe HLS → ffplay",

  // ── Per-test-case config (merged with / overrides global) ─
  // Leave empty to use global config.js values entirely.
  config: {enableFfmpeg:true,accessKey:process.env.API_ACCESS_KEY,accessSecret:process.env.API_SECRET,testVideoPath:"./test.mp4",watchRetryDelay:5000,retries:10},

  // ── Steps ─────────────────────────────────────────────────
  // Each step is an object with:
  //   name       {string}   – label printed in the terminal
  //   run        {async fn} – receives ctx (Context), must throw on failure
  //   critical   {boolean}  – if false, failure won't abort subsequent steps (default: true)
  //   alwaysRun  {boolean}  – if true, runs even after a critical failure (e.g. cleanup)
  steps: [

    // ── Step 1 : Login ──────────────────────────────────────
    {
      name: "Login and obtain bearer token",
      async run(ctx) {
        const { email, password } = ctx.config;
        const data = await login(email, password);

        // Store tokens on context for downstream steps
        ctx.bearerToken  = data.tokens.accessToken;
        ctx.refreshToken = data.tokens.refreshToken;

        return { summary: `token: ${ctx.bearerToken.slice(0, 20)}…` };
      },
    },

    // ── Step 2 : Create Media ───────────────────────────────
    {
      name: "Create media entry",
      async run(ctx) {
        const { accessKey, accessSecret } = ctx.config;
        const data = await createMedia(accessKey, accessSecret, {
          mediaType:         "STATIC",
          mediaAccessibility:"PROTECTED",
          moderation:        false,
        });

        ctx.mediaId  = data.id;
        ctx.assetId  = data.videoAsset?.id ?? null; // may be null before upload starts

        return { summary: `mediaId: ${ctx.mediaId}` };
      },
    },

    // ── Step 3 : Start Multipart Upload ─────────────────────
    {
      name: "Start video upload (init multipart)",
      async run(ctx) {
        const { accessKey, accessSecret, testVideoPath } = ctx.config;

        // Stat the file so we can tell the server its size
        if (!fs.existsSync(testVideoPath)) {
          throw new Error(
            `Test video not found at "${testVideoPath}". ` +
            `Set TEST_VIDEO env var or update config.testVideoPath.`
          );
        }
        const { size } = fs.statSync(testVideoPath);
        const ext      = path.extname(testVideoPath).replace(".", "") || "mp4";

        const data = await startVideoUpload(accessKey, accessSecret, ctx.mediaId, {
          fileName: path.basename(testVideoPath),
          fileType: `video/${ext}`,
          fileSize: size,
        });

        ctx.uploadId = data.uploadId;
        ctx.s3Key    = data.key;
        ctx.pushKey  = data.pushKey;
        ctx.assetId  = data.assetId ?? ctx.assetId;
        ctx.fileSize = size;

        return { summary: `uploadId: ${ctx.uploadId}` };
      },
    },

    // ── Step 4 : Upload Chunks ──────────────────────────────
    {
      name: "Upload file in chunks",
      async run(ctx) {
        const { testVideoPath, chunkSizeMB, userId } = ctx.config;
        const chunkSize = chunkSizeMB * 1024 * 1024;
        const fileBuffer = fs.readFileSync(testVideoPath);
        const totalChunks = Math.ceil(fileBuffer.length / chunkSize);

        ctx.etagMap = {};

        for (let partNumber = 1; partNumber <= totalChunks; partNumber++) {
          const start  = (partNumber - 1) * chunkSize;
          const end    = Math.min(start + chunkSize, fileBuffer.length);
          const chunk  = fileBuffer.slice(start, end);

          // The API returns a pre-signed S3 URL for this part.
          // We then PUT the raw bytes directly to S3.
          const { url } = await uploadChunk(ctx.pushKey, userId, {
            uploadId:   ctx.uploadId,
            assetId:    ctx.assetId,
            partNumber,
            key:        ctx.s3Key,
          });

          // PUT chunk directly to the pre-signed S3 URL
          const res = await fetch(url, {
            method:  "PUT",
            body:    chunk,
            headers: { "Content-Type": "application/octet-stream" },
          });

          if (!res.ok) throw new Error(`S3 chunk upload failed: HTTP ${res.status}`);

          // S3 returns an ETag header — collect it
          const etag = res.headers.get("ETag") ?? `etag-part-${partNumber}`;
          ctx.etagMap[String(partNumber)] = etag;

          process.stdout.write(
            `\r       Part ${partNumber}/${totalChunks} uploaded  [${Math.round((partNumber / totalChunks) * 100)}%]`
          );
        }
        process.stdout.write("\n");

        return { summary: `${totalChunks} parts, ${(fileBuffer.length / 1024 / 1024).toFixed(1)} MB` };
      },
    },

    // ── Step 5 : Complete Upload ────────────────────────────
    {
      name: "Complete multipart upload",
      async run(ctx) {
        const { userId } = ctx.config;
        await completeUpload(ctx.pushKey, userId, {
          uploadId: ctx.uploadId,
          assetId:  ctx.assetId,
          key:      ctx.s3Key,
          etagMap:  ctx.etagMap,
        });

        return { summary: "upload finalized" };
      },
    },

    // ── Step 6 : Generate Delivery Tokens ──────────────────
    {
      name: "Generate delivery tokens",
      async run(ctx) {
        const { accessKey, accessSecret, userId } = ctx.config;

        const tokens = await generateTokens(
          accessKey,
          accessSecret,
          ctx.mediaId,
          userId
        );

        ctx.deliveryAccessToken  = tokens.access_token;
        ctx.deliveryRefreshToken = tokens.refresh_token;

        return { summary: `delivery token: ${ctx.deliveryAccessToken.slice(0, 20)}…` };
      },
    },

    // ── Step 7 : Poll Watch Endpoint Until Ready ────────────
    {
      name: "Wait for media to be READY (poll watch)",
      async run(ctx) {
        const { userId, retries, watchRetryDelay } = ctx.config;
        if(!ctx.deliveryAccessToken)
        {
          throw new Error()
        }
        const result = await withRetry(
          async () => {
            const data = await watch(
              ctx.deliveryAccessToken,
              ctx.mediaId,
              ctx.assetId,
              userId,
              -1
            );
            // The watch endpoint returns the HLS playlist content (string)
            if (!data || data.length === 0) throw new Error("Empty playlist response");
            return data;
          },
          {
            retries: retries ?? 5,
            delay:   watchRetryDelay ?? 5000,
            label:   "watch endpoint not ready yet",
          }
        );

        // The response is an HLS playlist string — extract the URL base
        // so we can pass it to ffprobe / ffplay
        ctx.hlsPlaylistContent = result;

        // If the watch endpoint redirected to a CDN URL, ctx.hlsUrl will be set
        // by the response. We fall back to constructing it from baseUrl.
        ctx.hlsUrl =result;

        return { summary: "media is READY" };
      },
    },

    // ── Step 8 : Probe HLS with ffprobe ─────────────────────
    {
      name:     "Probe HLS stream with ffprobe",
      critical: false, // don't abort if ffprobe is not installed
      async run(ctx) {
        if (!ctx.config.enableFfmpeg) {
          throw new Error("ffmpeg disabled (ENABLE_FFMPEG=false)");
        }

        const headers = {
          "X-ACCESS-TOKEN": `${ctx.deliveryAccessToken}`,
          // "X-ASSET-ID":  String(ctx.assetId),
          "X-USER-ID":   ctx.config.userId,
        };

        const info = await probeHLS(ctx.hlsUrl, headers);

        return {
          summary: `${info.streams} stream(s)  duration=${info.duration ?? "live"}  format=${info.format}`,
        };
      },
    },

    // ── Step 9 : Open in ffplay (interactive / skipped in CI) ──
    {
      name:     "Open stream in ffplay",
      critical: false,
      async run(ctx) {
        if (!ctx.config.enableFfmpeg) {
          throw new Error("ffmpeg disabled — skipping ffplay");
        }
        if (process.env.CI) {
          throw new Error("CI environment — skipping interactive ffplay");
        }

        const headers = {
         "X-ACCESS-TOKEN": `${ctx.deliveryAccessToken}`,
          // "X-ASSET-ID":  String(ctx.assetId),
          "X-USER-ID":   ctx.config.userId,
        };

        openInFfplay(ctx.hlsUrl, headers, `VSNT · ${ctx.mediaId}`);
        await sleep(2000); // give ffplay time to start
        return { summary: "launched in background" };
      },
    },

  ], // end steps
};