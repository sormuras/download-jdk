import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

class DownloadJDK {
  public static void main(String... args) throws Exception {
    var feature = args.length == 0 ? "16" : args[0].toLowerCase();
    var os = computeOs(feature);
    var arch = computeArch();

    System.out.println("Download JDK " + feature + " (" + os + "-" + arch + ")");
    // System.getProperties().forEach((k, v) -> System.out.println(k + ":" + v));

    var uri = computeUri(feature, os, arch);
    var path = download(uri);
    var version = computeVersion(feature, path);

    setActionOutput("file", path);
    setActionOutput("version", version);

    setEnv("JDK_FILE", path);
    setEnv("JDK_VERSION", version);
  }

  private static void setActionOutput(String name, Object value) {
    System.out.printf("::set-output name=%s::%s%n", name, value);
  }

  private static void setEnv(String name, Object value) throws Exception {
    var env = Objects.requireNonNullElse(System.getenv("GITHUB_ENV"), ".download-jdk.env");
    var line = name + "=" + value + System.lineSeparator();
    Files.writeString(Path.of(env), line, UTF_8, CREATE, APPEND);
  }

  private static String computeOs(String feature) {
    var env = System.getenv("DOWNLOAD_JDK_OS");
    if (env != null) return env;
    var name = System.getProperty("os.name").toLowerCase();
    if (name.contains("win")) return "windows";
    if (name.contains("mac")) {
      try {
        var version = Integer.parseInt(feature);
        return version >= 17 ? "macos" : "osx";
      } catch (NumberFormatException exception) {
        return "osx";
      }
    }
    return "linux";
  }

  private static String computeArch() {
    //   linux: "aarch64", "x64", "x64-musl"
    //   macos: "aarch64", "x64"
    // windows: "x64"
    var env = System.getenv("DOWNLOAD_JDK_ARCH");
    if (env != null) return env;
    var arch = System.getProperty("os.arch", "x64");
    if (arch.equals("amd64")) return "x64";
    if (arch.equals("x86_64")) return "x64";
    return arch;
  }

  private static String computeUri(String feature, String os, String arch) throws Exception {
    var key = feature + "-" + os + "-" + arch;
    try {
      Integer.parseInt(feature);
      var properties = browse("https://github.com/sormuras/bach/raw/master/install-jdk.properties");
      var pro = new Properties();
      pro.load(new StringReader(properties));
      return pro.getProperty(key);
    } catch (NumberFormatException exception) {
      var map = parse(feature);
      // map.forEach((k, v) -> System.out.println(k + "\t" + v));
      return map.get(key);
    }
  }

  private static int computeVersion(String feature, Path path) {
    try {
      return Integer.parseInt(feature);
    } catch (NumberFormatException exception) {
      var name = path.getFileName().toString();
      return Integer.parseInt(substring(name, "openjdk-", "-"));
    }
  }

  private static String browse(String url) throws Exception {
    try (var stream = URI.create(url).toURL().openStream()) {
      return new String(stream.readAllBytes(), UTF_8);
    }
  }

  private static Path download(String uri) throws Exception {
    var url = new URL(uri);
    var file = uri.substring(uri.lastIndexOf('/') + 1);
    var path = Files.createTempDirectory("download-jdk-").resolve(file);
    System.out.println("  << " + url);
    System.out.println("  >> " + path);
    if (Boolean.getBoolean("ry-run")) {
      System.out.println("//");
      System.out.println("// Dry-run mode - skip download");
      System.out.println("//");
    } else {
      try (var source = url.openStream();
           var target = Files.newOutputStream(path)) {
        source.transferTo(target);
      }
    }
    return path;
  }

  /** Parse {@code https://jdk.java.net/${feature}} page. */
  private static Map<String, String> parse(String feature) throws Exception {
    var html = browse("https://jdk.java.net/" + feature);
    var table = substring(html, "<table class=\"builds\" summary=\"builds\">", "</table>");
    var map = new TreeMap<String, String>();
    for (var line : lines(table)) {
      var name = Path.of(URI.create(line).getPath()).getFileName().toString();
      var key = feature + "-" + substring(name, "_", "_bin");
      map.put(key, line);
      if (key.contains("-macos-")) map.put(key.replace("-macos-", "-osx-"), line);
    }
    return map;
  }

  private static String substring(String string, String beginTag, String endTag) {
    int beginIndex = string.indexOf(beginTag) + beginTag.length();
    int endIndex = string.indexOf(endTag, beginIndex);
    return string.substring(beginIndex, endIndex).trim();
  }

  static List<String> lines(String table) {
    var tags = table.lines().filter(line -> line.startsWith("<td><a href=\""));
    return tags.map(line -> line.substring(13, line.length() - 2)).collect(Collectors.toList());
  }
}
