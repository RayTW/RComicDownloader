package net.xuite.blog.ray00000test.rdownloadcomic.parseHtml;

import java.util.ArrayList;

import net.xuite.blog.ray00000test.rdownloadcomic.service.RComicDownloader;
import net.xuite.blog.ray00000test.rdownloadcomic.util.HttpReader;
import net.xuite.blog.ray00000test.rdownloadcomic.util.JDataTable;

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
		RComicDownloader.get().getDB().clearNewComicList();
		indexData = new String[table.getDataCount()][];
		for (int i = 0; i < table.getDataCount(); i++) {
			indexData[i] = new String[] { (String) table.getValutAt(i, 0),
					(String) table.getValutAt(i, 1) };
			RComicDownloader.get().getDB().addNewComicList(indexData[i]);
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
