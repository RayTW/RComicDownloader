package JDownLoadComic.util;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 顯示目前進度的bar
 * 
 * @author Ray
 * 
 */
public class LoadBar extends JPanel implements Runnable {
	/** 執行狀態 */
	private boolean isRun;
	/** 顯示內建的進度bar */
	private JProgressBar progressBar;
	/** 記錄目前進度資料物件 */
	private LoadData loadObj;
	/** 顯示目前讀取頁數與總頁數 */
	private JLabel percent;
	/** 顯示目前下載的漫畫名稱 */
	private JLabel loadName;
	/** 目前下載的漫畫名稱 */
	private String name;

	public LoadBar() {
		initLoadBar();
	}

	/**
	 * 初始化
	 * 
	 */
	public void initLoadBar() {
		// setLayout(new GridLayout(0,3,10,0));
		JPanel tmpJpanel = new JPanel();
		loadName = new JLabel();
		tmpJpanel.add(loadName);
		progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		tmpJpanel.add(progressBar);
		progressBar.setOpaque(false);
		percent = new JLabel();
		tmpJpanel.add(percent);
		add(tmpJpanel);
	}

	/**
	 * @param 設定目前進度物件
	 */
	public void setLoadObj(LoadData lobj) {
		loadObj = lobj;
	}

	public void setIdName(String idName) {
		name = idName;
	}

	public String getIdname() {
		return name;
	}

	public void setLoadName(String name) {
		loadName.setText(name);
	}

	/**
	 * 啟動顯示進度狀態列
	 * 
	 * @param nm
	 * @param id
	 */
	public void startLoad() {
		isRun = true;
		Thread load = new Thread(this);
		load.start();
	}

	/**
	 * 停止顯示進度狀態列
	 * 
	 */
	public void stopLoad() {
		isRun = false;
	}

	@Override
	public void run() {
		int value = loadObj.currentProeess;
		int end = loadObj.endValue;
		while (isRun && (value < end)) {
			value = loadObj.currentProeess;
			int percentValue = (value * 100) / end;
			progressBar.setValue(percentValue);
			percent.setText(value + "/" + end);
			Thread.yield();
		}
		progressBar.setValue(100);
		clear();
	}

	/**
	 * 取得漫畫名稱
	 * 
	 * @return
	 */
	public String getLoadName() {
		return name;
	}

	/**
	 * 清除
	 * 
	 */
	public void clear() {
		loadObj = null;
	}
}
