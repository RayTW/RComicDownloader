package JDownLoadComic;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import java.awt.Rectangle;

import JDownLoadComic.util.ReadFile;

public class DatabaseLite implements java.io.Serializable {
	private static final long serialVersionUID = -3997944256004566946L;
	/** 首次公開發佈日期 */
	public static final String firstDate = "2010/10/10";
	/** 軟體最後更新日期 */
	public String appUpdateDate = "2013/12/13";
	public String pathName;
	/** 最後更新的日期時間間隔幾小時才會再更新 */
	private long updateHours;
	private int newLine; // 漫畫簡介每幾個中文字跳行
	/** 每張圖片每秒下載最大kb數 */
	private int downLoadKB; // 每張圖片每秒下載最大kb數
	private ArrayList<String[]> comicList;
	private ArrayList<String[]> loveComicList;
	private ArrayList<String[]> newComicList;
	/** 最後更新的日期時間 */
	private String updateDate;
	/** 打開漫畫時的解析度w*h, 0x0:預設圖大小 */
	private String readerWH;
	/** 最多同時下載幾個 */
	public int downCountLimit; // 最多同時下載幾個

	/** 我的最愛裡有最新漫畫時標示的cell文字顏色 */
	public Color foregroundColor;
	/** 我的最愛裡有最新漫畫時標示的cell背景顏色 */
	public Color backgroundColor;
	/** 讀取最新漫畫日誌(0~5),此設定值暫不開放修改 */
	public int refrushPage;
	/** 管理者 */
	public boolean isAdmin;
	/** 漫畫集數排序方式,設定值請參考SortFilesTool */
	public int sortType;
	/** 升或降 */
	public boolean sortAsc;
	/** 點漫畫圖片就換頁 */
	public String touchImg = "Y";
	/** 匯出pdf的儲存路徑 */
	public String exportPDFpath = File.separator + Config.defaultSavePath;
	/** 首頁的視窗縮放設定 */
	public Rectangle indexBounds = new Rectangle(100, 100, 800, 600);

	public DatabaseLite() {
		init();
	}

	public void init() {
		comicList = new ArrayList<String[]>();
		loveComicList = new ArrayList<String[]>();
		newComicList = new ArrayList<String[]>();
		updateDate = "0000-00-00 00:00:00";
		isAdmin = false;
		pathName = "./db";
		resetDefault();
	}

	/**
	 * 回復設定為預設狀態
	 */
	public void resetDefault() {
		updateHours = 3;
		readerWH = "864x1200";
		downLoadKB = 100;
		newLine = 50;
		downCountLimit = 100;
		foregroundColor = Color.RED;
		backgroundColor = Color.YELLOW;
		refrushPage = 5;
		sortType = 3;// sort by name
		sortAsc = true;
	}

	public void setComicList(String path, String charset) {
		ReadFile rf = new ReadFile();
		ArrayList<String> ary = rf.readFileToArrayList(path, charset);

		for (int i = 0; i < ary.size(); i++) {
			comicList.add(ary.get(i).toString().split("[|]"));
		}
	}

	public void setLoveComicList(String path, String charset) {
		ReadFile rf = new ReadFile();
		ArrayList<String> ary = rf.readFileToArrayList(path, charset);

		for (int i = 0; i < ary.size(); i++) {
			loveComicList.add(ary.get(i).toString().split("[|]"));
		}
	}

	public void addComicList(String[] comicData) {
		comicList.add(comicData);
	}

	public void addLoveComicList(String[] comicData) {
		loveComicList.add(comicData);
	}

	public void addNewComicList(String[] comicData) {
		newComicList.add(comicData);
	}

	public void addAllComicList(ArrayList<String[]> ary) {
		comicList.addAll(ary);
	}

	public void addAllLoveComicList(ArrayList<String[]> ary) {
		loveComicList.addAll(ary);
	}

	public void addAllNewComicList(ArrayList<String[]> ary) {
		newComicList.addAll(ary);
	}

	public void clearComicList() {
		comicList.clear();
	}

	public void clearLoveComicList() {
		loveComicList.clear();
	}

	public void clearNewComicList() {
		newComicList.clear();
	}

	public ArrayList<String[]> getComicList() {
		return comicList;
	}

	public ArrayList<String[]> getLoveComicList() {
		return loveComicList;
	}

	public ArrayList<String[]> getNewComicList() {
		return newComicList;
	}

	public void setUpdateDate(String dateTime) {
		updateDate = dateTime;
	}

	// 設定最後更新時間
	public void updateDate() {
		updateDate = getDateTime(0);
	}

	public String getUpdateDate() {
		return updateDate;
	}

	/**
	 * 設定下載圖片最高速率(kb),0:表示不限制
	 * 
	 * @param kb
	 */
	public boolean setDownLoadKB(int kb) {
		downLoadKB = kb;
		return true;
	}

	/**
	 * 取得下載圖片最高速率(kb)
	 * 
	 * @return
	 */
	public int getDownLoadKB() {
		return downLoadKB;
	}

	/**
	 * 設定漫畫簡介每行字數
	 * 
	 * @param cnt
	 * @return
	 */
	public boolean setNewline(int cnt) {
		newLine = cnt;
		return true;
	}

	/**
	 * 取得漫畫簡介每行字數
	 * 
	 * @return
	 */
	public int getNewline() {
		return newLine;
	}

	/**
	 * 最後更新的日期時間間隔幾小時才會再更新
	 * 
	 * @param h
	 * @return
	 */
	public boolean setUpdateHours(long h) {
		updateHours = h;
		return true;
	}

	public boolean isValidUpdateHour(String dateTime) {
		return (dateTime.matches("[0-9]{0,10}"));
	}

	/**
	 * 取得最後更新的日期時間間隔幾小時
	 * 
	 * @return
	 */
	public long getUpdateHours() {
		return updateHours;
	}

	/**
	 * 設定同時下載漫畫佇列上限個數
	 * 
	 * @param cnt
	 * @return
	 */
	public boolean setDownCountLimit(int cnt) {
		downCountLimit = cnt;
		return true;
	}

	/**
	 * 取得同時下載漫畫佇列上限個數
	 * 
	 * @return
	 */
	public int getDownCountLimit() {
		return downCountLimit;
	}

	public boolean isValidReaderWH(String w, String h) {
		if (!w.matches("[0-9]{1,4}")) {
			return false;
		}
		if (!h.matches("[0-9]{1,4}")) {
			return false;
		}
		return true;
	}

	public void setReaderWH(String w, String h) {
		readerWH = w + "x" + h;
	}

	public String getReaderWH() {
		return readerWH;
	}

	public void save() {
		try {
			FileOutputStream fs = new FileOutputStream(pathName);
			ObjectOutputStream out = new ObjectOutputStream(fs);
			out.writeObject(this);
			out.close();
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void load() {
		try {
			FileInputStream fs = new FileInputStream(pathName);
			ObjectInputStream in = new ObjectInputStream(fs);
			DatabaseLite obj = (DatabaseLite) in.readObject();

			if (obj != null) {
				comicList = (ArrayList<String[]>) obj.getComicList().clone();
				loveComicList = (ArrayList<String[]>) obj.getLoveComicList()
						.clone();
				newComicList = (ArrayList<String[]>) obj.getNewComicList()
						.clone();
				updateDate = obj.getUpdateDate();
				updateHours = obj.updateHours;
				isAdmin = obj.isAdmin;

				if (obj.getReaderWH() != null) {
					readerWH = obj.getReaderWH();
					newLine = obj.getNewline();
					downCountLimit = obj.getDownCountLimit();
					downLoadKB = obj.getDownLoadKB();
				}

				if (obj.sortType > 0) {
					sortType = obj.sortType;
					sortAsc = obj.sortAsc;
				}

				if (obj.foregroundColor != null) {
					foregroundColor = obj.foregroundColor;
				}
				if (obj.backgroundColor != null) {
					backgroundColor = obj.backgroundColor;
				}
				if (obj.exportPDFpath != null) {
					exportPDFpath = obj.exportPDFpath;
				}
				if (obj.indexBounds != null) {
					indexBounds = obj.indexBounds;
				}
			}
			in.close();
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void comicInfo() {
		for (int i = 0; i < comicList.size(); i++) {
			String[] data = comicList.get(i);
			System.out.println(data[0] + "=>" + data[1]);
		}
	}

	public void loveComicInfo() {
		for (int i = 0; i < newComicList.size(); i++) {
			String[] data = newComicList.get(i);
			System.out.println(data[0] + "=>" + data[1]);
		}
	}

	public void newComicInfo() {
		for (int i = 0; i < newComicList.size(); i++) {
			String[] data = newComicList.get(i);
			System.out.println(data[0] + "=>" + data[1]);
		}
	}

	public boolean updateEnable() {
		String date1 = getDateTime(0);
		long sec = diffentSec(date1, updateDate);
		// System.out.println("sec["+sec+"],update["+(updateHours * 60 *
		// 60)+"]");
		return (sec >= (updateHours * 60 * 60));
	}

	/**
	 * 取得日期時間
	 * 
	 * @param days
	 * @return
	 */
	public String getDateTime(int days) {
		java.util.Date cdNow = new java.util.Date(System.currentTimeMillis());
		Calendar cCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cCal.setTime(cdNow);
		cCal.add(Calendar.HOUR, 8 + days * 24);
		int[] cAry = { Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY,
				Calendar.MINUTE, Calendar.SECOND };
		char[] wAry = { '-', '-', ' ', ':', ':' };

		StringBuffer str = new StringBuffer(Integer.toString(cCal
				.get(Calendar.YEAR)));

		for (int i = 0; i < cAry.length; i++) {
			str.append(wAry[i]);
			String n = "";
			if (i == 0) {
				n = Integer.toString(cCal.get(cAry[i]) + 1);
			} else {
				n = Integer.toString(cCal.get(cAry[i]));
			}
			if (n.length() < 2) {
				n = "0" + n;
			}
			str.append(n);
		}
		return str.toString();
	}

	/**
	 * 計算二個時間點的秒差
	 * 
	 * @param Time1
	 * @param Time2
	 * @return
	 */
	public long diffentSec(String dateTime1, String dateTime2) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter1 = new SimpleDateFormat(
				"MM/dd/yyyy HH:mm:ss");

		try {
			dateTime1 = formatter1.format(formatter.parse(dateTime1));
			dateTime2 = formatter1.format(formatter.parse(dateTime2));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (java.util.Date.parse(dateTime1) - java.util.Date
				.parse(dateTime2)) / 1000;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatabaseLite db = new DatabaseLite();
		// db.addComicList(new String[]{"1","abc"});
		// db.addComicList(new String[]{"2","abcdd"});
		// db.addComicList(new String[]{"3","aaeeabc"});
		// db.save();
		System.out.println(db.getDateTime(0));
		System.out.println(db.diffentSec("2013-01-08 02:58:53",
				"2013-01-08 02:58:3"));

		System.out.println(db.updateEnable());
		db.updateDate();
		db.updateHours = 0;
		System.out.println(db.updateEnable());
	}

}
