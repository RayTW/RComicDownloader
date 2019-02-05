package rcomic.ui;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import rcomic.control.DownloadComicTask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 漫畫下載下載狀態列表
 * 
 * @author Ray
 * 
 */
public class TableList<T> extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4933735654557260641L;
	private TableListLister mTableListLister;
	/** 目前在進度列長執行下載的執行緒 */
	private List<T> mDownloadTask;
	/** 限制最大下載數 */
	private int mLimitSize;

	/**
	 * 建立狀態列表
	 * 
	 * @param n
	 *            限制最大下載數
	 */
	public TableList(int n) {
		// +10是為了排版好看
		setLayout(new GridLayout(n + 10, 1));
		mLimitSize = n;
		initTableListDemo();
	}

	public void initTableListDemo() {
		mDownloadTask = new CopyOnWriteArrayList<T>();
	}

	/**
	 * 新增loadbar到畫面裡
	 * 
	 * @param obj
	 *            下載狀態列物件
	 */
	public void addObj(DownloadComicTask task) {
	}

	/**
	 * 取得目前下載中的漫畫本數
	 * 
	 * @return
	 */
	public int getCurrentDownLoadSize() {
		return mDownloadTask.size();
	}

	/**
	 * 是否下找串的漫畫本數已滿
	 * 
	 * @return
	 */
	public boolean isFill() {
		return mDownloadTask.size() == mLimitSize;
	}

	public void setTableListLister(TableListLister listener) {
		mTableListLister = listener;
	}

	/**
	 * 移除loadbar
	 * 
	 * @param obj
	 */
	public boolean removeObj(final DownloadComicTask task) {
		return false;
	}

	/**
	 * 檢查要再放入下載的是否已經在下載中
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isDownloading(String name) {

		return false;
	}

	/**
	 * 取得有滾軸的下載狀態列
	 * 
	 * @return
	 */
	public JScrollPane getJScrollPaneJTable() {
		return new JScrollPane(this);
	}

	/**
	 * 刷新下載狀態列畫面
	 * 
	 */
	public void repaintIndexUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (mTableListLister != null) {
					mTableListLister.onRepaint();
				}
			}
		});
	}

	public void setStateText(String text) {
		if (mTableListLister != null) {
			mTableListLister.onStateTestChanged(text);
		}
	}

	public static interface TableListLister {
		void onRepaint();

		void onStateTestChanged(String text);
	}
}
