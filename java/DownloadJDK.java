import java.net.URI;
import java.nio.charset.StandardCharsets;

class DownloadJDK {
  public static void main(String... args) throws Exception {
    System.out.println("Download JDK");

    System.getProperties().forEach((k, v) -> System.out.println(k + ":" + v));

    var properties = browse("https://github.com/sormuras/bach/raw/HEAD/install-jdk.properties");
    System.out.println(properties);
  }

  private static String browse(String url) throws Exception {
    try (var stream = URI.create(url).toURL().openStream()) {
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
