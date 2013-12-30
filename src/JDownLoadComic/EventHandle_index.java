package JDownLoadComic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import JDownLoadComic.parseHtml.ActDataObj;
import JDownLoadComic.parseHtml.LoadComicData;
import JDownLoadComic.parseHtml.LoadNewComicData;
import JDownLoadComic.parseHtml.NewComic;
import JDownLoadComic.util.JDataTable;

/**
 * 首頁所有漫畫列表事件處理
 * 
 * @author Ray
 * 
 */

public class EventHandle_index implements ActionListener {
	/** 父層 */
	private JDownLoadUI_index parentObj;
	/** 從文字檔讀取的漫畫列表、我的最愛列表資料 */
	private ArrayList<LoadComicData> loadData; // 用來每讀取網頁資料
	/** 右邊下載狀態列 */
	private TableList downLoadTable;
	/** 接收使用者輸入的指令 */
	private Command cmd;
	/**
	 * 記錄目前被打開的漫畫列表，防止重複打開列表
	 */
	private HashMap<String, JDownLoadUI_Act> comicListPool;

	public EventHandle_index() {
		initEventHandle_index();
	}

	/**
	 * 初始化
	 * 
	 */
	public void initEventHandle_index() {
		loadData = new ArrayList<LoadComicData>();
		cmd = new Command();
		comicListPool = new HashMap<String, JDownLoadUI_Act>();
	}

	/**
	 * 首頁所有漫畫列表上的按鈕被點擊時產生的事件處理,以事件名稱分別為:<BR>
	 * 自定事件名稱/功能說明<BR>
	 * "ListAllJPG" 將所選取的漫畫集數加到下載列並開始下載<BR>
	 * "find" 搜尋漫畫<BR>
	 * "update" 更新漫畫到列表<BR>
	 * "love" 把漫畫編號加到我的最愛<BR>
	 * "delete" 將漫畫從列表上刪除(有限制只有我的最愛才可刪除)<BR>
	 */
	@Override
	public void actionPerformed(ActionEvent act) {
		Object obj = act.getSource();

		if (obj instanceof JButton) {
			JButton b = (JButton) obj;
			String name = b.getName();
			if (name.equals("ListAllAct")) {
				final LoadComicData tmpData = getNowSelectListIndex();
				final int[] list = getSelectRowIndex();// 取得漫畫的id編號

				if (list.length > 0) {
					new Thread() {
						@Override
						public void run() {
							setStateText(Config.readyMsg);
							for (int i = 0; i < list.length; i++) {
								// 顯示一種漫畫所有的集數列表
								creadActListJFrame(tmpData.getActData(list[i],
										tmpData.getCartoonName(list[i])));
							}
							setStateText(Config.loadOKMsg);
						}
					}.start();
				}
			} else if (name.equals("update")) {
				if (!Config.db.updateEnable()) {
					JOptionPane.showMessageDialog(
							null,
							"最後更新日期時間:" + Config.db.getUpdateDate()
									+ "\n更新間隔時間(小時):"
									+ Config.db.getUpdateHours(), "訊息",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					updateComic(null, true);
				}

			} else if (name.equals("love")) {
				int[] delectList = getSelectRowIndex();// 取得漫畫的id編號
				if (delectList.length > 0) {

					LoadComicData tmpData = getLoadComicData(0);
					LoadComicData tmpDataLove = getLoadComicData(1);
					StringBuffer loveComic = new StringBuffer();
					StringBuffer tmpComic = new StringBuffer();
					for (int i = 0; i < delectList.length; i++) {
						String comicID = tmpData.getCartoonID(delectList[i]);
						String comicName = tmpData
								.getCartoonName(delectList[i]);

						String tmp = tmpDataLove
								.writeMyLove(comicID, comicName);
						if (!tmp.equals("")) {
							addLoveComic(new String[] { comicID, comicName });// 將新增到我的最愛漫畫add到table上秀
						} else {
							loveComic.append("編號[" + comicID + "]" + comicName
									+ "\n");
							continue;
						}
						if (i < 10) {
							tmpComic.append(tmp + "\n");
						}
					}
					tmpDataLove.initLoadCartoonData(Config.db
							.getLoveComicList());
					String msg = tmpComic.toString();
					if (!msg.equals("")) {
						if (delectList.length > 10) {
							msg += "...\n共" + delectList.length + "本漫畫加到我的最愛";
						}
						JOptionPane.showMessageDialog(null, msg, "加到我的最愛漫畫資料",
								JOptionPane.INFORMATION_MESSAGE);
					}
					if (loveComic.length() > 0) {
						JOptionPane.showMessageDialog(null, loveComic,
								"下列漫畫已經在我的最愛囉!",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			} else if (name.equals("delete")) {
				// 把從我的最愛中選取要刪除漫畫刪除
				LoadComicData tmpDataLove = getNowSelectListIndex();
				tmpDataLove.clearLoveComicData(getSelectRowIndex());
				parentObj.tableLove.removeSelectedRows();
			} else if (name.equals("reader")) {
				ComicRedaerDialogMenu menu = new ComicRedaerDialogMenu(
						parentObj);
				menu.setModal(true);
				menu.setTitle("請點選要看的漫畫");
				menu.setVisible(true);
			} else if (name.equals("exportPDF")) {
				ComicExportPdfDialogMenu menu = new ComicExportPdfDialogMenu(
						parentObj);
				menu.setSize(menu.getWidth(), menu.getHeight()
						- parentObj.getStateMessage().getHeight() - 10);
				menu.setModal(true);
				menu.setTitle("請點選要轉成PDF的漫畫");
				menu.setVisible(true);
			}
		}
	}

	/**
	 * 建立 漫畫集數列表
	 * 
	 * @param actData
	 */
	public void creadActListJFrame(ActDataObj actData) {
		// 2013/02/28防止jframe被重複打開
		JDownLoadUI_Act down = comicListPool.get(actData.cartoonName);
		if (down == null) {
			down = new JDownLoadUI_Act(this, actData);
			comicListPool.put(actData.cartoonName, down);
			down.pack();
			down.setLocation((int) Config.db.indexBounds.getX(),
					(int) Config.db.indexBounds.getY());
			down.setVisible(true);
			down.setDataTableList(downLoadTable);
		} else {
			down.toFront();
		}
	}

	/**
	 * 新增一筆漫畫資料到我的最愛
	 * 
	 * @param comicID
	 * @param comicName
	 * @return true 加入ok | false 加入失敗
	 */
	public boolean addComicToLove(String comicID, String comicName) {
		LoadComicData tmpDataLove = getLoadComicData(1);
		String tmp = tmpDataLove.writeMyLove(comicID, comicName);
		if (!tmp.equals("")) {
			String[] data = new String[] { comicID, comicName };
			addLoveComic(data);// 將新增到我的最愛漫畫add到table上秀
			tmpDataLove.initLoadCartoonData(Config.db.getLoveComicList());
			return true;
		}
		return false;
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
	 * 將更新的漫畫新增到畫面列表
	 * 
	 * @param d
	 */
	public void addComicArray(String[][] d) {
		parentObj.addTable(d);
	}

	/**
	 * 將漫畫編號資料加到 我的最愛
	 * 
	 * @param comic
	 */
	public void addLoveComic(String[] comic) {
		parentObj.tableLove.addRowAryData(comic);
		Config.db.addLoveComicList(comic);
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
	 * 設定列表資料
	 * 
	 * @param d
	 *            所有漫畫列表
	 * @param d2
	 *            我的最愛漫畫列表
	 */
	public void addLoadDataObj(int index, LoadComicData d) {
		if (index >= loadData.size()) {
			loadData.add(index, d);
		} else {
			loadData.set(index, d);
		}
	}

	/**
	 * 更新漫畫集數列表
	 * 
	 */
	public void updateList() {
		int index = parentObj.getSelectList();
		if (index == 0) {
			parentObj.table.initJDataTalbe(true);
			parentObj.table.addMutilRowDataArray(getLoadComicData(0)
					.getIndexData());
		} else if (index == 1) {
			parentObj.tableLove.initJDataTalbe(true);
			parentObj.tableLove.addMutilRowDataArray(getLoadComicData(0)
					.getIndexData());
		}
	}

	/**
	 * 取得目前view的漫畫列表索引 0:所有漫畫 1:我的最愛
	 * 
	 * @return
	 */
	public int getSelectListIndex() {
		return parentObj.getSelectList();
	}

	/**
	 * 取出被選取的漫畫編號
	 * 
	 * @return
	 */
	public int[] getSelectRowIndex() {
		return parentObj.getSelectRowIndex();
	}

	/**
	 * 取得目前正在看的漫畫列表
	 * 
	 * @return
	 */
	public LoadComicData getNowSelectListIndex() {
		return getLoadComicData(parentObj.getSelectList());
	}

	/**
	 * 取得要搜尋的漫畫名稱關鍵字
	 * 
	 * @return
	 */
	public String getFindFieldText() {
		return parentObj.getFindFieldText();
	}

	/**
	 * 設定右側下載漫畫狀態列參考
	 * 
	 * @param table
	 */
	public void setDataTableList(TableList table) {
		downLoadTable = table;
	}

	public LoadComicData getLoadComicData(int index) {
		return loadData.get(index);
	}

	public void findComic(String cartoonKeyWord) {
		// 使用者下命令
		if (cartoonKeyWord.indexOf("./") == 0) {
			cmd.sysCmd(cartoonKeyWord.substring(2));
			return;
		}

		final LoadComicData tmpData = getNowSelectListIndex();
		// 將搜尋到的資料秀成列表
		// String cartoonKeyWord = getFindFieldText();
		ArrayList<String> possibleValues = tmpData
				.findCatroonToArrayList(cartoonKeyWord);
		// System.out.println(possibleValues);
		if (possibleValues != null && possibleValues.size() > 0) {
			final Object op = JOptionPane.showInputDialog(null,
					"請選擇要顯示的漫畫集數列表", "搜尋資料如下:",
					JOptionPane.INFORMATION_MESSAGE, null,
					possibleValues.toArray(), possibleValues.get(0));

			if (op != null) {// 若使用者有選擇資料，再去load漫畫集數列表
				new Thread() {
					@Override
					public void run() {
						setStateText(Config.readyMsg);
						String[] findDataAry = ((String) op).split("[|]");// [0]漫畫編號,[1]漫畫名稱
						creadActListJFrame(tmpData.getActData_actIndex(
								findDataAry[0], findDataAry[1]));
						setStateText(Config.loadOKMsg);
					}
				}.start();
			}
		} else {
			JOptionPane.showMessageDialog(null, "沒有找到符合的漫畫名稱", "訊息",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// true:可更新
	public void updateComic(final JDataTable tableNew, final boolean showAlert) {
		if (Config.db.updateEnable()) {
			Config.db.updateDate();
			new Thread() {
				@Override
				public void run() {
					setStateText(Config.waitUpdate);
					LoadComicData tmpData = getNowSelectListIndex();
					// 最新漫畫
					ArrayList<NewComic> newComicAry = new ArrayList<NewComic>();
					String[][] data = tmpData.updateComic(newComicAry);

					if (data.length > 0) {
						tmpData.initLoadCartoonData(Config.db.getComicList());// 有更新資料，重load
						addComicArray(data);
						if (showAlert) {
							JOptionPane.showMessageDialog(null, "此次更新"
									+ data.length + "本最新漫畫^^", "訊息",
									JOptionPane.INFORMATION_MESSAGE);
						}
					} else {
						if (showAlert) {
							JOptionPane.showMessageDialog(null, "沒有最新漫畫資料^^",
									"訊息", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					setStateText(Config.updateOK);
					if (tableNew != null) {
						LoadNewComicData loadNewData = new LoadNewComicData();
						loadNewData.loadNew(tableNew, newComicAry);
						addLoadDataObj(2, loadNewData);
					}

				}
			}.start();
		} else {
			if (Config.db.getNewComicList().size() > 0) {
				LoadNewComicData loadNewData = new LoadNewComicData();
				loadNewData.initLoadCartoonData(Config.db.getNewComicList());
				addLoadDataObj(2, loadNewData);
				if (tableNew != null) {
					tableNew.addMutilRowDataArray(loadNewData.getIndexData());
				}
			}
		}
	}

	/**
	 * 將記錄已被打開的漫畫列表移除
	 * 
	 * @param comicName
	 */
	public void removeFromComicPool(String comicName) {
		if (comicListPool != null) {
			comicListPool.remove(comicName);
		}
	}

	/**
	 * 清除漫畫集數列表使用的物件
	 * 
	 */
	public void close() {
		comicListPool.clear();
		comicListPool = null;
		downLoadTable = null;
		parentObj = null;
	}
}
