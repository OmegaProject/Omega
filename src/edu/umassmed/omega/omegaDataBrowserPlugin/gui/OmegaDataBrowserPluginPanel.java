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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;

import edu.umassmed.omega.commons.eventSystem.OmegaDataChangedEvent;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaTrajectoriesManagerRun;

public class OmegaDataBrowserPluginPanel extends GenericPluginPanel {

	private static final long serialVersionUID = 4804154980131328463L;

	// private JMenu visualizationMenu;
	// private JMenuItem refreshMItem;

	private OmegaDataBrowserLoadedDataBrowserPanel loadedDataPanel;
	private List<OmegaDataBrowserAnalysisBrowserPanel> analysisPanels;
	private JSplitPane splitPane;

	private final OmegaData omegaData;
	private final OmegaLoadedData loadedData;
	private final List<OmegaAnalysisRun> loadedAnalysisRuns;

	private OmegaAnalysisRunContainer selectedOmeroElement,
	        selectedDetectionRun, selectedLinkingRun;

	private OmegaAnalysisRunContainer selectedTrajManagerRun;

	private OmegaAnalysisRunContainer selectedTrajSegRun;

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
		this.selectedTrajManagerRun = null;
		this.selectedTrajSegRun = null;

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

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP,
		        JTabbedPane.SCROLL_TAB_LAYOUT);

		final GenericPanel trackingBasedAnalysisPanel = new GenericPanel(
		        this.getParentContainer());
		trackingBasedAnalysisPanel.setLayout(new GridLayout(1, 5));

		this.analysisPanels = new ArrayList<OmegaDataBrowserAnalysisBrowserPanel>();

		// TODO change classes based on thingy ???
		final OmegaDataBrowserAnalysisBrowserPanel spotDetectionPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this,
		        OmegaParticleDetectionRun.class, this.selectedOmeroElement,
		        this.loadedAnalysisRuns);
		this.analysisPanels.add(spotDetectionPanel);
		final OmegaDataBrowserAnalysisBrowserPanel trackingPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaParticleLinkingRun.class,
		        this.selectedDetectionRun, this.loadedAnalysisRuns);
		this.analysisPanels.add(trackingPanel);
		final OmegaDataBrowserAnalysisBrowserPanel trajectoriesManagerPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this,
		        OmegaTrajectoriesManagerRun.class, this.selectedLinkingRun,
		        this.loadedAnalysisRuns);
		this.analysisPanels.add(trajectoriesManagerPanel);
		final OmegaDataBrowserAnalysisBrowserPanel trajectoriesSegmentationPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedTrajManagerRun, this.loadedAnalysisRuns);
		this.analysisPanels.add(trajectoriesSegmentationPanel);
		final OmegaDataBrowserAnalysisBrowserPanel motionAnalysisPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedTrajSegRun, this.loadedAnalysisRuns);
		this.analysisPanels.add(motionAnalysisPanel);

		trackingBasedAnalysisPanel.add(spotDetectionPanel);
		trackingBasedAnalysisPanel.add(trackingPanel);
		trackingBasedAnalysisPanel.add(trajectoriesManagerPanel);
		trackingBasedAnalysisPanel.add(trajectoriesSegmentationPanel);
		trackingBasedAnalysisPanel.add(motionAnalysisPanel);

		final GenericPanel genericAnalysisPanel = new GenericPanel(
		        this.getParentContainer());
		genericAnalysisPanel.setLayout(new GridLayout(1, 2));

		final OmegaDataBrowserAnalysisBrowserPanel snrEstimationPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedOmeroElement, this.loadedAnalysisRuns);
		this.analysisPanels.add(snrEstimationPanel);
		final OmegaDataBrowserAnalysisBrowserPanel statisticalAnalysis = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedOmeroElement, this.loadedAnalysisRuns);
		this.analysisPanels.add(statisticalAnalysis);

		genericAnalysisPanel.add(snrEstimationPanel);
		genericAnalysisPanel.add(statisticalAnalysis);

		final JScrollPane trackigScrollPane = new JScrollPane(
		        trackingBasedAnalysisPanel);
		tabbedPane.add("Tracking analysis", trackigScrollPane);
		final JScrollPane genericScrollPane = new JScrollPane(
		        genericAnalysisPanel);
		tabbedPane.add("Other analysis", genericScrollPane);

		this.splitPane = new JSplitPane();
		this.splitPane.setDividerLocation(0.3);
		this.splitPane.setLeftComponent(this.loadedDataPanel);
		this.splitPane.setRightComponent(tabbedPane);

		this.add(this.splitPane, BorderLayout.CENTER);
	}

	public void updateTrees() {
		this.loadedDataPanel.updateTree(this.omegaData);
		for (final OmegaDataBrowserAnalysisBrowserPanel analysisPanel : this.analysisPanels) {
			analysisPanel.updateTree(this.selectedOmeroElement);
		}
	}

	protected void fireDataChangedEvent() {
		this.getPlugin().fireEvent(new OmegaDataChangedEvent(this.getPlugin()));
	}

	private void addListeners() {

	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.loadedDataPanel.updateParentContainer(this.getParentContainer());
	}

	public void setSelectedAnalysisContainer(
	        final OmegaAnalysisRunContainer analysisRunContainer) {
		this.selectedOmeroElement = analysisRunContainer;
		this.analysisPanels.get(0).updateTree(this.selectedOmeroElement);
		this.setSelectedSubAnalysisContainer(this.selectedOmeroElement);
		this.repaint();
	}

	public void setSelectedSubAnalysisContainer(
	        final OmegaAnalysisRunContainer analysisRunContainer) {
		int start = 0;
		if (analysisRunContainer instanceof OmegaTrajectoriesManagerRun) {
			this.selectedTrajManagerRun = analysisRunContainer;
			start = 3;
			this.selectedTrajSegRun = null;
		} else if (analysisRunContainer instanceof OmegaParticleLinkingRun) {
			this.selectedLinkingRun = analysisRunContainer;
			start = 2;
			this.selectedTrajManagerRun = null;
			this.selectedTrajSegRun = null;
		} else if (analysisRunContainer instanceof OmegaParticleDetectionRun) {
			this.selectedDetectionRun = analysisRunContainer;
			start = 1;
			this.selectedLinkingRun = null;
			this.selectedTrajManagerRun = null;
			this.selectedTrajSegRun = null;
		} else {
			start = 1;
			this.selectedDetectionRun = null;
			this.selectedLinkingRun = null;
			this.selectedTrajManagerRun = null;
			this.selectedTrajSegRun = null;
		}

		switch (start) {
		case 1:
			this.analysisPanels.get(1).updateTree(this.selectedDetectionRun);
		case 2:
			this.analysisPanels.get(2).updateTree(this.selectedLinkingRun);
		case 3:
			this.analysisPanels.get(3).updateTree(this.selectedTrajManagerRun);
		case 4:
			this.analysisPanels.get(4).updateTree(this.selectedTrajSegRun);
		default:
		}
		// this.analysisPanels.get(4).updateTree(this.selectedTrajSegRun);
		this.repaint();
	}

	@Override
	public void onCloseOperation() {

	}
}
