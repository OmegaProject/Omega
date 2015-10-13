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
package edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.constants.StatsConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresDiffusivityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventTrajectories;
import edu.umassmed.omega.commons.exceptions.OmegaPluginExceptionStatusPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.GenericSegmentsBrowserPanel;
import edu.umassmed.omega.commons.gui.GenericStatusPanel;
import edu.umassmed.omega.commons.gui.interfaces.GenericSegmentsBrowserContainerInterface;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.TMDConstants;
import edu.umassmed.omega.trackingMeasuresIntensityPlugin.TMIConstants;

public class TMDPluginPanel extends GenericPluginPanel implements
GenericSegmentsBrowserContainerInterface {

	private static final long serialVersionUID = -5740459087763362607L;

	private OmegaGateway gateway;

	private TMDGraphPanel graphPanel;
	private TMDMotionTypeClassificationGraphPanel mtcGraphPanel;
	private GenericSegmentsBrowserPanel sbPanel;
	private TMDRunPanel runPanel;
	private GenericStatusPanel statusPanel;

	private JComboBox<String> images_cmb, particles_cmb, trajectories_cmb,
	        trajectoriesRelinking_cmb, trajectoriesSegmentation_cmb,
	        trackingMeasures_cmb;
	private boolean popImages, popParticles, popTrajectories, popTrajRelinking,
	        popTrajSegmentation, popTrackingMeasures;

	private boolean isHandlingEvent;

	private JTabbedPane tabbedPane, graphTabbedPane;

	private List<OmegaImage> images;
	private OrphanedAnalysisContainer orphanedAnalysis;
	private OmegaAnalysisRunContainer selectedImage;
	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	final List<OmegaParticleDetectionRun> particleDetectionRuns;
	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	final List<OmegaParticleLinkingRun> particleLinkingRuns;
	private OmegaParticleLinkingRun selectedParticleLinkingRun;
	final List<OmegaTrajectoriesRelinkingRun> trajRelinkingRuns;
	private OmegaTrajectoriesRelinkingRun selectedTrajRelinkingRun;
	final List<OmegaTrajectoriesSegmentationRun> trajSegmentationRuns;
	private OmegaTrajectoriesSegmentationRun selectedTrajSegmentationRun;
	final List<OmegaTrackingMeasuresDiffusivityRun> trackingMeasuresRuns;
	private OmegaTrackingMeasuresDiffusivityRun selectedTrackingMeasuresRun;

	private JPanel topPanel;
	private JMenuItem hideDataSelection_mItm;

	public TMDPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final OmegaGateway gateway,
	        final List<OmegaImage> images,
			final OrphanedAnalysisContainer orphanedAnalysis,
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
		this.orphanedAnalysis = orphanedAnalysis;
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
		this.createMenu();
		this.createAndAddWidgets();
		// this.loadedDataBrowserPanel.updateTree(images);

		this.addListeners();

		this.populateImagesCombo();
	}

	private void createMenu() {
		final JMenuBar menuBar = this.getMenu();
		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			final JMenu menu = menuBar.getMenu(i);
			if (!menu.getText().equals(OmegaGUIConstants.MENU_VIEW)) {
				continue;
			}
			this.hideDataSelection_mItm = new JMenuItem(
			        OmegaGUIConstants.MENU_VIEW_HIDE_DATA_SELECTION);
			menu.add(this.hideDataSelection_mItm);
		}
	}

	private void createAndAddWidgets() {
		// this.segmentPreferencesDialog = new TSSegmentPreferencesDialog(this,
		// this.getParentContainer(), this.segmTypesList);

		this.topPanel = new JPanel();
		this.topPanel.setLayout(new GridLayout(6, 1));

		final JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
		final JLabel lbl1 = new JLabel(TMDConstants.SELECT_IMAGE);
		lbl1.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p1.add(lbl1, BorderLayout.WEST);
		this.images_cmb = new JComboBox<String>();
		this.images_cmb.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.images_cmb.setEnabled(false);
		p1.add(this.images_cmb, BorderLayout.CENTER);
		this.topPanel.add(p1);

		final JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		final JLabel lbl2 = new JLabel(TMDConstants.SELECT_TRACKS_SPOT);
		lbl2.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p2.add(lbl2, BorderLayout.WEST);
		this.particles_cmb = new JComboBox<String>();
		this.particles_cmb
		.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.particles_cmb.setEnabled(false);
		p2.add(this.particles_cmb, BorderLayout.CENTER);
		this.topPanel.add(p2);

		final JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		final JLabel lbl3 = new JLabel(TMDConstants.SELECT_TRACKS_LINKING);
		lbl3.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p3.add(lbl3, BorderLayout.WEST);
		this.trajectories_cmb = new JComboBox<String>();
		this.trajectories_cmb
		.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectories_cmb.setEnabled(false);
		p3.add(this.trajectories_cmb, BorderLayout.CENTER);
		this.topPanel.add(p3);

		final JPanel p4 = new JPanel();
		p4.setLayout(new BorderLayout());
		final JLabel lbl4 = new JLabel(TMDConstants.SELECT_TRACKS_ADJ);
		lbl4.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p4.add(lbl4, BorderLayout.WEST);
		this.trajectoriesRelinking_cmb = new JComboBox<String>();
		this.trajectoriesRelinking_cmb
		.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectoriesRelinking_cmb.setEnabled(false);
		p4.add(this.trajectoriesRelinking_cmb, BorderLayout.CENTER);
		this.topPanel.add(p4);

		final JPanel p5 = new JPanel();
		p5.setLayout(new BorderLayout());
		final JLabel lbl5 = new JLabel(TMDConstants.SELECT_TRACKS_SEGM);
		lbl5.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p5.add(lbl5, BorderLayout.WEST);
		this.trajectoriesSegmentation_cmb = new JComboBox<String>();
		this.trajectoriesSegmentation_cmb
		.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trajectoriesSegmentation_cmb.setEnabled(false);
		p5.add(this.trajectoriesSegmentation_cmb, BorderLayout.CENTER);
		this.topPanel.add(p5);

		final JPanel p6 = new JPanel();
		p6.setLayout(new BorderLayout());
		final JLabel lbl6 = new JLabel(TMIConstants.SELECT_TRACK_MEASURES);
		lbl6.setPreferredSize(OmegaConstants.TEXT_SIZE);
		p6.add(lbl6, BorderLayout.WEST);
		this.trackingMeasures_cmb = new JComboBox<String>();
		this.trackingMeasures_cmb
		.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		this.trackingMeasures_cmb.setEnabled(false);
		p6.add(this.trackingMeasures_cmb, BorderLayout.CENTER);
		this.topPanel.add(p6);

		this.add(this.topPanel, BorderLayout.NORTH);

		this.tabbedPane = new JTabbedPane();

		this.sbPanel = new GenericSegmentsBrowserPanel(
				this.getParentContainer(), this, this.gateway, true, true);
		this.tabbedPane.add(StatsConstants.TAB_TRACK_BROWSER, this.sbPanel);

		this.runPanel = new TMDRunPanel(this.getParentContainer(), this);
		this.tabbedPane.add(StatsConstants.TAB_RUN, this.runPanel);

		final JPanel graphMainPanel = new JPanel();
		graphMainPanel.setLayout(new BorderLayout());

		// graphMainPanel.add(p6, BorderLayout.NORTH);

		this.graphTabbedPane = new JTabbedPane();

		this.graphPanel = new TMDGraphPanel(this.getParentContainer(), this,
				null);
		this.graphTabbedPane.add("Diffusivity", this.graphPanel);

		this.mtcGraphPanel = new TMDMotionTypeClassificationGraphPanel(
				this.getParentContainer(), this, null);
		this.graphTabbedPane.add("Motion type", this.mtcGraphPanel);

		graphMainPanel.add(this.graphTabbedPane, BorderLayout.CENTER);

		this.tabbedPane.add(StatsConstants.TAB_GRAPH, graphMainPanel);

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
				TMDPluginPanel.this.selectImage();
			}
		});
		this.particles_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMDPluginPanel.this.selectParticleDetectionRun();
			}
		});
		this.trajectories_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMDPluginPanel.this.selectParticleLinkingRun();
			}
		});
		this.trajectoriesRelinking_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMDPluginPanel.this.selectTrajectoriesRelinkingRun();
			}
		});
		this.trajectoriesSegmentation_cmb
		        .addActionListener(new ActionListener() {
			        @Override
			        public void actionPerformed(final ActionEvent e) {
				        TMDPluginPanel.this.selectTrajectoriesSegmentationRun();
			        }
		        });
		this.trackingMeasures_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMDPluginPanel.this.selectTrackingMeasuresRun();
			}
		});
		this.hideDataSelection_mItm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMDPluginPanel.this.handleHideDataSelection();
			}
		});
	}

	private void handleHideDataSelection() {
		if (this.hideDataSelection_mItm.getText().equals(
				OmegaGUIConstants.MENU_VIEW_HIDE_DATA_SELECTION)) {
			this.remove(this.topPanel);
			this.hideDataSelection_mItm
			.setText(OmegaGUIConstants.MENU_VIEW_SHOW_DATA_SELECTION);
		} else {
			this.add(this.topPanel, BorderLayout.NORTH);
			this.hideDataSelection_mItm
			.setText(OmegaGUIConstants.MENU_VIEW_HIDE_DATA_SELECTION);
		}
		this.revalidate();
		this.repaint();
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
		if ((this.images == null) || (index >= this.images.size())) {
			this.selectedImage = this.orphanedAnalysis;
			this.sbPanel.setImage(null);
		} else {
			this.selectedImage = this.images.get(index);
			this.sbPanel.setImage((OmegaImage) this.selectedImage);
			final OmegaImage selectedImage = (OmegaImage) this.selectedImage;
			this.graphPanel.setMaximumT(selectedImage.getDefaultPixels()
			        .getSizeT());
		}
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
			// this.populateTrackingMeasuresCombo();
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
		// this.populateTrackingMeasuresCombo();
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

		// TODO maybe has to be moved after the rework
		if (this.selectedImage instanceof OrphanedAnalysisContainer) {
			int maxT = 0;
			int maxX = 0, maxY = 0;
			for (final OmegaTrajectory track : this.selectedTrajRelinkingRun
			        .getResultingTrajectories()) {
				if (maxT < track.getLength()) {
					maxT = track.getLength();
				}
				for (final OmegaROI roi : track.getROIs()) {
					final int x = (int) (roi.getX() + 1);
					final int y = (int) (roi.getY() + 1);
					if (maxX < x) {
						maxX = x;
					}
					if (maxY < y) {
						maxY = y;
					}
				}
			}
			this.graphPanel.setMaximumT(maxT);
		}
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
		this.populateTrackingMeasuresCombo();

		this.sbPanel.updateSegments(
		        this.selectedTrajSegmentationRun.getResultingSegments(),
				this.selectedTrajSegmentationRun.getSegmentationTypes(), false);
		this.graphPanel
		.updateSelectedSegmentationTypes(this.selectedTrajSegmentationRun
				.getSegmentationTypes());
		this.graphPanel.setSegmentsMap(this.selectedTrajSegmentationRun
		        .getResultingSegments());
		this.mtcGraphPanel.setSegmentsMap(
		        this.selectedTrajSegmentationRun.getResultingSegments(),
		        this.selectedTrajSegmentationRun.getSegmentationTypes());
		this.mtcGraphPanel
		.updateSelectedSegmentationTypes(this.selectedTrajSegmentationRun
				.getSegmentationTypes());
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

		this.graphPanel
		        .updateSelectedTrackingMeasuresRun(this.selectedTrackingMeasuresRun);
		this.mtcGraphPanel
		.updateSelectedTrackingMeasuresRun(this.selectedTrackingMeasuresRun);
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.sbPanel.updateParentContainer(parent);
		this.graphPanel.updateParentContainer(parent);
	}

	@Override
	public void onCloseOperation() {

	}

	public void updateCombos(final List<OmegaImage> images,
	        final OrphanedAnalysisContainer orphanedAnalysis,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this.isHandlingEvent = true;
		this.images = images;
		this.orphanedAnalysis = orphanedAnalysis;
		this.loadedAnalysisRuns = analysisRuns;

		this.populateImagesCombo();
		this.isHandlingEvent = false;
	}

	private void populateImagesCombo() {
		this.popImages = true;
		this.images_cmb.removeAllItems();
		this.selectedImage = null;
		this.images_cmb.setSelectedIndex(-1);
		if (((this.images == null) || this.images.isEmpty())
				&& this.orphanedAnalysis.isEmpty()) {
			this.images_cmb.setEnabled(false);
			this.populateParticlesCombo();
			this.popImages = false;
			return;

		}
		this.images_cmb.setEnabled(true);

		if (this.images != null) {
			for (final OmegaImage image : this.images) {
				this.images_cmb.addItem(image.getName());
			}
		}
		this.images_cmb.addItem(OmegaGUIConstants.PLUGIN_ORPHANED_ANALYSES);
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
			this.popParticles = false;
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
			this.popParticles = false;
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
			// this.populateTrackingMeasuresCombo();
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
			// this.populateTrackingMeasuresCombo();
			this.popTrajectories = false;
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
			this.popTrajRelinking = false;
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
			this.popTrajRelinking = false;
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
			this.populateTrackingMeasuresCombo();
			this.popTrajSegmentation = false;
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
		if (this.trajSegmentationRuns.isEmpty()) {
			this.trajectoriesSegmentation_cmb.setEnabled(false);
			this.populateTrackingMeasuresCombo();
			this.popTrajSegmentation = false;
			return;
		}

		this.popTrajSegmentation = false;
		if (this.trajectoriesSegmentation_cmb.getItemCount() > 0) {
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
		if (this.selectedTrajSegmentationRun == null) {
			this.trackingMeasures_cmb.setEnabled(false);
			this.popTrackingMeasures = false;
			return;
		}
		for (final OmegaAnalysisRun analysisRun : this.loadedAnalysisRuns) {
			if (this.selectedTrajSegmentationRun.getAnalysisRuns().contains(
			        analysisRun)
			        && (analysisRun instanceof OmegaTrackingMeasuresDiffusivityRun)) {
				this.trackingMeasuresRuns
				        .add((OmegaTrackingMeasuresDiffusivityRun) analysisRun);
				this.trackingMeasures_cmb.addItem(analysisRun.getName());
			}
		}
		if (this.trackingMeasuresRuns.isEmpty()) {
			this.trackingMeasures_cmb.setEnabled(false);
			this.popTrackingMeasures = false;
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

	public void selectImage(final OmegaAnalysisRunContainer image) {
		this.isHandlingEvent = true;
		int index = -1;
		if (this.images != null) {
			index = this.images.indexOf(image);
		}
		if (index == -1) {
			this.images_cmb.setSelectedItem(this.images_cmb.getItemCount());
		} else {
			this.images_cmb.setSelectedIndex(index);
		}
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
			// this.populateTrackingMeasuresCombo();
		} catch (final OmegaPluginExceptionStatusPanel ex) {
			OmegaLogFileManager.handlePluginException(this.getPlugin(), ex);
		}
	}

	@Override
	public void sendEventTrajectories(
			final List<OmegaTrajectory> selectedTrajectories,
			final boolean selected) {
		// if (selected) {
		final Map<OmegaTrajectory, List<OmegaSegment>> segments = new LinkedHashMap<>();
		for (final OmegaTrajectory track : selectedTrajectories) {
			segments.put(track, this.sbPanel.getSegments().get(track));
		}
		this.graphPanel.setSelectedSegments(segments);
		this.mtcGraphPanel.setSelectedSegments(segments);
		// }
		// graphPanel.updateTrajectories(selectedTrajectories);
		this.fireEventTrajectories(selectedTrajectories, selected);
	}

	@Override
	public void sendEventSegments(
	        final Map<OmegaTrajectory, List<OmegaSegment>> selectedSegments,
	        final boolean selected) {
		this.graphPanel.setSelectedSegments(selectedSegments);
		this.mtcGraphPanel.setSelectedSegments(selectedSegments);
		// this.fireEventSegments(selectedSegments, selected);
	}

	@Override
	public void handleTrajectoryNameChanged() {
		// TODO Auto-generated method stub

	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
		this.sbPanel.setGateway(gateway);
	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		// TODO modify to keep changes if needed
		// this.sbPanel.updateTrajectories(trajectories, selection);
		// TODO refactoring ?
		// if (selection) {
		// final Map<OmegaTrajectory, List<OmegaSegment>> segments = new
		// LinkedHashMap<>();
		// for (final OmegaTrajectory track : trajectories) {
		// segments.put(track, new ArrayList<OmegaSegment>());
		// }
		// this.graphPanel.setSelectedSegments(segments);
		// this.mtcGraphPanel.setSelectedSegments(segments);
		// }
	}

	public OmegaParticleLinkingRun getSelectedLinkingRun() {
		return this.selectedParticleLinkingRun;
	}

	public OmegaTrajectoriesSegmentationRun getSelectedSegmentationRun() {
		return this.selectedTrajSegmentationRun;
	}

	public Map<OmegaTrajectory, List<OmegaSegment>> getSegments() {
		if (!this.sbPanel.getSelectedSegments().isEmpty())
			return this.sbPanel.getSelectedSegments();
		return this.selectedTrajSegmentationRun.getResultingSegments();
	}
}
