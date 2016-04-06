package net.xuite.blog.ray00000test.rdownloadcomic.parseHtml;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import net.xuite.blog.ray00000test.rdownloadcomic.service.Config;
import net.xuite.blog.ray00000test.rdownloadcomic.util.ChineseWord;
import net.xuite.blog.ray00000test.rdownloadcomic.util.LoadNetFile;
import net.xuite.blog.ray00000test.rdownloadcomic.util.WriteFile;

/**
 * 每套漫畫所有資料
 * 
 * @author Ray
 * 
 */

public class ActDataObj {
	public static final int STATE_SYNC_READY = 10;
	public static final int STATE_SYNC_START = 20;
	public static final int STATE_SYNC_SUCCESS = 30;

	/** 漫畫所有集數資訊是否已下載完畢 */
	public int syncState;

	/** 漫畫圖片url用到的變數 */
	public int ch; // 漫畫圖片url用到的變數
	/** 漫畫編號 */
	public String id; // 漫畫編號
	/** 漫畫名稱 */
	private String cartoonName; // 漫畫名稱
	/** 作者 */
	private String author; // 作者
	/** 發行日期 */
	private String publishDate; // 發行日期
	/** 最後更新的時間 */
	private String lastUpdateDate; // 最後更新的時間
	/** 漫畫說明 */
	private String detailText; // 漫畫說明
	/** 放這套漫畫，每本漫畫物件資料 */
	private Hashtable<String, SingleComicData> comicList; // 放這套漫畫，每本漫畫物件資料
	/** 記錄漫畫的列表順序 */
	private ArrayList<SingleComicData> comicSorted; // 記錄漫畫的列表順序
	/** 封面圖片儲存路徑 */
	private String imgSaveUrl; // 封面圖片儲存路徑
	/** 下載封本圖執行緒 */
	private LoadNetFile lf; // 下載封本圖執行緒

	public ActDataObj() {
		initActDataObj();
	}

	/**
	 * 初始化
	 * 
	 */
	public void initActDataObj() {
		syncState = STATE_SYNC_READY;
		lastUpdateDate = "";
		comicList = new Hashtable<String, SingleComicData>();
		comicSorted = new ArrayList<SingleComicData>();
		imgSaveUrl = "";
		author = "";
		publishDate = "";
		detailText = "";
	}

	/**
	 * 新增一本漫畫，有下載過資料不重抓
	 * 
	 * @param name
	 *            漫畫名稱
	 * @param viewdata
	 *            這本漫畫網頁網址
	 */
	public void addActComic(String name, String[] viewdata) {
		if (name != null) {
			name = name.trim();
		}
		SingleComicData scData = comicList.get(id + name);
		if (scData == null) {
			scData = new SingleComicData();// 產生一本漫畫物件
			scData.setName(name);
			scData.itemid = Integer.parseInt(viewdata[0]);
			scData.url = Config.indexHtml + viewdata[1];// 這本漫畫網頁網址
			comicList.put(id + scData.getName(), scData);
			// comicSorted.add(scData);//由上至下排序為1~n集
			// 2013/02/28 改成排序由上至下排序為n~1集
			comicSorted.add(0, scData);
		}
		String comicActPath = "./" + Config.defaultSavePath + "/" + cartoonName
				+ "/" + scData.getName();
		File file = new File(comicActPath);
		if (file.exists()) {// 判斷是否有抓過這集漫畫
			if (file.list().length > 0) {// 漫畫裡的圖片有1張以上就認定為抓過此集
				scData.isDownload = true;
				return;
			}
		}
		scData.isDownload = false;
	}

	/**
	 * 設定作者名稱
	 * 
	 * @param aut
	 */
	public void setAuthor(String aut) {
		author = ChineseWord.unicodeToChineseAll(aut);
	}

	/**
	 * 取得作者名稱
	 * 
	 * @return
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * 設定發行日期
	 * 
	 * @param aut
	 */
	public void setPublishDate(String pdate) {
		publishDate = pdate;
	}

	/**
	 * 取得發行日期
	 * 
	 * @return
	 */
	public String getPublishDate() {
		return publishDate;
	}

	/**
	 * 取得最後更新日期
	 * 
	 * @return
	 */
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	/**
	 * 設定最後更新日期
	 * 
	 * @param aut
	 */
	public void setLastUpdateDate(String pdate) {
		lastUpdateDate = pdate;
	}

	/**
	 * 取出這款漫畫所有的集數列表
	 * 
	 * @return [0]:漫畫集數,[1]:漫畫網址
	 */
	public String[][] getActDataList() {
		String[][] actData = new String[comicSorted.size()][2];

		for (int i = 0; i < comicSorted.size(); i++) {
			SingleComicData scd = comicSorted.get(i);
			actData[i][0] = scd.getName();
			if (scd.isDownload) {
				actData[i][1] = Config.DOWNLOADED;
			} else {
				actData[i][1] = Config.NO_DOWNLOAD;
			}
			// actData[i][1] = Config.indexHtml + (String)actList.get(i);
		}
		return actData;
	}

	/**
	 * 取得指定位置的漫畫名稱、集數
	 * 
	 * @param index
	 * @return
	 */
	public String getActName(int index) {
		SingleComicData scd = comicSorted.get(index);

		return id + scd.getName();
	}

	/**
	 * 取得集數名稱
	 * 
	 * @return
	 */
	public String getAct(int index) {
		SingleComicData scd = comicSorted.get(index);
		return scd.getName();
	}

	/**
	 * 取得一本漫畫資料物件
	 * 
	 * @param index
	 * @return
	 */
	public SingleComicData getActComicData(int index) {
		// SingleComicData scd = comicSorted.get(index);
		return comicSorted.get(index);
	}

	/**
	 * 設定封面圖片
	 * 
	 * @param comicUrl
	 *            圖片所有集數網址
	 * @param imgUrl
	 *            封面圖片網址
	 */
	public void stargLoadImg(String comicUrl, String imgUrl, String saveUrl) {
		WriteFile.mkDir(saveUrl);
		if (imgSaveUrl.equals("")) {
			imgSaveUrl = saveUrl + "/" + id + ".jpg";
			if (!WriteFile.exists(imgSaveUrl)) {
				lf = new LoadNetFile();
				lf.setRefererLoadData(imgUrl, comicUrl, saveUrl + "/", id, true);
				lf.startLoad();// 開始下載封面圖片
			}
		}
	}

	/**
	 * 取得目前下載封本圖片已經載好
	 * 
	 * @return
	 */
	public boolean isImgDownloadOK() {
		return lf != null ? !lf.isRuning() : WriteFile.exists(imgSaveUrl);
	}

	/**
	 * 取得封面圖片
	 */
	public ImageIcon getImg() {
		return WriteFile.getImageIcon(imgSaveUrl);
	}

	/**
	 * 設定此漫畫簡介
	 * 
	 * @param d
	 */
	public void setDetail(String d) {
		detailText = getStrLine(ChineseWord.unicodeToChineseAll(
				d.replaceAll("<br>", "\n")).replaceAll("&nbsp;", ""));
	}

	/**
	 * 取得漫畫簡介
	 */
	public String getDetail() {
		return detailText;
	}

	/**
	 * 處理中文跳行
	 * 
	 * @param newLineNum
	 *            幾個中文字就跳行
	 * @return
	 */
	public String getStrLine(String str) {
		StringBuffer txt = new StringBuffer();

		int lineCount = 0;
		int index = 0;
		while (index < str.length()) {
			String tmp = str.substring(
					index,
					str.length() < (index += Config.db.getNewline()) ? str
							.length() : index);

			lineCount++;
			if (lineCount > 20) {
				// txt.append(tmp + ".....");
				break;
			} else {
				txt.append(tmp + "\n");
			}
		}
		// System.out.println(detailText);
		return txt.toString();
	}

	public String getCartoonName() {
		return cartoonName;
	}

	public void setCartoonName(String cartoonName) {
		if (cartoonName != null) {
			cartoonName = cartoonName.trim();
		}
		this.cartoonName = cartoonName;
	}

	/**
	 * 清空資料
	 * 
	 */
	public void clear() {

		lastUpdateDate = null;
		detailText = null;
	}

}
