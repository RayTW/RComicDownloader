package JDownLoadComic;

import javax.swing.JFrame;

import JDownLoadComic.util.JDataTable;

/**
 * 將首頁漫畫列表 與 集數列表 提出default，以便以後要增加共用功能
 * 
 * @author Ray
 * 
 */
public class JDownLoadUI_Default extends JFrame {
	protected JDataTable table; // 秀首頁所有漫畫列表

	public JDownLoadUI_Default() {
		initJDownLoadUI_Default();
	}

	public void initJDownLoadUI_Default() {
		table = new JDataTable(false);// 設定建立的表格不可以編輯
	}

	/**
	 * 取出列表上被選取的欄位 位置 0,1,2,3...
	 * 
	 * @param column
	 *            欄位置
	 * @return
	 */
	public int[] getSelectRowIndex() {
		int[] rows = table.getSelectedRows();
		return rows;
	}

}
