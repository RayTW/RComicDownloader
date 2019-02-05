package rcomic.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 
 * @author ray
 *
 */
public class ThreadPool {
	public static final String TAG = ThreadPool.class.getSimpleName();
	public static final int DEFAULT_COUNT = 10;
	private ExecutorService mPool;

	public ThreadPool(int count) {
		if (count <= 0) {
			throw new IllegalArgumentException("count <= 0, count=" + count);
		}

		mPool = Executors.newFixedThreadPool(count);
	}

	public ThreadPool() {
		mPool = Executors.newCachedThreadPool();
	}

	public void execute(Runnable run) {
		mPool.execute(run);
	}

	public Future<?> submit(Runnable run) {
		return mPool.submit(run);
	}

	public Future<?> submit(Callable<?> call) {
		return mPool.submit(call);
	}

	public void executeTask(Runnable run) {
		mPool.execute(run);
	}

}
