package JDownLoadComic;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import JDownLoadComic.util.HtmlReader;
import JDownLoadComic.util.URLEncodeDecodeTool;
import JDownLoadComic.util.HttpReader;

/**
 * 讀取漫畫下載最新版本資訊，並提供線上更新漫畫版本
 * 
 * @author Ray
 * 
 */
public class VersionNews extends JPanel {
	public final static String TAG_COMIC_NEWS = "comicnews";
	public final static String TAG_VERSION = "version";
	public final static String TAG_NEWS = "news";
	public final static String TAG_APP_URL = "appurl";
	public final static String TAG_APP_NAME = "appname";
	public final static String NEW_VERSION = "最新版本:";
	public final static String NEW_LIST = "更新項目:";
	private HttpReader http;
	private JTextArea news;
	private JButton downloadBtn;
	private String versionText;
	private String newsText;
	private String appName;
	private String appUrl;

	public VersionNews() {
		setLayout(new BorderLayout());
		init();
	}

	public void init() {
		versionText = "";
		newsText = "";
		appUrl = "";
		http = new HttpReader();
		news = new JTextArea();
		news.setLineWrap(true);
		news.setEditable(false);

		add(news, BorderLayout.CENTER);
		downloadBtn = new JButton("下載更新");
		downloadBtn.setEnabled(false);
		downloadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ret = JOptionPane.showConfirmDialog(null, "是否更新版本？", "訊息",
						JOptionPane.YES_NO_OPTION);
				// 判斷使用者選擇要更新才執行
				if (JOptionPane.YES_OPTION == ret) {
					downloadBtn.setEnabled(false);

					new Thread() {
						@Override
						public void run() {
							upVersion();
						}
					}.start();
					downloadBtn.setEnabled(true);
				}
			}
		});
		add(downloadBtn, BorderLayout.SOUTH);
	}

	/**
	 * 刷新版本更新資訊
	 */
	public void refrush(final String nowVersion) {
		new Thread() {
			@Override
			public void run() {
				String html = http.getHTML(Config.newUrl, Config.newsCharset);
				// System.out.println(html);
				parse(html);
				news.setText(NEW_VERSION + versionText + "\n\n" + NEW_LIST
						+ newsText);
				if (hasNewVersion(nowVersion, versionText)) {
					downloadBtn.setEnabled(true);
				}
			}

		}.start();
	}

	/**
	 * true:有新版本
	 * 
	 * @param nowVersion
	 *            目前版本
	 * @param versionText
	 *            新版本
	 * @return
	 */
	private boolean hasNewVersion(String nowVersion, String versionText) {
		if (versionText.length() > 0) {
			int oldVer = versionToInt(nowVersion);
			int newVer = versionToInt(versionText);

			return (newVer > oldVer);
		}
		return false;
	}

	/**
	 * 將版本號轉為數字
	 * 
	 * @param version
	 * @return
	 */
	private int versionToInt(String version) {
		int sum = 0;
		String ver = version.substring(1, version.length());
		String[] ary = ver.split("[.]");
		for (String v : ary) {
			sum += Integer.parseInt(v);
		}
		return sum;
	}

	/**
	 * 取得目前執行本身的JAR路徑
	 */
	public URL getCurrentRunAppPath() {
		return getClass().getProtectionDomain().getCodeSource().getLocation();
	}

	public void parse(String html) {
		String text = parseTag(html, TAG_COMIC_NEWS);
		text = URLEncodeDecodeTool.decode(text, Config.newsCharset);

		versionText = parseTag(text, TAG_VERSION);
		newsText = parseTag(text, TAG_NEWS);
		appUrl = parseTag(text, TAG_APP_URL);
		appName = parseTag(text, TAG_APP_NAME);
	}

	/**
	 * 取得指定TAG裡的值
	 * 
	 * @param html
	 * @param tag
	 * @return
	 */
	private String parseTag(String html, String tag) {
		String String = "";
		String startTag = "<" + tag + ">";
		String endTag = "</" + tag + ">";

		int startIndex = html.indexOf(startTag);

		if (startIndex != -1) {
			String = html.substring(startIndex + startTag.length(),
					html.indexOf(endTag));
		}
		return String;
	}

	/**
	 * 下載新版本，刪除舊版本，並重新啟動新版本
	 */
	public void upVersion() {
		URL urlObj = getCurrentRunAppPath();

		try {

			// 刪除舊版本漫畫下載
			File file = new File(java.net.URLDecoder.decode(urlObj.getPath(),
					HtmlReader.UTF8));
			file.delete();
			final String newFileName = appName + ".jar";
			http.loadURLFile(appUrl, "." + java.io.File.separatorChar,
					newFileName);
			// 啟動新的版本應用程式
			String cmd = "java -jar " + newFileName;
			Runtime.getRuntime().exec(cmd);
			// Process process = Runtime.getRuntime().exec(cmd);
			// String errorStr = "";
			// File jarFile = new File("./" + newFileName);
			//
			// BufferedReader br = new BufferedReader(new
			// InputStreamReader(process.getErrorStream()));
			// for (String line = br.readLine(); line != null; line =
			// br.readLine()) {
			// errorStr +=line;
			// }
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
