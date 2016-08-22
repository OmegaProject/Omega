package edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresDiffusivityRun;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.runnable.TMDMotionTypeClassificationGraphProducer;

public class TMDMotionTypeClassificationGraphPanel extends GenericPanel {
	private static final long serialVersionUID = 1124434645792957106L;

	public static final int OPTION_LINEAR = 0;
	public static final int OPTION_LOG = 1;

	public static final int OPTION_SHOW_ALL = 0;
	public static final int OPTION_SHOW_TRACK_ONLY = 1;
	public static final int OPTION_SHOW_MSD_ONLY = 2;
	public static final int OPTION_SHOW_MSS_ONLY = 3;
	public static final int OPTION_SHOW_PHASE_ONLY = 4;
	public static final String OPTION_SHOW_ALL_TEXT = "All graphs";
	public static final String OPTION_SHOW_TRACK_ONLY_TEXT = "Only track graph";
	public static final String OPTION_SHOW_MSD_ONLY_TEXT = "Only MSD graph";
	public static final String OPTION_SHOW_MSS_ONLY_TEXT = "Only MSS graph";
	public static final String OPTION_SHOW_PHASE_ONLY_TEXT = "Only phase graph";

	private final TMDPluginPanel pluginPanel;

	private JPanel centerPanel;

	private GenericComboBox<String> showOption_cmb;
	private JButton drawGraph_btt;

	private Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	selectedSegments;
	private OmegaSegmentationTypes segmTypes;
	// private final OmegaTrajectory selectedTrack;
	// private List<OmegaSegment> selectedSegments;
	private int maxT, imgWidth, imgHeight;

	private JPanel[] chartPanels;

	private OmegaTrackingMeasuresDiffusivityRun selectedTrackingMeasuresRun;
	private Thread t;
	private TMDMotionTypeClassificationGraphProducer graphProducer;

	public TMDMotionTypeClassificationGraphPanel(
	        final RootPaneContainer parent, final TMDPluginPanel pluginPanel,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		super(parent);

		this.pluginPanel = pluginPanel;
		this.segmentsMap = segmentsMap;
		this.maxT = 0;
		this.imgWidth = 0;
		this.imgHeight = 0;

		this.selectedTrackingMeasuresRun = null;
		this.segmTypes = null;

		// this.selectedTrack = null;
		this.selectedSegments = null;

		this.chartPanels = new JPanel[4];
		for (int i = 0; i < this.chartPanels.length; i++) {
			this.chartPanels[i] = null;
		}
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

		final JLabel yAxis_lbl = new JLabel("Select graph to show");
		yAxis_lbl
		.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		yAxis_lbl.setSize(OmegaConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(yAxis_lbl);
		this.showOption_cmb = new GenericComboBox<>(this.getParentContainer());
		this.showOption_cmb
		.addItem(TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_ALL_TEXT);
		this.showOption_cmb
		.addItem(TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_TRACK_ONLY_TEXT);
		this.showOption_cmb
		.addItem(TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_MSD_ONLY_TEXT);
		this.showOption_cmb
		.addItem(TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_MSS_ONLY_TEXT);
		this.showOption_cmb
		.addItem(TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_PHASE_ONLY_TEXT);
		this.showOption_cmb.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		this.showOption_cmb.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.showOption_cmb);

		// this.drawGraph_btt = new JButton(StatsConstants.GRAPH_DRAW);
		// this.drawGraph_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE_LARGE);
		// this.drawGraph_btt.setSize(OmegaConstants.BUTTON_SIZE_LARGE);
		// leftPanel.add(this.drawGraph_btt);

		this.add(leftPanel, BorderLayout.WEST);

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());

		this.centerPanel = new JPanel();
		this.centerPanel.setLayout(new GridLayout(2, 2));
		mainPanel.add(this.centerPanel);

		this.add(mainPanel, BorderLayout.CENTER);
		// this.handleDrawChart();
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				TMDMotionTypeClassificationGraphPanel.this
				.handleComponentResized();
			}
		});
		this.showOption_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				TMDMotionTypeClassificationGraphPanel.this.handleDrawChart();
			}
		});
		// this.drawGraph_btt.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(final ActionEvent e) {
		// TMDMotionTypeClassificationGraphPanel.this.handleDrawChart();
		// }
		// });
	}

	private void handleComponentResized() {
		int charts = 0;
		for (final JPanel chartPanel : this.chartPanels) {
			if (chartPanel != null) {
				charts++;
			}
		}
		if (charts == 0)
			return;
		int size = this.getHeight() - 20;
		final int width = this.getWidth() - 20;
		if (size > width) {
			size = width;
		}
		size /= charts > 1 ? 2 : 1;
		final Dimension graphDim = new Dimension(size, size);
		for (final JPanel chartPanel : this.chartPanels) {
			if (chartPanel != null) {
				chartPanel.setSize(graphDim);
				chartPanel.setPreferredSize(graphDim);
			}
		}
	}

	private void handleDrawChart() {
		this.pluginPanel.updateStatus("Preparing graphs");
		for (int i = 0; i < this.chartPanels.length; i++) {
			final JPanel chartPanel = this.chartPanels[i];
			if (chartPanel != null) {
				this.centerPanel.remove(chartPanel);
			}
			this.chartPanels[i] = null;
		}
		this.revalidate();
		this.repaint();
		// if (this.selectedTrack == null)
		// return;
		int showOption = TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_ALL;
		final String selection = (String) this.showOption_cmb.getSelectedItem();
		if (selection == TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_TRACK_ONLY_TEXT) {
			showOption = TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_TRACK_ONLY;
		} else if (selection == TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_MSD_ONLY_TEXT) {
			showOption = TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_MSD_ONLY;
		} else if (selection == TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_MSS_ONLY_TEXT) {
			showOption = TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_MSS_ONLY;
		} else if (selection == TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_PHASE_ONLY_TEXT) {
			showOption = TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_PHASE_ONLY;
		}
		this.handleDrawChart(TMDMotionTypeClassificationGraphPanel.OPTION_LOG,
		        showOption);
	}

	private void handleDrawChart(final int motionTypeOption,
	        final int showOption) {
		this.pluginPanel.updateStatus("Preparing log graph");
		Map<OmegaTrajectory, List<OmegaSegment>> segments = null;
		if ((this.selectedSegments != null) && !this.selectedSegments.isEmpty()) {
			segments = this.selectedSegments;
		} else {
			segments = this.segmentsMap;
		}
		final TMDMotionTypeClassificationGraphProducer graphProducer = new TMDMotionTypeClassificationGraphProducer(
		        this, motionTypeOption, showOption, segments, this.segmTypes,
		        this.selectedTrackingMeasuresRun.getNyResults(),
		        this.selectedTrackingMeasuresRun.getMuResults(),
		        this.selectedTrackingMeasuresRun.getLogMuResults(),
		        this.selectedTrackingMeasuresRun.getDeltaTResults(),
		        this.selectedTrackingMeasuresRun.getLogDeltaTResults(),
		        this.selectedTrackingMeasuresRun.getGammaDResults(),
		        this.selectedTrackingMeasuresRun.getGammaDFromLogResults(),
		        // this.selectedTrackingMeasuresRun.getGammaResults(),
		        this.selectedTrackingMeasuresRun.getGammaFromLogResults(),
		        // this.selectedTrackingMeasuresRun.getSmssResults(),
		        this.selectedTrackingMeasuresRun.getSmssFromLogResults(),
		        // this.selectedTrackingMeasuresRun.getErrorsResults(),
		        this.selectedTrackingMeasuresRun.getErrosFromLogResults());
		this.launchGraphProducerThread(graphProducer);
	}

	private void launchGraphProducerThread(
	        final TMDMotionTypeClassificationGraphProducer graphProducer) {
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
		this.showOption_cmb.updateParentContainer(parent);
		// Bottom down menu here
	}

	public void setSegmentsMap(
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	        final OmegaSegmentationTypes segmTypes) {
		this.segmentsMap = segmentsMap;
		this.segmTypes = segmTypes;
		// this.selectedTrack = null;
		// this.handleChangeChart();
		// this.handleDrawChart();
	}

	public void setSelectedSegments(
			final Map<OmegaTrajectory, List<OmegaSegment>> selectedSegmentsMap) {
		// this.selectedTrack = null;
		// this.selectedSegments = null;
		this.selectedSegments = selectedSegmentsMap;
		// if (!selectedSegmentsMap.isEmpty()) {
		// this.selectedTrack = (OmegaTrajectory) selectedSegmentsMap.keySet()
		// .toArray()[0];
		// if ((selectedSegmentsMap.get(this.selectedTrack) != null)
		// && !selectedSegmentsMap.get(this.selectedTrack).isEmpty()) {
		// this.selectedSegments = selectedSegmentsMap
		// .get(this.selectedTrack);
		// }
		// }
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
	        final JPanel[] chartPanels) {
		if (ended) {
			this.chartPanels = chartPanels;
			int charts = 0;
			for (final JPanel chartPanel : this.chartPanels) {
				if (chartPanel != null) {
					charts++;
				}
			}
			final int sizeX = charts > 1 ? 2 : 1;
			final int sizeY = charts > 2 ? 2 : 1;
			this.centerPanel.setLayout(new GridLayout(sizeX, sizeY));
			for (final JPanel chartPanel : this.chartPanels) {
				if (chartPanel != null) {
					// chartPanel.updateParentContainer(this.getParentContainer());
					this.centerPanel.add(chartPanel);
				}
			}
			this.handleComponentResized();
			this.pluginPanel.updateStatus("Plugin ready");
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
