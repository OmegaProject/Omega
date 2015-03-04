package edu.umassmed.omega.core.runnables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaIntensityAnalizer implements Runnable {

	private final List<OmegaTrajectory> trajectories;
	private final Map<OmegaTrajectory, Double[]> peakSignalsMap;
	private final Map<OmegaTrajectory, Double[]> meanSignalsMap;
	private final Map<OmegaTrajectory, Double[]> localBackgroundsMap;
	private final Map<OmegaTrajectory, Double[]> localSNRsMap;

	public OmegaIntensityAnalizer(final List<OmegaTrajectory> trajectories) {
		this.trajectories = trajectories;
		this.peakSignalsMap = new LinkedHashMap<>();
		this.meanSignalsMap = new LinkedHashMap<>();
		this.localBackgroundsMap = new LinkedHashMap<>();
		this.localSNRsMap = new LinkedHashMap<>();
	}

	private void resetArray(final Double[] array) {
		array[0] = Double.MAX_VALUE;
		array[1] = 0.0;
		array[2] = 0.0;
	}

	@Override
	public void run() {
		for (final OmegaTrajectory track : this.trajectories) {
			final Double[] peaks = new Double[3];
			final Double[] means = new Double[3];
			final Double[] bgs = new Double[3];
			final Double[] snrs = new Double[3];
			this.resetArray(peaks);
			this.resetArray(means);
			this.resetArray(bgs);
			this.resetArray(snrs);
			for (final OmegaROI roi : track.getROIs()) {
				final OmegaParticle particle = (OmegaParticle) roi;
				final Double peakSignal = particle.getPeakSignal();
				final Double meanSignal = particle.getMeanSignal();
				final Double localBg = particle.getMeanBackground();
				final Double localSNR = particle.getSNR();
				peaks[1] += peakSignal;
				means[1] += meanSignal;
				bgs[1] += localBg;
				snrs[1] += localSNR;
				if (peaks[0] > peakSignal) {
					peaks[0] = peakSignal;
				}
				if (peaks[2] < peakSignal) {
					peaks[2] = peakSignal;
				}
				if (means[0] > meanSignal) {
					means[0] = meanSignal;
				}
				if (means[2] < meanSignal) {
					means[2] = meanSignal;
				}
				if (bgs[0] > localBg) {
					bgs[0] = localBg;
				}
				if (bgs[2] < localBg) {
					bgs[2] = localBg;
				}
				if (snrs[0] > localSNR) {
					snrs[0] = localSNR;
				}
				if (snrs[2] < localSNR) {
					snrs[2] = localSNR;
				}
			}
			peaks[1] /= track.getLength();
			means[1] /= track.getLength();
			bgs[1] /= track.getLength();
			snrs[1] /= track.getLength();
			this.peakSignalsMap.put(track, peaks);
			this.meanSignalsMap.put(track, peaks);
			this.localBackgroundsMap.put(track, peaks);
			this.localSNRsMap.put(track, peaks);
		}
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
}
