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
public class LoadBar extends JPanel {
	/** 顯示內建的進度bar */
	private JProgressBar progressBar;
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
	 * 取得漫畫名稱
	 * 
	 * @return
	 */
	public String getLoadName() {
		return name;
	}

	/**
	 * 設定目前顯示的值
	 * 
	 * @param value
	 *            例如:88
	 * @param end
	 * @param text
	 *            顯示的進度字串
	 */
	public void setProgressBar(int value, int end, String text) {
		progressBar.setValue(value);
		progressBar.setMaximum(end);
		percent.setText(text);
	}

	public void setBarText(String text) {
		percent.setText(text);
	}
}
