package JDownLoadComic.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadThreadPool {
	private static DownloadThreadPool instance;
	private ExecutorService mPool;

	public DownloadThreadPool(int count) {
		System.out.println("pool count : " + count);
		mPool = Executors.newFixedThreadPool(count);
	}

	public static DownloadThreadPool newInstance(int count) {
		if (instance != null) {
			// 將目前的任務都執行完畢
			instance.mPool.shutdown();
		}
		instance = new DownloadThreadPool(count);
		return instance;
	}

	public static DownloadThreadPool getInstance() {
		return instance;
	}

	public void execute(Runnable run) {
		mPool.execute(run);
	}
}
