package edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.eventSystem.OmegaDataChangedEvent;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;

public class OmegaDataBrowserPluginPanel extends GenericPluginPanel {

	private static final long serialVersionUID = 4804154980131328463L;

	private JMenu visualizationMenu;
	private JMenuItem refreshMItem;

	private OmegaDataBrowserLoadedDataBrowserPanel loadedDataPanel;
	private List<OmegaDataBrowserAnalysisBrowserPanel> analysisPanels;
	private JSplitPane splitPane;

	private final OmegaData omegaData;
	private final OmegaLoadedData loadedData;
	private final List<OmegaAnalysisRun> loadedAnalysisRuns;

	private OmegaAnalysisRunContainer selectedAnalysisContainer;

	public OmegaDataBrowserPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final OmegaData omegaData,
	        final OmegaLoadedData loadedData,
	        final List<OmegaAnalysisRun> loadedAnalysisRuns, final int index) {
		super(parent, plugin, index);

		this.omegaData = omegaData;
		this.loadedData = loadedData;
		this.loadedAnalysisRuns = loadedAnalysisRuns;
		this.selectedAnalysisContainer = null;

		// this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		this.createMenu();
		this.createAndAddWidgets();
		this.addListeners();
	}

	private void createMenu() {
		final JMenuBar menu = super.getMenu();
		this.visualizationMenu = new JMenu("Visualization");
		this.refreshMItem = new JMenuItem("Refresh data");
		this.visualizationMenu.add(this.refreshMItem);

		menu.add(this.visualizationMenu);
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
		trackingBasedAnalysisPanel
		        .setLayout(new FlowLayout(FlowLayout.LEADING));

		this.analysisPanels = new ArrayList<OmegaDataBrowserAnalysisBrowserPanel>();

		// TODO change classes based on thingy
		final OmegaDataBrowserAnalysisBrowserPanel spotDetectionPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this,
		        OmegaParticleDetectionRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		this.analysisPanels.add(spotDetectionPanel);
		final OmegaDataBrowserAnalysisBrowserPanel trackingPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaParticleLinkingRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		this.analysisPanels.add(trackingPanel);
		final OmegaDataBrowserAnalysisBrowserPanel segmentationPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		this.analysisPanels.add(segmentationPanel);
		final OmegaDataBrowserAnalysisBrowserPanel motionAnalysisPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		this.analysisPanels.add(motionAnalysisPanel);

		trackingBasedAnalysisPanel.add(new JScrollPane(spotDetectionPanel));
		trackingBasedAnalysisPanel.add(new JScrollPane(trackingPanel));
		trackingBasedAnalysisPanel.add(new JScrollPane(segmentationPanel));
		trackingBasedAnalysisPanel.add(new JScrollPane(motionAnalysisPanel));

		final GenericPanel genericAnalysisPanel = new GenericPanel(
		        this.getParentContainer());
		genericAnalysisPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final OmegaDataBrowserAnalysisBrowserPanel snrEstimationPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		this.analysisPanels.add(snrEstimationPanel);
		final OmegaDataBrowserAnalysisBrowserPanel statisticalAnalysis = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		this.analysisPanels.add(statisticalAnalysis);

		genericAnalysisPanel.add(new JScrollPane(snrEstimationPanel));
		genericAnalysisPanel.add(new JScrollPane(statisticalAnalysis));

		final JScrollPane trackigScrollPane = new JScrollPane(
		        trackingBasedAnalysisPanel);
		tabbedPane.add("Tracking based analysis", trackigScrollPane);
		final JScrollPane genericScrollPane = new JScrollPane(
		        genericAnalysisPanel);
		tabbedPane.add("Generic analysis", genericScrollPane);

		this.splitPane = new JSplitPane();
		this.splitPane.setDividerLocation(0.3);
		this.splitPane.setLeftComponent(this.loadedDataPanel);
		this.splitPane.setRightComponent(tabbedPane);

		this.add(this.splitPane, BorderLayout.CENTER);
	}

	public void updateTrees() {
		this.loadedDataPanel.updateTree(this.omegaData);
		for (final OmegaDataBrowserAnalysisBrowserPanel analysisPanel : this.analysisPanels) {
			analysisPanel.updateTree(this.selectedAnalysisContainer);
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
		this.selectedAnalysisContainer = analysisRunContainer;
		for (final OmegaDataBrowserAnalysisBrowserPanel analysisPanel : this.analysisPanels) {
			analysisPanel.updateTree(this.selectedAnalysisContainer);
		}
		this.repaint();
	}

	@Override
	public void onCloseOperation() {

	}

}
