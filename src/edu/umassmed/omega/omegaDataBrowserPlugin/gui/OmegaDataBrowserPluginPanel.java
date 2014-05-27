package edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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

public class OmegaDataBrowserPluginPanel extends GenericPluginPanel {

	private static final long serialVersionUID = 4804154980131328463L;

	private JMenu visualizationMenu;
	private JMenuItem refreshMItem;

	private OmegaDataBrowserLoadedDataBrowserPanel loadedDataPanel;
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

		// TODO change classes based on thingy
		final OmegaDataBrowserAnalysisBrowserPanel spotDetectionPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		final OmegaDataBrowserAnalysisBrowserPanel trackingPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		final OmegaDataBrowserAnalysisBrowserPanel segmentationPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		final OmegaDataBrowserAnalysisBrowserPanel motionAnalysisPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);

		trackingBasedAnalysisPanel.add(spotDetectionPanel);
		trackingBasedAnalysisPanel.add(trackingPanel);
		trackingBasedAnalysisPanel.add(segmentationPanel);
		trackingBasedAnalysisPanel.add(motionAnalysisPanel);

		final GenericPanel genericAnalysisPanel = new GenericPanel(
		        this.getParentContainer());
		genericAnalysisPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		final OmegaDataBrowserAnalysisBrowserPanel snrEstimationPanel = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);
		final OmegaDataBrowserAnalysisBrowserPanel statisticalAnalysis = new OmegaDataBrowserAnalysisBrowserPanel(
		        this.getParentContainer(), this, OmegaAnalysisRun.class,
		        this.selectedAnalysisContainer, this.loadedAnalysisRuns);

		genericAnalysisPanel.add(snrEstimationPanel);
		genericAnalysisPanel.add(statisticalAnalysis);

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

	public void update() {
		this.loadedDataPanel.update(this.omegaData);
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
	}

	@Override
	public void onCloseOperation() {

	}

}
