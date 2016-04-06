package net.xuite.blog.ray00000test.rdownloadcomic.service;

import javax.swing.JOptionPane;

import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.ActDataObj;
import net.xuite.blog.ray00000test.rdownloadcomic.util.ThreadPool;
import net.xuite.blog.ray00000test.rdownloadcomic.util.WorkaroundUtility;
import net.xuite.blog.ray00000test.rdownloadcomic.util.WriteFile;

public class RComicDownloader {
	private static RComicDownloader instance;
	private ThreadPool mPDFThreadPool;
	private String dbPath = Config.defaultSavePath + "/sys/comic.obj";
	private DatabaseLite mDB = new DatabaseLite();

	private RComicDownloader() {
		initialize();
	}

	private void initialize() {
		mPDFThreadPool = new ThreadPool(5);
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
	}

	public void addPDFTask(Runnable run) {
		mPDFThreadPool.executeTask(run);
	}

	public void addDownloadTask(DownloadComicTask.Callback callback,
			ActDataObj actObj, int index) {
		DownloadComicTask task = new DownloadComicTask(actObj, index);
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
}
