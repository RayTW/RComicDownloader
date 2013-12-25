package JDownLoadComic;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.itextpdf.text.PageSize;

import JDownLoadComic.util.PDF;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * 彈出讓使用者選擇要觀看哪集漫畫,使用者需要先單擊左邊Table，之後右邊Table會列出該漫畫目前已下載的集數，
 * 之後使用者在右邊Table雙擊一集漫畫後，會經由電腦預設的瀏覽器打開漫畫，讓使用者方便閱讀
 * 
 * @author Ray
 * 
 */
public class ComicExportPdfDialogMenu extends ComicRedaerDialogMenu {
	protected JLabel pdfPathLabel = new JLabel();

	protected class PdfExportHistory extends JFrame {
		private JTextArea history = new JTextArea();

		public PdfExportHistory() {
			getContentPane().add(new JScrollPane(history));

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					setModal(false);
					dispose();
				}
			});
		}

		public void appendText(String txt) {
			history.append(txt);
		}
	}

	protected PdfExportHistory mPdfExportHistory;

	public ComicExportPdfDialogMenu(final Window owner) {
		super(owner);
		mPdfExportHistory = new PdfExportHistory();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(pdfPathLabel, BorderLayout.CENTER);

		JButton pdfPathBtn = new JButton("瀏覽");
		leftPanel.add(pdfPathBtn, BorderLayout.WEST);
		pdfPathLabel.setText(Config.db.exportPDFpath);
		pdfPathBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser(Config.db.exportPDFpath);
				chooser.setDialogTitle("請選擇PDF儲存路徑");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = chooser.showOpenDialog(owner);

				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					Config.db.exportPDFpath = file.getPath();
					pdfPathLabel.setText(Config.db.exportPDFpath);
				}
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mPdfExportHistory.dispose();
				dispose();
			}
		});

		getContentPane().addHierarchyBoundsListener(
				new HierarchyBoundsListener() {
					@Override
					public void ancestorMoved(HierarchyEvent e) {
						resetJframeBounds(mPdfExportHistory);
					}

					@Override
					public void ancestorResized(HierarchyEvent e) {
						resetJframeBounds(mPdfExportHistory);
					}
				});
	}

	@Override
	public String openComicBtnTitle() {
		return "開始轉換";
	}

	/**
	 * 開啟指定漫畫集數
	 * 
	 * @param cFloder
	 *            漫畫第一層資料夾(例如:海賊王)
	 * @param comicActFloder
	 *            漫畫第二層資料夾(例如:第1話)
	 */
	@Override
	public void openComicFolder(String cFloder, String comicActFloder) {
		String fullPath = root + "/" + cFloder + "/" + comicActFloder;
		final File actComit = new File(fullPath);
		ArrayList<String> ary = sortJpgList(actComit.list());
		// 輸出的PDF路徑
		String targetPdfPath = Config.db.exportPDFpath + File.separator
				+ cFloder.trim() + comicActFloder.trim() + ".pdf";

		String result = "fail";
		try {
			if (PDF.comicFolderToPDF(PageSize.B3, fullPath, ary, targetPdfPath)) {
				result = "success";
			}
		} catch (Exception e) {
			result = e.toString();
		}
		if (!mPdfExportHistory.isShowing()) {
			resetJframeBounds(mPdfExportHistory);
			mPdfExportHistory.setVisible(true);
		}
		mPdfExportHistory.toFront();
		mPdfExportHistory.appendText(targetPdfPath + " : " + result + "\n");
	}

	public void resetJframeBounds(JFrame jframe) {
		int width = getWidth() / 2;
		int height = getHeight() / 2;
		jframe.setLocation(getX() + getWidth(), getY());
		jframe.setSize(width, height);
	}
}
