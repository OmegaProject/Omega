package edu.umassmed.omega.trackingMeasuresIntensityPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.StatsConstants;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.runnable.StatsGraphProducer;
import edu.umassmed.omega.trackingMeasuresIntensityPlugin.gui.TMIGraphPanel;

public class TMIGraphProducer extends StatsGraphProducer {
	
	private final TMIGraphPanel intensityGraphPanel;
	
	private final int peakMeanBgSnrOption, minMeanMaxOption;
	private final boolean isTimepointsGraph;
	private final int maxT;
	
	private final Map<OmegaSegment, Double[]> peakSignalsMap;
	private final Map<OmegaSegment, Double[]> centroidSignalsMap;
	
	private final Map<OmegaROI, Double> peakSignalsLocMap;
	private final Map<OmegaROI, Double> centroidSignalsLocMap;

	// SNR related START
	private final Map<OmegaSegment, Double[]> meanSignalsMap;
	private final Map<OmegaSegment, Double[]> backgroundsMap;
	private final Map<OmegaSegment, Double[]> noisesMap;
	private final Map<OmegaSegment, Double[]> areasMap;
	private final Map<OmegaSegment, Double[]> snrsMap;
	
	private final Map<OmegaROI, Double> meanSignalsLocMap;
	private final Map<OmegaROI, Double> backgroundsLocMap;
	private final Map<OmegaROI, Double> noisesLocMap;
	private final Map<OmegaROI, Double> areasLocMap;
	private final Map<OmegaROI, Double> snrsLocMap;
	// SNR related END
	
	private boolean itsLocal;
	
	public TMIGraphProducer(final TMIGraphPanel intensityGraphPanel,
			final int graphType, final int peakMeanBgSnrOption,
			final int minMeanMaxOption, final boolean isTimepointsGraph,
			final int maxT,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final OmegaSegmentationTypes segmTypes,
			final Map<OmegaSegment, Double[]> peakSignalsMap,
			final Map<OmegaSegment, Double[]> centroidSignalsMap,
			final Map<OmegaROI, Double> peakSignalsLocMap,
			final Map<OmegaROI, Double> centroidSignalsLocMap,
			final Map<OmegaSegment, Double[]> meanSignalsMap,
			final Map<OmegaSegment, Double[]> backgroundsMap,
			final Map<OmegaSegment, Double[]> noisesMap,
			final Map<OmegaSegment, Double[]> areasMap,
			final Map<OmegaSegment, Double[]> snrsMap,
			final Map<OmegaROI, Double> meanSignalsLocMap,
			final Map<OmegaROI, Double> backgroundsLocMap,
			final Map<OmegaROI, Double> noisesLocMap,
			final Map<OmegaROI, Double> areasLocMap,
			final Map<OmegaROI, Double> snrsLocMap) {
		super(graphType, segmentsMap, segmTypes);
		this.intensityGraphPanel = intensityGraphPanel;
		this.peakMeanBgSnrOption = peakMeanBgSnrOption;
		this.minMeanMaxOption = minMeanMaxOption;
		this.isTimepointsGraph = isTimepointsGraph;
		this.maxT = maxT;
		this.peakSignalsMap = peakSignalsMap;
		this.centroidSignalsMap = centroidSignalsMap;
		this.peakSignalsLocMap = peakSignalsLocMap;
		this.centroidSignalsLocMap = centroidSignalsLocMap;
		this.meanSignalsMap = meanSignalsMap;
		this.backgroundsMap = backgroundsMap;
		this.noisesMap = noisesMap;
		this.areasMap = areasMap;
		this.snrsMap = snrsMap;
		this.meanSignalsLocMap = meanSignalsLocMap;
		this.backgroundsLocMap = backgroundsLocMap;
		this.noisesLocMap = noisesLocMap;
		this.areasLocMap = areasLocMap;
		this.snrsLocMap = snrsLocMap;
		
		this.itsLocal = true;
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
			case TMIGraphPanel.OPTION_BACKGROUND:
				title += StatsConstants.GRAPH_NAME_BACKGROUND;
				break;
			case TMIGraphPanel.OPTION_SNR:
				title += StatsConstants.GRAPH_NAME_SNR;
				break;
			case TMIGraphPanel.OPTION_AREA:
				title += StatsConstants.GRAPH_NAME_AREA;
				break;
			case TMIGraphPanel.OPTION_NOISE:
				title += StatsConstants.GRAPH_NAME_NOISE;
				break;
			case TMIGraphPanel.OPTION_MEAN_SIGNAL:
				title += StatsConstants.GRAPH_NAME_INT_MEAN;
				break;
			case TMIGraphPanel.OPTION_CENTROID_SIGNAL:
				title += StatsConstants.GRAPH_NAME_INT_CENT;
				break;
			default:
				title += StatsConstants.GRAPH_NAME_INT_PEAK;
		}
		return title;
	}
	
	@Override
	public String getYAxisTitle() {
		return StatsConstants.GRAPH_LAB_Y_INT;
	}
	
	@Override
	protected Double[] getValue(final OmegaSegment segment, final OmegaROI roi) {
		Double[] values = null;
		final Double[] value = new Double[1];
		value[0] = null;
		if (!this.isTimepointsGraph) {
			switch (this.peakMeanBgSnrOption) {
				case TMIGraphPanel.OPTION_BACKGROUND:
					values = this.backgroundsMap.get(segment);
					break;
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
					values = this.peakSignalsMap.get(segment);
			}
			if (values != null) {
				value[0] = values[this.minMeanMaxOption];
			}
		} else {
			switch (this.peakMeanBgSnrOption) {
				case TMIGraphPanel.OPTION_BACKGROUND:
					value[0] = this.backgroundsLocMap.get(roi);
					break;
				case TMIGraphPanel.OPTION_AREA:
					value[0] = this.areasLocMap.get(roi);
					break;
				case TMIGraphPanel.OPTION_SNR:
					value[0] = this.snrsLocMap.get(roi);
					break;
				case TMIGraphPanel.OPTION_NOISE:
					value[0] = this.noisesLocMap.get(roi);
					break;
				case TMIGraphPanel.OPTION_MEAN_SIGNAL:
					value[0] = this.meanSignalsLocMap.get(roi);
					break;
				case TMIGraphPanel.OPTION_CENTROID_SIGNAL:
					value[0] = this.centroidSignalsLocMap.get(roi);
					break;
				default:
					value[0] = this.peakSignalsLocMap.get(roi);
			}
		}
		return value;
	}
	
	@Override
	public void updateStatus(final boolean ended) {
		if (this.itsLocal) {
			this.intensityGraphPanel.updateStatus(this.getCompleted(), ended);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						TMIGraphProducer.this.intensityGraphPanel.updateStatus(
								TMIGraphProducer.this.getCompleted(), ended);
					}
				});
			} catch (final InvocationTargetException | InterruptedException ex) {
				OmegaLogFileManager.handleUncaughtException(ex, true);
			}
		}
	}
}
