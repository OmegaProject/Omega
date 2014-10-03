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
package edu.umassmed.omega.trajectoryManagerPlugin;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.exceptions.OmegaMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesManagerPlugin;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaTrajectoriesManagerRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.trajectoryManagerPlugin.gui.TMPluginPanel;

public class TrajectoriesManagerPlugin extends OmegaTrajectoriesManagerPlugin
        implements OmegaDataDisplayerPluginInterface {

	public TrajectoriesManagerPlugin() {
		super(1);
	}

	public TrajectoriesManagerPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);
	}

	@Override
	public String getName() {
		return "Trajectories Manager";
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
			final TMPluginPanel specificPanel = (TMPluginPanel) panel;
			specificPanel.setSegmentationTypesList(segmTypesList);
		}
	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws OmegaMissingData {
		final TMPluginPanel panel = new TMPluginPanel(parent, this,
		        this.getGateway(), this.getLoadedImages(),
		        this.getLoadedAnalysisRuns(), this.getSegmentationTypesList(),
		        index);
		return panel;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		super.setGateway(gateway);
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TMPluginPanel specificPanel = (TMPluginPanel) panel;
			specificPanel.setGateway(gateway);
		}
	}

	@Override
	public void updateDisplayedData() {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TMPluginPanel specificPanel = (TMPluginPanel) panel;
			specificPanel.updateCombos(this.getLoadedImages(),
			        this.getLoadedAnalysisRuns());
		}
	}

	@Override
	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TMPluginPanel specificPanel = (TMPluginPanel) panel;
			specificPanel.updateAdjustTrajectories(trajectories, selection);
		}
	}

	@Override
	public void selectImage(final OmegaImage image) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TMPluginPanel specificPanel = (TMPluginPanel) panel;
			specificPanel.selectImage(image);
		}
	}

	@Override
	public void selectParticleDetectionRun(
	        final OmegaParticleDetectionRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TMPluginPanel specificPanel = (TMPluginPanel) panel;
			specificPanel.selectParticleDetectionRun(analysisRun);
		}
	}

	@Override
	public void selectParticleLinkingRun(
	        final OmegaParticleLinkingRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TMPluginPanel specificPanel = (TMPluginPanel) panel;
			specificPanel.selectParticleLinkingRun(analysisRun);
		}
	}

	@Override
	public void selectTrajectoriesManagerRun(
	        final OmegaTrajectoriesManagerRun analysisRun) {
		for (final GenericPluginPanel panel : this.getPanels()) {
			final TMPluginPanel specificPanel = (TMPluginPanel) panel;
			specificPanel.selectTrajectoriesManagerRun(analysisRun);
		}
	}

	@Override
	public String getAlgorithmName() {
		return "Trajectory Manager";
	}

	@Override
	public String getAlgorithmDescription() {
		return "Algorithm desc";
	}

	@Override
	public OmegaPerson getAlgorithmAuthor() {
		return new OmegaPerson("Alex", "Rigano");
	}

	@Override
	public Double getAlgorithmVersion() {
		return 1.0;
	}

	@Override
	public Date getAlgorithmPublicationDate() {
		return new GregorianCalendar(2014, 8, 21).getTime();
	}
}
