package unused;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class GenericBrowserTable extends JTable {

	private static final long serialVersionUID = 8328693780121477655L;

	@Override
	public Component prepareRenderer(final TableCellRenderer renderer,
	        final int row, final int column) {
		final Component component = super
		        .prepareRenderer(renderer, row, column);
		final int rendererWidth = component.getPreferredSize().width;
		final TableColumn tableColumn = this.getColumnModel().getColumn(column);
		tableColumn.setPreferredWidth(Math.max(
		        rendererWidth + this.getIntercellSpacing().width,
		        tableColumn.getPreferredWidth()));
		return component;
	}

	public void resizeContainer() {
		final Dimension dim = this.getPreferredSize();
		if (this.getParent() != null) {
			this.getParent().setMinimumSize(dim);
			this.getParent().setPreferredSize(dim);
			// this.getParent().setPreferredSize(dim);
			this.getParent().repaint();
		}
	}
}
