package JDownLoadComic.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 讀取文字檔 2010/05/12
 * 
 * @author Ray
 * 
 */

public class ReadFile {

	public ReadFile() {
	}

	/**
	 * 取得文章內容，串成一個string之後回傳
	 * 
	 * @param fileName
	 * @param fmt
	 * @return
	 */
	public String readFile(String fileName, String fmt) {
		StringBuffer txt = new StringBuffer();
		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fileInputStream, fmt));
			String line = "";

			while ((line = reader.readLine()) != null) {
				if (line.length() > 0) {
					// 處理讀取BOM（ byte-order mark
					// )格式檔案,在讀取utf8檔案的開頭會有utf8檔案格式的標記,需略過此標記再重串內容,標記16進位EF BB
					// BF
					int c = line.charAt(0);
					if (c == 65279) {
						line = line.substring(1, line.length());
					}
				}
				txt.append(line + "\r\n");
			}
			fileInputStream.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txt.toString();
	}

	/**
	 * 取得文章內容，以一行一行存在ArrayList
	 * 
	 * @param fileName
	 * @param fmt
	 * @return
	 */
	public ArrayList readFileToArrayList(String fileName, String fmt) {
		ArrayList list = new ArrayList();

		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fileInputStream, fmt));
			String line = "";

			while ((line = reader.readLine()) != null) {
				if (line.length() > 0) {
					// 處理讀取BOM（ byte-order mark
					// )格式檔案,在讀取utf8檔案的開頭會有utf8檔案格式的標記,需略過此標記再重串內容,標記16進位EF BB
					// BF
					int c = line.charAt(0);
					if (c == 65279) {
						line = line.substring(1, line.length());
					}
				}
				list.add(line);
			}
			fileInputStream.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
