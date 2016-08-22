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
package edu.umassmed.omega.trajectoriesRelinkingPlugin;

import java.util.Date;
import java.util.List;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaPerson;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesRelinkingPlugin;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.utilities.OmegaAlgorithmsUtilities;
import edu.umassmed.omega.trajectoriesRelinkingPlugin.gui.TRPluginPanel;

public class TrajectoriesRelinkingPlugin extends
        OmegaTrajectoriesRelinkingPlugin implements
        OmegaDataDisplayerPluginInterface {

	public TrajectoriesRelinkingPlugin() {
		super(1);
	}

	public TrajectoriesRelinkingPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);
	}

	@Override
	public String getName() {
		return TRConstants.PLUGIN_NAME;
	}

	@Override
	public String getShortName() {
		return TRConstants.PLUGIN_SNAME;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws OmegaCoreExceptionPluginMissingData {
		final TRPluginPanel panel = new TRPluginPanel(parent, this,
		        this.getGateway(), this.getLoadedImages(),
		        this.getOrphanedAnalysis(), this.getLoadedAnalysisRuns(), index);
		return panel;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TRPluginPanel specificPanel = (TRPluginPanel) panel;
			specificPanel.setGateway(gateway);
		}
	}

	@Override
	public void updateDisplayedData() {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TRPluginPanel specificPanel = (TRPluginPanel) panel;
			specificPanel.updateCombos(this.getLoadedImages(),
					this.getOrphanedAnalysis(), this.getLoadedAnalysisRuns());
		}
	}

	@Override
	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TRPluginPanel specificPanel = (TRPluginPanel) panel;
			specificPanel.updateTrajectories(trajectories, selection);
		}
	}

	@Override
	public void selectImage(final OmegaAnalysisRunContainer image) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TRPluginPanel specificPanel = (TRPluginPanel) panel;
			specificPanel.selectImage(image);
		}
	}

	@Override
	public void selectParticleDetectionRun(
	        final OmegaParticleDetectionRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TRPluginPanel specificPanel = (TRPluginPanel) panel;
			specificPanel.selectParticleDetectionRun(analysisRun);
		}
	}

	@Override
	public void selectParticleLinkingRun(
	        final OmegaParticleLinkingRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TRPluginPanel specificPanel = (TRPluginPanel) panel;
			specificPanel.selectParticleLinkingRun(analysisRun);
		}
	}

	@Override
	public void selectTrajectoriesRelinkingRun(
	        final OmegaTrajectoriesRelinkingRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TRPluginPanel specificPanel = (TRPluginPanel) panel;
			specificPanel.selectTrajectoriesRelinkingRun(analysisRun);
		}
	}

	@Override
	public void selectCurrentTrajectoriesRelinkingRun(
	        final OmegaAnalysisRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TRPluginPanel specificPanel = (TRPluginPanel) panel;
			specificPanel.selectCurrentTrajectoriesRelinkingRun(analysisRun);
		}
	}

	@Override
	public String getAlgorithmDescription() {
		return TRConstants.PLUGIN_ALGO_DESC;
	}

	@Override
	public OmegaPerson getAlgorithmAuthor() {
		return OmegaAlgorithmsUtilities.getDefaultDeveloper();
	}

	@Override
	public Double getAlgorithmVersion() {
		return 2.0;
	}

	@Override
	public Date getAlgorithmPublicationDate() {
		return TRConstants.PLUGIN_PUBL;
	}

	@Override
	public String getDescription() {
		return TRConstants.PLUGIN_DESC;
	}

	@Override
	public String getReference() {
		return "TBD";
	}
}
