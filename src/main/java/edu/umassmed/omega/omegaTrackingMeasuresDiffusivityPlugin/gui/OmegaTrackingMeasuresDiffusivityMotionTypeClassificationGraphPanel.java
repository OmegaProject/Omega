package edu.umassmed.omega.omegaTrackingMeasuresDiffusivityPlugin.gui;

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
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.constants.GraphLabelConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrackingMeasuresDiffusivityRun;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.GenericComboBox;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.omegaTrackingMeasuresDiffusivityPlugin.runnable.OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphProducer;

public class OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel extends GenericPanel {
	private static final long serialVersionUID = 1124434645792957106L;

	public static final int OPTION_LINEAR = 0;
	public static final int OPTION_LOG = 1;

	public static final int OPTION_SHOW_ALL = 0;
	public static final int OPTION_SHOW_TRACK_ONLY = 1;
	public static final int OPTION_SHOW_MSD_ONLY = 2;
	public static final int OPTION_SHOW_MSS_ONLY = 3;
	public static final int OPTION_SHOW_PHASE_ONLY = 4;

	private final OmegaTrackingMeasuresDiffusivityPluginPanel pluginPanel;

	private JPanel mainPanel, centerPanel, legendLeft, legendRight;

	private GenericComboBox<String> showOption_cmb;
	private String oldOptionSelection;

	private JButton drawGraph_btt;

	private Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			selectedSegments;
	private OmegaSegmentationTypes segmTypes;
	// private final OmegaTrajectory selectedTrack;
	// private List<OmegaSegment> selectedSegments;
	private int maxT, imgWidth, imgHeight;

	private JPanel[] chartPanels, legendPanels;

	private int lineSize, shapeSize;

	private OmegaTrackingMeasuresDiffusivityRun selectedTrackingMeasuresRun;
	private final Thread t;
	private OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphProducer graphProducer;

	public OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel(
			final RootPaneContainer parent, final OmegaTrackingMeasuresDiffusivityPluginPanel pluginPanel,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final int lineSize, final int shapeSize) {
		super(parent);

		this.lineSize = lineSize;
		this.shapeSize = shapeSize;

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
		this.legendPanels = new JPanel[4];
		for (int i = 0; i < this.legendPanels.length; i++) {
			this.legendPanels[i] = null;
		}
		this.t = null;

		this.oldOptionSelection = null;

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		final JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout());
		leftPanel.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);

		final JLabel yAxis_lbl = new JLabel("Select graph to show");
		yAxis_lbl
				.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		yAxis_lbl.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE_DOUBLE_HEIGHT);
		leftPanel.add(yAxis_lbl);
		this.showOption_cmb = new GenericComboBox<>(this.getParentContainer());
		this.showOption_cmb.addItem(GraphLabelConstants.GRAPH_MTC_LBL_COMPLETE);
		this.showOption_cmb.addItem(GraphLabelConstants.GRAPH_MTC_LBL_TRACK);
		this.showOption_cmb.addItem(GraphLabelConstants.GRAPH_MTC_LBL_MSD);
		this.showOption_cmb.addItem(GraphLabelConstants.GRAPH_MTC_LBL_MSS);
		this.showOption_cmb.addItem(GraphLabelConstants.GRAPH_MTC_LBL_PHASE);
		this.showOption_cmb
				.setPreferredSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		this.showOption_cmb.setSize(OmegaGUIConstants.BUTTON_SIZE_LARGE);
		leftPanel.add(this.showOption_cmb);

		// this.drawGraph_btt = new JButton(OmegaGenericConstants.GRAPH_DRAW);
		// this.drawGraph_btt.setPreferredSize(OmegaGenericConstants.BUTTON_SIZE_LARGE);
		// this.drawGraph_btt.setSize(OmegaGenericConstants.BUTTON_SIZE_LARGE);
		// leftPanel.add(this.drawGraph_btt);

		this.add(leftPanel, BorderLayout.WEST);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BorderLayout());

		this.centerPanel = new JPanel();
		this.centerPanel.setLayout(new GridLayout(2, 2));
		// final JPanel layerPanel = new JPanel();
		// layerPanel.setLayout(new FlowLayout());
		// layerPanel.add(this.centerPanel);
		this.mainPanel.add(this.centerPanel, BorderLayout.CENTER);

		this.legendLeft = new JPanel();
		this.legendLeft.setLayout(new GridLayout(2, 1));
		this.mainPanel.add(this.legendLeft, BorderLayout.WEST);

		this.legendRight = new JPanel();
		this.legendRight.setLayout(new GridLayout(2, 1));
		this.mainPanel.add(this.legendRight, BorderLayout.EAST);

		this.add(this.mainPanel, BorderLayout.CENTER);
		// this.handleDrawChart();
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.this
						.handleComponentResized();
			}
		});
		this.showOption_cmb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.this.handleChangeOption();
			}
		});
		// this.drawGraph_btt.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(final ActionEvent e) {
		// OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.this.handleDrawChart();
		// }
		// });
	}

	private void handleChangeOption() {
		// this.drawGraph_btt.setEnabled(false);
		final String selection = (String) this.showOption_cmb.getSelectedItem();
		if ((this.oldOptionSelection != null)
				&& this.oldOptionSelection.equals(selection))
			return;
		this.oldOptionSelection = selection;
		this.handleDrawChartLater();
		// this.drawGraph_btt.setEnabled(true);
	}

	private void handleComponentResized() {
		if (this.legendPanels == null)
			return;
		final int height = this.getHeight() / 2;
		final int width = this.getWidth() / 6;
		final Dimension graphDim = new Dimension(width, height);
		for (final JPanel legendPanel : this.legendPanels) {
			if (legendPanel == null) {
				continue;
			}
			legendPanel.setSize(graphDim);
			legendPanel.setPreferredSize(graphDim);
			legendPanel.setMaximumSize(graphDim);
		}
		this.repaint();
	}

	private void handleDrawChartLater() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.this.handleDrawChart();
			}
		});
	}

	private void handleDrawChart() {
		// if (this.selectedTrackingMeasuresRun == null)
		// return;
		this.pluginPanel.updateStatus("Preparing graphs");
		for (int i = 0; i < this.chartPanels.length; i++) {
			final JPanel chartPanel = this.chartPanels[i];
			if (chartPanel != null) {
				this.centerPanel.remove(chartPanel);
			}
			this.chartPanels[i] = null;
		}
		for (int i = 0; i < this.legendPanels.length; i++) {
			final JPanel legendPanel = this.legendPanels[i];
			if (legendPanel != null) {
				this.legendLeft.remove(legendPanel);
				this.legendRight.remove(legendPanel);
				this.mainPanel.remove(legendPanel);
			}

			this.legendPanels[i] = null;
		}
		this.mainPanel.remove(this.legendLeft);
		this.mainPanel.remove(this.legendRight);
		this.revalidate();
		this.repaint();
		if (this.selectedTrackingMeasuresRun == null)
			return;
		// if (this.selectedTrack == null)
		// return;
		final String selection = (String) this.showOption_cmb.getSelectedItem();
		int showOption = OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.OPTION_SHOW_ALL;
		if (selection == GraphLabelConstants.GRAPH_MTC_LBL_TRACK) {
			showOption = OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.OPTION_SHOW_TRACK_ONLY;
		} else if (selection == GraphLabelConstants.GRAPH_MTC_LBL_MSD) {
			showOption = OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.OPTION_SHOW_MSD_ONLY;
		} else if (selection == GraphLabelConstants.GRAPH_MTC_LBL_MSS) {
			showOption = OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.OPTION_SHOW_MSS_ONLY;
		} else if (selection == GraphLabelConstants.GRAPH_MTC_LBL_PHASE) {
			showOption = OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.OPTION_SHOW_PHASE_ONLY;
		}
		this.handleDrawChart(OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphPanel.OPTION_LOG,
				showOption);
	}

	private void handleDrawChart(final int motionTypeOption,
			final int showOption) {
		this.pluginPanel.updateStatus("Preparing log graph");
		Map<OmegaTrajectory, List<OmegaSegment>> segments = null;
		boolean isSame = false;
		if (this.selectedSegments != null) {
			for (final OmegaTrajectory track : this.selectedTrackingMeasuresRun
					.getSegments().keySet())
				if (this.selectedSegments.containsKey(track)) {
					isSame = true;
				}
		}
		if ((this.selectedSegments == null) || this.selectedSegments.isEmpty()
				|| !isSame) {
			segments = this.segmentsMap;
		} else {
			segments = this.selectedSegments;
		}
		final OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphProducer graphProducer = new OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphProducer(
				this,
				motionTypeOption,
				showOption,
				segments,
				this.segmTypes,
				this.selectedTrackingMeasuresRun.getNyResults(),
				this.selectedTrackingMeasuresRun.getMuResults(),
				this.selectedTrackingMeasuresRun.getLogMuResults(),
				this.selectedTrackingMeasuresRun.getDeltaTResults(),
				this.selectedTrackingMeasuresRun.getLogDeltaTResults(),
				this.selectedTrackingMeasuresRun.getGammaDResults(),
				this.selectedTrackingMeasuresRun.getGammaDFromLogResults(),
				// this.selectedTrackingMeasuresRun.getGammaResults(),
				// this.selectedTrackingMeasuresRun.getGammaFromLogResults(),
				// this.selectedTrackingMeasuresRun.getSmssResults(),
				this.selectedTrackingMeasuresRun.getSmssFromLogResults(),
				// this.selectedTrackingMeasuresRun.getErrorsResults(),
				this.selectedTrackingMeasuresRun.getErrosFromLogResults(),
				this.selectedTrackingMeasuresRun.getMinimumDetectableODC(),
				this.lineSize, this.shapeSize);
		this.launchGraphProducerThread(graphProducer);
	}

	private void launchGraphProducerThread(
			final OmegaTrackingMeasuresDiffusivityMotionTypeClassificationGraphProducer graphProducer) {
		// if ((this.t != null) && this.t.isAlive()) {
		// this.graphProducer.terminate();
		// }
		// this.t = new Thread(graphProducer);
		this.graphProducer = graphProducer;
		this.graphProducer.doRun();
		// this.t.setName("MotionTypeGraphProducer");
		// this.t.start();
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

	public void clearSegmentsSelection() {
		this.selectedSegments = null;
		this.handleDrawChart();
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
		this.handleDrawChartLater();
	}

	public void updateSelectedTrackingMeasuresRun(
			final OmegaTrackingMeasuresDiffusivityRun trackingMeasuresRun) {
		this.selectedTrackingMeasuresRun = trackingMeasuresRun;
		this.handleDrawChartLater();
	}

	public void updateStatus(final double completed, final boolean ended) {
		if (ended) {
			this.chartPanels = this.graphProducer.getGraphs();
			this.legendPanels = this.graphProducer.getLegends();
			this.handleComponentResized();
			this.pluginPanel.updateStatus("Plugin ready");
			if ((this.chartPanels == null) || (this.legendPanels == null))
				return;
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
			if (charts > 1) {
				int counter = 0;
				this.mainPanel.add(this.legendLeft, BorderLayout.WEST);
				this.mainPanel.add(this.legendRight, BorderLayout.EAST);
				for (final JPanel legendPanel : this.legendPanels) {
					if (legendPanel != null)
						if ((counter == 0) || (counter == 2)) {
							this.legendLeft.add(legendPanel);
						} else {
							this.legendRight.add(legendPanel);
						}
					counter++;
				}
			} else {
				for (final JPanel legendPanel : this.legendPanels) {
					if (legendPanel != null) {
						this.mainPanel.add(legendPanel, BorderLayout.EAST);
					}
				}
			}
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
