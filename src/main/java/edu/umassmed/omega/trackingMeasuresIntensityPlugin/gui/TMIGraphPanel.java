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

import edu.umassmed.omega.commons.constants.GraphLabelConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresIntensityRun;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.runnable.StatsGraphProducer;
import edu.umassmed.omega.trackingMeasuresIntensityPlugin.runnable.TMIGraphProducer;

public class TMIGraphPanel extends GenericPanel {
	private static final long serialVersionUID = 1124434645792957106L;

	public static final int OPTION_PEAK_SIGNAL = 0;
	public static final int OPTION_CENTROID_SIGNAL = 1;
	public static final int OPTION_MEAN_SIGNAL = 2;
	public static final int OPTION_NOISE = 3;
	public static final int OPTION_AREA = 4;
	public static final int OPTION_SNR = 5;
	public static final int OPTION_BACKGROUND = 6;

	public static final int OPTION_MIN = 0;
	public static final int OPTION_MEAN = 1;
	public static final int OPTION_MAX = 2;

	private final TMIPluginPanel pluginPanel;

	private JPanel centerPanel;
	private GenericComboBox<String> xAxis_cmb, yAxis_cmb, graphType_cmb,
			globalOrLocal_cmb;
	private JTextField selection_txt;
	private JButton drawGraph_btt;

	private Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap;
	private int maxT;
	private String oldXAxisSelection, oldYAxisSelection, oldGraphTypeSelection;

	private JPanel graphPanel, legendPanel;
	private final Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap;

	private OmegaTrackingMeasuresIntensityRun selectedTrackingMeasuresRun;
	private OmegaSegmentationTypes segmTypes;
	private final Thread t;
	private TMIGraphProducer graphProducer;
	
	private boolean handlingEvent;

	private int lineSize, shapeSize;

	public TMIGraphPanel(final RootPaneContainer parent,
			final TMIPluginPanel pluginPanel,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final int lineSize, final int shapeSize) {
		super(parent);

		this.pluginPanel = pluginPanel;

		this.lineSize = lineSize;
		this.shapeSize = shapeSize;

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
		leftPanel.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		
		final JLabel globalOrLocal_lbl = new JLabel(
				GraphLabelConstants.GRAPH_RESULTSTYPE_LBL);
		globalOrLocal_lbl
				.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		globalOrLocal_lbl
				.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(globalOrLocal_lbl);
		this.globalOrLocal_cmb = new GenericComboBox<>(
				this.getParentContainer());
		this.globalOrLocal_cmb.addItem(OmegaGUIConstants.TAB_RESULTS_LOCAL);
		this.globalOrLocal_cmb.addItem(OmegaGUIConstants.TAB_RESULTS_GLOBAL);
		this.globalOrLocal_cmb
				.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		this.globalOrLocal_cmb.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.globalOrLocal_cmb);

		final JLabel yAxis_lbl = new JLabel(GraphLabelConstants.GRAPH_Y_LBL);
		yAxis_lbl
				.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		yAxis_lbl.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(yAxis_lbl);
		this.yAxis_cmb = new GenericComboBox<>(this.getParentContainer());
		// this.yAxis_cmb.addItem(TMIConstants.GRAPH_NAME_INT_PEAK);
		// this.yAxis_cmb.addItem(TMIConstants.GRAPH_NAME_INT_CENT);
		this.yAxis_cmb.addItem(GraphLabelConstants.PEAK_INTENSITY_LOC);
		this.yAxis_cmb.addItem(GraphLabelConstants.CENTROID_INTENSITY_LOC);
		this.yAxis_cmb.addItem(GraphLabelConstants.MEAN_INTENSITY_LOC);
		this.yAxis_cmb.addItem(GraphLabelConstants.BACKGROUND_LOC);
		this.yAxis_cmb.addItem(GraphLabelConstants.NOISE_LOC);
		this.yAxis_cmb.addItem(GraphLabelConstants.SNR_LOC);
		this.yAxis_cmb.addItem(GraphLabelConstants.AREA_LOC);
		this.yAxis_cmb.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		this.yAxis_cmb.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.yAxis_cmb);

		final JLabel xAxis_lbl = new JLabel(GraphLabelConstants.GRAPH_X_LBL);
		xAxis_lbl
				.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		xAxis_lbl.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(xAxis_lbl);
		this.xAxis_cmb = new GenericComboBox<>(this.getParentContainer());
		this.xAxis_cmb.addItem(GraphLabelConstants.GRAPH_LAB_X_TPT);
		// this.xAxis_cmb.addItem(OmegaGenericConstants.GRAPH_LAB_X_TRACK);
		// this.xAxis_cmb.addItem("Segments");
		this.xAxis_cmb.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		this.xAxis_cmb.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.xAxis_cmb);

		final JLabel selection_lbl = new JLabel(
				GraphLabelConstants.GRAPH_VAL_RANGE);
		selection_lbl.setToolTipText(GraphLabelConstants.GRAPH_VAL_RANGE_TT);
		selection_lbl.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		selection_lbl.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(selection_lbl);
		this.selection_txt = new JTextField();
		this.selection_txt
				.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		this.selection_txt.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.selection_txt);

		final JLabel graphType_lbl = new JLabel(GraphLabelConstants.GRAPH_TYPE);
		graphType_lbl.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		graphType_lbl.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(graphType_lbl);
		this.graphType_cmb = new GenericComboBox<>(this.getParentContainer());
		this.graphType_cmb.addItem(GraphLabelConstants.GRAPH_TYPE_LINE);
		this.graphType_cmb.addItem(GraphLabelConstants.GRAPH_TYPE_BAR);
		this.graphType_cmb.addItem(GraphLabelConstants.GRAPH_TYPE_HIST);
		// this.xAxis_cmb.addItem("Segments");
		this.graphType_cmb
				.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		this.graphType_cmb.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.graphType_cmb);

		this.drawGraph_btt = new JButton(GraphLabelConstants.GRAPH_DRAW);
		this.drawGraph_btt
				.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		this.drawGraph_btt.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
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
				TMIGraphPanel.this.handleChangeResultsType();
			}
		});
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
	
	private void handleChangeResultsType() {
		this.handlingEvent = true;
		this.yAxis_cmb.removeAllItems();
		this.xAxis_cmb.removeAllItems();
		if (this.globalOrLocal_cmb.getSelectedItem().equals(
				OmegaGUIConstants.TAB_RESULTS_GLOBAL)) {
			this.xAxis_cmb.addItem(GraphLabelConstants.GRAPH_LAB_X_TRACK);
			this.yAxis_cmb
					.addItem(GraphLabelConstants.AVG_CENTROID_INTENSITY_GLO);
			this.yAxis_cmb
					.addItem(GraphLabelConstants.MIN_CENTROID_INTENSITY_GLO);
			this.yAxis_cmb
					.addItem(GraphLabelConstants.MAX_CENTROID_INTENSITY_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.AVG_PEAK_INTENSITY_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MIN_PEAK_INTENSITY_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MAX_PEAK_INTENSITY_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.AVG_MEAN_INTENSITY_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MIN_MEAN_INTENSITY_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MAX_MEAN_INTENSITY_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.AVG_BACKGROUND_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MIN_BACKGROUND_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MAX_BACKGROUND_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.AVG_NOISE_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MIN_NOISE_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MAX_NOISE_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.AVG_SNR_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MIN_SNR_GLO);
			this.yAxis_cmb.addItem(GraphLabelConstants.MAX_SNR_GLO);
			// this.yAxis_cmb.addItem(OmegaGenericConstants.MAX_AREA_GLO);
			// this.yAxis_cmb.addItem(OmegaGenericConstants.AVG_AREA_GLO);
			// this.yAxis_cmb.addItem(OmegaGenericConstants.MIN_AREA_GLO);
		} else {
			this.xAxis_cmb.addItem(GraphLabelConstants.GRAPH_LAB_X_TPT);
			this.yAxis_cmb.addItem(GraphLabelConstants.CENTROID_INTENSITY_LOC);
			this.yAxis_cmb.addItem(GraphLabelConstants.PEAK_INTENSITY_LOC);
			this.yAxis_cmb.addItem(GraphLabelConstants.MEAN_INTENSITY_LOC);
			this.yAxis_cmb.addItem(GraphLabelConstants.BACKGROUND_LOC);
			this.yAxis_cmb.addItem(GraphLabelConstants.NOISE_LOC);
			this.yAxis_cmb.addItem(GraphLabelConstants.SNR_LOC);
			this.yAxis_cmb.addItem(GraphLabelConstants.AREA_LOC);
		}
		this.handlingEvent = false;
		this.handleDrawChartLater();
	}

	private void handleComponentResized() {
		if (this.legendPanel == null)
			return;
		final int height = this.getHeight() - 20;
		final int width = this.getWidth() / 3;
		final Dimension graphDim = new Dimension(width, height);
		this.legendPanel.setSize(graphDim);
		this.legendPanel.setPreferredSize(graphDim);
		this.legendPanel.setMaximumSize(graphDim);
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
		if (xAxisSelection.equals(GraphLabelConstants.GRAPH_LAB_X_TPT)) {
			this.handleDrawTimepointsChart();
		} else {
			this.handleDrawTracksChart();
		}
	}

	private void handleDrawTimepointsChart() {
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		if (yAxisSelection.equals(GraphLabelConstants.PEAK_INTENSITY_LOC)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_PEAK_SIGNAL);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.CENTROID_INTENSITY_LOC)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.MEAN_INTENSITY_LOC)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_MEAN_SIGNAL);
		} else if (yAxisSelection.equals(GraphLabelConstants.BACKGROUND_LOC)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_BACKGROUND);
		} else if (yAxisSelection.equals(GraphLabelConstants.NOISE_LOC)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_NOISE);
		} else if (yAxisSelection.equals(GraphLabelConstants.SNR_LOC)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_SNR);
		} else if (yAxisSelection.equals(GraphLabelConstants.AREA_LOC)) {
			this.handleTimepointsChart(TMIGraphPanel.OPTION_AREA);
		}
	}

	private void handleDrawTracksChart() {
		final String yAxisSelection = (String) this.yAxis_cmb.getSelectedItem();
		if (yAxisSelection.equals(GraphLabelConstants.MIN_PEAK_INTENSITY_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_PEAK_SIGNAL,
					TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.AVG_PEAK_INTENSITY_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_PEAK_SIGNAL,
					TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.MAX_PEAK_INTENSITY_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_PEAK_SIGNAL,
					TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.MIN_CENTROID_INTENSITY_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL,
					TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.AVG_CENTROID_INTENSITY_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL,
					TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.MAX_CENTROID_INTENSITY_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_CENTROID_SIGNAL,
					TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.MIN_MEAN_INTENSITY_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_MEAN_SIGNAL,
					TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.AVG_MEAN_INTENSITY_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_MEAN_SIGNAL,
					TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.MAX_MEAN_INTENSITY_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_MEAN_SIGNAL,
					TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.MIN_BACKGROUND_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_BACKGROUND,
					TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.AVG_BACKGROUND_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_BACKGROUND,
					TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection
				.equals(GraphLabelConstants.MAX_BACKGROUND_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_BACKGROUND,
					TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(GraphLabelConstants.MIN_NOISE_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_NOISE,
					TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(GraphLabelConstants.AVG_NOISE_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_NOISE,
					TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(GraphLabelConstants.MAX_NOISE_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_NOISE,
					TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(GraphLabelConstants.MIN_SNR_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_SNR,
					TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(GraphLabelConstants.AVG_SNR_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_SNR,
					TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(GraphLabelConstants.MAX_SNR_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_SNR,
					TMIGraphPanel.OPTION_MAX);
		} else if (yAxisSelection.equals(GraphLabelConstants.MIN_AREA_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_AREA,
					TMIGraphPanel.OPTION_MIN);
		} else if (yAxisSelection.equals(GraphLabelConstants.AVG_AREA_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_AREA,
					TMIGraphPanel.OPTION_MEAN);
		} else if (yAxisSelection.equals(GraphLabelConstants.MAX_AREA_GLO)) {
			this.handleTracksChart(TMIGraphPanel.OPTION_AREA,
					TMIGraphPanel.OPTION_MAX);
		}
	}

	private void handleChangeAxis() {
		if (this.handlingEvent)
			return;
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
		this.handleDrawChartLater();
		this.drawGraph_btt.setEnabled(true);
	}

	private void handleTracksChart(final int peakMeanBgSnrOption,
			final int minMeanMaxOption) {
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
				GraphLabelConstants.GRAPH_TYPE_BAR)) {
			graphType = StatsGraphProducer.BAR_GRAPH;
		} else if (this.graphType_cmb.getSelectedItem().equals(
				GraphLabelConstants.GRAPH_TYPE_HIST)) {
			graphType = StatsGraphProducer.HISTOGRAM_GRAPH;
		}
		final TMIGraphProducer graphProducer = new TMIGraphProducer(this,
				graphType, peakMeanBgSnrOption, minMeanMaxOption, false,
				this.maxT, selectedSegmentsMap, this.segmTypes,
				this.selectedTrackingMeasuresRun.getPeakSignalsResults(),
				this.selectedTrackingMeasuresRun.getCentroidSignalsResults(),
				this.selectedTrackingMeasuresRun.getPeakSignalsLocalResults(),
				this.selectedTrackingMeasuresRun
						.getCentroidSignalsLocalResults(),
				this.selectedTrackingMeasuresRun.getMeanSignalsResults(),
				this.selectedTrackingMeasuresRun.getBackgroundsResults(),
				this.selectedTrackingMeasuresRun.getNoisesResults(),
				this.selectedTrackingMeasuresRun.getAreasResults(),
				this.selectedTrackingMeasuresRun.getSNRsResults(),
				this.selectedTrackingMeasuresRun.getMeanSignalsLocalResults(),
				this.selectedTrackingMeasuresRun.getBackgroundsLocalResults(),
				this.selectedTrackingMeasuresRun.getNoisesLocalResults(),
				this.selectedTrackingMeasuresRun.getAreasLocalResults(),
				this.selectedTrackingMeasuresRun.getSNRsLocalResults(),
				this.lineSize, this.shapeSize);
		this.launchGraphProducerThread(graphProducer);
	}

	private void handleTimepointsChart(final int peakMeanBgSnrOption) {
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
				GraphLabelConstants.GRAPH_TYPE_BAR)) {
			graphType = StatsGraphProducer.BAR_GRAPH;
		} else if (this.graphType_cmb.getSelectedItem().equals(
				GraphLabelConstants.GRAPH_TYPE_HIST)) {
			graphType = StatsGraphProducer.HISTOGRAM_GRAPH;
		}
		final TMIGraphProducer graphProducer = new TMIGraphProducer(this,
				graphType, peakMeanBgSnrOption, -1, true, this.maxT,
				selectedSegmentsMap, this.segmTypes,
				this.selectedTrackingMeasuresRun.getPeakSignalsResults(),
				this.selectedTrackingMeasuresRun.getCentroidSignalsResults(),
				this.selectedTrackingMeasuresRun.getPeakSignalsLocalResults(),
				this.selectedTrackingMeasuresRun
						.getCentroidSignalsLocalResults(),
				this.selectedTrackingMeasuresRun.getMeanSignalsResults(),
				this.selectedTrackingMeasuresRun.getBackgroundsResults(),
				this.selectedTrackingMeasuresRun.getNoisesResults(),
				this.selectedTrackingMeasuresRun.getAreasResults(),
				this.selectedTrackingMeasuresRun.getSNRsResults(),
				this.selectedTrackingMeasuresRun.getMeanSignalsLocalResults(),
				this.selectedTrackingMeasuresRun.getBackgroundsLocalResults(),
				this.selectedTrackingMeasuresRun.getNoisesLocalResults(),
				this.selectedTrackingMeasuresRun.getAreasLocalResults(),
				this.selectedTrackingMeasuresRun.getSNRsLocalResults(),
				this.lineSize, this.shapeSize);
		this.launchGraphProducerThread(graphProducer);
	}

	private void launchGraphProducerThread(final TMIGraphProducer graphProducer) {
		// if ((this.t != null) && this.t.isAlive()) {
		// this.graphProducer.terminate();
		// }
		// this.t = new Thread(graphProducer);
		this.graphProducer = graphProducer;
		this.graphProducer.doRun();
		// this.t.setName("IntensityGraphProducer");
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
			final OmegaTrackingMeasuresIntensityRun trackingMeasuresRun) {
		this.selectedTrackingMeasuresRun = trackingMeasuresRun;
		// this.yAxis_cmb.removeItem(TMIConstants.MAX_MEAN_INTENSITY);
		// this.yAxis_cmb.removeItem(TMIConstants.AVG_MEAN_INTENSITY);
		// this.yAxis_cmb.removeItem(TMIConstants.MIN_MEAN_INTENSITY);
		// this.yAxis_cmb.removeItem(TMIConstants.MAX_NOISE);
		// this.yAxis_cmb.removeItem(TMIConstants.AVG_NOISE);
		// this.yAxis_cmb.removeItem(TMIConstants.MIN_NOISE);
		// this.yAxis_cmb.removeItem(TMIConstants.MAX_SNR);
		// this.yAxis_cmb.removeItem(TMIConstants.AVG_SNR);
		// this.yAxis_cmb.removeItem(TMIConstants.MIN_SNR);
		// this.yAxis_cmb.removeItem(TMIConstants.MAX_AREA);
		// this.yAxis_cmb.removeItem(TMIConstants.AVG_AREA);
		// this.yAxis_cmb.removeItem(TMIConstants.MIN_AREA);
		// if ((this.selectedTrackingMeasuresRun != null)
		// && (this.selectedTrackingMeasuresRun.getSNRRun() != null)) {
		// this.yAxis_cmb.addItem(TMIConstants.MAX_MEAN_INTENSITY);
		// this.yAxis_cmb.addItem(TMIConstants.AVG_MEAN_INTENSITY);
		// this.yAxis_cmb.addItem(TMIConstants.MIN_MEAN_INTENSITY);
		// this.yAxis_cmb.addItem(TMIConstants.MAX_NOISE);
		// this.yAxis_cmb.addItem(TMIConstants.AVG_NOISE);
		// this.yAxis_cmb.addItem(TMIConstants.MIN_NOISE);
		// this.yAxis_cmb.addItem(TMIConstants.MAX_SNR);
		// this.yAxis_cmb.addItem(TMIConstants.AVG_SNR);
		// this.yAxis_cmb.addItem(TMIConstants.MIN_SNR);
		// this.yAxis_cmb.addItem(TMIConstants.MAX_AREA);
		// this.yAxis_cmb.addItem(TMIConstants.AVG_AREA);
		// this.yAxis_cmb.addItem(TMIConstants.MIN_AREA);
		// }
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
			if ((this.graphPanel == null) || (this.legendPanel == null))
				return;
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
	
	public void setLineSize(final int lineSize) {
		this.lineSize = lineSize;
		this.handleDrawChartLater();
	}

	public void setShapeSize(final int shapeSize) {
		this.shapeSize = shapeSize;
		this.handleDrawChartLater();
	}
}
