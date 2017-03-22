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
import edu.umassmed.omega.trackingMeasuresDiffusivityPlugin.gui.TMDMotionTypeClassificationGraphPanel;

public class TMDMotionTypeClassificationGraphProducer extends
StatsGraphProducer implements Runnable {

	private final static int TRACK = 0;
	private final static int MSD = 1;
	private final static int MSS = 2;
	private final static int PHASE = 3;

	private final TMDMotionTypeClassificationGraphPanel motionTypeClassificationPanel;

	private final int motionTypeOption, showOption;
	private int currentGraph;
	private int countCurrentGraph;
	// private final OmegaTrajectory track;
	private final Map<OmegaSegment, Double[]> nyMap;
	private final Map<OmegaSegment, Double[][]> logMuMap;
	private final Map<OmegaSegment, Double[][]> muMap;
	private final Map<OmegaSegment, Double[][]> logDeltaTMap;
	private final Map<OmegaSegment, Double[][]> deltaTMap;
	private final Map<OmegaSegment, Double[][]> gammaDFromLogMap;
	private final Map<OmegaSegment, Double[][]> gammaDMap;
	private final Map<OmegaSegment, Double[]> gammaFromLogMap;
	// private final Map<OmegaSegment, Double[]> gammaMap;
	private final Map<OmegaSegment, Double[]> smssFromLogMap;
	// private final Map<OmegaSegment, Double[]> smssMap;
	// private final Map<OmegaSegment, Double[]> errorMap;
	private final Map<OmegaSegment, Double[]> errorFromLogMap;

	private final JPanel[] chartPanels, legendPanels;

	boolean itsLocal;

	public TMDMotionTypeClassificationGraphProducer(
			final TMDMotionTypeClassificationGraphPanel motionTypeClassificationPanel,
			final int motionTypeOption, final int showOption,
			final Map<OmegaTrajectory, List<OmegaSegment>> segments,
			final OmegaSegmentationTypes segmTypes,
			final Map<OmegaSegment, Double[]> nyMap,
			final Map<OmegaSegment, Double[][]> muMap,
			final Map<OmegaSegment, Double[][]> logMuMap,
			final Map<OmegaSegment, Double[][]> deltaTMap,
			final Map<OmegaSegment, Double[][]> logDeltaTMap,
			final Map<OmegaSegment, Double[][]> gammaDMap,
			final Map<OmegaSegment, Double[][]> gammaDLogMap,
			// final Map<OmegaSegment, Double[]> gammaMap,
			final Map<OmegaSegment, Double[]> gammaLogMap,
			// final Map<OmegaSegment, Double[]> smssMap,
			final Map<OmegaSegment, Double[]> smssLogMap,
			// final Map<OmegaSegment, Double[]> errorMap,
			final Map<OmegaSegment, Double[]> errorLogMap) {
		super(StatsGraphProducer.LINE_GRAPH, segments, segmTypes);
		this.motionTypeClassificationPanel = motionTypeClassificationPanel;
		this.motionTypeOption = motionTypeOption;
		this.showOption = showOption;
		this.currentGraph = -1;
		this.countCurrentGraph = 0;
		// this.track = track;
		this.nyMap = nyMap;
		this.logMuMap = logMuMap;
		this.muMap = muMap;
		this.logDeltaTMap = logDeltaTMap;
		this.deltaTMap = deltaTMap;
		this.gammaDFromLogMap = gammaDLogMap;
		this.gammaDMap = gammaDMap;
		this.gammaFromLogMap = gammaLogMap;
		// this.gammaMap = gammaMap;
		this.smssFromLogMap = smssLogMap;
		// this.smssMap = smssMap;
		// this.errorMap = errorMap;
		this.errorFromLogMap = errorLogMap;
		this.chartPanels = new JPanel[4];
		this.legendPanels = new JPanel[4];

		this.itsLocal = true;
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
		this.itsLocal = false;
		this.doRun();
	}

	@Override
	public void doRun() {
		super.doRun();
		switch (this.showOption) {
			case TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_TRACK_ONLY:
				this.currentGraph = TMDMotionTypeClassificationGraphProducer.TRACK;
				this.prepareTrackGraph();
				this.chartPanels[0] = this.getGraphPanel();
				this.legendPanels[0] = this.getGraphLegendPanel();
				break;
			case TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_MSD_ONLY:
				this.currentGraph = TMDMotionTypeClassificationGraphProducer.MSD;
				switch (this.motionTypeOption) {
					case TMDMotionTypeClassificationGraphPanel.OPTION_LOG:
						this.prepareMSDGraph();
						this.chartPanels[1] = this.getGraphPanel();
						this.legendPanels[1] = this.getGraphLegendPanel();
					default:
						this.prepareMSDGraph();
						this.chartPanels[1] = this.getGraphPanel();
						this.legendPanels[1] = this.getGraphLegendPanel();
				}
				break;
			case TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_MSS_ONLY:
				this.currentGraph = TMDMotionTypeClassificationGraphProducer.MSS;
				switch (this.motionTypeOption) {
					case TMDMotionTypeClassificationGraphPanel.OPTION_LOG:
						this.prepareMSSGraph();
						this.chartPanels[2] = this.getGraphPanel();
						this.legendPanels[2] = this.getGraphLegendPanel();
						break;
					default:
						this.prepareMSSGraph();
						this.chartPanels[2] = this.getGraphPanel();
						this.legendPanels[2] = this.getGraphLegendPanel();
				}
				break;
			case TMDMotionTypeClassificationGraphPanel.OPTION_SHOW_PHASE_ONLY:
				this.currentGraph = TMDMotionTypeClassificationGraphProducer.PHASE;
				switch (this.motionTypeOption) {
					case TMDMotionTypeClassificationGraphPanel.OPTION_LOG:
						this.prepareSMSSvsDGraph();
						this.chartPanels[3] = this.getGraphPanel();
						this.legendPanels[3] = this.getGraphLegendPanel();
						break;
					default:
						// this.prepareMSSGraph();
				}
				break;
			default:
				switch (this.motionTypeOption) {
					case TMDMotionTypeClassificationGraphPanel.OPTION_LOG:
						this.currentGraph = TMDMotionTypeClassificationGraphProducer.TRACK;
						this.prepareTrackGraph();
						this.chartPanels[0] = this.getGraphPanel();
						this.legendPanels[0] = this.getGraphLegendPanel();
						this.currentGraph = TMDMotionTypeClassificationGraphProducer.MSD;
						this.prepareMSDGraph();
						this.chartPanels[1] = this.getGraphPanel();
						this.legendPanels[1] = this.getGraphLegendPanel();
						this.currentGraph = TMDMotionTypeClassificationGraphProducer.MSS;
						this.prepareMSSGraph();
						this.chartPanels[2] = this.getGraphPanel();
						this.legendPanels[2] = this.getGraphLegendPanel();
						this.currentGraph = TMDMotionTypeClassificationGraphProducer.PHASE;
						this.prepareSMSSvsDGraph();
						this.chartPanels[3] = this.getGraphPanel();
						this.legendPanels[3] = this.getGraphLegendPanel();

						break;
					default:
						this.currentGraph = TMDMotionTypeClassificationGraphProducer.TRACK;
						this.prepareTrackGraph();
						this.chartPanels[0] = this.getGraphPanel();
						this.legendPanels[0] = this.getGraphLegendPanel();
						this.currentGraph = TMDMotionTypeClassificationGraphProducer.MSD;
						this.prepareMSDGraph();
						this.chartPanels[1] = this.getGraphPanel();
						this.legendPanels[1] = this.getGraphLegendPanel();
						this.currentGraph = TMDMotionTypeClassificationGraphProducer.MSS;
						this.prepareMSSGraph();
						this.chartPanels[2] = this.getGraphPanel();
						this.legendPanels[2] = this.getGraphLegendPanel();
						// this.prepareSMSSvsDFromLogGraph();
				}
		}

		this.updateStatus(true);
	}

	@Override
	public void updateStatus(final boolean ended) {
		if (this.itsLocal) {
			this.motionTypeClassificationPanel.updateStatus(
			        this.getCompleted(), ended);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						TMDMotionTypeClassificationGraphProducer.this.motionTypeClassificationPanel
						.updateStatus(
								TMDMotionTypeClassificationGraphProducer.this
								.getCompleted(), ended);
					}
				});
			} catch (final InvocationTargetException | InterruptedException ex) {
				OmegaLogFileManager.handleUncaughtException(ex, true);
			}
		}
	}

	public JPanel[] getGraphs() {
		return this.chartPanels;
	}

	public JPanel[] getLegends() {
		return this.legendPanels;
	}

	@Override
	protected Double[] getValue(final OmegaSegment segment, final OmegaROI roi) {
		Double[] value = null;
		switch (this.currentGraph) {
			case MSD:
				final Map<OmegaSegment, Double[][]> muMap;
				final Map<OmegaSegment, Double[][]> deltaTMap;
				switch (this.motionTypeOption) {
					case TMDMotionTypeClassificationGraphPanel.OPTION_LOG:
						muMap = this.logMuMap;
						deltaTMap = this.logDeltaTMap;
						break;
					default:
						muMap = this.muMap;
						deltaTMap = this.deltaTMap;
				}
				if ((muMap != null) && (deltaTMap != null)
						&& muMap.containsKey(segment)
						&& deltaTMap.containsKey(segment)) {
					if (this.countCurrentGraph == 0) {
						final Double[] msd = muMap.get(segment)[2];
						value = msd;
					} else {
						final Double[] deltaT = deltaTMap.get(segment)[2];
						value = deltaT;
					}
					this.countCurrentGraph++;
					if (this.countCurrentGraph > 1) {
						this.countCurrentGraph = 0;
					}
				}
				break;
			case MSS:
				final Map<OmegaSegment, Double[]> gammaMap;
				switch (this.motionTypeOption) {
					case TMDMotionTypeClassificationGraphPanel.OPTION_LOG:
						gammaMap = this.gammaFromLogMap;
						break;
					default:
						gammaMap = null;
				}
				if ((gammaMap != null) && gammaMap.containsKey(segment)
						&& (this.nyMap != null)
						&& this.nyMap.containsKey(segment)) {
					if (this.countCurrentGraph == 0) {
						final Double[] gamma = gammaMap.get(segment);
						value = gamma;
					} else {
						final Double[] ny = this.nyMap.get(segment);
						value = ny;
					}
					this.countCurrentGraph++;
					if (this.countCurrentGraph > 1) {
						this.countCurrentGraph = 0;
					}
				} else
					return null;
				break;
			case PHASE:
				final Map<OmegaSegment, Double[][]> gammaDMap;
				final Map<OmegaSegment, Double[]> smssMap;
				final Map<OmegaSegment, Double[]> errorMap;
				switch (this.motionTypeOption) {
					case TMDMotionTypeClassificationGraphPanel.OPTION_LOG:
						gammaDMap = this.gammaDFromLogMap;
						smssMap = this.smssFromLogMap;
						errorMap = this.errorFromLogMap;
						break;
					default:
						gammaDMap = this.gammaDMap;
						smssMap = null;
						errorMap = null;
				}
				value = new Double[4];
				if ((gammaDMap != null) && gammaDMap.containsKey(segment)
				        && (smssMap != null) && smssMap.containsKey(segment)) {
					final Double d = gammaDMap.get(segment)[2][3];
					final Double smss = smssMap.get(segment)[0];
					Double dError = 0.0;
					Double smssError = 0.0;
					if ((errorMap != null) && !errorMap.isEmpty()) {
						dError = errorMap.get(segment)[0];
						smssError = errorMap.get(segment)[1];
					}
					value[0] = d;
					value[1] = smss;
					value[2] = dError;
					value[3] = smssError;
					// final double dMinus = d - deltaD;
					// final double dPlus = d + deltaD;
					// final double smssMinus = smss - deltaSMSS;
					// final double smssPlus = smss + deltaSMSS;
				} else
					return null;
				break;
			default:
		}
		return value;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getYAxisTitle() {
		return null;
	}
}
