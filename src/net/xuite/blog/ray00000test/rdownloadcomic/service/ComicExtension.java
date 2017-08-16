package net.xuite.blog.ray00000test.rdownloadcomic.service;

import java.util.List;

import javax.swing.Icon;

import net.xuite.blog.ray00000test.library.comicsdk.Comic;
import net.xuite.blog.ray00000test.library.comicsdk.Episode;

public class ComicExtension {
	private Comic mComic;
	
	public ComicExtension(Comic comic){
		mComic = comic;
	}

	public Comic get(){
		return mComic;
	}
	
	public boolean isImgDownloadOK(){
		return false;
	}
	
	public Icon getIcon(){
		return null;
	}
	
	public String [][] getActDataList(){
		List<Episode> episodes = mComic.getEpisodes();
		String[][] list = new String[episodes.size()][2];
		Episode episode = null;
		
		for(int i = 0; i < episodes.size(); i++){
			episode = episodes.get(i);
			list[i] = new String[]{episode.getName(), "NO"};
		}
		return list;
	}
}
