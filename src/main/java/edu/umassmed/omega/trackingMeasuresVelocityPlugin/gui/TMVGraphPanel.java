package edu.umassmed.omega.trackingMeasuresVelocityPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.StatsConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresVelocityRun;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.runnable.StatsGraphProducer;
import edu.umassmed.omega.trackingMeasuresVelocityPlugin.runnable.TMVGraphProducer;

public class TMVGraphPanel extends GenericPanel {
	private static final long serialVersionUID = 1124434645792957106L;

	public static final int OPTION_LOCAL_SPEED = 0;
	public static final int OPTION_LOCAL_VELOCITY = 1;
	public static final int OPTION_MEAN_SPEED = 2;
	public static final int OPTION_MEAN_VELOCITY = 3;
	public static final int OPTION_LOCAL_SPEED_P2P = 4;
	public static final int OPTION_FORPROLIN = 5;

	private final TMVPluginPanel pluginPanel;

	private JPanel centerPanel;
	private GenericComboBox<String> xAxis_cmb, yAxis_cmb, graphType_cmb,
			globalOrLocal_cmb;
	private JTextField selection_txt;
	private JButton drawGraph_btt;

	private Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap;
	private int maxT;
	private String oldXAxisSelection, oldYAxisSelection;

	private final String oldGraphTypeSelection;

	private JPanel graphPanel, legendPanel;
	private final Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap;

	private OmegaTrackingMeasuresVelocityRun selectedTrackingMeasuresRun;
	private OmegaSegmentationTypes segmTypes;
	private final Thread t;
	private TMVGraphProducer graphProducer;

	private boolean handlingEvent;

	public TMVGraphPanel(final RootPaneContainer parent,
			final TMVPluginPanel pluginPanel,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		super(parent);

		this.pluginPanel = pluginPanel;

		this.segmentsMap = segmentsMap;
		this.maxT = 0;
		this.oldXAxisSelection = null;
		this.oldYAxisSelection = null;
		this.oldGraphTypeSelection = null;

		this.selectedTrackingMeasuresRun = null;
		this.segmTypes = null;

		this.selectedSegmentsMap = new LinkedHashMap<>();
		this.t = null;

		this.handlingEvent = false;

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout());
		leftPanel.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		
		final JLabel globalOrLocal_lbl = new JLabel(
				StatsConstants.GRAPH_RESULTSTYPE_LBL);
		globalOrLocal_lbl
				.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		globalOrLocal_lbl
				.setSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(globalOrLocal_lbl);
		this.globalOrLocal_cmb = new GenericComboBox<>(
				this.getParentContainer());
		this.globalOrLocal_cmb.addItem(StatsConstants.TAB_RESULTS_LOCAL);
		this.globalOrLocal_cmb.addItem(StatsConstants.TAB_RESULTS_GLOBAL);
		this.globalOrLocal_cmb
				.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.globalOrLocal_cmb.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.globalOrLocal_cmb);

		final JLabel yAxis_lbl = new JLabel(StatsConstants.GRAPH_Y_LBL);
		yAxis_lbl
				.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		yAxis_lbl.setSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(yAxis_lbl);
		this.yAxis_cmb = new GenericComboBox<>(this.getParentContainer());
		this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_SPEED_P2P_LOC);
		this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_SPEED_LOC);
		this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_VEL_LOC);
		this.yAxis_cmb.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.yAxis_cmb.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.yAxis_cmb);

		final JLabel xAxis_lbl = new JLabel(StatsConstants.GRAPH_X_LBL);
		xAxis_lbl
				.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		xAxis_lbl.setSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(xAxis_lbl);
		this.xAxis_cmb = new GenericComboBox<>(this.getParentContainer());
		this.xAxis_cmb.addItem(StatsConstants.GRAPH_LAB_X_TPT);
		// this.xAxis_cmb.addItem("Segments");
		this.xAxis_cmb.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.xAxis_cmb.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.xAxis_cmb);

		final JLabel selection_lbl = new JLabel(StatsConstants.GRAPH_VAL_RANGE);
		selection_lbl.setToolTipText(StatsConstants.GRAPH_VAL_RANGE_TT);
		selection_lbl.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		selection_lbl.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(selection_lbl);
		this.selection_txt = new JTextField();
		this.selection_txt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.selection_txt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.selection_txt);

		final JLabel graphType_lbl = new JLabel(StatsConstants.GRAPH_TYPE);
		graphType_lbl.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		graphType_lbl.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(graphType_lbl);
		this.graphType_cmb = new GenericComboBox<>(this.getParentContainer());
		this.graphType_cmb.addItem(StatsConstants.GRAPH_TYPE_LINE);
		this.graphType_cmb.addItem(StatsConstants.GRAPH_TYPE_BAR);
		this.graphType_cmb.addItem(StatsConstants.GRAPH_TYPE_HIST);
		// this.xAxis_cmb.addItem("Segments");
		this.graphType_cmb.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.graphType_cmb.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.graphType_cmb);

		this.drawGraph_btt = new JButton(StatsConstants.GRAPH_DRAW);
		this.drawGraph_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.drawGraph_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		// leftPanel.add(this.drawGraph_btt);

		this.add(leftPanel, BorderLayout.WEST);

		this.centerPanel = new JPanel();
		this.centerPanel.setLayout(new BorderLayout());

		this.add(this.centerPanel, BorderLayout.CENTER);

		// this.handleChangeChart();
		// this.handleDrawChart();
	}

	private void addListeners() {
		this.globalOrLocal_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMVGraphPanel.this.handleChangeResultsType();
			}
		});
		this.xAxis_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMVGraphPanel.this.handleChangeAxis();
			}
		});
		this.yAxis_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMVGraphPanel.this.handleChangeAxis();
			}
		});
		this.graphType_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMVGraphPanel.this.handleChangeAxis();
			}
		});
		this.drawGraph_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMVGraphPanel.this.handleDrawChart();
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				TMVGraphPanel.this.handleComponentResized();
			}
		});
	}

	private void handleChangeResultsType() {
		this.handlingEvent = true;
		this.yAxis_cmb.removeAllItems();
		this.xAxis_cmb.removeAllItems();
		if (this.globalOrLocal_cmb.getSelectedItem().equals(
				StatsConstants.TAB_RESULTS_GLOBAL)) {
			this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_SPEED_GLO);
			this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_VEL_GLO);
			this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_FORPROLIN_GLO);
			this.xAxis_cmb.addItem(StatsConstants.GRAPH_LAB_X_TRACK);
		} else {
			this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_SPEED_P2P_LOC);
			this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_SPEED_LOC);
			this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_VEL_LOC);
			this.xAxis_cmb.addItem(StatsConstants.GRAPH_LAB_X_TPT);
		}
		this.handlingEvent = false;
		this.handleDrawChartLater();
	}

	private void handleComponentResized() {
		if (this.graphPanel == null)
			return;
		final int height = this.getHeight() - 20;
		final int width = this.getWidth()
				- OmegaConstants.BUTTON_SIZE_LARGE.width - 20;
		int size = height;
		if (height > width) {
			size = width;
		}
		final Dimension graphDim = new Dimension(size, size);
		this.graphPanel.setSize(graphDim);
		this.graphPanel.setPreferredSize(graphDim);
		this.repaint();
	}

	private void handleDrawChartLater() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				TMVGraphPanel.this.handleDrawChart();
			}
		});
	}

	private void handleDrawChart() {
		if (this.centerPanel.getComponentCount() > 0) {
			this.centerPanel.remove(this.graphPanel);
			this.centerPanel.remove(this.legendPanel);
		}
		this.revalidate();
		this.repaint();
		final String xAxisSelection = (String) this.xAxis_cmb.getSelectedItem();
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		final String graphTypeSelection = (String) this.graphType_cmb
				.getSelectedItem();
		if ((this.segmentsMap == null) || this.segmentsMap.isEmpty()
				|| (xAxisSelection == null) || (yAxisSelection == null)
				|| (graphTypeSelection == null)
				|| (this.selectedTrackingMeasuresRun == null))
			return;
		this.oldYAxisSelection = yAxisSelection;
		this.oldXAxisSelection = xAxisSelection;
		if (xAxisSelection.equals(StatsConstants.GRAPH_LAB_X_TPT)) {
			this.handleDrawTimepointsChart();
		} else {
			this.handleDrawTracksChart();
		}
	}

	private void handleDrawTimepointsChart() {
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_SPEED_LOC)) {
			this.handleTimepointsChart(TMVGraphPanel.OPTION_LOCAL_SPEED);
		} else if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_VEL_LOC)) {
			this.handleTimepointsChart(TMVGraphPanel.OPTION_LOCAL_VELOCITY);
		} else if (yAxisSelection
				.equals(StatsConstants.GRAPH_NAME_SPEED_P2P_LOC)) {
			this.handleTimepointsChart(TMVGraphPanel.OPTION_LOCAL_SPEED_P2P);
		}
	}

	private void handleDrawTracksChart() {
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_SPEED_GLO)) {
			this.handleTracksChart(TMVGraphPanel.OPTION_MEAN_SPEED);
		} else if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_VEL_GLO)) {
			this.handleTracksChart(TMVGraphPanel.OPTION_MEAN_VELOCITY);
		} else if (yAxisSelection
				.equals(StatsConstants.GRAPH_NAME_FORPROLIN_GLO)) {
			this.handleTracksChart(TMVGraphPanel.OPTION_FORPROLIN);
		}
	}

	private void handleChangeAxis() {
		if (this.handlingEvent)
			return;
		this.drawGraph_btt.setEnabled(false);
		final String xAxisSelection = (String) this.xAxis_cmb.getSelectedItem();
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		final String graphTypeSelection = (String) this.graphType_cmb
				.getSelectedItem();
		if (((this.oldYAxisSelection != null) && this.oldYAxisSelection
				.equals(yAxisSelection))
				&& ((this.oldXAxisSelection != null) && this.oldXAxisSelection
						.equals(xAxisSelection))
				&& ((this.oldGraphTypeSelection != null) && this.oldGraphTypeSelection
						.equals(graphTypeSelection)))
			return;
		if (xAxisSelection.equals(StatsConstants.GRAPH_LAB_X_TPT)) {
			if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_SPEED_GLO)
					|| yAxisSelection.equals(StatsConstants.GRAPH_NAME_VEL_GLO)
					|| yAxisSelection
							.equals(StatsConstants.GRAPH_NAME_FORPROLIN_GLO))
				return;
		} else {
			if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_SPEED_LOC)
					|| yAxisSelection.equals(StatsConstants.GRAPH_NAME_VEL_LOC)
					|| yAxisSelection
							.equals(StatsConstants.GRAPH_NAME_SPEED_P2P_LOC))
				return;
		}
		this.handleDrawChartLater();
		this.drawGraph_btt.setEnabled(true);
	}

	private void handleTracksChart(final int velocityOption) {
		this.pluginPanel.updateStatus("Preparing tracks graph");
		Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap = null;
		boolean isSame = false;
		for (final OmegaTrajectory track : this.selectedTrackingMeasuresRun
				.getSegments().keySet())
			if (this.selectedSegmentsMap.containsKey(track)) {
				isSame = true;
			}
		if (this.selectedSegmentsMap.isEmpty() || !isSame) {
			selectedSegmentsMap = this.segmentsMap;
		} else {
			selectedSegmentsMap = this.selectedSegmentsMap;
		}
		int graphType = StatsGraphProducer.LINE_GRAPH;
		if (this.graphType_cmb.getSelectedItem().equals(
				StatsConstants.GRAPH_TYPE_BAR)) {
			graphType = StatsGraphProducer.BAR_GRAPH;
		} else if (this.graphType_cmb.getSelectedItem().equals(
				StatsConstants.GRAPH_TYPE_HIST)) {
			graphType = StatsGraphProducer.HISTOGRAM_GRAPH;
		}
		final TMVGraphProducer graphProducer = new TMVGraphProducer(this,
				graphType, velocityOption, false, this.maxT,
				selectedSegmentsMap, this.segmTypes,
				this.selectedTrackingMeasuresRun.getLocalSpeedResults(),
				this.selectedTrackingMeasuresRun
						.getLocalSpeedFromOriginResults(),
				this.selectedTrackingMeasuresRun
						.getLocalVelocityFromOriginResults(),
				this.selectedTrackingMeasuresRun
						.getAverageCurvilinearSpeedMapResults(),
				this.selectedTrackingMeasuresRun
						.getAverageStraightLineVelocityMapResults(),
				this.selectedTrackingMeasuresRun
						.getForwardProgressionLinearityMapResults());
		this.launchGraphProducerThread(graphProducer);
	}

	private void handleTimepointsChart(final int velocityOption) {
		this.pluginPanel.updateStatus("Preparing timepoints graph");
		Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap = null;
		boolean isSame = false;
		for (final OmegaTrajectory track : this.selectedTrackingMeasuresRun
				.getSegments().keySet())
			if (this.selectedSegmentsMap.containsKey(track)) {
				isSame = true;
			}
		if (this.selectedSegmentsMap.isEmpty() || !isSame) {
			selectedSegmentsMap = this.segmentsMap;
		} else {
			selectedSegmentsMap = this.selectedSegmentsMap;
		}
		int graphType = StatsGraphProducer.LINE_GRAPH;
		if (this.graphType_cmb.getSelectedItem().equals(
				StatsConstants.GRAPH_TYPE_BAR)) {
			graphType = StatsGraphProducer.BAR_GRAPH;
		} else if (this.graphType_cmb.getSelectedItem().equals(
				StatsConstants.GRAPH_TYPE_HIST)) {
			graphType = StatsGraphProducer.HISTOGRAM_GRAPH;
		}
		final TMVGraphProducer graphProducer = new TMVGraphProducer(this,
				graphType, velocityOption, true, this.maxT,
				selectedSegmentsMap, this.segmTypes,
				this.selectedTrackingMeasuresRun.getLocalSpeedResults(),
				this.selectedTrackingMeasuresRun
						.getLocalSpeedFromOriginResults(),
				this.selectedTrackingMeasuresRun
						.getLocalVelocityFromOriginResults(),
				this.selectedTrackingMeasuresRun
						.getAverageCurvilinearSpeedMapResults(),
				this.selectedTrackingMeasuresRun
						.getAverageStraightLineVelocityMapResults(),
				this.selectedTrackingMeasuresRun
						.getForwardProgressionLinearityMapResults());
		this.launchGraphProducerThread(graphProducer);
	}

	private void launchGraphProducerThread(final TMVGraphProducer graphProducer) {
		// if ((this.t != null) && this.t.isAlive()) {
		// this.graphProducer.terminate();
		// }
		// this.t = new Thread(graphProducer);
		this.graphProducer = graphProducer;
		this.graphProducer.doRun();
		// this.t.setName("VelocityGraphProducer");
		// this.t.start();
	}
	
	public void setMaximumT(final int maxT) {
		this.maxT = maxT;
	}

	public void clearSelectedSegments() {
		this.selectedSegmentsMap.clear();
		this.handleDrawChart();
	}
	
	public void setSegmentsMap(
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		this.segmentsMap = segmentsMap;
		// this.handleChangeChart();
		// this.handleDrawChart();
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.xAxis_cmb.updateParentContainer(parent);
		this.yAxis_cmb.updateParentContainer(parent);
	}

	public void clearSegmentsSelection() {
		this.selectedSegmentsMap.clear();
		this.handleDrawChart();
	}

	public void setSelectedSegments(
			final Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap) {
		this.selectedSegmentsMap.clear();
		this.selectedSegmentsMap.putAll(selectedSegmentsMap);
		// this.handleChangeChart();
		this.handleDrawChartLater();
	}

	public void updateSelectedTrackingMeasuresRun(
			final OmegaTrackingMeasuresVelocityRun trackingMeasuresRun) {
		this.selectedTrackingMeasuresRun = trackingMeasuresRun;
		this.handleDrawChartLater();
	}

	public void updateSelectedSegmentationTypes(
			final OmegaSegmentationTypes segmentationTypes) {
		this.segmTypes = segmentationTypes;
	}

	public void updateStatus(final double completed, final boolean ended) {
		if (ended) {
			this.graphPanel = this.graphProducer.getGraphPanel();
			this.legendPanel = this.graphProducer.getGraphLegendPanel();
			this.handleComponentResized();
			// this.graphPanel.updateParentContainer(this.getParentContainer());
			this.pluginPanel.updateStatus("Plugin ready");
			if ((this.graphPanel == null) || (this.legendPanel == null))
				return;
			// this.add(this.graphPanel, BorderLayout.CENTER);
			this.centerPanel.add(this.graphPanel, BorderLayout.CENTER);
			this.centerPanel.add(this.legendPanel, BorderLayout.EAST);
			this.drawGraph_btt.setEnabled(false);
			this.revalidate();
			this.repaint();
		} else {
			final String completedS = new BigDecimal(completed).setScale(2,
					RoundingMode.HALF_UP).toString();
			this.pluginPanel
					.updateStatus("Graph " + completedS + " completed.");
		}
	}
}
