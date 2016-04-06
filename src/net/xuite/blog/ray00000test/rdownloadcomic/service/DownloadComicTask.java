package net.xuite.blog.ray00000test.rdownloadcomic.service;

import java.util.concurrent.Future;

import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.ActDataObj;
import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.SingleComicData;
import net.xuite.blog.ray00000test.rdownloadcomic.service.DownLoadThread.Callback;
import net.xuite.blog.ray00000test.rdownloadcomic.ui.EventHandle_Act;
import net.xuite.blog.ray00000test.rdownloadcomic.ui.LoadBarState;
import net.xuite.blog.ray00000test.rdownloadcomic.util.ThreadPool;
import net.xuite.blog.ray00000test.rdownloadcomic.util.WriteFile;

public class DownloadComicTask implements DownLoadThread.Callback {
	private String checkName;// 此任務的名稱
	private LoadBarState mLoadBarState;
	/** 下載單集漫畫執行緒 */
	private DownLoadThread mDownLoadThread;
	private Future<?> mFuture;
	private SingleComicData mSingleComicData;
	private String savePath;
	private Callback mCallback;
	
	public static interface Callback{
		public void onPrepare(DownloadComicTask task, SingleComicData data);
		public void onSuccess(DownloadComicTask task, SingleComicData data);
		public void onFail(DownloadComicTask task, SingleComicData data, String reason);
		public void onCancel(DownloadComicTask task, SingleComicData data);
	}

	public DownloadComicTask(ActDataObj actObj, int index) {
		mSingleComicData = actObj.getActComicData(index);
		savePath = "./" + Config.defaultSavePath + "/"
				+ actObj.getCartoonName() + "/" + actObj.getAct(index);
		checkName = actObj.id + actObj.getCartoonName() + "-"
				+ mSingleComicData.getName();
		LoadBarState loadb = new LoadBarState();
		loadb.setParentObj(this);
		loadb.setIdName(checkName);
		loadb.setLoadName(actObj.getCartoonName() + "-"
				+ mSingleComicData.getName());
		loadb.setBarText("準備下載");
		mLoadBarState = loadb;
	}
	
	public void setCallback(Callback callback){
		mCallback = callback;
	}

	public LoadBarState getmLoadBarState() {
		return mLoadBarState;
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
	 * @throws Exception 
	 */
	public Future<?> createThreadTask() throws Exception {
		if(mCallback != null){
			mCallback.onPrepare(this, mSingleComicData);
		}
		try{
			if (mFuture == null) {
				if (mSingleComicData.setPageList()) {
					mDownLoadThread = new DownLoadThread();// 建立排序去load漫畫
					mDownLoadThread.setSingleComicData(this, mSingleComicData);
					WriteFile.mkDir(savePath);
					mDownLoadThread.setSavePath(savePath);
					// 將下載任務放到pool
					mFuture = ThreadPool.submit(mDownLoadThread);
				} else {
					if (mCallback != null) {
						mCallback.onFail(this, mSingleComicData, "");
					}
					close();
				}
			}
		}catch(Exception e){
			if(mCallback != null){
				mCallback.onFail(this, mSingleComicData, "" + e.getMessage());
			}
			e.printStackTrace();
			throw e;
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
		mCallback = null;
		mFuture = null;
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
		if(mCallback != null){
			mCallback.onSuccess(this, mSingleComicData);
		}
		close();
	}

	@Override
	public void onDownloadFail(SingleComicData singleComic, String reason) {
		if(mCallback != null){
			mCallback.onFail(this, mSingleComicData, reason);
		}
		close();
	}
	
	public void cancel(){
		if(mCallback != null){
			mCallback.onCancel(this, mSingleComicData);
		}
		close();
	}
}
