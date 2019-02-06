package rcomic.ui;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 漫畫列表
 * 
 * @author Ray
 * 
 */
public class ComicList<T> extends JPanel {
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
	public ComicList(int n) {
		// +10是為了排版好看
		setLayout(new GridLayout(n + 10, 1));
		mLimitSize = n;
		initTableListDemo();
	}

	public void initTableListDemo() {
		mDownloadTask = new CopyOnWriteArrayList<T>();
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
