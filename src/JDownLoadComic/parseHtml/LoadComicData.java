package JDownLoadComic.parseHtml;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import JDownLoadComic.Config;
import JDownLoadComic.util.HttpReader;
import JDownLoadComic.util.WriteFile;

/**
 * 讀取漫畫列表文字檔
 * 
 * @author Ray
 * 
 */

public class LoadComicData {
	// private String vision; //漫畫列表版本
	protected HttpReader loadHtml; // 讀取網址
	protected String[][] indexData; // 漫舉列表
	protected Hashtable comicKind; // 已抓過的漫畫
	protected Hashtable cviewUrlHash;
	protected WriteFile wf;

	public LoadComicData() {
		comicKind = new Hashtable();
		loadHtml = new HttpReader();
	}

	/**
	 * 
	 * @param path
	 *            漫畫列表文字檔路徑
	 */
	public LoadComicData(ArrayList ary) {
		this();
		initLoadCartoonData(ary);
	}

	/**
	 * 初始化，讀取漫畫列表文字檔
	 * 
	 * @param path
	 *            漫畫列表文字檔路徑
	 */
	public void initLoadCartoonData(ArrayList aryList) {
		// wf = new WriteFile();

		Object[] ary = aryList.toArray();
		indexData = new String[ary.length][];

		for (int i = 0; i < ary.length; i++) {
			indexData[i] = (String[]) ary[i];
		}
	}

	/**
	 * 取得所有漫畫列表,讀取文字檔裡的漫畫資料
	 * 
	 * @return
	 */
	public String[][] getIndexData() {
		return indexData;
	}

	/**
	 * 取出第n筆漫畫的編碼
	 * 
	 * @param rowIndex
	 *            漫畫的idnex
	 * @return
	 */
	public String getCartoonID(int rowIndex) {
		return indexData[rowIndex][0];
	}

	/**
	 * 取出第n筆漫畫的名稱
	 * 
	 * @param rowIndex
	 *            漫畫的idnex
	 * @return
	 */
	public String getCartoonName(int rowIndex) {
		return indexData[rowIndex][1];
	}

	/**
	 * 回傳資料總筆數
	 * 
	 * @return
	 */
	public int getDataLength() {
		return indexData.length;
	}

	/**
	 * 清除第n筆漫畫資料後，並更新 漫畫列表文字檔
	 * 
	 * @param deletRowIndex
	 *            陣列存放要清除的位置漫畫資料
	 * @param type
	 *            0:漫畫列表,1:我的最
	 */
	public void clearLoveComicData(int[] deletRowIndex) {
		if (deletRowIndex.length <= 0) {
			return;
		}

		// 把要刪除的位置漫畫clear掉
		for (int i = 0; i < deletRowIndex.length; i++) {
			// System.out.println(indexData[deletRowIndex[i]][0]);
			indexData[deletRowIndex[i]][0] = null;
			indexData[deletRowIndex[i]][1] = null;
		}

		Config.db.clearLoveComicList();
		for (int i = 0; i < indexData.length; i++) {
			if (indexData[i][0] != null) {
				Config.db.addLoveComicList(indexData[i]);
			}
		}
		// 把資料重讀回來
		initLoadCartoonData(Config.db.getLoveComicList());
	}

	/**
	 * 搜尋漫畫在第幾筆
	 * 
	 * @param findName
	 *            要搜尋的名稱
	 * @param startIndex
	 *            從第幾筆開始搜尋
	 * @return
	 */
	public int findCartoonIndex(String findName, int startIndex) {
		if (startIndex >= getDataLength()) {
			return -1;
		}

		// Config.db.loveComicInfo();
		for (int i = startIndex; i < getDataLength(); i++) {
			String cartoonName = indexData[i][1];
			if (cartoonName.indexOf(findName) != -1) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 取出所有符合搜尋條件的資料
	 * 
	 * @param findName要搜尋的漫畫名稱
	 * @return
	 */
	public ArrayList<String> findCatroonToArrayList(String findName) {
		if (findName.trim().equals("")) {
			return null;
		}
		ArrayList<String> catList = new ArrayList<String>();

		for (int i = 0; i < getDataLength(); i++) {
			String cartoonName = getCartoonName(i);
			if (cartoonName.indexOf(findName) != -1) {
				String dataName = getCartoonID(i) + "|" + cartoonName;
				catList.add(dataName);
			}
		}
		return catList;
	}

	/**
	 * 取得一種漫畫所有的集數列表,用ROWd 位置轉換成真正的漫畫編號
	 * 
	 * @param rowIndex
	 *            選擇第幾筆漫畫
	 * @return
	 */
	public ActDataObj getActData(int rowIndex, String ComicName) {
		return getActData_actIndex(getCartoonID(rowIndex), ComicName);// 取出第n筆漫畫真正的ID編號
	}

	/**
	 * 用漫畫編號去load資料列表
	 * 
	 * @param actIndex
	 *            漫畫編號
	 * @param ComicName
	 *            漫畫名稱
	 * @return
	 */
	public ActDataObj getActData_actIndex(String actIndex, String ComicName) {
		ActDataObj actObj = (ActDataObj) comicKind.get(actIndex);
		if (actObj != null) {
			return actObj;
		} else {
			actObj = new ActDataObj();
			actObj.id = actIndex;
			comicKind.put(actIndex, actObj);
			actObj.cartoonName = ComicName;
		}

		String comicUrl = Config.indexHtml + "/html/" + actIndex + ".html";

		String imgurl = Config.imgUrl + "/" + actIndex + ".jpg";
		String imgSavePath = Config.defaultImgPath;
		actObj.stargLoadImg(comicUrl, imgurl, imgSavePath);// 開始下載封面圖片
		ArrayList dataAry = loadHtml.getHTMLtoArrayList(comicUrl,
				Config.actLang);
		String viewData = "";
		String tmpch = "";
		String findCview = "cview(";
		String findDeaitlTag = "style=\"padding:10px;line-height:25px\">";// 用來找漫畫簡介的字串位置
		// String replaceStr =
		// "<td colspan=\"2\" nowrap=\"nowrap\">";//漫畫網站改版後，有多了這個字串排版，將他replaceAll

		for (int i = 0; i < dataAry.size(); i++) {
			String txt = (String) dataAry.get(i);
			if (txt.indexOf(findCview) != -1) {
				viewData = txt;
				String[] data = viewData
						.substring(
								viewData.indexOf(findCview)
										+ findCview.length(),
								viewData.indexOf(");return"))
						.replaceAll("'", "").split("[,]");
				tmpch = data[0];
				String comicOneBookName = dataAry.get(i + 1).toString();// 漫畫集數名稱
				comicOneBookName = removeScriptsTag(comicOneBookName);// 2012/11/07
																		// 去除scripts
																		// tag
				comicOneBookName = replaceTag(comicOneBookName);// 2012/11/07
																// 去除td tag
				// 2013/09/15 修正漫畫集數出現":"
				comicOneBookName = comicOneBookName.replaceAll("[:]", "：");

				actObj.addActComic(comicOneBookName, cview(data[0], data[1]));
			} else {
				if (actObj.getDetail().equals("")) {
					if (txt.indexOf(findDeaitlTag) != -1) {// 找到簡介說明
						int detailStart = txt.indexOf(findDeaitlTag)
								+ findDeaitlTag.length();
						int detailEnd = txt.indexOf("</td>");
						actObj.setDetail(txt.substring(detailStart, detailEnd));
					}
				}
				if (actObj.getAuthor().equals("")) {
					String authorTag = "作者：</td>";
					if (txt.indexOf(authorTag) != -1) {// 找到作者
						// 2012/11/07 動漫網站有修改作者tag
						// actObj.setAuthor(replaceTag(findBookData(authorTag,(String)dataAry.get(i+1))));
						String author = replaceTag((String) dataAry.get(i + 1));
						actObj.setAuthor(author);
					}
				} else if (actObj.getLastUpdateDate().equals("")) {
					String lastUpdateDateTag = "更新：</td>";
					if (txt.indexOf(lastUpdateDateTag) != -1) {// 找到最後更新日期
						// 2012/11/07 修正最後更新日期tag
						String updateDate = replaceTag((String) dataAry
								.get(i + 1));
						actObj.setLastUpdateDate(updateDate);
						// actObj.setLastUpdateDate(replaceTag(findBookData(lastUpdateDateTag,(String)dataAry.get(i+1))));
					}
				}/*
				 * else if(actObj.getPublishDate().equals("")){//2010/08/14
				 * 此網站已不留發行日期 String publishDateTag = "出品：</td>";
				 * if(txt.indexOf(publishDateTag) != -1){//找到發行日期
				 * actObj.setPublishDate
				 * (findBookData(publishDateTag,(String)dataAry.get(i+1))); } }
				 */
			}
		}

		actObj.ch = Integer.parseInt(tmpch.split("[.]")[0].split("[-]")[1]);
		return actObj;
	}

	/**
	 * 找某tag下一行的資料
	 * 
	 * @param findTag
	 * @param nextTxt
	 * @return
	 */
	public String findBookData(String findTag, String nextTxt) {
		String findAu = "nowrap>";
		int auStart = nextTxt.indexOf(findAu) + findAu.length();
		int auEnd = nextTxt.indexOf("</td>");
		return nextTxt.substring(auStart, auEnd);
	}

	/**
	 * 取自原網站javascript function，功能為可取得漫畫真正的路徑 [0]:itemid,[1]:baseurl + url
	 * 
	 * @param url
	 * @param catid
	 * @return
	 */
	public String[] cview(String url, String catid) {
		String[] data = new String[2];
		String baseurl = "";
		if (cviewUrlHash == null) {
			cviewUrlHash = loadCviewJS(Config.cviewURL, Config.actLang);
		}

		baseurl = cviewUrlHash.get(catid).toString();

		url = url.replaceAll(".html", "").replaceAll("-", ".html?ch=");
		// System.out.println("   url" +url);
		data[0] = url.split("[.]")[0];
		data[1] = baseurl + url;
		return data;
	}

	/**
	 * 向動漫伺服器要comicview.js檔，並解析裡面的數字對應的網址
	 * 
	 * @param url
	 * @param fmt
	 * @return
	 */
	private Hashtable loadCviewJS(String url, String fmt) {
		ArrayList dataAry = loadHtml.getHTMLtoArrayList(Config.cviewURL,
				Config.actLang);
		Hashtable cviewHash = new Hashtable();

		String startTag = "if(";
		String endTab = "baseurl=\"";
		String urlStratTag = endTab;
		String urlEndTag = "\";";

		for (int i = 0; i < dataAry.size(); i++) {
			String txt = (String) dataAry.get(i);
			if (txt.length() > 0) {

				if (txt.indexOf(startTag) != -1) {
					txt = txt.replaceAll("[)]", "");
					String[] numCodeAry = txt.substring(startTag.length(),
							txt.indexOf(endTab)).split("[|][|]");
					String cviewUrl = txt.substring(
							txt.indexOf(urlStratTag) + urlStratTag.length(),
							txt.indexOf(urlEndTag)).trim();
					for (int j = 0; j < numCodeAry.length; j++) {
						String numCode = (numCodeAry[j].trim()).split("[=][=]")[1]
								.trim();
						cviewHash.put(numCode, cviewUrl);
					}
				}
			}
			if (txt.indexOf("url=url") != -1) {
				break;
			}
		}
		dataAry.clear();
		return cviewHash;
	}

	/**
	 * 更新漫畫
	 * 
	 * @return 新的漫畫列表[0]:漫畫編號,[1]:漫畫名稱
	 */
	public String[][] updateComic(ArrayList<NewComic> newComicAry) {
		Hashtable data = new Hashtable();

		// 讀取最近出的漫畫
		for (int i = 1; i <= Config.db.refrushPage; i++) {
			String url = Config.indexHtml + "comic/u-" + i + ".html";
			updateNewComic(url, Config.actLang, data, newComicAry);
		}

		// 將已經有的漫畫移除，留下新出的漫畫
		for (int i = 0; i < getDataLength(); i++) {
			data.remove(getCartoonID(i));
		}

		String[][] comicArray = new String[data.size()][2];
		if (data.size() > 0) {

			Set set = data.entrySet();
			Iterator t = set.iterator();

			// 將讀取到的新漫畫存成array回傳，再新增到畫面列表上
			int i = 0;
			while (t.hasNext()) {
				Map.Entry m = (Map.Entry) t.next();
				String id = (String) m.getKey();
				String name = (String) m.getValue();

				comicArray[i][0] = id;
				comicArray[i][1] = name;
				i++;
			}

			// 2012/11/07 將更新的漫畫編號由小到大排序
			comicArray = sortComicAry(comicArray);

			for (int j = 0; j < comicArray.length; j++) {
				String id = comicArray[j][0];
				String name = comicArray[j][1];

				Config.db.addComicList(new String[] { id, name });
			}
			Config.db.save();
		}
		return comicArray;
	}

	/**
	 * 以漫畫id進行排序(由小到大)
	 * 
	 * @param ary
	 * @return
	 */
	private String[][] sortComicAry(String[][] a) {
		for (int i = 0; i < a.length - 1; i++) {
			for (int j = 0; j < a.length - i - 1; j++) {
				if (Integer.parseInt(a[j + 1][0]) < Integer.parseInt(a[j][0])) {
					String[] temp = a[j + 1]; // 交換陣列元素
					a[j + 1] = a[j];
					a[j] = temp;
				}
			}
		}
		return a;
	}

	/**
	 * 把漫畫寫入我的最愛文字檔
	 * 
	 * @param comicID
	 *            漫畫編號
	 * @param comicName
	 *            漫畫名稱
	 * @return
	 */
	public String writeMyLove(String comicID, String comicName) {
		String str = "";
		int find = findCartoonIndex(comicName, 0);// 檢查要寫入我的最愛漫畫是否已經有存在
		// System.out.println("找["+comicName+"]漫畫==>" + find);
		if (find == -1) {// 要新增到我的最愛的漫畫沒在裡面才可以新增
			str = comicID + "|" + comicName;
			// wf.writeText_UTF8_Apend(str + "\r\n", Config.defaultSavePath +
			// "/" + Config.myLoveFileName);
		}
		return str;
	}

	/**
	 * 去網站讀最新出的漫畫
	 * 
	 * @param url
	 *            每週最新漫畫的網址
	 * @param fmt
	 *            網頁格式
	 * @param data
	 *            存新漫畫的容器,key:漫畫編號,value:漫畫名稱
	 */
	public void updateNewComic(String url, String fmt, Hashtable data,
			ArrayList<NewComic> newComicAry) {
		HttpReader lf = new HttpReader();
		ArrayList al = lf.getHTMLtoArrayList(url, fmt);

		for (int i = 0; i < al.size(); i++) {
			String html = al.get(i).toString();

			if (NewComic.hasTag(html)) {
				String nextHtml = al.get(i + 1).toString();
				NewComic obj = NewComic.parser(html, nextHtml);

				if (obj != null) {
					data.put(obj.id, obj.name);
					newComicAry.add(obj);
				}
			}
		}
	}

	/**
	 * 去掉"<"開頭與">"結尾的字串
	 * 
	 * @param txt
	 * @return
	 */
	public static String replaceTag(String txt) {
		StringBuffer data = new StringBuffer();
		char st = '<';
		char ed = '>';
		char[] charAry = txt.toCharArray();
		boolean check = false;

		for (int i = 0; i < charAry.length; i++) {
			char c = charAry[i];
			if (c == st) {
				check = true;
			} else if (c == ed) {
				check = false;
				continue;
			}
			if (check) {
				continue;
			}
			if (c == '\r' || c == '\n') {
				continue;
			}
			data.append(c);
		}
		return data.toString();
	}

	// 去除javascripts語法tag(包含tag裡面的程式)
	public static String removeScriptsTag(String st) {
		String ret = st;
		String beginStr = "<script>";
		String endStr = "</script>";
		int bIndex = st.indexOf(beginStr);
		int eIndex = st.indexOf(endStr);

		// System.out.println(bIndex+"==" + eIndex);
		if (bIndex != -1 && eIndex != -1) {
			ret = st.substring(0, bIndex);
			ret += st.substring(eIndex + endStr.length(), st.length());
			// st = st.substring(bIndex + beginStr.length(), eIndex);
		}
		return ret;

	}

}
