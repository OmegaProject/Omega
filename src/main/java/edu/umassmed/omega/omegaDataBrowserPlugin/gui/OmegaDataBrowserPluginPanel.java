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
package edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.data.OmegaData;
import edu.umassmed.omega.commons.data.OmegaLoadedData;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventDataChanged;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionAnalysisRun;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventSelectionImage;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.gui.dialogs.GenericResultsDialog;
import edu.umassmed.omega.commons.gui.interfaces.GenericElementInformationContainerInterface;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.commons.trajectoryTool.OmegaTracksExporter;
import edu.umassmed.omega.commons.trajectoryTool.OmegaTracksImporter;
import edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserConstants;
import edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserPlugin;

public class OmegaDataBrowserPluginPanel extends GenericPluginPanel implements
        GenericElementInformationContainerInterface {

	private static final long serialVersionUID = 4804154980131328463L;

	// private JMenu visualizationMenu;
	// private JMenuItem refreshMItem;
	
	private static boolean HAS_CHECKBOX_PROPAGATION = true;

	private OmegaDataBrowserLoadedDataBrowserPanel loadedDataPanel;
	private OmegaDataBrowserAnalysisBrowserPanel spotDetPanel, spotLinkPanel,
	        trackAdjPanel, trackSegmPanel, trackingMeasuresPanel, snrPanel;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private GenericPanel trackingAnalysisPanel, genericAnalysisPanel;

	private final OmegaData omegaData;
	private final OmegaLoadedData loadedData;
	private final List<OmegaAnalysisRun> loadedAnalysisRuns;
	
	private GenericResultsDialog resultsDialog;

	private OmegaAnalysisRunContainer selectedDataElement,
	        selectedDetectionRun, selectedLinkingRun, selectedTrajRelinkingRun,
	        selectedTrajSegmentationRun, selectedTrackingMeasuresRun,
	        selectedSNRRun;

	private JButton import_btt, export_btt, results_btt;

	private MouseMotionListener splitPaneDivider_mml;
	private ComponentListener resize_cl;

	public OmegaDataBrowserPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final OmegaData omegaData,
	        final OmegaLoadedData loadedData,
	        final List<OmegaAnalysisRun> loadedAnalysisRuns, final int index) {
		super(parent, plugin, index);

		this.omegaData = omegaData;
		this.loadedData = loadedData;
		this.loadedAnalysisRuns = loadedAnalysisRuns;
		this.selectedDataElement = null;
		this.selectedDetectionRun = null;
		this.selectedLinkingRun = null;
		this.selectedTrajRelinkingRun = null;
		this.selectedTrajSegmentationRun = null;
		this.selectedTrackingMeasuresRun = null;

		this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		this.createMenu();
		this.createAndAddWidgets();
		this.addListeners();
	}

	private void createMenu() {
		// final JMenuBar menu = super.getMenu();
		// this.visualizationMenu = new JMenu("Visualization");
		// this.refreshMItem = new JMenuItem("Refresh data");
		// this.visualizationMenu.add(this.refreshMItem);

		// menu.add(this.visualizationMenu);
	}

	public void createAndAddWidgets() {
		this.resultsDialog = new GenericResultsDialog(
		        this.getParentContainer(), "Analysis results", true);

		final JPanel dataPanel = new JPanel();
		dataPanel.setLayout(new BorderLayout());
		this.loadedDataPanel = new OmegaDataBrowserLoadedDataBrowserPanel(
		        this.getParentContainer(), this, this.omegaData,
		        this.loadedData, this);
		dataPanel.add(this.loadedDataPanel, BorderLayout.CENTER);
		this.import_btt = new JButton("Import tracks");
		this.import_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.import_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.import_btt.setEnabled(false);
		final JPanel buttPanel1 = new JPanel();
		buttPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttPanel1.add(this.import_btt);
		dataPanel.add(buttPanel1, BorderLayout.SOUTH);

		// this.add(this.loadedDataPanel, BorderLayout.WEST);

		this.tabbedPane = new JTabbedPane(SwingConstants.TOP,
		        JTabbedPane.SCROLL_TAB_LAYOUT);

		this.trackingAnalysisPanel = new GenericPanel(this.getParentContainer());
		this.trackingAnalysisPanel.setLayout(new GridLayout(1, 5));

		// TODO change classes based on thingy ???

		this.spotDetPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this,
		        OmegaParticleDetectionRun.class, this.selectedDataElement,
		        this.loadedAnalysisRuns);
		this.spotLinkPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaParticleLinkingRun.class,
		        this.selectedDetectionRun, this.loadedAnalysisRuns);
		this.trackAdjPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this,
		        OmegaTrajectoriesRelinkingRun.class, this.selectedLinkingRun,
		        this.loadedAnalysisRuns);
		this.trackSegmPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this,
		        OmegaTrajectoriesSegmentationRun.class,
		        this.selectedTrajRelinkingRun, this.loadedAnalysisRuns);
		this.trackingMeasuresPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this,
		        OmegaTrackingMeasuresRun.class,
		        this.selectedTrajSegmentationRun, this.loadedAnalysisRuns);
		// final OmegaDataBrowserAnalysisBrowserPanel motionAnalysisPanel = new
		// OmegaDataBrowserAnalysisBrowserPanel(
		// this.getParentContainer(), this, OmegaAnalysisRun.class,
		// this.selectedTrajSegRun, this.loadedAnalysisRuns);
		// this.analysisPanels.add(motionAnalysisPanel);

		this.trackingAnalysisPanel.add(this.spotDetPanel);
		this.trackingAnalysisPanel.add(this.spotLinkPanel);
		this.trackingAnalysisPanel.add(this.trackAdjPanel);
		this.trackingAnalysisPanel.add(this.trackSegmPanel);
		this.trackingAnalysisPanel.add(this.trackingMeasuresPanel);

		this.genericAnalysisPanel = new GenericPanel(this.getParentContainer());
		this.genericAnalysisPanel.setLayout(new GridLayout(1, 1));

		this.snrPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaSNRRun.class,
		        this.selectedDetectionRun, this.loadedAnalysisRuns);

		// genericAnalysisPanel.add(spotDetectionPanel);
		this.genericAnalysisPanel.add(this.snrPanel);

		final JScrollPane trackingScrollPane = new JScrollPane(
		        this.trackingAnalysisPanel);
		this.tabbedPane.add(OmegaDataBrowserConstants.TRACKING_TABNAME,
		        trackingScrollPane);

		final JScrollPane genericScrollPane = new JScrollPane(
		        this.genericAnalysisPanel);
		this.tabbedPane.add(OmegaDataBrowserConstants.OTHER_TABNAME,
		        genericScrollPane);

		final JPanel analysisPanel = new JPanel();
		analysisPanel.setLayout(new BorderLayout());
		analysisPanel.add(this.tabbedPane, BorderLayout.CENTER);
		final JPanel buttPanel2 = new JPanel();
		buttPanel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.results_btt = new JButton("See results");
		this.results_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.results_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.results_btt.setEnabled(false);
		buttPanel2.add(this.results_btt);
		this.export_btt = new JButton("Export results");
		this.export_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.export_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.export_btt.setEnabled(false);
		buttPanel2.add(this.export_btt);
		analysisPanel.add(buttPanel2, BorderLayout.SOUTH);

		this.splitPane = new JSplitPane();
		// this.splitPane.setDividerLocation(0.3);
		this.splitPane.setLeftComponent(dataPanel);
		this.splitPane.setRightComponent(analysisPanel);

		this.add(this.splitPane, BorderLayout.CENTER);
	}

	public void updateTrees() {
		this.loadedDataPanel.updateTree(this.omegaData);
		this.spotDetPanel.updateTree(this.selectedDataElement);
		this.spotLinkPanel.updateTree(this.selectedDataElement);
		this.trackAdjPanel.updateTree(this.selectedDataElement);
		this.trackSegmPanel.updateTree(this.selectedDataElement);
		this.trackingMeasuresPanel.updateTree(this.selectedDataElement);
		this.snrPanel.updateTree(this.selectedDataElement);
	}

	protected void fireDataChangedEvent() {
		this.getPlugin().fireEvent(
		        new OmegaPluginEventDataChanged(this.getPlugin()));
	}

	private void addListeners() {
		this.tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				OmegaDataBrowserPluginPanel.this.handleTabChanged();
			}
		});
		this.resize_cl = new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaDataBrowserPluginPanel.this.handleResize();
			}
		};
		this.addComponentListener(this.resize_cl);
		this.splitPaneDivider_mml = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(final MouseEvent evt) {
				OmegaDataBrowserPluginPanel.this.handleDividerMoved();
			}
		};
		this.import_btt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaDataBrowserPluginPanel.this.handleTracksImporter();
			}
		});
		this.export_btt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaDataBrowserPluginPanel.this.handleDataExporter();
			}
		});
		this.results_btt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaDataBrowserPluginPanel.this.handleDataViewer();
			}
		});
		((BasicSplitPaneUI) this.splitPane.getUI()).getDivider()
		.addMouseMotionListener(this.splitPaneDivider_mml);
	}
	
	private void handleDataViewer() {
		if ((this.selectedTrackingMeasuresRun != null)
				&& (this.selectedTrajSegmentationRun != null)) {
			this.resultsDialog.setAnalysis(
			        (OmegaAnalysisRun) this.selectedTrackingMeasuresRun,
			        (OmegaAnalysisRun) this.selectedTrajSegmentationRun);
		} else if (this.selectedTrajSegmentationRun != null) {
			this.resultsDialog.setAnalysis(
			        (OmegaAnalysisRun) this.selectedTrajSegmentationRun, null);
		} else if (this.selectedTrajRelinkingRun != null) {
			this.resultsDialog.setAnalysis(
			        (OmegaAnalysisRun) this.selectedTrajRelinkingRun, null);
		} else if (this.selectedLinkingRun != null) {
			this.resultsDialog.setAnalysis(
			        (OmegaAnalysisRun) this.selectedLinkingRun, null);
		} else if (this.selectedSNRRun != null) {
			this.resultsDialog.setAnalysis(
			        (OmegaAnalysisRun) this.selectedSNRRun,
			        (OmegaAnalysisRun) this.selectedDetectionRun);
		} else if (this.selectedDetectionRun != null) {
			this.resultsDialog.setAnalysis(
			        (OmegaAnalysisRun) this.selectedDetectionRun, null);
		} else {
			this.resultsDialog.setAnalysis(null, null);
		}
		this.resultsDialog.setVisible(true);
	}

	private void handleDataExporter() {
		final OmegaDataBrowserPlugin plugin = (OmegaDataBrowserPlugin) this
		        .getPlugin();
		final OmegaTracksExporter ote = plugin.getTracksExporter();
		ote.setParticleDetectionRun((OmegaParticleDetectionRun) this.selectedDetectionRun);
		ote.setParticleLinkingRun((OmegaParticleLinkingRun) this.selectedLinkingRun);
		ote.setTrackRelinkingRun((OmegaTrajectoriesRelinkingRun) this.selectedTrajRelinkingRun);
		ote.setTrackSegmentationRun((OmegaTrajectoriesSegmentationRun) this.selectedTrajSegmentationRun);
		ote.setTrackingMeasuresRun((OmegaTrackingMeasuresRun) this.selectedTrackingMeasuresRun);
		ote.showDialog(this.getParentContainer());
	}

	private void handleTracksImporter() {
		final OmegaDataBrowserPlugin plugin = (OmegaDataBrowserPlugin) this
		        .getPlugin();
		final OmegaTracksImporter oti = plugin.getTracksImporter();
		oti.setContainer(this.selectedDataElement);
		oti.showDialog(this.getParentContainer());
	}

	private void handleDividerMoved() {
		((BasicSplitPaneUI) this.splitPane.getUI()).getDivider()
		        .removeMouseMotionListener(this.splitPaneDivider_mml);
		this.removeComponentListener(this.resize_cl);
	}

	private void handleResize() {
		this.splitPane.setDividerLocation(0.3);
	}

	private void handleTabChanged() {
		final int selectedIndex = this.tabbedPane.getSelectedIndex();
		final String title = this.tabbedPane.getTitleAt(selectedIndex);
		if (title.equals(OmegaDataBrowserConstants.TRACKING_TABNAME)) {
			this.trackingAnalysisPanel.remove(this.spotDetPanel);
			this.genericAnalysisPanel.remove(this.spotDetPanel);
			this.trackingAnalysisPanel.add(this.spotDetPanel, 0);
		} else if (title.equals(OmegaDataBrowserConstants.OTHER_TABNAME)) {
			this.trackingAnalysisPanel.remove(this.spotDetPanel);
			this.genericAnalysisPanel.remove(this.spotDetPanel);
			this.genericAnalysisPanel.add(this.spotDetPanel, 0);
		}
		this.setSelectedAnalysisContainer(this.selectedDataElement);
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.loadedDataPanel.updateParentContainer(parent);
		this.spotDetPanel.updateParentContainer(parent);
		this.spotLinkPanel.updateParentContainer(parent);
		this.trackAdjPanel.updateParentContainer(parent);
		this.trackSegmPanel.updateParentContainer(parent);
		this.trackingMeasuresPanel.updateParentContainer(parent);
		this.snrPanel.updateParentContainer(parent);
		this.resultsDialog.updateParentContainer(parent);
	}

	public void setSelectedAnalysisContainer(
	        final OmegaAnalysisRunContainer analysisRunContainer) {
		this.selectedDataElement = analysisRunContainer;
		if (this.selectedDataElement instanceof OmegaImage) {
			this.fireEventSelectionImage();
		}
		this.spotDetPanel.updateTree(this.selectedDataElement);
		this.setSelectedSubAnalysisContainer(null);
		// this.analysisPanels.get(0).updateTree(this.selectedOmeroElement);
		if ((this.selectedDataElement instanceof OmegaImage)
				|| (this.selectedDataElement instanceof OrphanedAnalysisContainer)) {
			this.import_btt.setEnabled(true);
		} else {
			this.import_btt.setEnabled(false);
		}
		this.export_btt.setEnabled(false);
		this.results_btt.setEnabled(false);
		this.repaint();
	}

	public void setSelectedSubAnalysisContainer(
	        final OmegaAnalysisRunContainer analysisRunContainer) {
		this.export_btt.setEnabled(true);
		this.results_btt.setEnabled(true);
		if (analysisRunContainer instanceof OmegaSNRRun) {
			this.selectedSNRRun = analysisRunContainer;
			this.fireEventSelectionSNRRun();
		} else if (analysisRunContainer instanceof OmegaTrackingMeasuresRun) {
			this.selectedTrackingMeasuresRun = analysisRunContainer;
			this.fireEventSelectionTrackingMeasuresRun();
		} else if (analysisRunContainer instanceof OmegaTrajectoriesSegmentationRun) {
			this.selectedTrajSegmentationRun = analysisRunContainer;
			this.fireEventSelectionTrajectoriesSegmentationRun();
			this.selectedTrackingMeasuresRun = null;
			this.trackingMeasuresPanel
			        .updateTree(this.selectedTrajSegmentationRun);
		} else if (analysisRunContainer instanceof OmegaTrajectoriesRelinkingRun) {
			this.selectedTrajRelinkingRun = analysisRunContainer;
			this.fireEventSelectionTrajectoriesRelinkingRun();
			this.selectedTrajSegmentationRun = null;
			this.selectedTrackingMeasuresRun = null;
			this.trackSegmPanel.updateTree(this.selectedTrajRelinkingRun);
		} else if (analysisRunContainer instanceof OmegaParticleLinkingRun) {
			this.selectedLinkingRun = analysisRunContainer;
			this.fireEventSelectionParticleLinkingRun();
			this.selectedTrajRelinkingRun = null;
			this.selectedTrajSegmentationRun = null;
			this.selectedTrackingMeasuresRun = null;
			this.trackAdjPanel.updateTree(this.selectedLinkingRun);
			this.trackSegmPanel.updateTree(this.selectedTrajRelinkingRun);
			this.trackingMeasuresPanel
			        .updateTree(this.selectedTrajSegmentationRun);
		} else if (analysisRunContainer instanceof OmegaParticleDetectionRun) {
			this.selectedDetectionRun = analysisRunContainer;
			this.fireEventSelectionParticleDetectionRun();
			this.selectedLinkingRun = null;
			this.selectedTrajRelinkingRun = null;
			this.selectedTrajSegmentationRun = null;
			this.selectedTrackingMeasuresRun = null;
			this.spotLinkPanel.updateTree(this.selectedDetectionRun);
			this.snrPanel.updateTree(this.selectedDetectionRun);
			this.trackAdjPanel.updateTree(this.selectedLinkingRun);
			this.trackSegmPanel.updateTree(this.selectedTrajRelinkingRun);
			this.trackingMeasuresPanel
			        .updateTree(this.selectedTrajSegmentationRun);
		} else {
			this.export_btt.setEnabled(false);
			this.results_btt.setEnabled(false);
			this.selectedDetectionRun = null;
			this.selectedLinkingRun = null;
			this.selectedTrajRelinkingRun = null;
			this.selectedTrajSegmentationRun = null;
			this.selectedTrackingMeasuresRun = null;
			this.spotLinkPanel.updateTree(this.selectedDetectionRun);
			this.snrPanel.updateTree(this.selectedDetectionRun);
			this.trackAdjPanel.updateTree(this.selectedLinkingRun);
			this.trackingMeasuresPanel.updateTree(this.selectedLinkingRun);
			this.trackSegmPanel.updateTree(this.selectedTrajSegmentationRun);
		}
		// this.analysisPanels.get(4).updateTree(this.selectedTrajSegRun);
		this.repaint();
	}

	public void deselectAllChildren(
	        final OmegaAnalysisRunContainer analysisRunContainer) {
		if (!OmegaDataBrowserPluginPanel.HAS_CHECKBOX_PROPAGATION)
			return;
		if (analysisRunContainer instanceof OmegaTrajectoriesSegmentationRun) {
			this.trackingMeasuresPanel.deselectAll();
		} else if (analysisRunContainer instanceof OmegaTrajectoriesRelinkingRun) {
			this.trackSegmPanel.deselectAll();
		} else if (analysisRunContainer instanceof OmegaParticleLinkingRun) {
			this.trackAdjPanel.deselectAll();
		} else if (analysisRunContainer instanceof OmegaParticleDetectionRun) {
			this.spotLinkPanel.deselectAll();
		} else {
			this.spotDetPanel.deselectAll();
		}
		this.removeAllChildrenFromLoaded(analysisRunContainer);
		this.fireDataChangedEvent();
		// this.analysisPanels.get(4).updateTree(this.selectedTrajSegRun);
		this.repaint();
	}
	
	private void removeAllChildrenFromLoaded(
	        final OmegaAnalysisRunContainer analysisRunContainer) {
		for (final OmegaAnalysisRun analysisRun : analysisRunContainer
		        .getAnalysisRuns()) {
			this.loadedAnalysisRuns.remove(analysisRun);
			this.removeAllChildrenFromLoaded(analysisRun);
		}
	}

	@Override
	public void onCloseOperation() {

	}

	private void fireEventSelectionSNRRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), (OmegaAnalysisRun) this.selectedSNRRun);
		this.getPlugin().fireEvent(event);
	}

	private void fireEventSelectionImage() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionImage(
		        this.getPlugin(), this.selectedDataElement);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionParticleDetectionRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), (OmegaAnalysisRun) this.selectedDetectionRun);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionParticleLinkingRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(), (OmegaAnalysisRun) this.selectedLinkingRun);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionTrajectoriesRelinkingRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(),
		        (OmegaAnalysisRun) this.selectedTrajRelinkingRun);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionTrajectoriesSegmentationRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(),
		        (OmegaAnalysisRun) this.selectedTrajSegmentationRun);
		this.getPlugin().fireEvent(event);
	}
	
	private void fireEventSelectionTrackingMeasuresRun() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
		        this.getPlugin(),
		        (OmegaAnalysisRun) this.selectedTrackingMeasuresRun);
		this.getPlugin().fireEvent(event);
	}
	
	@Override
	public void fireElementChanged() {
		this.fireEventSelectionImage();
	}
}
