package JDownLoadComic.util;

import JDownLoadComic.Config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;

public class URLEncodeDecodeTool {

	public URLEncodeDecodeTool() {

	}

	public static String endoce(String s, String charset) {
		try {
			String txt = URLEncoder.encode(s, charset);
			// System.out.println(txt);

			return txt;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return s;
		}
	}

	public static String decode(String s, String charset) {
		try {
			String txt = URLDecoder.decode(s, charset);
			// System.out.println(txt);

			return txt;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return s;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReadFile rf = new ReadFile();
		String readerFile = "./" + Config.defaultSavePath + "/sys/reader.src";

		String html = rf.readFile("/Users/leeray/Desktop/newmessage.txt",
				"utf-8");
		String s = URLEncodeDecodeTool.endoce(html, "utf-8");
		System.out.println(s);
		// HtmlToBase64Tool.decode(s, "utf-8");
	}

}
