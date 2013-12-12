package JDownLoadComic.util;

/**
 * 下載進度資料物件
 * 
 * @author Ray
 * 
 */
public class LoadData {
	/** 目前的進度數值 */
	public int currentProeess;
	/** 結束的數值 */
	public int endValue;

	public LoadData() {
		LoadDataInit();
	}

	public void LoadDataInit() {
		currentProeess = 0;
		endValue = 1;
	}
}
