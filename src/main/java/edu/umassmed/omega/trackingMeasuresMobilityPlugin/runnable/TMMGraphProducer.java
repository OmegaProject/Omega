package edu.umassmed.omega.trackingMeasuresMobilityPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.StatsConstants;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.runnable.StatsGraphProducer;
import edu.umassmed.omega.trackingMeasuresMobilityPlugin.gui.TMMGraphPanel;

public class TMMGraphProducer extends StatsGraphProducer {

	private final TMMGraphPanel mobilityPanel;

	private final int mobilityOption;
	private final boolean isTimepointsGraph;
	private final int maxT;
	private final Map<OmegaSegment, List<Double>> distanceMap;
	private final Map<OmegaSegment, List<Double>> distanceFromOriginMap;
	private final Map<OmegaSegment, List<Double>> displacementFromOriginMap;
	private final Map<OmegaSegment, Double> maxDisplacementFromOriginMap;
	private final Map<OmegaSegment, List<Double>> timeTraveledMap;
	private final Map<OmegaSegment, List<Double>> confinementRatioMap;
	private final Map<OmegaSegment, List<Double[]>> anglesAndDirectionalChangesMap;

	private JPanel graphPanel;

	private boolean itsLocal;

	public TMMGraphProducer(
			final TMMGraphPanel mobilityPanel,
			final int graphType,
			final int mobilityOption,
			final boolean isTimepointsGraph,
			final int tMax,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final OmegaSegmentationTypes segmTypes,
			final Map<OmegaSegment, List<Double>> distanceMap,
			final Map<OmegaSegment, List<Double>> distanceFromOriginMap,
			final Map<OmegaSegment, List<Double>> displacementFromOriginMap,
			final Map<OmegaSegment, Double> maxDisplacementFromOriginMap,
			final Map<OmegaSegment, List<Double>> timeTraveledMap,
			final Map<OmegaSegment, List<Double>> confinementRatioMap,
			final Map<OmegaSegment, List<Double[]>> anglesAndDirectionalChangesMap) {
		super(graphType, segmentsMap, segmTypes);
		this.mobilityPanel = mobilityPanel;
		this.mobilityOption = mobilityOption;
		this.isTimepointsGraph = isTimepointsGraph;
		this.maxT = tMax;
		this.distanceMap = distanceMap;
		this.distanceFromOriginMap = distanceFromOriginMap;
		this.displacementFromOriginMap = displacementFromOriginMap;
		this.maxDisplacementFromOriginMap = maxDisplacementFromOriginMap;
		this.timeTraveledMap = timeTraveledMap;
		this.confinementRatioMap = confinementRatioMap;
		this.anglesAndDirectionalChangesMap = anglesAndDirectionalChangesMap;
		this.graphPanel = null;
		this.itsLocal = true;
	}

	@Override
	public void run() {
		this.itsLocal = false;
		this.doRun();
	}

	@Override
	public void doRun() {
		super.doRun();
		if (this.isTimepointsGraph) {
			this.prepareTimepointsGraph(this.maxT);
		} else {
			this.prepareTracksGraph(true);
		}
		this.graphPanel = this.getGraphPanel();
		this.updateStatus(true);
	}

	@Override
	public String getTitle() {
		String title;
		switch (this.mobilityOption) {
			case TMMGraphPanel.OPTION_MAX_DISP_GLO:
				title = StatsConstants.GRAPH_NAME_MAX_DISP_GLO;
				break;
			case TMMGraphPanel.OPTION_TOT_DISP_GLO:
				title = StatsConstants.GRAPH_NAME_DISP_GLO;
				break;
			case TMMGraphPanel.OPTION_TOT_TIME_GLO:
				title = StatsConstants.GRAPH_NAME_TIME_GLO;
				break;
			case TMMGraphPanel.OPTION_CONFRATIO_GLO:
				title = StatsConstants.GRAPH_NAME_CONFRATIO_GLO;
				break;
			case TMMGraphPanel.OPTION_DIST_P2P_LOC:
				title = StatsConstants.GRAPH_NAME_DIST_P2P_LOC;
				break;
			case TMMGraphPanel.OPTION_DIST_LOC:
				title = StatsConstants.GRAPH_NAME_DIST_LOC;
				break;
			case TMMGraphPanel.OPTION_DISP_LOC:
				title = StatsConstants.GRAPH_NAME_DISP_LOC;
				break;
			case TMMGraphPanel.OPTION_CONFRATIO_LOC:
				title = StatsConstants.GRAPH_NAME_CONFRATIO_LOC;
				break;
			case TMMGraphPanel.OPTION_ANGLES_LOC:
				title = StatsConstants.GRAPH_NAME_ANGLES_LOC;
				break;
			case TMMGraphPanel.OPTION_DIRCHANGE_LOC:
				title = StatsConstants.GRAPH_NAME_DIRCHANGE_LOC;
				break;
			case TMMGraphPanel.OPTION_TIME_LOC:
				title = StatsConstants.GRAPH_NAME_TIME_LOC;
				break;
			default:
				title = StatsConstants.GRAPH_NAME_DIST_GLO;
		}
		return title;
	}

	@Override
	public String getYAxisTitle() {
		String yAxisTitle;
		switch (this.mobilityOption) {
			case TMMGraphPanel.OPTION_TOT_DISP_GLO:
			case TMMGraphPanel.OPTION_MAX_DISP_GLO:
			case TMMGraphPanel.OPTION_DISP_LOC:
				yAxisTitle = StatsConstants.GRAPH_LAB_Y_DISP;
				break;
			case TMMGraphPanel.OPTION_TOT_TIME_GLO:
			case TMMGraphPanel.OPTION_TIME_LOC:
				yAxisTitle = StatsConstants.GRAPH_LAB_Y_TOT_TIME;
				break;
			case TMMGraphPanel.OPTION_CONFRATIO_GLO:
			case TMMGraphPanel.OPTION_CONFRATIO_LOC:
				yAxisTitle = StatsConstants.GRAPH_LAB_Y_CONFRATIO;
				break;
			case TMMGraphPanel.OPTION_TOT_DIST_GLO:
			case TMMGraphPanel.OPTION_DIST_P2P_LOC:
			case TMMGraphPanel.OPTION_DIST_LOC:
				yAxisTitle = StatsConstants.GRAPH_LAB_Y_DIST;
				break;
			case TMMGraphPanel.OPTION_DIRCHANGE_LOC:
				yAxisTitle = StatsConstants.GRAPH_LAB_Y_DIRCHANGE;
				break;
			default:
				yAxisTitle = StatsConstants.GRAPH_NAME_ANGLES_LOC;
		}
		return yAxisTitle;
	}

	@Override
	protected Double[] getValue(final OmegaSegment segment, final OmegaROI roi) {
		List<Double[]> valuesList = null;
		Double[] localValues = null;
		List<Double> values = null;
		final Double[] value = new Double[1];
		value[0] = null;
		int index = -1;
		switch (this.mobilityOption) {
			case TMMGraphPanel.OPTION_DIST_P2P_LOC:
				values = this.distanceMap.get(segment);
				index = roi.getFrameIndex() - 1;
				if ((values == null) || (index >= values.size())) {
					break;
				}
				value[0] = values.get(index);
				break;
			case TMMGraphPanel.OPTION_DISP_LOC:
			case TMMGraphPanel.OPTION_TOT_DISP_GLO:
				values = this.displacementFromOriginMap.get(segment);
				index = roi.getFrameIndex() - 1;
				if ((values == null) || (index >= values.size())) {
					break;
				}
				value[0] = values.get(index);
				break;
			case TMMGraphPanel.OPTION_MAX_DISP_GLO:
				value[0] = this.maxDisplacementFromOriginMap.get(segment);
				break;
			// case TMMGraphPanel.OPTION_TOT_TIME_GLO:
			// values = this.timeTraveledMap.get(segment);
			// index = segment.getEndingROI().getFrameIndex() - 1;
			// value[0] = values.get(index);
			// break;
			case TMMGraphPanel.OPTION_TOT_TIME_GLO:
			case TMMGraphPanel.OPTION_TIME_LOC:
				values = this.timeTraveledMap.get(segment);
				index = roi.getFrameIndex() - 1;
				value[0] = values.get(index);
				break;
			case TMMGraphPanel.OPTION_CONFRATIO_GLO:
			case TMMGraphPanel.OPTION_CONFRATIO_LOC:
				values = this.confinementRatioMap.get(segment);
				index = roi.getFrameIndex() - 1;
				if ((values == null) || (index >= values.size())) {
					break;
				}
				value[0] = values.get(index);
				break;
			case TMMGraphPanel.OPTION_ANGLES_LOC:
				valuesList = this.anglesAndDirectionalChangesMap.get(segment);
				localValues = null;
				index = roi.getFrameIndex() - 1;
				if ((valuesList == null) || (index >= valuesList.size())) {
					break;
				}
				localValues = valuesList.get(index);
				value[0] = localValues[0];
				break;
			case TMMGraphPanel.OPTION_DIRCHANGE_LOC:
				valuesList = this.anglesAndDirectionalChangesMap.get(segment);
				index = roi.getFrameIndex() - 1;
				if ((valuesList == null) || (index >= valuesList.size())) {
					break;
				}
				localValues = valuesList.get(index);
				value[0] = localValues[1];
				break;
			default:
				values = this.distanceFromOriginMap.get(segment);
				index = roi.getFrameIndex() - 1;
				if ((values == null) || (index >= values.size())) {
					break;
				}
				value[0] = values.get(index);
		}
		return value;
	}

	@Override
	public void updateStatus(final boolean ended) {
		if (this.itsLocal) {
			this.mobilityPanel.updateStatus(this.getCompleted(), ended);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						TMMGraphProducer.this.mobilityPanel.updateStatus(
								TMMGraphProducer.this.getCompleted(), ended);
					}
				});
			} catch (final InvocationTargetException | InterruptedException ex) {
				OmegaLogFileManager.handleUncaughtException(ex, true);
			}
		}
	}
}
