/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School Alessandro
 * Rigano (Program in Molecular Medicine) Caterina Strambio De Castillia
 * (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts: Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.mosaicOmegaFeaturePointTracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.pluginArchetypes.OmegaParticleTrackingPluginArchetype;
import edu.umassmed.omega.commons.pluginArchetypes.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.utilities.OperatingSystemEnum;
import edu.umassmed.omega.mosaicOmegaFeaturePointTracker.gui.MosaicOmegaFeaturePointTrackerPluginPanel;

public class MosaicOmegaFeaturePointTrackerPlugin extends OmegaParticleTrackingPluginArchetype implements
		OmegaDataDisplayerPluginInterface {
	
	public MosaicOmegaFeaturePointTrackerPlugin() {
		super(1);
	}
	
	public MosaicOmegaFeaturePointTrackerPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);
	}
	
	@Override
	public String getAlgorithmDescription() {
		return MosaicOmegaFeaturePointTrackerPluginConstants.PLUGIN_ALGO_DESC;
	}
	
	@Override
	public String getAlgorithmAuthors() {
		return MosaicOmegaFeaturePointTrackerPluginConstants.PLUGIN_AUTHORS;
	}
	
	@Override
	public String getAlgorithmVersion() {
		return MosaicOmegaFeaturePointTrackerPluginConstants.PLUGIN_VERSION;
	}
	
	@Override
	public Date getAlgorithmPublicationDate() {
		return MosaicOmegaFeaturePointTrackerPluginConstants.PLUGIN_PUBL;
	}
	
	@Override
	public String getName() {
		return MosaicOmegaFeaturePointTrackerPluginConstants.PLUGIN_NAME;
	}
	
	@Override
	public String getShortName() {
		return MosaicOmegaFeaturePointTrackerPluginConstants.PLUGIN_SNAME;
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
		final MosaicOmegaFeaturePointTrackerPluginPanel panel = new MosaicOmegaFeaturePointTrackerPluginPanel(parent, this,
				this.getGateway(), this.getLoadedImages(), null, null, index);
		// TODO null & null are for loadedAnalysisRuns and OrphanedAnalysis,
		// need to be fixed
		return panel;
	}
	
	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final GenericPluginPanel panel : this.getPanels()) {
			final MosaicOmegaFeaturePointTrackerPluginPanel specificPanel = (MosaicOmegaFeaturePointTrackerPluginPanel) panel;
			specificPanel.setGateway(gateway);
		}
	}
	
	@Override
	public void updateDisplayedData() {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final MosaicOmegaFeaturePointTrackerPluginPanel specificPanel = (MosaicOmegaFeaturePointTrackerPluginPanel) panel;
			specificPanel.updateTrees(this.getLoadedImages());
		}
	}
	
	@Override
	public void selectImage(final OmegaImage image) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final MosaicOmegaFeaturePointTrackerPluginPanel specificPanel = (MosaicOmegaFeaturePointTrackerPluginPanel) panel;
			specificPanel.selectImage(image);
		}
	}
	
	@Override
	public String getDescription() {
		return MosaicOmegaFeaturePointTrackerPluginConstants.PLUGIN_DESC;
	}
	
	@Override
	public String getReference() {
		return "TBD";
	}
}
