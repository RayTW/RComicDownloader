package net.xuite.blog.ray00000test.rdownloadcomic.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import JDownLoadComic.DatabaseLite;
import net.xuite.blog.ray00000test.library.comicsdk.Comic;
import net.xuite.blog.ray00000test.library.comicsdk.R8Comic;
import net.xuite.blog.ray00000test.library.comicsdk.R8Comic.OnLoadListener;
import net.xuite.blog.ray00000test.library.net.EasyHttp;
import net.xuite.blog.ray00000test.library.net.EasyHttp.Response;
import net.xuite.blog.ray00000test.rdownloadcomic.util.ThreadPool;
import net.xuite.blog.ray00000test.rdownloadcomic.util.WorkaroundUtility;
import net.xuite.blog.ray00000test.rdownloadcomic.util.WriteFile;

/**
 * 下載漫畫的核心
 * @author Ray Lee 
 * Created on 2017/08/16
 */
public class RComicDownloader {
	private static RComicDownloader instance;
	private ThreadPool mPDFThreadPool;
	private ThreadPool mTaskPool;
	private String dbPath = Config.defaultSavePath + "/sys/comic.obj";
	private DatabaseLite mDB = new DatabaseLite();
	private R8Comic mR8Comic = R8Comic.get();
	private List<Comic> mComics;
	private Map<String, String> mHostList;

	private RComicDownloader() {
		initialize();
	}

	private void initialize() {
		mPDFThreadPool = new ThreadPool(5);
		mTaskPool = new ThreadPool(3);
	}

	public static RComicDownloader get() {
		if (instance == null) {
			synchronized (RComicDownloader.class) {
				if (instance == null) {
					instance = new RComicDownloader();
				}
			}
		}
		return instance;
	}

	public DatabaseLite getDB() {
		return mDB;
	}
	
	public R8Comic getR8Comic(){
		return mR8Comic;
	}
	
	public List<Comic> getComics(){
		return mComics;
	}
	
	public Map<String, String> getHostList(){
		return mHostList;
	}

	public void preprogress() {
		// 檢查我的最愛文字檔
		WriteFile.mkDir(Config.defaultSavePath + "/sys");
		mDB.pathName = dbPath;
		mDB.load();

		// 建立下載執行緒上限個數
		int downloadCount = mDB.downloadCount;
		if (downloadCount == 0) {
			if (mDB.getDownCountLimit() > 0) {
				downloadCount = mDB.getDownCountLimit();
			} else {
				downloadCount = 5;
			}
		}
		ThreadPool.newInstance(downloadCount);

		WorkaroundUtility.replaceAllSpaceCharComic();
		WorkaroundUtility.replaceAllSpaceCharAct();
		
		//戴入全部漫畫
		mR8Comic.getAll(new OnLoadListener<List<Comic>>(){

			@Override
			public void onLoaded(List<Comic> comics) {
				mComics = comics;
			}
			
		});
		
		//戴入漫漫host列表
		mR8Comic.loadSiteUrlList(new OnLoadListener<Map<String, String>>() {

			@Override
			public void onLoaded(final Map<String, String> hostList) {
				mHostList = hostList;
			}
		});
	}

	public void addPDFTask(Runnable run) {
		mPDFThreadPool.executeTask(run);
	}
	
	public void addTask(Runnable run) {
		mTaskPool.executeTask(run);
	}
	

	public void addDownloadTask(DownloadComicTask.Callback callback,
			Comic comic, int index) {
		DownloadComicTask task = new DownloadComicTask(comic, index);
		try {
			task.setCallback(callback);
			task.createThreadTask();
		} catch (Exception e) {
			e.printStackTrace();
			String msg = e.getMessage();
			if (task != null) {
				msg = task.getCheckName() + "下載失敗";
			}
			JOptionPane.showMessageDialog(null, msg, "錯誤訊息",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * 取得網站上全部漫畫列表
	 * @return
	 */
	public ComicList getAllComics(){
		return new ComicList(mComics);
	}
	
	/**
	 * 取得目前我的最愛漫畫列表
	 * @return
	 */
	public ComicList getMyLoveComics(){
		return new ComicList(mComics, getDB().getLoveComicList());
	}
	
	public String getComicDetailUrl(String comicId){
		return mR8Comic.getConfig().getComicDetailUrl(comicId);
	}
	
	public String requestGetHttp(String url, String charset) throws IOException{
		String result = null;
		EasyHttp request = new EasyHttp.Builder()
				.setUrl(url)
				.setMethod("GET")
				.setIsRedirect(true)
				.setCharset(charset)
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
}
