package JDownLoadComic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import JDownLoadComic.parseHtml.SingleComicData;
import JDownLoadComic.util.ThreadPool;
import JDownLoadComic.util.LoadBar;
import JDownLoadComic.util.WriteFile;

/**
 * 秀下載進度的畫面
 * 
 * @author Ray
 * 
 */

public class LoadBarState extends LoadBar implements DownLoadThread.Callback {
	/** 父層(下載狀態列表) */
	private TableList parentObj;
	/** 下載單集漫畫執行緒 */
	private DownLoadThread downLoad;
	private SingleComicData singleComic;
	private boolean isDownloading;
	private String savePath;

	public LoadBarState() {
		isDownloading = false;
		initLoadBarJFrame();
	}

	/**
	 * 初始化，並實做狀態列功能事件處理:<BR>
	 * 1.暫停<BR>
	 * 2.繼續<BR>
	 * 3.移除<BR>
	 * 
	 */
	public void initLoadBarJFrame() {
		final JButton pause = new JButton("暫停");
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (downLoad != null) {
					pause.setText(downLoad.isPause ? "暫停" : "繼續");
					downLoad.isPause = !downLoad.isPause;

				}
			}
		});
		add(pause);

		final JButton delete = new JButton("移除");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downLoad.stopJpgLink();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						// delete.setText("處理中");
						// delete.setEnabled(false);
						delete.setEnabled(false);
						pause.setEnabled(false);
					}
				});
			}
		});
		add(delete);

	}

	/**
	 * 設定父層
	 * 
	 * @param p
	 */
	public void setParentObj(TableList p) {
		parentObj = p;
	}

	public void setDownloadPath(SingleComicData sComic, String path) {
		singleComic = sComic;
		savePath = path;
	}

	/**
	 * 是否已開下載中，true:已啟動下載，false:未啟動下載
	 * 
	 * @return
	 */
	public boolean isDownloading() {
		return isDownloading;
	}

	/**
	 * 開始下載漫畫,執行此METHOD之前需先呼叫setDownloadPath()
	 * 
	 * @param singleComic
	 *            單本漫畫資料
	 * @param savePath
	 *            漫畫存檔路徑
	 */
	public void startDownload() {
		if (!isDownloading) {
			isDownloading = true;
			boolean b = singleComic.setPageList();
			System.out.println("b->" + b);
			if (b) {
				downLoad = new DownLoadThread();// 建立排序去load漫畫
				downLoad.setSingleComicData(this, singleComic);
				WriteFile.mkDir(savePath);
				downLoad.savePath = savePath;
				downLoad.startJpgLink();
				// 將下載任務放到pool
				ThreadPool.execute(downLoad);
			} else {
				isDownloading = false;
				parentObj.setStateText(Config.netWorkDisconnect);
			}
		}
	}

	/**
	 * 下載完成時會呼叫此method，並通知上層移除狀態列中此集漫畫進度表
	 * 
	 * @param name
	 *            下載完成的漫畫
	 */
	public void downLoadDone(String name) {
		parentObj.removeObj(name);
	}

	/**
	 * 取得目前下載中漫畫名稱
	 * 
	 * @return
	 */
	public String getLoadCartoonName() {
		return getLoadName();
	}

	@Override
	public void onloading(int currentPage, int totalPage) {
		setProgressBar(currentPage, totalPage, currentPage + "/" + totalPage);

	}

	@Override
	public void onComplete() {
		close();
	}

	@Override
	public void onDownloadFail(SingleComicData singleComic) {
		parentObj.setStateText(Config.netWorkDisconnect);
	}

	/**
	 * 清除下載狀態列使用到的物件
	 * 
	 */
	public void close() {
		try {
			downLoad = null;
			downLoadDone(getLoadCartoonName());
			parentObj = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
