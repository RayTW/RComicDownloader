package rcomic.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import javax.net.ssl.HttpsURLConnection;

/**
 * 下載檔案，提供功能如下: 同步下載，建立DownloadFile之後，呼叫startSyncDownload()
 * 異步下載，建立DownloadFile之後，呼叫startDownload() 中途停止下載檔案，呼叫stopDownload() Callback
 * 為異步下載時使用，可獲得檔案下載時進度、exception、是否下載結束
 * 支援續載功能，若檔案已存在，並進使用此class進行下載時，會自動於request header 裡加上Range來續載
 * 
 * note: 例外情形，當已有下載的檔案，並向server要求檔案啟用了續載，向server request的range不符合時，
 * 會收到exception並且拿到response code 416
 * 
 * @author Ray
 * @version 2016/01/08
 * 
 */
public class DownloadFile {
	public static interface Callback {
		/**
		 * 下載時，readLength:每次讀取到的 byte,readedLength:目前已下載的byte, contentLength:檔案總長
		 */
		public void onProgress(DownloadFile download, int readLength, int readedLength, int contentLength);

		/** 下載完成或下載一半時被強制中止 */
		public void onFinish(DownloadFile download, String contextType, boolean isForceStop);

		/** 下載一半時發生Exception */
		public void onException(DownloadFile download, Exception e);
	}

	private boolean mDebug = false;

	private int mSleepSec = 10;// 每次從buffer取byte時的sleep秒數
	private int mBufferSize = 5 * 1024;
	private int mDownloaded;// 已下載的檔案長度
	private int mFileTotalLength;// 檔案總長
	private boolean mIsDownloading;// true:下載中
	private boolean mIsBytesEnable;// true:啟用續載功能，預設為false
	private String mUrl;
	private File mFile;// 此次下載的檔案
	private Callback mCallback;
	private URLConnection mURLConnection;
	private Thread mUnsyncThread;

	public DownloadFile() {
		init();
	}

	/**
	 * 建立下載連線
	 * 
	 * @param url
	 * @param savePath
	 * @param timeoutMillis
	 */
	public void createConnection(String url, String savePath, int timeoutMillis) {
		createConnection(url, savePath, timeoutMillis, null);
	}

	/**
	 * 建立下載連線
	 * 
	 * @param url
	 * @param folderPath
	 * @param timeoutMillis
	 * @param callback
	 */
	public void createConnection(String url, String folderPath, int timeoutMillis, Callback callback) {
		try {
			String[] ary = url.split("[/]|\\\\");
			createConnection(url, folderPath, ary[ary.length - 1], timeoutMillis, callback);
		} catch (Exception e) {
			e.printStackTrace();
			if (callback != null) {
				callback.onException(this, e);
			}
		}
	}

	/**
	 * 建立下載連線
	 * 
	 * @param url
	 * @param folderPath
	 * @param fileName
	 * @param timeoutMillis
	 * @param callback
	 */
	public void createConnection(String url, String folderPath, String fileName, int timeoutMillis, Callback callback) {
		init();
		mUrl = url;
		mCallback = callback;
		try {
			URL u = new URL(url);
			mURLConnection = u.openConnection();
			setTimeOut(timeoutMillis);
			mFile = new File(folderPath, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			if (callback != null) {
				callback.onException(this, e);
			}
		}
		log("2此次連線建立url[" + url + "],folderPath[" + folderPath + "],fileName[" + fileName + "]");
	}

	private void init() {
		mIsDownloading = false;
		mIsBytesEnable = false;
		mDownloaded = 0;
		mFileTotalLength = 0;
		mFile = null;
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	/**
	 * 設定連線逾時、讀取逾時秒數(1000 = 1秒)
	 * 
	 * @param timeoutMillis
	 */
	private boolean setTimeOut(int timeoutMillis) {
		if (mURLConnection != null) {
			mURLConnection.setReadTimeout(timeoutMillis);
			mURLConnection.setConnectTimeout(timeoutMillis);
			return true;
		}
		return false;
	}

	/**
	 * 每次從buffer取byte時的sleep秒數(1000 = 1秒)
	 * 
	 * @param sec
	 */
	public void setSleepTime(int time) {
		mSleepSec = time;
	}

	/**
	 * 非同步下載
	 * 
	 * @return
	 */
	public void startDownload() {
		try {
			mUnsyncThread = new Thread() {
				@Override
				public void run() {
					startSyncDownload();
				}
			};
			mUnsyncThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			if (mCallback != null) {
				mCallback.onException(this, e);
			}
		}
	}

	/**
	 * true:啟用續載功能，預設為true
	 * 
	 * @param bytesEnable
	 */
	public void setBytesEnable(boolean bytesEnable) {
		mIsBytesEnable = bytesEnable;
	}

	/**
	 * 同步方式下載
	 * 
	 * @param url
	 * @param savePath
	 * @return
	 */
	public boolean startSyncDownload() {
		if (mIsDownloading) {
			log("startSyncDownload() 目前下載中");
			return false;
		}

		mIsDownloading = true;
		OutputStream out = null;
		InputStream in = null;
		String contentType = "";
		boolean isForceStop = false;

		try {
			int tempDownloaded = 0;

			if (mIsBytesEnable) {
				if (mFile.exists()) {
					tempDownloaded = (int) mFile.length();
				}
				// 如果有支援續載再加上續載請求的header
				log("加上續載的header");
				mURLConnection.setRequestProperty("Range", "bytes=" + tempDownloaded + "-");
			} else {
				log("不使用續載");
			}
			mDownloaded = tempDownloaded;

			// 有關getheader()的使用都必須要在setRequestProperty之後，否則會有先連線再做設定header造成已連線的exception
			int contentLength = mURLConnection.getContentLength();// 若range超過server上的檔案大小會回傳-1
			if (contentLength < 0) {
				contentLength = 0;
			}
			contentType = mURLConnection.getContentType();
			mFileTotalLength = mDownloaded + contentLength;
			log("開始下載，已下載[" + mDownloaded + "],剩餘[" + contentLength + "],總長[" + mFileTotalLength + "],contextType["
					+ contentType + "]");
			log("startSyncDownload,file.getPath()==>" + mFile.getPath());

			out = getOutputStream(mFile.getPath());
			in = mURLConnection.getInputStream();
			int numRead = 0;
			byte[] buffer = new byte[mBufferSize];

			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				mDownloaded += numRead;
				if (mCallback != null) {
					mCallback.onProgress(this, numRead, mDownloaded, mFileTotalLength);
				}

				if (!mIsDownloading) {// 被中斷下載
					log("中斷下載");
					isForceStop = true;
					break;
				}
				try {
					Thread.sleep(mSleepSec);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mIsDownloading = false;
			isForceStop = true;
			// 下載時發生Exception
			if (mCallback != null) {
				mCallback.onException(this, e);
			}
			return false;
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mIsDownloading = false;
		log("1startSyncDownload(),isForceStop[" + isForceStop + "]");
		// 下載完成
		if (mCallback != null && (mDownloaded == mFileTotalLength)) {
			log("2startSyncDownload(),isForceStop[" + isForceStop + "],mCallback[" + mCallback + "]");
			mCallback.onFinish(this, contentType, isForceStop);
		}

		return true;
	}

	/**
	 * 每次下載時的buffer size，預設1024
	 * 
	 * @param buf
	 */
	public void setBuffer(int buf) {
		mBufferSize = buf;
	}

	/**
	 * 目前是否下載中
	 * 
	 * @return
	 */
	public boolean isDownloading() {
		return mIsDownloading;
	}

	/**
	 * 取得目前已下載的byte length
	 * 
	 * @return
	 */
	public int getDownloaded() {
		return mDownloaded;
	}

	/**
	 * 取得目前下載中檔案的總length
	 * 
	 * @return
	 */
	public int getTotalLength() {
		return mFileTotalLength;
	}

	/**
	 * 停止下載，若在下載中則會強制停止
	 */
	public void stopDownload() {
		mIsDownloading = false;
		if (mUnsyncThread != null) {
			mUnsyncThread.interrupt();
			mUnsyncThread = null;
		}
		if (mURLConnection != null) {
			if (mURLConnection instanceof HttpURLConnection) {
				getHttpURLConnection().disconnect();
			} else {
				getHttpsURLConnection().disconnect();
			}
		}
	}

	public URLConnection getURLConnection() {
		return mURLConnection;
	}

	public String digestFile(String algorithm) {
		return digestFile(mFile.getPath(), algorithm);
	}

	/**
	 * 將url 或 path 最後的檔名取出
	 * 
	 * @param url
	 * @return
	 */
	public static String getUrlFileName(String url) {
		String[] pathAry = url.split("[\\\\////]");
		String remoteFileName = "";

		if (pathAry.length > 0) {
			remoteFileName = pathAry[pathAry.length - 1];
		}
		return remoteFileName;
	}

	/**
	 * 取得Accept-Ranges，用來檢查是否支援斷線重新繼續下載
	 * 
	 * @return
	 */
	public String getAcceptRanges() {
		return mURLConnection.getHeaderField("Accept-Ranges");
	}

	/**
	 * 取得此次連線的response header
	 * 
	 * @param name
	 * @return
	 */
	public String getHeaderField(String name) {
		return mURLConnection.getHeaderField(name);
	}

	/**
	 * getResponseCode
	 * 
	 * @return
	 */
	public int getResponseCode() {
		try {
			if (mURLConnection instanceof HttpURLConnection) {
				return getHttpURLConnection().getResponseCode();

			} else {
				return getHttpsURLConnection().getResponseCode();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 取得此次下載的檔案
	 * 
	 * @return
	 */
	public File getFile() {
		return mFile;
	}

	protected HttpURLConnection getHttpURLConnection() {
		return (HttpURLConnection) mURLConnection;
	}

	protected HttpsURLConnection getHttpsURLConnection() {
		return (HttpsURLConnection) mURLConnection;
	}

	protected OutputStream getOutputStream(Object obj) throws FileNotFoundException {
		String filepath = obj.toString();
		FileOutputStream fos = null;

		if (mDownloaded == 0) {// 建立新檔
			log("建立新檔");
			fos = new FileOutputStream(filepath);
		} else {// 拿舊檔繼續寫入
			log("拿舊檔繼續寫入,downloaded[" + mDownloaded + "]");
			fos = new FileOutputStream(filepath, true);
		}

		return fos;
	}

	protected void log(Object obj) {
		if (mDebug) {
			System.out.println(obj);
		}
	}

	/**
	 * 取得此次下載的url
	 * 
	 * @return
	 */
	public String getUrl() {
		return mUrl;
	}

	/**
	 * 加密文件算法
	 * 
	 * @param filename
	 *            需要加密的文件名
	 * @param algorithm
	 *            加密算法名
	 */
	public static String digestFile(String path, String algorithm) {
		String hexString = "";
		FileInputStream fis = null;
		try {
			MessageDigest mMessageDigest = MessageDigest.getInstance(algorithm);
			fis = new FileInputStream(path);
			int len = 0;
			byte[] buf = new byte[1024 * 4];

			while ((len = fis.read(buf)) != -1) {
				mMessageDigest.update(buf, 0, len);
			}

			byte[] digestBytes = mMessageDigest.digest();
			hexString = new BigInteger(1, digestBytes).toString(16);
			int remain = 32 - hexString.length();
			while (remain != 0) {
				hexString = "0" + hexString;
				remain--;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return hexString;
	}

	/**
	 * 直接取得檔案的Length
	 * 
	 * @param url
	 * @param con
	 * @return
	 */
	public static int getContentLength(String url) {
		return getContentLength(url, 0);
	}

	/**
	 * 直接取得檔案的Length
	 * 
	 * @param url
	 * @param con
	 * @return
	 */
	public static int getContentLength(String url, int timeout) {
		int length = -1;
		try {
			URL u = new URL(url);
			URLConnection con = u.openConnection();
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
			length = con.getContentLength();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return length;
	}

	public static String getMD5EncryptedString(String encTarget) {
		java.security.MessageDigest mdEnc = null;
		try {
			mdEnc = java.security.MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		} // Encryption algorithm
		mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
		String md5 = new java.math.BigInteger(1, mdEnc.digest()).toString(16);
		return md5;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String url = "https://s3-ap-southeast-1.amazonaws.com/avnupdate/tw.com.elead.el817tmc1.2013.06.28.01.zip";
		// String url = "http://download.thinkbroadband.com/20MB.zip";
		String savePath = "./";

		// syncDownload(url, savePath);//同步
		unsyncDownload(url, savePath);// 非同步
	}

	// 同步下載
	public static void syncDownload(String url, String savePath) {
		DownloadFile download = new DownloadFile();
		download.createConnection(url, savePath, 5000);
		// download.setBytesEnable(true);
		boolean b = download.startSyncDownload();
		System.out.println("dowanload [" + b + "], md5[" + download.digestFile("MD5") + "]");
	}

	// 非同步方式下載
	public static DownloadFile unsyncDownload(String url, String savePath) {
		DownloadFile.Callback callback = new DownloadFile.Callback() {

			/** 下載時，readLength:每次讀取到的 byte,readedLength:目前已下載的byte */
			@Override
			public void onProgress(DownloadFile download, int readLength, int readedLength, int contentLength) {
				System.out.println("已下載[" + readedLength + "] 檔案總長[" + contentLength + "]");
			}

			/** 下載完成 */
			@Override
			public void onFinish(DownloadFile download, String contentType, boolean isForceStop) {
				System.out.println("非同步下載完成,md5[" + download.digestFile("MD5") + "], server header file md5["
						+ download.getHeaderField("x-amz-meta-checksum") + "]");
			}

			/** 下載一半時發生Exception */
			@Override
			public void onException(DownloadFile download, Exception e) {
				System.out.println("responseCode[" + download.getResponseCode() + "],onException==>" + download
						+ ",Exception=>" + e);

			}

		};
		// 9288372
		DownloadFile download = new DownloadFile();
		download.createConnection(url, savePath, 5000, callback);
		download.setTimeOut(30 * 1000);// 連線、讀取逾時(可選，預設為0，永無止盡的等下去...)
		// download.setBytesEnable(true);//是否啟用續載(預設false)
		download.startDownload();// 開始下載...
		return download;
	}

}
