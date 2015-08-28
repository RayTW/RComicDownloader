package JDownLoadComic.util;

import java.io.File;

import JDownLoadComic.Config;

public class WorkaroundUtility {
	// 存放所有集數的資料夾，例如" 海賊王"，前後若有半形空白trim掉
	public static void replaceAllSpaceCharComic() {
		File flist = new File("./" + Config.defaultSavePath);

		for (File f : flist.listFiles()) {
			if (f.isDirectory() && f.getName().indexOf(' ') != -1) {
				f.renameTo(new File(f.getParentFile().getPath(), f.getName()
						.replaceAll(" ", "")));
			}
		}
	}

	// 存放一集或1話的資料夾，例如" 第一卷"，前後若有半形空白trim掉
	public static void replaceAllSpaceCharAct() {
		File flist = new File("./" + Config.defaultSavePath);

		for (File comic : flist.listFiles()) {
			for (File f : comic.listFiles()) {
				if (f.isDirectory() && f.getName().indexOf(' ') != -1) {
					f.renameTo(new File(f.getParentFile().getPath(), f
							.getName().replaceAll(" ", "")));
				}
			}
		}
	}
}
