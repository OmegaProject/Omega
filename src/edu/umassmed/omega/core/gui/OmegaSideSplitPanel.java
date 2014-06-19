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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JSplitPane;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.gui.GenericSplitPane;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public class OmegaSideSplitPanel extends GenericSplitPane {

	private static final long serialVersionUID = -4565126277733287950L;

	private OmegaElementImagePanel imagePanel;
	private OmegaElementInformationsPanel infoPanel;

	// private JDesktopPane desktopPane;

	public OmegaSideSplitPanel(final RootPaneContainer parent) {
		super(parent, JSplitPane.VERTICAL_SPLIT, true);
		this.setDividerLocation(0.8);

		this.createAndAddWidgets();

		this.addListeners();
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.imagePanel.updateParentContainer(parent);
		this.infoPanel.updateParentContainer(parent);
	}

	private void createAndAddWidgets() {
		this.imagePanel = new OmegaElementImagePanel(this.getParentContainer());

		this.setLeftComponent(this.imagePanel);

		this.infoPanel = new OmegaElementInformationsPanel(
		        this.getParentContainer());

		this.setRightComponent(this.infoPanel);
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaSideSplitPanel.this.setDividerLocation(0.8);
			}
		});
	}

	public void update(final OmegaElement element,
	        final List<OmegaAnalysisRun> loadedAnalysisRuns,
	        final OmegaGateway gateway) {
		this.imagePanel.update(element, loadedAnalysisRuns, gateway);
		this.infoPanel.update(element);
	}
}
