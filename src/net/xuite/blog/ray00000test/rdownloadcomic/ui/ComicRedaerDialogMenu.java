package net.xuite.blog.ray00000test.rdownloadcomic.ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.xuite.blog.ray00000test.rdownloadcomic.service.Config;
import net.xuite.blog.ray00000test.rdownloadcomic.service.FolderManager;
import net.xuite.blog.ray00000test.rdownloadcomic.service.RComicDownloader;
import net.xuite.blog.ray00000test.rdownloadcomic.util.JDataTable;
import net.xuite.blog.ray00000test.rdownloadcomic.util.SortFilesTool;
import net.xuite.blog.ray00000test.rdownloadcomic.util.WriteFile;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 彈出讓使用者選擇要觀看哪集漫畫,使用者需要先單擊左邊Table，之後右邊Table會列出該漫畫目前已下載的集數，
 * 之後使用者在右邊Table雙擊一集漫畫後，會經由電腦預設的瀏覽器打開漫畫，讓使用者方便閱讀
 * 
 * @author Ray
 * 
 */
public class ComicRedaerDialogMenu extends JDialog {
	protected String root = "./" + Config.defaultSavePath;
	protected JDataTable actTable;// 漫畫集數列表
	protected String comicFloder = "";// 選擇哪本漫畫目錄
	protected JLabel selectedComicLab;
	protected WriteFile writeFileTool;
	protected JPanel topPanel;
	protected JPanel leftPanel;// on top bar
	protected JPanel rightPanel;// on top bar
	protected Window mWindow;

	public ComicRedaerDialogMenu(Window owner) {
		super(owner);
		mWindow = owner;

		writeFileTool = new WriteFile();
		final Container c = getContentPane();
		JPanel centerPanel = new JPanel(new GridLayout(1, 2));

		c.add(centerPanel, BorderLayout.CENTER);

		int w = owner.getWidth();
		int h = owner.getHeight();
		setBounds(owner.getX(), owner.getY(), w, h);

		// 建立左邊漫畫資料夾列表
		final JDataTable table = createComicFolder(root);
		table.setRowHeight(40);
		table.setFont(new Font("Serif", Font.BOLD, 20));
		table.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getJTable().rowAtPoint(e.getPoint());

				comicFloder = table.getJTable().getValueAt(row, 0).toString();

				// 點擊漫畫後，列出漫畫已下載的集數
				reloadActFolder(RComicDownloader.get().getDB().sortType, root
						+ "/" + comicFloder,
						RComicDownloader.get().getDB().sortAsc);
				selectedComicLab.setText(comicFloder);
			}
		});

		centerPanel.add(table.getJScrollPaneJTable());

		actTable = new JDataTable(false);
		actTable.setRowHeight(40);
		actTable.setFont(new Font("Serif", Font.BOLD, 20));
		actTable.setReorderingAllowed(false);
		actTable.addColumnName("集數");
		actTable.addColumnName("下載完成時間");
		actTable.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = actTable.getJTable().rowAtPoint(e.getPoint());

				String comicActFloder = actTable.getJTable().getValueAt(row, 0)
						.toString();

				selectedComicLab.setText(comicFloder + "_" + comicActFloder);
				if (e.getClickCount() == 2) {// 雙擊漫畫集數後，打開漫畫
					openComicFolder(comicFloder, comicActFloder);
				}
			}
		});
		centerPanel.add(actTable.getJScrollPaneJTable());

		selectedComicLab = new JLabel("目前點選的漫畫");
		c.add(selectedComicLab, BorderLayout.SOUTH);

		createTopUI(c);
	}

	// 建立上面按鈕列
	private void createTopUI(Container c) {
		topPanel = new JPanel(new GridLayout(1, 2));
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		topPanel.add(leftPanel);
		topPanel.add(rightPanel);

		JButton sortNameBtn = new JButton("名稱排序");
		sortNameBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sortComicByName();
			}
		});
		rightPanel.add(sortNameBtn);

		JButton sortDateBtn = new JButton("日期排序");
		sortDateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sortComicByDate();
			}
		});
		rightPanel.add(sortDateBtn);

		JButton openComicBtn = new JButton(openComicBtnTitle());
		openComicBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openSelectedComic();
			}
		});
		rightPanel.add(openComicBtn);

		c.add(topPanel, BorderLayout.NORTH);
	}

	public String openComicBtnTitle() {
		return "開啟已選漫畫";
	}

	// 顯示各漫畫列表
	public JDataTable createComicFolder(String path) {
		// 建立秀資料類別
		JDataTable table = new JDataTable(false);
		table.setReorderingAllowed(false);
		table.addColumnName("漫畫名稱");
		table.addColumnName("下載完成時間");
		ArrayList<File> ary = SortFilesTool.sortByDate(path, true);

		for (int i = 0; i < ary.size(); i++) {
			File f = ary.get(i);
			if (f.isHidden() || f.getName().equals("icon")
					|| f.getName().equals("sys") || f.getName().equals("temp")) {
				continue;
			}
			table.addRowAryData(new String[] { f.getName(),
					convertTime(f.lastModified()) });
		}

		return table;
	}

	public String convertTime(long time) {
		Date date = new Date(time);
		Format format = new SimpleDateFormat(Config.dateFormat);
		return format.format(date).toString();
	}

	// 顯示每種漫畫裡的集數(話數)
	public void reloadActFolder(int sortType, String path, boolean asc) {
		removeAll(actTable);
		ArrayList<File> ary = SortFilesTool.sort(sortType, path, asc);

		for (int i = 0; i < ary.size(); i++) {
			File f = ary.get(i);
			if (f.isHidden() || f.getName().equals("icon")
					|| f.getName().equals("sys") || f.getName().equals("temp")) {
				continue;
			}
			actTable.addRowAryData(new String[] { f.getName(),
					convertTime(f.lastModified()) });
		}
	}

	private void removeAll(JDataTable table) {
		for (int i = table.getJTable().getRowCount() - 1; i >= 0; i--) {
			table.removeRow(i);
		}
	}

	/**
	 * 讀取漫畫資料，並轉換成html輸出後，利用瀏覽器打開漫畫.html檔
	 * 
	 * @param text
	 * @param fileTarget
	 * @param list
	 *            ex:"0.jpg|1.jpg|2.jpg|3.jpg|4.jpg|5.jpg|6.jpg|7.jpg|8.jpg"
	 * @param floderPath
	 *            ex: "../火影忍者/ 第620話 千手柱間/"
	 * @param imgWH
	 *            ex:"0x0"
	 * @return
	 */
	public boolean openComic(String text, String fileTarget, String[] list,
			String floderPath, String imgWH) {
		StringBuilder str = new StringBuilder();
		// String [] tempList = new String[list.length];
		ArrayList<String> ary = sortJpgList(list);

		for (int i = 0; i < ary.size(); i++) {
			if (str.length() > 0) {
				str.append("|");
			}
			str.append(ary.get(i));
		}

		String html = String.format(text, str, floderPath, imgWH,
				RComicDownloader.get().getDB().touchImg);

		if (!writeFileTool.writeText_UTF8(html, fileTarget)) {
			return false;
		}

		try {
			Desktop.getDesktop().open(new File(fileTarget));
		} catch (IOException e) {
			Config.showMsgBar("沒有安裝瀏覽器，無法觀看漫畫", "發生錯誤");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 將JPG圖從小到大排序
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<String> sortJpgList(String[] list) {
		ArrayList<String> ary = new ArrayList<String>();

		for (int i = 0; i < list.length; i++) {
			if (list[i].toLowerCase().lastIndexOf(".jpg") != -1) {
				ary.add(list[i]);
			}
		}

		for (int i = 0; i < ary.size() - 1; i++) {
			for (int j = 0; j < ary.size() - i - 1; j++) {
				String nameA = ary.get(j + 1);
				String nameB = ary.get(j);
				int pageA = Integer.parseInt(nameA.split("[.]")[0]);
				int pageB = Integer.parseInt(nameB.split("[.]")[0]);

				if (pageA < pageB) {
					String temp = nameA;
					ary.set(j + 1, nameB);
					ary.set(j, temp);
				}
			}
		}
		return ary;
	}

	// 以名稱排序漫畫列表
	private void sortComicByName() {
		RComicDownloader.get().getDB().sortType = SortFilesTool.FILE_NAME;
		RComicDownloader.get().getDB().sortAsc = !RComicDownloader.get()
				.getDB().sortAsc;
		reloadActFolder(RComicDownloader.get().getDB().sortType, root + "/"
				+ comicFloder, RComicDownloader.get().getDB().sortAsc);

	}

	// 以日期排序漫畫列表
	private void sortComicByDate() {
		RComicDownloader.get().getDB().sortType = SortFilesTool.FILE_DATE;
		RComicDownloader.get().getDB().sortAsc = !RComicDownloader.get()
				.getDB().sortAsc;
		reloadActFolder(RComicDownloader.get().getDB().sortType, root + "/"
				+ comicFloder, RComicDownloader.get().getDB().sortAsc);
	}

	// 開啟所有選擇的漫畫
	private void openSelectedComic() {
		int[] rows = actTable.getSelectedRows();

		for (int i = 0; i < rows.length; i++) {
			String comicActFloder = actTable.getJTable().getValueAt(rows[i], 0)
					.toString();
			openComicFolder(comicFloder, comicActFloder);
		}
	}

	/**
	 * 開啟指定漫畫集數
	 * 
	 * @param cFloder
	 *            漫畫第一層資料夾(例如:海賊王)
	 * @param comicActFloder
	 *            漫畫第二層資料夾(例如:第1話)
	 */
	public void openComicFolder(String cFloder, String comicActFloder) {
		String fullPath = root + "/" + cFloder + "/" + comicActFloder;
		final File actComit = new File(fullPath);
		final String html = Config.readerHTML;
		final String targetPath = Config.tempFolderPath + "/" + cFloder + "_"
				+ comicActFloder + ".html";
		final String jsPath = "../" + cFloder + "/" + comicActFloder + "/";

		FolderManager.createFolder(Config.tempFolderPath);

		openComic(html, targetPath, actComit.list(), jsPath, RComicDownloader
				.get().getDB().getReaderWH());
	}

	public void setStateMessage(String text) {
		((JDownLoadUI_index) this.mWindow).setStateText(text);
	}
}
