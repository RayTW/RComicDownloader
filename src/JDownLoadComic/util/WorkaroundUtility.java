package JDownLoadComic.util;

import java.io.File;

import JDownLoadComic.Config;

public class WorkaroundUtility {
	// 存放所有集數的資料夾，例如" 海賊王"，前後若有半形空白trim掉
	public static void replaceAllSpaceCharComic() {
		File[] flist = new File("./" + Config.defaultSavePath).listFiles();
		if (flist == null) {
			return;
		}

		for (File f : flist) {
			if (f.isDirectory() && f.getName().indexOf(' ') != -1) {
				f.renameTo(new File(f.getParentFile().getPath(), f.getName()
						.replaceAll(" ", "")));
			}
		}
	}

	// 存放一集或1話的資料夾，例如" 第一卷"，前後若有半形空白trim掉
	public static void replaceAllSpaceCharAct() {
		File[] flist = new File("./" + Config.defaultSavePath).listFiles();
		if (flist == null) {
			return;
		}

		File[] comicList = null;

		for (File comic : flist) {
			comicList = comic.listFiles();
			if (comicList == null) {
				continue;
			}
			for (File f : comicList) {
				if (f.isDirectory() && f.getName().indexOf(' ') != -1) {
					f.renameTo(new File(f.getParentFile().getPath(), f
							.getName().replaceAll(" ", "")));
				}
			}
		}
	}
}
