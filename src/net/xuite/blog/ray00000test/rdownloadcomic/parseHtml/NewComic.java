package net.xuite.blog.ray00000test.rdownloadcomic.parseHtml;

import net.xuite.blog.ray00000test.rdownloadcomic.util.ChineseWord;

/**
 * 解析最新漫畫資料，並轉換為物件
 * 
 * @author Ray
 * 
 */
public class NewComic {
	public final static String findTagStart = "<td height=\"30\" nowrap> · <a href='/html/";
	public final static String findTagEnd = ".html' >";
	public final static String nameTagEnd = "  [ 漫畫";
	public final static String actTagStart = nameTagEnd;
	public final static String actTagEnd = " ]";

	public String id;// 漫畫編號
	public String name;// 漫畫名稱
	public String act;// 最新集數
	public String date;// 更新日期(未解析)

	public NewComic() {
		init();
	}

	public void init() {
		id = "";
		name = "";
		act = "";
		date = "";
	}

	/**
	 * 檢查是否可以解析成物件的開頭tag，true:可解析，true:不可解析
	 * 
	 * @param html
	 * @return
	 */
	public static boolean hasTag(String html) {
		int start = html.indexOf(findTagStart);
		return (start != -1);
	}

	/**
	 * 解析單筆最新漫畫資訊
	 * 
	 * @param html
	 * @param nextHtml
	 * @return
	 */
	public static NewComic parser(String html, String nextHtml) {
		int start = html.indexOf(findTagStart);

		if (start != -1) {
			NewComic comicObj = new NewComic();

			// 解析漫畫編號
			int end = html.indexOf(findTagEnd);
			comicObj.id = html.substring(start + findTagStart.length(), end);

			// 解析漫畫名稱
			String comicName = nextHtml.substring(0,
					nextHtml.indexOf(nameTagEnd));
			comicObj.name = ChineseWord.unicodeToChineseAll(comicName);

			// 解析最新集數
			String act = nextHtml.substring(nextHtml.indexOf(actTagStart)
					+ actTagStart.length(), nextHtml.length());
			act = act.substring(0, act.indexOf(actTagEnd));

			comicObj.act = act;

			return comicObj;
		}
		return null;
	}

	@Override
	public String toString() {
		String str = "漫畫編號[" + id + "],漫畫名稱[" + name + "],最新集數[" + act
				+ "],更新日期[" + date + "]";
		return str;
	}
}
