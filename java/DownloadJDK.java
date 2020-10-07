import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

class DownloadJDK {
  public static void main(String... args) throws Exception {
    var feature = args.length == 0 ? "15" : args[0];
    var os = computeOs();
    var arch = computeArch();

    System.out.println("Download JDK " + feature + " (" + os + "-" + arch + ")");

    // System.getProperties().forEach((k, v) -> System.out.println(k + ":" + v));

    var properties = browse("https://github.com/sormuras/bach/raw/HEAD/install-jdk.properties");
    var pro = new Properties();
    pro.load(new StringReader(properties));
    var uri = pro.getProperty(feature + "-" + os + "-" + arch);
    System.out.println(uri);
  }

  private static String computeOs() {
    var name = System.getProperty("os.name").toLowerCase();
    if (name.contains("win")) return "windows";
    if (name.contains("mac")) return "osx";
    return "linux";
  }

  private static String computeArch() {
    return System.getProperty("arch", "x64");
  }

  private static String browse(String url) throws Exception {
    try (var stream = URI.create(url).toURL().openStream()) {
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
