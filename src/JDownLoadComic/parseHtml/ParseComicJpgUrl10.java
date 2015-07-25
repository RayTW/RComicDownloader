package JDownLoadComic.parseHtml;

import java.util.ArrayList;

import JDownLoadComic.util.HttpReader;

/**
 * 2010.10~2014.03的網頁圖片解析方式
 * 
 * @author Ray
 * 
 */
public class ParseComicJpgUrl10 {
	/** 下載遠端檔案元件 */
	private HttpReader loadHtml;
	/** 這集漫畫的網址 */
	public String url; // 這集漫畫的網址
	/** 串漫網圖片網址的變數 */
	public int itemid; // 串漫網圖片網址的變數
	/** 串漫網圖片網址的變數 */
	public int chs; // 串漫網圖片網址的變數
	/** 編碼過的漫畫網址(需解碼) */
	private String codes; // 藏網址的亂碼
	/** 記錄每一頁網址 */
	private ArrayList pageUrl; // 記錄每一頁網址

	public ParseComicJpgUrl10() {
		codes = "";
		loadHtml = new HttpReader();
	}

	public boolean startParse(String url, int itemid) {
		this.itemid = itemid;
		if (codes.equals("")) {
			StringBuffer exception = new StringBuffer();
			codes = getCode(url, exception);

			if (exception.length() > 0) {
				System.out.println(getClass() + "," + exception);
				codes = "";
				return false;
			}
		}

		pageUrl = new ArrayList();
		String chStr = url.substring(url.indexOf("ch=") + "ch=".length(),
				url.length());
		int ch = 0;
		// int p=1;
		if (chStr.indexOf("-") > 0) {
			// p=Integer.parseInt(chStr.split("[-]")[1]);
			chStr = chStr.split("[-]")[0];
		}
		// int chs = chsTmp;//403
		// itemid = Integer.parseInt(itemidTmp);//104
		if (chStr.equals("")) {
			ch = 1;
		} else {
			ch = Integer.parseInt(chStr);
		}

		String[] codesArray = codes.split("[|]");

		String code = "";
		int cid = 0;
		for (int i = 0; i < codesArray.length; i++) {
			if (codesArray[i].indexOf(ch + " ") == 0) {
				cid = i;
				code = codesArray[i];
				break;
			}
		}
		if (code.equals("")) {
			for (int i = 0; i < codesArray.length; i++) {
				if (Integer.parseInt(codesArray[i].split("[ ]")[0]) > ch) {
					cid = i;
					code = codesArray[i];
					ch = Integer.parseInt(codesArray[i].split("[ ]")[0]);
					break;
				}
			}
		}
		if (code.equals("")) {
			cid = codesArray.length - 1;
			code = codesArray[cid];
			ch = chs;
		}

		String[] tmpCode = code.split("[ ]");
		String num = tmpCode[0];
		String sid = tmpCode[1];
		String did = tmpCode[2];
		int page = Integer.parseInt(tmpCode[3]);
		code = tmpCode[4];

		// 取得總頁數page，串出每一張圖片的url
		for (int p = 1; p <= page; p++) {
			String img = "";
			if (p < 10) {
				img = "00" + p;
			} else if (p < 100) {
				img = "0" + p;
			} else {
				img = "" + p;
			}
			int m = ((p - 1) / 10) % 10 + (((p - 1) % 10) * 3);
			// System.out.println(m);
			img += "_" + code.substring(m, m + 3);
			pageUrl.add("http://img" + sid + ".8comic.com/" + did + "/"
					+ itemid + "/" + num + "/" + img + ".jpg");
			// System.out.println("http://img"+sid+".8comic.com/"+did+"/"+itemid+"/"+num+"/"+img+".jpg");
		}
		return true;
	}

	/**
	 * 取得編碼過的網址 codes(亂碼字串)
	 * 
	 * @param url
	 * @return
	 */
	public String getCode(String url, StringBuffer exception) {
		String tmpCode = "";
		String html = loadHtml.getHTML_BIG5(url, exception);
		String[] tmpList = html.split("(\r\n)");

		System.out.println("html[" + html);

		for (int i = 0; i < tmpList.length; i++) {
			String tmpStr = tmpList[i];
			// 2013/04/06 修改取得每頁漫畫圖的變數名稱
			String codesParam = "var allcodes=";
			int codesBeginIndex = tmpStr.indexOf(codesParam);
			if (codesBeginIndex != -1) {
				tmpStr = tmpStr.substring(codesBeginIndex + codesParam.length()
						+ 1, tmpStr.length());
				int endIndex = tmpStr.indexOf("\"");
				tmpCode = tmpStr.substring(0, endIndex);
				break;
			}
		}
		return tmpCode;
	}

}
