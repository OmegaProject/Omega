package edu.umassmed.omega.core.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericScrollPane;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.data.coreElements.OmegaFrame;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaElementOverlaysPanel extends GenericScrollPane {

	private static final long serialVersionUID = -8544112231810505798L;

	private final OmegaSidePanel sidePanel;

	private final Map<OmegaAnalysisRun, List<OmegaAnalysisRun>> particleLinkingMap,
	trajectoriesRelinkingMap, trajectoriesSegmentationMap;

	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	private OmegaParticleLinkingRun selectedParticleLinkingRun;
	private OmegaTrajectoriesRelinkingRun selectedTrajectoriesRelinkingRun;
	private OmegaTrajectoriesRelinkingRun previouslySelectedTrajectoriesRelinkingRun;
	private OmegaTrajectoriesSegmentationRun selectedTrajectoriesSegmentationRun;
	private OmegaTrajectoriesSegmentationRun previouslySelectedTrajectoriesSegmentationRun;

	private boolean particlesOverlay, isPopulatingOverlay, isHandlingEvent;
	// Overlay panel element
	private JCheckBox trajOnlyStartingAtT_chk, trajOnlyUpToT_chk,
	        trajOnlyActive_chk;
	private GenericComboBox<String> overlayKind_cmb, overlayPD_cmb,
	overlayPL_cmb, overlayTR_cmb, overlayTS_cmb;

	public OmegaElementOverlaysPanel(final RootPaneContainer parent,
			final OmegaSidePanel sidePanel) {
		super(parent);

		this.sidePanel = sidePanel;

		this.particleLinkingMap = new LinkedHashMap<>();
		this.trajectoriesRelinkingMap = new LinkedHashMap<>();
		this.trajectoriesSegmentationMap = new LinkedHashMap<>();

		this.selectedParticleDetectionRun = null;
		this.selectedParticleLinkingRun = null;
		this.selectedTrajectoriesRelinkingRun = null;
		this.selectedTrajectoriesSegmentationRun = null;

		this.previouslySelectedTrajectoriesRelinkingRun = null;
		this.previouslySelectedTrajectoriesSegmentationRun = null;

		this.particlesOverlay = false;
		this.isPopulatingOverlay = false;
		this.isHandlingEvent = false;

		// this.setLayout(new GridLayout(11, 1));

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(13, 1));

		this.trajOnlyStartingAtT_chk = new JCheckBox(
		        OmegaGUIConstants.SIDEPANEL_TRACKS_SHOWATT);
		this.trajOnlyStartingAtT_chk.setPreferredSize(OmegaConstants.TEXT_SIZE);
		this.trajOnlyStartingAtT_chk.setSize(OmegaConstants.TEXT_SIZE);
		mainPanel.add(this.trajOnlyStartingAtT_chk);

		this.trajOnlyUpToT_chk = new JCheckBox(
		        OmegaGUIConstants.SIDEPANEL_TRACKS_SHOWUPT);
		this.trajOnlyUpToT_chk.setPreferredSize(OmegaConstants.TEXT_SIZE);
		this.trajOnlyUpToT_chk.setSize(OmegaConstants.TEXT_SIZE);
		mainPanel.add(this.trajOnlyUpToT_chk);

		this.trajOnlyActive_chk = new JCheckBox(
		        OmegaGUIConstants.SIDEPANEL_TRACKS_ACTIVEONLY);
		this.trajOnlyActive_chk.setPreferredSize(OmegaConstants.TEXT_SIZE);
		this.trajOnlyActive_chk.setSize(OmegaConstants.TEXT_SIZE);
		mainPanel.add(this.trajOnlyActive_chk);

		final JLabel lbl1 = new JLabel(
		        OmegaGUIConstants.SIDEPANEL_TRACKS_OVERLAY);
		mainPanel.add(lbl1);
		this.overlayKind_cmb = new GenericComboBox<String>(
				this.getParentContainer());
		this.overlayKind_cmb.addItem(OmegaGUIConstants.NONE);
		this.overlayKind_cmb
		        .addItem(OmegaGUIConstants.SIDEPANEL_TRACKS_OVERLAY_PARTICLES);
		this.overlayKind_cmb
		        .addItem(OmegaGUIConstants.SIDEPANEL_TRACKS_OVERLAY_TRACKS);
		this.overlayKind_cmb
		        .addItem(OmegaGUIConstants.SIDEPANEL_TRACKS_OVERLAY_ADJ);
		this.overlayKind_cmb
		        .addItem(OmegaGUIConstants.SIDEPANEL_TRACKS_OVERLAY_SEGM);
		this.overlayKind_cmb.setSelectedIndex(0);
		this.overlayKind_cmb.setEnabled(false);
		this.overlayKind_cmb.setPreferredSize(OmegaConstants.TEXT_SIZE);
		this.overlayKind_cmb.setSize(OmegaConstants.TEXT_SIZE);
		mainPanel.add(this.overlayKind_cmb);

		final JLabel lbl2 = new JLabel(OmegaGUIConstants.SELECT_TRACKS_SPOT);
		mainPanel.add(lbl2);
		this.overlayPD_cmb = new GenericComboBox<String>(
				this.getParentContainer());
		this.overlayPD_cmb.setEnabled(false);
		this.overlayPD_cmb.setPreferredSize(OmegaConstants.TEXT_SIZE);
		this.overlayPD_cmb.setSize(OmegaConstants.TEXT_SIZE);
		mainPanel.add(this.overlayPD_cmb);

		final JLabel lbl3 = new JLabel(
		        OmegaGUIConstants.SELECT_TRACKS_LINKING);
		mainPanel.add(lbl3);
		this.overlayPL_cmb = new GenericComboBox<String>(
				this.getParentContainer());
		this.overlayPL_cmb.setEnabled(false);
		this.overlayPL_cmb.setPreferredSize(OmegaConstants.TEXT_SIZE);
		this.overlayPL_cmb.setSize(OmegaConstants.TEXT_SIZE);
		mainPanel.add(this.overlayPL_cmb);

		final JLabel lbl4 = new JLabel(OmegaGUIConstants.SELECT_TRACKS_ADJ);
		mainPanel.add(lbl4);
		this.overlayTR_cmb = new GenericComboBox<String>(
				this.getParentContainer());
		this.overlayTR_cmb.setEnabled(false);
		this.overlayTR_cmb.setPreferredSize(OmegaConstants.TEXT_SIZE);
		this.overlayTR_cmb.setSize(OmegaConstants.TEXT_SIZE);
		mainPanel.add(this.overlayTR_cmb);

		final JLabel lbl5 = new JLabel(OmegaGUIConstants.SELECT_TRACKS_SEGM);
		mainPanel.add(lbl5);
		this.overlayTS_cmb = new GenericComboBox<String>(
				this.getParentContainer());
		this.overlayTS_cmb.setEnabled(false);
		this.overlayTS_cmb.setPreferredSize(OmegaConstants.TEXT_SIZE);
		this.overlayTS_cmb.setSize(OmegaConstants.TEXT_SIZE);
		mainPanel.add(this.overlayTS_cmb);

		this.setViewportView(mainPanel);
	}

	private void addListeners() {
		this.trajOnlyStartingAtT_chk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaElementOverlaysPanel.this
				        .handleTrajOnlyStartingAtTSelection();
			}
		});
		this.trajOnlyUpToT_chk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaElementOverlaysPanel.this.handleTrajOnlyUpToTSelection();
			}
		});
		this.trajOnlyActive_chk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaElementOverlaysPanel.this.handleTrajOnlyActiveSelection();
			}
		});

		this.overlayKind_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaElementOverlaysPanel.this.selectOverlayKind();
			}
		});
		this.overlayPD_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaElementOverlaysPanel.this.selectParticleDetection();
			}
		});
		this.overlayPL_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaElementOverlaysPanel.this.selectParticleLinking();
			}
		});
		this.overlayTR_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent vte) {
				OmegaElementOverlaysPanel.this.selectTrajectoriesRelinking();
			}
		});
		this.overlayTS_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaElementOverlaysPanel.this.selectTrajectoriesSegmentation();
			}
		});
	}

	private void handleTrajOnlyActiveSelection() {
		this.sidePanel.setShowTrajectoriesOnlyActive(this.trajOnlyActive_chk
				.isSelected());
	}

	private void handleTrajOnlyUpToTSelection() {
		this.sidePanel.setShowTrajectoriesOnlyUpToT(this.trajOnlyUpToT_chk
				.isSelected());
	}

	private void handleTrajOnlyStartingAtTSelection() {
		this.sidePanel
		        .setShowTrajectoriesOnlyStartingAtT(this.trajOnlyStartingAtT_chk
		                .isSelected());
	}

	private void selectOverlayKind() {
		if (this.isHandlingEvent)
			return;
		final String selected = (String) this.overlayKind_cmb.getSelectedItem();
		if (selected.equals("Spots")) {
			this.activateParticlesOverlay();
		} else if (selected.equals("Tracks")) {
			// this.activateParticlesOverlay();
			this.activateTrajectoriesOverlay();
		} else if (selected.equals("Tracks relinking")) {
			// this.activateParticlesOverlay();
			// this.activateTrajectoriesOverlay();
			this.activateTrajectoriesRelinkingOverlay();
		} else if (selected.equals("Tracks segmentation")) {
			// this.activateParticlesOverlay();
			// this.activateTrajectoriesOverlay();
			this.activateTrajectoriesSegmentationOverlay();
		} else {
			this.deactivateOverlays();
		}
	}

	private void selectTrajectoriesSegmentation() {
		// TODO to modify in case there is no relinking
		if (this.isPopulatingOverlay || !this.overlayTS_cmb.isEnabled())
			return;
		this.resetCurrentSelection();
		final List<OmegaROI> particles = this.selectParticleDetectionOverlay();
		this.selectParticleLinkingOverlay();
		final List<OmegaTrajectory> modifiedTrajectories = this
				.selectTrajectoriesRelinkingOverlay();
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = this
				.selectTrajectoriesSegmentationOverlay();
		this.sidePanel.setParticles(particles);
		this.sidePanel.setTrajectories(modifiedTrajectories);
		this.sidePanel.setSegments(segmentsMap);
		if (!this.isHandlingEvent) {
			if (this.selectedTrajectoriesSegmentationRun == null) {
				this.sidePanel
				.sendCoreEventSelectionCurrentTrajectoriesSegmentationRun();
			} else {
				this.sidePanel
				.sendCoreEventSelectionTrajectoriesSegmentationRun();
			}
		}
	}

	private void selectTrajectoriesRelinking() {
		if (this.isPopulatingOverlay || !this.overlayTR_cmb.isEnabled())
			return;
		this.resetCurrentSelection();
		final List<OmegaROI> particles = this.selectParticleDetectionOverlay();
		this.selectParticleLinkingOverlay();
		final List<OmegaTrajectory> modifiedTrajectories = this
				.selectTrajectoriesRelinkingOverlay();
		if (this.overlayKind_cmb.getSelectedItem().equals("Tracks relinking")) {
			this.sidePanel.setParticles(particles);
			this.sidePanel.setTrajectories(modifiedTrajectories);
			if (!this.isHandlingEvent) {
				if (this.selectedTrajectoriesRelinkingRun == null) {
					this.sidePanel
					.sendCoreEventSelectionCurrentTrajectoriesRelinkingRun();
				} else {
					this.sidePanel
					.sendCoreEventSelectionTrajectoriesRelinkingRun();
				}
			}
		} else {
			this.populateTrajectoriesSegmentationOverlay();
		}
	}

	private void selectParticleLinking() {
		if (this.isPopulatingOverlay || !this.overlayPL_cmb.isEnabled())
			return;
		this.resetCurrentSelection();
		final List<OmegaROI> particles = this.selectParticleDetectionOverlay();
		final List<OmegaTrajectory> trajectories = this
				.selectParticleLinkingOverlay();
		if (this.overlayKind_cmb.getSelectedItem().equals("Tracks")) {
			this.sidePanel.setParticles(particles);
			this.sidePanel.setTrajectories(trajectories);
			if (!this.isHandlingEvent) {
				this.sidePanel.sendCoreEventSelectionParticleLinkingRun();
			}
		} else {
			this.populateTrajectoriesRelinkingOverlay();
		}
	}

	private void selectParticleDetection() {
		if (this.isPopulatingOverlay || !this.overlayPD_cmb.isEnabled())
			return;
		this.resetCurrentSelection();
		final List<OmegaROI> particles = this.selectParticleDetectionOverlay();
		if (this.overlayKind_cmb.getSelectedItem().equals("Spots")) {
			this.sidePanel.setParticles(particles);
			if (!this.isHandlingEvent) {
				this.sidePanel.sendCoreEventSelectionParticleDetectionRun();
			}
		} else {
			this.populateParticleLinkingOverlay();
		}
	}

	private void populateTrajectoriesSegmentationOverlay() {
		if (!this.overlayTS_cmb.isEnabled())
			return;
		this.isPopulatingOverlay = true;
		this.overlayTS_cmb.removeAllItems();
		if (this.selectedTrajectoriesRelinkingRun != null) {
			final List<OmegaAnalysisRun> trajSegmentationRuns = this.trajectoriesSegmentationMap
					.get(this.selectedTrajectoriesRelinkingRun);
			for (final OmegaAnalysisRun trajSegmentationRun : trajSegmentationRuns) {
				this.overlayTS_cmb.addItem(trajSegmentationRun.getName());
			}
			this.overlayTS_cmb
			.addItem(OmegaConstants.OMEGA_SEGMENTATION_CURRENT);
		}
		this.isPopulatingOverlay = false;
		if (this.overlayTS_cmb.getItemCount() > 0) {
			this.overlayTS_cmb.setSelectedIndex(0);
		} else {
			this.overlayTS_cmb.setSelectedIndex(-1);
		}
	}

	private void populateTrajectoriesRelinkingOverlay() {
		if (!this.overlayTR_cmb.isEnabled())
			return;
		this.isPopulatingOverlay = true;
		this.overlayTR_cmb.removeAllItems();
		if (this.selectedParticleLinkingRun != null) {
			final List<OmegaAnalysisRun> trajRelinkingRuns = this.trajectoriesRelinkingMap
					.get(this.selectedParticleLinkingRun);
			for (final OmegaAnalysisRun trajRelinkingRun : trajRelinkingRuns) {
				this.overlayTR_cmb.addItem(trajRelinkingRun.getName());
			}
			this.overlayTR_cmb.addItem(OmegaConstants.OMEGA_RELINKING_CURRENT);
		}
		this.isPopulatingOverlay = false;
		if (this.overlayTR_cmb.getItemCount() > 0) {
			this.overlayTR_cmb.setSelectedIndex(0);
		} else {
			this.overlayTR_cmb.setSelectedIndex(-1);
		}
	}

	private void populateParticleLinkingOverlay() {
		if (!this.overlayPL_cmb.isEnabled())
			return;
		this.isPopulatingOverlay = true;
		this.overlayPL_cmb.removeAllItems();
		if (this.selectedParticleDetectionRun != null) {
			final List<OmegaAnalysisRun> particleLinkingRuns = this.particleLinkingMap
					.get(this.selectedParticleDetectionRun);
			for (final OmegaAnalysisRun particleLinkingRun : particleLinkingRuns) {
				this.overlayPL_cmb.addItem(particleLinkingRun.getName());
			}
		}
		this.isPopulatingOverlay = false;
		if (this.overlayPL_cmb.getItemCount() > 0) {
			this.overlayPL_cmb.setSelectedIndex(0);
		} else {
			this.overlayPL_cmb.setSelectedIndex(-1);
		}
	}

	private void populateParticleDetectionOverlay() {
		if (!this.overlayPD_cmb.isEnabled())
			return;
		this.isPopulatingOverlay = true;
		this.overlayPD_cmb.removeAllItems();
		for (final OmegaAnalysisRun particleDetectionRun : this.particleLinkingMap
				.keySet()) {
			this.overlayPD_cmb.addItem(particleDetectionRun.getName());
		}
		this.isPopulatingOverlay = false;
		if (this.overlayPD_cmb.getItemCount() > 0) {
			this.overlayPD_cmb.setSelectedIndex(0);
		} else {
			this.overlayPD_cmb.setSelectedIndex(-1);
		}
	}

	private void deactivateOverlays() {
		this.particlesOverlay = false;
		this.overlayPD_cmb.setEnabled(false);
		this.overlayPD_cmb.setSelectedIndex(-1);
		this.overlayPL_cmb.setEnabled(false);
		this.overlayPL_cmb.setSelectedIndex(-1);
		this.overlayTR_cmb.setEnabled(false);
		this.overlayTR_cmb.setSelectedIndex(-1);
		this.sidePanel.setParticles(null);
		this.sidePanel.setTrajectories(null);
		this.sidePanel.setSegments(null);
	}

	private void activateTrajectoriesSegmentationOverlay() {
		this.overlayPD_cmb.setEnabled(true);
		this.particlesOverlay = true;
		this.populateParticleDetectionOverlay();
		this.overlayPL_cmb.setEnabled(true);
		this.populateParticleLinkingOverlay();
		this.overlayTR_cmb.setEnabled(true);
		this.populateTrajectoriesRelinkingOverlay();
		this.overlayTS_cmb.setEnabled(true);
		this.populateTrajectoriesSegmentationOverlay();
	}

	private void activateTrajectoriesRelinkingOverlay() {
		this.overlayTS_cmb.setEnabled(false);
		this.overlayTS_cmb.setSelectedIndex(-1);
		this.overlayPD_cmb.setEnabled(true);
		this.particlesOverlay = true;
		this.populateParticleDetectionOverlay();
		this.overlayPL_cmb.setEnabled(true);
		this.populateParticleLinkingOverlay();
		this.overlayTR_cmb.setEnabled(true);
		this.populateTrajectoriesRelinkingOverlay();
	}

	private void activateTrajectoriesOverlay() {
		this.overlayTS_cmb.setEnabled(false);
		this.overlayTS_cmb.setSelectedIndex(-1);
		this.overlayTR_cmb.setEnabled(false);
		this.overlayTR_cmb.setSelectedIndex(-1);
		this.overlayPD_cmb.setEnabled(true);
		this.particlesOverlay = true;
		this.populateParticleDetectionOverlay();
		this.overlayPL_cmb.setEnabled(true);
		this.populateParticleLinkingOverlay();
	}

	private void activateParticlesOverlay() {
		this.overlayTS_cmb.setEnabled(false);
		this.overlayTS_cmb.setSelectedIndex(-1);
		this.overlayTR_cmb.setEnabled(false);
		this.overlayTR_cmb.setSelectedIndex(-1);
		this.overlayPL_cmb.setEnabled(false);
		this.overlayPL_cmb.setSelectedIndex(-1);
		this.sidePanel.setTrajectories(null);
		this.sidePanel.setSegments(null);
		this.overlayPD_cmb.setEnabled(true);
		this.particlesOverlay = true;
		this.populateParticleDetectionOverlay();
	}

	// final ImageData image, final RenderingEnginePrx engine

	private void addOverlayControl() {
		if (this.particleLinkingMap.isEmpty()) {
			this.overlayKind_cmb.setEnabled(true);
			this.overlayKind_cmb.setSelectedIndex(0);
			this.deactivateOverlays();
		} else {
			if (!this.overlayKind_cmb.isEnabled()) {
				this.overlayKind_cmb.setEnabled(true);
				this.overlayKind_cmb.setSelectedIndex(0);
			} else {
				if (!this.overlayKind_cmb.getSelectedItem().equals("None")) {
					this.overlayPD_cmb.setSelectedIndex(0);
				}
			}
		}
	}

	private void removeOverlayControl() {
		this.overlayKind_cmb.setEnabled(false);
		this.overlayKind_cmb.setSelectedIndex(0);
		this.deactivateOverlays();
		this.overlayPD_cmb.removeAllItems();
		this.overlayPL_cmb.removeAllItems();
		this.overlayTR_cmb.removeAllItems();
	}

	public void setOverlayControl(final boolean enabled) {
		// this.engine = engine;
		// this.image = image;
		if (!enabled) {
			this.removeOverlayControl();
		} else {
			this.addOverlayControl();
		}
	}

	private Map<OmegaTrajectory, List<OmegaSegment>> selectTrajectoriesSegmentationOverlay() {
		if (this.isPopulatingOverlay)
			return null;
		Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = null;
		final String s = (String) this.overlayTS_cmb.getSelectedItem();
		if (this.selectedTrajectoriesRelinkingRun != null) {
			final List<OmegaAnalysisRun> trajSegmentationRuns = this.trajectoriesSegmentationMap
					.get(this.selectedTrajectoriesRelinkingRun);
			for (final OmegaAnalysisRun trajSegmentationRun : trajSegmentationRuns) {
				if (!trajSegmentationRun.getName().equals(s)) {
					continue;
				}
				final OmegaTrajectoriesSegmentationRun trajectoriesSegmentationRun = (OmegaTrajectoriesSegmentationRun) trajSegmentationRun;
				this.selectedTrajectoriesSegmentationRun = trajectoriesSegmentationRun;
				segmentsMap = trajectoriesSegmentationRun
						.getResultingSegments();
				return segmentsMap;
			}
		}
		return null;
		// this.canvas.setTrajectories(modifiedTrajectories);
	}

	private List<OmegaTrajectory> selectTrajectoriesRelinkingOverlay() {
		if (this.isPopulatingOverlay)
			return null;
		List<OmegaTrajectory> modifiedTrajectories = null;
		final String s = (String) this.overlayTR_cmb.getSelectedItem();
		if (this.selectedParticleLinkingRun != null) {
			final List<OmegaAnalysisRun> modifiedTrajectoriesRun = this.trajectoriesRelinkingMap
					.get(this.selectedParticleLinkingRun);
			for (final OmegaAnalysisRun trajRelinkingRun : modifiedTrajectoriesRun) {
				if (!trajRelinkingRun.getName().equals(s)) {
					continue;
				}
				final OmegaTrajectoriesRelinkingRun trajectoriesRelinkingRun = (OmegaTrajectoriesRelinkingRun) trajRelinkingRun;
				this.selectedTrajectoriesRelinkingRun = trajectoriesRelinkingRun;
				modifiedTrajectories = trajectoriesRelinkingRun
						.getResultingTrajectories();
				return modifiedTrajectories;
			}
		}
		return null;
		// this.canvas.setTrajectories(modifiedTrajectories);
	}

	private List<OmegaTrajectory> selectParticleLinkingOverlay() {
		if (this.isPopulatingOverlay)
			return null;
		List<OmegaTrajectory> trajectories = null;
		final String s = (String) this.overlayPL_cmb.getSelectedItem();
		if (this.selectedParticleDetectionRun != null) {
			final List<OmegaAnalysisRun> trajectoriesRuns = this.particleLinkingMap
					.get(this.selectedParticleDetectionRun);
			for (final OmegaAnalysisRun pLinkingRun : trajectoriesRuns) {
				if (!pLinkingRun.getName().equals(s)) {
					continue;
				}
				final OmegaParticleLinkingRun particleLinkingRun = (OmegaParticleLinkingRun) pLinkingRun;
				this.selectedParticleLinkingRun = particleLinkingRun;
				trajectories = particleLinkingRun.getResultingTrajectories();
				return trajectories;
			}
		}
		return null;
		// this.canvas.setTrajectories(trajectories);
	}

	protected List<OmegaROI> getFrameParticlesOverlay(final int t) {
		for (final OmegaFrame frame : this.selectedParticleDetectionRun
				.getResultingParticles().keySet()) {
			if (frame.getIndex() == t) {
				final List<OmegaROI> resultingParticles = this.selectedParticleDetectionRun
						.getResultingParticles().get(frame);
				return resultingParticles;
			}
		}
		return null;
	}

	private List<OmegaROI> selectParticleDetectionOverlay() {
		if (this.isPopulatingOverlay)
			return null;
		List<OmegaROI> particles = null;
		final String s = (String) this.overlayPD_cmb.getSelectedItem();
		for (final OmegaAnalysisRun pDetectionRun : this.particleLinkingMap
				.keySet()) {
			if (!pDetectionRun.getName().equals(s)) {
				continue;
			}
			final OmegaParticleDetectionRun particleDetectionRun = (OmegaParticleDetectionRun) pDetectionRun;
			this.selectedParticleDetectionRun = particleDetectionRun;
			particles = this.getFrameParticlesOverlay(this.sidePanel
					.getCurrentT());
			if (particles != null)
				return particles;
		}

		return null;
		// this.canvas.setParticles(particles);
	}

	public void selectParticleDetectionRun(
			final OmegaParticleDetectionRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlayKind_cmb.setSelectedIndex(1);
		this.activateParticlesOverlay();
		for (int i = 0; i < this.overlayPD_cmb.getItemCount(); i++) {
			final String s = this.overlayPD_cmb.getItemAt(i);
			if (analysisRun.getName().equals(s)) {
				this.overlayPD_cmb.setSelectedIndex(i);
				this.isHandlingEvent = false;
				return;
			}
		}
		this.isHandlingEvent = false;
		// TODO throw error
		// What error? Why?
	}

	public void selectParticleLinkingRun(
			final OmegaParticleLinkingRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlayKind_cmb.setSelectedIndex(2);
		this.activateParticlesOverlay();
		this.activateTrajectoriesOverlay();
		for (int i = 0; i < this.overlayPL_cmb.getItemCount(); i++) {
			final String s = this.overlayPL_cmb.getItemAt(i);
			if (analysisRun.getName().equals(s)) {
				this.overlayPL_cmb.setSelectedIndex(i);
				this.isHandlingEvent = false;
				return;
			}
		}
		this.isHandlingEvent = false;
		// TODO throw error
		// What error? Why?
	}

	public void selectTrajectoriesRelinkingRun(
			final OmegaTrajectoriesRelinkingRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlayKind_cmb.setSelectedIndex(3);
		this.activateParticlesOverlay();
		this.activateTrajectoriesOverlay();
		this.activateTrajectoriesRelinkingOverlay();

		if (analysisRun == null) {
			// TODO handle as an error?
			this.overlayTR_cmb.setSelectedIndex(0);
			this.isHandlingEvent = false;
			return;
		}

		for (int i = 0; i < this.overlayTR_cmb.getItemCount(); i++) {
			final String s = this.overlayTR_cmb.getItemAt(i);
			if (analysisRun.getName().equals(s)) {
				this.overlayTR_cmb.setSelectedIndex(i);
				this.isHandlingEvent = false;
				return;
			}
		}

		this.isHandlingEvent = false;
		// TODO handle as an error?
	}

	public void selectCurrentTrajectoriesRelinkingRun(
			final List<OmegaTrajectory> trajectories) {
		this.isHandlingEvent = true;
		this.overlayKind_cmb.setSelectedIndex(3);
		this.activateParticlesOverlay();
		this.activateTrajectoriesOverlay();
		this.activateTrajectoriesRelinkingOverlay();
		this.overlayTR_cmb
		.setSelectedItem(OmegaConstants.OMEGA_RELINKING_CURRENT);
		this.isHandlingEvent = false;
	}

	public void selectTrajectoriesSegmentationRun(
			final OmegaTrajectoriesSegmentationRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlayKind_cmb.setSelectedIndex(4);
		this.activateParticlesOverlay();
		this.activateTrajectoriesOverlay();
		this.activateTrajectoriesRelinkingOverlay();
		this.activateTrajectoriesSegmentationOverlay();

		if (analysisRun == null) {
			// TODO handle as an error?
			this.overlayTS_cmb.setSelectedIndex(0);
			this.isHandlingEvent = false;
			return;
		}

		for (int i = 0; i < this.overlayTS_cmb.getItemCount(); i++) {
			final String s = this.overlayTS_cmb.getItemAt(i);
			if (analysisRun.getName().equals(s)) {
				this.overlayTS_cmb.setSelectedIndex(i);
				this.isHandlingEvent = false;
				return;
			}
		}

		this.isHandlingEvent = false;
		// TODO handle as an error?
	}

	public void selectCurrentSegmentationRun(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		this.isHandlingEvent = true;
		this.overlayKind_cmb.setSelectedIndex(4);
		this.activateParticlesOverlay();
		this.activateTrajectoriesOverlay();
		this.activateTrajectoriesRelinkingOverlay();
		this.activateTrajectoriesSegmentationOverlay();
		this.overlayTS_cmb
		.setSelectedItem(OmegaConstants.OMEGA_SEGMENTATION_CURRENT);
		this.isHandlingEvent = false;
	}

	private void resetCurrentSelection() {
		this.selectedParticleDetectionRun = null;
		this.selectedParticleLinkingRun = null;
		if (this.selectedTrajectoriesRelinkingRun != null) {
			this.previouslySelectedTrajectoriesRelinkingRun = this.selectedTrajectoriesRelinkingRun;
		}
		this.selectedTrajectoriesRelinkingRun = null;
		if (this.selectedTrajectoriesSegmentationRun != null) {
			this.previouslySelectedTrajectoriesSegmentationRun = this.selectedTrajectoriesSegmentationRun;
		}
		this.selectedTrajectoriesSegmentationRun = null;
	}

	public void updateMap(final List<OmegaAnalysisRun> loadedAnalysisRuns,
			final OmegaAnalysisRun analysisRun) {
		this.updateMap(loadedAnalysisRuns, analysisRun, this.particleLinkingMap);
	}

	private void updateMap(final List<OmegaAnalysisRun> loadedAnalysisRuns,
			final OmegaAnalysisRun analysisRun,
			final Map<OmegaAnalysisRun, List<OmegaAnalysisRun>> targetMap) {
		if (!loadedAnalysisRuns.contains(analysisRun))
			return;
		List<OmegaAnalysisRun> subAnalysisRuns;
		if (targetMap.containsKey(analysisRun)) {
			subAnalysisRuns = targetMap.get(analysisRun);
		} else {
			subAnalysisRuns = new ArrayList<>();
		}
		for (final OmegaAnalysisRun subAnalysisRun : analysisRun
				.getAnalysisRuns()) {
			if (!loadedAnalysisRuns.contains(subAnalysisRun)) {
				continue;
			}
			Map<OmegaAnalysisRun, List<OmegaAnalysisRun>> subTargetMap = null;
			if (subAnalysisRun instanceof OmegaTrajectoriesSegmentationRun) {
				// subTargetMap = this.trajectoriesSegmentationMap;
			} else if (subAnalysisRun instanceof OmegaTrajectoriesRelinkingRun) {
				subTargetMap = this.trajectoriesSegmentationMap;
			} else if (subAnalysisRun instanceof OmegaParticleLinkingRun) {
				subTargetMap = this.trajectoriesRelinkingMap;
			}
			if (subTargetMap != null) {
				this.updateMap(loadedAnalysisRuns, subAnalysisRun, subTargetMap);
			}
			subAnalysisRuns.add(subAnalysisRun);
		}
		targetMap.put(analysisRun, subAnalysisRuns);
	}

	public void resizePanel(final int width, final int height) {
		final Dimension newDim = new Dimension(width, height);
		this.setPreferredSize(newDim);
		this.setSize(newDim);
	}

	// public void rescale(final int width) {
	// final Dimension dim = new Dimension(width, 20);
	// this.overlayKind_cmb.setPreferredSize(dim);
	// this.overlayKind_cmb.setSize(dim);
	// this.overlayPD_cmb.setPreferredSize(dim);
	// this.overlayPD_cmb.setSize(dim);
	// this.overlayPL_cmb.setPreferredSize(dim);
	// this.overlayPL_cmb.setSize(dim);
	// this.overlayTR_cmb.setPreferredSize(dim);
	// this.overlayTR_cmb.setSize(dim);
	// this.overlayTS_cmb.setPreferredSize(dim);
	// this.overlayTS_cmb.setSize(dim);
	// }

	public boolean isParticlesOverlay() {
		return this.particlesOverlay;
	}

	public void resetMaps() {
		this.particleLinkingMap.clear();
		this.trajectoriesRelinkingMap.clear();
		this.trajectoriesSegmentationMap.clear();
	}

	public OmegaAnalysisRun getSelectedPDRun() {
		return this.selectedParticleDetectionRun;
	}

	public OmegaAnalysisRun getSelectedPLRun() {
		return this.selectedParticleLinkingRun;
	}

	public OmegaAnalysisRun getSelectedTRRun() {
		return this.selectedTrajectoriesRelinkingRun;
	}

	public OmegaAnalysisRun getPreviouslySelectedTRRun() {
		return this.previouslySelectedTrajectoriesRelinkingRun;
	}

	public OmegaAnalysisRun getSelectedTSRun() {
		return this.selectedTrajectoriesSegmentationRun;
	}

	public OmegaAnalysisRun getPreviouslySelectedTSRun() {
		return this.previouslySelectedTrajectoriesSegmentationRun;
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.overlayKind_cmb.updateParentContainer(parent);
		this.overlayPD_cmb.updateParentContainer(parent);
		this.overlayPL_cmb.updateParentContainer(parent);
		this.overlayTR_cmb.updateParentContainer(parent);
		this.overlayTS_cmb.updateParentContainer(parent);
	}
}
