package JDownLoadComic.parseHtml;

import java.util.ArrayList;

import JDownLoadComic.Config;
import JDownLoadComic.util.JDataTable;
import JDownLoadComic.util.HttpReader;

/**
 * 解析目前最新漫畫
 * 
 * @author Ray
 * 
 */
public class LoadNewComicData extends LoadComicData {

	public LoadNewComicData() {
		loadHtml = new HttpReader();
	}

	public void loadNew(final JDataTable table,
			final ArrayList<NewComic> newComicAry) {

		for (int i = 0; i < newComicAry.size(); i++) {
			NewComic obj = newComicAry.get(i);

			table.addRowAryData(new String[] { obj.id,
					obj.name + "[" + obj.act + "]" });
		}
		Config.db.clearNewComicList();
		indexData = new String[table.getDataCount()][];
		for (int i = 0; i < table.getDataCount(); i++) {
			indexData[i] = new String[] { (String) table.getValutAt(i, 0),
					(String) table.getValutAt(i, 1) };
			Config.db.addNewComicList(indexData[i]);
		}
	}

	/**
	 * 取出第n筆漫畫的名稱
	 * 
	 * @param rowIndex
	 *            漫畫的idnex
	 * @return
	 */
	@Override
	public String getCartoonName(int rowIndex) {
		// 取最新漫畫名稱時，去除最後的最新集數
		String txt = indexData[rowIndex][1];
		return txt.substring(0, txt.lastIndexOf("["));
	}
}
