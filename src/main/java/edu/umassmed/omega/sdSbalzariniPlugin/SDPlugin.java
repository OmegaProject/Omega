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
package edu.umassmed.omega.sdSbalzariniPlugin;

import java.util.Date;
import java.util.List;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainerInterface;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.plugins.OmegaParticleTrackingPlugin;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleDetectionRunPluginInterface;
import edu.umassmed.omega.sdSbalzariniPlugin.gui.SDPluginPanel;

public class SDPlugin extends OmegaParticleTrackingPlugin implements
		OmegaDataDisplayerPluginInterface,
		OmegaLoadedAnalysisConsumerPluginInterface,
		OmegaSelectParticleDetectionRunPluginInterface {

	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	public SDPlugin() {
		super(1);
	}

	public SDPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);

		this.loadedAnalysisRuns = null;
	}

	@Override
	public String getAlgorithmDescription() {
		return SDConstants.PLUGIN_ALGO_DESC;
	}

	@Override
	public String getAlgorithmAuthors() {
		return SDConstants.PLUGIN_AUTHORS;
	}

	@Override
	public String getAlgorithmVersion() {
		return SDConstants.PLUGIN_VERSION;
	}

	@Override
	public Date getAlgorithmPublicationDate() {
		return SDConstants.PLUGIN_PUBL;
	}

	@Override
	public String getName() {
		return SDConstants.PLUGIN_NAME;
	}

	@Override
	public String getShortName() {
		return SDConstants.PLUGIN_SNAME;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
			final int index) throws OmegaCoreExceptionPluginMissingData {
		final SDPluginPanel panel = new SDPluginPanel(parent, this,
				this.getGateway(), this.getLoadedImages(),
				this.getLoadedAnalysisRuns(), index);
		return panel;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final GenericPluginPanel panel : this.getPanels()) {
			final SDPluginPanel specificPanel = (SDPluginPanel) panel;
			specificPanel.setGateway(gateway);
		}
	}

	@Override
	public void updateDisplayedData() {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final SDPluginPanel specificPanel = (SDPluginPanel) panel;
			specificPanel.updateTrees(this.getLoadedImages(),
					this.getLoadedAnalysisRuns());
		}
	}
	
	@Override
	public void selectImage(final OmegaAnalysisRunContainerInterface image) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final SDPluginPanel specificPanel = (SDPluginPanel) panel;
			specificPanel.selectImage(image);
		}
	}
	
	@Override
	public void selectParticleDetectionRun(
			final OmegaParticleDetectionRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final SDPluginPanel specificPanel = (SDPluginPanel) panel;
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
		return SDConstants.PLUGIN_DESC;
	}

	@Override
	public String getReference() {
		return SDConstants.PLUGIN_REFERENCE;
	}
}
