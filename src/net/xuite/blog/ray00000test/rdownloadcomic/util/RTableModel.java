package net.xuite.blog.ray00000test.rdownloadcomic.util;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class RTableModel extends DefaultTableModel{

	public RTableModel(Vector<String> data, Vector<String> columnNames) {
		super(data, columnNames);
	}

	public RTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

}
