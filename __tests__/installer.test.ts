import * as io from "@actions/io";
import fs = require("fs");
import path = require("path");

const tempDir = path.join(__dirname, "runner", "temp");

process.env["RUNNER_TEMP"] = tempDir;
import * as downloader from "../src/downloader";

jest.setTimeout(10000);

describe("installer tests", () => {
  beforeAll(async () => {
    try {
      await io.rmRF(tempDir);
    } catch {
      console.log("Failed to remove test directories");
    }

    await io.mkdirP(tempDir);
  });

  afterAll(async () => {
    try {
      await io.rmRF(tempDir);
    } catch {
      console.log("Failed to remove test directories");
    }
  });

  it("Downloads OpenJDK 14", async () => {
    await downloader.downloadJava("14", "x64");

    const files = fs.readdirSync(tempDir);
    expect(files.length).toBeGreaterThan(0);
  }, 100000);

  it("Throws if invalid version", async () => {
    let thrown = false;
    try {
      await downloader.downloadJava("invalid", "x64");
    } catch {
      thrown = true;
    }
    expect(thrown).toBe(true);
  });

  it("Throws if invalid arch", async () => {
    let thrown = false;
    try {
      await downloader.downloadJava("11", "invalid-arch");
    } catch {
      thrown = true;
    }
    expect(thrown).toBe(true);
  });
});
