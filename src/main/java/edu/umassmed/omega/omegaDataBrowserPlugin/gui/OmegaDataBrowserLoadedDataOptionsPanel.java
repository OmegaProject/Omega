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
package main.java.edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.RootPaneContainer;

import main.java.edu.umassmed.omega.commons.gui.GenericPanel;
import main.java.edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserConstants;

public class OmegaDataBrowserLoadedDataOptionsPanel extends GenericPanel {

	private static final long serialVersionUID = -8855766053467978179L;

	private JCheckBox autoSelectRelatives;

	public OmegaDataBrowserLoadedDataOptionsPanel(final RootPaneContainer parent) {
		super(parent);

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		this.createAndAddWidgets();
	}

	public void createAndAddWidgets() {
		this.autoSelectRelatives = new JCheckBox(
		        OmegaDataBrowserConstants.TREE_AUTO_SELECTION_OPTION);

		this.add(this.autoSelectRelatives);
	}

	public boolean isAutoSelectRelatives() {
		return this.autoSelectRelatives.isSelected();
	}
}
