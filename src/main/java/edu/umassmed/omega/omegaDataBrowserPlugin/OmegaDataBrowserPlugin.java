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
package edu.umassmed.omega.omegaDataBrowserPlugin;

import java.util.List;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.OmegaData;
import edu.umassmed.omega.commons.data.OmegaLoadedData;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainerInterface;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.plugins.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectImagePluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleDetectionRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleLinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectSNRRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrackingMeasuresRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesRelinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesSegmentationRunPluginInterface;
import edu.umassmed.omega.omegaDataBrowserPlugin.gui.OmegaDataBrowserPluginPanel;

public class OmegaDataBrowserPlugin extends OmegaBrowserPlugin implements
		OmegaDataDisplayerPluginInterface, OmegaSelectImagePluginInterface,
		OmegaSelectParticleDetectionRunPluginInterface,
		OmegaSelectParticleLinkingRunPluginInterface,
		OmegaSelectTrajectoriesRelinkingRunPluginInterface,
		OmegaSelectTrajectoriesSegmentationRunPluginInterface,
		OmegaSelectSNRRunPluginInterface,
		OmegaSelectTrackingMeasuresRunPluginInterface {
	
	private OmegaDataBrowserPluginPanel panel;
	
	public OmegaDataBrowserPlugin() {
		super();
	}
	
	@Override
	public String getName() {
		return OmegaDataBrowserConstants.PLUGIN_NAME;
	}
	
	@Override
	public String getShortName() {
		return OmegaDataBrowserConstants.PLUGIN_SNAME;
	}
	
	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
			final int index) throws OmegaCoreExceptionPluginMissingData {
		
		final OmegaData omegaData = this.getMainData();
		final OmegaLoadedData loadedData = this.getLoadedData();
		final List<OmegaAnalysisRun> loadedAnalysisRuns = this
				.getLoadedAnalysisRuns();
		if (omegaData == null)
			throw new OmegaCoreExceptionPluginMissingData(this);
		
		// TODO loadedData exception
		// TODO loadedAnalysisRuns exception
		
		this.panel = new OmegaDataBrowserPluginPanel(parent, this, omegaData,
				loadedData, loadedAnalysisRuns, index);
		
		return this.panel;
	}
	
	@Override
	public void run() {
		//
	}
	
	@Override
	public void updateDisplayedData() {
		if (this.panel != null) {
			this.panel.updateTrees();
		}
	}
	
	@Override
	public String getDescription() {
		return OmegaDataBrowserConstants.PLUGIN_DESC;
	}

	@Override
	public void selectSNRRun(final OmegaSNRRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final OmegaDataBrowserPluginPanel specificPanel = (OmegaDataBrowserPluginPanel) panel;
			specificPanel.selectSubAnalysisContainer(analysisRun);
		}
	}
	
	@Override
	public void selectTrackingMeasuresRun(
			final OmegaTrackingMeasuresRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final OmegaDataBrowserPluginPanel specificPanel = (OmegaDataBrowserPluginPanel) panel;
			specificPanel.selectSubAnalysisContainer(analysisRun);
		}
	}
	
	@Override
	public void selectTrajectoriesSegmentationRun(
			final OmegaTrajectoriesSegmentationRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final OmegaDataBrowserPluginPanel specificPanel = (OmegaDataBrowserPluginPanel) panel;
			specificPanel.selectSubAnalysisContainer(analysisRun);
		}
	}
	
	@Override
	public void selectTrajectoriesRelinkingRun(
			final OmegaTrajectoriesRelinkingRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final OmegaDataBrowserPluginPanel specificPanel = (OmegaDataBrowserPluginPanel) panel;
			specificPanel.selectSubAnalysisContainer(analysisRun);
		}
	}
	
	@Override
	public void selectParticleLinkingRun(
			final OmegaParticleLinkingRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final OmegaDataBrowserPluginPanel specificPanel = (OmegaDataBrowserPluginPanel) panel;
			specificPanel.selectSubAnalysisContainer(analysisRun);
		}
	}
	
	@Override
	public void selectParticleDetectionRun(
			final OmegaParticleDetectionRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final OmegaDataBrowserPluginPanel specificPanel = (OmegaDataBrowserPluginPanel) panel;
			specificPanel.selectSubAnalysisContainer(analysisRun);
		}
	}

	@Override
	public void selectImage(final OmegaAnalysisRunContainerInterface image) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final OmegaDataBrowserPluginPanel specificPanel = (OmegaDataBrowserPluginPanel) panel;
			specificPanel.selectAnalysisContainer(image);
		}
	}
}
