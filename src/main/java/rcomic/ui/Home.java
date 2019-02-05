package rcomic.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rcomic.control.ComicList;
import rcomic.control.ComicWrapper;
import rcomic.control.RComic;
import rcomic.utils.ui.JDataTable;

/**
 * 
 * @author ray
 *
 */
public class Home extends JFrame {
	/** 搜尋輸入框 */
	private JTextField mFindField;
	/** 放分頁 */
	public JTabbedPane mTabbedPand;
	/** 秀首頁所有漫畫列表 */
	protected JDataTable mAllComic;
	/** 最新漫畫 */
	protected JDataTable mNewComic;
	/** 下載中漫畫佇列 */
	private TableList mDownloadingComic;

	private JScrollPane mDownloadingComicScroll;

	/** 從文字檔讀取的漫畫列表、我的最愛列表資料 */
	private List<ComicList> mComicList; // 用來每讀取網頁資料

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
		ComicList comicList = RComic.get().getAllComics();
		mAllComic = new JDataTable<String>(false);
		mAllComic.addMultiColumnName(new String[] { "編號", "漫畫名稱" });
		mAllComic.setReorderingAllowed(false);// 鎖住換欄位位置功能，會影嚮雙擊開列表功能
		comicList.getComics().forEach(comic -> {
			mAllComic.addRowData(new String[] { comic.getId(), comic.getName() });
		});
		mAllComic.setRowHeight(40);
		mAllComic.getColumn(0).setMaxWidth(60);
		mAllComic.setFont(RComic.get().getConfig().getComicListFont());
		mAllComic.getJTable().setToolTipText(RComic.get().getConfig().getLangValue("PleaseDoubleClick"));

		// 在table上增加雙擊開啟動畫集數列表功能
		mAllComic.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {// 雙擊漫畫後，開始讀取漫畫集數列表
					int row = mAllComic.getJTable().rowAtPoint(e.getPoint());
					String comicId = mAllComic.getJTable().getValueAt(row, 0).toString();

					showActDialog(comicId);
				}
			}
		});
	}

	private void setupNewComic() {
		ComicList newComics = RComic.get().getNewComics();
		mNewComic = new JDataTable<String>(false);
		mNewComic.addMultiColumnName(new String[] { "編號", "漫畫名稱[集數]" });
		newComics.getComics().forEach(comic -> {
			mNewComic.addRowData(new String[] { comic.getId(), comic.getNameWithNewestEpisode() });
		});
		mNewComic.setRowHeight(40);
		mNewComic.getColumn(0).setMaxWidth(60);
		mNewComic.setFont(RComic.get().getConfig().getComicListFont());
		mNewComic.getJTable().setToolTipText(RComic.get().getConfig().getLangValue("PleaseDoubleClick"));
		// 在table上增加雙擊開啟動畫集數列表功能
		mNewComic.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {// 雙擊漫畫後，開始讀取漫畫集數列表
					int row = mNewComic.getJTable().rowAtPoint(e.getPoint());
					String comicId = mNewComic.getJTable().getValueAt(row, 0).toString();

					showActDialog(comicId);
				}
			}
		});
	}

	private void setupUI() {
		Container container = getContentPane();
		JPanel northPanel = new JPanel(new GridLayout(0, 5, 10, 0));
		JPanel centerPanel = new JPanel(new GridLayout(0, 2));
		getContentPane().add(northPanel, BorderLayout.NORTH);

		JPanel findPanel = new JPanel(new BorderLayout());
		mFindField = new JTextField();
		mFindField.setToolTipText("請輸入漫畫名稱後按enter");
		findPanel.add(new JLabel("搜尋"), BorderLayout.WEST);
		findPanel.add(mFindField, BorderLayout.CENTER);
		northPanel.add(findPanel);

		JButton exportPDFBtn = new JButton("匯出PDF");
		exportPDFBtn.setName("exportPDF");
		exportPDFBtn.addActionListener(mActionListener);
		northPanel.add(exportPDFBtn);

		mTabbedPand = new JTabbedPane();
		mTabbedPand.add("漫畫列表", mAllComic.toJScrollPane());
		mTabbedPand.add("最新漫畫", mNewComic.toJScrollPane());

		// 切換到我的最愛時不能使用更新與加到我的最愛功能
		mTabbedPand.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// int index = jt.getSelectedIndex();
				// // updateBtn.setEnabled(index == 0);
				// loveBtn.setEnabled(index == 0);
				// deleteBtn.setEnabled(index == 1);
				// // dataActListBtn.setEnabled(index != 3 && index != 4);
				// mFindField.setEnabled(index != 3 && index != 4);
				//
				// switch (index) {
				// case 1:
				// // 標示我的最愛有哪幾部漫畫有更新 與 更新的漫畫有哪幾步在我的最愛
				// // 標示我的最愛裡的漫畫有更新,將更新的漫畫編號讀取出來檢查，有出現在我的最愛就修改table cell color
				// markTableCell(tableNew, tableLove);
				// break;
				// case 2:
				// // 在最新漫畫上標示在我的最愛裡的漫畫
				// markTableCell(tableLove, tableNew);
				// break;
				// case 3:// 切回設定頁時，重新讀取設定值
				// downloadsetting.reload();
				// break;
				// default:
				// mFindField.requestFocus();
				// }
			}
		});

		centerPanel.add(mTabbedPand);

		container.add(centerPanel, BorderLayout.CENTER);

		mFindField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String findText = mFindField.getText();
					// eventHandleIndex.findComic(findText);

					// TODO 取出欲搜尋的關鍵字進行搜尋漫畫
				}
			}
		});
	}

	/**
	 * 建立 漫畫集數列表
	 * 
	 * @param comicId
	 */
	public synchronized void showActDialog(String comicId) {
		ComicList comicData = mComicList.get(0);
		ComicWrapper comic = comicData.getComicById(comicId);
		ComicAct act = new ComicAct();

		RComic.get().getR8Comic().loadComicDetail(comic, newComic -> {
			act.setGlassPane(this);
			act.setComic(comic);
			act.setVisible(true);
		});
	}

	/**
	 * 取得搜尋漫畫輸入框 要搜尋的漫畫名稱
	 */
	public String getFindFieldText() {
		return mFindField.getText();
	}

	/**
	 * 新增更新的漫畫到畫面列表上
	 * 
	 * @param data
	 */
	public void addTable(String[][] data) {
	}

	/**
	 * 取得目前畫面正在view的列表 0:所有漫畫列表, 1:我的最愛列表
	 * 
	 * @return
	 */
	public int getSelectList() {
		return mTabbedPand.getSelectedIndex();
	}

	/**
	 * 取得目前下載中的漫畫本數
	 * 
	 * @return
	 */
	public int getCurrentDownLoadSize() {
		int cnt = 0;
		if (mDownloadingComic != null) {
			cnt = mDownloadingComic.getCurrentDownLoadSize();
		}
		return cnt;
	}

	private static JDialog showLoading() {
		JDialog jDialog = new JDialog();
		jDialog.setLayout(new GridBagLayout());
		jDialog.add(new JLabel(RComic.get().getConfig().getLangValue("Loading")));
		jDialog.setMaximumSize(new Dimension(150, 50));
		jDialog.setResizable(false);
		jDialog.setModal(false);
		jDialog.setUndecorated(true);
		jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		jDialog.setLocationRelativeTo(null);
		jDialog.setVisible(true);
		jDialog.pack();
		return jDialog;
	}

	private ActionListener mActionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.out.println("e-->" + e.getActionCommand());
		}

	};

	/**
	 * 漫畫下載程式 啟動點,啟動時會連向遠端檢查是本地端是否有漫畫編號文字檔，若沒有會重load並存檔
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JDialog loading = showLoading();

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
