require('dotenv').config()
const fs     = require("fs");
const path   = require("path");
const globalConfig = require("./config");
const { runAll }   = require("./engine");

// ── Auto-discover all test cases in ./testcases/ ─────────────
const tcDir = path.join(__dirname, "testcases");
let files = fs
  .readdirSync(tcDir)
  .filter(f => f.endsWith(".js"))
  .sort();
 
// Optional CLI filter: node index.js <pattern>
const filter = process.argv[2]?.toLowerCase();
if (filter) {
  files = files.filter(f => f.toLowerCase().includes(filter));
  if (!files.length) {
    console.error(`No test cases match filter: "${filter}"`);
    process.exit(1);
  }
}
 
// ── Load and merge config ─────────────────────────────────────
const testCases = files.map(file => {
  const tc = require(path.join(tcDir, file));
  return {
    ...tc,
    // Merge global config with per-test-case config (test case wins on conflict)
    config: { ...globalConfig, ...tc.config },
  };
});
 
// ── Run ───────────────────────────────────────────────────────
console.log(`\nVSNT Test Runner  ·  ${testCases.length} case(s) loaded`);
console.log(`Base URL: ${globalConfig.baseUrl}\n`);
 
runAll(testCases).catch(err => {
  console.error("Fatal runner error:", err);
  process.exit(1);
});
 