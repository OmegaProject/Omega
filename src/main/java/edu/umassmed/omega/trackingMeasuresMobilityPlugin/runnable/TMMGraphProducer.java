package edu.umassmed.omega.trackingMeasuresMobilityPlugin.runnable;

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
import edu.umassmed.omega.trackingMeasuresMobilityPlugin.TMMConstants;
import edu.umassmed.omega.trackingMeasuresMobilityPlugin.gui.TMMGraphPanel;

public class TMMGraphProducer extends StatsGraphProducer {
	
	private final TMMGraphPanel mobilityPanel;
	
	private final int mobilityOption;
	private final boolean isTimepointsGraph;
	private final int maxT;
	private final Map<OmegaSegment, List<Double>> distanceMap;
	private final Map<OmegaSegment, List<Double>> displacementMap;
	private final Map<OmegaSegment, Double> maxDisplacementMap;
	private final Map<OmegaSegment, Integer> totalTimeTraveledMap;
	private final Map<OmegaSegment, List<Double>> confinementRatioMap;
	private final Map<OmegaSegment, List<Double[]>> anglesAndDirectionalChangesMap;
	
	private JPanel graphPanel;
	
	public TMMGraphProducer(
	        final TMMGraphPanel mobilityPanel,
	        final int graphType,
	        final int mobilityOption,
	        final boolean isTimepointsGraph,
	        final int tMax,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	        final OmegaSegmentationTypes segmTypes,
	        final Map<OmegaSegment, List<Double>> distanceMap,
	        final Map<OmegaSegment, List<Double>> displacementMap,
	        final Map<OmegaSegment, Double> maxDisplacementMap,
	        final Map<OmegaSegment, Integer> totalTimeTraveledMap,
	        final Map<OmegaSegment, List<Double>> confinementRatioMap,
	        final Map<OmegaSegment, List<Double[]>> anglesAndDirectionalChangesMap) {
		super(graphType, segmentsMap, segmTypes);
		this.mobilityPanel = mobilityPanel;
		this.mobilityOption = mobilityOption;
		this.isTimepointsGraph = isTimepointsGraph;
		this.maxT = tMax;
		this.distanceMap = distanceMap;
		this.displacementMap = displacementMap;
		this.maxDisplacementMap = maxDisplacementMap;
		this.totalTimeTraveledMap = totalTimeTraveledMap;
		this.confinementRatioMap = confinementRatioMap;
		this.anglesAndDirectionalChangesMap = anglesAndDirectionalChangesMap;
		this.graphPanel = null;
	}
	
	@Override
	public void run() {
		super.run();
		if (this.isTimepointsGraph) {
			this.graphPanel = this.prepareTimepointsGraph(this.maxT, true);
		} else {
			this.graphPanel = this.prepareTracksGraph(true, true);
		}
		this.updateStatus(true);
	}
	
	@Override
	public String getTitle() {
		String title;
		switch (this.mobilityOption) {
			case TMMGraphPanel.OPTION_DISPLACEMENT:
				title = TMMConstants.GRAPH_NAME_TOT_DISP;
				break;
			case TMMGraphPanel.OPTION_MAX_DISPLACEMENT:
				title = TMMConstants.GRAPH_NAME_MAX_DISP;
				break;
			case TMMGraphPanel.OPTION_TOTAL_TIME_TRAVELED:
				title = TMMConstants.GRAPH_NAME_TOT_TIME;
				break;
			case TMMGraphPanel.OPTION_CONFINEMENT_RATIO:
				title = TMMConstants.GRAPH_NAME_CONFRATIO;
				break;
			case TMMGraphPanel.OPTION_LOCAL_ANGLES:
				title = TMMConstants.GRAPH_NAME_ANGLES;
				break;
			case TMMGraphPanel.OPTION_LOCAL_DIRECTIONAL_CHANGES:
				title = TMMConstants.GRAPH_NAME_ANGLES_LOCAL;
				break;
			default:
				title = TMMConstants.GRAPH_NAME_TOT_DIST;
		}
		return title;
	}
	
	@Override
	public String getYAxisTitle() {
		String yAxisTitle;
		switch (this.mobilityOption) {
			case TMMGraphPanel.OPTION_DISPLACEMENT:
				yAxisTitle = TMMConstants.GRAPH_LAB_Y_TOT_DISP;
				break;
			case TMMGraphPanel.OPTION_MAX_DISPLACEMENT:
				yAxisTitle = TMMConstants.GRAPH_LAB_Y_MAX_DISP;
				break;
			case TMMGraphPanel.OPTION_TOTAL_TIME_TRAVELED:
				yAxisTitle = TMMConstants.GRAPH_LAB_Y_TOT_TIME;
				break;
			case TMMGraphPanel.OPTION_CONFINEMENT_RATIO:
				yAxisTitle = TMMConstants.GRAPH_LAB_Y_CONFRATIO;
				break;
			case TMMGraphPanel.OPTION_LOCAL_ANGLES:
				yAxisTitle = TMMConstants.GRAPH_LAB_Y_ANGLES;
				break;
			case TMMGraphPanel.OPTION_LOCAL_DIRECTIONAL_CHANGES:
				yAxisTitle = TMMConstants.GRAPH_LAB_Y_ANGLES_LOCAL;
				break;
			default:
				yAxisTitle = TMMConstants.GRAPH_LAB_Y_TOT_DIST;
		}
		return yAxisTitle;
	}
	
	@Override
	protected Double[] getValue(final OmegaSegment segment, final OmegaROI roi) {
		List<Double[]> valuesList = null;
		Double[] localValues = null;
		List<Double> values = null;
		final Double[] value = new Double[1];
		int index = -1;
		switch (this.mobilityOption) {
			case TMMGraphPanel.OPTION_DISPLACEMENT:
				values = this.displacementMap.get(segment);
				index = roi.getFrameIndex() - 1;
				value[0] = values.get(index);
				break;
			case TMMGraphPanel.OPTION_MAX_DISPLACEMENT:
				value[0] = this.maxDisplacementMap.get(segment);
				break;
			case TMMGraphPanel.OPTION_TOTAL_TIME_TRAVELED:
				value[0] = Double.valueOf(this.totalTimeTraveledMap
				        .get(segment));
				break;
			case TMMGraphPanel.OPTION_CONFINEMENT_RATIO:
				values = this.confinementRatioMap.get(segment);
				index = roi.getFrameIndex() - 1;
				value[0] = values.get(index);
				break;
			case TMMGraphPanel.OPTION_LOCAL_ANGLES:
				valuesList = this.anglesAndDirectionalChangesMap.get(segment);
				localValues = null;
				index = roi.getFrameIndex() - 1;
				localValues = valuesList.get(index);
				value[0] = localValues[0];
				break;
			case TMMGraphPanel.OPTION_LOCAL_DIRECTIONAL_CHANGES:
				valuesList = this.anglesAndDirectionalChangesMap.get(segment);
				index = roi.getFrameIndex() - 1;
				localValues = valuesList.get(index);
				value[0] = localValues[1];
				break;
			default:
				values = this.distanceMap.get(segment);
				index = roi.getFrameIndex() - 1;
				value[0] = values.get(index);
		}
		return value;
	}
	
	@Override
	public void updateStatus(final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TMMGraphProducer.this.mobilityPanel.updateStatus(
					        TMMGraphProducer.this.getCompleted(), ended,
					        TMMGraphProducer.this.graphPanel);
				}
			});
		} catch (final InvocationTargetException | InterruptedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex, true);
		}
	}
	
	public JPanel getGraph() {
		return this.graphPanel;
	}
}
