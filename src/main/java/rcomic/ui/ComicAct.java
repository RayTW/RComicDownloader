package rcomic.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import rcomic.control.ComicWrapper;
import rcomic.utils.ui.JDataTable;

/**
 * 單本漫畫所有集數列表的畫面
 * 
 * @author Ray
 * 
 */

public class ComicAct extends JDialog {
	private static final long serialVersionUID = 5167725310966938209L;

	private JDataTable<String> mTable; // 秀首頁所有漫畫列表
	/** 上面工具列 */
	private JPanel mNorthPanel;

	private ComicWrapper mComic;

	public ComicAct() {
		super();
		initialize();
	}

	/**
	 * 初始化
	 * 
	 */
	public void initialize() {
		Container c = getContentPane();
		c.setLayout(new BorderLayout());

		mNorthPanel = new JPanel();

		JButton dataActListBtn = new JButton("下載選取漫畫");
		dataActListBtn.setName("ListAllJPG");
		dataActListBtn.addActionListener(mActionListener);
		mNorthPanel.add(dataActListBtn);

		JButton detialBtn = new JButton("漫畫簡介");
		detialBtn.setName("detial");
		detialBtn.addActionListener(mActionListener);
		mNorthPanel.add(detialBtn);

		JButton addLoveBtn = new JButton("加到我的最愛");
		addLoveBtn.setName("addLove");
		addLoveBtn.addActionListener(mActionListener);
		mNorthPanel.add(addLoveBtn);

		mTable.setRowHeight(40);
		mTable.setFont(new Font("Serif", Font.BOLD, 20));

		c.add(mNorthPanel, BorderLayout.NORTH);
		c.add(mTable.toJScrollPane(), BorderLayout.CENTER);

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
	 * 初始化整套漫畫連結資料、總集數等等..
	 * 
	 * @param comic
	 */
	public void setComic(ComicWrapper comic) {
		setTitle(comic.getName());
		mComic = comic;
		mTable.addMultiColumnName(new String[] { "集數", "已下載" });
		mTable.getColumn(1).setMaxWidth(60);
		mComic.getActDataList(mTable::addRowData);

		JPanel bookDataPanel = new JPanel(new GridLayout(2, 1, 10, 0));
		bookDataPanel.add(new JLabel("作者:" + comic.getAuthor()));
		bookDataPanel.add(new JLabel("更新:" + comic.getLatestUpdateDateTime()));
		mNorthPanel.add(bookDataPanel);

		// 在table上增加雙擊開啟動畫集數列表功能
		mTable.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {// 雙擊漫畫後，開始下戴漫畫
					int row = mTable.getJTable().rowAtPoint(e.getPoint());
					listAllJPG(mComic, new int[] { row });
				}
			}
		});
	}

	/**
	 * 釋放UI memory
	 */
	public void close() {
		dispose();
	}

	public boolean isDownloadedList() {
		return (mTable.getDataCount() > 0);
	}

	private void listAllJPG(final ComicWrapper comic, final int[] selectList) {
	}

	private ActionListener mActionListener = new ActionListener() {
		/**
		 * 單種漫畫集數列表上的按鈕被點擊時產生的事件處理,以事件名稱分別為:<BR>
		 * 自定事件名稱/功能說明<BR>
		 * "ListAllJPG" 將所選取的漫畫集數加到下載列並開始下載<BR>
		 * "detial" 漫畫簡介<BR>
		 * "addLove" 把漫畫編號加到我的最愛<BR>
		 */
		@Override
		public void actionPerformed(ActionEvent act) {
			Object obj = act.getSource();

			if (obj instanceof JButton) {
				JButton b = (JButton) obj;
				if (b.getName().equals("ListAllJPG")) {
					int[] selectList = mTable.getSelectedRows();// 取出選了哪幾集漫畫

					listAllJPG(mComic, selectList);
				} else if (b.getName().equals("detial")) {
					detail(mComic);
				} else if (b.getName().equals("addLove")) {
					addLove(mComic);
				}
			}
		}
	};

	private void detail(ComicWrapper comic) {
		if (comic.isDownloadedIcon()) {
			JOptionPane.showMessageDialog(null, comic.getDescription(), comic.getName() + "漫畫簡介",
					JOptionPane.INFORMATION_MESSAGE, comic.getIcon());
		} else {
			JOptionPane.showMessageDialog(null, comic.getDescription(), comic.getName() + "漫畫簡介",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void addLove(ComicWrapper comic) {
		// TODO 以後再加的功能
	}
}
