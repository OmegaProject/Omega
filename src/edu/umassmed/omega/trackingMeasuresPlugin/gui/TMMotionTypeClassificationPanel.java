package edu.umassmed.omega.trackingMeasuresPlugin.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import org.jfree.chart.ChartPanel;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrackingMeasuresRun;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.trackingMeasuresPlugin.runnables.TMMotionTypeClassificationGraphProducer;

public class TMMotionTypeClassificationPanel extends GenericPanel {
	private static final long serialVersionUID = 1124434645792957106L;

	public static final int OPTION_LINEAR = 0;
	public static final int OPTION_LOG = 1;

	private final TMPluginPanel pluginPanel;

	private JPanel centerPanel;

	private Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap;
	private OmegaTrajectory track;
	private int maxT, imgWidth, imgHeight;

	private ChartPanel[] chartPanels;

	private OmegaTrackingMeasuresRun selectedTrackingMeasuresRun;
	private Thread t;
	private TMMotionTypeClassificationGraphProducer graphProducer;

	public TMMotionTypeClassificationPanel(final RootPaneContainer parent,
	        final TMPluginPanel pluginPanel,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		super(parent);

		this.pluginPanel = pluginPanel;

		this.segmentsMap = segmentsMap;
		this.maxT = 0;
		this.imgWidth = 0;
		this.imgHeight = 0;

		this.chartPanels = new ChartPanel[4];
		for (int i = 0; i < this.chartPanels.length; i++) {
			this.chartPanels[i] = null;
		}
		this.t = null;

		this.setLayout(new FlowLayout());

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		this.centerPanel = new JPanel();
		this.centerPanel.setLayout(new GridLayout(2, 2));

		this.add(this.centerPanel/* , BorderLayout.CENTER */);
		// this.handleDrawChart();
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				TMMotionTypeClassificationPanel.this.handleComponentResized();
			}
		});
	}

	private void handleComponentResized() {
		if (this.chartPanels[0] == null)
			return;
		int size = this.getHeight() - 20;
		final int width = this.getWidth() - 20;
		if (size > width) {
			size = width;
		}
		size /= 2;
		final Dimension graphDim = new Dimension(size, size);
		for (final ChartPanel chartPanel : this.chartPanels) {
			chartPanel.setSize(graphDim);
			chartPanel.setPreferredSize(graphDim);
		}
	}

	private void handleDrawChart() {
		this.pluginPanel.updateStatus("Preparing graphs");
		for (int i = 0; i < this.chartPanels.length; i++) {
			final ChartPanel chartPanel = this.chartPanels[i];
			if (chartPanel != null) {
				this.centerPanel.remove(chartPanel);
			}
			this.chartPanels[i] = null;
		}
		this.revalidate();
		this.repaint();
		if (this.track == null)
			return;
		this.handleDrawChart(TMMotionTypeClassificationPanel.OPTION_LOG);
	}

	private void handleDrawChart(final int motionTypeOption) {
		this.pluginPanel.updateStatus("Preparing log graph");
		final TMMotionTypeClassificationGraphProducer graphProducer = new TMMotionTypeClassificationGraphProducer(
		        this, motionTypeOption, this.maxT, this.imgWidth,
		        this.imgHeight, this.track, this.segmentsMap.get(this.track),
		        this.selectedTrackingMeasuresRun.getNyResults(),
		        this.selectedTrackingMeasuresRun.getMuResults(),
		        this.selectedTrackingMeasuresRun.getLogMuResults(),
		        this.selectedTrackingMeasuresRun.getDeltaTResults(),
		        this.selectedTrackingMeasuresRun.getLogDeltaTResults(),
		        this.selectedTrackingMeasuresRun.getGammaDResults(),
		        this.selectedTrackingMeasuresRun.getGammaDFromLogResults(),
		        this.selectedTrackingMeasuresRun.getGammaResults(),
		        this.selectedTrackingMeasuresRun.getGammaFromLogResults(),
		        this.selectedTrackingMeasuresRun.getSmssResults(),
		        this.selectedTrackingMeasuresRun.getSmssFromLogResults());
		this.launchGraphProducerThread(graphProducer);
	}

	private void launchGraphProducerThread(
	        final TMMotionTypeClassificationGraphProducer graphProducer) {
		if ((this.t != null) && this.t.isAlive()) {
			this.graphProducer.terminate();
		}
		this.t = new Thread(graphProducer);
		this.graphProducer = graphProducer;
		this.t.setName("MotionTypeGraphProducer");
		this.t.start();
	}

	public void setMaximumT(final int maxT) {
		this.maxT = maxT;
	}

	public void setImageWidth(final int width) {
		this.imgWidth = width;
	}

	public void setImageHeight(final int height) {
		this.imgHeight = height;
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		// Bottom down menu here
	}

	public void setSegmentsMap(
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		this.segmentsMap = segmentsMap;
		this.track = null;
		// this.handleChangeChart();
		// this.handleDrawChart();
	}

	public void setSelectedTrajectories(
	        final List<OmegaTrajectory> selectedTrajectories) {
		this.track = null;
		if (!selectedTrajectories.isEmpty()) {
			this.track = selectedTrajectories.get(0);
		}
		// this.handleChangeXChart();
		this.handleDrawChart();
	}

	public void updateSelectedTrackingMeasuresRun(
	        final OmegaTrackingMeasuresRun trackingMeasuresRun) {
		this.selectedTrackingMeasuresRun = trackingMeasuresRun;
	}

	public void updateStatus(final double completed, final int graph,
	        final ChartPanel[] chartPanels) {
		if (graph == -1) {
			this.chartPanels = chartPanels;
			for (final ChartPanel chartPanel : this.chartPanels) {
				// chartPanel.updateParentContainer(this.getParentContainer());
				this.centerPanel.add(chartPanel);
			}
			this.handleComponentResized();
			this.pluginPanel.updateStatus("Plugin ready");
			this.revalidate();
			this.repaint();
		} else {
			final String completedS = new BigDecimal(completed).setScale(2,
			        RoundingMode.HALF_UP).toString();
			this.pluginPanel.updateStatus("Graph " + graph + " " + completedS
			        + " completed.");
		}
	}
}
