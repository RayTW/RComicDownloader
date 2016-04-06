package net.xuite.blog.ray00000test.rdownloadcomic.parseHtml;

import java.util.ArrayList;

import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.ParseComicJpgUrl20.ComicData;
import net.xuite.blog.ray00000test.rdownloadcomic.util.HttpReader;

/**
 * 單本漫畫資料
 * 
 * @author Ray
 * 
 */

public class SingleComicData {
	/** 下載遠端檔案元件 */
	private HttpReader loadHtml;

	/** 串漫網圖片網址的變數 */
	public int itemid; // 串漫網圖片網址的變數
	/** 集數名稱 */
	private String name; // 集數名稱

	/** 這集漫畫的網址 */
	public String url; // 這集漫畫的網址

	/** 標記目前這集漫畫是否下載過 true : 已下載, false:未下載 */
	public boolean isDownload; // true : 已下載, false:未下載
	/** 記錄每一頁網址 */
	private ArrayList pageUrl; // 記錄每一頁網址

	private ParseComicJpgUrl20 mParseComicJpgUrl20;

	public SingleComicData() {
		initSingleComicData();
	}

	/**
	 * 初始化
	 * 
	 */
	public void initSingleComicData() {
		loadHtml = new HttpReader();
	}

	/**
	 * 去網站下載所有的圖片網址
	 * 
	 * @param url
	 * @param codes
	 * @return
	 */
	public boolean setPageList() {
		StringBuffer exception = new StringBuffer();
		String html = loadHtml.getHTML_BIG5(url, exception);

		if (exception.toString().isEmpty()) {
			String ch = getCH();// 從url取得卷數或集數
			ComicData comicData = ParseComicJpgUrl20.getComicData(html);
			mParseComicJpgUrl20 = new ParseComicJpgUrl20(ch, comicData.chs,
					comicData.ti, comicData.cs);
			pageUrl = mParseComicJpgUrl20.getTotalPageUrl();
			return true;
		}
		return false;
	}

	/**
	 * 取得第index筆的圖片網址
	 */
	public String getJPGUrl(int point) {
		return (String) pageUrl.get(point);
	}

	/**
	 * 取出 格式為 「變數 = "xxx"」裡的xxx內容
	 * 
	 * @deprecated 網站改版，此方式無法取得資料
	 * @param txt
	 * @param isEncode
	 *            true 編碼 | false 解碼
	 * @return xxx
	 */
	@Deprecated
	public String getVarContent(String vatString) {
		int startIndex = vatString.indexOf("\"") + 1;
		// String beginStr = vatString.substring(0,startIndex);
		String tmpTxt = vatString.substring(startIndex, vatString.length());
		int endIndex = tmpTxt.indexOf("\"");
		// String lastStr = tmpTxt.substring(endIndex,tmpTxt.length());

		return tmpTxt.substring(0, endIndex);
	}

	public String getCH() {
		String chStr = url.substring(url.indexOf("ch=") + "ch=".length(),
				url.length());
		if (chStr.indexOf("-") > 0) {
			// p=Integer.parseInt(chStr.split("[-]")[1]);
			chStr = chStr.split("[-]")[0];
		}
		return chStr;
	}

	/**
	 * 取得此本漫畫頁數
	 * 
	 * @return
	 */
	public int getPageSize() {
		return pageUrl.size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null) {
			name = name.replaceAll(" ", "");
		}
		this.name = name;
	}
}
