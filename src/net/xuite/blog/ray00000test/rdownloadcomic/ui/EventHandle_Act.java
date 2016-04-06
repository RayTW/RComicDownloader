package net.xuite.blog.ray00000test.rdownloadcomic.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.ActDataObj;
import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.SingleComicData;
import net.xuite.blog.ray00000test.rdownloadcomic.service.Config;
import net.xuite.blog.ray00000test.rdownloadcomic.service.DownloadComicTask;

/**
 * 單本漫畫所有集數列表的事件處理
 * 
 * @author Ray
 * 
 */

public class EventHandle_Act implements ActionListener {
	/** 父層參考 */
	private JDownLoadUI_Act parentObj;
	/** 整套漫畫連結資料、總集數等等.. */
	private ActDataObj actDataObj;
	/** 漫畫集數列表 */
	private TableList downLoadTable;

	public EventHandle_Act() {
		initEventHandle_Act();
	}

	/**
	 * 初始化
	 * 
	 */
	public void initEventHandle_Act() {
	}

	/**
	 * 單種漫畫集數列表上的按鈕被點擊時產生的事件處理,以事件名稱分別為:<BR>
	 * 自定事件名稱/功能說明<BR>
	 * "ListAllJPG" 將所選取的漫畫集數加到下載列並開始下載<BR>
	 * "detial" 漫畫簡介<BR>
	 * "addLove" 把漫畫編號加到我的最愛<BR>
	 */
	@Override
	public void actionPerformed(ActionEvent act) {
		Object obj = act.getSource();

		if (obj instanceof JButton) {
			JButton b = (JButton) obj;
			if (b.getName().equals("ListAllJPG")) {
				listAllJPG(actDataObj);
			} else if (b.getName().equals("detial")) {
				detail(actDataObj);
			} else if (b.getName().equals("addLove")) {
				addLove(actDataObj);
			}
		}
	}

	private void listAllJPG(final ActDataObj actObj) {
		final int[] selectList = getSelectRowIndex();// 取出選了哪幾集漫畫

		if (selectList.length > 0) {
			// setStateText(Config.readyDownLoad);
			new Thread() {
				@Override
				public void run() {
					// 將每集漫畫的圖片連結取出來
					for (int i = 0; i < selectList.length; i++) {
						int index = selectList[i];
						// actDataObj.getActName(index);
						SingleComicData singleComic = actObj
								.getActComicData(index);

						String checkName = actObj.id + actObj.getCartoonName()
								+ "-" + singleComic.getName();
						if (downLoadTable.isDownloading(checkName)) {// 沒有在下載序列中才可以再下載
							setStateText(actObj.getCartoonName() + "-"
									+ singleComic.getName() + "已經在下載佇列中^^");
							// JOptionPane.showMessageDialog(null,
							// actObj.cartoonName + "-" + singleComic.name
							// + "已經在下載佇列中^^", "訊息",
							// JOptionPane.INFORMATION_MESSAGE);
							// break;
						} else if (downLoadTable.isFill()) {// 佇列已滿
							String msg = "目前下載中的佇列有"
									+ downLoadTable.getCurrentDownLoadSize()
									+ "同時下載最多" + Config.db.getDownCountLimit()
									+ "個^^";
							JOptionPane.showMessageDialog(null, msg, "訊息",
									JOptionPane.INFORMATION_MESSAGE);
							break;
						} else {// 新增下載任務
							EventHandle_Act.this.addTask(actObj, index);
						}
					}
					EventHandle_Act.this.setStateText(Config.DownLoadStart);
				}
			}.start();

		}
	}

	private void detail(final ActDataObj actObj) {
		if (actObj.isImgDownloadOK()) {
			JOptionPane.showMessageDialog(null, actObj.getDetail(),
					actObj.getCartoonName() + "漫畫簡介",
					JOptionPane.INFORMATION_MESSAGE, actObj.getImg());
		} else {
			JOptionPane.showMessageDialog(null, actObj.getDetail(),
					actObj.getCartoonName() + "漫畫簡介",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void addLove(final ActDataObj actObj) {
		boolean isOK = addComicToMyLove(actObj.id, actObj.getCartoonName());// 把目前在看的這一套漫畫加到我的最愛
		String msg = "";
		if (isOK) {
			msg = "編號[" + actObj.id + "]" + actObj.getCartoonName()
					+ "加到我的最愛ok^^";
		} else {
			msg = "編號[" + actObj.id + "]" + actObj.getCartoonName() + "已存在我的最愛";
		}
		JOptionPane.showMessageDialog(null, msg, "訊息",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * 設定右側下載漫畫狀態列參考
	 * 
	 * @param table
	 */
	public void setDataTableList(TableList table) {
		downLoadTable = table;
	}

	/**
	 * 設定狀態列文字
	 * 
	 * @param txt
	 */
	public void setStateText(String txt) {
		parentObj.setStateText(txt);
	}

	/**
	 * 新增漫畫資料到我的最愛
	 * 
	 * @param comicID
	 * @param comicName
	 */
	public boolean addComicToMyLove(String comicID, String comicName) {
		return parentObj.addToLove(comicID, comicName);
	}

	/**
	 * 設定上層物件參考
	 * 
	 * @param p
	 */
	public void setParentObj(JDownLoadUI_Act p) {
		parentObj = p;
	}

	void addTask(ActDataObj actObj, int index) {
		DownloadComicTask task = new DownloadComicTask(actObj, index);
		try {
			task.setParent(this);
			downLoadTable.addObj(task);
			task.createThreadTask();
		} catch (Exception e) {
			e.printStackTrace();
			downLoadTable.removeObj(task);
			String msg = e.getMessage();
			if (task != null) {
				msg = task.getCheckName() + "下載失敗";
			}
			JOptionPane.showMessageDialog(null, msg, "錯誤訊息",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * 移除下載中的任務
	 * 
	 * @param task
	 */
	public void removeTask(DownloadComicTask task) {
		downLoadTable.removeObj(task);
	}

	/**
	 * 設定一集漫畫資料物件
	 * 
	 * @param data
	 */
	public void setActDataObj(ActDataObj data) {
		actDataObj = data;
	}

	/**
	 * 取出被選取的漫畫位置
	 * 
	 * @return
	 */
	public int[] getSelectRowIndex() {
		return parentObj.getSelectRowIndex();
	}
}
