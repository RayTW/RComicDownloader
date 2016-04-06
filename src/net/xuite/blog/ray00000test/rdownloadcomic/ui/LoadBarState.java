package net.xuite.blog.ray00000test.rdownloadcomic.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import net.xuite.blog.ray00000test.rdownloadcomic.service.DownLoadThread;
import net.xuite.blog.ray00000test.rdownloadcomic.service.DownloadComicTask;
import net.xuite.blog.ray00000test.rdownloadcomic.util.LoadBar;

/**
 * 秀下載進度的畫面
 * 
 * @author Ray
 * 
 */

public class LoadBarState extends LoadBar {
	/** 父層(下載狀態列表) */
	private DownloadComicTask parentObj;

	public LoadBarState() {
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
				DownLoadThread downLoad = parentObj.getDownLoadThread();

				if (downLoad != null) {
					if (downLoad.isPause()) {
						pause.setText("暫停");
						downLoad.resume();
					} else {
						pause.setText("繼續");
						downLoad.pause();
					}
				}
			}
		});
		add(pause);

		final JButton delete = new JButton("取消");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (parentObj != null) {
					parentObj.cancel();
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
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
	public void setParentObj(DownloadComicTask p) {
		parentObj = p;
	}

	/**
	 * 取得目前下載中漫畫名稱
	 * 
	 * @return
	 */
	public String getLoadCartoonName() {
		return getLoadName();
	}

	/**
	 * 清除下載狀態列使用到的物件
	 * 
	 */
	public void close() {
		parentObj = null;
	}
}
