package rcomic.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import rcomic.control.DownLoadThread;
import rcomic.utils.ui.LoadBar;

/**
 * 秀下載進度的畫面
 * 
 * @author Ray
 * 
 */

public class LoadBarState extends LoadBar {
	/** 父層(下載狀態列表) */
	private Listener mListener;

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
		final JButton delete = new JButton("取消");

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mListener != null) {
					mListener.cancel();
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						delete.setEnabled(false);
					}
				});
			}
		});
		add(delete);

	}

	public void setListener(Listener listener) {
		mListener = listener;
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
		mListener = null;
	}

	public static interface Listener {
		DownLoadThread getDownLoadThread();

		void cancel();
	}
}
