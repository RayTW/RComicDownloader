package JDownLoadComic;

import javax.swing.JOptionPane;

import JDownLoadComic.util.URLEncodeDecodeTool;

/**
 * 1.動漫首頁網址<BR>
 * 2.網頁編語格式(big5)<BR>
 * 3.漫畫下載速度、存檔路徑等設定值<BR>
 * 4.下載狀態訊息內容<BR>
 * 
 * 2013-01-06 1.增加顯示最新漫畫列表 2013-04-06 1.修改SingleComicData，網站將圖片編碼變數進行修改以及讀取不到圖片
 * 
 * @author Ray
 * 
 */
public class Config {
	/** 版本 */
	public static final String version = "v1.21.6";
	// 首頁的title
	public static String indexName = "漫畫下載%s \t 程式設計: Ray";

	/** 無限動漫首頁網址 */
	public static String indexHtml = "http://www.8comic.com/"; // 無限動漫首頁網址
	/** 漫畫封面圖片前段網址 */
	public static String imgUrl = indexHtml + "pics/0/"; // 漫畫封面圖片前段網址
	/** 網頁編碼 */
	public static String actLang = "big5"; // 網頁編碼
	/** 所有漫畫索引列表檔案路徑 */
	public static String indexList = "./ComicList.data"; // 所有漫畫索引列表
	/** 預設下載存放資料夾名稱 */
	public static String defaultSavePath = "漫畫下載區"; // 預設下載存放資料夾名稱

	// 所有漫畫伺服器的網址 javascript
	// ,對方是透過此js檔將計算後的value傳入後取回正確的漫畫前段網址
	/** 對方是透過此js檔將計算後的value傳入後取回正確的漫畫前段網址，勿更動 */
	public static String cviewURL = indexHtml + "js/comicview.js";

	/** 預覽過的 封面圖片存放區 */
	public static String defaultImgPath = defaultSavePath + "/" + "icon";
	/** 看漫畫時產生的HTML檔暫存區 */
	public static String tempFolderPath = defaultSavePath + "/" + "temp";
	/** 每幾秒換下載下一頁 */
	public static double pageSec = 0.1; // 每幾秒換下載下一頁
	/** 正在讀取漫畫列表時訊息 */
	public static String readyMsg = "讀取漫畫資料，產生漫畫列表中，請等待..";
	/** 讀取漫畫列表完成時 */
	public static String loadOKMsg = "讀取漫畫資料完成...";
	/** 更新漫畫列表時訊息 */
	public static String waitUpdate = "讀取漫畫更新資料中，請等待...";
	/** 更新漫畫完成訊息 */
	public static String updateOK = "更新漫畫完成";

	/** 漫畫最新版本公告txt的url */
	public static String newUrl = "http://blog.xuite.net/ray00000test/blog/66855641";// 更新版本訊息的txt檔url
	/** 漫畫最新版本公告的格式 */
	public static String newsCharset = "utf-8";

	/** 開始下載單集漫畫圖片時訊息 */
	public static String DownLoadStart = "開始下載漫畫圖片...";

	/** 目前單集漫畫是否已下載過訊息(已存在) */
	public static String DOWNLOADED = "YES";
	/** 目前單集漫畫是否已下載過訊息(已存在) */
	public static String NO_DOWNLOAD = "NO";

	// 看漫畫列表的日期時間格式
	public static String dateFormat = "yyyy/MM/dd HH:mm:ss";

	public static String dbPath = defaultSavePath + "/sys/comic.obj";
	public static DatabaseLite db = new DatabaseLite();

	public static String cmdList = "./help:列出所有可使用的指令\n"
			+ "./updatehour h:修改漫畫更新時間間隔，預設為3小時,範例\"./updatehour 5\"\n"
			+ "./showhour:顯示修改漫畫更新時間間隔\n"
			+ "./reader w h:修改看漫畫的圖寬高,不帶參數為使用圖片預設寬高,範例\"./reader 320 480\"\n"
			+ "./showupdate:顯示漫畫最後修改日期時間\n";

	/** 漫畫閱讀器html */
	public static String readerHTML = "%3Chtml%3E%0D%0A%3Chead%3E%0D%0A++++%3Cmeta+http-equiv%3D%22Content-Type%22+content%3D%22text%2Fhtml%3B+charset%3DUTF-8%22%3E%0D%0A++++%3Cscript%3E%0D%0A++++++++var+list+%3D+%22%25s%22%3B%0D%0A++++++++var+floderPath+%3D+%22%25s%22%3B%0D%0A++++++++var+imgWH+%3D+%22%25s%22%3B%0D%0A++++++++var+changePageEnable+%3D+%22%25s%22%3B%0D%0A++++++++%0D%0A++++++++function+onload%28%29%7B%0D%0A++++++++++++var+obj+%3D+new+Object%28%29%3B%0D%0A++++++++++++obj.topLastBtn+%3D+document.getElementById%28%22topLastBtn%22%29%3B%0D%0A++++++++++++obj.topSelect+%3D+document.getElementById%28%22topSelect%22%29%3B%0D%0A++++++++++++obj.topNextBtn+%3D+document.getElementById%28%22topNextBtn%22%29%3B%0D%0A++++++++++++obj.img+%3D+document.getElementById%28%22img%22%29%3B%0D%0A++++++++++++obj.bottomLastBtn+%3D+document.getElementById%28%22bottomLastBtn%22%29%3B%0D%0A++++++++++++obj.bottomSelect+%3D+document.getElementById%28%22bottomSelect%22%29%3B%0D%0A++++++++++++obj.bottomNextBtn+%3D+document.getElementById%28%22bottomNextBtn%22%29%3B%0D%0A++++++++++++obj.jpgAry+%3D+list.split%28%22%7C%22%29%3B%0D%0A++++++++++++obj.folder+%3D+floderPath%3B%0D%0A%0D%0A++++++++++++%0D%0A++++++++++++var+comicObj+%3D+new+Comiclreader%28obj%29%3B%0D%0A++++++++++++comicObj.init%28%29%3B%0D%0A++++++++%7D%0D%0A++++++++%0D%0A++++++++function+Comiclreader%28obj%29%7B%0D%0A++++++++++++var+self+%3D+this%3B%0D%0A++++++++++++%0D%0A++++++++++++self.flag+%3D+0%3B%0D%0A++++++++++++self.topLastBtn+%3D+obj.topLastBtn%3B%0D%0A++++++++++++self.topSelect+%3D+obj.topSelect%3B%0D%0A++++++++++++self.topNextBtn+%3D+obj.topNextBtn%3B%0D%0A++++++++++++self.img+%3D+obj.img%3B%0D%0A++++++++++++self.bottomLastBtn+%3D+obj.bottomLastBtn%3B%0D%0A++++++++++++self.bottomSelect+%3D+obj.bottomSelect%3B%0D%0A++++++++++++self.bottomNextBtn+%3D+obj.bottomNextBtn%3B%0D%0A++++++++++++self.jpgAry+%3D+obj.jpgAry%3B%0D%0A++++++++++++%0D%0A++++++++++++self.init+%3D+function%28%29%7B%0D%0A++++++++++++++++self.topLastBtn.onclick+%3D+self.lastPage%3B%0D%0A++++++++++++++++self.topNextBtn.onclick+%3D+self.nextPage%3B%0D%0A++++++++++++++++self.bottomLastBtn.onclick+%3D+self.lastPage%3B%0D%0A++++++++++++++++self.bottomNextBtn.onclick+%3D+self.nextPage%3B%0D%0A++++++++++++++++self.topSelect.onchange+%3D+self.selectOnChange%3B%0D%0A++++++++++++++++self.bottomSelect.onchange+%3D+self.selectOnChange%3B%0D%0A++++++++++++++++%0D%0A++++++++++++++++self.topSelect.options.length+%3D+0%3B%0D%0A++++++++++++++++self.bottomSelect.options.length+%3D+0%3B%0D%0A++++++++++++++++%0D%0A++++++++++++++++if%28imgWH+%21%3D+%220x0%22%29%7B%0D%0A++++++++++++++++++++var+wh+%3D+imgWH.split%28%22x%22%29%3B%0D%0A++++++++++++++++++++self.img.width+%3D+parseInt%28wh%5B0%5D%29%3B%0D%0A++++++++++++++++++++self.img.height+%3D+parseInt%28wh%5B1%5D%29%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++%2F%2F%E9%BB%9E%E6%93%8A%E5%9C%96%E7%89%87%E6%8F%9B%E4%B8%8B%E4%B8%80%E9%A0%81%0D%0A++++++++++++++++if%28changePageEnable+%3D%3D+%22Y%22%29%7B%0D%0A++++++++++++++++++++self.img.onclick+%3D+self.nextPage%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++%0D%0A++++++++++++++++for%28var+i+%3D+0%3B+i+%3C+self.jpgAry.length%3B+i%2B%2B%29%7B%0D%0A++++++++++++++++++++self.topSelect.options.add%28new+Option+%28self.jpgAry%5Bi%5D%2C+i%29%29%3B%0D%0A++++++++++++++++++++self.bottomSelect.options.add%28new+Option+%28self.jpgAry%5Bi%5D%2C+i%29%29%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++%0D%0A++++++++++++++++self.setSelectIndex%28self.flag%29%3B%0D%0A++++++++++++++++self.setImgSrc%28self.flag%29%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++%2F%2F%E4%B8%8A%E4%B8%80%E9%A0%81%0D%0A++++++++++++self.lastPage+%3D+function%28%29%7B%0D%0A++++++++++++++++if%28self.checkLast%28self.flag%29%29+return%3B%0D%0A++++++++++++++++self.flag--%3B%0D%0A++++++++++++++++self.setSelectIndex%28self.flag%29%3B%0D%0A++++++++++++++++self.setImgSrc%28self.flag%29%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++self.selectOnChange+%3D+function%28event%29%7B%0D%0A++++++++++++++++var+selectObj+%3D+event.currentTarget%3B%0D%0A++++++++++++++++%0D%0A++++++++++++++++self.flag+%3D+selectObj.selectedIndex%3B%0D%0A++++++++++++++++self.setSelectIndex%28self.flag%29%3B%0D%0A++++++++++++++++self.setImgSrc%28self.flag%29%3B%0D%0A++++++++++++++++self.scrollToTop%28%29%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++%2F%2F%E4%B8%8B%E4%B8%80%E9%A0%81%0D%0A++++++++++++self.nextPage+%3D+function%28%29%7B%0D%0A++++++++++++++++if%28self.checkNext%28self.flag%29%29+return%3B%0D%0A++++++++++++++++self.flag%2B%2B%3B%0D%0A++++++++++++++++self.setSelectIndex%28self.flag%29%3B%0D%0A++++++++++++++++self.setImgSrc%28self.flag%29%3B%0D%0A++++++++++++++++self.scrollToTop%28%29%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++self.setImgSrc+%3D+function%28page%29%7B%0D%0A++++++++++++++++var+imgPath+%3D+obj.folder+%2B+obj.jpgAry%5Bpage%5D%3B%0D%0A++++++++++++++++self.img.src+%3D+imgPath%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++self.checkLast+%3D+function%28page%29%7B%0D%0A++++++++++++++++if%28%28page+-+1%29+%3C+0%29%7B%0D%0A++++++++++++++++++++alert%28%22%E7%AC%AC1%E9%A0%81%E5%9B%89%22%29%3B%0D%0A++++++++++++++++++++return+true%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++return+false%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++self.checkNext+%3D+function%28page%29%7B%0D%0A++++++++++++++++if%28%28page+%2B+1%29+%3E%3D+obj.jpgAry.length%29%7B%0D%0A++++++++++++++++++++alert%28%22%E6%9C%80%E5%BE%8C1%E9%A0%81%E5%9B%89%22%29%3B%0D%0A++++++++++++++++++++return+true%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++return+false%3B%0D%0A++++++++++++%7D%0D%0A%0D%0A++++++++++++self.setSelectIndex+%3D+function%28index%29%7B%0D%0A++++++++++++++++self.topSelect.selectedIndex+%3D+index%3B%0D%0A++++++++++++++++self.bottomSelect.selectedIndex+%3D+index%3B%0D%0A++++++++++++%7D%0D%0A%0D%0A++++++++++++self.scrollToTop+%3D+function%28%29%7B%0D%0A++++++++++++++++document.body.scrollTop+%3D+document.documentElement.scrollTop+%3D+0%3B%0D%0A++++++++++++%7D%0D%0A++++++++%7D%0D%0A++++++++%3C%2Fscript%3E%0D%0A%3C%2Fhead%3E%0D%0A%3Cbody+onload%3D%22onload%28%29%3B%22%3E%0D%0A++++%3Cp+align+%3D+%22center%22%3E%0D%0A++++++++%3Cinput+id+%3D+%22topLastBtn%22+type%3D%22button%22+value%3D%22%E4%B8%8A%E4%B8%80%E9%A0%81%22%3E%0D%0A++++++++%3Cselect+id+%3D+%22topSelect%22%3E%0D%0A++++++++++++%3Coption%3E1%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E2%3C%2Foption%3E%0D%0A++++++++++++%3Coption+selected%3D%22true%22%3E3%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E4%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E5%3C%2Foption%3E%0D%0A++++++++%3C%2Fselect%3E%0D%0A++++++++%3Cinput+id+%3D+%22topNextBtn%22+type%3D%22button%22+value%3D%22%E4%B8%8B%E4%B8%80%E9%A0%81%22%3E%0D%0A++++%3C%2Fp%3E%0D%0A++++%3Cp+align+%3D+%22center%22%3E%3Cimg+id+%3D+%22img%22%3E%3C%2Fp%3E+%0D%0A++++%3Cp+align+%3D+%22center%22%3E%0D%0A++++++++%3Cinput+id+%3D+%22bottomLastBtn%22+type%3D%22button%22+value%3D%22%E4%B8%8A%E4%B8%80%E9%A0%81%22%3E%0D%0A++++++++%3Cselect+id+%3D+%22bottomSelect%22%3E%0D%0A++++++++++++%3Coption%3E1%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E2%3C%2Foption%3E%0D%0A++++++++++++%3Coption+selected%3D%22true%22%3E3%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E4%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E5%3C%2Foption%3E%0D%0A++++++++%3C%2Fselect%3E%0D%0A++++++++%3Cinput+id+%3D+%22bottomNextBtn%22+type%3D%22button%22+value%3D%22%E4%B8%8B%E4%B8%80%E9%A0%81%22%3E%0D%0A++++%3C%2Fp%3E%0D%0A%0D%0A%3C%2Fbody%3E%0D%0A%3C%2Fhtml%3E%0D%0A";

	// 將HTML轉成可閱讀的格式
	static {
		readerHTML = URLEncodeDecodeTool.decode(readerHTML, "utf-8");
	}

	/**
	 * 彈出警告訊息框
	 * 
	 * @param obj
	 *            要秀的訊息
	 * @param title
	 *            訊息框title
	 */
	public static void showMsgBar(Object obj, String title) {
		JOptionPane.showMessageDialog(null, obj.toString(), title,
				JOptionPane.WARNING_MESSAGE);
	}
}
