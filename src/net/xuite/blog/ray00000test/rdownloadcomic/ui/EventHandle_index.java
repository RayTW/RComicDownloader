package net.xuite.blog.ray00000test.rdownloadcomic.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.ActDataObj;
import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.LoadComicData;
import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.LoadNewComicData;
import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.NewComic;
import net.xuite.blog.ray00000test.rdownloadcomic.service.Command;
import net.xuite.blog.ray00000test.rdownloadcomic.service.Config;
import net.xuite.blog.ray00000test.rdownloadcomic.service.RComicDownloader;
import net.xuite.blog.ray00000test.rdownloadcomic.util.JDataTable;

/**
 * 首頁所有漫畫列表事件處理
 * 
 * @author Ray
 * 
 */

public class EventHandle_index implements ActionListener,
		LoadComicData.Callback {
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
	protected Hashtable<String, ActDataObj> comicKind; // 已抓過的漫畫

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
		comicKind = new Hashtable();
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
			/*
			 * if (name.equals("ListAllAct")) { final LoadComicData tmpData =
			 * getNowSelectListIndex(); //final LoadComicData tmpData =
			 * getLoadComicData(0); final int[] list = getSelectRowIndex();//
			 * 取得漫畫的id編號
			 * 
			 * if (list.length > 0) { for (int i = 0; i < list.length; i++) {
			 * String comicNumber = tmpData.getCartoonID(list[i]); // String
			 * comicName = tmpData.getCartoonName(list[i]);
			 * 
			 * // 顯示一種漫畫所有的集數列表 creadActListJFrame(comicNumber); } } } else
			 */if (name.equals("update")) {
				if (!RComicDownloader.get().getDB().updateEnable()) {
					JOptionPane.showMessageDialog(null, "最後更新日期時間:"
							+ RComicDownloader.get().getDB().getUpdateDate()
							+ "\n更新間隔時間(小時):"
							+ RComicDownloader.get().getDB().getUpdateHours(),
							"訊息", JOptionPane.INFORMATION_MESSAGE);
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
					tmpDataLove.initLoadCartoonData(RComicDownloader.get()
							.getDB().getLoveComicList());
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
	public synchronized void creadActListJFrame(String comicNumber) {
		LoadComicData comicData = getLoadComicData(0);
		String comicName = comicData.findComicName(comicNumber);
		// ActDataObj被建立時狀態為STATE_SYNC_READY
		ActDataObj actData = getActData_actIndex(comicNumber, comicName);

		// 正準備開始同步漫畫集數
		if (actData.syncState == ActDataObj.STATE_SYNC_READY) {
			JDownLoadUI_Act down = comicListPool.get(actData.getCartoonName());
			if (down == null) {
				down = new JDownLoadUI_Act(this);
				comicListPool.put(actData.getCartoonName(), down);
				setStateText(Config.readyMsg + actData.getCartoonName());
				comicData.startSync(actData, this);
			}
			return;
		}
		// 開始同步漫畫集數
		if (actData.syncState == ActDataObj.STATE_SYNC_START) {
			setStateText("漫畫\"" + actData.getCartoonName() + "\"正在讀取集數列表中...");
			return;
		}
		// 漫畫集數已存在，且同步完成
		if (actData.syncState == ActDataObj.STATE_SYNC_SUCCESS) {
			JDownLoadUI_Act down = comicListPool.get(actData.getCartoonName());

			if (down != null) {
				down.setVisible(true);
				down.toFront();
			}
			return;
		}
	}

	// 打開漫畫集數列表時，同步完成的callback
	@Override
	public void onSynced(ActDataObj actObj) {
		JDownLoadUI_Act down = comicListPool.get(actObj.getCartoonName());
		if (down != null) {
			down.setActDataObj(actObj);
			down.pack();
			down.setLocation(parentObj.getX(), parentObj.getY());
			down.setDataTableList(downLoadTable);
			down.setVisible(true);
			down.toFront();
			setStateText(Config.loadOKMsg + actObj.getCartoonName());
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
			tmpDataLove.initLoadCartoonData(RComicDownloader.get().getDB()
					.getLoveComicList());
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
		RComicDownloader.get().getDB().addLoveComicList(comic);
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

		// final LoadComicData tmpData = getNowSelectListIndex();
		LoadComicData tmpData = getLoadComicData(0);
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
				String[] findDataAry = ((String) op).split("[|]");// [0]漫畫編號,[1]漫畫名稱
				String comicNumber = findDataAry[0];
				// String comicName = findDataAry[1];

				creadActListJFrame(comicNumber);
			}
		} else {
			JOptionPane.showMessageDialog(null, "沒有找到符合的漫畫名稱", "訊息",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// true:可更新
	public void updateComic(final JDataTable tableNew, final boolean showAlert) {
		if (RComicDownloader.get().getDB().getNewComicList().size() > 0) {
			LoadNewComicData loadNewData = new LoadNewComicData();
			loadNewData.initLoadCartoonData(RComicDownloader.get().getDB()
					.getNewComicList());
			addLoadDataObj(2, loadNewData);
			if (tableNew != null) {
				tableNew.addMutilRowDataArray(loadNewData.getIndexData());
			}
		}

		if (RComicDownloader.get().getDB().updateEnable()) {
			RComicDownloader.get().getDB().updateDate();
			new Thread() {
				@Override
				public void run() {
					setStateText(Config.waitUpdate);
					// LoadComicData tmpData = getNowSelectListIndex();
					LoadComicData tmpData = getLoadComicData(0);
					// 最新漫畫
					ArrayList<NewComic> newComicAry = new ArrayList<NewComic>();
					String[][] data = tmpData.updateComic(newComicAry);

					if (data.length > 0) {
						tmpData.initLoadCartoonData(RComicDownloader.get()
								.getDB().getComicList());// 有更新資料，重load
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
						tableNew.removeAll();
						LoadNewComicData loadNewData = new LoadNewComicData();
						loadNewData.loadNew(tableNew, newComicAry);
						addLoadDataObj(2, loadNewData);
					}
					System.out.println("ccc "
							+ tableNew.getJTable().getRowCount());
				}
			}.start();
		}
	}

	/**
	 * 用漫畫編號去load資料列表
	 * 
	 * @param comicNumber
	 *            漫畫編號
	 * @param comicName
	 *            漫畫名稱
	 * @return
	 */
	public ActDataObj getActData_actIndex(String comicNumber, String comicName) {
		ActDataObj actObj = comicKind.get(comicNumber);
		if (actObj != null) {
			return actObj;
		} else {
			actObj = new ActDataObj();
			actObj.id = comicNumber;
			comicKind.put(comicNumber, actObj);
			actObj.setCartoonName(comicName);
		}

		return actObj;
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
