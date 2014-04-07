package JDownLoadComic.util;

/**
 * 建立像excel的表格資料表
 * 2010/04/23
 * @author Ray
 */

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.Font;
import java.util.Vector;

public class JDataTable {
	/** 資料表內容(二維陣列) */
	protected Vector tmpTableData;
	/** 資料表的欄位名稱 */
	protected Vector tableFieldName;
	/** 操作內容 */
	protected DefaultTableModel tmodel;
	/** JTable本身 */
	protected JTable table; // 建立JTable

	public JDataTable() {
		initJDataTalbe(true);
	}

	/**
	 * 建立此表格時，所有的格子設定是否可以編輯
	 * 
	 * @param isEdit
	 *            true 可編輯 | false 不可編輯
	 */
	public JDataTable(final boolean isEdit) {
		initJDataTalbe(isEdit);
	}

	/**
	 * 初始化
	 * 
	 * @param isEdit
	 */
	public void initJDataTalbe(final boolean isEdit) {
		tableFieldName = new Vector();
		tmpTableData = new Vector();
		tmodel = new DefaultTableModel(tmpTableData, tableFieldName) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return isEdit;
			}
		};
		table = new JTable(tmodel); // 建立JTable
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN );

		// jtable某筆資料被選取時事件
		/*
		 * table.getSelectionModel().addListSelectionListener(new
		 * ListSelectionListener(){
		 * 
		 * public void valueChanged(ListSelectionEvent e) { if
		 * (table.getSelectedRow()>=0) { } System.out.println("選擇了jtable"); }
		 * });
		 */
	}

	/**
	 * 設定每筆資料高度
	 * 
	 * @param h
	 */
	public void setRowHeight(int h) {
		table.setRowHeight(h);
	}

	/**
	 * 設定顯示的字體
	 * 
	 * @param font
	 */
	public void setFont(Font font) {
		table.setFont(font);
	}

	/**
	 * 新增欄位名稱
	 * 
	 * @param ColumnName
	 */
	public void addColumnName(String ColumnName) {
		tmodel.addColumn(ColumnName);
	}

	/**
	 * 一次新增多個欄位名稱
	 * 
	 * @param ColumnNameArray
	 */
	public void addMultiColumnName(String[] ColumnNameArray) {
		for (int i = 0; i < ColumnNameArray.length; i++) {
			addColumnName(ColumnNameArray[i]);
		}
	}

	/**
	 * 新增一筆資料,資料將會新增在最下面一行 資料欄位少於設定值時，自動補空格，超過設定值欄位數的資料將遺失
	 * 
	 * @param data
	 */
	public void addRowData(Vector data) {
		tmodel.addRow(data);
	}

	/**
	 * 新增一筆資料,資料將會新增在最下面一行 資料欄位少於設定值時，自動補空格，超過設定值欄位數的資料將遺失
	 * 
	 * @param data
	 */
	public void addRowAryData(Object[] data) {
		tmodel.addRow(data);
	}

	/**
	 * 新增資料到第row筆下面
	 * 
	 * @param row
	 * @param data
	 */
	public void insertRowData(int row, Vector data) {
		tmodel.insertRow(row, data);
	}

	/**
	 * 新增資料到第row筆下面
	 * 
	 * @param row
	 * @param data
	 */
	public void insertRowAryData(int row, Object[] data) {
		tmodel.insertRow(row, data);
	}

	/**
	 * 一次將多筆存在vector的資料(每筆也用vector存放的)新增到資料列最下面
	 * 
	 * @param dataV
	 *            存放多筆資料(vector)
	 */
	public void addMultiRowDataLast(Vector dataV) {
		Vector tmpData = (Vector) dataV.clone();
		for (int i = 0; i < tmpData.size(); i++) {
			addRowData((Vector) tmpData.get(i));
		}
	}

	/**
	 * 一次將二維陣列的資料新增到資料表最下面
	 * 
	 * @param dataArray
	 *            二維資料表s
	 */
	public void addMutilRowDataArray(Object[][] dataArray) {
		for (int i = 0; i < dataArray.length; i++) {
			addRowAryData(dataArray[i]);
		}
	}

	/**
	 * 移除第row列資料筆
	 * 
	 * @param row
	 */
	public void removeRow(int row) {
		tmodel.removeRow(row);
	}

	public void removeAll() {
		for (int i = tmodel.getRowCount() - 1; i >= 0; i--) {
			tmodel.removeRow(i);
		}
	}

	/**
	 * 移除所點選的資料筆，可移除多筆資料且資料筆不連續也可以刪
	 */
	public void removeSelectedRows() {
		int[] row = table.getSelectedRows();
		for (int r = row.length - 1; r >= 0; r--) {
			removeRow(row[r]);
		}
	}

	/**
	 * 取得選取的筆數位置(被選取的列會反白)
	 * 
	 * @return
	 */
	public int[] getSelectedRows() {
		return table.getSelectedRows();
	}

	/**
	 * 開啟(關閉)換欄位置功能,預設為開啟
	 * 
	 * @param b
	 *            true 開啟 | false 關閉
	 */
	public void setReorderingAllowed(boolean b) {
		table.getTableHeader().setReorderingAllowed(b);
	}

	/**
	 * 顯示(隱藏)欄位格的隔線,預設為顯示
	 * 
	 * @param b
	 *            true 顯示 | false 隱藏
	 */
	public void setShowLine(boolean b) {
		table.setShowHorizontalLines(b);
	}

	/**
	 * 設定欄位格寬度
	 * 
	 * @param index
	 *            欄位格位置(從0開始)
	 * @param w
	 *            欄位寬度
	 */
	public void setColumnWidth(int index, int w) {
		getColumn(index).setPreferredWidth(w);
	}

	/**
	 * 取得指定列物件
	 * 
	 * @param index
	 * @return
	 */
	public TableColumn getColumn(int index) {
		return table.getColumnModel().getColumn(index);
	}

	/**
	 * 取得目前資料筆數
	 * 
	 * @return
	 */
	public int getDataCount() {
		return tmpTableData.size();
	}

	/**
	 * 取得[第r列,第c行]資料，與二維陣列取法同，若無資料則回傳null
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public Object getValutAt(int row, int column) {
		Object obj = tmodel.getValueAt(row, column);
		return obj;
	}

	/**
	 * 取得此資料的JTable
	 * 
	 * @return
	 */
	public JTable getJTable() {
		return table;
	}

	/**
	 * 取得實際操作table的物件
	 * 
	 * @return
	 */
	public DefaultTableModel getDefaultTableModel() {
		return tmodel;
	}

	public Class getTableFieldNameClass() {
		return tableFieldName.getClass();
	}

	/**
	 * 取得加入滾輪軸的JTable
	 * 
	 * @return
	 */
	public JScrollPane getJScrollPaneJTable() {
		return new JScrollPane(table);
	}
}
