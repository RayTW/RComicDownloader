package net.xuite.blog.ray00000test.rdownloadcomic.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 讀取html資料
 * 
 * @author Ray
 * 
 */
public abstract class HtmlReader {
	public static final String BIG5 = "big5";
	public static final String UTF8 = "utf-8";
	/** 讀取資料使用的buffer大小(預設8KB) */
	protected int bufferReaderKB = 8;
	protected HashMap headerFields;
	/** 封包表頭設定 */
	protected HashMap requestProperty;

	public HtmlReader() {

		HtmlReaderInit();
		defaultRequestProperty();
	}

	public void HtmlReaderInit() {
		headerFields = new HashMap();
		requestProperty = new HashMap();
	}

	/**
	 * 預設的網頁封包表頭
	 */
	public void defaultRequestProperty() {
		requestProperty
				.put("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
		requestProperty
				.put("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		requestProperty.put("Accept-Language", "zh-tw,en-us;q=0.7,en;q=0.3");
		requestProperty.put("Accept-Charse", "Big5,utf-8;q=0.7,*;q=0.7");
		requestProperty
				.put("Content-Type", "application/x-www-form-urlencoded");
	}

	/**
	 * 設定讀取資料的暫存區大小(預設8KB)
	 * 
	 * @param kb
	 */
	public void setBufferReaderSize(int kb) {
		bufferReaderKB = kb;
	}

	/**
	 * 取得讀取資料的暫存區大小
	 * 
	 * @return
	 */
	public int getBufferReaderSize() {
		return bufferReaderKB;
	}

	/**
	 * 讀取遠端檔案，遠端檔案可以為任何格式rar,rmvb,jpg...
	 * 註:此method一旦開始讀檔之後就會整個hold在此method,所以不建議讀大檔,取用讀大檔請用LoadNetFile Class
	 * 
	 * @param urlPath
	 *            檔案網址
	 * @param savePath
	 *            存檔路徑
	 * @param fileName
	 *            檔名(需指定副檔名)
	 * @return 若下載發生exception會回傳exception名稱
	 */
	public String loadURLFile(String urlPath, String savePath, String fileName) {
		StringBuffer exception = new StringBuffer();
		loadURLFile(urlPath, savePath, fileName, exception);
		return exception.toString();
	}

	/**
	 * 讀取遠端檔案，遠端檔案可以為任何格式rar,rmvb,jpg...
	 * 註:此method一旦開始讀檔之後就會整個hold在此method,所以不建議讀大檔,取用讀大檔請用LoadNetFile Class
	 * 
	 * @param urlPath
	 *            檔案網址
	 * @param savePath
	 *            存檔路徑
	 * @param fileName
	 *            檔名(需指定副檔名)
	 * @param exception
	 *            exception名稱
	 * @return true:下載成功, false:下載失敗
	 */
	public boolean loadURLFile(String urlPath, String savePath,
			String fileName, StringBuffer exception) {
		try {
			URL zeroFile = new URL(urlPath);
			InputStream is = zeroFile.openStream();

			BufferedInputStream bs = new BufferedInputStream(is,
					bufferReaderKB * 1024);
			byte[] b = new byte[1024];// 一次取得 1024 個 bytes
			FileOutputStream fs = new FileOutputStream(savePath + "/"
					+ fileName);
			int len;
			while ((len = bs.read(b, 0, b.length)) != -1) {
				fs.write(b, 0, len);
			}
			// System.out.println("done====>");
			bs.close();
			fs.close();

		} catch (IOException e) {
			e.printStackTrace();
			if (exception != null) {
				exception.append(e.getMessage());
			}
			return false;
		}
		return true;
	}

	/**
	 * 取得整個網頁的html,使用BIG5格式編碼
	 * 
	 * @param urlPath
	 *            網址,例如:http://tw.yahoo.com/
	 * @return
	 */
	public String getHTML_BIG5(String urlPath) {
		return getHTML(urlPath, BIG5);
	}

	/**
	 * 取得整個網頁的html,使用BIG5格式編碼
	 * 
	 * @param urlPath
	 *            網址
	 * @return
	 */
	public String getHTML_UTF8(String urlPath) {
		return getHTML(urlPath, UTF8);
	}

	/**
	 * 取得整個網頁的html,使用BIG5格式編碼
	 * 
	 * @param urlPath
	 *            網址,例如:http://tw.yahoo.com/
	 * @param exception
	 * @return
	 */
	public String getHTML_BIG5(String urlPath, StringBuffer exception) {
		return getHTML(urlPath, BIG5, null, exception);
	}

	/**
	 * 取得整個網頁的html,使用BIG5格式編碼
	 * 
	 * @param urlPath
	 *            網址
	 * @param exception
	 * @return
	 */
	public String getHTML_UTF8(String urlPath, StringBuffer exception) {
		return getHTML(urlPath, UTF8, null, exception);
	}

	/**
	 * 取得整個網頁的html
	 * 
	 * @param urlPath
	 *            網址,例如:http://tw.yahoo.com/
	 * @param fmt
	 *            網頁編碼格式 utf8,big5
	 * @return
	 */
	public String getHTML(String urlPath, String fmt) {
		return getHTML(urlPath, fmt, null);
	}

	/**
	 * 取得整個網頁的html
	 * 
	 * @param urlPath
	 * @param fmt
	 * @param cookie
	 * @return
	 */
	public String getHTML(String urlPath, String fmt, String cookie) {
		String total = "";

		try {
			total = getHTMLNoTry(urlPath, fmt, cookie);
		} catch (IOException e) {
			e.printStackTrace();
			total = "<code>994</code><data></data>";
			System.out.println("getHTML取得網頁html時發生錯誤");
		}
		return total.toString();
	}

	/**
	 * 取網頁資料，可得知是否有exception
	 * 
	 * @param urlPath
	 * @param fmt
	 * @param cookie
	 * @param exception
	 * @return
	 */
	public String getHTML(String urlPath, String fmt, String cookie,
			StringBuffer exception) {
		String total = "";

		try {
			total = getHTMLNoTry(urlPath, fmt, cookie);
		} catch (IOException e) {
			e.printStackTrace();

			if (exception != null) {
				exception.append(e.getMessage());
			}
		}
		return total.toString();
	}

	/**
	 * 抓網頁資料，不做try-catch
	 * 
	 * @param urlPath
	 * @param fmt
	 * @param cookie
	 * @return
	 * @throws IOException
	 */
	public String getHTMLNoTry(String urlPath, String fmt, String cookie)
			throws IOException {
		StringBuffer total = new StringBuffer();
		InputStream inStream = getInputStream(urlPath, cookie);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inStream, fmt), bufferReaderKB * 1024);
		String line = "";

		while ((line = reader.readLine()) != null) {
			total.append(line + "\r\n");
		}
		inStream.close();
		reader.close();
		return total.toString();
	}

	/**
	 * 取得整個網頁的html,將讀回來的html每行字串為一筆資料存到ArrayList
	 * 
	 * @param urlPath
	 * @param fmt
	 * @return
	 */
	public ArrayList getHTMLtoArrayList(String urlPath, String fmt) {
		return getHTMLtoArrayList(urlPath, fmt, null);
	}

	/**
	 * 取得整個網頁的html,將讀回來的html每行字串為一筆資料存到ArrayList
	 * 
	 * @param urlPath
	 * @param fmt
	 * @param cookie
	 * @return
	 */
	public ArrayList getHTMLtoArrayList(String urlPath, String fmt,
			String cookie) {
		ArrayList list = new ArrayList();
		try {
			InputStream inStream = getInputStream(urlPath, cookie);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inStream, fmt), bufferReaderKB * 1024);
			String line = "";

			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
			inStream.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("getHTMLtoArrayList取得網頁html時發生錯誤");
		}
		return list;
	}

	/**
	 * 
	 * @param urlPath
	 * @param cookie
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream(String urlPath, String cookie)
			throws IOException {
		return getInputStream(urlPath, cookie, true);
	}

	/**
	 * 取得開啟網頁連線後的讀取網頁資料串流,回傳的標頭資料會存在 headerFields,預設為重導頁面
	 * 
	 * @param url
	 *            要連結的網址
	 * @param data
	 *            post要串後面的資料
	 * @param cookie
	 *            要送過去的cookie，null:表示不設定cookie
	 * @param referer
	 *            設定目前網站連結由哪一頁(網址)發送的
	 * @param post
	 *            true : 用post方式 開網頁 | false: 用get方式開網頁
	 */
	public InputStream openURL(String url, String data, String cookie,
			String referer, boolean post) throws IOException {
		return openURL(url, data, cookie, referer, post, true);// true 表示預設為重導頁面
	}

	/**
	 * 
	 * @param urlPath
	 *            url
	 * @param cookie
	 *            cookie
	 * @param isRedirect
	 *            設定目前的HttpURLConnection物件是否重新導向
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream getInputStream(String urlPath, String cookie,
			boolean isRedirect) throws IOException;

	/**
	 * 取得開啟網頁連線後的讀取網頁資料串流,回傳的標頭資料會存在 headerFields
	 * 
	 * @param url
	 *            要連結的網址
	 * @param data
	 *            post要串後面的資料
	 * @param cookie
	 *            要送過去的cookie，null:表示不設定cookie
	 * @param referer
	 *            設定目前網站連結由哪一頁(網址)發送的
	 * @param post
	 *            true : 用post方式 開網頁 | false: 用get方式開網頁
	 */
	public abstract InputStream openURL(String url, String data, String cookie,
			String referer, boolean post, boolean isRedirect)
			throws IOException;

	/**
	 * 取得連完網頁後的標頭資料(資料為原始HttpURLConnection裡的map取出來)
	 * 
	 * @param key
	 * @return
	 */
	public String getHeaderField(String key) {
		String fieldValue = "";
		if (headerFields != null) {
			Object obj = headerFields.get(key);
			if (obj != null) {
				fieldValue = obj.toString();
				// fieldValue = fieldValue.substring(0, fieldValue.length() -
				// 1);
			}
		}
		return fieldValue;
	}

	/**
	 * 將header重新整理一次，去除value第0個字元
	 * 
	 * @return
	 */
	protected void substringHeader() {
		if (headerFields != null) {
			HashMap closeHeader = (HashMap) headerFields.clone();
			Set set = closeHeader.entrySet();
			Iterator t = set.iterator();

			while (t.hasNext()) {
				Map.Entry m = (Map.Entry) t.next();
				t.remove();// 加上此method，修正會產生ConcurrentModificationException
				String key = (String) m.getKey();
				String value = m.getValue().toString();

				if (key != null) {
					headerFields.put(key.toLowerCase(),
							value.substring(1, value.length() - 1));
				}
			}
		}
	}

	/**
	 * 清除表頭Hash資料
	 */
	public void clearHeaderData() {
		headerFields.clear();
	}

	/**
	 * 取得表頭hash資料
	 * 
	 * @return
	 */
	public HashMap getHeader() {
		return headerFields;
	}

	/**
	 * 設定User-agent、Accept等等資料
	 * 
	 * @param key
	 * @param value
	 */
	public void putRequestProperty(String key, String value) {
		requestProperty.put(key, value);
	}

	/**
	 * 取得User-agent、Accept等等資料
	 * 
	 * @param key
	 * @return
	 */
	public String getRequestProperty(String key) {
		return (String) requestProperty.get(key);
	}

	/**
	 * 移除User-agent、Accept等等資料
	 * 
	 * @param key
	 * @return
	 */
	public String removeRequestProperty(String key) {
		return (String) requestProperty.remove(key);
	}
}
