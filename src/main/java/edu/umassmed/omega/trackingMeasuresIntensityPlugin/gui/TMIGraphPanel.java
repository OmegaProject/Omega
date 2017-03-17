package edu.umassmed.omega.trackingMeasuresIntensityPlugin.gui;

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
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresIntensityRun;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.runnable.StatsGraphProducer;
import edu.umassmed.omega.trackingMeasuresIntensityPlugin.TMIConstants;
import edu.umassmed.omega.trackingMeasuresIntensityPlugin.runnable.TMIGraphProducer;

public class TMIGraphPanel extends GenericPanel {
	private static final long serialVersionUID = 1124434645792957106L;

	public static final int OPTION_PEAK_SIGNAL = 0;
	public static final int OPTION_CENTROID_SIGNAL = 1;
	public static final int OPTION_MEAN_SIGNAL = 2;
	public static final int OPTION_NOISE = 3;
	public static final int OPTION_AREA = 4;
	public static final int OPTION_SNR = 5;

	public static final int OPTION_MIN = 0;
	public static final int OPTION_MEAN = 1;
	public static final int OPTION_MAX = 2;

	private final TMIPluginPanel pluginPanel;

	private JPanel centerPanel;
	private GenericComboBox<String> xAxis_cmb, yAxis_cmb, graphType_cmb;
	private JTextField selection_txt;
	private JButton drawGraph_btt;

	private Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap;
	private int maxT;
	private String oldXAxisSelection, oldYAxisSelection, oldGraphTypeSelection;

	private JPanel graphPanel, legendPanel;
	private final Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap;

	private OmegaTrackingMeasuresIntensityRun selectedTrackingMeasuresRun;
	private OmegaSegmentationTypes segmTypes;
	private Thread t;
	private TMIGraphProducer graphProducer;

	public TMIGraphPanel(final RootPaneContainer parent,
	        final TMIPluginPanel pluginPanel,
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
		// this.yAxis_cmb.addItem(TMIConstants.GRAPH_NAME_INT_PEAK);
		// this.yAxis_cmb.addItem(TMIConstants.GRAPH_NAME_INT_CENT);
		this.yAxis_cmb.addItem(TMIConstants.MAX_PEAK_INTENSITY);
		this.yAxis_cmb.addItem(TMIConstants.AVG_PEAK_INTENSITY);
		this.yAxis_cmb.addItem(TMIConstants.MIN_PEAK_INTENSITY);
		this.yAxis_cmb.addItem(TMIConstants.MAX_CENTROID_INTENSITY);
		this.yAxis_cmb.addItem(TMIConstants.AVG_CENTROID_INTENSITY);
		this.yAxis_cmb.addItem(TMIConstants.MIN_CENTROID_INTENSITY);
		this.yAxis_cmb.addItem(TMIConstants.MAX_MEAN_INTENSITY);
		this.yAxis_cmb.addItem(TMIConstants.AVG_MEAN_INTENSITY);
		this.yAxis_cmb.addItem(TMIConstants.MIN_MEAN_INTENSITY);
		this.yAxis_cmb.addItem(TMIConstants.MAX_NOISE);
		this.yAxis_cmb.addItem(TMIConstants.AVG_NOISE);
		this.yAxis_cmb.addItem(TMIConstants.MIN_NOISE);
		this.yAxis_cmb.addItem(TMIConstants.MAX_SNR);
		this.yAxis_cmb.addItem(TMIConstants.AVG_SNR);
		this.yAxis_cmb.addItem(TMIConstants.MIN_SNR);
		this.yAxis_cmb.addItem(TMIConstants.MAX_AREA);
		this.yAxis_cmb.addItem(TMIConstants.AVG_AREA);
		this.yAxis_cmb.addItem(TMIConstants.MIN_AREA);
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
		// leftPanel.add(this.drawGraph_btt);

		this.add(leftPanel, BorderLayout.WEST);

		this.centerPanel = new JPanel();
		this.centerPanel.setLayout(new BorderLayout());

		this.add(this.centerPanel, BorderLayout.CENTER);

		// this.handleChangeChart();
		// this.handleDrawChart();
	}

	private void addListeners() {
		this.xAxis_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMIGraphPanel.this.handleChangeAxis();
			}
		});
		this.yAxis_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				TMIGraphPanel.this.handleChangeAxis();
			}
		});
		this.graphType_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMIGraphPanel.this.handleChangeAxis();
			}
		});
		this.drawGraph_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMIGraphPanel.this.handleDrawChart();
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				TMIGraphPanel.this.handleComponentResized();
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
	
	private void handleDrawChartLater() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				TMIGraphPanel.this.handleDrawChart();
			}
		});
	}

	private void handleDrawChart() {
		if (this.selectedTrackingMeasuresRun == null)
			return;
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
		this.oldGraphTypeSelection = graphTypeSelection;
		if (xAxisSelection.equals(StatsConstants.GRAPH_LAB_X_TPT)) {
			this.handleDrawTimepointsChart();
		} else {
			this.handleDrawTracksChart();
		}
	}

	private void handleDrawTimepointsChart() {
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		if (yAxisSelection.equals(TMIConstants.MIN_PEAK_INTENSITY)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_PEAK_SIGNAL,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_PEAK_INTENSITY)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_PEAK_SIGNAL,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_PEAK_INTENSITY)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_PEAK_SIGNAL,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_CENTROID_INTENSITY)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_CENTROID_INTENSITY)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_CENTROID_INTENSITY)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_MEAN_INTENSITY)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_MEAN_SIGNAL,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_MEAN_INTENSITY)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_MEAN_SIGNAL,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_MEAN_INTENSITY)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_MEAN_SIGNAL,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_NOISE)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_NOISE,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_NOISE)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_NOISE,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_NOISE)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_NOISE,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_SNR)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_SNR,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_SNR)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_SNR,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_SNR)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_SNR,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_AREA)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_AREA,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_AREA)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_AREA,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_AREA)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_AREA,
			        TMIGraphPanel.OPTION_MAX);
		}
	}

	private void handleDrawTracksChart() {
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		if (yAxisSelection.equals(TMIConstants.MIN_PEAK_INTENSITY)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_PEAK_SIGNAL,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_PEAK_INTENSITY)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_PEAK_SIGNAL,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_PEAK_INTENSITY)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_PEAK_SIGNAL,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_CENTROID_INTENSITY)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_CENTROID_INTENSITY)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_CENTROID_INTENSITY)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_MEAN_INTENSITY)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_MEAN_SIGNAL,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_MEAN_INTENSITY)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_MEAN_SIGNAL,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_MEAN_INTENSITY)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_MEAN_SIGNAL,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_NOISE)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_NOISE,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_NOISE)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_NOISE,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_NOISE)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_NOISE,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_SNR)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_SNR,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_SNR)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_SNR,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_SNR)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_SNR,
			        TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(TMIConstants.MIN_AREA)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_AREA,
			        TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(TMIConstants.AVG_AREA)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_AREA,
			        TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(TMIConstants.MAX_AREA)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_AREA,
			        TMIGraphPanel.OPTION_MAX);
		}
	}

	private void handleChangeAxis() {
		this.drawGraph_btt.setEnabled(false);
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		final String xAxisSelection = (String) this.xAxis_cmb.getSelectedItem();
		final String graphTypeSelection = (String) this.graphType_cmb
		        .getSelectedItem();
		if (((this.oldYAxisSelection != null) && this.oldYAxisSelection
		        .equals(yAxisSelection))
		        && ((this.oldXAxisSelection != null) && this.oldXAxisSelection
		                .equals(xAxisSelection))
		        && ((this.oldGraphTypeSelection != null) && this.oldGraphTypeSelection
		                .equals(graphTypeSelection)))
			return;
		// if (xAxisSelection.equals(StatsConstants.GRAPH_LAB_X_TPT)) {
		// if (!yAxisSelection.equals(TMIConstants.GRAPH_NAME_INT_PEAK)
		// && !yAxisSelection.equals(TMIConstants.GRAPH_NAME_INT_CENT)
		// && !yAxisSelection.equals(TMIConstants.GRAPH_NAME_INT_MEAN)
		// && !yAxisSelection.equals(TMIConstants.GRAPH_NAME_NOISE)
		// && !yAxisSelection.equals(TMIConstants.GRAPH_NAME_SNR)
		// && !yAxisSelection.equals(TMIConstants.GRAPH_NAME_AREA))
		// return;
		// } else {
		// if (yAxisSelection.equals(TMIConstants.GRAPH_NAME_INT_PEAK)
		// || yAxisSelection.equals(TMIConstants.GRAPH_NAME_INT_CENT)
		// || yAxisSelection.equals(TMIConstants.GRAPH_NAME_INT_MEAN)
		// || yAxisSelection.equals(TMIConstants.GRAPH_NAME_NOISE)
		// || yAxisSelection.equals(TMIConstants.GRAPH_NAME_SNR)
		// || yAxisSelection.equals(TMIConstants.GRAPH_NAME_AREA))
		// return;
		// }
		this.handleDrawChartLater();
		this.drawGraph_btt.setEnabled(true);
	}

	private void handleTracksChart(final int peakMeanBgSnrOption,
	        final int minMeanMaxOption) {
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
		final TMIGraphProducer graphProducer = new TMIGraphProducer(this,
		        graphType, peakMeanBgSnrOption, minMeanMaxOption, this.maxT,
		        selectedSegmentsMap, this.segmTypes,
		        this.selectedTrackingMeasuresRun.getPeakSignalsResults(),
		        this.selectedTrackingMeasuresRun.getCentroidSignalsResults(),
		        this.selectedTrackingMeasuresRun.getMeanSignalsResults(),
		        this.selectedTrackingMeasuresRun.getNoisesResults(),
		        this.selectedTrackingMeasuresRun.getAreasResults(),
		        this.selectedTrackingMeasuresRun.getSNRsResults(), false);
		this.launchGraphProducerThread(graphProducer);
	}

	private void handleTimepointsChart(final int peakMeanBgSnrOption,
			final int minMeanMaxOption) {
		this.pluginPanel.updateStatus("Preparing timepoints graph");
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
		final TMIGraphProducer graphProducer = new TMIGraphProducer(this,
		        graphType, peakMeanBgSnrOption, minMeanMaxOption, this.maxT,
		        selectedSegmentsMap, this.segmTypes,
				this.selectedTrackingMeasuresRun.getPeakSignalsResults(),
		        this.selectedTrackingMeasuresRun.getCentroidSignalsResults(),
		        this.selectedTrackingMeasuresRun.getMeanSignalsResults(),
		        this.selectedTrackingMeasuresRun.getNoisesResults(),
		        this.selectedTrackingMeasuresRun.getAreasResults(),
		        this.selectedTrackingMeasuresRun.getSNRsResults(), true);
		this.launchGraphProducerThread(graphProducer);
	}

	private void launchGraphProducerThread(final TMIGraphProducer graphProducer) {
		if ((this.t != null) && this.t.isAlive()) {
			this.graphProducer.terminate();
		}
		this.t = new Thread(graphProducer);
		this.graphProducer = graphProducer;
		this.t.setName("IntensityGraphProducer");
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
		this.handleDrawChartLater();
	}

	public void updateSelectedTrackingMeasuresRun(
	        final OmegaTrackingMeasuresIntensityRun trackingMeasuresRun) {
		this.selectedTrackingMeasuresRun = trackingMeasuresRun;
		this.yAxis_cmb.removeItem(TMIConstants.MAX_MEAN_INTENSITY);
		this.yAxis_cmb.removeItem(TMIConstants.AVG_MEAN_INTENSITY);
		this.yAxis_cmb.removeItem(TMIConstants.MIN_MEAN_INTENSITY);
		this.yAxis_cmb.removeItem(TMIConstants.MAX_NOISE);
		this.yAxis_cmb.removeItem(TMIConstants.AVG_NOISE);
		this.yAxis_cmb.removeItem(TMIConstants.MIN_NOISE);
		this.yAxis_cmb.removeItem(TMIConstants.MAX_SNR);
		this.yAxis_cmb.removeItem(TMIConstants.AVG_SNR);
		this.yAxis_cmb.removeItem(TMIConstants.MIN_SNR);
		this.yAxis_cmb.removeItem(TMIConstants.MAX_AREA);
		this.yAxis_cmb.removeItem(TMIConstants.AVG_AREA);
		this.yAxis_cmb.removeItem(TMIConstants.MIN_AREA);
		if ((this.selectedTrackingMeasuresRun != null)
				&& (this.selectedTrackingMeasuresRun.getSNRRun() != null)) {
			this.yAxis_cmb.addItem(TMIConstants.MAX_MEAN_INTENSITY);
			this.yAxis_cmb.addItem(TMIConstants.AVG_MEAN_INTENSITY);
			this.yAxis_cmb.addItem(TMIConstants.MIN_MEAN_INTENSITY);
			this.yAxis_cmb.addItem(TMIConstants.MAX_NOISE);
			this.yAxis_cmb.addItem(TMIConstants.AVG_NOISE);
			this.yAxis_cmb.addItem(TMIConstants.MIN_NOISE);
			this.yAxis_cmb.addItem(TMIConstants.MAX_SNR);
			this.yAxis_cmb.addItem(TMIConstants.AVG_SNR);
			this.yAxis_cmb.addItem(TMIConstants.MIN_SNR);
			this.yAxis_cmb.addItem(TMIConstants.MAX_AREA);
			this.yAxis_cmb.addItem(TMIConstants.AVG_AREA);
			this.yAxis_cmb.addItem(TMIConstants.MIN_AREA);
		}
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
