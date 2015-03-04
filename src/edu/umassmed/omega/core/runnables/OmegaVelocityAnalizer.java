package edu.umassmed.omega.core.runnables;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.libraries.OmegaVelocityLibrary;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaVelocityAnalizer implements Runnable {

	private final int tMax;
	private final List<OmegaTrajectory> trajectories;
	private final Map<OmegaTrajectory, List<Double>> localSpeedMap;
	private final Map<OmegaTrajectory, List<Double>> localVelocityMap;
	private final Map<OmegaTrajectory, Double> meanSpeedMap;
	private final Map<OmegaTrajectory, Double> meanVelocityMap;

	public OmegaVelocityAnalizer(final int tMax,
	        final List<OmegaTrajectory> trajectories) {
		this.tMax = tMax;
		this.trajectories = trajectories;
		this.localSpeedMap = new LinkedHashMap<>();
		this.localVelocityMap = new LinkedHashMap<>();
		this.meanSpeedMap = new LinkedHashMap<>();
		this.meanVelocityMap = new LinkedHashMap<>();
	}

	@Override
	public void run() {
		for (final OmegaTrajectory track : this.trajectories) {
			final List<Double> localSpeeds = new ArrayList<>();
			final List<Double> localVelocities = new ArrayList<>();
			for (int t = 0; t < this.tMax; t++) {
				final Double localSpeed = OmegaVelocityLibrary
				        .computeLocalSpeed(track, t);
				final Double localVelocity = OmegaVelocityLibrary
				        .computeLocalVelocity(track, t);
				localSpeeds.add(localSpeed);
				localVelocities.add(localVelocity);
			}
			this.localSpeedMap.put(track, localSpeeds);
			this.localVelocityMap.put(track, localVelocities);

			final Double meanSpeed = OmegaVelocityLibrary
			        .computeMeanSpeed(track);
			final Double meanVelocity = OmegaVelocityLibrary
			        .computeMeanVelocity(track);
			this.meanSpeedMap.put(track, meanSpeed);
			this.meanVelocityMap.put(track, meanVelocity);
		}
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
}
