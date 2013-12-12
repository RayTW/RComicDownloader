package JDownLoadComic;

public class Command {

	public Command() {
	}

	public void sysCmd(String cmd) {
		String[] cmds = cmd.split("[ ]");
		if (cmds[0].equals("updatehour")) {
			if (cmds.length == 2) {
				try {
					long h = Long.parseLong(cmds[1]);
					Config.db.setUpdateHours(h);
					Config.showMsgBar("更新間隔時間修改為" + Config.db.getUpdateHours()
							+ "小時", "系統指令");
				} catch (Exception e) {
					Config.showMsgBar("參數錯誤:" + cmds[1], "系統指令");
					e.printStackTrace();
				}
			}
			return;
		}
		if (cmds[0].equals("showhour")) {
			Config.showMsgBar("間隔時間為" + Config.db.getUpdateHours() + "小時",
					"系統指令");
			return;
		}
		if (cmds[0].equals("showupdate")) {
			Config.showMsgBar("漫畫最後更新時間為" + Config.db.getUpdateDate(), "系統指令");
			return;
		}
		if (cmds[0].equals("reader")) {
			if (cmds.length == 3) {
				String w = cmds[1];
				String h = cmds[2];

				if (!Config.db.isValidReaderWH(w, h)) {
					Config.showMsgBar("漫畫圖片寬設定錯誤，寬:" + w, "，高:" + h + "系統指令");
					return;
				}
				Config.db.setReaderWH(w, h);
				Config.showMsgBar("漫畫圖片寬高已重設為，寬:" + w + "，高:" + h, "系統指令");
			} else {
				Config.db.setReaderWH("0", "0");
				Config.showMsgBar("漫畫圖片寬高已重設為自動縮放大小", "系統指令");
			}

			return;
		}
		if (cmds[0].equals("superman")) {
			if (cmds.length == 2) {
				Config.db.isAdmin = cmds[1].equalsIgnoreCase("true");
				Config.db.save();
				Config.showMsgBar("最大權限設定[" + Config.db.isAdmin + "]!!", "系統指令");
				return;
			}
		}
		Config.showMsgBar(Config.cmdList, "系統指令");
	}
}
