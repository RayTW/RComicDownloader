package rcomic.utils.ui;

import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author ray
 *
 * @param <T>
 */
public class RTableModel<T extends List<?>> extends DefaultTableModel {
	private static final long serialVersionUID = -6800245208090134048L;

	public RTableModel(Vector<String> data, Vector<String> columnNames) {
		super(data, columnNames);
	}

	public RTableModel(T data, T columnNames) {
		Vector<String> d = new Vector<>();
		Vector<String> c = new Vector<>();

		data.forEach(o -> d.add(o.toString()));
		columnNames.forEach(o -> c.add(o.toString()));

		setDataVector(d, c);
	}

	public RTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	public <E> void addRow(List<E> data) {
		Vector<String> d = new Vector<>();

		data.forEach(o -> d.add(o.toString()));

		super.addRow(d);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Object obj = getValueAt(0, columnIndex);

		if (obj != null) {
			return obj.getClass();
		}
		return super.getColumnClass(columnIndex);
	}
}
