package net.xuite.blog.ray00000test.rdownloadcomic.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {
	public static final String TAG = ThreadPool.class.getSimpleName();
	public static int DEFAULT_COUNT = 10;
	private static ThreadPool instance;
	private ExecutorService mPool;

	public ThreadPool(int count) {
		if (count > 0)
			this.mPool = Executors.newFixedThreadPool(count);
		else
			this.mPool = Executors.newCachedThreadPool();
	}

	public static ThreadPool newInstance(int count) {
		if (instance != null) {
			instance.mPool.shutdown();
		}
		instance = new ThreadPool(count);
		return instance;
	}

	public static ThreadPool getInstance() {
		if (instance == null) {
			newInstance(DEFAULT_COUNT);
		}
		return instance;
	}

	public static void execute(Runnable run) {
		getInstance().mPool.execute(run);
	}

	public static Future<?> submit(Runnable run) {
		return getInstance().mPool.submit(run);
	}

	public static Future<?> submit(Callable<?> call) {
		return getInstance().mPool.submit(call);
	}

	public void executeTask(Runnable run) {
		mPool.execute(run);
	}

}
