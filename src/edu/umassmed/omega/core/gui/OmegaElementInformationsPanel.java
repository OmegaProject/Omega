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
package edu.umassmed.omega.core.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;

public class OmegaElementInformationsPanel extends GenericPanel {

	private static final long serialVersionUID = -8599077833612345455L;

	private JLabel class_label, id_label, name_label;

	public OmegaElementInformationsPanel(final RootPaneContainer parent) {
		super(parent);

		this.setLayout(new GridLayout(3, 1));

		this.setBorder(new TitledBorder("Element information"));

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		this.class_label = new JLabel();
		this.add(this.class_label);

		this.id_label = new JLabel();
		this.id_label.setText("No element isSelected");
		this.add(this.id_label);

		this.name_label = new JLabel();
		this.add(this.name_label);
	}

	private void addListeners() {
		// TODO Auto-generated method stub

	}

	public void update(final OmegaElement element) {
		if (element != null) {
			this.class_label.setText(element.getClass().getSimpleName());
			this.id_label.setText(Long.toString(element.getElementID()));
			if (element instanceof OmegaNamedElement) {
				this.name_label
				        .setText(((OmegaNamedElement) element).getName());
			}
		} else {
			this.class_label.setText("");
			this.id_label.setText("No element isSelected");
			this.name_label.setText("");
		}
		this.repaint();
	}
}
