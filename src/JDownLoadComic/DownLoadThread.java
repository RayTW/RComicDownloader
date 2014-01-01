package JDownLoadComic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import JDownLoadComic.parseHtml.SingleComicData;

/**
 * 下載單集漫畫執行緒<BR>
 * 下載一本漫畫所有的圖片，一次一張下載
 * 
 * @author Ray
 * 
 */

public class DownLoadThread implements Runnable {
	public static final String ERROR = "error";
	public static final String FILE_EXISTED = "fileExist";

	public interface Callback {
		public void onloading(int currentPage, int totalPage);

		public void onComplete();

		public void onDownloadFail(SingleComicData singleComic, String message);
	}

	/** 父層(下載進度狀態列) */
	public Callback mCallback;
	/** 一本漫畫資料 */
	private SingleComicData singleComic; // 一本漫畫資料
	/** 目前下載到第幾張 */
	public int point;
	/** isRun:是否下載中,isPause:是否暫停中 */
	public boolean isRun, isPause;

	/** 讀取檔案的網址 */
	private String urlPath;
	/** 存檔路徑 */
	private String savePath;
	/** 檔名 */
	private String fileName;
	/** 每秒下載kb,0:不限制 */
	private int speedKB; // 每秒下載kb,0:不限制
	/** 目前讀取的byte */
	private int currentBits;
	/** 讀取檔案的緩衝+串流 */
	private BufferedInputStream bufInStream;
	/** 寫出檔案的串檔 */
	private FileOutputStream fileOutStream;

	public DownLoadThread() {
		initDownLoadThread();
	}

	/**
	 * 初始化，並給予初始值
	 */
	public void initDownLoadThread() {
		isRun = true;
		isPause = false;
		point = 0;
		savePath = "";
		urlPath = "";
		savePath = "";
		fileName = "";
		speedKB = 0;
	}

	public void setSavePath(String path) {
		savePath = path;
	}

	/**
	 * 設定單本漫畫下載需要使用到的資料
	 * 
	 * @param callback
	 *            目前單本下載中漫畫的進度物件
	 * @param sc
	 */
	public void setSingleComicData(Callback callback, SingleComicData sc) {
		singleComic = sc;
		mCallback = callback;
	}

	@Override
	public void run() {
		try {
			String state = "";

			while (isRun && hasNext()) {
				if (!isPause) {
					state = setDownloadSinglePageData(point, singleComic,
							savePath);
					// 要下載的圖頁已存在，換下一張
					if (state.equals(FILE_EXISTED)) {
						point++;
						if (mCallback != null) {
							mCallback.onloading(point,
									singleComic.getPageSize());
						}
						continue;
					}

					if (startDownload()) {
						point++;
						if (mCallback != null) {
							mCallback.onloading(point,
									singleComic.getPageSize());
						}
					} else {
						isRun = false;
						break;
					}
				}
				try {
					Thread.sleep((int) (Config.pageSec * 1000));// 每一張下載時間間隔秒數
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (mCallback != null) {
				if (state.equals(ERROR)) {
					mCallback.onDownloadFail(singleComic,
							Config.netWorkDisconnect);
				} else if (state.equals(FILE_EXISTED)) {
					mCallback.onDownloadFail(singleComic, Config.file_existed);
				} else {
					mCallback.onComplete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * 設定要下載單頁的圖片URL與儲存路徑
	 */
	private String setDownloadSinglePageData(int index,
			SingleComicData singleData, String path) {
		String ret = "";
		String url = singleData.getJPGUrl(index);
		String nextUrl = singleData.url;

		// ret若為error表示下載失敗(可能是網路斷線)
		ret = readyDownload(url, nextUrl, path + "/", "" + index, true);
		setLoadKB(Config.db.getDownLoadKB());
		return ret;
	}

	/**
	 * 設定每秒下載速度
	 * 
	 * @param kb
	 */
	public void setLoadKB(int kb) {
		if (kb < 0) {
			kb = 0;
		}
		speedKB = kb;
	}

	public int getTotalPageSize() {
		if (singleComic != null) {
			return singleComic.getPageSize();
		}
		return 0;
	}

	/**
	 * 停止 下載單集漫畫
	 * 
	 */
	public void stopJpgLink() {
		isRun = false;
	}

	/**
	 * 暫停 下載單集漫畫
	 * 
	 */
	public void pauseJpgLink() {
		isPause = true;
	}

	/**
	 * 繼續下載 下載單集漫畫
	 * 
	 */
	public void restartJpgLink() {
		isPause = false;
	}

	/**
	 * 檢查是否有下一頁
	 * 
	 * @return
	 */
	public boolean hasNext() {
		return point < singleComic.getPageSize();
	}

	/**
	 * 需給上一頁網址才可以下載檔案，無名、無限動漫...
	 * 
	 * @param url
	 *            要抓的圖片網址
	 * @param nextUrl
	 *            上一頁網址
	 * @param saveP
	 *            儲存路徑
	 * @param tmpFname
	 *            存檔檔名(可不給副檔名)
	 * @param isOverrideFile
	 *            true 寫檔時檢查檔案是否已存在，已存在不蓋檔 | false 不檢查檔案是否存在，直接蓋檔
	 * @return
	 */
	public String readyDownload(String url, String nextUrl, String saveP,
			String tmpFname, boolean isOverrideFile) {
		urlPath = url.replaceAll(" ", "%20");// 將空白字轉換成%20
		try {
			URL zeroFile = new URL(urlPath);

			String name = zeroFile.getFile();
			String tmpName = name.substring(name.lastIndexOf("."),
					name.length());// 取得副檔名
			fileName = ((tmpFname.indexOf(".") == -1) ? (tmpFname + tmpName)
					: tmpFname);
			// System.out.println("副檔名==>" + tmpName + ",===" + fileName);
			String path = saveP + fileName;
			File file = new File(path);
			// System.out.println(isOverrideFile +
			// "檔案["+fileName+"]是否已在"+file.exists());
			if (isOverrideFile && file.exists()) {
				return FILE_EXISTED;
			}
			HttpURLConnection urlconn = (HttpURLConnection) zeroFile
					.openConnection();
			urlconn.setRequestProperty("Referer", nextUrl);
			bufInStream = new BufferedInputStream(urlconn.getInputStream());
			fileOutStream = new FileOutputStream(path);
		} catch (Exception e) {
			e.printStackTrace();
			close();
			return ERROR;
		}
		return "";
	}

	/**
	 * 開始下載彈張圖
	 */
	public boolean startDownload() {
		try {
			// System.out.println("開始下載["+fileName+"]");
			byte[] b = new byte[1024];// 一次取得 1024 個 bytes
			int len, bits = 0;// len單次讀取位元數,bits每秒累計下載位元數
			while (isRun && (len = bufInStream.read(b, 0, b.length)) != -1) {
				fileOutStream.write(b, 0, len);
				bits += len;
				currentBits += len;
				if (bits >= (speedKB * 1024) && speedKB != 0) {// 1秒下載的kb足夠就sleep1秒
					bits = 0;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
				// System.out.println("檔案["+fileName+"]目前下載["+getCurrentKB()+"]kb");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		closeIO();
		return true;
	}

	/**
	 * 清除此執行緒所使用到的資源、物件
	 * 
	 */
	public void close() {
		if (mCallback != null) {
			mCallback.onloading(singleComic.getPageSize(),
					singleComic.getPageSize());
		}
		singleComic = null;
		mCallback = null;
	}

	public void closeIO() {
		try {
			if (fileOutStream != null) {
				fileOutStream.close();
			}
			if (bufInStream != null) {
				bufInStream.close();
			}
			fileOutStream = null;
			bufInStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
