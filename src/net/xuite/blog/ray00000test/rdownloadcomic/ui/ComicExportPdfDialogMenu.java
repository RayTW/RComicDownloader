package net.xuite.blog.ray00000test.rdownloadcomic.ui;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import net.xuite.blog.ray00000test.rdownloadcomic.service.Config;
import net.xuite.blog.ray00000test.rdownloadcomic.service.RComicDownloader;
import net.xuite.blog.ray00000test.rdownloadcomic.util.PDF;

import com.itextpdf.text.PageSize;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

	public ComicExportPdfDialogMenu(final Window owner) {
		super(owner);
		this.leftPanel.setLayout(new BorderLayout());
		this.leftPanel.add(this.pdfPathLabel, "Center");

		JButton pdfPathBtn = new JButton("瀏覽");
		this.leftPanel.add(pdfPathBtn, "West");
		this.pdfPathLabel.setText(RComicDownloader.get().getDB().exportPDFpath);
		pdfPathBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser(RComicDownloader.get().getDB().exportPDFpath);
				chooser.setDialogTitle("請選擇PDF儲存路徑");
				chooser.setFileSelectionMode(1);
				int result = chooser.showOpenDialog(owner);

				if (result == 0) {
					File file = chooser.getSelectedFile();
					RComicDownloader.get().getDB().exportPDFpath = file.getPath();
					ComicExportPdfDialogMenu.this.pdfPathLabel
							.setText(RComicDownloader.get().getDB().exportPDFpath);
				}
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ComicExportPdfDialogMenu.this.dispose();
			}
		});
	}

	@Override
	public String openComicBtnTitle() {
		return "開始轉換";
	}

	@Override
	public void openComicFolder(final String cFloder,
			final String comicActFloder) {
		Runnable runObject = new Runnable() {
			@Override
			public void run() {
				String targetPdfPath = RComicDownloader.get().getDB().exportPDFpath + File.separator
						+ cFloder.trim() + comicActFloder.trim() + ".pdf";
				String fullPath = ComicExportPdfDialogMenu.this.root + "/"
						+ cFloder + "/" + comicActFloder;
				File actComit = new File(fullPath);
				ArrayList ary = ComicExportPdfDialogMenu.this
						.sortJpgList(actComit.list());

				String outPutMsg = cFloder.trim() + comicActFloder.trim()
						+ ".pdf";
				ComicExportPdfDialogMenu.this.setStateMessage(outPutMsg
						+ " 開始匯出");
				String result = "失敗";
				try {
					if (PDF.comicFolderToPDF(PageSize.B3, fullPath, ary,
							targetPdfPath))
						result = "成功";
				} catch (Exception e) {
					result = e.toString();
				}
				ComicExportPdfDialogMenu.this.setStateMessage(outPutMsg + " : "
						+ result);
			}
		};
		RComicDownloader.get().addPDFTask(runObject);
	}

}
