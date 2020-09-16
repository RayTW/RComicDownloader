package rcomic.control;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import org.json.JSONObject;

/** @author ray */
public class Config {
  /** 版本 */
  public String version = "v3.1.2";

  public String charsetUtf8 = "UTF-8";

  public String htmlFolder = "rcomic.8comic";

  public String episodeImageUrlSchema = "https:";

  /** 漫畫閱讀器html檔名 */
  private String readerHtml = "openComic.html";

  private String favoritesJson = "favorites.json";
  private Font comicListFont = new Font("Serif", Font.BOLD, 20);
  private Properties lang;

  public Config() {
    lang = new Properties();
    try {
      try (Reader reader =
          new InputStreamReader(
              this.getClass().getResourceAsStream("langTw.properties"), charsetUtf8)) {
        lang.load(reader);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 彈出警告訊息框
   *
   * @param obj 要秀的訊息
   * @param title 訊息框title
   */
  public void showMsgBar(Object obj, String title) {
    JOptionPane.showMessageDialog(null, obj.toString(), title, JOptionPane.WARNING_MESSAGE);
  }

  public Font getComicListFont() {
    return comicListFont;
  }

  public String getLangValue(String key) {
    return lang.getProperty(key);
  }

  public String getComicHtml(String imageList) {
    String ret = "";
    StringBuilder html = new StringBuilder();

    try (Stream<String> stream =
        new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream(readerHtml), charsetUtf8))
            .lines()) {
      stream.forEach(
          line -> {
            if (html.length() > 0) {
              html.append(System.lineSeparator());
            }
            html.append(line);
          });
      ret =
          String.format(
              html.toString(),
              imageList,
              getLangValue("FirstPage"),
              getLangValue("FinalPage"),
              getLangValue("PreviousPage"),
              getLangValue("NextPage"),
              getLangValue("PreviousPage"),
              getLangValue("NextPage"));

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * 讀取已收藏漫畫
   *
   * @return
   */
  public JSONObject loadFavorites() {
    try {
      Path tmpdir = Paths.get(System.getProperty("java.io.tmpdir") + htmlFolder);

      if (!Files.exists(tmpdir)) {
        Files.createDirectory(tmpdir);
      }

      Path tempPath = Paths.get(tmpdir.toString(), favoritesJson);

      if (Files.notExists(tempPath)) {
        Files.createFile(tempPath);
      }

      byte[] source = Files.readAllBytes(tempPath);

      if (source.length > 0) {
        return new JSONObject(new String(source, charsetUtf8));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new JSONObject();
  }

  /**
   * 儲存已收藏漫畫
   *
   * @param json
   */
  public void storeFavorites(JSONObject json) {
    try {
      Path tmpdir = Paths.get(System.getProperty("java.io.tmpdir") + htmlFolder);

      if (!Files.exists(tmpdir)) {
        Files.createDirectory(tmpdir);
      }

      Path tempPath = Paths.get(tmpdir.toString(), favoritesJson);

      if (Files.notExists(tempPath)) {
        Files.createFile(tempPath);
      }

      Files.write(
          tempPath,
          json.toString().getBytes(charsetUtf8),
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
