package net.xuite.blog.ray00000test.rdownloadcomic.util;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 顯示更新的最新漫畫有在我的最愛裡的漫畫 標示其他顏色
 * 當cell裡的value與map裡的key相同時，該cell的文字(foregroundColor)與背景顏色
 * (backgroundColor)會以設定值進行變動
 * 
 * @author Ray
 * 
 */
public class NewComicTableCellRenderer extends DefaultTableCellRenderer {
	// 漫畫編號,漫畫名稱
	private HashMap<String, String> map = new HashMap<String, String>();
	private Color foregroundColor = Color.BLUE;// 文字顏色
	private Color backgroundColor = Color.WHITE;// 背景顏色

	public NewComicTableCellRenderer() {
	}

	public void put(String comicNum, String comicName) {
		map.put(comicNum, comicName);
	}

	public String get(String comicNum) {
		return map.get(comicNum);
	}

	public void clear() {
		map.clear();
	}

	/**
	 * 設定 cell 文字顏色
	 * 
	 * @param c
	 */
	public void setForegroundColor(Color c) {
		foregroundColor = c;
	}

	// 設定 cell 背景顏色
	public void setBackgroundColor(Color c) {
		backgroundColor = c;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		// 沒有被選取才修改顏色，否則會連選取之後的反白效果都改掉
		if (!isSelected) {
			if (map.containsKey(value)) {
				c.setForeground(foregroundColor);
				c.setBackground(backgroundColor);
			} else {
				c.setForeground(Color.BLACK);
				c.setBackground(Color.WHITE);
			}
		}
		return c;
	}
}
