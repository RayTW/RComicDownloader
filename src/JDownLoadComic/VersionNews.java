package JDownLoadComic;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import JDownLoadComic.util.DownloadFile;
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
	private DownloadFile mDownloadFile;

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
				StringBuffer exception = new StringBuffer();
				String html = http.getHTML(Config.newUrl, Config.newsCharset,
						null, exception);

				if (exception.length() == 0) {
					// System.out.println(html);
					parse(html);
					news.setText(NEW_VERSION + versionText + "\n\n" + NEW_LIST
							+ newsText);
					if (hasNewVersion(nowVersion, versionText)) {
						downloadBtn.setEnabled(true);
					}
				} else {
					Config.showMsgBar(Config.netWorkDisconnect, "訊息");
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
			String[] oldVer = nowVersion.replaceFirst("[vV]", "").split("[.]");
			String[] newVer = versionText.replaceFirst("[vV]", "").split("[.]");

			for (int i = 0; i < newVer.length; i++) {
				if (Integer.parseInt(newVer[i]) > Integer.parseInt(oldVer[i])) {
					return true;
				}
			}
		}
		return false;
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
		if (mDownloadFile != null) {
			return;
		}
		mDownloadFile = new DownloadFile();

		class LoadStateJFrame extends JFrame {
			public int endValueDefault = 100;
			JProgressBar mJProgressBar;

			public LoadStateJFrame() {

				setEndValue(endValueDefault);
				add(mJProgressBar);
			}

			public void setEndValue(int endValue) {
				mJProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0,
						endValue);
			}

			public void setValue(int value) {
				mJProgressBar.setValue(value);
			}
		}
		URL urlObj = getCurrentRunAppPath();
		final LoadStateJFrame loadState = new LoadStateJFrame();
		loadState.setTitle("下載更新進度...");
		loadState.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		loadState.setUndecorated(true);// 去掉窗体修饰,包括最大化按钮
		// loadState.setResizable(false); //禁止改變視窗大小
		try {

			// 舊版本APP改名字放旁邊, "comic.jar" to "comic.jar~)
			final File file = new File(java.net.URLDecoder.decode(
					urlObj.getPath(), HtmlReader.UTF8));
			// 備份檔案的檔名
			final String backFileName = file.getAbsolutePath() + "~";
			copyFile(file, backFileName);
			file.delete();

			final String newFileName = appName + ".jar";
			String savePath = "." + java.io.File.separatorChar;
			loadState.setSize(250, 80);
			loadState.setLocation(Config.db.indexBounds.x
					+ Config.db.indexBounds.width / 2, Config.db.indexBounds.y
					+ Config.db.indexBounds.height / 2);
			loadState.setVisible(true);

			loadState.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub
					// super.windowClosed(e);
					System.out.println("windowClosed");
				}

				@Override
				public void windowStateChanged(WindowEvent e) {
					// TODO Auto-generated method stub
					// super.windowStateChanged(e);
					System.out.println("windowStateChanged");
				}

				@Override
				public void windowClosing(WindowEvent e) {
					System.out.println("windowClosing");
				}
			});

			mDownloadFile.createConnection(appUrl, savePath,
					new DownloadFile.Callback() {

						@Override
						public void onProgress(DownloadFile download,
								int readLength, int readedLength,
								int contentLength) {
							int updateProgressStatus = (int) (((readedLength * 1.0) / (contentLength * 1.0)) * loadState.endValueDefault);

							loadState.setValue(updateProgressStatus);
						}

						@Override
						public void onFinish(DownloadFile download) {
							// 啟動新的版本應用程式
							String cmd = "java -jar " + newFileName;
							try {
								Runtime.getRuntime().exec(cmd);
								new File(backFileName).delete();// 刪除備份檔
								System.exit(0);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onException(DownloadFile download,
								Exception e) {
							// 下載失敗，把備份檔還原
							File backFile = new File(backFileName);
							copyFile(backFile, file.getAbsolutePath());
							backFile.delete();
							mDownloadFile = null;
							Config.showMsgBar("下載更新檔失敗", "訊息");
						}
					});
			mDownloadFile.startDownload();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void copyFile(File oldfile, String newPath) {
		try {
			int byteread = 0;

			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldfile);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
