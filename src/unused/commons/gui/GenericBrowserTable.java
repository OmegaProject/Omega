/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package unused.commons.gui;

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
