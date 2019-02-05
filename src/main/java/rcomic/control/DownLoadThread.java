package rcomic.control;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import net.xuite.blog.ray00000test.library.comicsdk.Episode;

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

	public interface Listener {
		public void onloading(int currentPage, int totalPage);

		public void onComplete();

		public void onDownloadFail(Episode episode, String message);
	}

	/** 父層(下載進度狀態列) */
	public Listener mListener;
	private String mComicUrl;
	/** 一本漫畫資料 */
	private Episode mEpisode; // 一本漫畫資料
	/** 目前下載到第幾張 */
	public int mCurrentPage;
	/** 是否下載中 */
	private boolean mIsRun;

	/** 讀取檔案的網址 */
	private String mUrl;
	/** 存檔路徑 */
	private String mSavePath;
	/** 檔名 */
	private String mFileName;
	/** 每秒下載kb,0:不限制 */
	private int mSpeedKB; // 每秒下載kb,0:不限制
	/** 目前讀取的byte */
	private int mCurrentBits;
	/** 讀取檔案的緩衝+串流 */
	private BufferedInputStream mBufInStream;
	/** 寫出檔案的串檔 */
	private FileOutputStream mFileOutStream;

	public DownLoadThread() {
		initDownLoadThread();
	}

	/**
	 * 初始化，並給予初始值
	 */
	public void initDownLoadThread() {
		mIsRun = true;
		mCurrentPage = 0;
		mSavePath = "";
		mUrl = "";
		mSavePath = "";
		mFileName = "";
		mSpeedKB = Integer.MAX_VALUE;
	}

	public void setSavePath(String path) {
		mSavePath = path;
	}

	/**
	 * 設定單本漫畫下載需要使用到的資料
	 * 
	 * @param callback
	 *            目前單本下載中漫畫的進度物件
	 * @param comicUrl
	 * @param sc
	 */
	public void setEpisode(Listener callback, String comicUrl, Episode sc) {
		mEpisode = sc;
		mComicUrl = comicUrl;
		mListener = callback;
	}

	@Override
	public void run() {
		try {
			String state = "";

			while (mIsRun && hasNext()) {
				state = setDownloadSinglePageData(mCurrentPage, mEpisode, mSavePath);
				// 要下載的圖頁已存在，換下一張
				if (state.equals(FILE_EXISTED)) {
					mCurrentPage++;
					if (mListener != null) {
						mListener.onloading(mCurrentPage, mEpisode.getPages());
					}
					continue;
				}

				if (startDownload()) {
					mCurrentPage++;
					if (mListener != null) {
						mListener.onloading(mCurrentPage, mEpisode.getPages());
					}
				} else {
					mIsRun = false;
					break;
				}
			}

			if (mListener != null) {
				if (state.equals(ERROR)) {
					mListener.onDownloadFail(mEpisode, RComic.get().getConfig().getLangValue("MsgNetWorkDisconnect"));
				} else if (state.equals(FILE_EXISTED)) {
					mListener.onDownloadFail(mEpisode, RComic.get().getConfig().getLangValue("ComicExisted"));
				} else {
					mListener.onComplete();
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
	private String setDownloadSinglePageData(int index, Episode episode, String path) {
		String ret = "";
		String url = episode.getImageUrlList().get(index);
		String nextUrl = mComicUrl;

		// ret若為error表示下載失敗(可能是網路斷線)
		ret = readyDownload(url, nextUrl, path + "/", "" + index, true);
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
		mSpeedKB = kb;
	}

	public int getTotalPageSize() {
		if (mEpisode != null) {
			return mEpisode.getPages();
		}
		return 0;
	}

	/**
	 * 停止 下載單集漫畫
	 * 
	 */
	public void stopJpgLink() {
		mIsRun = false;
	}

	/**
	 * 檢查是否有下一頁
	 * 
	 * @return
	 */
	public boolean hasNext() {
		return mCurrentPage < mEpisode.getPages();
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
	public String readyDownload(String url, String nextUrl, String saveP, String tmpFname, boolean isOverrideFile) {
		mUrl = url.replaceAll(" ", "%20");// 將空白字轉換成%20
		try {
			URL zeroFile = new URL(mUrl);

			String name = zeroFile.getFile();
			String tmpName = name.substring(name.lastIndexOf("."), name.length());// 取得副檔名
			mFileName = ((tmpFname.indexOf(".") == -1) ? (tmpFname + tmpName) : tmpFname);
			String path = saveP + mFileName;
			File file = new File(path);

			if (isOverrideFile && file.exists()) {
				return FILE_EXISTED;
			}
			HttpURLConnection urlconn = (HttpURLConnection) zeroFile.openConnection();
			urlconn.setRequestProperty("Referer", nextUrl);
			mBufInStream = new BufferedInputStream(urlconn.getInputStream());
			mFileOutStream = new FileOutputStream(path);
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
			while (mIsRun && (len = mBufInStream.read(b, 0, b.length)) != -1) {
				mFileOutStream.write(b, 0, len);
				bits += len;
				mCurrentBits += len;
				if (bits >= (mSpeedKB * 1024) && mSpeedKB != 0) {// 1秒下載的kb足夠就sleep1秒
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
		if (mListener != null) {
			mListener.onloading(mEpisode.getPages(), mEpisode.getPages());
		}
		mEpisode = null;
		mListener = null;
	}

	public void closeIO() {
		try {
			if (mFileOutStream != null) {
				mFileOutStream.close();
			}
			if (mBufInStream != null) {
				mBufInStream.close();
			}
			mFileOutStream = null;
			mBufInStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
