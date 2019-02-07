package rcomic.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import rcomic.control.ComicWrapper;
import rcomic.control.Comics;
import rcomic.control.RComic;
import rcomic.utils.ui.JDataTable;

import java.util.function.Consumer;

/**
 * 漫畫列表
 * 
 * @author Ray
 * 
 */
public class ComicList {
	/** 秀首頁所有漫畫列表 */
	private JDataTable<String> mAllComic;
	/** 最新漫畫 */
	private JDataTable<String> mNewComic;

	private Consumer<ComicWrapper> mOpenClmicListener;

	public void initialize() {
		setupAllComic();
		setupNewComic();
	}

	public JDataTable<String> getAll() {
		return mAllComic;
	}

	public JDataTable<String> getNew() {
		return mNewComic;
	}

	private void setupAllComic() {
		Comics comicList = RComic.get().getAllComics();
		mAllComic = new JDataTable<String>(false);
		mAllComic.addMultiColumnName(
				new String[] { RComic.get().getLang("Number"), RComic.get().getLang("ComicName"), "" });
		mAllComic.setReorderingAllowed(false);// 鎖住換欄位位置功能，會影嚮雙擊開列表功能
		comicList.getComics().forEach(this::refreshComicListCell);
		mAllComic.setRowHeight(40);
		mAllComic.getColumn(0).setMaxWidth(60);
		mAllComic.getColumn(2).setMaxWidth(20);
		mAllComic.setFont(RComic.get().getConfig().getComicListFont());

		mAllComic.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable table = (JTable) e.getSource();
					int row = table.getSelectedRow();
					int column = 2;
					Object obj = table.getValueAt(row, column);

					if (obj instanceof JLabel) {
						String comicId = (String) table.getValueAt(row, 0);

						if (RComic.get().existedFavorites(comicId)) {
							RComic.get().removeFromFavorites(comicId);
						} else {
							RComic.get().addToFavorites(comicId);
						}

						table.repaint();
					}
				}
			}
		});

		mAllComic.getJTable().setDefaultRenderer(JLabel.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof JLabel) {
					JLabel label = (JLabel) value;
					String comicId = (String) table.getValueAt(row, 0);

					setFavoritesState(label, comicId);

					return label;
				} else {
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			}
		});

		// 在table上增加雙擊開啟動畫集數列表功能
		mAllComic.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = mAllComic.getJTable().rowAtPoint(e.getPoint());
				String comicId = mAllComic.getJTable().getValueAt(row, 0).toString();
				ComicWrapper comic = RComic.get().searchAllById(comicId);

				showComicActList(comic);
			}
		});
	}

	private void setupNewComic() {
		Comics newComics = RComic.get().getNewComics();
		mNewComic = new JDataTable<String>(false);
		mNewComic.addMultiColumnName(
				new String[] { RComic.get().getLang("Number"), RComic.get().getLang("ComicNameEpisode") });
		newComics.getComics().forEach(comic -> {
			mNewComic.addRowData(new String[] { comic.getId(), comic.getNameWithNewestEpisode() });
		});
		mNewComic.setRowHeight(40);
		mNewComic.getColumn(0).setMaxWidth(60);
		mNewComic.setFont(RComic.get().getConfig().getComicListFont());
		mNewComic.getJTable().setToolTipText(RComic.get().getLang("PleaseDoubleClick"));
		// 在table上增加雙擊開啟動畫集數列表功能
		mNewComic.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = mNewComic.getJTable().rowAtPoint(e.getPoint());
				String comicId = mNewComic.getJTable().getValueAt(row, 0).toString();
				ComicWrapper comic = RComic.get().searchNewById(comicId);

				showComicActList(comic);
			}
		});
	}

	/**
	 * 漫畫集數列表
	 * 
	 * @param comic
	 */
	private void showComicActList(ComicWrapper comic) {
		RComic.get().getR8Comic().loadComicDetail(comic.get(), newComic -> {
			if (mOpenClmicListener != null) {
				mOpenClmicListener.accept(comic);
			}
		});
	}

	private void refreshComicListCell(ComicWrapper comic) {
		JLabel lab = new JLabel("", SwingConstants.CENTER);

		lab.setForeground(Color.RED);
		setFavoritesState(lab, comic.getId());
		mAllComic.addRowData(new Object[] { comic.getId(), comic.getName(), lab });
	}

	private void setFavoritesState(JLabel lab, String comicId) {
		if (RComic.get().existedFavorites(comicId)) {
			lab.setText("★");
		} else {
			lab.setText("☆");
		}
	}

	public void setOpenClmicListener(Consumer<ComicWrapper> listener) {
		mOpenClmicListener = listener;
	}

	public void refreshSearchResult(String keyword) {
		RComic.get().search(keyword, result -> {
			SwingUtilities.invokeLater(() -> {
				mAllComic.removeAll();
				result.forEach(ComicList.this::refreshComicListCell);
			});
		});
	}

}
