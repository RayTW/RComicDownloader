package JDownLoadComic;

import java.awt.GridLayout;
//import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import java.util.Vector;

/**
 * 漫畫下載下載狀態列表
 * 
 * @author Ray
 * 
 */
public class TableList extends JPanel {
	/** 父層(首頁漫畫列表) */
	private JDownLoadUI_index parentObj;
	/** 目前在進度列長執行下載的執行緒 */
	private Vector dataList;
	/** 限制最大下載數 */
	private int limitSize;

	/**
	 * 建立狀態列表
	 * 
	 * @param n
	 *            限制最大下載數
	 */
	public TableList(int n) {
		// +10是為了排版好看
		setLayout(new GridLayout(n + 10, 1));
		limitSize = n;
		initTableListDemo();
	}

	public void initTableListDemo() {
		dataList = new Vector();
	}

	/**
	 * 新增loadbar到畫面裡
	 * 
	 * @param obj
	 *            下載狀態列物件
	 */
	public void addObj(LoadBarState obj) {
		if (dataList.size() < limitSize) {
			dataList.add(obj);
			add(obj);
		}
	}

	/**
	 * 再一次全廓執行開始下載，已開始下載的不會再重新啟動
	 */
	public void startDownloadAll() {
		for (int i = dataList.size() - 1; i >= 0; i--) {
			LoadBarState loadbar = (LoadBarState) dataList.get(i);
			loadbar.startDownload();
		}
	}

	/**
	 * 取得目前下載中的漫畫本數
	 * 
	 * @return
	 */
	public int getCurrentDownLoadSize() {
		return dataList.size();
	}

	/**
	 * 是否下找串的漫畫本數已滿
	 * 
	 * @return
	 */
	public boolean isFill() {
		return dataList.size() == limitSize;
	}

	/**
	 * 設定上層物件參考
	 * 
	 * @param p
	 */
	public void setParentObj(JDownLoadUI_index p) {
		parentObj = p;
	}

	/**
	 * 移除loadbar
	 * 
	 * @param obj
	 */
	public void removeObj(String name) {
		for (int i = dataList.size() - 1; i >= 0; i--) {
			final LoadBarState loadbar = (LoadBarState) dataList.get(i);

			if (name.equals(loadbar.getLoadName())) {
				dataList.remove(loadbar);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						remove(loadbar);
					}
				});
				loadbar.setVisible(false);
				break;
			}
		}
		repaintIndexUI();
	}

	/**
	 * 檢查要再放入下載的是否已經在下載中
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isDownloading(String name) {
		Vector tmpDataList = (Vector) dataList.clone();
		for (int i = 0; i < tmpDataList.size(); i++) {
			if (((LoadBarState) tmpDataList.get(i)).getLoadName().equals(name)) {
				return true;
			}
		}
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
				parentObj.repaint();
			}
		});
	}
}
