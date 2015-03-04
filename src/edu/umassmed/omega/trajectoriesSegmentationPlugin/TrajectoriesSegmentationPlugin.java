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
package edu.umassmed.omega.trajectoriesSegmentationPlugin;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesSegmentationPlugin;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.utilities.OmegaAlgorithmsUtilities;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaPerson;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.trajectoriesSegmentationPlugin.gui.TSPluginPanel;

public class TrajectoriesSegmentationPlugin extends
        OmegaTrajectoriesSegmentationPlugin implements
        OmegaDataDisplayerPluginInterface {

	public TrajectoriesSegmentationPlugin() {
		super(1);
	}

	public TrajectoriesSegmentationPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);
	}

	@Override
	public String getName() {
		return "Trajectories Segmentation";
	}

	@Override
	public String getShortName() {
		return "TS";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSegmentationTypesList(
	        final List<OmegaSegmentationTypes> segmTypesList) {
		this.setSegmentationTypesList(segmTypesList);
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.setSegmentationTypesList(segmTypesList);
		}
	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws OmegaCoreExceptionPluginMissingData {
		final TSPluginPanel panel = new TSPluginPanel(parent, this,
		        this.getGateway(), this.getLoadedImages(),
		        this.getLoadedAnalysisRuns(), this.getSegmentationTypesList(),
		        index);
		return panel;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.setGateway(gateway);
		}
	}

	@Override
	public void updateDisplayedData() {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.updateCombos(this.getLoadedImages(),
			        this.getLoadedAnalysisRuns());
		}
	}

	@Override
	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.updateTrajectories(trajectories, selection);
		}
	}

	@Override
	public void selectImage(final OmegaImage image) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.selectImage(image);
		}
	}

	@Override
	public void selectParticleDetectionRun(
	        final OmegaParticleDetectionRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.selectParticleDetectionRun(analysisRun);
		}
	}

	@Override
	public void selectParticleLinkingRun(
	        final OmegaParticleLinkingRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.selectParticleLinkingRun(analysisRun);
		}
	}

	@Override
	public void selectTrajectoriesRelinkingRun(
	        final OmegaTrajectoriesRelinkingRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.selectTrajectoriesRelinkingRun(analysisRun);
		}
	}

	@Override
	public void selectTrajectoriesSegmentationRun(
	        final OmegaTrajectoriesSegmentationRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.selectTrajectoriesSegmentationRun(analysisRun);
		}
	}

	@Override
	public void selectCurrentTrajectoriesSegmentationRun(
	        final OmegaAnalysisRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TSPluginPanel specificPanel = (TSPluginPanel) panel;
			specificPanel.selectCurrentTrajectoriesSegmentationRun(analysisRun);
		}
	}

	@Override
	public String getAlgorithmName() {
		return "Trajectories Segmentation";
	}

	@Override
	public String getAlgorithmDescription() {
		return "Default OMEGA trajectories segmentation";
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
		return new GregorianCalendar(2014, 12, 1).getTime();
	}

	@Override
	public String getDescription() {
		return "Default OMEGA trajectories segmentation";
	}
}
