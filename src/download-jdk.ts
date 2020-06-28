import * as core from "@actions/core";
import * as downloader from "./downloader";
import * as path from "path";

async function run() {
  try {
    // Feature number of the JDK to download
    const version = core.getInput("feature") || "14";
    const arch = core.getInput("architecture") || "x64";

    await downloader.downloadJava(version, arch);
  } catch (error) {
    core.setFailed(error.message);
  }
}

run();
