package rcomic.control;

import java.util.concurrent.Future;
import java.util.function.Function;
import net.xuite.blog.ray00000test.library.comicsdk.Episode;
import rcomic.ui.LoadBarState;

/**
 * 
 * @author ray
 *
 */
public class DownloadComicTask implements DownLoadThread.Listener, LoadBarState.Listener {
	private String mName;// 此任務的名稱
	private LoadBarState mLoadBarState;
	/** 下載單集漫畫執行緒 */
	private DownLoadThread mDownLoadThread;
	private Future<?> mFuture;
	private ComicWrapper mComic;
	private Episode mEpisode;
	private Listener mListener;

	public DownloadComicTask(ComicWrapper comic, Episode episode, int index) {
		mComic = comic;
		mEpisode = episode;

		mName = comic.getId() + comic.getName() + "-" + mEpisode.getName();
		LoadBarState loadb = new LoadBarState();
		loadb.setListener(this);
		loadb.setIdName(mName);
		loadb.setLoadName(comic.getName() + "-" + mEpisode.getName());
		loadb.setBarText("準備下載");
		mLoadBarState = loadb;
	}

	public void setCallback(Listener callback) {
		mListener = callback;
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
		if (mName != null) {
			return mName.equals(name);
		}
		return false;
	}

	public String getCheckName() {
		return mName;
	}

	/**
	 * 開始下載漫畫,執行此METHOD之前需先呼叫setDownloadPath()
	 * 
	 * @param taskGenerator
	 * @throws Exception
	 */
	public Future<?> createThreadTask(Function<DownLoadThread, Future<?>> taskGenerator) throws Exception {
		if (mListener != null) {
			mListener.onPrepare(this, mEpisode);
		}
		try {
			if (mFuture == null) {
				mDownLoadThread = new DownLoadThread();
				mFuture = taskGenerator.apply(mDownLoadThread);
			}
		} catch (Exception e) {
			if (mListener != null) {
				mListener.onFail(this, mEpisode, "" + e.getMessage());
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
		mListener = null;
		mFuture = null;
	}

	@Override
	public DownLoadThread getDownLoadThread() {
		return mDownLoadThread;
	}

	@Override
	public void onloading(int currentPage, int totalPage) {
		mLoadBarState.setProgressBar(currentPage, totalPage, currentPage + "/" + totalPage);

	}

	@Override
	public void onComplete() {
		if (mListener != null) {
			mListener.onSuccess(this, mEpisode);
		}
		close();
	}

	@Override
	public void onDownloadFail(Episode singleComic, String reason) {
		if (mListener != null) {
			mListener.onFail(this, mEpisode, reason);
		}
		close();
	}

	@Override
	public void cancel() {
		if (mListener != null) {
			mListener.onCancel(this, mEpisode);
		}
		close();
	}

	public static interface Listener {
		public void onPrepare(DownloadComicTask task, Episode data);

		public void onSuccess(DownloadComicTask task, Episode data);

		public void onFail(DownloadComicTask task, Episode data, String reason);

		public void onCancel(DownloadComicTask task, Episode data);
	}
}
