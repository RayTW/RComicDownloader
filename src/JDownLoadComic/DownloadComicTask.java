package JDownLoadComic;

import java.util.concurrent.Future;

import JDownLoadComic.parseHtml.ActDataObj;
import JDownLoadComic.parseHtml.SingleComicData;
import JDownLoadComic.util.ThreadPool;
import JDownLoadComic.util.WriteFile;

public class DownloadComicTask implements DownLoadThread.Callback {
	private String checkName;// 此任務的名稱
	private LoadBarState mLoadBarState;
	/** 下載單集漫畫執行緒 */
	private DownLoadThread mDownLoadThread;
	private Future<?> mFuture;
	private SingleComicData mSingleComicData;
	private String savePath;
	private EventHandle_Act parentObj;

	public DownloadComicTask(ActDataObj actObj, int index) {
		mSingleComicData = actObj.getActComicData(index);
		savePath = "./" + Config.defaultSavePath + "/" + actObj.cartoonName
				+ "/" + actObj.getAct(index);
		checkName = actObj.id + actObj.cartoonName.trim() + "-"
				+ mSingleComicData.name.trim();
		LoadBarState loadb = new LoadBarState();
		loadb.setParentObj(this);
		loadb.setIdName(checkName);
		loadb.setLoadName(actObj.cartoonName + "-" + mSingleComicData.name);
		loadb.setBarText("準備下載");
		mLoadBarState = loadb;
	}

	public LoadBarState getmLoadBarState() {
		return mLoadBarState;
	}

	public void setParent(EventHandle_Act parent) {
		parentObj = parent;
	}

	public void setLoadBarState(LoadBarState state) {
		mLoadBarState = state;
	}

	public Future<?> getmFuture() {
		return mFuture;
	}

	public void setFuture(Future<?> future) {
		mFuture = future;
	}

	public boolean equalsComicName(String name) {
		if (checkName != null) {
			return checkName.equals(name);
		}
		return false;
	}

	public String getCheckName() {
		return checkName;
	}

	/**
	 * 開始下載漫畫,執行此METHOD之前需先呼叫setDownloadPath()
	 * 
	 * @param singleComic
	 *            單本漫畫資料
	 * @param savePath
	 *            漫畫存檔路徑
	 */
	public Future<?> createThreadTask() {
		if (mFuture == null) {
			if (mSingleComicData.setPageList()) {
				mDownLoadThread = new DownLoadThread();// 建立排序去load漫畫
				mDownLoadThread.setSingleComicData(this, mSingleComicData);
				WriteFile.mkDir(savePath);
				mDownLoadThread.setSavePath(savePath);
				// 將下載任務放到pool
				mFuture = ThreadPool.submit(mDownLoadThread);
			} else {
				if (parentObj != null) {
					parentObj.setStateText(mSingleComicData.name + " 下載失敗");
				}
			}
		}
		return mFuture;
	}

	public void close() {
		if (mFuture != null) {
			mFuture.cancel(true);
		}
		if (mLoadBarState != null) {
			mLoadBarState.close();
		}
		if (mDownLoadThread != null) {
			mDownLoadThread.stopJpgLink();
		}
		parentObj = null;
	}

	public DownLoadThread getDownLoadThread() {
		return mDownLoadThread;
	}

	@Override
	public void onloading(int currentPage, int totalPage) {
		mLoadBarState.setProgressBar(currentPage, totalPage, currentPage + "/"
				+ totalPage);

	}

	@Override
	public void onComplete() {
		removeSelf("\"" + checkName + "\" 下載完成");
	}

	@Override
	public void onDownloadFail(SingleComicData singleComic, String message) {
		removeSelf("\"" + checkName + "\" 下載失敗，" + message);
	}

	public void removeSelf(String massage) {
		setFuture(null);
		if (parentObj != null) {
			if (massage != null && massage.length() > 0) {
				parentObj.setStateText(massage);
			}
			parentObj.removeTask(this);
		}
		close();
	}
}
