package edu.umassmed.omega.trackingMeasuresVelocityPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.runnable.StatsGraphProducer;
import edu.umassmed.omega.trackingMeasuresVelocityPlugin.TMVConstants;
import edu.umassmed.omega.trackingMeasuresVelocityPlugin.gui.TMVGraphPanel;

public class TMVGraphProducer extends StatsGraphProducer {

	private final TMVGraphPanel velocityPanel;

	private final int velocityOption;
	private final int maxT;
	private final Map<OmegaSegment, List<Double>> localSpeedMap;
	private final Map<OmegaSegment, List<Double>> localVelocityMap;
	private final Map<OmegaSegment, Double> averageCurvilinearSpeedMap;
	private final Map<OmegaSegment, Double> averageStraightLineVelocityMap;
	private final Map<OmegaSegment, Double> forwardProgressionLinearityMap;

	private JPanel graphPanel;

	public TMVGraphProducer(final TMVGraphPanel velocityPanel,
	        final int graphType, final int velocityOption, final int tMax,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final OmegaSegmentationTypes segmTypes,
	        final Map<OmegaSegment, List<Double>> localSpeedMap,
	        final Map<OmegaSegment, List<Double>> localVelocityMap,
	        final Map<OmegaSegment, Double> averageCurvilinearSpeedMap,
	        final Map<OmegaSegment, Double> averageStraightLineVelocityMap,
	        final Map<OmegaSegment, Double> forwardProgressionLinearityMap) {
		super(graphType, segmentsMap, segmTypes);
		this.velocityPanel = velocityPanel;
		this.velocityOption = velocityOption;
		this.maxT = tMax;
		this.localSpeedMap = localSpeedMap;
		this.localVelocityMap = localVelocityMap;
		this.averageCurvilinearSpeedMap = averageCurvilinearSpeedMap;
		this.averageStraightLineVelocityMap = averageStraightLineVelocityMap;
		this.forwardProgressionLinearityMap = forwardProgressionLinearityMap;
		this.graphPanel = null;
	}

	// public TMMGraphProducer(final int distDispOption,final int tMax,
	// final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	// final Map<OmegaTrajectory, List<Double[]>> motilityMap) {
	// this.completed = 0.0;
	// this.distDispOption = distDispOption;
	// this.isTimepointsGraph = false;
	// this.maxT = tMax;
	// this.segmentsMap = segmentsMap;
	// this.motilityMap = motilityMap;
	//
	// this.graphPanel = null;
	// }

	@Override
	public void run() {
		super.run();
		switch (this.velocityOption) {
		case TMVGraphPanel.OPTION_LOCAL_VELOCITY:
		case TMVGraphPanel.OPTION_LOCAL_SPEED:
			this.graphPanel = this.prepareTimepointsGraph(this.maxT);
			break;
		default:
			this.graphPanel = this.prepareTracksGraph(false);
		}
		this.updateStatus(true);
	}

	@Override
	public String getTitle() {
		String title;
		switch (this.velocityOption) {
		case TMVGraphPanel.OPTION_LOCAL_VELOCITY:
			title = TMVConstants.GRAPH_NAME_VEL_LOCAL;
			break;
		case TMVGraphPanel.OPTION_MEAN_SPEED:
			title = TMVConstants.GRAPH_NAME_SPEED;
			break;
		case TMVGraphPanel.OPTION_MEAN_VELOCITY:
			title = TMVConstants.GRAPH_NAME_VEL;
			break;
		default:
			title = TMVConstants.GRAPH_NAME_SPEED_LOCAL;
		}
		return title;
	}

	@Override
	public String getYAxisTitle() {
		String yAxisTitle;
		switch (this.velocityOption) {
		case TMVGraphPanel.OPTION_LOCAL_VELOCITY:
		case TMVGraphPanel.OPTION_MEAN_VELOCITY:
			yAxisTitle = TMVConstants.GRAPH_LAB_Y_VEL;
			break;
		default:
			yAxisTitle = TMVConstants.GRAPH_LAB_Y_SPEED;
		}
		return yAxisTitle;
	}

	@Override
	protected Double[] getValue(final OmegaSegment segment, final OmegaROI roi) {
		List<Double> values = null;
		final Double[] value = new Double[1];
		switch (this.velocityOption) {
		case TMVGraphPanel.OPTION_MEAN_VELOCITY:
			value[0] = this.averageStraightLineVelocityMap.get(segment);
			break;
		case TMVGraphPanel.OPTION_MEAN_SPEED:
			value[0] = this.averageCurvilinearSpeedMap.get(segment);
			break;
		case TMVGraphPanel.OPTION_LOCAL_VELOCITY:
			values = this.localVelocityMap.get(segment);
			value[0] = values.get(roi.getFrameIndex());
			break;
		default:
			values = this.localSpeedMap.get(segment);
			value[0] = values.get(roi.getFrameIndex());
		}
		return value;
	}

	@Override
	public void updateStatus(final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TMVGraphProducer.this.velocityPanel.updateStatus(
					        TMVGraphProducer.this.getCompleted(), ended,
					        TMVGraphProducer.this.graphPanel);
				}
			});
		} catch (final InvocationTargetException | InterruptedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	public JPanel getGraph() {
		return this.graphPanel;
	}
}
