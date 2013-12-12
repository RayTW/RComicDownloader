package JDownLoadComic.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.ImageIcon;

/**
 * 寫檔 2010/04/24
 * 
 * @author 吉他手Ray
 * 
 */

public class WriteFile {

	public WriteFile() {
		initWriteFile();
	}

	public void initWriteFile() {

	}

	/**
	 * 寫入文字檔(使用FileWriter 寫檔編碼為預設的iso-8859-1)，
	 * 因此此method使用OutputStreamWriter寫檔，可自行指定格式
	 * 
	 * @param text
	 *            將整個String寫入指定的檔案
	 * @param filename
	 *            可用相對路徑或絕對路徑
	 * @param format
	 *            寫入檔案的編碼格式
	 * @param append
	 *            true 將此次寫檔串在原本檔案最後面 | false 將此次寫檔蓋掉原本的文字檔內容
	 * @return true 寫檔成功 | false 寫檔失敗
	 */
	public boolean writeText(String text, String filename, String format,
			boolean append) {
		if (text.equals("")) {
			return false;
		}
		File file = new File(filename);// 建立檔案，準備寫檔
		try {
			BufferedWriter bufWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file, append),
							format));
			bufWriter.write(text);
			bufWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(filename + "寫檔發生錯誤");
			return false;
		}
		return true;
	}

	/**
	 * 寫入檔案使用utf8格式寫檔，並且復蓋原本檔案內容
	 * 
	 * @param text
	 * @param filename
	 * @return
	 */
	public boolean writeText_UTF8(String text, String filename) {
		return writeText(text, filename, "utf8", false);
	}

	/**
	 * 寫入檔案使用big5格式寫檔，並且復蓋原本檔案內容
	 * 
	 * @param text
	 * @param filename
	 * @return
	 */
	public boolean writeText_BIG5(String text, String filename) {
		return writeText(text, filename, "big5", false);
	}

	/**
	 * 寫入檔案使用utf8格式寫檔，串在原本檔案內容後面
	 * 
	 * @param text
	 * @param filename
	 * @return
	 */
	public boolean writeText_UTF8_Apend(String text, String filename) {
		return writeText(text, filename, "utf8", true);
	}

	/**
	 * 寫入檔案使用big5格式寫檔，串在原本檔案內容後面
	 * 
	 * @param text
	 * @param filename
	 * @return
	 */
	public boolean writeText_BIG5_Apend(String text, String filename) {
		return writeText(text, filename, "big5", true);
	}

	/**
	 * 檢查檔案是否存在
	 * 
	 * @param filename
	 * @return true 檔案已存在 | false 檔案不存在
	 */
	public static boolean exists(String path) {
		return new File(path).exists();
	}

	/**
	 * 建立新檔(檔案已存在會刪除舊檔並建新檔)
	 * 
	 * @param path
	 */
	public static void createNewFile(String path) {
		try {
			File file = new File(path);
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刪檔案
	 * 
	 * @param path
	 */
	public static void deleteFile(String path) {

		File file = new File(path);
		file.delete();

	}

	/**
	 * 讀圖檔，若沒有圖片回傳null
	 * 
	 * @param path
	 * @return
	 */
	public static ImageIcon getImageIcon(String path) {
		File img = new File(path);
		if (img.exists()) {
			return new ImageIcon(img.getPath());
		}
		return null;
	}

	/**
	 * 建立資料夾(可建多層資料夾
	 * 
	 * @param path
	 * @param 最後一層的資料夾
	 */
	public static void mkDir(String path) {
		File dir = new File(path);
		dir.mkdirs();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// WriteFile wf = new WriteFile();
		// wf.mkDir("./DownCartoon/==/==惡魔愛神 Vol.01");

		System.out.println(WriteFile.exists("./cartoonList.data"));

	}

}
