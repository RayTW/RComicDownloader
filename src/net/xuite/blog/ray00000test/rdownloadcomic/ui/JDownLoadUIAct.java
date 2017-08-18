package net.xuite.blog.ray00000test.rdownloadcomic.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.xuite.blog.ray00000test.rdownloadcomic.service.ComicList;
import net.xuite.blog.ray00000test.rdownloadcomic.service.ComicWrapper;
import net.xuite.blog.ray00000test.rdownloadcomic.util.JDataTable;
import net.xuite.blog.ray00000test.rdownloadcomic.util.Log;

/**
 * 單本漫畫所有集數列表的畫面
 * 
 * @author Ray
 * 
 */

public class JDownLoadUIAct extends JDownLoadUIDefault {
	/** 父層參考(首頁漫畫列表) */
	private EventHandleIndex mParentObj;
	/** 處理集數網頁事件 */
	private EventHandleAct mEventHandleAct; // 處理集數網頁事件
	/** 上面工具列 */
	private JPanel mNorthPanel;

	private ComicWrapper mComic;

	public JDownLoadUIAct(EventHandleIndex parent) {
		mParentObj = parent;
		initJDownLoadUIAct();
	}

	/**
	 * 初始化整套漫畫連結資料、總集數等等..
	 * 
	 * @param comic
	 */
	public void setComic(ComicWrapper comic) {
		setTitle(comic.getName());
		mComic = comic;
		table.addMultiColumnName(new String[] { "集數", "已下載" });
		table.getColumn(1).setMaxWidth(60);
		table.addMutilRowDataArray(mComic.getActDataList());
		mEventHandleAct.setComic(comic);

		JPanel bookDataPanel = new JPanel(new GridLayout(2, 1, 10, 0));
		bookDataPanel.add(new JLabel("作者:" + comic.getAuthor()));
		bookDataPanel.add(new JLabel("更新:" + comic.getLatestUpdateDateTime()));
		mNorthPanel.add(bookDataPanel);
		
		addComidListDoubleClickEvent(table, mEventHandleAct);
	}

	/**
	 * 初始化
	 * 
	 */
	public void initJDownLoadUIAct() {
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		mEventHandleAct = new EventHandleAct();
		mEventHandleAct.setParentObj(this);

		mNorthPanel = new JPanel();

		JButton dataActListBtn = new JButton("下載選取漫畫");
		dataActListBtn.setName("ListAllJPG");
		dataActListBtn.addActionListener(mEventHandleAct);
		mNorthPanel.add(dataActListBtn);

		JButton detialBtn = new JButton("漫畫簡介");
		detialBtn.setName("detial");
		detialBtn.addActionListener(mEventHandleAct);
		mNorthPanel.add(detialBtn);

		JButton addLoveBtn = new JButton("加到我的最愛");
		addLoveBtn.setName("addLove");
		addLoveBtn.addActionListener(mEventHandleAct);
		mNorthPanel.add(addLoveBtn);

		table.setRowHeight(40);
		table.setFont(new Font("Serif", Font.BOLD, 20));

		c.add(mNorthPanel, BorderLayout.NORTH);
		c.add(table.getJScrollPaneJTable(), BorderLayout.CENTER);

		// 設定視窗
		setSize(600, 300);
		setLocation(300, 200);

		// 按下視窗關閉鈕事件處理
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
	}

	/**
	 * 新增漫畫資料到我的最愛
	 * 
	 * @param comicID
	 * @param comicName
	 */
	public boolean addToLove(String comicID, String comicName) {
		return mParentObj.addComicToLove(comicID, comicName);
	}

	/**
	 * 設定狀態列文字
	 * 
	 * @param txt
	 */
	public void setStateText(String txt) {
		mParentObj.setStateText(txt);
	}

	/**
	 * 釋放UI memory
	 */
	public void close() {
		dispose();
	}

	/**
	 * 設定右側下載漫畫狀態列參考
	 * 
	 * @param table
	 */
	public void setDataTableList(TableList table) {
		mEventHandleAct.setDataTableList(table);
	}

	public boolean isDownloadedList() {
		return (table.getDataCount() > 0);
	}
	
	/**
	 * 在table上增加雙擊開啟動畫集數列表功能
	 * 
	 * @param jtable
	 * @param handle
	 */
	private void addComidListDoubleClickEvent(final JDataTable jtable,
			final EventHandleAct handle) {
		jtable.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {// 雙擊漫畫後，開始下戴漫畫
					int row = jtable.getJTable().rowAtPoint(e.getPoint());
					handle.listAllJPG(mComic, new int[]{row});
				}
			}
		});
	}
}
