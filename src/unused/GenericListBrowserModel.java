package unused;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class GenericListBrowserModel extends AbstractTableModel {

	private static final long serialVersionUID = -2618857102779599857L;

	private final GenericBrowserTable table;
	private final List<GenericListBrowserItem> items;

	public GenericListBrowserModel(final GenericBrowserTable table) {
		this.table = table;
		this.items = new ArrayList<GenericListBrowserItem>();
		// this.update(elements);
	}

	public GenericListBrowserItem getSelectedItem() {
		final int index = this.table.getSelectedRow();
		return this.getItem(index);
	}

	public GenericListBrowserItem getItem(final int index) {
		if ((index < 0) || (index > this.getRowCount()))
			return null;
		return this.items.get(index);
	}

	public void update(final Map<Long, String> elements) {
		this.update(elements, false, false);
	}

	public void update(final Map<Long, String> elements,
	        final boolean isSelected) {
		this.update(elements, isSelected, false);
	}

	public void update(final Map<Long, String> elements,
	        final boolean isSelected, final boolean selectAllItems) {
		this.items.clear();
		for (final Long id : elements.keySet()) {
			final String name = elements.get(id);
			final GenericListBrowserItem item = new GenericListBrowserItem(id,
			        name);
			if (selectAllItems && (isSelected != item.isSelected())) {
				item.setSelection(isSelected);
			}
			this.items.add(item);
		}
		this.fireTableDataChanged();
		this.resize();
	}

	private void resize() {
		for (int c = 0; c < this.getColumnCount(); c++) {
			for (int r = 0; r < this.getRowCount(); r++) {
				this.table.prepareRenderer(this.table.getCellRenderer(r, c), r,
				        c);
			}
		}
		this.table.resizeContainer();
	}

	@Override
	public String getColumnName(final int column) {
		switch (column) {
		case 0:
			return "ID";
		case 1:
			return "Name";
		case 2:
			return "";
		default:
			return "ERROR";
		}
	}

	@Override
	public int getRowCount() {
		return this.items.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		if (columnIndex == 2)
			return true;
		return false;
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		case 2:
			return Boolean.class;
		default:
			return Object.class;
		}
	}

	public void checkId(final Long id) {
		for (int index = 0; index < this.items.size(); index++) {
			final GenericListBrowserItem item = this.items.get(index);
			if (item.getId() == id) {
				item.select();
				this.fireTableRowsUpdated(index, index);
			}
		}
	}

	public void selectId(final Long id) {
		for (int index = 0; index < this.items.size(); index++) {
			final GenericListBrowserItem item = this.items.get(index);
			if (item.getId() == id) {
				this.table.setRowSelectionInterval(index, index);
			}
		}
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		Object val = "ERROR";
		switch (columnIndex) {
		case 0:
			val = Long.toString(this.items.get(rowIndex).getId());
			break;
		case 1:
			val = this.items.get(rowIndex).getName();
			break;
		case 2:
			val = this.items.get(rowIndex).isSelected();
			break;
		default:
			break;
		}

		return val;
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex,
	        final int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 2) {
			final boolean tof = (boolean) aValue;
			if (tof) {
				this.items.get(rowIndex).select();
			} else {
				this.items.get(rowIndex).deselect();
			}
		}
	}
}
