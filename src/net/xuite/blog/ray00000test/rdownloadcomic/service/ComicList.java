package net.xuite.blog.ray00000test.rdownloadcomic.service;

import java.util.ArrayList;
import java.util.List;

import net.xuite.blog.ray00000test.library.comicsdk.Comic;

/**
 * 
 * @author Ray Lee 
 * Created on 2017/08/16
 */
public class ComicList {
	private List<Comic> mComics;
	
	public ComicList(List<Comic> serverComicList){
		mComics = serverComicList;
	}
	
	public ComicList(List<Comic> serverComicList, ArrayList<String[]> dbComicList){
		mComics = new ArrayList<Comic>();
		
		for(String [] dbcomic : dbComicList){
			for(Comic comic : serverComicList){
				if(comic.getId().equals(dbcomic[0])){
					mComics.add(comic);
				}
			}
		}
	}
	
	public boolean hasComic(String comicId){
		for(Comic comic : mComics){
			if(comic.getId().equals(comicId)){
				return true;
			}
		}
		return false;
	}
	
	public Comic getComicById(String comicId){
		for(Comic comic : mComics){
			if(comic.getId().equals(comicId)){
				return comic;
			}
		}
		return null;
	}

	/**
	 * 取得所有漫畫列表,讀取文字檔裡的漫畫資料
	 * 
	 * @return
	 */
	public String[][] getIndexData() {
		String[][] list = new String[mComics.size()][2];
		Comic comic = null;
		
		for(int i = 0; i < mComics.size(); i++){
			comic = mComics.get(i);
			list[i] = new String[]{comic.getId(), comic.getName()};
		}
		return list;
	}
	
	public Comic getComic(int index){
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

		for (Comic comic : mComics) {
			String cartoonName = comic.getName();
			
			if (cartoonName.indexOf(findName) != -1) {
				String dataName = comic.getId() + "|" + comic.getName();
				catList.add(dataName);
			}
		}
		return catList;
	}
}
