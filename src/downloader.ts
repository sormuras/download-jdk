let tempDirectory = process.env["RUNNER_TEMP"] || "";

import * as core from "@actions/core";
import * as httpm from "@actions/http-client";
import * as fs from "fs";
import * as http from "https";
import * as path from "path";

const IS_WINDOWS = process.platform === "win32";
if (!tempDirectory) {
  let baseLocation;
  if (IS_WINDOWS) {
    // On windows use the USERPROFILE env variable
    baseLocation = process.env["USERPROFILE"] || "C:\\";
  } else {
    if (process.platform === "darwin") {
      baseLocation = "/Users";
    } else {
      baseLocation = "/home";
    }
  }
  tempDirectory = path.join(baseLocation, "actions", "temp");
}

export async function downloadJava(
  version: string,
  arch: string
): Promise<void> {
  core.debug(`Downloading OpenJDK ${version}`);

  const os = getOsString(process.platform);
  const jdkUrl = await getJdkUrl(version, arch, os);
  if (jdkUrl === undefined || jdkUrl === "") {
    throw new Error(
      `Couldn't find download URL for OpenJDK ${version} on ${os} with architecture ${arch}`
    );
  }
  core.debug(`Downloading OpenJDK ${version} from ${jdkUrl}`);

  const filename = getFilename(jdkUrl);
  const jdkPath = path.join(tempDirectory, filename);
  await download(jdkUrl, jdkPath);

  core.exportVariable("JDK_FILE", jdkPath);
  core.exportVariable("JDK_VERSION", version);
  core.exportVariable("JAVA_VERSION", version);
  core.setOutput("file", jdkPath);
  core.setOutput("version", version);
}

function getFilename(url: string): string {
  const idx = url.lastIndexOf("/");
  return url.substring(idx + 1);
}

function getOsString(platform: string): string {
  switch (platform) {
    case "win32":
      return "windows";
    case "darwin":
      return "osx";
    default:
      return "linux";
  }
}

async function download(url: string, dest: string): Promise<string> {
  return new Promise((resolve, reject) => {
    const file = fs.createWriteStream(dest, { flags: "wx" });

    const request = http.get(url, response => {
      if (response.statusCode === 200) {
        response.pipe(file);
      } else {
        file.close();
        fs.unlink(dest, () => {}); // Delete temp file
        reject(
          `Server responded with ${response.statusCode}: ${response.statusMessage}`
        );
      }
    });

    request.on("error", err => {
      file.close();
      fs.unlink(dest, () => {}); // Delete temp file
      reject(err.message);
    });

    file.on("finish", () => {
      resolve();
    });

    file.on("error", err => {
      file.close();

      if (err.code === "EEXIST") {
        reject("File already exists");
      } else {
        fs.unlink(dest, () => {}); // Delete temp file
        reject(err.message);
      }
    });
  });
}

async function getJdkUrl(version: string, arch: string, os: string) {
  const http = new httpm.HttpClient("download-jdk", undefined, {
    allowRetries: true,
    maxRetries: 3
  });
  const url =
    "https://github.com/sormuras/bach/raw/master/install-jdk.properties";
  const response = await http.get(url);

  const statusCode = response.message.statusCode || 0;
  if (statusCode < 200 || statusCode > 299) {
    let body = "";
    try {
      body = await response.readBody();
    } catch (err) {
      core.debug(`Unable to read body: ${err.message}`);
    }
    const message = `Unexpected HTTP status code '${response.message.statusCode}' when retrieving versions from '${url}'. ${body}`.trim();
    throw new Error(message);
  }
  const body = await response.readBody();
  const properties = new Map<string, string>(
    body
      .split("\n")
      .filter((s: string) => !isBlank(s))
      .filter((s: string) => !s.trim().startsWith("#"))
      .map((s: string) => s.split("="))
      .map((s: string[]) => [s[0], s[1]] as [string, string])
      .values()
  );

  if (properties.has(`${version}-${os}-${arch}`)) {
    return properties.get(`${version}-${os}-${arch}`);
  } else {
    throw new Error(`Couldn't find OpenJDK for version ${version}`);
  }
}

function isBlank(str: string): boolean {
  return !str || /^\s*$/.test(str);
}
