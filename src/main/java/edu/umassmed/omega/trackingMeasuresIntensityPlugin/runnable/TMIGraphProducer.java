package edu.umassmed.omega.trackingMeasuresIntensityPlugin.runnable;

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
import edu.umassmed.omega.trackingMeasuresIntensityPlugin.TMIConstants;
import edu.umassmed.omega.trackingMeasuresIntensityPlugin.gui.TMIGraphPanel;

public class TMIGraphProducer extends StatsGraphProducer {

	private final TMIGraphPanel intensityGraphPanel;

	private final int peakMeanBgSnrOption, minMeanMaxOption;
	private final boolean isTimepointsGraph;
	private final int maxT;

	private final Map<OmegaSegment, Double[]> peakSignalMap;
	private final Map<OmegaSegment, Double[]> centroidSignalsMap;
	
	// SNR related START
	private final Map<OmegaSegment, Double[]> meanSignalsMap;
	private final Map<OmegaSegment, Double[]> noisesMap;
	private final Map<OmegaSegment, Double[]> areasMap;
	private final Map<OmegaSegment, Double[]> snrsMap;
	// SNR related END

	private JPanel graphPanel;

	public TMIGraphProducer(final TMIGraphPanel intensityGraphPanel,
	        final int graphType, final int peakMeanBgSnrOption,
	        final int minMeanMaxOption, final int maxT,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	        final OmegaSegmentationTypes segmTypes,
	        final Map<OmegaSegment, Double[]> peakSignalMap,
	        final Map<OmegaSegment, Double[]> centroidSignalsMap,
	        final Map<OmegaSegment, Double[]> meanSignalsMap,
	        final Map<OmegaSegment, Double[]> noisesMap,
	        final Map<OmegaSegment, Double[]> areasMap,
	        final Map<OmegaSegment, Double[]> snrsMap, final boolean isTimePoint) {
		super(graphType, segmentsMap, segmTypes);
		this.intensityGraphPanel = intensityGraphPanel;
		this.peakMeanBgSnrOption = peakMeanBgSnrOption;
		this.minMeanMaxOption = minMeanMaxOption;
		this.isTimepointsGraph = isTimePoint;
		this.maxT = maxT;
		this.peakSignalMap = peakSignalMap;
		this.centroidSignalsMap = centroidSignalsMap;
		this.meanSignalsMap = meanSignalsMap;
		this.noisesMap = noisesMap;
		this.areasMap = areasMap;
		this.snrsMap = snrsMap;
	}

	// public TMIGraphProducer(final TMIGraphPanel intensityGraphPanel,
	// final int graphType, final int peakMeanBgSnrOption, final int maxT,
	// final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	// final OmegaSegmentationTypes segmTypes) {
	// super(graphType, segmentsMap, segmTypes);
	// this.intensityGraphPanel = intensityGraphPanel;
	// this.peakMeanBgSnrOption = peakMeanBgSnrOption;
	// this.minMeanMaxOption = -1;
	// this.isTimepointsGraph = true;
	// this.maxT = maxT;
	// this.peakSignalMap = null;
	// this.centroidSignalsMap = null;
	// this.meanSignalsMap = null;
	// this.noisesMap = null;
	// this.areasMap = null;
	// this.snrsMap = null;
	// }

	@Override
	public void run() {
		super.run();
		if (this.isTimepointsGraph) {
			this.graphPanel = this.prepareTimepointsGraph(this.maxT);
		} else {
			this.graphPanel = this.prepareTracksGraph(true);
		}
		this.updateStatus(true);
	}

	@Override
	public String getTitle() {
		String title = "";
		switch (this.minMeanMaxOption) {
			case TMIGraphPanel.OPTION_MIN:
				title += "Min ";
				break;
			case TMIGraphPanel.OPTION_MEAN:
				title += "Avg ";
				break;
			case TMIGraphPanel.OPTION_MAX:
				title += "Max ";
				break;
			default:
				break;
		}
		switch (this.peakMeanBgSnrOption) {
			case TMIGraphPanel.OPTION_SNR:
				title += TMIConstants.GRAPH_NAME_SNR;
				break;
			case TMIGraphPanel.OPTION_AREA:
				title += TMIConstants.GRAPH_NAME_AREA;
				break;
			case TMIGraphPanel.OPTION_NOISE:
				title += TMIConstants.GRAPH_NAME_NOISE;
				break;
			case TMIGraphPanel.OPTION_MEAN_SIGNAL:
				title += TMIConstants.GRAPH_NAME_INT_MEAN;
				break;
			case TMIGraphPanel.OPTION_CENTROID_SIGNAL:
				title += TMIConstants.GRAPH_NAME_INT_CENT;
				break;
			default:
				title += TMIConstants.GRAPH_NAME_INT_PEAK;
		}
		return title;
	}

	@Override
	public String getYAxisTitle() {
		return TMIConstants.GRAPH_LAB_Y_INT;
	}

	@Override
	protected Double[] getValue(final OmegaSegment segment, final OmegaROI roi) {
		Double[] values = null;
		final Double[] value = new Double[1];
		switch (this.peakMeanBgSnrOption) {
			case TMIGraphPanel.OPTION_AREA:
				values = this.areasMap.get(segment);
				break;
			case TMIGraphPanel.OPTION_SNR:
				values = this.snrsMap.get(segment);
				break;
			case TMIGraphPanel.OPTION_NOISE:
				values = this.noisesMap.get(segment);
				break;
			case TMIGraphPanel.OPTION_MEAN_SIGNAL:
				values = this.meanSignalsMap.get(segment);
				break;
			case TMIGraphPanel.OPTION_CENTROID_SIGNAL:
				values = this.centroidSignalsMap.get(segment);
				break;
			default:
				values = this.peakSignalMap.get(segment);
		}
		value[0] = values[this.minMeanMaxOption];
		return value;
	}

	@Override
	public void updateStatus(final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TMIGraphProducer.this.intensityGraphPanel.updateStatus(
					        TMIGraphProducer.this.getCompleted(), ended,
					        TMIGraphProducer.this.graphPanel);
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
