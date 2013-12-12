package JDownLoadComic;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class DownLoadSetting extends Panel {
	private JDownLoadUI_index dowanloadUI;
	private String[] compoment = { "6", "0", "1", "2", "3", "4", "5", "7", "8",
			"9", "10", "11", "12", "13", "14", "15" };

	public DownLoadSetting(JDownLoadUI_index ui) {
		dowanloadUI = ui;
		setLayout(new GridLayout(0, 2));
		setBackground(Color.white);
		for (int i = 0; i < compoment.length; i++) {
			JLabel lab = new JLabel();
			JTextField txt = new JTextField();
			lab.setName("lab" + compoment[i]);
			txt.setName("txt" + compoment[i]);
			txt.setVisible(false);
			add(lab);
			add(txt);
		}
		JButton btn1 = new JButton("回復");
		JButton btn2 = new JButton("套用");
		add(btn1);
		add(btn2);

		btn1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Config.db.resetDefault();
				reload();
			}
		});

		btn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				apply();
			}
		});
		reload();
	}

	private Component getComponent(String name) {
		Component[] com = getComponents();

		for (int i = 0; i < com.length; i++) {
			if (com[i].getName().equals(name)) {
				return com[i];
			}
		}
		return null;
	}

	public void reload() {
		// 版本資訊
		JLabel lab6 = (JLabel) getComponent("lab6");
		JTextField txt6 = (JTextField) getComponent("txt6");
		lab6.setText("目前版本");
		txt6.setText(Config.version);
		txt6.setEditable(false);
		txt6.setVisible(true);

		// 設定漫畫解析度
		JLabel lab0 = (JLabel) getComponent("lab0");
		JTextField txt0 = (JTextField) getComponent("txt0");
		lab0.setText("漫畫解析度(0x0:原圖大小)");
		txt0.setText(Config.db.getReaderWH());
		txt0.setVisible(true);

		// 更新間隔時間
		JLabel lab1 = (JLabel) getComponent("lab1");
		JTextField txt1 = (JTextField) getComponent("txt1");
		lab1.setText("更新間隔時間(小時)");
		txt1.setText("" + Config.db.getUpdateHours());
		txt1.setVisible(true);

		// 漫畫最後更新日期時間
		JLabel lab2 = (JLabel) getComponent("lab2");
		JTextField txt2 = (JTextField) getComponent("txt2");
		lab2.setText("漫畫最後更新日期時間");
		txt2.setText(Config.db.getUpdateDate());
		txt2.setEditable(false);
		txt2.setVisible(true);

		// 圖片下載速率限制(每秒/kb)
		JLabel lab3 = (JLabel) getComponent("lab3");
		JTextField txt3 = (JTextField) getComponent("txt3");
		lab3.setText("下載速率限制(每秒/kb),0:無限");
		txt3.setText("" + Config.db.getDownLoadKB());
		txt3.setVisible(true);

		// 漫畫簡介每行字數
		JLabel lab4 = (JLabel) getComponent("lab4");
		JTextField txt4 = (JTextField) getComponent("txt4");
		lab4.setText("漫畫簡介每行字數");
		txt4.setText("" + Config.db.getNewline());
		txt4.setVisible(true);

		// 同時下載漫畫佇列上限個數
		JLabel lab5 = (JLabel) getComponent("lab5");
		JTextField txt5 = (JTextField) getComponent("txt5");
		lab5.setText("同時下載漫畫佇列上限個數");
		txt5.setText("" + Config.db.getDownCountLimit());
		txt5.setVisible(true);

		// 以下功能只有最高權限者才可使用
		txt5.setEditable(Config.db.isAdmin);
		txt3.setEditable(Config.db.isAdmin);
	}

	public void apply() {
		// 設定漫畫解析度
		JTextField txt0 = (JTextField) getComponent("txt0");
		String[] wh = txt0.getText().split("[x]");

		if (wh.length != 2 || !Config.db.isValidReaderWH(wh[0], wh[1])) {
			Config.showMsgBar("漫畫圖片寬設定錯誤", "訊息");
			return;
		}

		// 更新間隔時間
		JTextField txt1 = (JTextField) getComponent("txt1");
		String updateHour = txt1.getText();

		if (!Config.db.isValidUpdateHour(updateHour)) {
			Config.showMsgBar("更新間隔時間設定錯誤", "訊息");
			return;
		}

		// 圖片下載速率限制(每秒/kb)
		JTextField txt3 = (JTextField) getComponent("txt3");
		String kbSec = txt3.getText();

		if (!kbSec.matches("[0-9]{0,4}")) {
			Config.showMsgBar("圖片下載速率限制(每秒/kb)設定錯誤", "訊息");
			return;
		}

		// 漫畫簡介每行字數
		JTextField txt4 = (JTextField) getComponent("txt4");
		String lineCount = txt4.getText();

		if (!lineCount.matches("[0-9]{0,4}")) {
			Config.showMsgBar("漫畫簡介每行字數設定錯誤", "訊息");
			return;
		}

		// 同時下載漫畫佇列上限個數
		JTextField txt5 = (JTextField) getComponent("txt5");
		String limitCount = txt5.getText();

		if (!limitCount.matches("[0-9]{0,3}")) {
			Config.showMsgBar("同時下載漫畫佇列上限個數設定錯誤", "訊息");
			return;
		}
		// 檢查目前是否有在下載漫畫，若正在下載則禁修改上限
		if (dowanloadUI.getCurrentDownLoadSize() > 0) {
			Config.showMsgBar("目前正在下載漫畫，無法修改下載上限個數，請下載完成後再修改設定", "訊息");
			return;
		}

		Config.db.setReaderWH(wh[0], wh[1]);
		Config.db.setUpdateHours(Long.parseLong(updateHour));
		Config.db.setDownLoadKB(Integer.parseInt(kbSec));
		Config.db.setNewline(Integer.parseInt(lineCount));
		Config.db.setDownCountLimit(Integer.parseInt(limitCount));

		Config.db.save();
		dowanloadUI.resetTableList();
	}
}
