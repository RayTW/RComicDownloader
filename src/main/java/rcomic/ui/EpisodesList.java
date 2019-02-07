package rcomic.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import net.xuite.blog.ray00000test.library.comicsdk.Episode;
import rcomic.control.ComicWrapper;
import rcomic.control.RComic;
import rcomic.utils.ui.JDataTable;

/**
 * 單本漫畫所有集數列表
 * 
 * @author Ray
 * 
 */

public class EpisodesList extends JPanel {
	private static final long serialVersionUID = 5167725310966938209L;

	private JDataTable<String> mTable; // 秀首頁漫畫集數列表
	/** 上面工具列 */
	private JLabel mName;
	private JLabel mAuthor;
	private JLabel mModifyDate;
	private JTextArea mComicIntroduction;
	private ComicWrapper mComic;

	public EpisodesList() {
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
		mTable.getJTable().setToolTipText(RComic.get().getLang("DoubleClick"));

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
					Episode episode = mComic.getEpisodes().get(row);

					RComic.get().loadEpisodesImagesPagesUrl(episode, result -> {
						SwingUtilities.invokeLater(() -> {
							JDialog loading = RComic.get().newLoadingDialog();
							loading.toFront();

							RComic.get().addTask(() -> {
								// 用瀏覽器開啟漫畫
								RComic.get().openComic(result);
								SwingUtilities.invokeLater(() -> loading.dispose());
							});
						});
					});
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
