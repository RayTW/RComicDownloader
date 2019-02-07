package rcomic.control;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

/**
 * 
 * @author ray
 *
 */
public class Config {
	/** 版本 */
	public String mVersion = "v3.0.0";

	public String mCharsetUtf8 = "UTF-8";

	public String mHtmlFolder = "rcomic";

	public String mEpisodeImageUrlSchema = "https:";

	/** 漫畫閱讀器html檔名 */
	private String mReaderHtml = "openComic.html";
	private Font mComicListFont = new Font("Serif", Font.BOLD, 20);
	private Properties mLang;

	public Config() {
		mLang = new Properties();
		try {
			try (Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("langTw.properties"),
					mCharsetUtf8)) {
				mLang.load(reader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 彈出警告訊息框
	 * 
	 * @param obj
	 *            要秀的訊息
	 * @param title
	 *            訊息框title
	 */
	public void showMsgBar(Object obj, String title) {
		JOptionPane.showMessageDialog(null, obj.toString(), title, JOptionPane.WARNING_MESSAGE);
	}

	public Font getComicListFont() {
		return mComicListFont;
	}

	public String getLangValue(String key) {
		return mLang.getProperty(key);
	}

	public String getComicHtml(String imageList) {
		String ret = "";
		StringBuilder html = new StringBuilder();

		try (Stream<String> stream = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream(mReaderHtml), mCharsetUtf8)).lines()) {
			stream.forEach(line -> {
				if (html.length() > 0) {
					html.append(System.lineSeparator());
				}
				html.append(line);
			});
			ret = String.format(html.toString(), imageList, getLangValue("FirstPage"), getLangValue("FinalPage"),
					getLangValue("PreviousPage"), getLangValue("NextPage"), getLangValue("PreviousPage"),
					getLangValue("NextPage"));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
