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
package edu.umassmed.omega.trackingMeasuresPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventTrajectories;
import edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.GenericTrajectoriesBrowserPanel;
import edu.umassmed.omega.commons.gui.interfaces.GenericTrajectoriesBrowserContainerInterface;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class TMPluginPanel extends GenericPluginPanel implements
        GenericTrajectoriesBrowserContainerInterface {

	private static final long serialVersionUID = -5740459087763362607L;

	private OmegaGateway gateway;

	private TMIntensityPanel intensityPanel;
	private TMMobilityPanel mobilityPanel;
	private TMVelocityPanel velocityPanel;
	private TMDiffusivityPanel diffusivityPanel;
	private TMMotionTypeClassificationPanel motionTypeClassificationPanel;
	private GenericTrajectoriesBrowserPanel tbPanel;
	private GenericStatusPanel statusPanel;

	private JComboBox<String> images_cmb, particles_cmb, trajectories_cmb,
	        trajectoriesRelinking_cmb, trajectoriesSegmentation_cmb,
	        trackingMeasures_cmb;
	private boolean popImages, popParticles, popTrajectories, popTrajRelinking,
	        popTrajSegmentation, popTrackingMeasures;

	private boolean isHandlingEvent;

	private JTabbedPane tabbedPane;

	private List<OmegaImage> images;
	private OmegaImage selectedImage;

	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	final List<OmegaParticleDetectionRun> particleDetectionRuns;
	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	final List<OmegaParticleLinkingRun> particleLinkingRuns;
	private OmegaParticleLinkingRun selectedParticleLinkingRun;
	final List<OmegaTrajectoriesRelinkingRun> trajRelinkingRuns;
	private OmegaTrajectoriesRelinkingRun selectedTrajRelinkingRun;
	final List<OmegaTrajectoriesSegmentationRun> trajSegmentationRuns;
	private OmegaTrajectoriesSegmentationRun selectedTrajSegmentationRun;
	final List<OmegaTrackingMeasuresRun> trackingMeasuresRuns;
	private OmegaTrackingMeasuresRun selectedTrackingMeasuresRun;

	public TMPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final OmegaGateway gateway,
	        final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns, final int index) {
		super(parent, plugin, index);

		this.gateway = gateway;

		this.selectedImage = null;
		this.particleDetectionRuns = new ArrayList<>();
		this.selectedParticleDetectionRun = null;
		this.particleLinkingRuns = new ArrayList<>();
		this.selectedParticleLinkingRun = null;
		this.trajRelinkingRuns = new ArrayList<>();
		this.selectedTrajRelinkingRun = null;
		this.trajSegmentationRuns = new ArrayList<>();
		this.selectedTrajSegmentationRun = null;
		this.trackingMeasuresRuns = new ArrayList<>();
		this.selectedTrackingMeasuresRun = null;

		this.images = images;
		this.loadedAnalysisRuns = analysisRuns;

		this.popImages = false;
		this.popParticles = false;
		this.popTrajectories = false;
		this.popTrajRelinking = false;
		this.popTrajSegmentation = false;
		this.popTrackingMeasures = false;
		this.isHandlingEvent = false;

		this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		// this.createMenu();
		this.createAndAddWidgets();
		// this.loadedDataBrowserPanel.updateTree(images);

		this.addListeners();

		this.populateImagesCombo();
	}

	private void createAndAddWidgets() {
		// this.segmentPreferencesDialog = new TSSegmentPreferencesDialog(this,
		// this.getParentContainer(), this.segmTypesList);

		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(6, 1));
		this.images_cmb = new JComboBox<String>();
		this.images_cmb.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.images_cmb.setEnabled(false);
		topPanel.add(this.images_cmb);

		this.particles_cmb = new JComboBox<String>();
		this.particles_cmb
		        .setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.particles_cmb.setEnabled(false);
		topPanel.add(this.particles_cmb);

		this.trajectories_cmb = new JComboBox<String>();
		this.trajectories_cmb
		        .setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectories_cmb.setEnabled(false);
		topPanel.add(this.trajectories_cmb);

		this.trajectoriesRelinking_cmb = new JComboBox<String>();
		this.trajectoriesRelinking_cmb
		        .setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectoriesRelinking_cmb.setEnabled(false);
		topPanel.add(this.trajectoriesRelinking_cmb);

		this.trajectoriesSegmentation_cmb = new JComboBox<String>();
		this.trajectoriesSegmentation_cmb
		        .setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectoriesSegmentation_cmb.setEnabled(false);
		topPanel.add(this.trajectoriesSegmentation_cmb);

		this.trackingMeasures_cmb = new JComboBox<String>();
		this.trackingMeasures_cmb
		        .setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trackingMeasures_cmb.setEnabled(false);
		topPanel.add(this.trackingMeasures_cmb);

		this.add(topPanel, BorderLayout.NORTH);

		this.tabbedPane = new JTabbedPane();

		this.intensityPanel = new TMIntensityPanel(this.getParentContainer(),
		        this, null);
		this.tabbedPane.add("Intensity", this.intensityPanel);

		this.mobilityPanel = new TMMobilityPanel(this.getParentContainer(),
		        this, null);
		this.tabbedPane.add("Mobility", this.mobilityPanel);

		this.velocityPanel = new TMVelocityPanel(this.getParentContainer(),
		        this, null);
		this.tabbedPane.add("Velocity", this.velocityPanel);

		this.diffusivityPanel = new TMDiffusivityPanel(
		        this.getParentContainer(), this, null);
		this.tabbedPane.add("Diffusivity", this.diffusivityPanel);

		this.motionTypeClassificationPanel = new TMMotionTypeClassificationPanel(
		        this.getParentContainer(), this, null);
		this.tabbedPane.add("Motion Type Classification",
		        this.motionTypeClassificationPanel);

		this.tbPanel = new GenericTrajectoriesBrowserPanel(
		        this.getParentContainer(), this, this.gateway, true, true);
		this.tabbedPane.add("Track browser", this.tbPanel);

		this.add(this.tabbedPane, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		this.statusPanel = new GenericStatusPanel(1);
		bottomPanel.add(this.statusPanel, BorderLayout.SOUTH);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void addListeners() {
		this.tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				// TODO
			}
		});
		this.images_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMPluginPanel.this.selectImage();
			}
		});
		this.particles_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMPluginPanel.this.selectParticleDetectionRun();
			}
		});
		this.trajectories_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMPluginPanel.this.selectParticleLinkingRun();
			}
		});
		this.trajectoriesRelinking_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMPluginPanel.this.selectTrajectoriesRelinkingRun();
			}
		});
		this.trajectoriesSegmentation_cmb
		        .addActionListener(new ActionListener() {
			        @Override
			        public void actionPerformed(final ActionEvent e) {
				        TMPluginPanel.this.selectTrajectoriesSegmentationRun();
			        }
		        });
		this.trackingMeasures_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMPluginPanel.this.selectTrackingMeasuresRun();
			}
		});
	}

	private void selectImage() {
		if (this.popImages)
			return;
		final int index = this.images_cmb.getSelectedIndex();
		this.selectedImage = null;
		if (index == -1) {
			this.populateParticlesCombo();
			// this.resetTrajectories();
			return;
		}
		this.selectedImage = this.images.get(index);
		this.tbPanel.setImage(this.selectedImage);
		this.intensityPanel.setMaximumT(this.selectedImage.getDefaultPixels()
		        .getSizeT());
		this.mobilityPanel.setMaximumT(this.selectedImage.getDefaultPixels()
		        .getSizeT());
		this.velocityPanel.setMaximumT(this.selectedImage.getDefaultPixels()
		        .getSizeT());
		this.diffusivityPanel.setMaximumT(this.selectedImage.getDefaultPixels()
		        .getSizeT());
		this.motionTypeClassificationPanel.setMaximumT(this.selectedImage
		        .getDefaultPixels().getSizeT());
		this.motionTypeClassificationPanel.setImageWidth(this.selectedImage
		        .getDefaultPixels().getSizeX());
		this.motionTypeClassificationPanel.setImageHeight(this.selectedImage
		        .getDefaultPixels().getSizeY());
		if (!this.isHandlingEvent) {
			this.fireEventSelectionPluginImage();
		}
		this.populateParticlesCombo();
		// this.populateTrajectoriesCombo();
	}

	private void selectParticleDetectionRun() {
		if (this.popParticles)
			return;
		final int index = this.particles_cmb.getSelectedIndex();
		this.selectedParticleDetectionRun = null;
		if (index == -1) {
			this.populateTrajectoriesCombo();
			// this.resetTrajectories();
			return;
		}
		this.selectedParticleDetectionRun = this.particleDetectionRuns
		        .get(index);
		if (!this.isHandlingEvent) {
			this.fireEventSelectionPluginParticleDetectionRun();
		}
		this.populateTrajectoriesCombo();
	}

	private void selectParticleLinkingRun() {
		if (this.popTrajectories)
			return;
		final int index = this.trajectories_cmb.getSelectedIndex();
		this.selectedParticleLinkingRun = null;
		if (index == -1) {
			this.populateTrajectoriesRelinkingCombo();
			this.populateTrackingMeasuresCombo();
			// this.resetTrajectories();
			return;
		}
		this.selectedParticleLinkingRun = this.particleLinkingRuns.get(index);
		if (!this.isHandlingEvent) {
			this.fireEventSelectionParticleLinkingRun();
		}
		this.selectedParticleLinkingRun.getAlgorithmSpec().getParameter(
		        OmegaConstantsAlgorithmParameters.PARAM_RADIUS);
		// if ((radius != null)
		// && radius.getClazz().equals(Integer.class.getName())) {
		// this.setRadius((int) radius.getValue());
		// }
		this.populateTrajectoriesRelinkingCombo();
		this.populateTrackingMeasuresCombo();
		// this.tbPanel.updateTrajectories(
		// this.selectedParticleLinkingRun.getResultingTrajectories(),
		// false);
	}

	private void selectTrajectoriesRelinkingRun() {
		if (this.popTrajRelinking)
			return;
		final int index = this.trajectoriesRelinking_cmb.getSelectedIndex();
		this.selectedTrajRelinkingRun = null;
		if (index == -1) {
			this.populateTrajectoriesSegmentationCombo();
			// this.resetTrajectories();
			return;
		}
		if (index < this.trajRelinkingRuns.size()) {
			this.selectedTrajRelinkingRun = this.trajRelinkingRuns.get(index);
		}
		if (!this.isHandlingEvent) {
			this.fireEventSelectionTrajectoriesRelinkingRun();
		}
		this.populateTrajectoriesSegmentationCombo();
		// this.tbPanel
		// .updateTrajectories(this.selectedTrajRelinkingRun
		// .getResultingTrajectories(), false);
	}

	private void selectTrajectoriesSegmentationRun() {
		if (this.popTrajSegmentation)
			return;
		final int index = this.trajectoriesSegmentation_cmb.getSelectedIndex();
		this.selectedTrajSegmentationRun = null;
		if (index == -1)
			return;

		if (index < this.trajSegmentationRuns.size()) {
			this.selectedTrajSegmentationRun = this.trajSegmentationRuns
			        .get(index);
		}
		if (!this.isHandlingEvent) {
			this.fireEventSelectionTrajectoriesSegmentationRun();
		}

		this.tbPanel
		        .updateTrajectories(this.selectedTrajRelinkingRun
		                .getResultingTrajectories(), false);
		this.intensityPanel.setSegmentsMap(this.selectedTrajSegmentationRun
		        .getResultingSegments());
		this.mobilityPanel.setSegmentsMap(this.selectedTrajSegmentationRun
		        .getResultingSegments());
		this.velocityPanel.setSegmentsMap(this.selectedTrajSegmentationRun
		        .getResultingSegments());
		this.diffusivityPanel.setSegmentsMap(this.selectedTrajSegmentationRun
		        .getResultingSegments());
		this.motionTypeClassificationPanel
		        .setSegmentsMap(this.selectedTrajSegmentationRun
		                .getResultingSegments());
	}

	private void selectTrackingMeasuresRun() {
		if (this.popTrackingMeasures)
			return;
		final int index = this.trackingMeasures_cmb.getSelectedIndex();
		this.selectedTrackingMeasuresRun = null;
		if (index == -1)
			return;

		if (index < this.trackingMeasuresRuns.size()) {
			this.selectedTrackingMeasuresRun = this.trackingMeasuresRuns
			        .get(index);
		}
		if (!this.isHandlingEvent) {
			// this.fireEventSelectionTrajectoriesSegmentationRun();
		}

		this.intensityPanel
		        .updateSelectedTrackingMeasuresRun(this.selectedTrackingMeasuresRun);
		this.mobilityPanel
		        .updateSelectedTrackingMeasuresRun(this.selectedTrackingMeasuresRun);
		this.velocityPanel
		        .updateSelectedTrackingMeasuresRun(this.selectedTrackingMeasuresRun);
		this.diffusivityPanel
		        .updateSelectedTrackingMeasuresRun(this.selectedTrackingMeasuresRun);
		this.motionTypeClassificationPanel
		        .updateSelectedTrackingMeasuresRun(this.selectedTrackingMeasuresRun);
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.tbPanel.updateParentContainer(parent);
		this.intensityPanel.updateParentContainer(parent);
		this.mobilityPanel.updateParentContainer(parent);
		this.diffusivityPanel.updateParentContainer(parent);
		this.motionTypeClassificationPanel.updateParentContainer(parent);
	}

	@Override
	public void onCloseOperation() {

	}

	public void updateCombos(final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this.isHandlingEvent = true;
		this.images = images;
		this.loadedAnalysisRuns = analysisRuns;

		this.populateImagesCombo();
		this.isHandlingEvent = false;
	}

	private void populateImagesCombo() {
		this.popImages = true;
		this.images_cmb.removeAllItems();
		this.selectedImage = null;

		if ((this.images == null) || this.images.isEmpty()) {
			this.images_cmb.setEnabled(false);
			this.populateParticlesCombo();
			// this.resetTrajectories();
			return;

		}
		this.images_cmb.setEnabled(true);

		for (final OmegaImage image : this.images) {
			this.images_cmb.addItem(image.getName());
		}
		this.popImages = false;

		if (this.images_cmb.getItemCount() > 0) {
			this.images_cmb.setSelectedIndex(0);
		} else {
			this.images_cmb.setSelectedIndex(-1);
		}
	}

	private void populateParticlesCombo() {
		this.popParticles = true;
		this.particles_cmb.removeAllItems();
		this.particleDetectionRuns.clear();
		this.particles_cmb.setSelectedIndex(-1);
		this.selectedParticleDetectionRun = null;

		if ((this.selectedImage == null)) {
			this.particles_cmb.setEnabled(false);
			this.populateTrajectoriesCombo();
			// this.resetTrajectories();
			return;
		}

		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedImage.getAnalysisRuns().contains(analysisRun)
			        && (analysisRun instanceof OmegaParticleDetectionRun)) {
				this.particleDetectionRuns
				        .add((OmegaParticleDetectionRun) analysisRun);
				this.particles_cmb.addItem(analysisRun.getName());
			}
		}

		if (this.particleDetectionRuns.isEmpty()) {
			this.particles_cmb.setEnabled(false);
			this.populateTrajectoriesCombo();
			// this.resetTrajectories();
			return;
		}

		this.popParticles = false;
		if (this.particles_cmb.getItemCount() > 0) {
			this.particles_cmb.setEnabled(true);
			this.particles_cmb.setSelectedIndex(0);
		} else {
			this.particles_cmb.setSelectedIndex(-1);
		}
	}

	private void populateTrajectoriesCombo() {
		this.popTrajectories = true;
		this.trajectories_cmb.removeAllItems();
		this.particleLinkingRuns.clear();
		this.trajectories_cmb.setSelectedIndex(-1);
		this.selectedParticleLinkingRun = null;

		if ((this.selectedParticleDetectionRun == null)) {
			this.trajectories_cmb.setEnabled(false);
			this.populateTrajectoriesRelinkingCombo();
			this.populateTrackingMeasuresCombo();
			// this.resetTrajectories();
			return;
		}

		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedParticleDetectionRun.getAnalysisRuns().contains(
			        analysisRun)) {
				this.particleLinkingRuns
				        .add((OmegaParticleLinkingRun) analysisRun);
				this.trajectories_cmb.addItem(analysisRun.getName());
			}
		}
		if (this.particleLinkingRuns.isEmpty()) {
			this.trajectories_cmb.setEnabled(false);
			this.populateTrajectoriesRelinkingCombo();
			this.populateTrackingMeasuresCombo();
			// this.resetTrajectories();
			return;
		}

		this.popTrajectories = false;
		if (this.trajectories_cmb.getItemCount() > 0) {
			this.trajectories_cmb.setEnabled(true);
			this.trajectories_cmb.setSelectedIndex(0);
		} else {
			this.trajectories_cmb.setSelectedIndex(-1);
		}
	}

	private void populateTrajectoriesRelinkingCombo() {
		this.popTrajRelinking = true;
		this.trajectoriesRelinking_cmb.removeAllItems();
		this.trajRelinkingRuns.clear();
		this.trajectoriesRelinking_cmb.setSelectedIndex(-1);
		this.selectedTrajRelinkingRun = null;

		if (this.selectedParticleLinkingRun == null) {
			this.trajectoriesRelinking_cmb.setEnabled(false);
			this.populateTrajectoriesSegmentationCombo();
			// this.resetTrajectories();
			return;
		}

		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedParticleLinkingRun.getAnalysisRuns().contains(
			        analysisRun)
			        && (analysisRun instanceof OmegaTrajectoriesRelinkingRun)) {
				this.trajRelinkingRuns
				        .add((OmegaTrajectoriesRelinkingRun) analysisRun);
				this.trajectoriesRelinking_cmb.addItem(analysisRun.getName());
			}
		}
		if (this.trajRelinkingRuns.isEmpty()) {
			this.trajectoriesRelinking_cmb.setEnabled(false);
			this.populateTrajectoriesSegmentationCombo();
			// this.resetTrajectories();
			return;
		}

		this.popTrajRelinking = false;
		if (this.trajectoriesRelinking_cmb.getItemCount() > 0) {
			this.trajectoriesRelinking_cmb.setEnabled(true);
			this.trajectoriesRelinking_cmb.setSelectedIndex(0);
		} else {
			this.trajectoriesRelinking_cmb.setSelectedIndex(-1);
		}
	}

	private void populateTrajectoriesSegmentationCombo() {
		this.popTrajSegmentation = true;
		this.trajectoriesSegmentation_cmb.removeAllItems();
		this.trajSegmentationRuns.clear();
		this.trajectoriesSegmentation_cmb.setSelectedIndex(-1);
		this.selectedTrajSegmentationRun = null;
		if (this.selectedTrajRelinkingRun == null) {
			this.trajectoriesSegmentation_cmb.setEnabled(false);
			// this.resetTrajectories();
			return;
		}
		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedTrajRelinkingRun.getAnalysisRuns().contains(
			        analysisRun)) {
				this.trajSegmentationRuns
				        .add((OmegaTrajectoriesSegmentationRun) analysisRun);
				this.trajectoriesSegmentation_cmb
				        .addItem(analysisRun.getName());
			}
		}
		this.trajectoriesSegmentation_cmb
		        .addItem(OmegaConstants.OMEGA_SEGMENTATION_CURRENT);
		if (this.trajSegmentationRuns.isEmpty()) {
			this.trajectoriesSegmentation_cmb.setEnabled(false);
			// this.resetTrajectories();
			return;
		}

		this.popTrajSegmentation = false;
		if (this.trajectoriesSegmentation_cmb.getItemCount() > 1) {
			this.trajectoriesSegmentation_cmb.setEnabled(true);
			this.trajectoriesSegmentation_cmb.setSelectedIndex(0);
		} else {
			this.trajectoriesSegmentation_cmb.setSelectedIndex(-1);
		}
	}

	private void populateTrackingMeasuresCombo() {
		this.popTrackingMeasures = true;
		this.trackingMeasures_cmb.removeAllItems();
		this.trackingMeasuresRuns.clear();
		this.trackingMeasures_cmb.setSelectedIndex(-1);
		this.selectedTrackingMeasuresRun = null;
		if (this.selectedParticleLinkingRun == null) {
			this.trackingMeasures_cmb.setEnabled(false);
			// this.resetTrajectories();
			return;
		}
		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedParticleLinkingRun.getAnalysisRuns().contains(
			        analysisRun)
			        && (analysisRun instanceof OmegaTrackingMeasuresRun)) {
				this.trackingMeasuresRuns
				        .add((OmegaTrackingMeasuresRun) analysisRun);
				this.trackingMeasures_cmb.addItem(analysisRun.getName());
			}
		}
		if (this.trackingMeasuresRuns.isEmpty()) {
			this.trackingMeasures_cmb.setEnabled(false);
			// this.resetTrajectories();
			return;
		}

		this.popTrackingMeasures = false;
		if (this.trackingMeasures_cmb.getItemCount() > 0) {
			this.trackingMeasures_cmb.setEnabled(true);
			this.trackingMeasures_cmb.setSelectedIndex(0);
		} else {
			this.trackingMeasures_cmb.setSelectedIndex(-1);
		}
	}

	private void fireEventSelectionPluginImage() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionImage(
		        this.getPlugin(), this.selectedImage);
		this.getPlugin().fireEvent(event);
	}

	private void fireEventSelectionPluginParticleDetectionRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), this.selectedParticleDetectionRun);
		this.getPlugin().fireEvent(event);
	}

	private void fireEventSelectionParticleLinkingRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), this.selectedParticleLinkingRun);
		this.getPlugin().fireEvent(event);
	}

	private void fireEventSelectionTrajectoriesRelinkingRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), this.selectedTrajRelinkingRun);
		this.getPlugin().fireEvent(event);
	}

	private void fireEventSelectionTrajectoriesSegmentationRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), this.selectedTrajSegmentationRun);
		this.getPlugin().fireEvent(event);
	}

	protected void fireEventTrajectories(
	        final List<OmegaTrajectory> trajectories, final boolean selection) {
		// TODO modified as needed
		final OmegaPluginEvent event = new OmegaPluginEventTrajectories(
		        this.getPlugin(), trajectories, selection);
		this.getPlugin().fireEvent(event);
	}

	public void selectImage(final OmegaImage image) {
		this.isHandlingEvent = true;
		final int index = this.images.indexOf(image);
		this.images_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectParticleDetectionRun(
	        final OmegaParticleDetectionRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.particleDetectionRuns.indexOf(analysisRun);
		this.particles_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectParticleLinkingRun(
	        final OmegaParticleLinkingRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.particleLinkingRuns.indexOf(analysisRun);
		this.trajectories_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectTrajectoriesRelinkingRun(
	        final OmegaTrajectoriesRelinkingRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.trajRelinkingRuns.indexOf(analysisRun);
		this.trajectoriesRelinking_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectTrajectoriesSegmentationRun(
	        final OmegaTrajectoriesSegmentationRun analysisRun) {
		this.isHandlingEvent = true;
		final int index = this.trajSegmentationRuns.indexOf(analysisRun);
		this.trajectoriesSegmentation_cmb.setSelectedIndex(index);
		this.isHandlingEvent = false;
	}

	public void selectCurrentTrajectoriesSegmentationRun(
	        final OmegaAnalysisRun analysisRun) {
		this.isHandlingEvent = true;
		this.selectedTrajSegmentationRun = (OmegaTrajectoriesSegmentationRun) analysisRun;
		this.trajectoriesSegmentation_cmb
		        .setSelectedItem(OmegaConstants.OMEGA_SEGMENTATION_CURRENT);
		this.isHandlingEvent = false;
	}

	@Override
	public void updateStatus(final String s) {
		try {
			this.statusPanel.updateStatus(0, s);
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
	}

	@Override
	public void sendEventTrajectories(
	        final List<OmegaTrajectory> selectedTrajectories,
	        final boolean selected) {
		if (selected) {
			this.intensityPanel.setSelectedTrajectories(selectedTrajectories);
			this.mobilityPanel.setSelectedTrajectories(selectedTrajectories);
			this.velocityPanel.setSelectedTrajectories(selectedTrajectories);
			this.diffusivityPanel.setSelectedTrajectories(selectedTrajectories);
			this.motionTypeClassificationPanel
			        .setSelectedTrajectories(selectedTrajectories);
		}
		// intensityPanel.updateTrajectories(selectedTrajectories);
		this.fireEventTrajectories(selectedTrajectories, selected);
	}

	@Override
	public void handleTrajectoryNameChanged() {
		// TODO Auto-generated method stub

	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
		this.tbPanel.setGateway(gateway);
	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		// TODO modify to keep changes if needed
		this.tbPanel.updateTrajectories(trajectories, selection);
		// TODO refactoring ?
		if (selection) {
			this.intensityPanel.setSelectedTrajectories(trajectories);
			this.mobilityPanel.setSelectedTrajectories(trajectories);
			this.velocityPanel.setSelectedTrajectories(trajectories);
			this.diffusivityPanel.setSelectedTrajectories(trajectories);
			this.motionTypeClassificationPanel
			        .setSelectedTrajectories(trajectories);
		}
	}
}
