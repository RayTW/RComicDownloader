package JDownLoadComic;

import java.io.File;

public class FolderManager {

	public FolderManager() {
		// TODO Auto-generated constructor stub
	}

	public static void createFolder(String path) {
		File tmpFolder = new File(path);

		if (!tmpFolder.exists()) {
			tmpFolder.mkdirs();
		}
	}

	public static void deleteFolder(String path) {
		File tmpFolder = new File(path);
		deleteFolder(tmpFolder);
	}

	public static void deleteFolder(File path) {
		if (!path.exists()) {
			return;
		}
		if (path.isFile()) {
			path.delete();
			return;
		}
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteFolder(files[i]);
		}
		path.delete();
	}
}
