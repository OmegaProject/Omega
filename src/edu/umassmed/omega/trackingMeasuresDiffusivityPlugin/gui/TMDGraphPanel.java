package edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.gui;

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

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.StatsConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresDiffusivityRun;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.runnable.StatsGraphProducer;
import edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.runnable.TMDGraphProducer;

public class TMDGraphPanel extends GenericPanel {
	private static final long serialVersionUID = 1124434645792957106L;

	public static final int OPTION_TRACK_SLOPE_LOG_MSD = 0;
	public static final int OPTION_TRACK_SLOPE_MSS = 1;
	public static final int OPTION_TRACK_D = 2;
	public static final int OPTION_TRACK_ERROR_D = 3;
	public static final int OPTION_TRACK_ERROR_SMSS = 4;

	private final TMDPluginPanel pluginPanel;

	private JPanel centerPanel;
	private GenericComboBox<String> xAxis_cmb, yAxis_cmb, graphType_cmb;
	private JTextField selection_txt;
	private JButton drawGraph_btt;

	private Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap;
	private int maxT;
	private String oldXAxisSelection, oldYAxisSelection;

	private final String oldGraphTypeSelection;

	private JPanel graphPanel;
	private final Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap;

	private OmegaTrackingMeasuresDiffusivityRun selectedTrackingMeasuresRun;
	private OmegaSegmentationTypes segmTypes;

	private Thread t;
	private TMDGraphProducer graphProducer;

	public TMDGraphPanel(final RootPaneContainer parent,
	        final TMDPluginPanel pluginPanel,
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

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout());
		leftPanel.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.setSize(OmegaConstants.BUTTON_SIZE_LARGE);

		final JLabel yAxis_lbl = new JLabel(StatsConstants.GRAPH_Y_LBL);
		yAxis_lbl
		        .setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		yAxis_lbl.setSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(yAxis_lbl);
		this.yAxis_cmb = new GenericComboBox<>(this.getParentContainer());
		this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_MSD);
		this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_MSS);
		this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_DIFF);
		this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_UNCERT_SMSS);
		this.yAxis_cmb.addItem(StatsConstants.GRAPH_NAME_UNCERT_D);
		this.yAxis_cmb.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.yAxis_cmb.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.yAxis_cmb);

		final JLabel xAxis_lbl = new JLabel(StatsConstants.GRAPH_X_LBL);
		xAxis_lbl
		        .setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		xAxis_lbl.setSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(xAxis_lbl);
		this.xAxis_cmb = new GenericComboBox<>(this.getParentContainer());
		this.xAxis_cmb.addItem(StatsConstants.GRAPH_LAB_X_TRACK);
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
		leftPanel.add(this.drawGraph_btt);

		this.add(leftPanel, BorderLayout.WEST);

		this.centerPanel = new JPanel();
		this.centerPanel.setLayout(new FlowLayout());

		this.add(this.centerPanel, BorderLayout.CENTER);

		// this.handleChangeChart();
		// this.handleDrawChart();
	}

	private void addListeners() {
		this.xAxis_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMDGraphPanel.this.handleChangeAxis();
			}
		});
		this.yAxis_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMDGraphPanel.this.handleChangeAxis();
			}
		});
		this.graphType_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMDGraphPanel.this.handleChangeAxis();
			}
		});
		this.drawGraph_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMDGraphPanel.this.handleDrawChart();
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				TMDGraphPanel.this.handleComponentResized();
			}
		});
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

	private void handleDrawChart() {
		if (this.centerPanel.getComponentCount() > 0) {
			this.centerPanel.remove(this.graphPanel);
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
		if (xAxisSelection.equals(StatsConstants.GRAPH_LAB_X_TRACK)) {
			this.handleDrawTracksChart();
		} else {
			// this.handleDrawTimepointsChart();
		}
	}

	private void handleDrawTimepointsChart() {
		// final String yAxisSelection = (String)
		// this.yAxis_cmb.getSelectedItem();
	}

	private void handleDrawTracksChart() {
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_MSD)) {
			this.handleTracksChart(TMDGraphPanel.OPTION_TRACK_SLOPE_LOG_MSD);
		} else if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_MSS)) {
			this.handleTracksChart(TMDGraphPanel.OPTION_TRACK_SLOPE_MSS);
		} else if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_DIFF)) {
			this.handleTracksChart(TMDGraphPanel.OPTION_TRACK_D);
		} else if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_UNCERT_SMSS)) {
			this.handleTracksChart(TMDGraphPanel.OPTION_TRACK_ERROR_SMSS);
		} else if (yAxisSelection.equals(StatsConstants.GRAPH_NAME_UNCERT_D)) {
			this.handleTracksChart(TMDGraphPanel.OPTION_TRACK_ERROR_D);
		}
	}

	private void handleChangeAxis() {
		this.drawGraph_btt.setEnabled(false);
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		final String xAxisSelection = (String) this.xAxis_cmb.getSelectedItem();
		if (((this.oldYAxisSelection != null) && this.oldYAxisSelection
		        .equals(yAxisSelection))
		        && ((this.oldXAxisSelection != null) && this.oldXAxisSelection
		                .equals(xAxisSelection)))
			return;
		if (xAxisSelection.equals(StatsConstants.GRAPH_LAB_X_TRACK)) {
			//
		} else {
			//
		}
		this.drawGraph_btt.setEnabled(true);
	}

	private void handleTracksChart(final int diffusivityOption) {
		this.pluginPanel.updateStatus("Preparing tracks graph");
		Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap = null;
		if (this.selectedSegmentsMap.isEmpty()) {
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
		final TMDGraphProducer graphProducer = new TMDGraphProducer(this,
				graphType, diffusivityOption, this.maxT, selectedSegmentsMap,
				this.segmTypes,
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

	private void handleTimepointsChart(final int diffusivityOption) {
		this.pluginPanel.updateStatus("Preparing timepoints graph");
		Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap = null;
		if (this.selectedSegmentsMap.isEmpty()) {
			selectedSegmentsMap = this.segmentsMap;
		} else {
			selectedSegmentsMap = new LinkedHashMap<>();
			for (final OmegaTrajectory track : this.selectedSegmentsMap
					.keySet()) {
				selectedSegmentsMap.put(track, this.segmentsMap.get(track));
			}
		}
		int graphType = StatsGraphProducer.LINE_GRAPH;
		if (this.graphType_cmb.getSelectedItem().equals(
				StatsConstants.GRAPH_TYPE_BAR)) {
			graphType = StatsGraphProducer.BAR_GRAPH;
		} else if (this.graphType_cmb.getSelectedItem().equals(
				StatsConstants.GRAPH_TYPE_HIST)) {
			graphType = StatsGraphProducer.HISTOGRAM_GRAPH;
		}
		final TMDGraphProducer graphProducer = new TMDGraphProducer(this,
				graphType, diffusivityOption, this.maxT, selectedSegmentsMap,
				this.segmTypes,
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

	private void launchGraphProducerThread(final TMDGraphProducer graphProducer) {
		if ((this.t != null) && this.t.isAlive()) {
			this.graphProducer.terminate();
		}
		this.t = new Thread(graphProducer);
		this.graphProducer = graphProducer;
		this.t.setName("DiffusivityGraphProducer");
		this.t.start();
	}

	public void setMaximumT(final int maxT) {
		this.maxT = maxT;
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

	public void setSelectedSegments(
	        final Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap) {
		this.selectedSegmentsMap.clear();
		this.selectedSegmentsMap.putAll(selectedSegmentsMap);
		// this.handleChangeChart();
		this.handleDrawChart();
	}

	public void updateSelectedTrackingMeasuresRun(
	        final OmegaTrackingMeasuresDiffusivityRun trackingMeasuresRun) {
		this.selectedTrackingMeasuresRun = trackingMeasuresRun;
	}

	public void updateSelectedSegmentationTypes(
	        final OmegaSegmentationTypes segmentationTypes) {
		this.segmTypes = segmentationTypes;
	}

	public void updateStatus(final double completed, final boolean ended,
	        final JPanel graphPanel) {
		if (ended) {
			this.graphPanel = graphPanel;
			this.handleComponentResized();
			// this.graphPanel.updateParentContainer(this.getParentContainer());
			this.pluginPanel.updateStatus("Plugin ready");
			// this.add(this.graphPanel, BorderLayout.CENTER);
			this.centerPanel.add(this.graphPanel);
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
