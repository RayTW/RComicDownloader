package JDownLoadComic.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 以https方式讀取網頁資料(ssl)
 * 
 * @author Ray
 * 
 */
public class HttpsReader extends HttpReader {

	public HttpsReader() {
		HttpsReaderInit();
	}

	public void HttpsReaderInit() {

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
	@Override
	public InputStream getInputStream(String urlPath, String cookie,
			boolean isRedirect) throws IOException {
		URL url = new URL(urlPath);
		createTrustManager(url);

		HttpsURLConnection connection = (HttpsURLConnection) url
				.openConnection();
		connection.setRequestProperty("User-agent",
				getRequestProperty("User-agent"));
		connection.setRequestProperty("Cookie", cookie == null ? "" : cookie);
		connection.setInstanceFollowRedirects(isRedirect);// 設定目前的HttpURLConnection物件是否重新導向
		connection.connect();
		headerFields.clear();
		headerFields.putAll(connection.getHeaderFields());

		if ("gzip".equalsIgnoreCase(connection.getContentEncoding())) {
			return new GZIPInputStream(connection.getInputStream());
		}
		return connection.getInputStream();
	}

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
	@Override
	public InputStream openURL(String url, String data, String cookie,
			String referer, boolean post, boolean isRedirect)
			throws IOException {
		URL tmpurl = new URL(url);
		createTrustManager(tmpurl);
		HttpsURLConnection connection = (HttpsURLConnection) tmpurl
				.openConnection();

		connection.setDoOutput(true);
		connection.setDoInput(true);
		if (post) {
			connection.setRequestMethod("POST");
		}
		connection.setUseCaches(false);
		connection.setAllowUserInteraction(true);
		connection.setInstanceFollowRedirects(isRedirect);
		connection.setRequestProperty("User-agent",
				getRequestProperty("User-agent"));
		connection.setRequestProperty("Accept", getRequestProperty("Accept"));
		connection.setRequestProperty("Accept-Language",
				getRequestProperty("Accept-Language"));
		connection.setRequestProperty("Accept-Charse",
				getRequestProperty("Accept-Charse"));
		if (cookie != null) {
			connection.setRequestProperty("Cookie", cookie);
		}
		if (referer != null) {
			connection.setRequestProperty("Referer", referer);
		}

		if (post) {
			connection.setRequestProperty("Content-Type",
					getRequestProperty("Content-Type"));
			DataOutputStream dos = new DataOutputStream(
					connection.getOutputStream());
			dos.write(data.getBytes(charSet));
			dos.flush();
			dos.close();
		}
		headerFields.clear();

		if (connection.getHeaderFields() != null) {
			headerFields.putAll(connection.getHeaderFields());// 取得回傳的標頭資料
		}
		if ("gzip".equalsIgnoreCase(connection.getContentEncoding())) {
			return new GZIPInputStream(connection.getInputStream());
		}
		return connection.getInputStream();
	}

	/**
	 * 建立ssl憑證
	 * 
	 * @param urlObj
	 * @return
	 */
	private TrustManager createTrustManager(URL urlObj) {
		System.setProperty("java.protocol.handler.pkgs",
				"com.sun.net.ssl.internal.www.protocol");
		System.setProperty("javax.net.ssl.trustStore", "keystore");
		TrustManager trust = new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] chain, String authType) {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
			}
		};

		SSLContext sslcontext;
		try {
			sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { trust }, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext
					.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return trust;
	}

}
