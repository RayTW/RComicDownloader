package rcomic.control;

import java.util.List;
import java.util.function.Consumer;

import raytw.sdk.comic.comicsdk.Comic;
import raytw.sdk.comic.comicsdk.Episode;
import rcomic.utils.UnicodeUtility;

/**
 * 擴充Comic類別的功能
 *
 * @author ray
 */
public class ComicWrapper extends Comic {
  private Comic comic;

  public ComicWrapper(Comic comic) {
    this.comic = comic;
  }

  @Override
  public String getAuthor() {
    return UnicodeUtility.unicodeToChineseAll(comic.getAuthor());
  }

  @Override
  public String getDescription() {
    String description = comic.getDescription();

    if (description != null) {
      return UnicodeUtility.unicodeToChineseAll(description);
    }
    return null;
  }

  @Override
  public List<Episode> getEpisodes() {
    return comic.getEpisodes();
  }

  @Override
  public String getIconUrl() {
    return comic.getIconUrl();
  }

  @Override
  public String getId() {
    return comic.getId();
  }

  @Override
  public String getLatestUpdateDateTime() {
    return comic.getLatestUpdateDateTime();
  }

  public String getNameWithNewestEpisode() {
    return getName() + "[" + getNewestEpisode() + "]";
  }

  @Override
  public String getName() {
    return UnicodeUtility.unicodeToChineseAll(comic.getName());
  }

  @Override
  public String getNewestEpisode() {
    return comic.getNewestEpisode();
  }

  @Override
  public String getSmallIconUrl() {
    return comic.getSmallIconUrl();
  }

  @Override
  public void setAuthor(String author) {
    comic.setAuthor(author);
  }

  @Override
  public void setDescription(String description) {
    comic.setDescription(description);
  }

  @Override
  public void setEpisodes(List<Episode> episodes) {
    comic.setEpisodes(episodes);
  }

  @Override
  public void setIconUrl(String iconUrl) {
    comic.setIconUrl(iconUrl);
  }

  @Override
  public void setId(String id) {
    comic.setId(id);
  }

  @Override
  public void setLatestUpdateDateTime(String latestUpdateDateTime) {
    comic.setLatestUpdateDateTime(latestUpdateDateTime);
  }

  @Override
  public void setName(String name) {
    comic.setName(name);
  }

  @Override
  public void setNewestEpisode(String newestEpisode) {
    comic.setNewestEpisode(newestEpisode);
  }

  @Override
  public void setSmallIconUrl(String smallIconUrl) {
    comic.setSmallIconUrl(smallIconUrl);
  }

  public Comic get() {
    return comic;
  }

  public void getEpisodesName(Consumer<String[]> consumer) {
    comic
        .getEpisodes()
        .forEach(
            episode -> {
              if (consumer != null) {
                consumer.accept(new String[] {episode.getName()});
              }
            });
  }
}
