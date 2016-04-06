package net.xuite.blog.ray00000test.rdownloadcomic.service;

public class Command {

	public Command() {
	}

	public void sysCmd(String cmd) {
		String[] cmds = cmd.split("[ ]");
		if (cmds[0].equals("updatehour")) {
			if (cmds.length == 2) {
				try {
					long h = Long.parseLong(cmds[1]);
					RComicDownloader.get().getDB().setUpdateHours(h);
					Config.showMsgBar("更新間隔時間修改為"
							+ RComicDownloader.get().getDB().getUpdateHours()
							+ "小時", "系統指令");
				} catch (Exception e) {
					Config.showMsgBar("參數錯誤:" + cmds[1], "系統指令");
					e.printStackTrace();
				}
			}
			return;
		}
		if (cmds[0].equals("showhour")) {
			Config.showMsgBar("間隔時間為"
					+ RComicDownloader.get().getDB().getUpdateHours() + "小時",
					"系統指令");
			return;
		}
		if (cmds[0].equals("showupdate")) {
			Config.showMsgBar("漫畫最後更新時間為"
					+ RComicDownloader.get().getDB().getUpdateDate(), "系統指令");
			return;
		}
		if (cmds[0].equals("reader")) {
			if (cmds.length == 3) {
				String w = cmds[1];
				String h = cmds[2];

				if (!RComicDownloader.get().getDB().isValidReaderWH(w, h)) {
					Config.showMsgBar("漫畫圖片寬設定錯誤，寬:" + w, "，高:" + h + "系統指令");
					return;
				}
				RComicDownloader.get().getDB().setReaderWH(w, h);
				Config.showMsgBar("漫畫圖片寬高已重設為，寬:" + w + "，高:" + h, "系統指令");
			} else {
				RComicDownloader.get().getDB().setReaderWH("0", "0");
				Config.showMsgBar("漫畫圖片寬高已重設為自動縮放大小", "系統指令");
			}

			return;
		}
		if (cmds[0].equals("superman")) {
			if (cmds.length == 2) {
				RComicDownloader.get().getDB().isAdmin = cmds[1]
						.equalsIgnoreCase("true");
				RComicDownloader.get().getDB().save();
				Config.showMsgBar("最大權限設定["
						+ RComicDownloader.get().getDB().isAdmin + "]!!",
						"系統指令");
				return;
			}
		}
		Config.showMsgBar(Config.cmdList, "系統指令");
	}
}
