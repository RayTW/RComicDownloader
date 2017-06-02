package net.xuite.blog.ray00000test.rdownloadcomic.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.xuite.blog.ray00000test.rdownloadcomic.parseHtml.LoadComicData;
import net.xuite.blog.ray00000test.rdownloadcomic.service.Config;
import net.xuite.blog.ray00000test.rdownloadcomic.service.FolderManager;
import net.xuite.blog.ray00000test.rdownloadcomic.service.RComicDownloader;
import net.xuite.blog.ray00000test.rdownloadcomic.util.JDataTable;
import net.xuite.blog.ray00000test.rdownloadcomic.util.Log;
import net.xuite.blog.ray00000test.rdownloadcomic.util.NewComicTableCellRenderer;

/**
 * 漫畫下載列表首頁
 * 
 * @author Ray 名稱:漫畫下載 日期:
 */

public class JDownLoadUI_index extends JDownLoadUI_Default {
	static {
		Log.setGlobalDebug(Config.DEBUG);
	}
	private static Log sLog = Log.newInstance(true);

	/** 處理首頁事件 */
	private EventHandle_index eventHandleIndex; // 處理首頁事件
	/** 搜尋輸入框 */
	private JTextField findField;
	/** 下方訊息框 */
	private StateMessage stateTextArea;
	/** 放分頁 */
	public JTabbedPane jt;
	/** 秀首頁所有漫畫列表 */
	protected JDataTable tableLove;
	/** 最新漫畫 */
	protected JDataTable tableNew;
	/** 下載中漫畫佇列 */
	private TableList downLoadTable;
	/** 版本更新公告 */
	private VersionNews news;

	private JScrollPane downLoadTableScroll;
	private JPanel centerPanel;

	public JDownLoadUI_index() {
		initJdownLoadUI_index();
	}

	/**
	 * 初始化，產生首頁所有使用到的物件
	 * 
	 */
	public void initJdownLoadUI_index() {
		String title = String.format(Config.indexName, Config.version);
		setTitle(title);
		Container c = getContentPane();

		eventHandleIndex = new EventHandle_index();
		eventHandleIndex.setParentObj(this);

		LoadComicData loadData = new LoadComicData(RComicDownloader.get()
				.getDB().getComicList());
		table.addMultiColumnName(new String[] { "編號", "漫畫名稱" });
		table.setReorderingAllowed(false);// 鎖住換欄位位置功能，會影嚮雙擊開列表功能
		table.addMutilRowDataArray(loadData.getIndexData());
		table.setRowHeight(40);
		table.getColumn(0).setMaxWidth(60);
		table.setFont(new Font("Serif", Font.BOLD, 20));
		table.getJTable().setToolTipText(Config.please_double_click);
		addComidListDoubleClickEvent(table, eventHandleIndex);
		eventHandleIndex.addLoadDataObj(0, loadData);

		LoadComicData loadDataLove = new LoadComicData(RComicDownloader.get()
				.getDB().getLoveComicList());
		tableLove = new JDataTable(false);
		tableLove.addMultiColumnName(new String[] { "編號", "漫畫名稱" });
		tableLove.addMutilRowDataArray(loadDataLove.getIndexData());
		tableLove.setRowHeight(40);
		tableLove.getColumn(0).setMaxWidth(60);
		tableLove.setFont(new Font("Serif", Font.BOLD, 20));
		tableLove.getJTable().setToolTipText(Config.please_double_click);
		addComidListDoubleClickEvent(tableLove, eventHandleIndex);
		eventHandleIndex.addLoadDataObj(1, loadDataLove);

		tableNew = new JDataTable(false);
		tableNew.addMultiColumnName(new String[] { "編號", "漫畫名稱[集數]" });
		// tableNew.addMutilRowDataArray(loadDataLove.getIndexData());
		tableNew.setRowHeight(40);
		tableNew.getColumn(0).setMaxWidth(60);
		tableNew.setFont(new Font("Serif", Font.BOLD, 20));
		tableNew.getJTable().setToolTipText(Config.please_double_click);
		addComidListDoubleClickEvent(tableNew, eventHandleIndex);

		JPanel northPanel = new JPanel(new GridLayout(0, 5, 10, 0));
		c.add(northPanel, BorderLayout.NORTH);
		// final JButton dataActListBtn = new JButton("列出漫畫集數");
		// dataActListBtn.setName("ListAllAct");
		// dataActListBtn.addActionListener(eventHandleIndex);
		// northPanel.add(dataActListBtn);

		JPanel findPanel = new JPanel(new BorderLayout());
		findField = new JTextField();
		findField.setToolTipText("請輸入漫畫名稱後按enter");
		findPanel.add(new JLabel("搜尋"), BorderLayout.WEST);
		findPanel.add(findField, BorderLayout.CENTER);
		northPanel.add(findPanel);

		// final JButton updateBtn = new JButton("更新漫畫列表");
		// updateBtn.setName("update");
		// updateBtn.addActionListener(eventHandleIndex);
		// northPanel.add(updateBtn);

		final JButton loveBtn = new JButton("加到我的最愛");
		loveBtn.setName("love");
		loveBtn.addActionListener(eventHandleIndex);
		northPanel.add(loveBtn);

		final JButton deleteBtn = new JButton("從最愛移除");
		deleteBtn.setName("delete");
		deleteBtn.addActionListener(eventHandleIndex);
		deleteBtn.setEnabled(false);
		northPanel.add(deleteBtn);

		final JButton readerBtn = new JButton("看漫畫囉");
		readerBtn.setName("reader");
		readerBtn.addActionListener(eventHandleIndex);
		northPanel.add(readerBtn);

		final JButton exportPDFBtn = new JButton("匯出PDF");
		exportPDFBtn.setName("exportPDF");
		exportPDFBtn.addActionListener(eventHandleIndex);
		northPanel.add(exportPDFBtn);

		centerPanel = new JPanel(new GridLayout(0, 2));
		news = new VersionNews();
		// 設定頁
		final DownLoadSetting downloadsetting = new DownLoadSetting(this);
		jt = new JTabbedPane();
		jt.add("漫畫列表", table.getJScrollPaneJTable());
		jt.add("我的最愛", tableLove.getJScrollPaneJTable());
		jt.add("最新漫畫", tableNew.getJScrollPaneJTable());
		jt.add("設定列表", downloadsetting);
		jt.add("更新公告", news);
		news.refrush(Config.version);

		// 切換到我的最愛時不能使用更新與加到我的最愛功能
		jt.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = jt.getSelectedIndex();
				// updateBtn.setEnabled(index == 0);
				loveBtn.setEnabled(index == 0);
				deleteBtn.setEnabled(index == 1);
				// dataActListBtn.setEnabled(index != 3 && index != 4);
				findField.setEnabled(index != 3 && index != 4);

				switch (index) {
				case 1:
					// 標示我的最愛有哪幾部漫畫有更新 與 更新的漫畫有哪幾步在我的最愛
					// 標示我的最愛裡的漫畫有更新,將更新的漫畫編號讀取出來檢查，有出現在我的最愛就修改table cell color
					markTableCell(tableNew, tableLove);
					break;
				case 2:
					// 在最新漫畫上標示在我的最愛裡的漫畫
					markTableCell(tableLove, tableNew);
					break;
				case 3:// 切回設定頁時，重新讀取設定值
					downloadsetting.reload();
					break;
				default:
					findField.requestFocus();
				}
			}
		});

		centerPanel.add(jt);

		c.add(centerPanel, BorderLayout.CENTER);

		resetTableList();

		stateTextArea = new StateMessage();
		stateTextArea.setFocusable(false);
		JPanel txtPanel = new JPanel(new BorderLayout());
		txtPanel.add(new JScrollPane(this.stateTextArea));
		c.add(txtPanel, "South");

		// 取得上次關閉APP時的視窗大小來設定視窗
		setBounds(RComicDownloader.get().getDB().indexBounds);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				new Thread() {
					@Override
					public void run() {
						try {
							RComicDownloader.get().getDB().indexBounds = JDownLoadUI_index.this
									.getBounds();
							setVisible(false);
							RComicDownloader.get().getDB().save();
							FolderManager.deleteFolder(Config.tempFolderPath);
						} catch (Exception e) {
							e.printStackTrace();
						}
						sLog.println("2 windowClosing");
						System.exit(0);
					}
				}.start();
			}
		});

		findField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String findText = findField.getText();
					eventHandleIndex.findComic(findText);
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
	}

	/**
	 * 取出列表上被選取的欄位 位置 0,1,2,3...
	 * 
	 * @param column
	 *            欄位置
	 * @return
	 */
	@Override
	public int[] getSelectRowIndex() {
		switch (jt.getSelectedIndex()) {
		case 0:
			return table.getSelectedRows();
		case 1:
			return tableLove.getSelectedRows();
		case 2:
			return tableNew.getSelectedRows();
		}
		return null;
	}

	/**
	 * 取得搜尋漫畫輸入框 要搜尋的漫畫名稱
	 */
	public String getFindFieldText() {
		return findField.getText();
	}

	/**
	 * 設定狀態列上的文字
	 * 
	 * @param txt
	 */
	public void setStateText(String text) {
		stateTextArea.addText(text);
		stateTextArea.refresh();
	}

	/**
	 * 新增更新的漫畫到畫面列表上
	 * 
	 * @param data
	 */
	public void addTable(String[][] data) {
		table.addMutilRowDataArray(data);
	}

	/**
	 * 取得目前畫面正在view的列表 0:所有漫畫列表, 1:我的最愛列表
	 * 
	 * @return
	 */
	public int getSelectList() {
		return jt.getSelectedIndex();
	}

	public void updateComic() {
		eventHandleIndex.updateComic(tableNew, false);
	}

	/**
	 * 改變下載佇列上限個數
	 */
	public void resetTableList() {
		if (downLoadTableScroll != null) {
			centerPanel.remove(downLoadTableScroll);
		}
		downLoadTable = new TableList(RComicDownloader.get().getDB()
				.getDownCountLimit());
		downLoadTable.setParentObj(this);
		eventHandleIndex.setDataTableList(downLoadTable);
		downLoadTableScroll = downLoadTable.getJScrollPaneJTable();
		centerPanel.add(downLoadTableScroll);
	}

	/**
	 * 取得目前下載中的漫畫本數
	 * 
	 * @return
	 */
	public int getCurrentDownLoadSize() {
		int cnt = 0;
		if (downLoadTable != null) {
			cnt = downLoadTable.getCurrentDownLoadSize();
		}
		return cnt;
	}

	/**
	 * 當t1的第0列value有與t2的相符時，t2就會顯示指定的顏色
	 */
	public void markTableCell(JDataTable t1, JDataTable t2) {
		NewComicTableCellRenderer render = new NewComicTableCellRenderer();
		render.setForegroundColor(RComicDownloader.get().getDB().foregroundColor);
		render.setBackgroundColor(RComicDownloader.get().getDB().backgroundColor);

		for (int i = t1.getDataCount() - 1; i >= 0; i--) {
			String comicNum = t1.getValutAt(i, 0).toString();
			render.put(comicNum, null);
		}
		t2.getJTable().setDefaultRenderer(Object.class, render);
	}

	public StateMessage getStateMessage() {
		return stateTextArea;
	}

	/**
	 * 在table上增加雙擊開啟動畫集數列表功能
	 * 
	 * @param jtable
	 * @param handle
	 */
	public void addComidListDoubleClickEvent(final JDataTable jtable,
			final EventHandle_index handle) {
		jtable.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {// 雙擊漫畫後，開始讀取漫畫集數列表
					int row = jtable.getJTable().rowAtPoint(e.getPoint());

					String comicNumber = jtable.getJTable().getValueAt(row, 0)
							.toString();
					// String comicName = jtable.getJTable().getValueAt(row, 1)
					// .toString();

					handle.creadActListJFrame(comicNumber);
				}
			}
		});
	}

	/**
	 * 漫畫下載程式 啟動點,啟動時會連向遠端檢查是本地端是否有漫畫編號文字檔，若沒有會重load並存檔
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RComicDownloader.get().preprogress();

		// 建立動畫程式首頁
		JDownLoadUI_index download = new JDownLoadUI_index();
		download.setVisible(true);
		download.updateComic();
	}

}
