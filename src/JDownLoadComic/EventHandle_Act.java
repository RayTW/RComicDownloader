package JDownLoadComic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import JDownLoadComic.parseHtml.ActDataObj;
import JDownLoadComic.parseHtml.SingleComicData;

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

						String checkName = actObj.id + actObj.cartoonName + "-"
								+ singleComic.name;
						if (downLoadTable.isDownloading(checkName)) {// 沒有在下載序列中才可以再下載
							JOptionPane.showMessageDialog(null,
									actObj.cartoonName + "-" + singleComic.name
											+ "已經在下載佇列中^^", "訊息",
									JOptionPane.INFORMATION_MESSAGE);
							break;
						} else if (downLoadTable.isFill()) {
							String msg = "目前下載中的佇列有"
									+ downLoadTable.getCurrentDownLoadSize()
									+ "同時下載最多" + Config.db.getDownCountLimit()
									+ "個^^";
							JOptionPane.showMessageDialog(null, msg, "訊息",
									JOptionPane.INFORMATION_MESSAGE);
							break;
						} else {
							String savePath = "./" + Config.defaultSavePath
									+ "/" + actObj.cartoonName + "/"
									+ actObj.getAct(index);
							LoadBarState loadb = new LoadBarState();
							loadb.setParentObj(downLoadTable);
							loadb.setIdName(checkName);
							loadb.setLoadName(actDataObj.cartoonName + "-"
									+ singleComic.name);
							loadb.setDownloadPath(singleComic, savePath);
							downLoadTable.addObj(loadb);
						}
					}
					// 設定完使用者選擇的漫畫之後，再一次全廓執行開始下載，已開始下載的不會再重新啟動
					downLoadTable.startDownloadAll();
				}
			}.start();
			setStateText(Config.DownLoadStart);
		}
	}

	private void detail(final ActDataObj actObj) {
		if (actObj.isImgDownloadOK()) {
			JOptionPane.showMessageDialog(null, actObj.getDetail(),
					actObj.cartoonName + "漫畫簡介",
					JOptionPane.INFORMATION_MESSAGE, actObj.getImg());
		} else {
			JOptionPane.showMessageDialog(null, actObj.getDetail(),
					actObj.cartoonName + "漫畫簡介",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void addLove(final ActDataObj actObj) {
		boolean isOK = addComicToMyLove(actObj.id, actObj.cartoonName);// 把目前在看的這一套漫畫加到我的最愛
		String msg = "";
		if (isOK) {
			msg = "編號[" + actObj.id + "]" + actObj.cartoonName + "加到我的最愛ok^^";
		} else {
			msg = "編號[" + actObj.id + "]" + actObj.cartoonName + "已存在我的最愛";
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

	public void addLoadBar(LoadBarState loadBar) {
		downLoadTable.addObj(loadBar);
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

	/**
	 * 清除漫畫集數列表使用的物件
	 */
	public void close() {
		parentObj = null;
		actDataObj = null;
	}

}
