package rcomic.utils.ui;

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
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ray
 *
 */
public class JDataTable<E> {
	/** 資料表內容(二維陣列) */
	protected List<String> mTableData;
	/** 資料表的欄位名稱 */
	protected List<String> mTableFieldName;
	/** 操作內容 */
	protected RTableModel<List<E>> mModel;
	/** JTable本身 */
	protected JTable mTable; // 建立JTable

	public JDataTable() {
		this(true);
	}

	/**
	 * 建立此表格時，所有的格子設定是否可以編輯
	 * 
	 * @param isEdit
	 *            true 可編輯 | false 不可編輯
	 */
	public JDataTable(boolean isEdit) {
		mTableFieldName = new ArrayList<String>();
		mTableData = new ArrayList<String>();
		mModel = new RTableModel(mTableData, mTableFieldName) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return isEdit;
			}
		};
		mTable = new JTable(mModel); // 建立JTable
	}

	/**
	 * 設定每筆資料高度
	 * 
	 * @param h
	 */
	public void setRowHeight(int h) {
		mTable.setRowHeight(h);
	}

	/**
	 * 設定顯示的字體
	 * 
	 * @param font
	 */
	public void setFont(Font font) {
		mTable.setFont(font);
	}

	/**
	 * 新增欄位名稱
	 * 
	 * @param ColumnName
	 */
	public void addColumnName(String ColumnName) {
		mModel.addColumn(ColumnName);
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
	public void addRowData(List<E> data) {
		mModel.addRow(data);
	}

	/**
	 * 新增一筆資料,資料將會新增在最下面一行 資料欄位少於設定值時，自動補空格，超過設定值欄位數的資料將遺失
	 * 
	 * @param data
	 */
	public void addRowData(Object[] data) {
		mModel.addRow(data);
	}

	/**
	 * 新增資料到第row筆下面
	 * 
	 * @param row
	 * @param data
	 */
	public void insertRowAryData(int row, Object[] data) {
		mModel.insertRow(row, data);
	}

	/**
	 * 一次將多筆存在vector的資料(每筆也用vector存放的)新增到資料列最下面
	 * 
	 * @param dataV
	 *            存放多筆資料(vector)
	 */
	public void addMultiRowDataLast(List<List<E>> data) {
		data.forEach(this::addRowData);
	}

	/**
	 * 一次將二維陣列的資料新增到資料表最下面
	 * 
	 * @param dataArray
	 *            二維資料表s
	 */
	public void addMutilRowData(List<List<E>> mutilRowData) {
		mutilRowData.forEach(this::addRowData);
	}

	/**
	 * 移除第row列資料筆
	 * 
	 * @param row
	 */
	public void removeRow(int row) {
		mModel.removeRow(row);
	}

	public void removeAll() {
		for (int i = mModel.getRowCount() - 1; i >= 0; i--) {
			mModel.removeRow(i);
		}
	}

	/**
	 * 移除所點選的資料筆，可移除多筆資料且資料筆不連續也可以刪
	 */
	public void removeSelectedRows() {
		int[] row = mTable.getSelectedRows();
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
		return mTable.getSelectedRows();
	}

	/**
	 * 開啟(關閉)換欄位置功能,預設為開啟
	 * 
	 * @param b
	 *            true 開啟 | false 關閉
	 */
	public void setReorderingAllowed(boolean b) {
		mTable.getTableHeader().setReorderingAllowed(b);
	}

	/**
	 * 顯示(隱藏)欄位格的隔線,預設為顯示
	 * 
	 * @param b
	 *            true 顯示 | false 隱藏
	 */
	public void setShowLine(boolean b) {
		mTable.setShowHorizontalLines(b);
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
		return mTable.getColumnModel().getColumn(index);
	}

	/**
	 * 取得目前資料筆數
	 * 
	 * @return
	 */
	public int getDataCount() {
		return mTableData.size();
	}

	/**
	 * 取得[第r列,第c行]資料，與二維陣列取法同，若無資料則回傳null
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public Object getValutAt(int row, int column) {
		Object obj = mModel.getValueAt(row, column);
		return obj;
	}

	/**
	 * 取得此資料的JTable
	 * 
	 * @return
	 */
	public JTable getJTable() {
		return mTable;
	}

	/**
	 * 取得實際操作table的物件
	 * 
	 * @return
	 */
	public DefaultTableModel getDefaultTableModel() {
		return mModel;
	}

	/**
	 * 取得加入滾輪軸的JTable
	 * 
	 * @return
	 */
	public JScrollPane toJScrollPane() {
		return new JScrollPane(mTable);
	}
}
