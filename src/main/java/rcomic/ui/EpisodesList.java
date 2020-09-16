package rcomic.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import raytw.sdk.comic.comicsdk.Episode;
import rcomic.control.ComicWrapper;
import rcomic.control.RComic;
import rcomic.utils.ui.JDataTable;

/**
 * 單本漫畫所有集數列表
 *
 * @author Ray
 */
public class EpisodesList extends JPanel {
  private static final long serialVersionUID = 5167725310966938209L;

  private JDataTable<String> table; // 秀首頁漫畫集數列表
  /** 上面工具列 */
  private JLabel name;

  private JLabel author;
  private JLabel modifyDate;
  private JTextArea comicIntroduction;
  private ComicWrapper comic;

  public EpisodesList() {
    initialize();
  }

  /** 初始化 */
  public void initialize() {
    setLayout(new BorderLayout());
    table = new JDataTable<String>(false);
    JPanel northPanel = new JPanel();
    JPanel cneterPanel = new JPanel(new GridLayout(2, 0));

    table.setRowHeight(40);
    table.setFont(RComic.get().getConfig().getComicListFont());
    table.addMultiColumnName(new String[] {RComic.get().getLang("Episode")});
    table.setColumnWidth(0, 100);
    table.getJTable().setToolTipText(RComic.get().getLang("DoubleClick"));

    add(northPanel, BorderLayout.NORTH);

    comicIntroduction = new JTextArea();
    comicIntroduction.setWrapStyleWord(true);
    comicIntroduction.setLineWrap(true);
    comicIntroduction.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(comicIntroduction);
    scrollPane.setBounds(10, 60, 780, 500);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    cneterPanel.add(scrollPane);
    cneterPanel.add(table.toJScrollPane());

    add(cneterPanel, BorderLayout.CENTER);

    JPanel bookDataPanel = new JPanel(new GridLayout(3, 1, 10, 0));
    author = new JLabel();
    modifyDate = new JLabel();
    name = new JLabel();
    bookDataPanel.add(name);
    bookDataPanel.add(author);
    bookDataPanel.add(modifyDate);

    northPanel.add(bookDataPanel);

    // 在table上增加雙擊開啟動畫集數列表功能
    table
        .getJTable()
        .addMouseListener(
            new MouseAdapter() {
              @Override
              public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 雙擊開啓漫畫
                  int row = table.getJTable().rowAtPoint(e.getPoint());
                  Episode episode = comic.getEpisodes().get(row);

                  RComic.get()
                      .loadEpisodesImagesPagesUrl(
                          episode,
                          result -> {
                            SwingUtilities.invokeLater(
                                () -> {
                                  JDialog loading = RComic.get().newLoadingDialog();
                                  loading.toFront();

                                  RComic.get()
                                      .addTask(
                                          () -> {
                                            // 用瀏覽器開啟漫畫
                                            RComic.get().openComic(result);
                                            SwingUtilities.invokeLater(() -> loading.dispose());
                                          });
                                });
                          });
                }
              }
            });
  }

  /**
   * 初始化整套漫畫連結資料、總集數等等..
   *
   * @param comic
   */
  public void setComic(ComicWrapper comic) {
    this.comic = comic;
    table.removeAll();
    comic.getEpisodesName(table::addRowData);
    name.setText(comic.getName());
    author.setText(RComic.get().getLang("Author") + comic.getAuthor());
    modifyDate.setText(RComic.get().getLang("ModifyDate") + comic.getLatestUpdateDateTime());
    comicIntroduction.setText(comic.getDescription());
  }

  public boolean isDownloadedList() {
    return (table.getDataCount() > 0);
  }
}
