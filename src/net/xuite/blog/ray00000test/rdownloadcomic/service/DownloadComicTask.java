package net.xuite.blog.ray00000test.rdownloadcomic.service;

import java.util.concurrent.Future;

import net.xuite.blog.ray00000test.library.comicsdk.Comic;
import net.xuite.blog.ray00000test.library.comicsdk.Episode;
import net.xuite.blog.ray00000test.library.comicsdk.R8Comic.OnLoadListener;
import net.xuite.blog.ray00000test.rdownloadcomic.ui.LoadBarState;
import net.xuite.blog.ray00000test.rdownloadcomic.util.ThreadPool;
import net.xuite.blog.ray00000test.rdownloadcomic.util.WriteFile;

public class DownloadComicTask implements DownLoadThread.Callback {
	private String checkName;// 此任務的名稱
	private LoadBarState mLoadBarState;
	/** 下載單集漫畫執行緒 */
	private DownLoadThread mDownLoadThread;
	private Future<?> mFuture;
	private Comic mComic;
	private Episode mEpisode;
	private String savePath;
	private Callback mCallback;

	public static interface Callback {
		public void onPrepare(DownloadComicTask task, Episode data);

		public void onSuccess(DownloadComicTask task, Episode data);

		public void onFail(DownloadComicTask task, Episode data,
				String reason);

		public void onCancel(DownloadComicTask task, Episode data);
	}

	public DownloadComicTask(Comic comic, int index) {
		mComic = comic;
		Episode episode = comic.getEpisodes().get(index);
		
		//重組單集漫畫的網址
		if(episode.getUrl().indexOf("http") == -1){
			String host = RComicDownloader.get().getHostList().get(episode.getCatid());
			episode.setUrl(host + episode.getUrl());
		}
		mEpisode = episode;
		
		savePath = "./" + Config.defaultSavePath + "/"
				+ comic.getName() + "/" + comic.getEpisodes().get(index).getName();
		checkName = comic.getId() + comic.getName() + "-"
				+ mEpisode.getName();
		LoadBarState loadb = new LoadBarState();
		loadb.setParentObj(this);
		loadb.setIdName(checkName);
		loadb.setLoadName(comic.getName() + "-"
				+ mEpisode.getName());
		loadb.setBarText("準備下載");
		mLoadBarState = loadb;
	}

	public void setCallback(Callback callback) {
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
		if (mCallback != null) {
			mCallback.onPrepare(this, mEpisode);
		}
		try {
			if (mFuture == null) {
				RComicDownloader.get().getR8Comic().loadEpisodeDetail(mEpisode, new OnLoadListener<Episode>() {

					@Override
					public void onLoaded(
							Episode result) {
						
						
						result.setUpPages();

						mDownLoadThread = new DownLoadThread();// 建立排序去load漫畫
						mDownLoadThread.setEpisode(DownloadComicTask.this, 
								mEpisode.getUrl(),
								mEpisode);
						WriteFile.mkDir(savePath);
						mDownLoadThread.setSavePath(savePath);
						// 將下載任務放到pool
						mFuture = ThreadPool.submit(mDownLoadThread);
					}

				});


			}
		} catch (Exception e) {
			if (mCallback != null) {
				mCallback.onFail(this, mEpisode, "" + e.getMessage());
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
		if (mCallback != null) {
			mCallback.onSuccess(this, mEpisode);
		}
		close();
	}

	@Override
	public void onDownloadFail(Episode singleComic, String reason) {
		if (mCallback != null) {
			mCallback.onFail(this, mEpisode, reason);
		}
		close();
	}

	public void cancel() {
		if (mCallback != null) {
			mCallback.onCancel(this, mEpisode);
		}
		close();
	}
}
