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
package edu.umassmed.omega.core.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.OmegaLoadedData;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainerInterface;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.commons.data.coreElements.OmegaDataset;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSegments;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventSelectionImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaCoreEventTrajectories;
import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionLoadedElementNotFound;
import edu.umassmed.omega.commons.gui.GenericElementInformationPanel;
import edu.umassmed.omega.commons.gui.GenericFrame;
import edu.umassmed.omega.commons.gui.GenericImageCanvas;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.interfaces.GenericElementInformationContainerInterface;
import edu.umassmed.omega.commons.gui.interfaces.GenericImageCanvasContainer;

public class OmegaSidePanel extends GenericPanel implements
		GenericImageCanvasContainer,
		GenericElementInformationContainerInterface {

	private static final long serialVersionUID = -4565126277733287950L;

	private GenericImageCanvas canvas;
	private OmegaElement currentElement;

	private JSplitPane splitPane;
	private JTabbedPane tabPane;

	private GenericElementInformationPanel infoPanel;
	private OmegaElementRenderingPanel renderingPanel;
	private OmegaElementOverlaysPanel overlaysPanel;

	private JLabel selected_lbl;
	private JButton arrowLeft_btt, arrowRight_btt;
	private JSlider elements_slider;
	private JButton scale1on1_btt, scaleToFit_btt;

	private boolean isAttached, isHandlingEvent, isScaleToFit;

	private OrphanedAnalysisContainer orphanedAnalysis;
	private OmegaLoadedData loadedData;
	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	private int itemIndex;

	private TitledBorder border;

	// private JDesktopPane desktopPane;

	public OmegaSidePanel(final RootPaneContainer parent) {
		super(parent);
		this.isAttached = true;
		this.isHandlingEvent = false;
		this.isScaleToFit = false;
		this.loadedData = null;
		this.currentElement = null;

		this.setLayout(new BorderLayout());

		this.itemIndex = -1;
	}

	protected void initializePanel() {
		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final Dimension btt_dim = OmegaConstants.BUTTON_SIZE;
		this.border = new TitledBorder(OmegaGUIConstants.SIDEPANEL_NO_DETAILS);

		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		this.scale1on1_btt = new JButton(OmegaGUIConstants.SIDEPANEL_SCALE_ONE);
		this.scale1on1_btt.setPreferredSize(btt_dim);
		this.scale1on1_btt.setSize(btt_dim);

		this.scaleToFit_btt = new JButton(OmegaGUIConstants.SIDEPANEL_SCALE_FIT);
		this.scaleToFit_btt.setPreferredSize(btt_dim);
		this.scaleToFit_btt.setSize(btt_dim);

		topPanel.add(this.scale1on1_btt, BorderLayout.WEST);
		topPanel.add(this.scaleToFit_btt, BorderLayout.EAST);

		this.add(topPanel, BorderLayout.NORTH);

		this.canvas = new GenericImageCanvas(this.getParentContainer(), this);
		this.canvas.render();
		// this.canvasSP = new JScrollPane(this.canvas);
		// this.resizeCanvasScrollPane();

		this.tabPane = new JTabbedPane(SwingConstants.TOP,
				JTabbedPane.WRAP_TAB_LAYOUT);

		this.infoPanel = new GenericElementInformationPanel(
				this.getParentContainer(), this);
		this.resizeInfoPanel();
		// final JScrollPane infoScrollPane = new JScrollPane(this.infoPanel);
		this.tabPane.add(OmegaGUIConstants.SIDEPANEL_TABS_GENERAL,
				this.infoPanel);

		this.renderingPanel = new OmegaElementRenderingPanel(
				this.getParentContainer(), this);
		// final JScrollPane renderingScrollPane = new JScrollPane(
		// this.renderingPanel);
		this.tabPane.add(OmegaGUIConstants.SIDEPANEL_TABS_VIEWOPTIONS,
				this.renderingPanel);

		this.overlaysPanel = new OmegaElementOverlaysPanel(
				this.getParentContainer(), this);
		// final JScrollPane imageOverlayScrollPane = new JScrollPane(
		// this.overlaysPanel);
		this.resizeOverlaysPanel();
		this.tabPane.add(OmegaGUIConstants.SIDEPANEL_TABS_TRACKS,
				this.overlaysPanel);

		this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.splitPane.setBorder(this.border);
		this.splitPane.setLeftComponent(this.canvas);
		this.splitPane.setRightComponent(this.tabPane);
		this.splitPane.setDividerLocation(0.5);

		this.add(this.splitPane, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		final Dimension dim = new Dimension(50, 20);

		this.arrowLeft_btt = new JButton("<");
		this.arrowLeft_btt.setPreferredSize(dim);
		this.arrowLeft_btt.setSize(dim);
		this.arrowLeft_btt.setEnabled(false);

		this.arrowRight_btt = new JButton(">");
		this.arrowRight_btt.setPreferredSize(dim);
		this.arrowRight_btt.setSize(dim);
		this.arrowLeft_btt.setEnabled(false);

		this.selected_lbl = new JLabel(
				OmegaGUIConstants.SIDEPANEL_NO_ITEM_SELECTED);
		this.selected_lbl.setHorizontalAlignment(SwingConstants.CENTER);

		bottomPanel.add(this.arrowLeft_btt, BorderLayout.WEST);
		bottomPanel.add(this.selected_lbl, BorderLayout.CENTER);
		bottomPanel.add(this.arrowRight_btt, BorderLayout.EAST);

		// this.elements_slider = new JSlider(0, 0, 0);
		// this.elements_slider.setSnapToTicks(true);
		// this.elements_slider.setMajorTickSpacing(1);
		// this.elements_slider.setMinorTickSpacing(1);
		// this.elements_slider.setEnabled(false);
		//
		// bottomPanel.add(this.elements_slider);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaSidePanel.this.handleResize();
			}
		});
		this.tabPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaSidePanel.this.handleTabPaneResize();
			}
		});
		this.splitPane.addPropertyChangeListener(
				JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(final PropertyChangeEvent evt) {
						final JSplitPane source = (JSplitPane) evt.getSource();
						OmegaSidePanel.this.handleSplitChange(source.getSize());
					}
				});
		this.scale1on1_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaSidePanel.this.handleScale1on1();
			}
		});
		this.scaleToFit_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaSidePanel.this.handleScaleToFit();
			}
		});
		this.arrowLeft_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmegaSidePanel.this.handleChangeItem(-1);
			}
		});
		this.arrowRight_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaSidePanel.this.handleChangeItem(1);
			}
		});
		// this.elements_slider.addMouseListener(new MouseAdapter() {
		//
		// @Override
		// public void mouseReleased(final MouseEvent e) {
		// if (!OmegaSidePanel.this.elements_slider.isEnabled())
		// return;
		// final int index = OmegaSidePanel.this.elements_slider
		// .getValue();
		// OmegaSidePanel.this.updateCurrentElement(index);
		// }
		// });
	}

	private void handleScale1on1() {
		this.canvas.setScale(1);
		this.isScaleToFit = false;
		this.canvas.rescale();
	}

	private void handleScaleToFit() {
		this.canvas.computeAndSetScaleToFit();
		this.isScaleToFit = true;
		this.canvas.rescale();
	}

	private void handleSplitChange(final Dimension size) {
		// this.handleTabPaneResize();
	}

	private void handleTabPaneResize() {
		this.resizeInfoPanel();
		this.resizeRenderingPanel();
		this.resizeOverlaysPanel();
	}

	private void handleResize() {
		this.resizeCanvasScrollPane();
		this.resizeTabPane();
	}

	private void handleChangeItem(final int mover) {
		if (this.itemIndex != -1) {
			this.computeNewItemIndex(mover);
		}
		this.manageItemChanged();
	}

	private void setBorderTitle(final String title) {
		this.border.setTitle(title);
		// FIXME see if there is another way instead of repaint everything here
		this.validate();
		this.repaint();
	}

	private void resizeTabPane() {
		final int height = this.getHeight() - this.canvas.getHeight() - 83;
		final int width = this.getWidth() - 20;
		final Dimension dim = new Dimension(width, height);
		this.tabPane.setPreferredSize(dim);
		this.tabPane.setSize(dim);
	}

	private void resizeCanvasScrollPane() {
		final Dimension dim = new Dimension(this.getWidth() - 20,
				this.splitPane.getDividerLocation());
		this.canvas.setPreferredSize(dim);
		this.canvas.setSize(dim);
	}

	private void resizeInfoPanel() {
		final int tabRun = this.tabPane.getTabRunCount();
		final int height = this.tabPane.getHeight() - (tabRun * 20);
		final int width = this.tabPane.getWidth();
		this.infoPanel.resizePanel(width - 20, height - 20);
	}

	private void resizeRenderingPanel() {
		final int tabRun = this.tabPane.getTabRunCount();
		final int height = this.tabPane.getHeight() - (tabRun * 20);
		final int width = this.tabPane.getWidth();
		this.renderingPanel.resizePanel(width - 20, height - 20);
		// this.overlaysPanel.rescale(this.tabPane.getWidth() - 20);
	}

	private void resizeOverlaysPanel() {
		final int tabRun = this.tabPane.getTabRunCount();
		final int height = this.tabPane.getHeight() - (tabRun * 20);
		final int width = this.tabPane.getWidth();
		this.overlaysPanel.resizePanel(width - 20, height - 20);
		// this.overlaysPanel.rescale(this.tabPane.getWidth() - 20);
	}

	private void computeNewItemIndex(final int mover) {
		this.itemIndex += mover;
		final int size = this.loadedData.getLoadedDataSize();
		if (this.itemIndex < 1) {
			this.itemIndex = size;
		} else if (this.itemIndex > size) {
			this.itemIndex = 1;
		}
	}

	private void manageItemChanged() {
		this.setCurrentElementLabel();
		this.updateCurrentElement(this.itemIndex);
	}

	private void setCurrentElementLabel() {
		final StringBuffer buf = new StringBuffer();
		if ((this.itemIndex != -1) && (this.loadedData != null)) {
			final int size = this.loadedData.getLoadedDataSize();
			buf.append(OmegaGUIConstants.SIDEPANEL_ITEM_SELECTED);
			buf.append(this.itemIndex);
			buf.append("/");
			buf.append(size);
		} else {
			buf.append(OmegaGUIConstants.SIDEPANEL_NO_ITEM_SELECTED);
		}
		this.selected_lbl.setText(buf.toString());
		this.selected_lbl.revalidate();
		this.selected_lbl.repaint();
	}

	public boolean isAttached() {
		return this.isAttached;
	}

	public void setAttached(final boolean tof) {
		this.isAttached = tof;
	}

	private void sendCoreEventSelectionImage(final OmegaImage img) {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaCoreEvent event = new OmegaCoreEventSelectionImage(
				OmegaCoreEvent.SOURCE_SIDE_BAR, img);
		frame.sendCoreEvent(event);
	}

	private void updateCurrentElement(final int index) {
		if (index > 0) {
			OmegaElement element = null;
			try {
				element = this.loadedData.getElement(index);
			} catch (final OmegaCoreExceptionLoadedElementNotFound ex) {
				OmegaLogFileManager.handleCoreException(ex, true);
				// TODO should i do something here?
				return;
			}
			if (!this.isHandlingEvent) {
				if (element instanceof OmegaImage) {
					this.sendCoreEventSelectionImage((OmegaImage) element);
				} else {
					this.sendCoreEventSelectionImage(null);
				}
			}
			this.updateCurrentElement(element);
		} else if (index == 0) {
			// TODO handle orphaned analyses case
			// How do I retrieve them? I have only loaded data here
			this.updateCurrentElement(this.orphanedAnalysis);
		} else {
			this.updateCurrentElement(null);
		}
	}

	private void updateCurrentElement(final OmegaElement element) {
		// TODO see where overlays are set
		this.currentElement = element;
		this.canvas.resetOverlays();
		this.infoPanel.update(element);
		this.overlaysPanel.resetMaps();
		// this.loadedAnalysisRuns = loadedAnalysisRuns;
		final OmegaImagePixels oldPixels = this.canvas.getImagePixels();
		OmegaImagePixels pixels = null;
		if (element == null) {
			this.setBorderTitle(OmegaGUIConstants.SIDEPANEL_NO_DETAILS);
			pixels = null;
		} else {
			if (element instanceof OmegaProject) {
				this.setBorderTitle(OmegaGUIConstants.SIDEPANEL_PROJECT_DETAILS);
				pixels = null;
			} else if (element instanceof OmegaDataset) {
				this.setBorderTitle(OmegaGUIConstants.SIDEPANEL_DATASET_DETAILS);
				pixels = null;
			} else if (element instanceof OmegaImage) {
				this.setBorderTitle(OmegaGUIConstants.SIDEPANEL_IMAGE_DETAILS);
				final OmegaImage image = (OmegaImage) element;
				pixels = image.getDefaultPixels();
				this.updateDisplayableElements(image, this.loadedAnalysisRuns);
			} else if (element instanceof OmegaImagePixels) {
				this.setBorderTitle(OmegaGUIConstants.SIDEPANEL_PIXELS_DETAILS);
				pixels = (OmegaImagePixels) element;
			} else if (element instanceof OmegaPlane) {
				this.setBorderTitle(OmegaGUIConstants.SIDEPANEL_FRAME_DETAILS);
				final OmegaPlane frame = (OmegaPlane) element;
				frame.getIndex();
				pixels = null;
			} else {
				// TODO manage exception
				// System.out
				// .println("OmegaElementImagePanel: update case not supported");
				this.setBorderTitle(OmegaGUIConstants.SIDEPANEL_NO_DETAILS);
				pixels = null;
			}
		}
		this.canvas.setPixels(pixels);
		if (pixels != oldPixels) {
			if (this.isScaleToFit) {
				this.handleScaleToFit();
			} else {
				this.canvas.setScale(1);
			}
		}
		this.renderingPanel.setRenderingControl(pixels != null);
		this.overlaysPanel.setOverlayControl(pixels != null);
		this.overlaysPanel.populateParticleDetection(this.isHandlingEvent);
		this.canvas.render();
	}

	private void updateDisplayableElements(final OmegaImage image,
			final List<OmegaAnalysisRun> loadedAnalysisRuns) {
		for (final OmegaAnalysisRun analysisRun : image.getAnalysisRuns()) {
			if (analysisRun instanceof OmegaParticleDetectionRun) {
				this.overlaysPanel.updateMap(loadedAnalysisRuns, analysisRun);
			}
		}
	}

	public void updateGUI(final OmegaLoadedData loadedData,
			final OrphanedAnalysisContainer orphanedAnalysis,
			final List<OmegaAnalysisRun> loadedAnalysisRuns,
			final OmegaGateway gateway) {
		this.isHandlingEvent = true;
		this.loadedData = loadedData;
		this.orphanedAnalysis = orphanedAnalysis;
		this.loadedAnalysisRuns = loadedAnalysisRuns;
		this.canvas.setGateway(gateway);
		final int dataSize = loadedData.getLoadedDataSize();
		if (dataSize > 0) {
			this.arrowLeft_btt.setEnabled(true);
			this.arrowRight_btt.setEnabled(true);
			this.itemIndex = 1;
			this.manageItemChanged();
		} else {
			this.arrowLeft_btt.setEnabled(false);
			this.arrowRight_btt.setEnabled(false);
			this.itemIndex = -1;
			this.manageItemChanged();
		}
		this.isHandlingEvent = false;
	}

	protected void sendCoreEventSelectionParticleDetectionRun() {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaCoreEvent event = new OmegaCoreEventSelectionAnalysisRun(
				OmegaCoreEvent.SOURCE_SIDE_BAR,
				this.overlaysPanel.getSelectedPDRun());
		frame.sendCoreEvent(event);
	}

	protected void sendCoreEventSelectionParticleLinkingRun() {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaCoreEvent event = new OmegaCoreEventSelectionAnalysisRun(
				OmegaCoreEvent.SOURCE_SIDE_BAR,
				this.overlaysPanel.getSelectedPLRun());
		frame.sendCoreEvent(event);
	}

	protected void sendCoreEventSelectionTrajectoriesRelinkingRun() {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaCoreEvent event = new OmegaCoreEventSelectionAnalysisRun(
				OmegaCoreEvent.SOURCE_SIDE_BAR,
				this.overlaysPanel.getSelectedTRRun());
		frame.sendCoreEvent(event);
	}

	// protected void sendCoreEventSelectionCurrentTrajectoriesRelinkingRun() {
	// final OmegaGUIFrame frame = this.getOmegaGUIFrame();
	// final OmegaCoreEvent event = new
	// OmegaCoreEventSelectionTrajectoriesRelinkingRun(
	// OmegaCoreEvent.SOURCE_SIDE_BAR,
	// this.overlaysPanel.getPreviouslySelectedTRRun());
	// frame.sendCoreEvent(event);
	// }

	protected void sendCoreEventSelectionTrajectoriesSegmentationRun() {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaCoreEvent event = new OmegaCoreEventSelectionAnalysisRun(
				OmegaCoreEvent.SOURCE_SIDE_BAR,
				this.overlaysPanel.getSelectedTSRun());
		frame.sendCoreEvent(event);
	}

	// protected void sendCoreEventSelectionCurrentTrajectoriesSegmentationRun()
	// {
	// final OmegaGUIFrame frame = this.getOmegaGUIFrame();
	// final OmegaCoreEvent event = new
	// OmegaCoreEventSelectionTrajectoriesSegmentationRun(
	// OmegaCoreEvent.SOURCE_SIDE_BAR,
	// this.overlaysPanel.getPreviouslySelectedTSRun());
	// frame.sendCoreEvent(event);
	// }

	@Override
	public void sendCoreEventTrajectories(
			final List<OmegaTrajectory> trajectories, final boolean selection) {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaCoreEvent event = new OmegaCoreEventTrajectories(
				OmegaCoreEvent.SOURCE_SIDE_BAR, trajectories, selection);
		frame.sendCoreEvent(event);
	}

	@Override
	public void sendCoreEventSegments(
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
			final OmegaSegmentationTypes segmTypes, final boolean selection) {
		final OmegaGUIFrame frame = this.getOmegaGUIFrame();
		final OmegaCoreEvent event = new OmegaCoreEventSegments(
				OmegaCoreEvent.SOURCE_SIDE_BAR, segments, segmTypes, selection);
		frame.sendCoreEvent(event);
	}

	public void selectImage(final OmegaAnalysisRunContainerInterface image) {
		if (image == null)
			return;
		this.isHandlingEvent = true;
		this.infoPanel.update((OmegaElement) image);
		if (image instanceof OrphanedAnalysisContainer) {
			this.itemIndex = 0;
			this.manageItemChanged();
		} else {
			try {
				final int newItemIndex = this.loadedData
						.getElementIndex((OmegaElement) image);
				if (this.itemIndex != newItemIndex) {
					this.itemIndex = newItemIndex;
					this.manageItemChanged();
				}
			} catch (final OmegaCoreExceptionLoadedElementNotFound ex) {
				OmegaLogFileManager.handleCoreException(ex, true);
				// TODO should I do something here?
			}
		}
		this.isHandlingEvent = false;
	}

	public void selectParticleDetectionRun(
			final OmegaParticleDetectionRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlaysPanel.selectParticleDetectionRun(analysisRun);
		this.isHandlingEvent = false;
	}

	public void selectParticleLinkingRun(
			final OmegaParticleLinkingRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlaysPanel.selectParticleLinkingRun(analysisRun);
		this.isHandlingEvent = false;
	}

	public void selectTrajectoriesRelinkingRun(
			final OmegaTrajectoriesRelinkingRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlaysPanel.selectTrajectoriesRelinkingRun(analysisRun);
		this.isHandlingEvent = false;
	}

	public void selectCurrentTrajectoriesRelinking(
			final List<OmegaTrajectory> trajectories) {
		this.isHandlingEvent = true;
		this.overlaysPanel.selectCurrentTrajectoriesRelinkingRun();
		this.setTrajectories(trajectories);
		this.isHandlingEvent = false;
	}

	public void selectTrajectoriesSegmentationRun(
			final OmegaTrajectoriesSegmentationRun analysisRun) {
		this.isHandlingEvent = true;
		this.overlaysPanel.selectTrajectoriesSegmentationRun(analysisRun);
		this.isHandlingEvent = false;
	}

	public void selectCurrentTrajectoriesSegmentationRun(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		this.isHandlingEvent = true;
		this.overlaysPanel.selectCurrentSegmentationRun();
		this.setSegments(segmentsMap);
		this.isHandlingEvent = false;
	}

	public void render() {
		this.canvas.render();
	}

	public void setCompressed(final boolean compressed) {
		this.canvas.setCompressed(compressed);
	}

	public void setZValues(final int z, final boolean isDefault) {
		this.canvas.setZValues(z, isDefault);
	}

	public void setTValues(final int t, final boolean isDefault) {
		this.canvas.setTValues(t, isDefault);
		if (this.overlaysPanel.isParticlesOverlay()) {
			final List<OmegaROI> particles = this.overlaysPanel
					.getFrameParticlesOverlay(t);
			this.canvas.setParticles(particles);
		}
	}

	public int getCurrentT() {
		return this.canvas.getCurrentT();
	}

	public void setActiveChannel(final int channel, final boolean isActive) {
		this.canvas.setActiveChannel(channel, isActive);
	}

	public void setParticles(final List<OmegaROI> particles) {
		this.canvas.setParticles(particles);
	}

	public void setTrajectories(final List<OmegaTrajectory> trajectories) {
		this.canvas.setTrajectories(trajectories);
	}

	public void setSegments(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		this.canvas.setSegments(segmentsMap);
	}

	public OmegaGateway getGateway() {
		return this.canvas.getGateway();
	}

	public OmegaImagePixels getImagePixels() {
		return this.canvas.getImagePixels();
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.canvas.updateParentContainer(parent);
		this.overlaysPanel.updateParentContainer(parent);
		this.renderingPanel.updateParentContainer(parent);
		this.infoPanel.updateParentContainer(parent);
	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories,
			final boolean selection) {
		this.canvas.updateTrajectories(trajectories, selection);
	}

	public void updateSegments(
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
			final OmegaSegmentationTypes segmTypes, final boolean selection) {
		this.canvas.updateSegments(segments, segmTypes, selection);
	}

	public void updateParticles(final List<OmegaROI> particles) {
		this.canvas.setParticles(particles);
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

	public void setShowTrajectoriesOnlyActive(final boolean enabled) {
		this.canvas.setShowTrajectoriesOnlyActive(enabled);
	}

	public void setShowTrajectoriesOnlyUpToT(final boolean enabled) {
		this.canvas.setShowTrajectoriesOnlyUpToT(enabled);
	}

	public void setShowTrajectoriesOnlyStartingAtT(final boolean enabled) {
		this.canvas.setShowTrajectoriesOnlyStartingAtT(enabled);
	}

	@Override
	public void fireElementChanged() {
		if (this.currentElement instanceof OmegaImage) {
			this.sendCoreEventSelectionImage((OmegaImage) this.currentElement);
		} else {
			this.sendCoreEventSelectionImage(null);
		}
	}
}
