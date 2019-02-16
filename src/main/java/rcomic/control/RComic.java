package rcomic.control;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.json.JSONObject;

import net.xuite.blog.ray00000test.library.comicsdk.Comic;
import net.xuite.blog.ray00000test.library.comicsdk.Episode;
import net.xuite.blog.ray00000test.library.comicsdk.R8Comic;
import net.xuite.blog.ray00000test.library.comicsdk.R8Comic.OnLoadListener;
import net.xuite.blog.ray00000test.library.net.EasyHttp;
import net.xuite.blog.ray00000test.library.net.EasyHttp.Response;
import rcomic.utils.ThreadPool;

/**
 * 下載漫畫的核心
 * 
 * @author Ray Lee Created on 2017/08/16
 */
public class RComic {
	private static RComic sInstance;

	private Config mConfig = new Config();
	private ThreadPool mTaskPool;
	private ThreadPool mFIFOPool;
	private R8Comic mR8Comic = R8Comic.get();
	private List<ComicWrapper> mComics;
	private List<ComicWrapper> mNewComics;
	private Map<String, String> mHostList;
	private JSONObject mFavorites;

	private RComic() {
		initialize();
	}

	private void initialize() {
		mComics = new CopyOnWriteArrayList<>();
		mNewComics = new CopyOnWriteArrayList<>();
		mTaskPool = new ThreadPool(10);
		mFIFOPool = new ThreadPool(1);
		mFavorites = new JSONObject();
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
		mFavorites = mConfig.loadFavorites();
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

	public void addTask(Runnable run) {
		mTaskPool.executeTask(run);
	}

	public void loadEpisodesImagesPagesUrl(Episode episode, Consumer<Episode> consumer) {
		String downloadHost = mHostList.get(episode.getCatid());

		if (!episode.getUrl().startsWith("http")) {
			episode.setUrl(downloadHost + episode.getUrl());
		}

		R8Comic.get().loadEpisodeDetail(episode, result -> {
			result.setUpPages();

			if (consumer != null) {
				consumer.accept(result);
			}
		});
	}

	/**
	 * 取得網站上全部漫畫列表
	 * 
	 * @return
	 */
	public List<ComicWrapper> getAllComics() {
		return mComics;
	}

	/**
	 * 取得目前最新漫畫列表
	 * 
	 * @return
	 */
	public List<ComicWrapper> getNewComics() {
		return mNewComics;
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

	/**
	 * 開啟指定漫畫集數
	 * 
	 * @param cFloder
	 *            漫畫第一層資料夾(例如:海賊王)
	 * @param comicActFloder
	 *            漫畫第二層資料夾(例如:第1話)
	 */
	public void openComic(Episode episode) {
		StringBuilder imageUrl = new StringBuilder();

		episode.getImageUrlList().forEach(url -> {
			if (imageUrl.length() > 0) {
				imageUrl.append("|");
			}
			imageUrl.append(mConfig.mEpisodeImageUrlSchema + url);
		});
		String html = mConfig.getComicHtml(imageUrl.toString());
		try {
			Path tmpdir = Paths.get(System.getProperty("java.io.tmpdir") + RComic.get().getConfig().mHtmlFolder);

			if (!Files.exists(tmpdir)) {
				Files.createDirectory(tmpdir);
			}

			Path tempComicPath = Paths.get(tmpdir.toString(), "tempOpenComic.html");

			if (Files.notExists(tempComicPath)) {
				Files.createFile(tempComicPath);
			}

			Path target = Files.write(tempComicPath, html.getBytes(mConfig.mCharsetUtf8), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);

			try {
				Desktop.getDesktop().open(target.toFile());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, getLang("NoBrowser"), getLang("Error"),
						JOptionPane.WARNING_MESSAGE);
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public JDialog newLoadingDialog() {
		JDialog jDialog = new JDialog();
		jDialog.setLayout(new GridBagLayout());
		jDialog.add(new JLabel(RComic.get().getConfig().getLangValue("Loading")));
		jDialog.setMaximumSize(new Dimension(150, 50));
		jDialog.setResizable(false);
		jDialog.setModal(false);
		jDialog.setUndecorated(true);
		jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		jDialog.setLocationRelativeTo(null);
		jDialog.setVisible(true);
		jDialog.pack();

		return jDialog;
	}

	public void addToFavorites(String comicId) {
		mFavorites.put(comicId, "");
		mFIFOPool.execute(() -> {
			mConfig.storeFavorites(mFavorites);
		});
	}

	public void removeFromFavorites(String comicId) {
		mFavorites.remove(comicId);
		mFIFOPool.execute(() -> {
			mConfig.storeFavorites(mFavorites);
		});
	}

	public boolean existedFavorites(String comidId) {
		return mFavorites.has(comidId);
	}
}
