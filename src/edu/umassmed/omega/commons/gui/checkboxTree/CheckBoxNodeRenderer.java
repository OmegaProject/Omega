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
package edu.umassmed.omega.commons.gui.checkboxTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class CheckBoxNodeRenderer extends TriStateCheckBox implements
        TreeCellRenderer {

	private static final long serialVersionUID = 5667252206708991028L;

	private final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	private final JPanel panel = new JPanel(new BorderLayout());

	public CheckBoxNodeRenderer() {
		super();
		final String uiName = this.getUI().getClass().getName();
		if (uiName.contains("Synth")
		        && System.getProperty("java.version").startsWith("1.7.0")) {
			System.out
			        .println("XXX: FocusBorder bug?, JDK 1.7.0, Nimbus start LnF");
			this.renderer.setBackgroundSelectionColor(new Color(0, 0, 0, 0));
		}
		this.panel.setFocusable(false);
		this.panel.setRequestFocusEnabled(false);
		this.panel.setOpaque(false);
		this.panel.add(this, BorderLayout.WEST);
		this.setOpaque(false);
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree,
	        final Object value, final boolean selected, final boolean expanded,
	        final boolean leaf, final int row, final boolean hasFocus) {
		final JLabel l = (JLabel) this.renderer.getTreeCellRendererComponent(
		        tree, value, selected, expanded, leaf, row, hasFocus);
		l.setFont(tree.getFont());
		if (value instanceof DefaultMutableTreeNode) {
			this.setEnabled(tree.isEnabled());
			this.setFont(tree.getFont());
			final Object userObject = ((DefaultMutableTreeNode) value)
			        .getUserObject();
			if (userObject instanceof CheckBoxNode) {
				final CheckBoxNode node = (CheckBoxNode) userObject;
				if (node.getStatus() == CheckBoxStatus.INDETERMINATE) {
					this.setIcon(new IndeterminateIcon());
				} else {
					this.setIcon(null);
				}
				l.setText(node.getLabel());
				this.setSelected(node.getStatus() == CheckBoxStatus.SELECTED);
			}
			// panel.add(this, BorderLayout.WEST);
			this.panel.add(l);
			return this.panel;
		}
		return l;
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (this.panel != null) {
			// panel.removeAll(); //??? Change to Nimbus LnF, JDK 1.6.0
			this.panel.updateUI();
			// panel.add(this, BorderLayout.WEST);
		}
		this.setName("Tree.cellRenderer");
		// ???#1: JDK 1.6.0 bug??? @see 1.7.0 DefaultTreeCellRenderer#updateUI()
		// if(System.getProperty("java.version").startsWith("1.6.0")) {
		// renderer = new DefaultTreeCellRenderer();
		// }
	}
}