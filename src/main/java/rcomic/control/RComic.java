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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.json.JSONObject;

import raytw.sdk.comic.comicsdk.Comic;
import raytw.sdk.comic.comicsdk.Episode;
import raytw.sdk.comic.comicsdk.R8Comic;
import raytw.sdk.comic.comicsdk.R8Comic.OnLoadListener;
import raytw.sdk.comic.net.EasyHttp;
import raytw.sdk.comic.net.EasyHttp.Response;
import rcomic.utils.DistributedTask;
import rcomic.utils.ThreadPool;

/**
 * 下載漫畫的核心
 *
 * @author Ray Lee Created on 2017/08/16
 */
public class RComic {
  private static RComic sInstance;

  private Config config = new Config();
  private ThreadPool taskPool;
  private ThreadPool fifoPool;
  private R8Comic r8Comic = R8Comic.get();
  private List<ComicWrapper> comics;
  private List<ComicWrapper> newComics;
  private String hostList;
  private JSONObject favorites;
  private Thread searchThread;
  private SearchTask searchTask;
  private ForkJoinPool pool;

  private RComic() {
    initialize();
  }

  private void initialize() {
    comics = new CopyOnWriteArrayList<>();
    newComics = new CopyOnWriteArrayList<>();
    taskPool = new ThreadPool(10);
    fifoPool = new ThreadPool(1);
    pool = new ForkJoinPool();
    favorites = new JSONObject();
    searchThread = new Thread(() -> startSearch());
    searchThread.start();
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
    return r8Comic;
  }

  public List<ComicWrapper> getComics() {
    return comics;
  }

  public String getHostList() {
    return hostList;
  }

  public Config getConfig() {
    return config;
  }

  public void preprogress(Runnable completeListener) {
    favorites = config.loadFavorites();
    CountDownLatch countdonw = new CountDownLatch(3);

    // 戴入全部漫畫
    r8Comic.getAll(
        new OnLoadListener<List<Comic>>() {

          @Override
          public void onLoaded(List<Comic> list) {
            comics = new CopyOnWriteArrayList<>();

            for (Comic comic : list) {
              comics.add(new ComicWrapper(comic));
            }
            countdonw.countDown();
          }
        });

    r8Comic.getNewest(
        new OnLoadListener<List<Comic>>() {

          @Override
          public void onLoaded(List<Comic> comics) {
            newComics = new CopyOnWriteArrayList<ComicWrapper>();

            for (Comic comic : comics) {
              newComics.add(new ComicWrapper(comic));
            }
            countdonw.countDown();
          }
        });

    // 戴入漫漫host列表
    r8Comic.loadSiteUrlList(
        new OnLoadListener<String>() {

          @Override
          public void onLoaded(final String host) {
            hostList = host;
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
    taskPool.executeTask(run);
  }

  public void loadEpisodesImagesPagesUrl(Episode episode, Consumer<Episode> consumer) {
    if (!episode.getUrl().startsWith("http")) {
      episode.setUrl(hostList + episode.getUrl());
    }

    R8Comic.get()
        .loadEpisodeDetail(
            episode,
            result -> {
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
    return comics;
  }

  /**
   * 取得目前最新漫畫列表
   *
   * @return
   */
  public List<ComicWrapper> getNewComics() {
    return newComics;
  }

  public String getComicDetailUrl(String comicId) {
    return r8Comic.getConfig().getComicDetailUrl(comicId);
  }

  public String requestGetHttp(String url, String charset) throws IOException {
    String result = null;
    EasyHttp request =
        new EasyHttp.Builder()
            .setUrl(url)
            .setMethod("GET")
            .setIsRedirect(true)
            .setReadCharset(charset)
            .setWriteCharset(charset)
            .putHeader(
                "Accept",
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
    return config.getLangValue(key);
  }

  private void startSearch() {
    while (true) {
      try {
        synchronized (searchThread) {
          TimeUnit.MILLISECONDS.timedWait(searchThread, 500);
        }
        SearchTask task = searchTask;

        if (task == null || task.isFinish()) {
          continue;
        }

        for (; ; ) {
          task = searchTask;

          if (!task.isFinish()) {
            task.execute();
            break;
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 搜尋漫畫名稱是否有符合關鍵字
   *
   * @param keyword
   * @param consumer
   */
  public void search(String keyword, Consumer<List<ComicWrapper>> consumer) {
    synchronized (searchThread) {
      searchTask =
          new SearchTask(
              () -> {
                ArrayList<ComicWrapper> list =
                    pool.invoke(new DistributedSearchTask(comics, keyword, 0, comics.size(), 1000));

                if (consumer != null) {
                  consumer.accept(list);
                }
              });
    }
    synchronized (searchThread) {
      searchThread.notifyAll();
    }
  }

  public ComicWrapper searchAllById(String id) {
    return comics.stream().filter(o -> o.getId().equals(id)).findFirst().get();
  }

  public ComicWrapper searchNewById(String id) {
    return newComics.stream().filter(o -> o.getId().equals(id)).findFirst().get();
  }

  /**
   * 開啟指定漫畫集數
   *
   * @param cFloder 漫畫第一層資料夾(例如:海賊王)
   * @param comicActFloder 漫畫第二層資料夾(例如:第1話)
   */
  public void openComic(Episode episode) {
    StringBuilder imageUrl = new StringBuilder();

    episode
        .getImageUrlList()
        .forEach(
            url -> {
              if (imageUrl.length() > 0) {
                imageUrl.append("|");
              }
              imageUrl.append(config.episodeImageUrlSchema + url);
            });
    String html = config.getComicHtml(imageUrl.toString());
    try {
      Path tmpdir =
          Paths.get(System.getProperty("java.io.tmpdir") + RComic.get().getConfig().htmlFolder);

      if (!Files.exists(tmpdir)) {
        Files.createDirectory(tmpdir);
      }

      Path tempComicPath = Paths.get(tmpdir.toString(), "tempOpenComic.html");

      if (Files.notExists(tempComicPath)) {
        Files.createFile(tempComicPath);
      }

      Path target =
          Files.write(
              tempComicPath,
              html.getBytes(config.charsetUtf8),
              StandardOpenOption.CREATE,
              StandardOpenOption.TRUNCATE_EXISTING);

      try {
        Desktop.getDesktop().open(target.toFile());
      } catch (IOException e) {
        JOptionPane.showMessageDialog(
            null, getLang("NoBrowser"), getLang("Error"), JOptionPane.WARNING_MESSAGE);
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
    favorites.put(comicId, "");
    fifoPool.execute(
        () -> {
          config.storeFavorites(favorites);
        });
  }

  public void removeFromFavorites(String comicId) {
    favorites.remove(comicId);
    fifoPool.execute(
        () -> {
          config.storeFavorites(favorites);
        });
  }

  public boolean existedFavorites(String comidId) {
    return favorites.has(comidId);
  }

  public JSONObject getFavorites() {
    return favorites;
  }

  // 執行使用關鍵字搜尋漫畫的任務
  private class SearchTask {
    private volatile boolean isFinish;
    private Runnable runnable;

    public SearchTask(Runnable task) {
      runnable = task;
    }

    public void execute() {
      long time = System.currentTimeMillis();
      try {
        runnable.run();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        isFinish = true;
      }
      System.out.println("搜尋漫畫花費-" + (System.currentTimeMillis() - time));
    }

    public boolean isFinish() {
      return isFinish;
    }
  }

  private class DistributedSearchTask extends DistributedTask<ArrayList<ComicWrapper>> {
    private static final long serialVersionUID = 1L;
    private List<ComicWrapper> comicsList;
    private String keyword;

    public DistributedSearchTask(
        List<ComicWrapper> comics, String keyword, int start, int end, int batch) {
      super(start, end, batch);
      this.comicsList = comics;
      this.keyword = keyword;
    }

    @Override
    protected ArrayList<ComicWrapper> compute() {
      ArrayList<ComicWrapper> list = new ArrayList<>();

      if (canCompute()) {
        ComicWrapper comic = null;

        for (int i = getStart(); i < getEnd(); i++) {
          comic = comicsList.get(i);

          if (comic.getName().contains(keyword)) {
            list.add(comic);
          }
        }
        return list;
      }
      int mid = getMiddle();

      ForkJoinTask<ArrayList<ComicWrapper>> task1 =
          new DistributedSearchTask(comicsList, keyword, getStart(), mid, getBatch()).fork();
      DistributedSearchTask task2 =
          new DistributedSearchTask(comicsList, keyword, mid, getEnd(), getBatch());

      ArrayList<ComicWrapper> task2Result = task2.compute();
      ArrayList<ComicWrapper> task1Result = task1.join();

      task1Result.addAll(task2Result);

      return task1Result;
    }
  }
}
