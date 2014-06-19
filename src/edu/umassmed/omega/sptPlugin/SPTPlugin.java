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
package edu.umassmed.omega.sptPlugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.OmegaParticleTrackingPlugin;
import edu.umassmed.omega.commons.exceptions.OmegaMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.sptPlugin.gui.SPTPluginPanel;

public class SPTPlugin extends OmegaParticleTrackingPlugin implements
        OmegaDataDisplayerPluginInterface {

	private final List<SPTPluginPanel> panels;

	public SPTPlugin() {
		super(1);

		this.panels = new ArrayList<SPTPluginPanel>();
	}

	public SPTPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);

		this.panels = new ArrayList<SPTPluginPanel>();
	}

	@Override
	public String getAlgorithmDescription() {
		return "Algorithm desc";
	}

	@Override
	public String getAlgorithmName() {
		return "Single particle tracking by Ivo Sbalzarini";
	}

	@Override
	public OmegaPerson getAlgorithmAuthor() {
		return new OmegaPerson(UUID.randomUUID().getMostSignificantBits(),
		        "Ivo", "Sbalzarini");
	}

	@Override
	public Double getAlgorithmVersion() {
		return 1.0;
	}

	@Override
	public Date getAlgorithmPublicationDate() {
		return new GregorianCalendar(1996, 4, 7).getTime();
	}

	@Override
	public String getName() {
		return "Single Particle Tracking";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws OmegaMissingData {
		final SPTPluginPanel panel = new SPTPluginPanel(parent, this,
		        this.getGateway(), this.getLoadedImages(), index);
		this.panels.add(panel);
		return panel;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final SPTPluginPanel panel : this.panels) {
			panel.setGateway(gateway);
		}
	}

	@Override
	public void updateDisplayedData() {
		for (final SPTPluginPanel panel : this.panels) {
			panel.updateTrees(this.getLoadedImages());
		}
	}

}
