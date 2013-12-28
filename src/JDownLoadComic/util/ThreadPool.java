package JDownLoadComic.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 功能說明: 可建立一個Thread pool，進行排程執行任務，例如: Runnable runObject = new Runnable(){
 * public void run(){ //Do some thing } };
 * 
 * ThreadPool.execute(runObject);
 * 
 * runObject裡面的run將會等到runObject被放到ThreadPool.execute()才會執行。
 * 
 * 建立一個singleton的ThreadPool，若count為1~2147483647則產生固定個數執行緒，
 * 若count為0則產生不定個數執行緒，但執行緒擁有Cache功能，預設被建立的執行緒60秒內沒再被使用，將會回收
 * 
 * 
 * note: 此class必須使用java5.0以上的版本才可以使用!
 * 
 * @author Ray
 * @version 2013.12.25
 * 
 */
public class ThreadPool {
	public final static String TAG = ThreadPool.class.getSimpleName();
	public static int DEFAULT_COUNT = 10;
	private static ThreadPool instance;
	private ExecutorService mPool;

	public ThreadPool(int count) {
		if (count > 0) {
			mPool = Executors.newFixedThreadPool(count);
		} else {
			mPool = Executors.newCachedThreadPool();
		}
	}

	/**
	 * 重新產生新的thread pool， 若count為1~2147483647則產生固定個數執行緒，
	 * 若count為0則產生不定個數執行緒，但執行緒擁有Cache功能，預設被建立的執行緒60秒內沒再被使用，將會回收
	 * 
	 * @param count
	 * @return
	 */
	public static ThreadPool newInstance(int count) {
		if (instance != null) {
			// 將目前的任務都停止
			instance.mPool.shutdown();
		}
		instance = new ThreadPool(count);
		return instance;
	}

	/**
	 * 取得目前使用中的Thread pool
	 * 
	 * @return
	 */
	public static ThreadPool getInstance() {
		if (instance == null) {
			newInstance(DEFAULT_COUNT);
		}
		return instance;
	}

	/**
	 * 將任務加到目前使用中的Thread pool
	 * 
	 * @param run
	 */
	public static void execute(Runnable run) {
		getInstance().mPool.execute(run);
	}

	/**
	 * 將任務加到目前使用中的Thread pool
	 * 
	 * @param run
	 * @return Future<?>
	 */
	public static Future<?> submit(Runnable run) {
		return getInstance().mPool.submit(run);
	}

	/**
	 * 將任務加到目前使用中的Thread pool
	 * 
	 * @param Future
	 *            <?>
	 */
	public static Future<?> submit(Callable<?> call) {
		return getInstance().mPool.submit(call);
	}

	public static void main(String[] args) {
		// 範例:
		// 假設有100個任務要同時進行，會產生100個執行緒，並同時執行任務
		for (int i = 0; i < 100; i++) {
			final int number = i;
			new Thread() {
				@Override
				public void run() {
					int sleep = (int) (Math.random() * 10);
					try {
						Thread.sleep(sleep * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("thread task : " + number + ",sleep:"
							+ sleep);
				}
			}.start();
		}

		// 如果使用上面方式，那將會同時有100個執行緒在搶資源，不合手效能，因此可以改用下面方式
		ThreadPool.newInstance(5);// 建立5個執行緒執行所有任務

		// 產生100個任務
		for (int i = 0; i < 100; i++) {
			final int number = i;
			Runnable runObject = new Runnable() {
				@Override
				public void run() {
					int sleep = (int) (Math.random() * 5);
					try {
						Thread.sleep(sleep * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("thread pool : task : " + number
							+ ",sleep:" + sleep);
				}
			};

			// 將任務放入Thread pool等待執行
			ThreadPool.execute(runObject);
		}
	}
}
