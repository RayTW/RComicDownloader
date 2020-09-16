package rcomic.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rcomic.control.ComicWrapper;
import rcomic.control.RComic;
import rcomic.utils.ui.JDataTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 漫畫列表
 * 
 * @author Ray
 * 
 */
public class ComicList {
	/** 秀首頁所有漫畫列表 */
	private JDataTable<String> allComic;
	/** 最新漫畫 */
	private JDataTable<String> newComic;

	private Consumer<ComicWrapper> openClmicListener;

	public void initialize() {
		setupAllComic();
		setupNewComic();
	}

	public JDataTable<String> getAll() {
		return allComic;
	}

	public JDataTable<String> getNew() {
		return newComic;
	}

	private void setupAllComic() {
		List<ComicWrapper> comicList = RComic.get().getAllComics();
		allComic = new JDataTable<String>(false);
		allComic.addMultiColumnName(new String[] { RComic.get().getLang("Number"), RComic.get().getLang("ComicName"),
				RComic.get().getLang("Favorites") });
		allComic.setReorderingAllowed(false);// 鎖住換欄位位置功能，會影嚮雙擊開列表功能
		comicList.forEach(this::refreshComicListCell);
		allComic.setRowHeight(40);
		allComic.getColumn(0).setMaxWidth(60);
		allComic.getColumn(2).setMaxWidth(60);
		allComic.setFont(RComic.get().getConfig().getComicListFont());
		allComic.getJTable().setAutoCreateRowSorter(true);

		TableRowSorter<TableModel> sorter = new TableRowSorter<>(allComic.getJTable().getModel());
		allComic.getJTable().setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();

		allComic.getJTable().addMouseListener(new MouseAdapter() {
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

		allComic.getJTable().setDefaultRenderer(JLabel.class, new DefaultTableCellRenderer() {
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

		// 在table上增加單擊開啟動畫集數列表功能
		allComic.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = allComic.getJTable().rowAtPoint(e.getPoint());
				String comicId = allComic.getJTable().getValueAt(row, 0).toString();
				ComicWrapper comic = RComic.get().searchAllById(comicId);

				showComicActList(comic);
			}
		});

		int columnIndexToSort = 2;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);

		// 檢查若有收藏漫畫時，將已收藏的漫畫排到列表最上面
		if (!RComic.get().getFavorites().isEmpty()) {
			sorter.sort();
		}
	}

	private void setupNewComic() {
		List<ComicWrapper> newComics = RComic.get().getNewComics();
		newComic = new JDataTable<String>(false);
		newComic.addMultiColumnName(
				new String[] { RComic.get().getLang("Number"), RComic.get().getLang("ComicNameEpisode") });
		newComics.forEach(comic -> {
			newComic.addRowData(new String[] { comic.getId(), comic.getNameWithNewestEpisode() });
		});
		newComic.setRowHeight(40);
		newComic.getColumn(0).setMaxWidth(60);
		newComic.setFont(RComic.get().getConfig().getComicListFont());
		newComic.getJTable().setToolTipText(RComic.get().getLang("PleaseDoubleClick"));
		// 在table上增加單擊開啟動畫集數列表功能
		newComic.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = newComic.getJTable().rowAtPoint(e.getPoint());
				String comicId = newComic.getJTable().getValueAt(row, 0).toString();
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
			if (openClmicListener != null) {
				openClmicListener.accept(comic);
			}
		});
	}

	private void refreshComicListCell(ComicWrapper comic) {
		JLabel lab = new JLabel("", SwingConstants.CENTER);

		lab.setForeground(Color.RED);
		setFavoritesState(lab, comic.getId());
		allComic.addRowData(new Object[] { comic.getId(), comic.getName(), lab });
	}

	private void setFavoritesState(JLabel lab, String comicId) {
		if (RComic.get().existedFavorites(comicId)) {
			lab.setText("★");
		} else {
			lab.setText("☆");
		}
	}

	public void setOpenClmicListener(Consumer<ComicWrapper> listener) {
		openClmicListener = listener;
	}

	public void refreshSearchResult(String keyword) {
		RComic.get().search(keyword, result -> {
			SwingUtilities.invokeLater(() -> {
				allComic.removeAll();
				result.forEach(ComicList.this::refreshComicListCell);
			});
		});
	}

}
