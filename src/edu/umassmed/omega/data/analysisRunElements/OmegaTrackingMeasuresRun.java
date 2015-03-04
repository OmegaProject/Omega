package edu.umassmed.omega.data.analysisRunElements;

import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaTrackingMeasuresRun extends OmegaAnalysisRun {

	// private Map<OmegaROI, List<Double>> particlesMeasures;
	private final Map<OmegaTrajectory, Double[]> peakSignalsMap;
	private final Map<OmegaTrajectory, Double[]> meanSignalsMap;
	private final Map<OmegaTrajectory, Double[]> localBackgroundsMap;
	private final Map<OmegaTrajectory, Double[]> localSNRsMap;

	private final Map<OmegaTrajectory, List<Double>> distancesMap;
	private final Map<OmegaTrajectory, List<Double>> displacementsMap;
	private final Map<OmegaTrajectory, Double> maxDisplacementesMap;
	private final Map<OmegaTrajectory, Integer> totalTimeTraveledMap;
	private final Map<OmegaTrajectory, List<Double>> confinementRatioMap;
	final Map<OmegaTrajectory, List<Double[]>> anglesAndDirectionalChangesMap;

	final Map<OmegaTrajectory, List<Double>> localSpeedMap;
	final Map<OmegaTrajectory, List<Double>> localVelocityMap;
	final Map<OmegaTrajectory, Double> meanSpeedMap;
	final Map<OmegaTrajectory, Double> meanVelocityMap;

	final Map<OmegaTrajectory, Double[]> nyMap;
	private final Map<OmegaTrajectory, Double[][]> logMuMap;
	private final Map<OmegaTrajectory, Double[][]> muMap;
	private final Map<OmegaTrajectory, Double[][]> logDeltaTMap;
	private final Map<OmegaTrajectory, Double[][]> deltaTMap;
	private final Map<OmegaTrajectory, Double[][]> gammaDFromLogMap;
	private final Map<OmegaTrajectory, Double[][]> gammaDMap;
	private final Map<OmegaTrajectory, Double[]> gammaFromLogMap;
	private final Map<OmegaTrajectory, Double[]> gammaMap;
	private final Map<OmegaTrajectory, Double[]> smssFromLogMap;
	private final Map<OmegaTrajectory, Double[]> smssMap;

	public OmegaTrackingMeasuresRun(
	        final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Map<OmegaTrajectory, Double[]> peakSignalsMap,
	        final Map<OmegaTrajectory, Double[]> meanSignalsMap,
	        final Map<OmegaTrajectory, Double[]> localBackgroundsMap,
	        final Map<OmegaTrajectory, Double[]> localSNRsMap,
	        final Map<OmegaTrajectory, List<Double>> distancesMap,
	        final Map<OmegaTrajectory, List<Double>> displacementsMap,
	        final Map<OmegaTrajectory, Double> maxDisplacementesMap,
	        final Map<OmegaTrajectory, Integer> totalTimeTraveledMap,
	        final Map<OmegaTrajectory, List<Double>> confinementRatioMap,
	        final Map<OmegaTrajectory, List<Double[]>> anglesAndDirectionalChangesMap,
	        final Map<OmegaTrajectory, List<Double>> localSpeedMap,
	        final Map<OmegaTrajectory, List<Double>> localVelocityMap,
	        final Map<OmegaTrajectory, Double> meanSpeedMap,
	        final Map<OmegaTrajectory, Double> meanVelocityMap,
	        final Map<OmegaTrajectory, Double[]> ny,
	        final Map<OmegaTrajectory, Double[][]> mu,
	        final Map<OmegaTrajectory, Double[][]> logMu,
	        final Map<OmegaTrajectory, Double[][]> deltaT,
	        final Map<OmegaTrajectory, Double[][]> logDeltaT,
	        final Map<OmegaTrajectory, Double[][]> gammaD,
	        final Map<OmegaTrajectory, Double[][]> gammaDLog,
	        final Map<OmegaTrajectory, Double[]> gamma,
	        final Map<OmegaTrajectory, Double[]> gammaLog,
	        final Map<OmegaTrajectory, Double[]> smss,
	        final Map<OmegaTrajectory, Double[]> smssLog) {
		super(owner, algorithmSpec);
		this.peakSignalsMap = peakSignalsMap;
		this.meanSignalsMap = meanSignalsMap;
		this.localBackgroundsMap = localBackgroundsMap;
		this.localSNRsMap = localSNRsMap;
		this.distancesMap = distancesMap;
		this.displacementsMap = displacementsMap;
		this.maxDisplacementesMap = maxDisplacementesMap;
		this.totalTimeTraveledMap = totalTimeTraveledMap;
		this.confinementRatioMap = confinementRatioMap;
		this.anglesAndDirectionalChangesMap = anglesAndDirectionalChangesMap;
		this.localSpeedMap = localSpeedMap;
		this.localVelocityMap = localVelocityMap;
		this.meanSpeedMap = meanSpeedMap;
		this.meanVelocityMap = meanVelocityMap;

		this.nyMap = ny;
		this.muMap = mu;
		this.logMuMap = logMu;
		this.deltaTMap = deltaT;
		this.logDeltaTMap = logDeltaT;
		this.gammaDMap = gammaD;
		this.gammaDFromLogMap = gammaDLog;
		this.gammaMap = gamma;
		this.gammaFromLogMap = gammaLog;
		this.smssMap = smss;
		this.smssFromLogMap = smssLog;
	}

	public OmegaTrackingMeasuresRun(
	        final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final String name,
	        final Map<OmegaTrajectory, Double[]> peakSignalsMap,
	        final Map<OmegaTrajectory, Double[]> meanSignalsMap,
	        final Map<OmegaTrajectory, Double[]> localBackgroundsMap,
	        final Map<OmegaTrajectory, Double[]> localSNRsMap,
	        final Map<OmegaTrajectory, List<Double>> distancesMap,
	        final Map<OmegaTrajectory, List<Double>> displacementsMap,
	        final Map<OmegaTrajectory, Double> maxDisplacementesMap,
	        final Map<OmegaTrajectory, Integer> totalTimeTraveledMap,
	        final Map<OmegaTrajectory, List<Double>> confinementRatioMap,
	        final Map<OmegaTrajectory, List<Double[]>> anglesAndDirectionalChangesMap,
	        final Map<OmegaTrajectory, List<Double>> localSpeedMap,
	        final Map<OmegaTrajectory, List<Double>> localVelocityMap,
	        final Map<OmegaTrajectory, Double> meanSpeedMap,
	        final Map<OmegaTrajectory, Double> meanVelocityMap,
	        final Map<OmegaTrajectory, Double[]> ny,
	        final Map<OmegaTrajectory, Double[][]> mu,
	        final Map<OmegaTrajectory, Double[][]> logMu,
	        final Map<OmegaTrajectory, Double[][]> deltaT,
	        final Map<OmegaTrajectory, Double[][]> logDeltaT,
	        final Map<OmegaTrajectory, Double[][]> gammaD,
	        final Map<OmegaTrajectory, Double[][]> gammaDLog,
	        final Map<OmegaTrajectory, Double[]> gamma,
	        final Map<OmegaTrajectory, Double[]> gammaLog,
	        final Map<OmegaTrajectory, Double[]> smss,
	        final Map<OmegaTrajectory, Double[]> smssLog) {
		super(owner, algorithmSpec, name);
		this.peakSignalsMap = peakSignalsMap;
		this.meanSignalsMap = meanSignalsMap;
		this.localBackgroundsMap = localBackgroundsMap;
		this.localSNRsMap = localSNRsMap;
		this.distancesMap = distancesMap;
		this.displacementsMap = displacementsMap;
		this.maxDisplacementesMap = maxDisplacementesMap;
		this.totalTimeTraveledMap = totalTimeTraveledMap;
		this.confinementRatioMap = confinementRatioMap;
		this.anglesAndDirectionalChangesMap = anglesAndDirectionalChangesMap;
		this.localSpeedMap = localSpeedMap;
		this.localVelocityMap = localVelocityMap;
		this.meanSpeedMap = meanSpeedMap;
		this.meanVelocityMap = meanVelocityMap;

		this.nyMap = ny;
		this.muMap = mu;
		this.logMuMap = logMu;
		this.deltaTMap = deltaT;
		this.logDeltaTMap = logDeltaT;
		this.gammaDMap = gammaD;
		this.gammaDFromLogMap = gammaDLog;
		this.gammaMap = gamma;
		this.gammaFromLogMap = gammaLog;
		this.smssMap = smss;
		this.smssFromLogMap = smssLog;
	}

	public OmegaTrackingMeasuresRun(
	        final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Date timeStamps,
	        final String name,
	        final Map<OmegaTrajectory, Double[]> peakSignalsMap,
	        final Map<OmegaTrajectory, Double[]> meanSignalsMap,
	        final Map<OmegaTrajectory, Double[]> localBackgroundsMap,
	        final Map<OmegaTrajectory, Double[]> localSNRsMap,
	        final Map<OmegaTrajectory, List<Double>> distancesMap,
	        final Map<OmegaTrajectory, List<Double>> displacementsMap,
	        final Map<OmegaTrajectory, Double> maxDisplacementesMap,
	        final Map<OmegaTrajectory, Integer> totalTimeTraveledMap,
	        final Map<OmegaTrajectory, List<Double>> confinementRatioMap,
	        final Map<OmegaTrajectory, List<Double[]>> anglesAndDirectionalChangesMap,
	        final Map<OmegaTrajectory, List<Double>> localSpeedMap,
	        final Map<OmegaTrajectory, List<Double>> localVelocityMap,
	        final Map<OmegaTrajectory, Double> meanSpeedMap,
	        final Map<OmegaTrajectory, Double> meanVelocityMap,
	        final Map<OmegaTrajectory, Double[]> ny,
	        final Map<OmegaTrajectory, Double[][]> mu,
	        final Map<OmegaTrajectory, Double[][]> logMu,
	        final Map<OmegaTrajectory, Double[][]> deltaT,
	        final Map<OmegaTrajectory, Double[][]> logDeltaT,
	        final Map<OmegaTrajectory, Double[][]> gammaD,
	        final Map<OmegaTrajectory, Double[][]> gammaDLog,
	        final Map<OmegaTrajectory, Double[]> gamma,
	        final Map<OmegaTrajectory, Double[]> gammaLog,
	        final Map<OmegaTrajectory, Double[]> smss,
	        final Map<OmegaTrajectory, Double[]> smssLog) {
		super(owner, algorithmSpec, timeStamps, name);
		this.peakSignalsMap = peakSignalsMap;
		this.meanSignalsMap = meanSignalsMap;
		this.localBackgroundsMap = localBackgroundsMap;
		this.localSNRsMap = localSNRsMap;
		this.distancesMap = distancesMap;
		this.displacementsMap = displacementsMap;
		this.maxDisplacementesMap = maxDisplacementesMap;
		this.totalTimeTraveledMap = totalTimeTraveledMap;
		this.confinementRatioMap = confinementRatioMap;
		this.anglesAndDirectionalChangesMap = anglesAndDirectionalChangesMap;
		this.localSpeedMap = localSpeedMap;
		this.localVelocityMap = localVelocityMap;
		this.meanSpeedMap = meanSpeedMap;
		this.meanVelocityMap = meanVelocityMap;

		this.nyMap = ny;
		this.muMap = mu;
		this.logMuMap = logMu;
		this.deltaTMap = deltaT;
		this.logDeltaTMap = logDeltaT;
		this.gammaDMap = gammaD;
		this.gammaDFromLogMap = gammaDLog;
		this.gammaMap = gamma;
		this.gammaFromLogMap = gammaLog;
		this.smssMap = smss;
		this.smssFromLogMap = smssLog;
	}

	public Map<OmegaTrajectory, Double[]> getPeakSignalsResults() {
		return this.peakSignalsMap;
	}

	public Map<OmegaTrajectory, Double[]> getMeanSignalsResults() {
		return this.meanSignalsMap;
	}

	public Map<OmegaTrajectory, Double[]> getLocalBackgroundsResults() {
		return this.localBackgroundsMap;
	}

	public Map<OmegaTrajectory, Double[]> getLocalSNRsResults() {
		return this.localSNRsMap;
	}

	public Map<OmegaTrajectory, List<Double>> getDistancesResults() {
		return this.distancesMap;
	}

	public Map<OmegaTrajectory, List<Double>> getDisplacementsResults() {
		return this.displacementsMap;
	}

	public Map<OmegaTrajectory, Double> getMaxDisplacementsResults() {
		return this.maxDisplacementesMap;
	}

	public Map<OmegaTrajectory, Integer> getTotalTimeTraveledResults() {
		return this.totalTimeTraveledMap;
	}

	public Map<OmegaTrajectory, List<Double>> getConfinementRatioResults() {
		return this.confinementRatioMap;
	}

	public Map<OmegaTrajectory, List<Double[]>> getAnglesAndDirectionalChangesResults() {
		return this.anglesAndDirectionalChangesMap;
	}

	public Map<OmegaTrajectory, List<Double>> getLocalSpeedResults() {
		return this.localSpeedMap;
	}

	public Map<OmegaTrajectory, List<Double>> getLocalVelocityResults() {
		return this.localVelocityMap;
	}

	public Map<OmegaTrajectory, Double> getMeanSpeedResults() {
		return this.meanSpeedMap;
	}

	public Map<OmegaTrajectory, Double> getMeanVelocityResults() {
		return this.meanVelocityMap;
	}

	public Map<OmegaTrajectory, Double[]> getNyResults() {
		return this.nyMap;
	}

	public Map<OmegaTrajectory, Double[]> getGammaFromLogResults() {
		return this.gammaFromLogMap;
	}

	public Map<OmegaTrajectory, Double[]> getGammaResults() {
		return this.gammaMap;
	}

	public Map<OmegaTrajectory, Double[][]> getGammaDFromLogResults() {
		return this.gammaDFromLogMap;
	}

	public Map<OmegaTrajectory, Double[][]> getGammaDResults() {
		return this.gammaDMap;
	}

	public Map<OmegaTrajectory, Double[][]> getLogMuResults() {
		return this.logMuMap;
	}

	public Map<OmegaTrajectory, Double[][]> getMuResults() {
		return this.muMap;
	}

	public Map<OmegaTrajectory, Double[][]> getLogDeltaTResults() {
		return this.logDeltaTMap;
	}

	public Map<OmegaTrajectory, Double[][]> getDeltaTResults() {
		return this.deltaTMap;
	}

	public Map<OmegaTrajectory, Double[]> getSmssFromLogResults() {
		return this.smssFromLogMap;
	}

	public Map<OmegaTrajectory, Double[]> getSmssResults() {
		return this.smssMap;
	}
}
