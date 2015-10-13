package edu.umassmed.omega.trackingMeasuresIntensityPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaParticle;
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

	private final Map<OmegaSegment, Double[]> peakSignalmaMap;
	private final Map<OmegaSegment, Double[]> meanSignalMap;
	private final Map<OmegaSegment, Double[]> localBackgroundMap;
	private final Map<OmegaSegment, Double[]> localSNRMap;

	private JPanel graphPanel;

	public TMIGraphProducer(final TMIGraphPanel intensityGraphPanel,
			final int graphType, final int peakMeanBgSnrOption,
			final int minMeanMaxOption, final int maxT,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final OmegaSegmentationTypes segmTypes,
			final Map<OmegaSegment, Double[]> peakSignalMap,
			final Map<OmegaSegment, Double[]> meanSignalMap,
			final Map<OmegaSegment, Double[]> localBackgroundMap,
			final Map<OmegaSegment, Double[]> localSNRMap) {
		super(graphType, segmentsMap, segmTypes);
		this.intensityGraphPanel = intensityGraphPanel;
		this.peakMeanBgSnrOption = peakMeanBgSnrOption;
		this.minMeanMaxOption = minMeanMaxOption;
		this.isTimepointsGraph = true;
		this.maxT = maxT;
		this.peakSignalmaMap = peakSignalMap;
		this.meanSignalMap = meanSignalMap;
		this.localBackgroundMap = localBackgroundMap;
		this.localSNRMap = localSNRMap;
	}

	public TMIGraphProducer(final TMIGraphPanel intensityGraphPanel,
			final int graphType, final int peakMeanBgSnrOption, final int maxT,
			final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
			final OmegaSegmentationTypes segmTypes) {
		super(graphType, segmentsMap, segmTypes);
		this.intensityGraphPanel = intensityGraphPanel;
		this.peakMeanBgSnrOption = peakMeanBgSnrOption;
		this.minMeanMaxOption = -1;
		this.isTimepointsGraph = true;
		this.maxT = maxT;
		this.peakSignalmaMap = null;
		this.meanSignalMap = null;
		this.localBackgroundMap = null;
		this.localSNRMap = null;
	}

	@Override
	public void run() {
		super.run();
		if (this.isTimepointsGraph) {
			this.graphPanel = this.prepareTimepointsGraph(this.maxT);
		} else {
			this.graphPanel = this.prepareTracksGraph(false);
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
			title += "Mean ";
			break;
		case TMIGraphPanel.OPTION_MAX:
			title += "Max ";
			break;
		default:
			break;
		}
		switch (this.peakMeanBgSnrOption) {
		case TMIGraphPanel.OPTION_MEAN_SIGNAL:
			title += "Mean Intensity";
			break;
		case TMIGraphPanel.OPTION_LOCAL_BACKGROUND:
			title += "Background";
			break;
		case TMIGraphPanel.OPTION_LOCAL_SNR:
			title += "SNR";
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
		if (roi != null) {
			final OmegaParticle particle = (OmegaParticle) roi;
			switch (this.peakMeanBgSnrOption) {
			case TMIGraphPanel.OPTION_MEAN_SIGNAL:
				value[0] = particle.getMeanSignal();
			case TMIGraphPanel.OPTION_LOCAL_BACKGROUND:
				value[0] = particle.getMeanBackground();
			case TMIGraphPanel.OPTION_LOCAL_SNR:
				value[0] = particle.getSNR();
			default:
				value[0] = particle.getPeakSignal();
			}
		} else {
			switch (this.peakMeanBgSnrOption) {
			case TMIGraphPanel.OPTION_MEAN_SIGNAL:
				values = this.meanSignalMap.get(segment);
				break;
			case TMIGraphPanel.OPTION_LOCAL_BACKGROUND:
				values = this.localBackgroundMap.get(segment);
				break;
			case TMIGraphPanel.OPTION_LOCAL_SNR:
				values = this.localSNRMap.get(segment);
				break;
			default:
				values = this.peakSignalmaMap.get(segment);
			}
			value[0] = values[this.minMeanMaxOption];
		}
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
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	public JPanel getGraph() {
		return this.graphPanel;
	}
}
