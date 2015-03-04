package edu.umassmed.omega.core.runnables;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.libraries.OmegaMobilityLibrary;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaMobilityAnalizer implements Runnable {

	private final int tMax;
	private final List<OmegaTrajectory> trajectories;
	private final Map<OmegaTrajectory, List<Double>> distancesMap;
	private final Map<OmegaTrajectory, List<Double>> displacementsMap;
	private final Map<OmegaTrajectory, Double> maxDisplacementesMap;
	private final Map<OmegaTrajectory, Integer> totalTimeTraveledMap;
	private final Map<OmegaTrajectory, List<Double>> confinementRatioMap;
	private final Map<OmegaTrajectory, List<Double[]>> anglesAndDirectionalChangesMap;

	public OmegaMobilityAnalizer(final int tMax,
	        final List<OmegaTrajectory> trajectories) {
		this.tMax = tMax;
		this.trajectories = trajectories;
		this.distancesMap = new LinkedHashMap<>();
		this.displacementsMap = new LinkedHashMap<>();
		this.maxDisplacementesMap = new LinkedHashMap<>();
		this.totalTimeTraveledMap = new LinkedHashMap<>();
		this.confinementRatioMap = new LinkedHashMap<>();
		this.anglesAndDirectionalChangesMap = new LinkedHashMap<>();
	}

	@Override
	public void run() {
		for (final OmegaTrajectory track : this.trajectories) {
			double maxDisp = 0.0;
			int totalTimeTraveled = 0;
			final List<Double> distances = new ArrayList<>();
			final List<Double> displacements = new ArrayList<>();
			final List<Double> confinementRatios = new ArrayList<>();
			final List<Double[]> anglesAndDirectionalChanges = new ArrayList<>();
			boolean counting = false;
			Double prevAngle = null;
			for (int t = 0; t < this.tMax; t++) {
				final Double distance = OmegaMobilityLibrary
				        .computeTotalDistanceTraveled(track, t);
				final Double displacement = OmegaMobilityLibrary
				        .computeTotalNetDisplacement(track, t);

				final Double[] angleAndDirectionalChange = OmegaMobilityLibrary
				        .computeDirectionalChange(track, prevAngle, t);
				prevAngle = angleAndDirectionalChange[0];

				Double confinementRatio = null;
				if (displacement != null) {
					confinementRatio = displacement / distance;
					if (!counting) {
						counting = true;
					}
					if (maxDisp < displacement) {
						maxDisp = displacement;
					}
				}
				if (counting && (displacement == null)) {
					final List<OmegaROI> rois = track.getROIs();
					final int maxT = rois.get(rois.size() - 1).getFrameIndex();
					if (t > maxT) {
						counting = false;
					}
				}
				if (counting) {
					totalTimeTraveled++;
				}
				distances.add(distance);
				displacements.add(displacement);
				confinementRatios.add(confinementRatio);
				anglesAndDirectionalChanges.add(angleAndDirectionalChange);
			}
			this.distancesMap.put(track, distances);
			this.displacementsMap.put(track, displacements);
			this.maxDisplacementesMap.put(track, maxDisp);
			this.totalTimeTraveledMap.put(track, totalTimeTraveled);
			this.confinementRatioMap.put(track, confinementRatios);
			this.anglesAndDirectionalChangesMap.put(track,
			        anglesAndDirectionalChanges);
		}
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
}
