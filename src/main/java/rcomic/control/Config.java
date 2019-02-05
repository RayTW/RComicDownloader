package rcomic.control;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import javax.swing.JOptionPane;

import rcomic.utils.URLCodec;

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
	private String mVersion = "v3.0.0";
	private boolean mDebug = false;

	/** 預設下載存放資料夾名稱 */
	public String mDefaultSavePath = "漫畫下載區"; // 預設下載存放資料夾名稱

	public String mLogPath = mDefaultSavePath + "/" + "log";

	/** 開啟看漫畫時，要濾掉不顯示的資料夾名稱 */
	private String mFilterFolderNames = "(sys)|(icon)|(temp)|(log)";

	/** 漫畫最新版本公告txt的url */
	public String mNewUrl = "http://blog.xuite.net/ray00000test/blog/66855641";// 更新版本訊息的txt檔url
	/** 漫畫最新版本公告的格式 */
	public String mNewsCharset = "utf-8";

	/** 漫畫閱讀器html */
	private String mReaderHtml;
	private Font mComicListFont = new Font("Serif", Font.BOLD, 20);
	private Properties mLang;

	public Config() {
		mLang = new Properties();
		try {
			try (Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("msg_tw.properties"),
					"UTF-8")) {
				mLang.load(reader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String src = "%3Chtml%3E%0D%0A%3Chead%3E%0D%0A++++%3Cmeta+http-equiv%3D%22Content-Type%22+content%3D%22text%2Fhtml%3B+charset%3DUTF-8%22%3E%0D%0A++++%3Cscript%3E%0D%0A++++++++var+list+%3D+%22%25s%22%3B%0D%0A++++++++var+floderPath+%3D+%22%25s%22%3B%0D%0A++++++++var+imgWH+%3D+%22%25s%22%3B%0D%0A++++++++var+changePageEnable+%3D+%22%25s%22%3B%0D%0A++++++++%0D%0A++++++++function+onload%28%29%7B%0D%0A++++++++++++var+obj+%3D+new+Object%28%29%3B%0D%0A++++++++++++obj.topLastBtn+%3D+document.getElementById%28%22topLastBtn%22%29%3B%0D%0A++++++++++++obj.topSelect+%3D+document.getElementById%28%22topSelect%22%29%3B%0D%0A++++++++++++obj.topNextBtn+%3D+document.getElementById%28%22topNextBtn%22%29%3B%0D%0A++++++++++++obj.img+%3D+document.getElementById%28%22img%22%29%3B%0D%0A++++++++++++obj.bottomLastBtn+%3D+document.getElementById%28%22bottomLastBtn%22%29%3B%0D%0A++++++++++++obj.bottomSelect+%3D+document.getElementById%28%22bottomSelect%22%29%3B%0D%0A++++++++++++obj.bottomNextBtn+%3D+document.getElementById%28%22bottomNextBtn%22%29%3B%0D%0A++++++++++++obj.jpgAry+%3D+list.split%28%22%7C%22%29%3B%0D%0A++++++++++++obj.folder+%3D+floderPath%3B%0D%0A%0D%0A++++++++++++%0D%0A++++++++++++var+comicObj+%3D+new+Comiclreader%28obj%29%3B%0D%0A++++++++++++comicObj.init%28%29%3B%0D%0A++++++++%7D%0D%0A++++++++%0D%0A++++++++function+Comiclreader%28obj%29%7B%0D%0A++++++++++++var+self+%3D+this%3B%0D%0A++++++++++++%0D%0A++++++++++++self.flag+%3D+0%3B%0D%0A++++++++++++self.topLastBtn+%3D+obj.topLastBtn%3B%0D%0A++++++++++++self.topSelect+%3D+obj.topSelect%3B%0D%0A++++++++++++self.topNextBtn+%3D+obj.topNextBtn%3B%0D%0A++++++++++++self.img+%3D+obj.img%3B%0D%0A++++++++++++self.bottomLastBtn+%3D+obj.bottomLastBtn%3B%0D%0A++++++++++++self.bottomSelect+%3D+obj.bottomSelect%3B%0D%0A++++++++++++self.bottomNextBtn+%3D+obj.bottomNextBtn%3B%0D%0A++++++++++++self.jpgAry+%3D+obj.jpgAry%3B%0D%0A++++++++++++%0D%0A++++++++++++self.init+%3D+function%28%29%7B%0D%0A++++++++++++++++self.topLastBtn.onclick+%3D+self.lastPage%3B%0D%0A++++++++++++++++self.topNextBtn.onclick+%3D+self.nextPage%3B%0D%0A++++++++++++++++self.bottomLastBtn.onclick+%3D+self.lastPage%3B%0D%0A++++++++++++++++self.bottomNextBtn.onclick+%3D+self.nextPage%3B%0D%0A++++++++++++++++self.topSelect.onchange+%3D+self.selectOnChange%3B%0D%0A++++++++++++++++self.bottomSelect.onchange+%3D+self.selectOnChange%3B%0D%0A++++++++++++++++%0D%0A++++++++++++++++self.topSelect.options.length+%3D+0%3B%0D%0A++++++++++++++++self.bottomSelect.options.length+%3D+0%3B%0D%0A++++++++++++++++%0D%0A++++++++++++++++if%28imgWH+%21%3D+%220x0%22%29%7B%0D%0A++++++++++++++++++++var+wh+%3D+imgWH.split%28%22x%22%29%3B%0D%0A++++++++++++++++++++self.img.width+%3D+parseInt%28wh%5B0%5D%29%3B%0D%0A++++++++++++++++++++self.img.height+%3D+parseInt%28wh%5B1%5D%29%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++%2F%2F%E9%BB%9E%E6%93%8A%E5%9C%96%E7%89%87%E6%8F%9B%E4%B8%8B%E4%B8%80%E9%A0%81%0D%0A++++++++++++++++if%28changePageEnable+%3D%3D+%22Y%22%29%7B%0D%0A++++++++++++++++++++self.img.onclick+%3D+self.nextPage%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++%0D%0A++++++++++++++++for%28var+i+%3D+0%3B+i+%3C+self.jpgAry.length%3B+i%2B%2B%29%7B%0D%0A++++++++++++++++++++self.topSelect.options.add%28new+Option+%28self.jpgAry%5Bi%5D%2C+i%29%29%3B%0D%0A++++++++++++++++++++self.bottomSelect.options.add%28new+Option+%28self.jpgAry%5Bi%5D%2C+i%29%29%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++%0D%0A++++++++++++++++self.setSelectIndex%28self.flag%29%3B%0D%0A++++++++++++++++self.setImgSrc%28self.flag%29%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++%2F%2F%E4%B8%8A%E4%B8%80%E9%A0%81%0D%0A++++++++++++self.lastPage+%3D+function%28%29%7B%0D%0A++++++++++++++++if%28self.checkLast%28self.flag%29%29+return%3B%0D%0A++++++++++++++++self.flag--%3B%0D%0A++++++++++++++++self.setSelectIndex%28self.flag%29%3B%0D%0A++++++++++++++++self.setImgSrc%28self.flag%29%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++self.selectOnChange+%3D+function%28event%29%7B%0D%0A++++++++++++++++var+selectObj+%3D+event.currentTarget%3B%0D%0A++++++++++++++++%0D%0A++++++++++++++++self.flag+%3D+selectObj.selectedIndex%3B%0D%0A++++++++++++++++self.setSelectIndex%28self.flag%29%3B%0D%0A++++++++++++++++self.setImgSrc%28self.flag%29%3B%0D%0A++++++++++++++++self.scrollToTop%28%29%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++%2F%2F%E4%B8%8B%E4%B8%80%E9%A0%81%0D%0A++++++++++++self.nextPage+%3D+function%28%29%7B%0D%0A++++++++++++++++if%28self.checkNext%28self.flag%29%29+return%3B%0D%0A++++++++++++++++self.flag%2B%2B%3B%0D%0A++++++++++++++++self.setSelectIndex%28self.flag%29%3B%0D%0A++++++++++++++++self.setImgSrc%28self.flag%29%3B%0D%0A++++++++++++++++self.scrollToTop%28%29%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++self.setImgSrc+%3D+function%28page%29%7B%0D%0A++++++++++++++++var+imgPath+%3D+obj.folder+%2B+obj.jpgAry%5Bpage%5D%3B%0D%0A++++++++++++++++self.img.src+%3D+imgPath%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++self.checkLast+%3D+function%28page%29%7B%0D%0A++++++++++++++++if%28%28page+-+1%29+%3C+0%29%7B%0D%0A++++++++++++++++++++alert%28%22%E7%AC%AC1%E9%A0%81%E5%9B%89%22%29%3B%0D%0A++++++++++++++++++++return+true%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++return+false%3B%0D%0A++++++++++++%7D%0D%0A++++++++++++%0D%0A++++++++++++self.checkNext+%3D+function%28page%29%7B%0D%0A++++++++++++++++if%28%28page+%2B+1%29+%3E%3D+obj.jpgAry.length%29%7B%0D%0A++++++++++++++++++++alert%28%22%E6%9C%80%E5%BE%8C1%E9%A0%81%E5%9B%89%22%29%3B%0D%0A++++++++++++++++++++return+true%3B%0D%0A++++++++++++++++%7D%0D%0A++++++++++++++++return+false%3B%0D%0A++++++++++++%7D%0D%0A%0D%0A++++++++++++self.setSelectIndex+%3D+function%28index%29%7B%0D%0A++++++++++++++++self.topSelect.selectedIndex+%3D+index%3B%0D%0A++++++++++++++++self.bottomSelect.selectedIndex+%3D+index%3B%0D%0A++++++++++++%7D%0D%0A%0D%0A++++++++++++self.scrollToTop+%3D+function%28%29%7B%0D%0A++++++++++++++++document.body.scrollTop+%3D+document.documentElement.scrollTop+%3D+0%3B%0D%0A++++++++++++%7D%0D%0A++++++++%7D%0D%0A++++++++%3C%2Fscript%3E%0D%0A%3C%2Fhead%3E%0D%0A%3Cbody+onload%3D%22onload%28%29%3B%22%3E%0D%0A++++%3Cp+align+%3D+%22center%22%3E%0D%0A++++++++%3Cinput+id+%3D+%22topLastBtn%22+type%3D%22button%22+value%3D%22%E4%B8%8A%E4%B8%80%E9%A0%81%22%3E%0D%0A++++++++%3Cselect+id+%3D+%22topSelect%22%3E%0D%0A++++++++++++%3Coption%3E1%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E2%3C%2Foption%3E%0D%0A++++++++++++%3Coption+selected%3D%22true%22%3E3%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E4%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E5%3C%2Foption%3E%0D%0A++++++++%3C%2Fselect%3E%0D%0A++++++++%3Cinput+id+%3D+%22topNextBtn%22+type%3D%22button%22+value%3D%22%E4%B8%8B%E4%B8%80%E9%A0%81%22%3E%0D%0A++++%3C%2Fp%3E%0D%0A++++%3Cp+align+%3D+%22center%22%3E%3Cimg+id+%3D+%22img%22%3E%3C%2Fp%3E+%0D%0A++++%3Cp+align+%3D+%22center%22%3E%0D%0A++++++++%3Cinput+id+%3D+%22bottomLastBtn%22+type%3D%22button%22+value%3D%22%E4%B8%8A%E4%B8%80%E9%A0%81%22%3E%0D%0A++++++++%3Cselect+id+%3D+%22bottomSelect%22%3E%0D%0A++++++++++++%3Coption%3E1%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E2%3C%2Foption%3E%0D%0A++++++++++++%3Coption+selected%3D%22true%22%3E3%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E4%3C%2Foption%3E%0D%0A++++++++++++%3Coption%3E5%3C%2Foption%3E%0D%0A++++++++%3C%2Fselect%3E%0D%0A++++++++%3Cinput+id+%3D+%22bottomNextBtn%22+type%3D%22button%22+value%3D%22%E4%B8%8B%E4%B8%80%E9%A0%81%22%3E%0D%0A++++%3C%2Fp%3E%0D%0A%0D%0A%3C%2Fbody%3E%0D%0A%3C%2Fhtml%3E%0D%0A";

		// 將HTML轉成可閱讀的格式
		mReaderHtml = URLCodec.decode(src, "utf-8");
	}

	/**
	 * 彈出警告訊息框
	 * 
	 * @param obj
	 *            要秀的訊息
	 * @param title
	 *            訊息框title
	 */
	public void showMsgBar(Object obj, String title) {
		JOptionPane.showMessageDialog(null, obj.toString(), title, JOptionPane.WARNING_MESSAGE);
	}

	public Font getComicListFont() {
		return mComicListFont;
	}

	public String getLangValue(String key) {
		return mLang.getProperty(key);
	}
}
