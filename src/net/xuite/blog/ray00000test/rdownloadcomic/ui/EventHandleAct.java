package net.xuite.blog.ray00000test.rdownloadcomic.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.ActDataObj;
import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.SingleComicData;
import net.xuite.blog.ray00000test.rdownloadcomic.service.Config;
import net.xuite.blog.ray00000test.rdownloadcomic.service.DownloadComicTask;
import net.xuite.blog.ray00000test.rdownloadcomic.service.RComicDownloader;

/**
 * 單本漫畫所有集數列表的事件處理
 * 
 * @author Ray
 * 
 */

public class EventHandleAct implements ActionListener {
	/** 父層參考 */
	private JDownLoadUIAct parentObj;
	/** 整套漫畫連結資料、總集數等等.. */
	private ActDataObj actDataObj;
	/** 漫畫集數列表 */
	private TableList downLoadTable;

	public EventHandleAct() {
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
				int[] selectList = getSelectRowIndex();// 取出選了哪幾集漫畫
				
				listAllJPG(actDataObj, selectList);
			} else if (b.getName().equals("detial")) {
				detail(actDataObj);
			} else if (b.getName().equals("addLove")) {
				addLove(actDataObj);
			}
		}
	}

	public void listAllJPG(final ActDataObj actObj, final int[] selectList) {
		if (selectList.length > 0) {
			new Thread() {
				@Override
				public void run() {
					// 將每集漫畫的圖片連結取出來
					for (int i = 0; i < selectList.length; i++) {
						int index = selectList[i];
						SingleComicData singleComic = actObj
								.getActComicData(index);

						String checkName = actObj.id + actObj.getCartoonName()
								+ "-" + singleComic.getName();
						if (downLoadTable.isDownloading(checkName)) {// 沒有在下載序列中才可以再下載
							setStateText(actObj.getCartoonName() + "-"
									+ singleComic.getName() + "已經在下載佇列中^^");
						} else if (downLoadTable.isFill()) {// 佇列已滿
							String msg = "目前下載中的佇列有"
									+ downLoadTable.getCurrentDownLoadSize()
									+ "同時下載最多"
									+ RComicDownloader.get().getDB()
											.getDownCountLimit() + "個^^";
							JOptionPane.showMessageDialog(null, msg, "訊息",
									JOptionPane.INFORMATION_MESSAGE);
							break;
						} else {// 新增下載任務
							RComicDownloader.get().addDownloadTask(
									new DownloadComicTask.Callback() {
										@Override
										public void onPrepare(
												DownloadComicTask task,
												SingleComicData data) {
											downLoadTable.addObj(task);
											setStateText(actObj.getCartoonName()
													+ " "
													+ data.getName()
													+ " 準備下載");
										}

										@Override
										public void onSuccess(
												DownloadComicTask task,
												SingleComicData data) {
											downLoadTable.removeObj(task);
											setStateText(actObj.getCartoonName()
													+ " "
													+ data.getName()
													+ " 下載完成");
										}

										@Override
										public void onFail(
												DownloadComicTask task,
												SingleComicData data,
												String reason) {
											downLoadTable.removeObj(task);
											setStateText(actObj.getCartoonName()
													+ " "
													+ data.getName()
													+ " 下載失敗，原因:" + reason);
										}

										@Override
										public void onCancel(
												DownloadComicTask task,
												SingleComicData data) {
											downLoadTable.removeObj(task);
											setStateText(actObj.getCartoonName()
													+ " "
													+ data.getName()
													+ " 下載取消");
										}
									}, actObj, index);

						}
					}
					EventHandleAct.this.setStateText(Config.DownLoadStart);
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
	public void setParentObj(JDownLoadUIAct p) {
		parentObj = p;
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
