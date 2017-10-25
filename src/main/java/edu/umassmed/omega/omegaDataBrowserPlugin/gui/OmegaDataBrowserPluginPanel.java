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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
import edu.umassmed.omega.commons.data.OmegaData;
import edu.umassmed.omega.commons.data.OmegaLoadedData;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainerInterface;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresDiffusivityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresIntensityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresMobilityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresVelocityRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaNamedElement;
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

	private boolean isHandlingEvent;
	
	private OmegaAnalysisRunContainerInterface selectedDataElement,
			selectedDetectionRun, selectedLinkingRun, selectedTrajRelinkingRun,
			selectedTrajSegmentationRun, selectedTrackingMeasuresRun,
			selectedSNRRun;
	
	private JButton import_btt, exportAll_btt, exportLast_btt, results_btt;
	
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

		this.isHandlingEvent = false;
		
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
				OmegaParticleDetectionRun.getStaticDisplayName(),
				this.selectedDataElement, this.loadedAnalysisRuns);
		this.spotLinkPanel = new OmegaDataBrowserAnalysisBrowserPanel(
				this.getParentContainer(), this,
				OmegaParticleLinkingRun.getStaticDisplayName(),
				this.selectedDetectionRun, this.loadedAnalysisRuns);
		this.trackAdjPanel = new OmegaDataBrowserAnalysisBrowserPanel(
				this.getParentContainer(), this,
				OmegaTrajectoriesRelinkingRun.getStaticDisplayName(),
				this.selectedLinkingRun, this.loadedAnalysisRuns);
		this.trackSegmPanel = new OmegaDataBrowserAnalysisBrowserPanel(
				this.getParentContainer(), this,
				OmegaTrajectoriesSegmentationRun.getStaticDisplayName(),
				this.selectedTrajRelinkingRun, this.loadedAnalysisRuns);
		this.trackingMeasuresPanel = new OmegaDataBrowserAnalysisBrowserPanel(
				this.getParentContainer(), this,
				OmegaTrackingMeasuresRun.getStaticDisplayName(),
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
				this.getParentContainer(), this,
				OmegaSNRRun.getStaticDisplayName(), this.selectedDetectionRun,
				this.loadedAnalysisRuns);
		
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
		this.results_btt = new JButton("See Last Selected Result");
		this.results_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.results_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.results_btt.setEnabled(false);
		buttPanel2.add(this.results_btt);
		this.exportLast_btt = new JButton("Export Last Selected Result");
		this.exportLast_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.exportLast_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.exportLast_btt.setEnabled(false);
		this.exportAll_btt = new JButton("Export All Results");
		this.exportAll_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.exportAll_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.exportAll_btt.setEnabled(false);
		buttPanel2.add(this.exportLast_btt);
		buttPanel2.add(this.exportAll_btt);
		analysisPanel.add(buttPanel2, BorderLayout.SOUTH);
		
		this.splitPane = new JSplitPane();
		// this.splitPane.setDividerLocation(0.3);
		this.splitPane.setLeftComponent(dataPanel);
		this.splitPane.setRightComponent(analysisPanel);
		
		this.add(this.splitPane, BorderLayout.CENTER);
	}
	
	public void updateTrees() {
		this.loadedDataPanel.updateTree(this.omegaData);
		this.selectedDataElement = null;
		this.import_btt.setEnabled(false);
		this.exportAll_btt.setEnabled(false);
		this.exportLast_btt.setEnabled(false);
		this.results_btt.setEnabled(false);
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
		// TODO to be tested
		this.fireEventSelectionImage();
		this.fireEventSelectionParticleDetectionRun();
		this.fireEventSelectionParticleLinkingRun();
		this.fireEventSelectionTrajectoriesRelinkingRun();
		this.fireEventSelectionTrajectoriesSegmentationRun();
		this.fireEventSelectionTrackingMeasuresRun();
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
		this.exportLast_btt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaDataBrowserPluginPanel.this.handleDataExporter(true);
			}
		});
		this.exportAll_btt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaDataBrowserPluginPanel.this.handleDataExporter(false);
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
		String c = null, z = null;
		if (this.selectedDetectionRun != null) {
			for (final OmegaParameter param : ((OmegaAnalysisRun) this.selectedDetectionRun)
					.getAlgorithmSpec().getParameters()) {
				if (param.getName().equals(
						OmegaConstantsAlgorithmParameters.PARAM_CHANNEL)) {
					c = param.getStringValue();
				} else if (param.getName().equals(
						OmegaConstantsAlgorithmParameters.PARAM_ZSECTION)) {
					z = param.getStringValue();
				}
			}
		}
		
		if ((this.selectedTrackingMeasuresRun != null)
				&& (this.selectedTrajSegmentationRun != null)) {
			this.resultsDialog.setAnalysis(
					(OmegaAnalysisRun) this.selectedTrackingMeasuresRun,
					(OmegaAnalysisRun) this.selectedTrajSegmentationRun, c, z);
		} else if (this.selectedTrajSegmentationRun != null) {
			this.resultsDialog.setAnalysis(
					(OmegaAnalysisRun) this.selectedTrajSegmentationRun, null,
					c, z);
		} else if (this.selectedTrajRelinkingRun != null) {
			this.resultsDialog.setAnalysis(
					(OmegaAnalysisRun) this.selectedTrajRelinkingRun, null, c,
					z);
		} else if (this.selectedLinkingRun != null) {
			this.resultsDialog.setAnalysis(
					(OmegaAnalysisRun) this.selectedLinkingRun, null, c, z);
		} else if (this.selectedSNRRun != null) {
			this.resultsDialog.setAnalysis(
					(OmegaAnalysisRun) this.selectedSNRRun,
					(OmegaAnalysisRun) this.selectedDetectionRun, c, z);
		} else if (this.selectedDetectionRun != null) {
			this.resultsDialog.setAnalysis(
					(OmegaAnalysisRun) this.selectedDetectionRun, null, c, z);
		} else {
			this.resultsDialog.setAnalysis(null, null, c, z);
		}
		this.resultsDialog.setVisible(true);
	}
	
	private void handleDataExporter(final boolean selectLastOnly) {
		final OmegaDataBrowserPlugin plugin = (OmegaDataBrowserPlugin) this
				.getPlugin();
		final OmegaTracksExporter ote = plugin.getTracksExporter();
		if (this.selectedDataElement != null) {
			final Map<Integer, OmegaImage> images = new LinkedHashMap<Integer, OmegaImage>();
			images.put(0, (OmegaImage) this.selectedDataElement);
			ote.setImages(images);
		}

		if (this.selectedDetectionRun != null) {
			final Map<Integer, Map<Integer, OmegaParticleDetectionRun>> pDetRuns = new LinkedHashMap<Integer, Map<Integer, OmegaParticleDetectionRun>>();
			final Map<Integer, OmegaParticleDetectionRun> pDets = new LinkedHashMap<Integer, OmegaParticleDetectionRun>();
			pDets.put(0, (OmegaParticleDetectionRun) this.selectedDetectionRun);
			pDetRuns.put(0, pDets);
			ote.setParticleDetectionRun(pDetRuns);
		}

		if (this.selectedLinkingRun != null) {
			final Map<Integer, Map<Integer, OmegaParticleLinkingRun>> pLinkRuns = new LinkedHashMap<Integer, Map<Integer, OmegaParticleLinkingRun>>();
			final Map<Integer, OmegaParticleLinkingRun> pLinks = new LinkedHashMap<Integer, OmegaParticleLinkingRun>();
			pLinks.put(0, (OmegaParticleLinkingRun) this.selectedLinkingRun);
			pLinkRuns.put(0, pLinks);
			ote.setParticleLinkingRun(pLinkRuns);
		}

		if (this.selectedTrajRelinkingRun != null) {
			final Map<Integer, Map<Integer, OmegaTrajectoriesRelinkingRun>> pRelinkRuns = new LinkedHashMap<Integer, Map<Integer, OmegaTrajectoriesRelinkingRun>>();
			final Map<Integer, OmegaTrajectoriesRelinkingRun> pRelinks = new LinkedHashMap<Integer, OmegaTrajectoriesRelinkingRun>();
			pRelinks.put(
					0,
					(OmegaTrajectoriesRelinkingRun) this.selectedTrajRelinkingRun);
			pRelinkRuns.put(0, pRelinks);
			ote.setTrackRelinkingRun(pRelinkRuns);
		}

		if (this.selectedTrajSegmentationRun != null) {
			final Map<Integer, Map<Integer, OmegaTrajectoriesSegmentationRun>> pSegmRuns = new LinkedHashMap<Integer, Map<Integer, OmegaTrajectoriesSegmentationRun>>();
			final Map<Integer, OmegaTrajectoriesSegmentationRun> pSegms = new LinkedHashMap<Integer, OmegaTrajectoriesSegmentationRun>();
			pSegms.put(
					0,
					(OmegaTrajectoriesSegmentationRun) this.selectedTrajSegmentationRun);
			pSegmRuns.put(0, pSegms);
			ote.setTrackSegmentationRun(pSegmRuns);
		}
		
		if (this.selectedTrackingMeasuresRun != null) {
			if (this.selectedTrackingMeasuresRun instanceof OmegaTrackingMeasuresIntensityRun) {
				final Map<Integer, Map<Integer, OmegaTrackingMeasuresIntensityRun>> inteRuns = new LinkedHashMap<Integer, Map<Integer, OmegaTrackingMeasuresIntensityRun>>();
				final Map<Integer, OmegaTrackingMeasuresIntensityRun> intes = new LinkedHashMap<Integer, OmegaTrackingMeasuresIntensityRun>();
				intes.put(
						0,
						(OmegaTrackingMeasuresIntensityRun) this.selectedTrackingMeasuresRun);
				inteRuns.put(0, intes);
				ote.setIntensityTrackingMeasuresRun(inteRuns);
			} else if (this.selectedTrackingMeasuresRun instanceof OmegaTrackingMeasuresVelocityRun) {
				final Map<Integer, Map<Integer, OmegaTrackingMeasuresVelocityRun>> veloRuns = new LinkedHashMap<Integer, Map<Integer, OmegaTrackingMeasuresVelocityRun>>();
				final Map<Integer, OmegaTrackingMeasuresVelocityRun> velos = new LinkedHashMap<Integer, OmegaTrackingMeasuresVelocityRun>();
				velos.put(
						0,
						(OmegaTrackingMeasuresVelocityRun) this.selectedTrackingMeasuresRun);
				veloRuns.put(0, velos);
				ote.setVelocityTrackingMeasuresRun(veloRuns);
			} else if (this.selectedTrackingMeasuresRun instanceof OmegaTrackingMeasuresMobilityRun) {
				final Map<Integer, Map<Integer, OmegaTrackingMeasuresMobilityRun>> mobiRuns = new LinkedHashMap<Integer, Map<Integer, OmegaTrackingMeasuresMobilityRun>>();
				final Map<Integer, OmegaTrackingMeasuresMobilityRun> mobis = new LinkedHashMap<Integer, OmegaTrackingMeasuresMobilityRun>();
				mobis.put(
						0,
						(OmegaTrackingMeasuresMobilityRun) this.selectedTrackingMeasuresRun);
				mobiRuns.put(0, mobis);
				ote.setMobilityTrackingMeasuresRun(mobiRuns);
			} else if (this.selectedTrackingMeasuresRun instanceof OmegaTrackingMeasuresDiffusivityRun) {
				final Map<Integer, Map<Integer, OmegaTrackingMeasuresDiffusivityRun>> diffRuns = new LinkedHashMap<Integer, Map<Integer, OmegaTrackingMeasuresDiffusivityRun>>();
				final Map<Integer, OmegaTrackingMeasuresDiffusivityRun> diffs = new LinkedHashMap<Integer, OmegaTrackingMeasuresDiffusivityRun>();
				diffs.put(
						0,
						(OmegaTrackingMeasuresDiffusivityRun) this.selectedTrackingMeasuresRun);
				diffRuns.put(0, diffs);
				ote.setDiffusivityTrackingMeasuresRun(diffRuns);
			}
		}
		
		// ote.setTrackingMeasuresRun((OmegaTrackingMeasuresRun)
		// this.selectedTrackingMeasuresRun);
		if (selectLastOnly) {
			ote.setExportLastOnly();
		}
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

	public void selectAnalysisContainer(
			final OmegaAnalysisRunContainerInterface analysisRunContainer) {
		this.isHandlingEvent = true;
		this.setSelectedAnalysisContainer(analysisRunContainer);
		this.loadedDataPanel
				.selectTreeElement((OmegaNamedElement) analysisRunContainer);
		this.isHandlingEvent = false;
	}
	
	public void selectSubAnalysisContainer(
			final OmegaAnalysisRunContainerInterface analysisRunContainer) {
		this.isHandlingEvent = true;
		this.setSelectedSubAnalysisContainer(analysisRunContainer);
		if (analysisRunContainer instanceof OmegaSNRRun) {
			this.snrPanel
					.selectTreeElement((OmegaNamedElement) analysisRunContainer);
		} else if (analysisRunContainer instanceof OmegaTrackingMeasuresRun) {
			this.trackingMeasuresPanel
					.selectTreeElement((OmegaNamedElement) analysisRunContainer);
		} else if (analysisRunContainer instanceof OmegaTrajectoriesSegmentationRun) {
			this.trackSegmPanel
					.selectTreeElement((OmegaNamedElement) analysisRunContainer);
		} else if (analysisRunContainer instanceof OmegaTrajectoriesRelinkingRun) {
			this.trackAdjPanel
					.selectTreeElement((OmegaNamedElement) analysisRunContainer);
		} else if (analysisRunContainer instanceof OmegaParticleLinkingRun) {
			this.spotLinkPanel
					.selectTreeElement((OmegaNamedElement) analysisRunContainer);
		} else if (analysisRunContainer instanceof OmegaParticleDetectionRun) {
			this.spotDetPanel
					.selectTreeElement((OmegaNamedElement) analysisRunContainer);
		}
		this.isHandlingEvent = false;
	}
	
	protected void setSelectedAnalysisContainer(
			final OmegaAnalysisRunContainerInterface analysisRunContainer) {
		this.selectedDataElement = analysisRunContainer;
		if (!this.isHandlingEvent
				&& (this.selectedDataElement instanceof OmegaImage)) {
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
		if (this.selectedDataElement instanceof OmegaImage) {
			this.exportAll_btt.setEnabled(true);
		} else {
			this.exportAll_btt.setEnabled(false);
		}
		this.exportLast_btt.setEnabled(false);
		this.results_btt.setEnabled(false);
		this.repaint();
	}
	
	public void setSelectedSubAnalysisContainer(
			final OmegaAnalysisRunContainerInterface analysisRunContainer) {
		this.exportLast_btt.setEnabled(true);
		this.results_btt.setEnabled(true);
		if (analysisRunContainer instanceof OmegaSNRRun) {
			this.selectedSNRRun = analysisRunContainer;
			if (!this.isHandlingEvent) {
				this.fireEventSelectionSNRRun();
			}
		} else if (analysisRunContainer instanceof OmegaTrackingMeasuresRun) {
			this.selectedTrackingMeasuresRun = analysisRunContainer;
			if (!this.isHandlingEvent) {
				this.fireEventSelectionTrackingMeasuresRun();
			}
		} else if (analysisRunContainer instanceof OmegaTrajectoriesSegmentationRun) {
			this.selectedTrajSegmentationRun = analysisRunContainer;
			if (!this.isHandlingEvent) {
				this.fireEventSelectionTrajectoriesSegmentationRun();
			}
			this.selectedTrackingMeasuresRun = null;
			this.trackingMeasuresPanel
					.updateTree(this.selectedTrajSegmentationRun);
		} else if (analysisRunContainer instanceof OmegaTrajectoriesRelinkingRun) {
			this.selectedTrajRelinkingRun = analysisRunContainer;
			if (!this.isHandlingEvent) {
				this.fireEventSelectionTrajectoriesRelinkingRun();
			}
			this.selectedTrajSegmentationRun = null;
			this.selectedTrackingMeasuresRun = null;
			this.trackSegmPanel.updateTree(this.selectedTrajRelinkingRun);
			this.trackingMeasuresPanel
					.updateTree(this.selectedTrajSegmentationRun);
		} else if (analysisRunContainer instanceof OmegaParticleLinkingRun) {
			this.selectedLinkingRun = analysisRunContainer;
			if (!this.isHandlingEvent) {
				this.fireEventSelectionParticleLinkingRun();
			}
			this.selectedTrajRelinkingRun = null;
			this.selectedTrajSegmentationRun = null;
			this.selectedTrackingMeasuresRun = null;
			this.trackAdjPanel.updateTree(this.selectedLinkingRun);
			this.trackSegmPanel.updateTree(this.selectedTrajRelinkingRun);
			this.trackingMeasuresPanel
					.updateTree(this.selectedTrajSegmentationRun);
		} else if (analysisRunContainer instanceof OmegaParticleDetectionRun) {
			this.selectedDetectionRun = analysisRunContainer;
			if (!this.isHandlingEvent) {
				this.fireEventSelectionParticleDetectionRun();
			}
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
			this.exportLast_btt.setEnabled(false);
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
	
	public void fireEventAnalysisRunDeselection() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionAnalysisRun(
				this.getPlugin(), null);
		this.getPlugin().fireEvent(event);
	}

	public void fireEventImageDeselection() {
		final OmegaPluginEvent event = new OmegaPluginEventSelectionImage(
				this.getPlugin(), null);
		this.getPlugin().fireEvent(event);
	}
	
	public void deselectAllChildren(
			final OmegaAnalysisRunContainerInterface analysisRunContainer) {
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
			if (!(analysisRunContainer instanceof OmegaTrackingMeasuresRun)
					&& !(analysisRunContainer instanceof OmegaSNRRun)) {
				this.spotDetPanel.deselectAll();
			}
		}
		this.removeAllChildrenFromLoaded(analysisRunContainer);
		this.fireDataChangedEvent();
		// this.analysisPanels.get(4).updateTree(this.selectedTrajSegRun);
		this.repaint();
	}

	private void removeAllChildrenFromLoaded(
			final OmegaAnalysisRunContainerInterface analysisRunContainer) {
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
