package rcomic.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import rcomic.control.ComicWrapper;
import rcomic.control.RComic;
import rcomic.utils.ui.JDataTable;

/**
 * 單本漫畫所有集數列表的畫面
 * 
 * @author Ray
 * 
 */

public class ComicAct extends JPanel {
	private static final long serialVersionUID = 5167725310966938209L;

	private JDataTable<String> mTable; // 秀首頁所有漫畫列表
	/** 上面工具列 */
	private JLabel mName;
	private JLabel mAuthor;
	private JLabel mModifyDate;
	private JTextArea mComicIntroduction;
	private ComicWrapper mComic;

	public ComicAct() {
		initialize();
	}

	/**
	 * 初始化
	 */
	public void initialize() {
		setLayout(new BorderLayout());
		mTable = new JDataTable<String>(false);
		JPanel northPanel = new JPanel();
		JPanel cneterPanel = new JPanel(new GridLayout(2, 0));

		mTable.setRowHeight(40);
		mTable.setFont(RComic.get().getConfig().getComicListFont());
		mTable.addMultiColumnName(new String[] { RComic.get().getLang("Episode") });
		mTable.setColumnWidth(0, 100);

		add(northPanel, BorderLayout.NORTH);

		mComicIntroduction = new JTextArea();
		mComicIntroduction.setWrapStyleWord(true);
		mComicIntroduction.setLineWrap(true);
		mComicIntroduction.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(mComicIntroduction);
		scrollPane.setBounds(10, 60, 780, 500);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		cneterPanel.add(scrollPane);
		cneterPanel.add(mTable.toJScrollPane());

		add(cneterPanel, BorderLayout.CENTER);

		JPanel bookDataPanel = new JPanel(new GridLayout(3, 1, 10, 0));
		mAuthor = new JLabel();
		mModifyDate = new JLabel();
		mName = new JLabel();
		bookDataPanel.add(mName);
		bookDataPanel.add(mAuthor);
		bookDataPanel.add(mModifyDate);

		northPanel.add(bookDataPanel);

		// 在table上增加雙擊開啟動畫集數列表功能
		mTable.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {// 雙擊漫畫後，開始下戴漫畫
					int row = mTable.getJTable().rowAtPoint(e.getPoint());
					// listAllJPG(mComic, new int[] { row });
				}
			}
		});
	}

	/**
	 * 初始化整套漫畫連結資料、總集數等等..
	 * 
	 * @param comic
	 */
	public void setComic(ComicWrapper comic) {
		mComic = comic;
		mTable.removeAll();
		mComic.getEpisodesName(mTable::addRowData);
		mName.setText(comic.getName());
		mAuthor.setText(RComic.get().getLang("Author") + comic.getAuthor());
		mModifyDate.setText(RComic.get().getLang("ModifyDate") + comic.getLatestUpdateDateTime());
		mComicIntroduction.setText(comic.getDescription());
	}

	public boolean isDownloadedList() {
		return (mTable.getDataCount() > 0);
	}
}
