// ─────────────────────────────────────────────────────────────
// runner/engine.js  –  Test execution engine
// ─────────────────────────────────────────────────────────────
const { execSync, spawn } = require("child_process");
const fs   = require("fs");
const path = require("path");
const chalk = require("chalk"); // npm i chalk@4

// ── Shared context passed through every step ──────────────────
// Each test case gets its own fresh context object.
// Steps read and write to it freely (e.g. ctx.mediaId, ctx.pushKey).
class Context {
  constructor(config) {
    this.config = config;   // global config from test case
    this.log    = [];       // collected log lines for this run
  }
}

// ── Pretty printer ────────────────────────────────────────────
const icon = {
  pass:  chalk.green("✓"),
  fail:  chalk.red("✗"),
  skip:  chalk.yellow("⊘"),
  run:   chalk.cyan("→"),
  info:  chalk.blue("ℹ"),
  warn:  chalk.yellow("⚠"),
  retry: chalk.magenta("↺"),
};

function pad(str, n) { return String(str).padEnd(n); }
function ms(n)       { return chalk.dim(`${n}ms`); }

function printHeader(name, description) {
  const line = "─".repeat(64);
  console.log("\n" + chalk.bold.white(line));
  console.log(chalk.bold.white(`  TEST CASE : `) + chalk.bold.cyan(name));
  if (description) console.log(chalk.dim(`  ${description}`));
  console.log(chalk.bold.white(line));
}

function printStep(stepIndex, stepName, status, elapsed, detail) {
  const num    = chalk.dim(`[${String(stepIndex).padStart(2, "0")}]`);
  const ic     = icon[status] || " ";
  const timing = elapsed != null ? "  " + ms(elapsed) : "";
  const det    = detail ? "  " + chalk.dim(detail) : "";
  console.log(`  ${num} ${ic}  ${pad(stepName, 38)}${timing}${det}`);
}

function printSummary(results) {
  const total  = results.length;
  const passed = results.filter(r => r.status === "pass").length;
  const failed = results.filter(r => r.status === "fail").length;
  const skipped= results.filter(r => r.status === "skip").length;

  console.log("\n" + "═".repeat(64));
  console.log(
    chalk.bold("  SUMMARY  ") +
    chalk.green(`${passed} passed`) + "  " +
    (failed  ? chalk.red(`${failed} failed`)   + "  " : "") +
    (skipped ? chalk.yellow(`${skipped} skipped`) + "  " : "") +
    chalk.dim(`(${total} total)`)
  );
  console.log("═".repeat(64) + "\n");
}

// ── Retry helper ──────────────────────────────────────────────
async function withRetry(fn, { retries = 10, delay = 5000, label = "step" } = {}) {
  let lastErr;
  for (let attempt = 1; attempt <= retries + 1; attempt++) {
    try {
      return await fn();
    } catch (err) {
      lastErr = err;
      if (attempt <= retries) {
        console.log(`  ${icon.retry}  ${chalk.magenta(`Retry ${attempt}/${retries}`)}  ${chalk.dim(label)}  ${chalk.dim(err.message)}`);
        await sleep(delay);
      }
    }
  }
  throw lastErr;
}

function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }

// ── ffplay / ffprobe helper ───────────────────────────────────
function checkFfmpeg() {
  try { execSync("ffmpeg -version", { stdio: "ignore" }); return true; }
  catch { return false; }
}

function checkFfplay() {
  try { execSync("ffplay -version", { stdio: "ignore" }); return true; }
  catch { return false; }
}

/**
 * Verify an HLS stream is playable using ffprobe (non-interactive, CI-safe).
 * Returns { ok, duration, streams } or throws.
 */
async function probeHLS(url, headers = {}) {
  return new Promise((resolve, reject) => {
    const headerArgs = Object.entries(headers).flatMap(([k, v]) => [
      "-headers", `${k}: ${v}\r\n`,
    ]);

    const args = [
   
      "-v", "quiet",
      "-print_format", "json",
      "-show_streams",
      "-show_format",
      url,
    ];

    const proc = spawn("ffprobe", args);
    let stdout = "";
    let stderr = "";
    proc.stdout.on("data", d => (stdout += d));
    proc.stderr.on("data", d => (stderr += d));
    proc.on("close", code => {
      if (code !== 0) {
        reject(new Error(`ffprobe exited ${code}: ${stderr.trim()}`));
        return;
      }
      try {
        const info = JSON.parse(stdout);
        resolve({
          ok:      true,
          streams: info.streams?.length ?? 0,
          duration: info.format?.duration ?? null,
          format:  info.format?.format_name ?? null,
        });
      } catch {
        reject(new Error("ffprobe output parse failed"));
      }
    });
  });
}

/**
 * Open the HLS stream in ffplay for interactive viewing.
 * Non-blocking — returns immediately, player opens in background.
 */
function openInFfplay(url, headers = {}, title = "VSNT Stream") {
  if (!checkFfplay()) {
    console.log(`  ${icon.warn}  ffplay not found — skipping interactive playback`);
    return null;
  }
  const headerArgs = Object.entries(headers).flatMap(([k, v]) => [
    "-headers", `${k}: ${v}\r\n`,
  ]);
  const args = [
    ...headerArgs,
    "-window_title", title,
    "-autoexit",
    url,
  ];
  console.log(`  ${icon.info}  Launching ffplay → ${chalk.cyan(url)}`);
  const proc = spawn("ffplay", args, { detached: true, stdio: "ignore" });
  proc.unref();
  return proc;
}

// ── Step runner ───────────────────────────────────────────────
async function runSteps(steps, ctx) {
  const results = [];
  let aborted = false;

  for (let i = 0; i < steps.length; i++) {
    const step = steps[i];

    if (aborted && !step.alwaysRun) {
      printStep(i + 1, step.name, "skip", null, "aborted by previous failure");
      results.push({ name: step.name, status: "skip" });
      continue;
    }

    const t0 = Date.now();
    try {
      console.log(`  ${chalk.dim(`[${String(i + 1).padStart(2, "0")}]`)} ${icon.run}  ${chalk.bold(step.name)}`);

      const result = await step.run(ctx);

      const elapsed = Date.now() - t0;
      const detail  = result?.summary ?? "";
      printStep(i + 1, step.name, "pass", elapsed, detail);
      results.push({ name: step.name, status: "pass", elapsed, result });

    } catch (err) {
      const elapsed = Date.now() - t0;
      printStep(i + 1, step.name, "fail", elapsed, err.message);

      if (process.env.VERBOSE) {
        console.log(chalk.dim("     " + (err.stack ?? err.message).split("\n").join("\n     ")));
      }

      results.push({ name: step.name, status: "fail", elapsed, error: err });

      if (step.critical !== false) aborted = true;
    }
  }

  return results;
}

// ── Main TestCase runner ──────────────────────────────────────
async function runTestCase(testCase) {
  printHeader(testCase.name, testCase.description);

  const ctx = new Context(testCase.config ?? {});
  const stepResults = await runSteps(testCase.steps, ctx);

  printSummary(stepResults);
  return stepResults;
}

// ── Multi-case runner (index.js entry) ───────────────────────
async function runAll(testCases) {
  let totalPass = 0, totalFail = 0;

  for (const tc of testCases) {
    const results = await runTestCase(tc);
    totalPass += results.filter(r => r.status === "pass").length;
    totalFail += results.filter(r => r.status === "fail").length;
  }

  const grand = "═".repeat(64);
  console.log(chalk.bold.white(grand));
  console.log(
    chalk.bold.white("  ALL CASES  ") +
    chalk.green(`${totalPass} passed`) + "  " +
    (totalFail ? chalk.red(`${totalFail} failed`) : chalk.dim("0 failed"))
  );
  console.log(chalk.bold.white(grand) + "\n");
}

module.exports = {
  runTestCase,
  runAll,
  withRetry,
  probeHLS,
  openInFfplay,
  checkFfmpeg,
  sleep,
  Context,
};