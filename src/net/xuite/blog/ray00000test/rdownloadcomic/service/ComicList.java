package net.xuite.blog.ray00000test.rdownloadcomic.service;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ray Lee 
 * Created on 2017/08/16
 */
public class ComicList {
	private List<ComicWrapper> mComics;
	
	public ComicList(List<ComicWrapper> serverComicList){
		mComics = serverComicList;
	}
	
	public ComicList(List<ComicWrapper> serverComicList, ArrayList<String[]> dbComicList){
		mComics = new ArrayList<ComicWrapper>();
		
		for(String [] dbcomic : dbComicList){
			for(ComicWrapper comic : serverComicList){
				if(comic.getId().equals(dbcomic[0])){
					mComics.add(comic);
				}
			}
		}
	}
	
	public boolean hasComic(String comicId){
		for(ComicWrapper comic : mComics){
			if(comic.getId().equals(comicId)){
				return true;
			}
		}
		return false;
	}
	
	public ComicWrapper getComicById(String comicId){
		for(ComicWrapper comic : mComics){
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
		return getIndexData(false);
	}

	/**
	 * 取得所有漫畫列表,讀取文字檔裡的漫畫資料
	 * 
	 * @return
	 */
	public String[][] getIndexData(boolean isShowNew) {
		String[][] list = new String[mComics.size()][2];
		ComicWrapper comic = null;
		
		for(int i = 0; i < mComics.size(); i++){
			comic = mComics.get(i);
			if(isShowNew){
				list[i] = new String[]{comic.getId(), comic.getName() + "["+comic.getNewestEpisode()+"]"};
			}else{
				list[i] = new String[]{comic.getId(), comic.getName()};
			}
			
		}
		return list;
	}
	
	public ComicWrapper getComic(int index){
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
}
