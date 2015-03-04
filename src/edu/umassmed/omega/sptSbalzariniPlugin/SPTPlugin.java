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
package edu.umassmed.omega.sptSbalzariniPlugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.plugins.OmegaParticleTrackingPlugin;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.utilities.OperatingSystemEnum;
import edu.umassmed.omega.data.coreElements.OmegaPerson;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.sptSbalzariniPlugin.gui.SPTPluginPanel;

public class SPTPlugin extends OmegaParticleTrackingPlugin implements
        OmegaDataDisplayerPluginInterface {

	public SPTPlugin() {
		super(1);
	}

	public SPTPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);
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
		return new OmegaPerson("Ivo", "Sbalzarini");
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
	public String getShortName() {
		return "SPT";
	}

	@Override
	public List<OperatingSystemEnum> getSupportedPlatforms() {
		final List<OperatingSystemEnum> supportedPlatforms = new ArrayList<OperatingSystemEnum>();
		supportedPlatforms.add(OperatingSystemEnum.WIN);
		return supportedPlatforms;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws OmegaCoreExceptionPluginMissingData {
		final SPTPluginPanel panel = new SPTPluginPanel(parent, this,
		        this.getGateway(), this.getLoadedImages(), index);
		return panel;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final GenericPluginPanel panel : this.getPanels()) {
			final SPTPluginPanel specificPanel = (SPTPluginPanel) panel;
			specificPanel.setGateway(gateway);
		}
	}

	@Override
	public void updateDisplayedData() {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final SPTPluginPanel specificPanel = (SPTPluginPanel) panel;
			specificPanel.updateTrees(this.getLoadedImages());
		}
	}

	@Override
	public String getDescription() {
		return "Single particle tracker optimized for WINDOWS";
	}
}
