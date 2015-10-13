package edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.runnable;

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
import edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.TMDConstants;
import edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.gui.TMDGraphPanel;

public class TMDGraphProducer extends StatsGraphProducer {

	private final TMDGraphPanel diffusivityPanel;

	private final int diffusivityOption;
	private final int maxT;
	private final Map<OmegaSegment, Double[]> nyMap;
	private final Map<OmegaSegment, Double[][]> logMuMap;
	private final Map<OmegaSegment, Double[][]> muMap;
	private final Map<OmegaSegment, Double[][]> logDeltaTMap;
	private final Map<OmegaSegment, Double[][]> deltaTMap;
	private final Map<OmegaSegment, Double[][]> gammaDFromLogMap;
	private final Map<OmegaSegment, Double[][]> gammaDMap;
	private final Map<OmegaSegment, Double[]> gammaFromLogMap;
	private final Map<OmegaSegment, Double[]> gammaMap;
	private final Map<OmegaSegment, Double[]> smssFromLogMap;
	private final Map<OmegaSegment, Double[]> smssMap;

	private JPanel graphPanel;

	public TMDGraphProducer(final TMDGraphPanel diffusivityPanel,
	        final int graphType, final int diffusivityOption, final int tMax,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final OmegaSegmentationTypes segmTypes,
			final Map<OmegaSegment, Double[]> nyMap,
			final Map<OmegaSegment, Double[][]> muMap,
			final Map<OmegaSegment, Double[][]> logMuMap,
			final Map<OmegaSegment, Double[][]> deltaTMap,
			final Map<OmegaSegment, Double[][]> logDeltaTMap,
			final Map<OmegaSegment, Double[][]> gammaDMap,
			final Map<OmegaSegment, Double[][]> gammaDLogMap,
			final Map<OmegaSegment, Double[]> gammaMap,
			final Map<OmegaSegment, Double[]> gammaLogMap,
			final Map<OmegaSegment, Double[]> smssMap,
			final Map<OmegaSegment, Double[]> smssLogMap) {
		super(graphType, segmentsMap, segmTypes);
		this.diffusivityPanel = diffusivityPanel;
		this.diffusivityOption = diffusivityOption;
		this.maxT = tMax;
		this.nyMap = nyMap;
		this.logMuMap = logMuMap;
		this.muMap = muMap;
		this.logDeltaTMap = logDeltaTMap;
		this.deltaTMap = deltaTMap;
		this.gammaDFromLogMap = gammaDLogMap;
		this.gammaDMap = gammaDMap;
		this.gammaFromLogMap = gammaLogMap;
		this.gammaMap = gammaMap;
		this.smssFromLogMap = smssLogMap;
		this.smssMap = smssMap;
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
		switch (this.diffusivityOption) {
		default:
			this.graphPanel = this.prepareTracksGraph(false);
		}

		this.updateStatus(true);
	}

	@Override
	public String getTitle() {
		String title = "";
		switch (this.diffusivityOption) {
		case TMDGraphPanel.OPTION_TRACK_SLOPE_MSS:
			title = TMDConstants.GRAPH_NAME_MSS;
			break;
		case TMDGraphPanel.OPTION_TRACK_D:
			title = TMDConstants.GRAPH_NAME_DIFF;
			break;
		case TMDGraphPanel.OPTION_TRACK_ERROR_D:
			title = TMDConstants.GRAPH_NAME_UNCERT_D;
			break;
		case TMDGraphPanel.OPTION_TRACK_ERROR_SMSS:
			title = TMDConstants.GRAPH_NAME_UNCERT_SMSS;
			break;
		default:
			title = TMDConstants.GRAPH_NAME_MSD;
		}
		return title;
	}

	@Override
	public String getYAxisTitle() {
		String yAxisTitle;
		switch (this.diffusivityOption) {
		case TMDGraphPanel.OPTION_TRACK_SLOPE_MSS:
			yAxisTitle = TMDConstants.GRAPH_LAB_Y_MSS;
			break;
		case TMDGraphPanel.OPTION_TRACK_D:
			yAxisTitle = TMDConstants.GRAPH_LAB_Y_DIFF;
			break;
		case TMDGraphPanel.OPTION_TRACK_ERROR_D:
			yAxisTitle = TMDConstants.GRAPH_LAB_Y_UNCERT_D;
			break;
		case TMDGraphPanel.OPTION_TRACK_ERROR_SMSS:
			yAxisTitle = TMDConstants.GRAPH_LAB_Y_UNCERT_SMSS;
			break;
		default:
			yAxisTitle = TMDConstants.GRAPH_LAB_Y_MSD;
		}
		return yAxisTitle;
	}

	@Override
	protected Double[] getValue(final OmegaSegment segment, final OmegaROI roi) {
		Double[][] array = null;
		Double[] values = null;
		final Double[] value = new Double[1];
		if (!this.nyMap.containsKey(segment))
			return null;
		switch (this.diffusivityOption) {
		case TMDGraphPanel.OPTION_TRACK_SLOPE_MSS:
			values = this.smssFromLogMap.get(segment);
			value[0] = values[0];
			break;
		case TMDGraphPanel.OPTION_TRACK_D:
			array = this.gammaDFromLogMap.get(segment);
			values = array[2];
			value[0] = values[3];
			break;
			// case TMDGraphPanel.OPTION_TRACK_ERROR_SMSS:
			// value = this.meanVelocityMap.get(track);
			// break;
			// case TMDGraphPanel.OPTION_TRACK_ERROR_D:
			// value = this.meanVelocityMap.get(track);
			// break;
		default:
			array = this.gammaDFromLogMap.get(segment);
			values = array[2];
			value[0] = values[0];
		}
		return value;
	}

	@Override
	public void updateStatus(final boolean ended) {
		if (this.isTerminated())
			return;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TMDGraphProducer.this.diffusivityPanel.updateStatus(
					        TMDGraphProducer.this.getCompleted(), ended,
					        TMDGraphProducer.this.graphPanel);
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
