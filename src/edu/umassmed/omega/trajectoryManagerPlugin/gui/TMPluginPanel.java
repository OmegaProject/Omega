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
package edu.umassmed.omega.trajectoryManagerPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.eventSystem.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaTMPluginImageSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaTMPluginParticleDetectionRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaTMPluginParticleLinkingRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaTMPluginTrajectoriesEvent;
import edu.umassmed.omega.commons.genericPlugins.OmegaPlugin;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class TMPluginPanel extends GenericPluginPanel {

	private static final long serialVersionUID = -5740459087763362607L;

	private JSplitPane mainSplitPane;

	private JComboBox<String> images_combo, particles_combo,
	        trajectories_combo;
	private boolean popImages, popParticles, popTrajectories, isHandlingEvent;

	private TMTrajectoriesPanel trajectoriesPanel;
	private JScrollPane scrollPane;

	private List<OmegaImage> images;

	private OmegaImage selectedImage;

	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	final List<OmegaParticleDetectionRun> particleDetectionRuns;
	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	final List<OmegaParticleLinkingRun> particleLinkingRuns;
	private OmegaParticleLinkingRun selectedParticleLinkingRun;

	public TMPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns, final int index) {
		super(parent, plugin, index);

		this.particleDetectionRuns = new ArrayList<OmegaParticleDetectionRun>();
		this.particleLinkingRuns = new ArrayList<OmegaParticleLinkingRun>();

		this.images = images;
		this.loadedAnalysisRuns = analysisRuns;
		this.selectedImage = null;
		this.selectedParticleDetectionRun = null;
		this.selectedParticleLinkingRun = null;
		this.popImages = false;
		this.popParticles = false;
		this.popTrajectories = false;
		this.isHandlingEvent = false;

		this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		// this.createMenu();
		this.createAndAddWidgets();
		// this.loadedDataBrowserPanel.updateTree(images);
		this.addListeners();

		this.populateImagesCombo();
	}

	private void createMenu() {

	}

	public void createAndAddWidgets() {
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(3, 1));
		this.images_combo = new JComboBox<String>();
		this.images_combo.setEnabled(false);
		topPanel.add(this.images_combo);

		this.particles_combo = new JComboBox<String>();
		this.particles_combo.setEnabled(false);
		topPanel.add(this.particles_combo);

		this.trajectories_combo = new JComboBox<String>();
		this.trajectories_combo.setEnabled(false);
		topPanel.add(this.trajectories_combo);

		this.add(topPanel, BorderLayout.NORTH);

		this.trajectoriesPanel = new TMTrajectoriesPanel(
		        this.getParentContainer(), this);
		this.scrollPane = new JScrollPane(this.trajectoriesPanel);

		this.add(this.scrollPane, BorderLayout.CENTER);
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				super.componentResized(evt);
				// TMPluginPanel.this.mainSplitPane.setDividerLocation(0.25);
			}
		});
		this.images_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMPluginPanel.this.selectImage();
			}
		});
		this.particles_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMPluginPanel.this.selectParticleDetectionRun();
			}
		});
		this.trajectories_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMPluginPanel.this.selectParticleLinkingRun();
			}
		});
	}

	private void selectImage() {
		if (this.popImages)
			return;
		final int index = this.images_combo.getSelectedIndex();
		if (index == -1)
			return;
		this.selectedImage = TMPluginPanel.this.images.get(index);
		if (!this.isHandlingEvent) {
			this.sendTMPluginImageSelectionEvent();
		}
		this.populateParticlesCombo();
		this.populateTrajectoriesCombo();
	}

	private void selectParticleDetectionRun() {
		if (this.popParticles)
			return;
		final int index = this.particles_combo.getSelectedIndex();
		if (index == -1)
			return;
		this.selectedParticleDetectionRun = this.particleDetectionRuns
		        .get(index);
		if (!this.isHandlingEvent) {
			this.sendTMPluginParticleDetectionRunSelectionEvent();
		}
		this.populateTrajectoriesCombo();
	}

	private void selectParticleLinkingRun() {
		if (this.popTrajectories)
			return;
		final int index = this.trajectories_combo.getSelectedIndex();
		if (index == -1)
			return;
		this.selectedParticleLinkingRun = this.particleLinkingRuns.get(index);
		if (!this.isHandlingEvent) {
			this.sendTMPluginParticleLinkingRunSelectionEvent();
		}
		this.drawTrajectoriesTable();
	}

	private void setRadius(final int radius) {
		this.trajectoriesPanel.setRadius(radius);
	}

	private void drawTrajectoriesTable() {
		this.trajectoriesPanel
		        .updateTrajectories(this.selectedParticleLinkingRun);
		this.scrollPane.repaint();
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		// this.loadedDataBrowserPanel.updateParentContainer(parent);
		// this.projectListPanel.updateParentContainer(parent);
	}

	@Override
	public void onCloseOperation() {

	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		this.trajectoriesPanel.updateTrajectories(trajectories, selection);
	}

	public void updateCombos(final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this.images = images;
		this.loadedAnalysisRuns = analysisRuns;

		this.populateImagesCombo();
	}

	private void populateImagesCombo() {
		this.popImages = true;
		this.images_combo.removeAllItems();
		this.selectedImage = null;

		if (this.images.isEmpty()) {
			this.images_combo.setEnabled(false);
			return;

		}
		this.images_combo.setEnabled(true);

		for (final OmegaImage image : this.images) {
			this.images_combo.addItem(image.getName());
		}
		this.popImages = false;
		this.images_combo.setSelectedIndex(0);
	}

	private void populateParticlesCombo() {
		this.popParticles = true;
		this.particles_combo.removeAllItems();
		this.particleDetectionRuns.clear();
		this.particles_combo.setSelectedIndex(-1);
		this.selectedParticleDetectionRun = null;

		if ((this.selectedImage == null)) {
			this.particles_combo.setEnabled(false);
			return;
		}

		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedImage.getAnalysisRuns().contains(analysisRun)
			        && (analysisRun instanceof OmegaParticleDetectionRun)) {
				this.particleDetectionRuns
				        .add((OmegaParticleDetectionRun) analysisRun);
				this.particles_combo.addItem(analysisRun.getName());
			}
		}

		if (this.particleDetectionRuns.isEmpty()) {
			this.particles_combo.setEnabled(false);
			return;
		}

		this.particles_combo.setEnabled(true);

		this.popParticles = false;
		this.particles_combo.setSelectedIndex(0);
	}

	private void populateTrajectoriesCombo() {
		this.popTrajectories = true;
		this.trajectories_combo.removeAllItems();
		this.particleLinkingRuns.clear();
		this.trajectories_combo.setSelectedIndex(-1);
		this.selectedParticleLinkingRun = null;

		if ((this.selectedParticleDetectionRun == null)) {
			this.trajectories_combo.setEnabled(false);
			return;
		}

		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedParticleDetectionRun.getAnalysisRuns().contains(
			        analysisRun)) {
				this.particleLinkingRuns
				        .add((OmegaParticleLinkingRun) analysisRun);
				this.trajectories_combo.addItem(analysisRun.getName());
			}
		}

		if (this.particleLinkingRuns.isEmpty()) {
			this.trajectories_combo.setEnabled(false);
			return;
		}

		this.trajectories_combo.setEnabled(true);

		this.popTrajectories = false;
		this.trajectories_combo.setSelectedIndex(0);
	}

	private void sendTMPluginImageSelectionEvent() {
		final OmegaPluginEvent event = new OmegaTMPluginImageSelectionEvent(
		        this.getPlugin(), this.selectedImage);
		this.getPlugin().fireEvent(event);
	}

	private void sendTMPluginParticleDetectionRunSelectionEvent() {
		final OmegaPluginEvent event = new OmegaTMPluginParticleDetectionRunSelectionEvent(
		        this.getPlugin(), this.selectedParticleDetectionRun);
		this.getPlugin().fireEvent(event);
	}

	private void sendTMPluginParticleLinkingRunSelectionEvent() {
		final OmegaPluginEvent event = new OmegaTMPluginParticleLinkingRunSelectionEvent(
		        this.getPlugin(), this.selectedParticleLinkingRun);
		this.getPlugin().fireEvent(event);
	}

	protected void sendTMPluginTrajectoriesEvent(
	        final List<OmegaTrajectory> trajectories, final boolean selection) {
		final OmegaPluginEvent event = new OmegaTMPluginTrajectoriesEvent(
		        this.getPlugin(), trajectories, selection);
		this.getPlugin().fireEvent(event);
	}

	public void setBufferedImage(final BufferedImage bufferedImage) {
		this.trajectoriesPanel.setBufferedImage(bufferedImage);
	}

	public void selectImage(final OmegaImage image) {
		this.isHandlingEvent = true;
		final int index = this.images.indexOf(image);
		this.images_combo.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectParticleDetectionRun(
	        final OmegaParticleDetectionRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.particleLinkingRuns.indexOf(analysisRun);
		this.particles_combo.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectParticleLinkingRun(
	        final OmegaParticleLinkingRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.particleLinkingRuns.indexOf(analysisRun);
		this.trajectories_combo.setSelectedIndex(index);
		this.isHandlingEvent = false;

	}
}
