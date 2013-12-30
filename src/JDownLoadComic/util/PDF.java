package JDownLoadComic.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 將指定的folder裡所有的JPG轉成一份PDF
 * 
 * @author Ray
 * @version 2013.12.13
 * 
 */
public class PDF {

	public PDF() {
	}

	/**
	 * 將排序過的圖片轉成PDF
	 * 
	 * @param pageSize
	 *            pdf的文件大小:A4 or B3 etc...
	 * @param sourceFolderPath
	 *            圖片所在的資料夾路徑
	 * @param pathList
	 *            排序過的圖片列表
	 * @param targetPdfPath
	 *            要儲存的的PDF路徑
	 * @return
	 */
	public static boolean comicFolderToPDF(Rectangle pageSize,
			String sourceFolderPath, ArrayList<String> pathList,
			String targetPdfPath) {
		boolean result = false;
		Document document = new Document(pageSize);

		try {
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(targetPdfPath));
			writer.setPageEvent(new HeaderFooter(14));
			document.open();

			for (String filename : pathList) {
				String filePath = sourceFolderPath + File.separator + filename;
				document.newPage();

				Image img = Image.getInstance(filePath);
				img.scaleToFit(img.getWidth(), img.getHeight());
				img.setBorderColor(BaseColor.WHITE);

				document.add(img);
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		document.close();

		return result;
	}

	public static boolean comicFolderToPDF(Rectangle pageSize,
			String sourceFolderPath, String targetPdfPath) {
		boolean result = false;
		Document document = new Document(pageSize);

		try {
			File f = new File(sourceFolderPath);

			if (f.isDirectory()) {
				PdfWriter writer = PdfWriter.getInstance(document,
						new FileOutputStream(targetPdfPath));
				writer.setPageEvent(new HeaderFooter(14));
				document.open();

				File[] list = f.listFiles();

				for (File file : list) {
					String absolutePath = file.getAbsolutePath().toLowerCase();

					if (file.isFile()
							&& (absolutePath.endsWith("jpg") || absolutePath
									.endsWith("jpeg"))) {
						// System.out.println("absolutePath-->" + absolutePath);
						document.newPage();

						Image img = Image.getInstance(file.getPath());
						img.scaleToFit(img.getWidth(), img.getHeight());
						img.setBorderColor(BaseColor.WHITE);

						document.add(img);
					}
				}
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		document.close();

		return result;
	}

	// public static void main(String[] args) {
	// String path =
	// "/Users/leeray/Documents/swing_project/JComicDownload2/漫畫下載區/火影忍者/ 第658話 尾獸VS斑…！/";
	// comicFolderToPDF(PageSize.B3, path, "./火影.pdf");
	// }
}

class HeaderFooter extends PdfPageEventHelper {
	private int pageCount;
	private Font mFont;

	public HeaderFooter(int fontSize) {
		mFont = new Font();
		mFont.setColor(BaseColor.BLACK);
		mFont.setSize(fontSize);
	}

	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
		pageCount = 0;
	}

	@Override
	public void onChapter(PdfWriter writer, Document document,
			float paragraphPosition, Paragraph title) {
	}

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		pageCount++;
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		// 計算出文件中間位置
		float center = (document.right() - document.left()) / 2;

		writer.getDefaultColorspace();
		ColumnText.showTextAligned(writer.getDirectContent(),
				Element.ALIGN_MIDDLE, new Phrase(Integer.toString(pageCount),
						mFont), center, 0 + 10, 0);

	}
}
