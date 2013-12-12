package JDownLoadComic;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import JDownLoadComic.parseHtml.ActDataObj;

/**
 * 單本漫畫所有集數列表的畫面
 * 
 * @author Ray
 * 
 */

public class JDownLoadUI_Act extends JDownLoadUI_Default {
	/** 父層參考(首頁漫畫列表) */
	private EventHandle_index parentObj;
	/** 處理集數網頁事件 */
	private EventHandle_Act eventHandleAct; // 處理集數網頁事件
	/** 上面工具列 */
	private JPanel northPanel;

	private ActDataObj actObj;

	/**
	 * 初始化
	 * 
	 * @param dataObj
	 *            整套漫畫連結資料、總集數等等..
	 */
	public JDownLoadUI_Act(ActDataObj dataObj) {
		setTitle(dataObj.cartoonName);
		actObj = dataObj;
		table.addMultiColumnName(new String[] { "集數", "已下載" });
		table.getColumn(1).setMaxWidth(60);
		table.addMutilRowDataArray(dataObj.getActDataList());
		initJDownLoadUI_Act();
		eventHandleAct.setActDataObj(dataObj);

		JPanel bookDataPanel = new JPanel(new GridLayout(2, 1, 10, 0));
		bookDataPanel.add(new JLabel("作者:" + dataObj.getAuthor()));
		bookDataPanel.add(new JLabel("更新:" + dataObj.getLastUpdateDate()));
		// bookDataPanel.add(new JLabel("發行:"+dataObj.getPublishDate()));
		northPanel.add(bookDataPanel);
	}

	/**
	 * 初始化
	 * 
	 */
	public void initJDownLoadUI_Act() {
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		eventHandleAct = new EventHandle_Act();
		eventHandleAct.setParentObj(this);

		northPanel = new JPanel();

		JButton dataActListBtn = new JButton("下載選取漫畫");
		dataActListBtn.setName("ListAllJPG");
		dataActListBtn.addActionListener(eventHandleAct);
		northPanel.add(dataActListBtn);

		JButton detialBtn = new JButton("漫畫簡介");
		detialBtn.setName("detial");
		detialBtn.addActionListener(eventHandleAct);
		northPanel.add(detialBtn);

		JButton addLoveBtn = new JButton("加到我的最愛");
		addLoveBtn.setName("addLove");
		addLoveBtn.addActionListener(eventHandleAct);
		northPanel.add(addLoveBtn);

		table.setRowHeight(40);
		table.setFont(new Font("Serif", Font.BOLD, 20));

		c.add(northPanel, BorderLayout.NORTH);
		c.add(table.getJScrollPaneJTable(), BorderLayout.CENTER);

		// 設定視窗
		setSize(600, 300);
		setLocation(300, 200);

		// 按下視窗關閉鈕事件處理
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
	}

	/**
	 * 新增漫畫資料到我的最愛
	 * 
	 * @param comicID
	 * @param comicName
	 */
	public boolean addToLove(String comicID, String comicName) {
		return parentObj.addComicToLove(comicID, comicName);
	}

	/**
	 * 設定父層
	 * 
	 * @param obj
	 */
	public void setParent(EventHandle_index obj) {
		parentObj = obj;
	}

	/**
	 * 設定狀態列文字
	 * 
	 * @param txt
	 */
	public void setStateText(String txt) {
		parentObj.setStateText(txt);
	}

	/**
	 * 因一種漫畫列表都是new出來的，當關閉時，清除所有的參考
	 */
	public void close() {
		parentObj.removeFromComicPool(actObj.cartoonName);
		eventHandleAct.close();
		eventHandleAct = null;
		northPanel = null;
		parentObj = null;
		actObj = null;
		dispose();
	}

	/**
	 * 設定右側下載漫畫狀態列參考
	 * 
	 * @param table
	 */
	public void setDataTableList(TableList table) {
		eventHandleAct.setDataTableList(table);
	}

}
