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
package edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventDataChanged;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.data.OmegaData;
import edu.umassmed.omega.data.OmegaLoadedData;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaSNRRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesRelinkingRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;

public class OmegaDataBrowserPluginPanel extends GenericPluginPanel {

	private static final long serialVersionUID = 4804154980131328463L;

	private static final String TRACKING_ANALYSIS_TABNAME = "Tracking analysis";
	private static final String OTHER_ANALYSIS_TABNAME = "Other analysis";

	// private JMenu visualizationMenu;
	// private JMenuItem refreshMItem;

	private OmegaDataBrowserLoadedDataBrowserPanel loadedDataPanel;
	private OmegaDataBrowserAnalysisBrowserPanel spotDetPanel, spotLinkPanel,
	        trackAdjPanel, trackSegmPanel, trackingMeasuresPanel, snrPanel;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private GenericPanel trackingAnalysisPanel, genericAnalysisPanel;

	private final OmegaData omegaData;
	private final OmegaLoadedData loadedData;
	private final List<OmegaAnalysisRun> loadedAnalysisRuns;

	private OmegaAnalysisRunContainer selectedOmeroElement,
	        selectedDetectionRun, selectedLinkingRun, selectedTrajRelinkingRun,
	        selectedTrajSegmentationRun, selectedTrackingMeasuresRun,
	        selectedSNRRun;

	public OmegaDataBrowserPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final OmegaData omegaData,
	        final OmegaLoadedData loadedData,
	        final List<OmegaAnalysisRun> loadedAnalysisRuns, final int index) {
		super(parent, plugin, index);

		this.omegaData = omegaData;
		this.loadedData = loadedData;
		this.loadedAnalysisRuns = loadedAnalysisRuns;
		this.selectedOmeroElement = null;
		this.selectedDetectionRun = null;
		this.selectedLinkingRun = null;
		this.selectedTrajRelinkingRun = null;
		this.selectedTrajSegmentationRun = null;
		this.selectedTrackingMeasuresRun = null;

		// this.setPreferredSize(new Dimension(750, 500));
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
		this.loadedDataPanel = new OmegaDataBrowserLoadedDataBrowserPanel(
		        this.getParentContainer(), this, this.omegaData,
		        this.loadedData);

		// this.add(this.loadedDataPanel, BorderLayout.WEST);

		this.tabbedPane = new JTabbedPane(SwingConstants.TOP,
		        JTabbedPane.SCROLL_TAB_LAYOUT);

		this.trackingAnalysisPanel = new GenericPanel(this.getParentContainer());
		this.trackingAnalysisPanel.setLayout(new GridLayout(1, 5));

		// TODO change classes based on thingy ???
		this.spotDetPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this,
		        OmegaParticleDetectionRun.class, this.selectedOmeroElement,
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
		        OmegaTrackingMeasuresRun.class, this.selectedLinkingRun,
		        this.loadedAnalysisRuns);
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
		this.tabbedPane.add(
		        OmegaDataBrowserPluginPanel.TRACKING_ANALYSIS_TABNAME,
		        trackingScrollPane);

		final JScrollPane genericScrollPane = new JScrollPane(
		        this.genericAnalysisPanel);
		this.tabbedPane.add(OmegaDataBrowserPluginPanel.OTHER_ANALYSIS_TABNAME,
		        genericScrollPane);

		this.splitPane = new JSplitPane();
		this.splitPane.setDividerLocation(0.3);
		this.splitPane.setLeftComponent(this.loadedDataPanel);
		this.splitPane.setRightComponent(this.tabbedPane);

		this.add(this.splitPane, BorderLayout.CENTER);
	}

	public void updateTrees() {
		this.loadedDataPanel.updateTree(this.omegaData);
		this.spotDetPanel.updateTree(this.selectedOmeroElement);
		this.spotLinkPanel.updateTree(this.selectedOmeroElement);
		this.trackAdjPanel.updateTree(this.selectedOmeroElement);
		this.trackSegmPanel.updateTree(this.selectedOmeroElement);
		this.trackingMeasuresPanel.updateTree(this.selectedOmeroElement);
		this.snrPanel.updateTree(this.selectedOmeroElement);
	}

	protected void fireDataChangedEvent() {
		this.getPlugin().fireEvent(
		        new OmegaPluginEventDataChanged(this.getPlugin()));
	}

	private void addListeners() {
		this.tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				OmegaDataBrowserPluginPanel.this.handleTabChanged();
			}
		});
	}

	private void handleTabChanged() {
		final int selectedIndex = this.tabbedPane.getSelectedIndex();
		final String title = this.tabbedPane.getTitleAt(selectedIndex);
		if (title.equals(OmegaDataBrowserPluginPanel.TRACKING_ANALYSIS_TABNAME)) {
			this.trackingAnalysisPanel.remove(this.spotDetPanel);
			this.genericAnalysisPanel.remove(this.spotDetPanel);
			this.trackingAnalysisPanel.add(this.spotDetPanel, 0);
		} else if (title
		        .equals(OmegaDataBrowserPluginPanel.OTHER_ANALYSIS_TABNAME)) {
			this.trackingAnalysisPanel.remove(this.spotDetPanel);
			this.genericAnalysisPanel.remove(this.spotDetPanel);
			this.genericAnalysisPanel.add(this.spotDetPanel, 0);
		}
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.loadedDataPanel.updateParentContainer(this.getParentContainer());
	}

	public void setSelectedAnalysisContainer(
	        final OmegaAnalysisRunContainer analysisRunContainer) {
		this.selectedOmeroElement = analysisRunContainer;
		this.spotDetPanel.updateTree(this.selectedOmeroElement);
		// this.analysisPanels.get(0).updateTree(this.selectedOmeroElement);
		this.setSelectedSubAnalysisContainer(this.selectedOmeroElement);
		this.repaint();
	}

	public void setSelectedSubAnalysisContainer(
	        final OmegaAnalysisRunContainer analysisRunContainer) {
		if (analysisRunContainer instanceof OmegaSNRRun) {
			this.selectedSNRRun = analysisRunContainer;
		} else if (analysisRunContainer instanceof OmegaTrackingMeasuresRun) {
			this.selectedTrackingMeasuresRun = analysisRunContainer;
		} else if (analysisRunContainer instanceof OmegaTrajectoriesSegmentationRun) {
			this.selectedTrajSegmentationRun = analysisRunContainer;
			this.trackingMeasuresPanel
			        .updateTree(this.selectedTrajSegmentationRun);
		} else if (analysisRunContainer instanceof OmegaTrajectoriesRelinkingRun) {
			this.selectedTrajRelinkingRun = analysisRunContainer;
			this.selectedTrajSegmentationRun = null;
			this.trackSegmPanel.updateTree(this.selectedTrajRelinkingRun);
		} else if (analysisRunContainer instanceof OmegaParticleLinkingRun) {
			this.selectedLinkingRun = analysisRunContainer;
			this.selectedTrajRelinkingRun = null;
			this.selectedTrajSegmentationRun = null;
			this.selectedTrackingMeasuresRun = null;
			this.trackAdjPanel.updateTree(this.selectedLinkingRun);
		} else if (analysisRunContainer instanceof OmegaParticleDetectionRun) {
			this.selectedDetectionRun = analysisRunContainer;
			this.selectedLinkingRun = null;
			this.selectedTrajRelinkingRun = null;
			this.selectedTrajSegmentationRun = null;
			this.selectedTrackingMeasuresRun = null;
			this.spotLinkPanel.updateTree(this.selectedDetectionRun);
			this.snrPanel.updateTree(this.selectedDetectionRun);
		} else {
			this.selectedDetectionRun = null;
			this.selectedLinkingRun = null;
			this.selectedTrajRelinkingRun = null;
			this.selectedTrajSegmentationRun = null;
			this.selectedTrackingMeasuresRun = null;
			this.spotLinkPanel.updateTree(this.selectedDetectionRun);
			this.snrPanel.updateTree(this.selectedDetectionRun);
		}
		// this.analysisPanels.get(4).updateTree(this.selectedTrajSegRun);
		this.repaint();
	}

	@Override
	public void onCloseOperation() {

	}
}
