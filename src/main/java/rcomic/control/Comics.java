package rcomic.control;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * 
 * @author Ray Lee Created on 2017/08/16
 */
public class Comics {
	private List<ComicWrapper> mComics;

	private Comics() {
		mComics = new CopyOnWriteArrayList<ComicWrapper>();
	}

	public Comics(List<ComicWrapper> serverComicList) {
		this();
		mComics = serverComicList;
	}

	public Comics(List<ComicWrapper> serverComicList, List<String> comidIds) {
		this();

		comidIds.forEach(id -> serverComicList.forEach(comic -> {
			if (comic.getId().equals(id)) {
				mComics.add(comic);
			}
		}));
	}

	public boolean hasComic(String comicId) {
		for (ComicWrapper comic : mComics) {
			if (comic.getId().equals(comicId)) {
				return true;
			}
		}
		return false;
	}

	public ComicWrapper getComicById(String comicId) {
		for (ComicWrapper comic : mComics) {
			if (comic.getId().equals(comicId)) {
				return comic;
			}
		}
		return null;
	}

	public List<ComicWrapper> getComics() {
		return mComics;
	}

	public ComicWrapper getComic(int index) {
		return mComics.get(index);
	}

	/**
	 * 取出所有符合搜尋條件的資料
	 * 
	 * @param findName要搜尋的漫畫名稱
	 * @return
	 */
	public ArrayList<String> findCatroonToArrayList(String findName) {
		if (findName.trim().isEmpty()) {
			return null;
		}
		ArrayList<String> catList = new ArrayList<String>();

		for (ComicWrapper comic : mComics) {
			String cartoonName = comic.getName();

			if (cartoonName.indexOf(findName) != -1) {
				String dataName = comic.getId() + "|" + comic.getName();
				catList.add(dataName);
			}
		}
		return catList;
	}

	private static JDialog showLoading() {
		JDialog jDialog = new JDialog();
		jDialog.setLayout(new GridBagLayout());
		jDialog.add(new JLabel(RComic.get().getConfig().getLangValue("Loading")));
		jDialog.setMaximumSize(new Dimension(150, 50));
		jDialog.setResizable(false);
		jDialog.setModal(false);
		jDialog.setUndecorated(true);
		jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		jDialog.setLocationRelativeTo(null);
		jDialog.setVisible(true);
		jDialog.pack();

		return jDialog;
	}
}
