package JDownLoadComic;

import JDownLoadComic.parseHtml.SingleComicData;
import JDownLoadComic.util.LoadNetFile;

/**
 * 下載單集漫畫執行緒<BR>
 * 下載一本漫畫所有的圖片，一次一張下載
 * 
 * @author Ray
 * 
 */

public class DownLoadThread implements Runnable {
	public interface Callback {
		public void onloading(int currentPage, int totalPage);

		public void onComplete();

		public void onDownloadFail(SingleComicData singleComic, String message);
	}

	/** 父層(下載進度狀態列) */
	public Callback mCallback;
	/** 一本漫畫資料 */
	private SingleComicData singleComic; // 一本漫畫資料
	/** 下載遠端檔案元件 */
	public LoadNetFile jpgLoad;
	/** 存檔路徑 */
	public String savePath; // 存檔路徑
	/** 目前下載到第幾張 */
	public int point;
	/** isRun:是否下載中,isPause:是否暫停中 */
	public boolean isRun, isPause;

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

			while (isRun) {
				if (!isPause) {
					if (mCallback != null) {
						mCallback.onloading(point, singleComic.getPageSize());
					}
					state = startJpgLink();

					if (hasNext() || state.equals(LoadNetFile.FILE_EXISTED)) {
						point++;
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
				if (state.equals(LoadNetFile.ERROR)) {
					mCallback.onDownloadFail(singleComic,
							Config.netWorkDisconnect);
				} else if (state.equals(LoadNetFile.FILE_EXISTED)) {
					mCallback.onDownloadFail(singleComic, Config.file_existed
							+ ":" + singleComic.name + ",夏數:" + point);
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
	 * 啟動執行緒下載單集漫畫
	 */
	private String startJpgLink() {
		String ret = "";
		if (jpgLoad == null) {
			int totalPageSize = singleComic.getPageSize();
			if (mCallback != null) {
				mCallback.onloading(point, totalPageSize);
			}
			jpgLoad = new LoadNetFile();
			String url = singleComic.getJPGUrl(point);
			String nextUrl = singleComic.url;

			// ret若為error表示下載失敗(可能是網路斷線)
			ret = jpgLoad.setRefererLoadData(url, nextUrl, savePath + "/", ""
					+ point, true);
			jpgLoad.setLoadKB(Config.db.getDownLoadKB());
			jpgLoad.startLoad();
		}
		return ret;
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
	 * 清除此執行緒所使用到的資源、物件
	 * 
	 */
	public void close() {
		jpgLoad = null;
		if (mCallback != null) {
			mCallback.onloading(singleComic.getPageSize(),
					singleComic.getPageSize());
		}
		singleComic = null;
		mCallback = null;
	}
}
