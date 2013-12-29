package JDownLoadComic.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 2010/04/18 使用執行緒下載網路檔案,可同時產生多個同時下載
 * 
 * @author Ray
 * @deprecated 即將棄用的元件
 */

@Deprecated
public class LoadNetFile implements Runnable {
	private boolean isRun;
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

	public static final String ERROR = "error";
	public static final String FILE_EXISTED = "fileExist";

	public LoadNetFile() {
		initLoadNetFile();
	}

	public void initLoadNetFile() {
		isRun = true;
		urlPath = "";
		savePath = "";
		fileName = "";
		speedKB = 0;
	}

	/**
	 * 開始讀取檔案
	 * 
	 */
	public void startLoad() {
		Thread load = new Thread(this);
		load.start();
	}

	/**
	 * 停止讀取檔案
	 */
	public void stopLoad() {
		isRun = false;
	}

	/**
	 * 目前是否讀取中,true 讀取中,false 已停止
	 * 
	 * @return
	 */
	public boolean isRuning() {
		return isRun;
	}

	/**
	 * @param url
	 *            要抓的圖片網址
	 * @param saveP
	 *            儲存路徑
	 * @param tmpFname
	 *            存檔檔名(可不給副檔名)
	 * @param isOverrideFile
	 *            true 寫檔時檢查檔案是否已存在，已存在不蓋檔 | false 不檢查檔案是否存在，直接蓋檔
	 * @return
	 */
	public String setLoadData(String url, String saveP, String tmpFname,
			boolean isOverrideFile) {
		urlPath = url.replaceAll(" ", "%20");// 將空白字轉換成%20
		try {
			URL zeroFile = new URL(urlPath);

			String name = zeroFile.getFile();
			String tmpName = name.substring(name.lastIndexOf("."),
					name.length());// 取得副檔名
			fileName = ((tmpFname.indexOf(".") == -1) ? (tmpFname + tmpName)
					: tmpFname);
			savePath = saveP + fileName;

			File file = new File(savePath);

			if (isOverrideFile && file.exists()) {
				isRun = false;
				return FILE_EXISTED;
			}
			bufInStream = new BufferedInputStream(zeroFile.openStream());
			fileOutStream = new FileOutputStream(file.getPath());
		} catch (Exception e) {
			e.printStackTrace();
			close();
			return ERROR;
		}
		return "";
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
	public String setRefererLoadData(String url, String nextUrl, String saveP,
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
			savePath = saveP + fileName;
			File file = new File(savePath);
			// System.out.println(isOverrideFile +
			// "檔案["+fileName+"]是否已在"+file.exists());
			if (isOverrideFile && file.exists()) {
				isRun = false;
				return FILE_EXISTED;
			}
			HttpURLConnection urlconn = (HttpURLConnection) zeroFile
					.openConnection();
			urlconn.setRequestProperty("Referer", nextUrl);
			bufInStream = new BufferedInputStream(urlconn.getInputStream());
			fileOutStream = new FileOutputStream(savePath);
		} catch (Exception e) {
			e.printStackTrace();
			close();
			return ERROR;
		}
		return "";
	}

	/**
	 * 設定要下載的檔案網址與要存檔檔名
	 * 
	 * @param url
	 *            下載網址
	 * @param tmpFname
	 *            存檔檔名(可不給副檔名)
	 * @return
	 */
	public String setLoadData(String url, String saveP, String tmpFname) {
		return setLoadData(url, saveP, tmpFname, false);
	}

	@Override
	public void run() {
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
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				// System.out.println("檔案["+fileName+"]目前下載["+getCurrentKB()+"]kb");
			}
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
		isRun = false;
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

	/**
	 * 取得目前已下載位元數
	 * 
	 * @return
	 */
	public int getCurrentBits() {
		return currentBits;
	}

	/**
	 * 取得目前已下載kb
	 * 
	 * @return
	 */
	public double getCurrentKB() {
		double kb = Math.floor((currentBits / 1024.0) * 1000 + 0.001) / 1000;// 目前下載kb數
		return kb;
	}

	/**
	 * 取得存檔路徑
	 * 
	 * @return
	 */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * 取得下載檔名
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 關閉讀取檔案串流
	 * 
	 */
	public void close() {
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadNetFile lf = new LoadNetFile();

		// 絕對路徑
		lf.setLoadData(
				"http://news.sanxia.net.cn/upimg/allimg/090609/1143342.jpg",
				"C:/", "abc");
		// 相對路徑,自己指定副檔名
		// System.out.println(lf.setLoadData(args[0],"./" ,args[1]));
		lf.startLoad();
		// se566_ctl.cvssp.com
	}

}
