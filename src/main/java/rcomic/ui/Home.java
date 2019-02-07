package rcomic.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import rcomic.control.Comics;
import rcomic.control.ComicWrapper;
import rcomic.control.RComic;
import rcomic.utils.ui.JDataTable;

/**
 * 
 * @author ray
 *
 */
public class Home extends JFrame {
	private static final long serialVersionUID = 4239430715284526041L;
	/** 搜尋輸入框 */
	private JTextField mSearchComic;
	/** 放分頁 */
	private JTabbedPane mTabbedPand;
	/** 漫畫集數列表 */
	private EpisodesList mComicAct;
	/** 秀首頁所有漫畫列表 */
	private JDataTable<String> mAllComic;
	/** 最新漫畫 */
	private JDataTable<String> mNewComic;

	public Home() {
	}

	/**
	 * 初始化，產生首頁所有使用到的物件
	 * 
	 */
	public void initialize() {
		setupAllComic();
		setupNewComic();
		setupUI();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		double scale = 0.8;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		setSize((int) (screen.getWidth() * scale), (int) (screen.getHeight() * scale));

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void setupAllComic() {
		Comics comicList = RComic.get().getAllComics();
		mAllComic = new JDataTable<String>(false);
		mAllComic
				.addMultiColumnName(new String[] { RComic.get().getLang("Number"), RComic.get().getLang("ComicName") });
		mAllComic.setReorderingAllowed(false);// 鎖住換欄位位置功能，會影嚮雙擊開列表功能
		comicList.getComics().forEach(comic -> {
			mAllComic.addRowData(new String[] { comic.getId(), comic.getName() });
		});
		mAllComic.setRowHeight(40);
		mAllComic.getColumn(0).setMaxWidth(60);
		mAllComic.setFont(RComic.get().getConfig().getComicListFont());

		// 在table上增加雙擊開啟動畫集數列表功能
		mAllComic.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = mAllComic.getJTable().rowAtPoint(e.getPoint());
				String comicId = mAllComic.getJTable().getValueAt(row, 0).toString();
				ComicWrapper comic = RComic.get().searchAllById(comicId);

				showComicActList(comic);
			}
		});
	}

	private void setupNewComic() {
		Comics newComics = RComic.get().getNewComics();
		mNewComic = new JDataTable<String>(false);
		mNewComic.addMultiColumnName(
				new String[] { RComic.get().getLang("Number"), RComic.get().getLang("ComicNameEpisode") });
		newComics.getComics().forEach(comic -> {
			mNewComic.addRowData(new String[] { comic.getId(), comic.getNameWithNewestEpisode() });
		});
		mNewComic.setRowHeight(40);
		mNewComic.getColumn(0).setMaxWidth(60);
		mNewComic.setFont(RComic.get().getConfig().getComicListFont());
		mNewComic.getJTable().setToolTipText(RComic.get().getLang("PleaseDoubleClick"));
		// 在table上增加雙擊開啟動畫集數列表功能
		mNewComic.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = mNewComic.getJTable().rowAtPoint(e.getPoint());
				String comicId = mNewComic.getJTable().getValueAt(row, 0).toString();
				ComicWrapper comic = RComic.get().searchNewById(comicId);

				showComicActList(comic);
			}
		});
	}

	/**
	 * 漫畫集數列表
	 * 
	 * @param comic
	 */
	private void showComicActList(ComicWrapper comic) {
		RComic.get().getR8Comic().loadComicDetail(comic.get(), newComic -> {
			SwingUtilities.invokeLater(() -> mComicAct.setComic(new ComicWrapper(newComic)));
		});
	}

	private void setupUI() {
		setTitle(RComic.get().getConfig().mVersion);
		Container container = getContentPane();
		JPanel northPanel = new JPanel(new GridLayout(0, 5, 10, 0));
		JPanel centerPanel = new JPanel(new GridLayout(0, 2));
		getContentPane().add(northPanel, BorderLayout.NORTH);

		JPanel findPanel = new JPanel(new BorderLayout());
		mSearchComic = new JTextField();
		findPanel.add(new JLabel(RComic.get().getLang("Search")), BorderLayout.WEST);
		findPanel.add(mSearchComic, BorderLayout.CENTER);
		northPanel.add(findPanel);

		mTabbedPand = new JTabbedPane();
		mTabbedPand.add(RComic.get().getLang("ComicList"), mAllComic.toJScrollPane());
		mTabbedPand.add(RComic.get().getLang("NewestComic"), mNewComic.toJScrollPane());
		centerPanel.add(mTabbedPand);

		mComicAct = new EpisodesList();
		centerPanel.add(mComicAct);

		container.add(centerPanel, BorderLayout.CENTER);

		mSearchComic.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void update() {
				RComic.get().search(mSearchComic.getText(), result -> {
					SwingUtilities.invokeLater(() -> {
						mAllComic.removeAll();
						result.forEach(comic -> {
							mAllComic.addRowData(new String[] { comic.getId(), comic.getName() });
						});
					});
				});
			}
		});
	}

	/**
	 * 取得搜尋漫畫輸入框 要搜尋的漫畫名稱
	 */
	public String getFindFieldText() {
		return mSearchComic.getText();
	}

	/**
	 * 取得目前畫面正在view的列表 0:所有漫畫列表, 1:最新漫畫列表
	 * 
	 * @return
	 */
	public int getSelectList() {
		return mTabbedPand.getSelectedIndex();
	}

	/**
	 * 漫畫下載程式 啟動點,啟動時會連向遠端檢查是本地端是否有漫畫編號文字檔，若沒有會重load並存檔
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JDialog loading = RComic.get().newLoadingDialog();

		RComic.get().preprogress(() -> {
			SwingUtilities.invokeLater(() -> {
				// 建立動畫程式首頁
				Home download = new Home();
				download.initialize();

				loading.dispose();
			});
		});
	}
}
