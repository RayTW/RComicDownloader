package JDownLoadComic;

import JDownLoadComic.parseHtml.SingleComicData;
import JDownLoadComic.util.LoadData;
import JDownLoadComic.util.LoadNetFile;

/**
 * 下載單集漫畫執行緒<BR>
 * 下載一本漫畫所有的圖片，一次一張下載
 * 
 * @author Ray
 * 
 */

public class DownLoadThread extends Thread {
	/** 父層(下載進度狀態列) */
	public LoadBarState parentObj;
	/** 一本漫畫資料 */
	private SingleComicData singleComic; // 一本漫畫資料
	/** 下載進度資料 */
	public LoadData load;
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
	 * @param lo
	 *            目前單本下載中漫畫的進度物件
	 * @param sc
	 */
	public void setSingleComicData(LoadData lo, SingleComicData sc) {
		singleComic = sc;
		load = lo;
		load.endValue = sc.getPageSize();
	}

	@Override
	public void run() {
		while (isRun) {
			// System.out.println("目前抓第幾張["+savePath+"]");
			if (!isPause) {
				if (jpgLoad != null) {
					if (!jpgLoad.isRuning()) {
						jpgLoad = null;
						point++;
						load.currentProeess = point;
						if (hasNext()) {
							startJpgLink();
						} else {
							isRun = false;
							break;
						}
					}
				}
			}
			try {
				Thread.sleep((int) (Config.pageSec * 1000));// 每一張下載時間間隔秒數
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		close();
	}

	/**
	 * 啟動執行緒下載單集漫畫
	 */
	public void startJpgLink() {
		if (jpgLoad == null) {
			load.endValue = singleComic.getPageSize();
			jpgLoad = new LoadNetFile();
			String url = singleComic.getJPGUrl(point);
			String nextUrl = singleComic.url;
			// String name = singleComic.name;
			// jpgLoad.setLoadData(data[point][1], savePath +
			// "/",data[point][0],true);
			jpgLoad.setRefererLoadData(url, nextUrl, savePath + "/",
					"" + point, true);
			jpgLoad.setLoadKB(Config.db.getDownLoadKB());
			jpgLoad.startLoad();
			// System.out.println("下載["+data[point][0]+"]==>"
			// +jpgLoad.isRuning() );
		}
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
	 * 下載完成時執行
	 */
	public void complete() {
		parentObj.close();
	}

	/**
	 * 清除此執行緒所使用到的資源、物件
	 * 
	 */
	public void close() {
		jpgLoad = null;
		load.currentProeess = load.endValue;
		singleComic = null;
		load = null;
		complete();
		parentObj = null;
	}

}
