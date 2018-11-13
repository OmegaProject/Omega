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
package edu.umassmed.omega.mosaicFeaturePointDetectionPlugin;

import java.util.Date;
import java.util.List;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.pluginArchetypes.OmegaParticleTrackingPluginArchetype;
import edu.umassmed.omega.commons.pluginArchetypes.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.pluginArchetypes.interfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.pluginArchetypes.interfaces.OmegaSelectParticleDetectionRunPluginInterface;
import edu.umassmed.omega.mosaicFeaturePointDetectionPlugin.gui.MosaicFeaturePointDetectionPluginPanel;

public class MosaicFeaturePointDetectionPlugin extends OmegaParticleTrackingPluginArchetype implements
		OmegaDataDisplayerPluginInterface,
		OmegaLoadedAnalysisConsumerPluginInterface,
		OmegaSelectParticleDetectionRunPluginInterface {
	
	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	
	public MosaicFeaturePointDetectionPlugin() {
		super(1);
	}
	
	public MosaicFeaturePointDetectionPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);
		
		this.loadedAnalysisRuns = null;
	}
	
	@Override
	public String getAlgorithmDescription() {
		return MosaicFeaturePointDetectionPluginConstants.PLUGIN_ALGO_DESC;
	}
	
	@Override
	public String getAlgorithmAuthors() {
		return MosaicFeaturePointDetectionPluginConstants.PLUGIN_AUTHORS;
	}
	
	@Override
	public String getAlgorithmVersion() {
		return MosaicFeaturePointDetectionPluginConstants.PLUGIN_VERSION;
	}
	
	@Override
	public Date getAlgorithmPublicationDate() {
		return MosaicFeaturePointDetectionPluginConstants.PLUGIN_PUBL;
	}
	
	@Override
	public String getName() {
		return MosaicFeaturePointDetectionPluginConstants.PLUGIN_NAME;
	}
	
	@Override
	public String getShortName() {
		return MosaicFeaturePointDetectionPluginConstants.PLUGIN_SNAME;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
			final int index) throws OmegaCoreExceptionPluginMissingData {
		final MosaicFeaturePointDetectionPluginPanel panel = new MosaicFeaturePointDetectionPluginPanel(parent, this,
				this.getGateway(), this.getLoadedImages(),
				this.getLoadedAnalysisRuns(), index);
		return panel;
	}
	
	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final GenericPluginPanel panel : this.getPanels()) {
			final MosaicFeaturePointDetectionPluginPanel specificPanel = (MosaicFeaturePointDetectionPluginPanel) panel;
			specificPanel.setGateway(gateway);
		}
	}
	
	@Override
	public void updateDisplayedData() {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final MosaicFeaturePointDetectionPluginPanel specificPanel = (MosaicFeaturePointDetectionPluginPanel) panel;
			specificPanel.updateTrees(this.getLoadedImages(),
					this.getLoadedAnalysisRuns());
		}
	}

	@Override
	public void selectImage(final OmegaImage image) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final MosaicFeaturePointDetectionPluginPanel specificPanel = (MosaicFeaturePointDetectionPluginPanel) panel;
			specificPanel.selectImage(image);
		}
	}

	@Override
	public void selectParticleDetectionRun(
			final OmegaParticleDetectionRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final MosaicFeaturePointDetectionPluginPanel specificPanel = (MosaicFeaturePointDetectionPluginPanel) panel;
			specificPanel.selectParticleDetectionRun(analysisRun);
		}
	}
	
	@Override
	public void setLoadedAnalysisRun(
			final List<OmegaAnalysisRun> loadedAnalysisRuns) {
		this.loadedAnalysisRuns = loadedAnalysisRuns;
	}
	
	@Override
	public List<OmegaAnalysisRun> getLoadedAnalysisRuns() {
		return this.loadedAnalysisRuns;
	}
	
	@Override
	public String getDescription() {
		return MosaicFeaturePointDetectionPluginConstants.PLUGIN_DESC;
	}
	
	@Override
	public String getReference() {
		return MosaicFeaturePointDetectionPluginConstants.PLUGIN_REFERENCE;
	}
}
