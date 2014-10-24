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
package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationParticleDetectionRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationParticleLinkingRunSelectionEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationTrajectoriesEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaApplicationTrajectoriesManagerRunSelectionEvent;
import edu.umassmed.omega.commons.gui.GenericFrame;
import edu.umassmed.omega.commons.gui.GenericImageCanvas;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.utilities.OmegaImageRenderingUtilities;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaTrajectoriesManagerRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaElementImagePanel extends GenericPanel {

	private static final long serialVersionUID = 7327403424923046398L;

	private OmegaElementInformationsPanel infoPanel;

	private OmegaImagePixels pixels;
	private OmegaGateway gateway;
	/** The image canvas. */
	private JScrollPane canvasSP;
	private GenericImageCanvas canvas;

	private JTabbedPane tabPane;
	private JPanel imageControlsPanel, imageOverlayPanel;

	private final Map<OmegaParticleDetectionRun, List<OmegaParticleLinkingRun>> trajectoriesMap;
	private final Map<OmegaParticleLinkingRun, List<OmegaTrajectoriesManagerRun>> modifiedTrajectoriesMap;

	private OmegaParticleDetectionRun selectedParticleDetectionRun;
	private OmegaParticleLinkingRun selectedParticleLinkingRun;
	private OmegaTrajectoriesManagerRun selectedTrajectoriesManagerRun;

	private boolean isPopulatingOverlay, particlesOverlay, isHandlingEvent;

	/** The compression level. */
	private static final float COMPRESSION = 0.5f;
	/** The slider to select the z-section and t-section. */
	private JSlider z_slider, t_slider;
	/** The label showing the Z and T values */
	private JLabel z_label, t_label;
	/** Box indicating to render the image as compressed or not. */
	private JCheckBox compressed;
	/** JPanel displaying all the available channels **/
	private JPanel channelsPanel;
	/** Number of channels of the image **/
	private int channelsNumber;
	/** Checkboxs rappresenting the channels **/
	private JCheckBox[] channels;
	/** The current maximum Z of the image. **/
	private int currentMaximumZValue;
	/**
	 * The current sizeX, sizeY, sizeZ (i.e.: micron between pixels/planes) of
	 * the image (if present).
	 */
	private Double sizeX = null;
	private Double sizeY = null;
	private Double sizeZ = null;
	/**
	 * The current maximum T of the image.
	 */
	private int currentMaximumTValue;
	/**
	 * The current sizeT (i.e.: seconds between frames) of the image (if
	 * present).
	 */
	private Double sizeT = null;

	// Overlay panel element
	private JComboBox<String> overlayKind_combo, overlayParticle_combo,
	        overlayTraj_combo, overlayTM_combo;

	public OmegaElementImagePanel(final RootPaneContainer parent) {
		super(parent);

		this.trajectoriesMap = new LinkedHashMap<OmegaParticleDetectionRun, List<OmegaParticleLinkingRun>>();
		this.modifiedTrajectoriesMap = new LinkedHashMap<OmegaParticleLinkingRun, List<OmegaTrajectoriesManagerRun>>();

		this.isPopulatingOverlay = false;
		this.particlesOverlay = false;
		this.isHandlingEvent = false;

		this.pixels = null;
		this.gateway = null;

		this.selectedParticleDetectionRun = null;
		this.selectedParticleLinkingRun = null;
		this.selectedTrajectoriesManagerRun = null;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.setBorder(new TitledBorder("Selected item"));

		this.setBackground(Color.white);

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		this.canvas = new GenericImageCanvas(this.getParentContainer(), this);
		this.render();
		this.canvasSP = new JScrollPane(this.canvas);
		this.resizeCanvasScrollPane();

		// scrollPane.setPreferredSize(this.canvas.getPreferredSize());

		this.tabPane = new JTabbedPane(SwingConstants.TOP,
		        JTabbedPane.SCROLL_TAB_LAYOUT);

		this.infoPanel = new OmegaElementInformationsPanel(
		        this.getParentContainer());

		this.tabPane.add("Information", this.infoPanel);

		this.imageControlsPanel = new JPanel();
		// this.imageControlsPanel.setBorder(new
		// TitledBorder("Image controls"));
		this.imageControlsPanel.setLayout(new GridLayout(3, 1));

		// sliders panel
		final GenericPanel slidersPanel = new GenericPanel(
		        this.getParentContainer());
		slidersPanel.setLayout(new GridLayout(3, 1));

		final GenericPanel zSliderPanel = new GenericPanel(
		        this.getParentContainer());
		zSliderPanel.setLayout(new BorderLayout());

		this.z_slider = new JSlider();
		this.z_slider.setMinimum(0);
		this.z_slider.setMaximum(0);
		this.z_slider.setExtent(1);
		this.z_slider.setEnabled(false);
		zSliderPanel.add(new JLabel("Z"), BorderLayout.WEST);
		zSliderPanel.add(this.z_slider, BorderLayout.CENTER);
		this.z_label = new JLabel();
		zSliderPanel.add(this.z_label, BorderLayout.EAST);

		slidersPanel.add(Box.createHorizontalStrut(5));

		final GenericPanel tSliderPanel = new GenericPanel(
		        this.getParentContainer());
		tSliderPanel.setLayout(new BorderLayout());

		this.t_slider = new JSlider();
		this.t_slider.setMinimum(0);
		this.t_slider.setMaximum(0);
		this.t_slider.setExtent(1);
		this.t_slider.setEnabled(false);
		tSliderPanel.add(new JLabel("T"), BorderLayout.WEST);
		tSliderPanel.add(this.t_slider, BorderLayout.CENTER);
		this.t_label = new JLabel();
		tSliderPanel.add(this.t_label, BorderLayout.EAST);

		slidersPanel.add(zSliderPanel);
		slidersPanel.add(tSliderPanel);

		this.imageControlsPanel.add(slidersPanel);

		// compressed panel
		final JPanel compressionPanel = new JPanel();
		compressionPanel.setLayout(new GridLayout(1, 1));

		this.compressed = new JCheckBox("Compressed Image");
		this.compressed.setSelected(false);
		this.compressed.setEnabled(false);

		compressionPanel.add(this.compressed);
		this.imageControlsPanel.add(compressionPanel);

		// channels panel
		this.channelsPanel = new JPanel();
		this.channelsPanel.setLayout(new GridLayout(1, 1));

		this.imageControlsPanel.add(this.channelsPanel);
		final JScrollPane imageControlsScrollPane = new JScrollPane(
		        this.imageControlsPanel);

		this.tabPane.add("Viewing options", imageControlsScrollPane);

		this.imageOverlayPanel = new JPanel();
		this.imageOverlayPanel.setLayout(new GridLayout(4, 1));

		this.overlayKind_combo = new JComboBox<String>();
		this.overlayKind_combo.addItem("None");
		this.overlayKind_combo.addItem("Particles");
		this.overlayKind_combo.addItem("Trajectories");
		this.overlayKind_combo.addItem("Trajectories manager");
		this.overlayKind_combo.setSelectedIndex(0);
		this.overlayKind_combo.setEnabled(false);
		this.imageOverlayPanel.add(this.overlayKind_combo);

		this.overlayParticle_combo = new JComboBox<String>();
		this.overlayParticle_combo.setEnabled(false);
		this.imageOverlayPanel.add(this.overlayParticle_combo);

		this.overlayTraj_combo = new JComboBox<String>();
		this.overlayTraj_combo.setEnabled(false);
		this.imageOverlayPanel.add(this.overlayTraj_combo);

		this.overlayTM_combo = new JComboBox<String>();
		this.overlayTM_combo.setEnabled(false);
		this.imageOverlayPanel.add(this.overlayTM_combo);

		final JScrollPane imageOverlayScrollPane = new JScrollPane(
		        this.imageOverlayPanel);
		this.tabPane.add("Overlays", imageOverlayScrollPane);

		this.add(this.canvasSP, BorderLayout.NORTH);
		this.add(this.tabPane, BorderLayout.SOUTH);
	}

	private void resizeCanvasScrollPane() {
		final Dimension dim = new Dimension(this.getWidth() - 20,
		        (this.getHeight() - 20) / 2);
		this.canvasSP.setPreferredSize(dim);
		this.canvasSP.setSize(dim);
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaElementImagePanel.this.resizeCanvasScrollPane();
			}
		});
		this.compressed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (OmegaElementImagePanel.this.compressed.isEnabled()) {
					OmegaElementImagePanel.this.renderImage();
				}
			}
		});
		this.z_slider.addChangeListener(this.createChangeListener());
		this.t_slider.addChangeListener(this.createChangeListener());
		this.overlayKind_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaElementImagePanel.this.selectOverlayKind();
			}
		});
		this.overlayParticle_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaElementImagePanel.this.selectParticles();
			}
		});
		this.overlayTraj_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaElementImagePanel.this.selectTrajectories();
			}
		});
		this.overlayTM_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaElementImagePanel.this.selectModifiedTrajectories();
			}
		});
	}

	private void selectOverlayKind() {
		if (this.isHandlingEvent)
			return;
		final String selected = (String) OmegaElementImagePanel.this.overlayKind_combo
		        .getSelectedItem();
		if (selected.equals("Particles")) {
			this.activateParticlesOverlay();
		} else if (selected.equals("Trajectories")) {
			// this.activateParticlesOverlay();
			this.activateTrajectoriesOverlay();
		} else if (selected.equals("Trajectories manager")) {
			// this.activateParticlesOverlay();
			// this.activateTrajectoriesOverlay();
			this.activateTrajectoriesManagerOverlay();
		} else {
			this.deactivateOverlays();
		}
	}

	private void selectModifiedTrajectories() {
		if (this.isPopulatingOverlay || !this.overlayTM_combo.isEnabled())
			return;
		this.resetCurrentSelection();
		final List<OmegaROI> particles = this.selectParticlesOverlay();
		this.selectTrajectoriesOverlay();
		final List<OmegaTrajectory> modifiedTrajectories = this
		        .selectTrajectoriesManagerOverlay();
		this.setParticles(particles);
		this.setTrajectories(modifiedTrajectories);
		if (!OmegaElementImagePanel.this.isHandlingEvent) {
			OmegaElementImagePanel.this
			        .sendApplicationTrajectoriesManagerRunSelectionEvent();
		}
	}

	private void selectTrajectories() {
		if (this.isPopulatingOverlay || !this.overlayTraj_combo.isEnabled())
			return;
		this.resetCurrentSelection();
		final List<OmegaROI> particles = this.selectParticlesOverlay();
		final List<OmegaTrajectory> trajectories = this
		        .selectTrajectoriesOverlay();
		if (this.overlayKind_combo.getSelectedItem().equals("Trajectories")) {
			this.setParticles(particles);
			this.setTrajectories(trajectories);
			if (!this.isHandlingEvent) {
				this.sendApplicationParticleLinkingRunSelectionEvent();
			}
		} else {
			this.populateTrajectoriesManagerOverlay();
		}
	}

	private void selectParticles() {
		if (this.isPopulatingOverlay || !this.overlayParticle_combo.isEnabled())
			return;
		this.resetCurrentSelection();
		final List<OmegaROI> particles = this.selectParticlesOverlay();
		if (this.overlayKind_combo.getSelectedItem().equals("Particles")) {
			this.setParticles(particles);
			if (!this.isHandlingEvent) {
				this.sendApplicationParticleDetectionRunSelectionEvent();
			}
		} else {
			this.populateTrajectoriesOverlay();
		}
	}

	private void setTrajectories(final List<OmegaTrajectory> trajectories) {
		this.canvas.setTrajectories(trajectories);
	}

	private void setParticles(final List<OmegaROI> particles) {
		this.canvas.setParticles(particles);
	}

	private List<OmegaTrajectory> selectTrajectoriesManagerOverlay() {
		if (this.isPopulatingOverlay)
			return null;
		List<OmegaTrajectory> modifiedTrajectories = null;
		final String s = (String) this.overlayTM_combo.getSelectedItem();
		if (this.selectedParticleLinkingRun != null) {
			final List<OmegaTrajectoriesManagerRun> modifiedTrajectoriesRun = this.modifiedTrajectoriesMap
			        .get(this.selectedParticleLinkingRun);
			for (final OmegaTrajectoriesManagerRun trajectoriesManagerRun : modifiedTrajectoriesRun) {
				if (!trajectoriesManagerRun.getName().equals(s)) {
					continue;
				}
				this.selectedTrajectoriesManagerRun = trajectoriesManagerRun;
				modifiedTrajectories = trajectoriesManagerRun
				        .getResultingTrajectories();
				return modifiedTrajectories;
			}
		}
		return null;
		// this.canvas.setTrajectories(modifiedTrajectories);
	}

	private List<OmegaTrajectory> selectTrajectoriesOverlay() {
		if (this.isPopulatingOverlay)
			return null;
		List<OmegaTrajectory> trajectories = null;
		final String s = (String) this.overlayTraj_combo.getSelectedItem();
		if (this.selectedParticleDetectionRun != null) {
			final List<OmegaParticleLinkingRun> trajectoriesRuns = this.trajectoriesMap
			        .get(this.selectedParticleDetectionRun);
			for (final OmegaParticleLinkingRun particleLinkingRun : trajectoriesRuns) {
				if (!particleLinkingRun.getName().equals(s)) {
					continue;
				}
				this.selectedParticleLinkingRun = particleLinkingRun;
				trajectories = particleLinkingRun.getResultingTrajectories();
				return trajectories;
			}
		}
		return null;
		// this.canvas.setTrajectories(trajectories);
	}

	private List<OmegaROI> getFrameParticlesOverlay() {
		final int t = this.t_slider.getValue() - 1;
		for (final OmegaFrame frame : this.selectedParticleDetectionRun
		        .getResultingParticles().keySet()) {
			if (frame.getIndex() == t)
				return this.selectedParticleDetectionRun
				        .getResultingParticles().get(frame);
		}
		return null;
	}

	private List<OmegaROI> selectParticlesOverlay() {
		if (this.isPopulatingOverlay)
			return null;
		List<OmegaROI> particles = null;
		final String s = (String) this.overlayParticle_combo.getSelectedItem();
		for (final OmegaParticleDetectionRun particleDetectionRun : this.trajectoriesMap
		        .keySet()) {
			if (!particleDetectionRun.getName().equals(s)) {
				continue;
			}
			this.selectedParticleDetectionRun = particleDetectionRun;
			particles = this.getFrameParticlesOverlay();
			if (particles != null)
				return particles;
		}

		return null;
		// this.canvas.setParticles(particles);
	}

	private void resetCurrentSelection() {
		this.selectedParticleDetectionRun = null;
		this.selectedParticleLinkingRun = null;
		this.selectedTrajectoriesManagerRun = null;
	}

	private void populateTrajectoriesManagerOverlay() {
		if (!this.overlayTM_combo.isEnabled())
			return;
		this.isPopulatingOverlay = true;
		this.overlayTM_combo.removeAllItems();
		if (this.selectedParticleLinkingRun != null) {
			final List<OmegaTrajectoriesManagerRun> modifiedTrajectories = this.modifiedTrajectoriesMap
			        .get(this.selectedParticleLinkingRun);
			for (final OmegaTrajectoriesManagerRun trajManagerRun : modifiedTrajectories) {
				this.overlayTM_combo.addItem(trajManagerRun.getName());
			}
			this.overlayTM_combo.addItem("Actual modification");
		}
		this.isPopulatingOverlay = false;
		if (this.overlayTM_combo.getItemCount() > 0) {
			this.overlayTM_combo.setSelectedIndex(0);
		}
	}

	private void populateTrajectoriesOverlay() {
		if (!this.overlayTraj_combo.isEnabled())
			return;
		this.isPopulatingOverlay = true;
		this.overlayTraj_combo.removeAllItems();
		if (this.selectedParticleDetectionRun != null) {
			final List<OmegaParticleLinkingRun> trajectories = this.trajectoriesMap
			        .get(this.selectedParticleDetectionRun);
			for (final OmegaParticleLinkingRun particleLinkingRun : trajectories) {
				this.overlayTraj_combo.addItem(particleLinkingRun.getName());
			}
		}
		this.isPopulatingOverlay = false;
		if (this.overlayTraj_combo.getItemCount() > 0) {
			this.overlayTraj_combo.setSelectedIndex(0);
		}
	}

	private void populateParticlesOverlay() {
		if (!this.overlayParticle_combo.isEnabled())
			return;
		this.isPopulatingOverlay = true;
		this.overlayParticle_combo.removeAllItems();
		for (final OmegaParticleDetectionRun particleDetectionRun : this.trajectoriesMap
		        .keySet()) {
			this.overlayParticle_combo.addItem(particleDetectionRun.getName());
		}
		this.isPopulatingOverlay = false;
		if (this.overlayParticle_combo.getItemCount() > 0) {
			this.overlayParticle_combo.setSelectedIndex(0);
		}
	}

	private void deactivateOverlays() {
		this.particlesOverlay = false;
		this.overlayParticle_combo.setEnabled(false);
		this.overlayParticle_combo.setSelectedIndex(-1);
		this.overlayTraj_combo.setEnabled(false);
		this.overlayTraj_combo.setSelectedIndex(-1);
		this.overlayTM_combo.setEnabled(false);
		this.overlayTM_combo.setSelectedIndex(-1);
	}

	private void activateTrajectoriesManagerOverlay() {
		this.overlayParticle_combo.setEnabled(true);
		this.populateParticlesOverlay();
		this.overlayTraj_combo.setEnabled(true);
		this.populateTrajectoriesOverlay();
		this.overlayTM_combo.setEnabled(true);
		this.populateTrajectoriesManagerOverlay();
	}

	private void activateTrajectoriesOverlay() {
		this.overlayTM_combo.setEnabled(false);
		this.overlayTM_combo.setSelectedIndex(-1);
		this.overlayParticle_combo.setEnabled(true);
		this.populateParticlesOverlay();
		this.overlayTraj_combo.setEnabled(true);
		this.populateTrajectoriesOverlay();
	}

	private void activateParticlesOverlay() {
		this.overlayTraj_combo.setEnabled(false);
		this.overlayTraj_combo.setSelectedIndex(-1);
		this.overlayTM_combo.setEnabled(false);
		this.overlayTM_combo.setSelectedIndex(-1);
		this.setTrajectories(null);
		this.overlayParticle_combo.setEnabled(true);
		this.particlesOverlay = true;
		this.populateParticlesOverlay();
	}

	private ChangeListener createChangeListener() {
		final ChangeListener cl = new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				final JSlider slider = (JSlider) evt.getSource();
				if (!slider.isEnabled())
					return;

				final String sizes[] = OmegaElementImagePanel.this
				        .createSizesStrings(
				                OmegaElementImagePanel.this.z_slider.getValue(),
				                OmegaElementImagePanel.this.t_slider.getValue());

				final Long id = OmegaElementImagePanel.this.pixels
				        .getElementID();
				final int z = OmegaElementImagePanel.this.z_slider.getValue();
				final int t = OmegaElementImagePanel.this.t_slider.getValue();

				if (t == 0) {
					// TODO handle error if needed
					System.out.println("ERROR");
				}

				if (slider == OmegaElementImagePanel.this.z_slider) {
					OmegaElementImagePanel.this.gateway.setDefaultZ(id, z - 1);
					OmegaElementImagePanel.this.pixels.setSelectedZ(z - 1);
				} else if (slider == OmegaElementImagePanel.this.t_slider) {
					OmegaElementImagePanel.this.gateway.setDefaultT(id, t - 1);
					OmegaElementImagePanel.this.canvas.setCurrentT(t - 1);
					if (OmegaElementImagePanel.this.particlesOverlay) {
						final List<OmegaROI> particles = OmegaElementImagePanel.this
						        .getFrameParticlesOverlay();
						OmegaElementImagePanel.this.canvas
						        .setParticles(particles);
					}
				}

				OmegaElementImagePanel.this.z_label.setText(String.format(
				        "%d %s / %d", z, sizes[0],
				        OmegaElementImagePanel.this.currentMaximumZValue));
				OmegaElementImagePanel.this.t_label.setText(String.format(
				        "%d %s / %d", t, sizes[1],
				        OmegaElementImagePanel.this.currentMaximumTValue));

				OmegaElementImagePanel.this.renderImage();
			}
		};
		return cl;
	}

	/**
	 * Builds the channel component.
	 */
	private void buildChannelsPane(final int n) {
		this.channelsPanel.removeAll();
		this.channelsPanel.setLayout(new GridLayout(n, 1));
		this.channels = new JCheckBox[n];

		for (int i = 0; i < n; i++) {
			this.channels[i] = new JCheckBox("Channel " + i);

			this.channels[i].setSelected(true);

			this.channels[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					OmegaElementImagePanel.this.setActiveChannels(e);
				}
			});

			this.channelsPanel.add(this.channels[i]);
		}

		this.channelsPanel.repaint();
	}

	// final ImageData image, final RenderingEnginePrx engine
	private void setRenderingControl() {
		// this.engine = engine;
		// this.image = image;
		if (this.pixels == null) {
			this.removeRenderingControl();
			this.removeOverlayControl();
		} else {
			this.addRenderingControl();
			this.addOverlayControl();
		}
	}

	private void addOverlayControl() {
		if (this.trajectoriesMap.isEmpty()) {
			// Alert that no possible analysis there
		} else {
			this.overlayKind_combo.setSelectedIndex(0);
			this.overlayKind_combo.setEnabled(true);
		}
	}

	private void removeOverlayControl() {
		this.overlayKind_combo.setEnabled(false);
		this.overlayKind_combo.setSelectedIndex(0);
		this.deactivateOverlays();
		this.overlayParticle_combo.removeAllItems();
		this.overlayTraj_combo.removeAllItems();
		this.overlayTM_combo.removeAllItems();
	}

	private void removeRenderingControl() {
		this.compressed.setEnabled(false);
		this.compressed.setSelected(false);

		this.z_slider.setEnabled(false);
		this.z_slider.setMaximum(0);
		this.z_slider.setMinimum(0);
		this.z_slider.setValue(0);

		this.currentMaximumTValue = -1;
		this.currentMaximumZValue = -1;

		this.t_slider.setEnabled(false);
		this.t_slider.setMaximum(0);
		this.t_slider.setMinimum(0);
		this.t_slider.setValue(0);

		this.sizeX = null;
		this.sizeY = null;
		this.sizeZ = null;
		this.sizeT = null;

		this.z_label.setText("");
		this.t_label.setText("");

		// number of channels in the image (RGB)
		this.channelsNumber = 0;

		this.buildChannelsPane(this.channelsNumber);
	}

	private void addRenderingControl() {
		this.compressed.setEnabled(true);
		this.compressed.setSelected(true);
		final Long id = this.pixels.getElementID();

		this.gateway
		        .setCompressionLevel(id, OmegaElementImagePanel.COMPRESSION);

		// final PixelsData pixels = image.getDefaultPixels();
		this.currentMaximumTValue = this.pixels.getSizeT();
		this.currentMaximumZValue = this.pixels.getSizeZ();

		this.z_slider.setMaximum(this.currentMaximumZValue);
		this.z_slider.setMinimum(1);

		this.t_slider.setMaximum(this.currentMaximumTValue);
		this.t_slider.setMinimum(1);

		// get the sizeZ and the sizeT of the image in order to display them in
		// the sliders
		// cache the double values, so when the user moves the slider only the
		// strings are re-calculated
		this.sizeX = this.pixels.getPixelSizeX();
		this.sizeY = this.pixels.getPixelSizeY();
		this.sizeZ = this.pixels.getPixelSizeZ();

		this.sizeT = this.gateway.computeSizeT(id, this.pixels.getSizeT(),
		        this.currentMaximumTValue);

		final int defaultZ = this.gateway.getDefaultZ(id);
		final int defaultT = this.gateway.getDefaultT(id);
		this.z_slider.setValue(defaultZ + 1);
		this.t_slider.setValue(defaultT + 1);

		final String sizes[] = this.createSizesStrings(defaultZ + 1,
		        defaultT + 1);

		this.z_label.setText(String.format("%d %s / %d", defaultZ + 1,
		        sizes[0], this.currentMaximumZValue));
		this.t_label.setText(String.format("%d %s / %d", defaultT + 1,
		        sizes[1], this.currentMaximumTValue));

		this.canvas.setCurrentT(defaultT + 1);

		// number of channels in the image (RGB)
		this.channelsNumber = this.pixels.getSizeC();

		this.buildChannelsPane(this.channelsNumber);

		// Enabled at the end to avoid triggering of listeners
		this.z_slider.setEnabled(this.pixels.getSizeZ() > 1);
		this.t_slider.setEnabled(this.pixels.getSizeT() > 1);
	}

	private String[] createSizesStrings(final int defaultZ, final int defaultT) {
		final String sizes[] = new String[] { "", "" };

		if ((this.sizeZ != null) && (this.sizeZ > 0.0)) {
			sizes[0] = String.format("(%.2f micron)", this.sizeZ * defaultZ);
		}

		if ((this.sizeT != null) && (this.sizeT > 0)) {
			sizes[1] = String.format("(%.2f sec)", this.sizeT * defaultT);
		}

		return sizes;
	}

	private void render() {
		if (this.pixels == null) {
			this.renderNoImage();
		} else {
			this.renderImage();
		}
	}

	private void renderNoImage() {
		final String fileName = OmegaConstants.OMEGA_IMGS_FOLDER
		        + File.separator + "noImage.jpg";
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fileName));
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}
		this.canvas.setImage(img, true);
	}

	/** Renders a plane. */
	private void renderImage() {
		try {
			final Long id = this.pixels.getElementID();
			// now render the image, possible to render it compressed or not
			// compressed
			BufferedImage img = null;
			final int sizeX = this.pixels.getSizeX();
			final int sizeY = this.pixels.getSizeY();
			if (this.compressed.isSelected()) {
				final int[] buf = this.gateway.renderAsPackedInt(id);
				img = OmegaImageRenderingUtilities.createImage(buf, 32, sizeX,
				        sizeY);
			} else {
				final byte[] values = this.gateway.renderCompressed(id);
				final ByteArrayInputStream stream = new ByteArrayInputStream(
				        values);
				img = ImageIO.read(stream);
				img.setAccelerationPriority(1f);
			}
			this.canvas.setImage(img, false);
		} catch (final IOException e) {
			// TODO manage exception
			e.printStackTrace();
		}
	}

	/**
	 * Set or unset a channel.
	 */
	private void setActiveChannels(final ActionEvent evt) {
		int c = 0;
		for (int i = 0; i < this.channelsNumber; i++) {
			final boolean active = this.channels[i].isSelected();
			this.gateway
			        .setActiveChannel(this.pixels.getElementID(), i, active);
			if (active) {
				c++;
			}
		}
		this.pixels.setSelectedC(c);
		this.renderImage();
	}

	private void updateDisplayableElements(final OmegaImage image,
	        final List<OmegaAnalysisRun> loadedAnalysisRuns) {
		for (final OmegaAnalysisRun particleDetectionRun : image
		        .getAnalysisRuns()) {
			if (particleDetectionRun instanceof OmegaParticleDetectionRun) {
				this.updateTrajectoriesMap(loadedAnalysisRuns,
				        (OmegaParticleDetectionRun) particleDetectionRun);
			}
		}
	}

	private void updateTrajectoriesMap(
	        final List<OmegaAnalysisRun> loadedAnalysisRuns,
	        final OmegaParticleDetectionRun particleDetectionRun) {
		if (!loadedAnalysisRuns.contains(particleDetectionRun))
			return;
		List<OmegaParticleLinkingRun> particleLinkingRuns;
		if (this.trajectoriesMap.containsKey(particleDetectionRun)) {
			particleLinkingRuns = this.trajectoriesMap
			        .get(particleDetectionRun);
		} else {
			particleLinkingRuns = new ArrayList<OmegaParticleLinkingRun>();
		}
		for (final OmegaAnalysisRun analysisRun : particleDetectionRun
		        .getAnalysisRuns()) {
			if (!(analysisRun instanceof OmegaParticleLinkingRun)
			        || !loadedAnalysisRuns.contains(analysisRun)) {
				continue;
			}
			final OmegaParticleLinkingRun particleLinkingRun = (OmegaParticleLinkingRun) analysisRun;
			particleLinkingRuns.add(particleLinkingRun);
			this.updateModifiedTrajectoriesMap(loadedAnalysisRuns,
			        particleLinkingRun);
		}
		this.trajectoriesMap.put(particleDetectionRun, particleLinkingRuns);
	}

	private void updateModifiedTrajectoriesMap(
	        final List<OmegaAnalysisRun> loadedAnalysisRuns,
	        final OmegaParticleLinkingRun particleLinkingRun) {
		if (!loadedAnalysisRuns.contains(particleLinkingRun))
			return;
		List<OmegaTrajectoriesManagerRun> trajectoriesManagerRuns;
		if (this.modifiedTrajectoriesMap.containsKey(particleLinkingRun)) {
			trajectoriesManagerRuns = this.modifiedTrajectoriesMap
			        .get(particleLinkingRun);
		} else {
			trajectoriesManagerRuns = new ArrayList<OmegaTrajectoriesManagerRun>();
		}
		for (final OmegaAnalysisRun analysisRun : particleLinkingRun
		        .getAnalysisRuns()) {
			if (!(analysisRun instanceof OmegaTrajectoriesManagerRun)
			        || !loadedAnalysisRuns.contains(analysisRun)) {
				continue;
			}
			final OmegaTrajectoriesManagerRun trajectoriesManagerRun = (OmegaTrajectoriesManagerRun) analysisRun;
			trajectoriesManagerRuns.add(trajectoriesManagerRun);
		}
		this.modifiedTrajectoriesMap.put(particleLinkingRun,
		        trajectoriesManagerRuns);
	}

	public void update(final OmegaElement element,
	        final List<OmegaAnalysisRun> loadedAnalysisRuns,
	        final OmegaGateway gateway) {
		this.infoPanel.update(element);
		this.trajectoriesMap.clear();
		// this.loadedAnalysisRuns = loadedAnalysisRuns;
		this.gateway = gateway;
		if (element == null) {
			this.pixels = null;
		} else {
			if (element instanceof OmegaImage) {
				final OmegaImage image = (OmegaImage) element;
				this.pixels = image.getDefaultPixels();
				this.updateDisplayableElements(image, loadedAnalysisRuns);
			} else if (element instanceof OmegaImagePixels) {
				this.pixels = (OmegaImagePixels) element;
			} else if (element instanceof OmegaFrame) {
				final OmegaFrame frame = (OmegaFrame) element;
				frame.getIndex();
				this.pixels = null;
			} else {
				this.pixels = null;
			}
		}
		this.setRenderingControl();
		this.render();
	}

	public Double getSizeX() {
		return this.sizeX;
	}

	public Double getSizeY() {
		return this.sizeY;
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.infoPanel.updateParentContainer(parent);
	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
	        final boolean selection) {
		this.canvas.updateTrajectories(trajectories, selection);
	}

	private OmegaGUIFrame getOmegaGUIFrame() {
		final RootPaneContainer parent = this.getParentContainer();
		OmegaGUIFrame frame = null;
		if (parent instanceof GenericFrame) {
			final GenericFrame genericFrame = (GenericFrame) parent;
			frame = (OmegaGUIFrame) genericFrame.getParent();
		} else {
			frame = (OmegaGUIFrame) parent;
		}

		return frame;
	}

	private void sendApplicationParticleDetectionRunSelectionEvent() {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaApplicationEvent event = new OmegaApplicationParticleDetectionRunSelectionEvent(
		        OmegaApplicationEvent.SOURCE_SIDE_BAR,
		        this.selectedParticleDetectionRun);
		frame.sendApplicationEvent(event);
	}

	private void sendApplicationParticleLinkingRunSelectionEvent() {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaApplicationEvent event = new OmegaApplicationParticleLinkingRunSelectionEvent(
		        OmegaApplicationEvent.SOURCE_SIDE_BAR,
		        this.selectedParticleLinkingRun);
		frame.sendApplicationEvent(event);
	}

	private void sendApplicationTrajectoriesManagerRunSelectionEvent() {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaApplicationEvent event = new OmegaApplicationTrajectoriesManagerRunSelectionEvent(
		        OmegaApplicationEvent.SOURCE_SIDE_BAR,
		        this.selectedTrajectoriesManagerRun);
		frame.sendApplicationEvent(event);
	}

	public void sendApplicationTrajectoriesEvent(
	        final List<OmegaTrajectory> trajectories, final boolean selection) {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaApplicationEvent event = new OmegaApplicationTrajectoriesEvent(
		        OmegaApplicationEvent.SOURCE_SIDE_BAR, trajectories, selection);
		frame.sendApplicationEvent(event);
	}

	public void selectParticleDetectionRun(
	        final OmegaParticleDetectionRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlayKind_combo.setSelectedIndex(1);
		this.activateParticlesOverlay();
		for (int i = 0; i < this.overlayParticle_combo.getItemCount(); i++) {
			final String s = this.overlayParticle_combo.getItemAt(i);
			if (analysisRun.getName().equals(s)) {
				this.overlayParticle_combo.setSelectedIndex(i);
				this.isHandlingEvent = false;
				return;
			}
		}
		this.isHandlingEvent = false;
		// TODO throw error
	}

	public void selectParticleLinkingRun(
	        final OmegaParticleLinkingRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlayKind_combo.setSelectedIndex(2);
		this.activateParticlesOverlay();
		this.activateTrajectoriesOverlay();
		for (int i = 0; i < this.overlayTraj_combo.getItemCount(); i++) {
			final String s = this.overlayTraj_combo.getItemAt(i);
			if (analysisRun.getName().equals(s)) {
				this.overlayTraj_combo.setSelectedIndex(i);
				this.isHandlingEvent = false;
				return;
			}
		}
		this.isHandlingEvent = false;
		// TODO throw error
	}

	public void selectTrajectoriesManagerRun(
	        final OmegaParticleLinkingRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlayKind_combo.setSelectedIndex(3);
		this.activateParticlesOverlay();
		this.activateTrajectoriesOverlay();
		this.activateTrajectoriesManagerOverlay();

		if (analysisRun == null) {
			this.overlayTM_combo.setSelectedIndex(0);
			this.isHandlingEvent = false;
			return;
		}

		for (int i = 0; i < this.overlayTM_combo.getItemCount(); i++) {
			final String s = this.overlayTM_combo.getItemAt(i);
			if (analysisRun.getName().equals(s)) {
				this.overlayTM_combo.setSelectedIndex(i);
				this.isHandlingEvent = false;
				return;
			}
		}

		this.isHandlingEvent = false;
		// TODO throw error
	}
}
