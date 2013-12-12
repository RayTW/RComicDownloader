package JDownLoadComic.parseHtml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import JDownLoadComic.Config;
import JDownLoadComic.util.HttpReader;

/**
 * 到xuite網站讀取漫畫列表
 * 
 * @author Ray
 * 
 */

public class LoadXuiteData {
	public String url;
	public String data;
	public String cookie;
	public String referer;
	public boolean post;

	public LoadXuiteData() {
		initLoadXuiteData();
	}

	public void initLoadXuiteData() {
		url = "";
		data = "";
		cookie = null;
		referer = null;
		post = false;
	}

	/**
	 * 讀取遠端備份的首頁漫畫列表
	 * 
	 * @return
	 */
	public boolean loadData() {
		HttpReader lf = new HttpReader();

		try {
			InputStream inputstream = lf.openURL(url, data, cookie, referer,
					post);
			String setCokie = (lf.getHeaderField("Set-Cookie").toString())
					.split("[;]")[0];// 取得要設定給鎖密碼文章的cookie內容
			String locationUrl = "";

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					inputstream, "utf8"));
			String line;
			while ((line = rd.readLine()) != null) {
				if (line != null) {
					int beginIndex = line.indexOf("='/") + "='/".length();
					int endIndex = line.indexOf("';</");
					locationUrl = referer
							+ line.substring(beginIndex, endIndex);
				}
			}
			// System.out.println("locationUrl===>" + locationUrl);

			rd.close();
			// System.out.println("setCokie=headerFields=>" + lf.getHeader());

			// 2011-06-01
			// 修正從日誌讀取漫畫列表，使用arraylist讀取會因html裡的資料過長而被截斷，改成用stringbuffer串html回來解析
			// ArrayList data = lf.getHTMLtoArrayList(locationUrl, "utf8",
			// setCokie);//舊寫法
			String tmp = lf.getHTML(locationUrl, "utf8", setCokie);// 修正後寫法

			String startTag = "[comicdata]";
			String endTag = "[/comicdata]";
			// String tmp = (String)data.get(i);
			// System.out.println("comicdata==>" + tmp);
			if (tmp.indexOf(startTag) != -1) {
				String comicdata = tmp.substring(
						tmp.indexOf(startTag) + startTag.length(),
						tmp.indexOf(endTag)).replaceAll("<br />", "\r\n");
				// System.out.println("comicdata==>" + tmp.indexOf(startTag));
				JDownLoadComic.util.WriteFile wf = new JDownLoadComic.util.WriteFile();
				wf.writeText_UTF8(comicdata, Config.indexList);
				// System.out.println("comicdata=====>" + comicdata);
			}
			/*
			 * for(int i = 0; i < data.size(); i++){ String startTag =
			 * "[comicdata]"; String endTag = "[/comicdata]"; String tmp =
			 * (String)data.get(i); System.out.println("comicdata==>" + tmp);
			 * if(tmp.indexOf(startTag) != -1){ String comicdata =
			 * tmp.substring(tmp.indexOf(startTag) + startTag.length(),
			 * tmp.indexOf(endTag)).replaceAll("<br />", "\r\n");
			 * System.out.println("comicdata==>" + tmp.indexOf(startTag));
			 * JDownLoadComic.util.WriteFile wf = new
			 * JDownLoadComic.util.WriteFile(); wf.writeText_UTF8(comicdata,
			 * Config.indexList); System.out.println("comicdata=====>" +
			 * comicdata); } }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			Config.showMsgBar("loadData 時發生例外[" + e.toString() + "]", "錯誤");
			return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動產生方法 Stub

	}

}
