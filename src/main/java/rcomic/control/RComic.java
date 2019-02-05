package rcomic.control;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import net.xuite.blog.ray00000test.library.comicsdk.Comic;
import net.xuite.blog.ray00000test.library.comicsdk.Episode;
import net.xuite.blog.ray00000test.library.comicsdk.R8Comic;
import net.xuite.blog.ray00000test.library.comicsdk.R8Comic.OnLoadListener;
import net.xuite.blog.ray00000test.library.net.EasyHttp;
import net.xuite.blog.ray00000test.library.net.EasyHttp.Response;
import rcomic.utils.ThreadPool;
import rcomic.utils.Utility;
import rcomic.utils.io.WriteFile;

/**
 * 下載漫畫的核心
 * 
 * @author Ray Lee Created on 2017/08/16
 */
public class RComic {
	private static RComic sInstance;

	private Config mConfig = new Config();
	private ThreadPool mPDFThreadPool;
	private ThreadPool mTaskPool;
	private ThreadPool mComidDownloadPool;
	private R8Comic mR8Comic = R8Comic.get();
	private List<ComicWrapper> mComics;
	private List<ComicWrapper> mNewComics;
	private Map<String, String> mHostList;
	private WriteFile mDebugLog = new WriteFile();

	private RComic() {
		initialize();
	}

	private void initialize() {
		mComics = new CopyOnWriteArrayList<>();
		mNewComics = new CopyOnWriteArrayList<>();
		mPDFThreadPool = new ThreadPool(5);
		mTaskPool = new ThreadPool(10);
	}

	public static RComic get() {
		if (sInstance == null) {
			synchronized (RComic.class) {
				if (sInstance == null) {
					sInstance = new RComic();
				}
			}
		}
		return sInstance;
	}

	public R8Comic getR8Comic() {
		return mR8Comic;
	}

	public List<ComicWrapper> getComics() {
		return mComics;
	}

	public Map<String, String> getHostList() {
		return mHostList;
	}

	public Config getConfig() {
		return mConfig;
	}

	public void preprogress(Runnable completeListener) {
		WriteFile.mkDir(mConfig.mDefaultSavePath + "/sys");

		// 建立下載執行緒上限個數
		mComidDownloadPool = new ThreadPool();

		Utility.replaceAllSpaceCharComic("./" + mConfig.mDefaultSavePath);
		Utility.replaceAllSpaceCharAct("./" + mConfig.mDefaultSavePath);

		CountDownLatch countdonw = new CountDownLatch(3);

		// 戴入全部漫畫
		mR8Comic.getAll(new OnLoadListener<List<Comic>>() {

			@Override
			public void onLoaded(List<Comic> comics) {
				mComics = new CopyOnWriteArrayList<>();

				for (Comic comic : comics) {
					mComics.add(new ComicWrapper(comic));
				}
				countdonw.countDown();
			}

		});

		mR8Comic.getNewest(new OnLoadListener<List<Comic>>() {

			@Override
			public void onLoaded(List<Comic> comics) {
				mNewComics = new CopyOnWriteArrayList<ComicWrapper>();

				for (Comic comic : comics) {
					mNewComics.add(new ComicWrapper(comic));
				}
				countdonw.countDown();
			}

		});

		// 戴入漫漫host列表
		mR8Comic.loadSiteUrlList(new OnLoadListener<Map<String, String>>() {

			@Override
			public void onLoaded(final Map<String, String> hostList) {
				mHostList = hostList;
				countdonw.countDown();
			}
		});

		try {
			countdonw.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (completeListener != null) {
			completeListener.run();
		}
	}

	public void addPDFTask(Runnable run) {
		mPDFThreadPool.executeTask(run);
	}

	public void addTask(Runnable run) {
		mTaskPool.executeTask(run);
	}

	public void addDownloadTask(DownloadComicTask.Listener callback, ComicWrapper comic, int index) {
		Episode episode = comic.getEpisodes().get(index);

		// 重組單集漫畫的網址
		if (episode.getUrl().indexOf("http") == -1) {
			String host = getHostList().get(episode.getCatid());

			episode.setUrl(host + episode.getUrl());
		}
		String savePath = "./" + mConfig.mDefaultSavePath + "/" + comic.getName() + "/"
				+ comic.getEpisodes().get(index).getName();
		DownloadComicTask task = new DownloadComicTask(comic, episode, index);

		try {
			task.setCallback(callback);
			task.createThreadTask((thread) -> {

				getR8Comic().loadEpisodeDetail(episode, new OnLoadListener<Episode>() {

					@Override
					public void onLoaded(Episode result) {

						result.setUpPages();

						thread.setEpisode(task, episode.getUrl(), episode);
						WriteFile.mkDir(savePath);
						thread.setSavePath(savePath);
					}
				});
				// 將下載任務放到pool
				return mComidDownloadPool.submit(thread);
			});
		} catch (Exception e) {
			e.printStackTrace();
			String msg = e.getMessage();
			if (task != null) {
				msg = task.getCheckName() + "下載失敗";
			}
			JOptionPane.showMessageDialog(null, msg, "錯誤訊息", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * 取得網站上全部漫畫列表
	 * 
	 * @return
	 */
	public ComicList getAllComics() {
		return new ComicList(mComics);
	}

	/**
	 * 取得目前最新漫畫列表
	 * 
	 * @return
	 */
	public ComicList getNewComics() {
		return new ComicList(mNewComics);
	}

	/**
	 * 取得目前我的最愛漫畫列表
	 * 
	 * @return
	 */
	public ComicList getMyLoveComics() {
		// TODO 目前沒db，最愛是空的
		return new ComicList(mComics, new ArrayList<String>());
	}

	public String getComicDetailUrl(String comicId) {
		return mR8Comic.getConfig().getComicDetailUrl(comicId);
	}

	public String requestGetHttp(String url, String charset) throws IOException {
		String result = null;
		EasyHttp request = new EasyHttp.Builder().setUrl(url).setMethod("GET").setIsRedirect(true)
				.setReadCharset(charset).setWriteCharset(charset)
				.putHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.putHeader("Accept-Encoding", "gzip, deflate, br")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
				.build();

		Response response = request.connect();
		result = response.getBody();

		return result;
	}

	public void enableLog() {
		WriteFile.mkDir(mConfig.mLogPath);

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				mDebugLog.writeTextUTF8Apend("Thread[" + t + "] " + exceptionAsString,
						new File(mConfig.mLogPath, "debug.log").getPath());
			}

		});
	}

	/**
	 * 取得語系key對應的字串
	 * 
	 * @param key
	 * @return
	 */
	public String getLang(String key) {
		return mConfig.getLangValue(key);
	}

	/**
	 * 搜尋漫畫名稱是否有符合關鍵字
	 * 
	 * @param keyword
	 * @param consumer
	 */
	public void search(String keyword, Consumer<List<ComicWrapper>> consumer) {
		mTaskPool.execute(() -> {
			ArrayList<ComicWrapper> list = new ArrayList<>();

			mComics.stream().filter(o -> o.getName().contains(keyword)).forEach(list::add);

			if (consumer != null) {
				consumer.accept(list);
			}
		});
	}

	public ComicWrapper searchAllById(String id) {
		return mComics.stream().filter(o -> o.getId().equals(id)).findFirst().get();
	}

	public ComicWrapper searchNewById(String id) {
		return mNewComics.stream().filter(o -> o.getId().equals(id)).findFirst().get();
	}
}
