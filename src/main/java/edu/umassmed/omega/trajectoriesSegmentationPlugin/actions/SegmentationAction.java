package edu.umassmed.omega.trajectoriesSegmentationPlugin.actions;

import java.util.List;

import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;

public class SegmentationAction {
	private final OmegaTrajectory traj;
	private final List<OmegaSegment> originalEdges, modifiedEdges;
	private boolean hasBeenApplied;

	public SegmentationAction(final OmegaTrajectory traj,
	        final List<OmegaSegment> from, final List<OmegaSegment> to) {
		this.hasBeenApplied = false;
		this.traj = traj;
		this.originalEdges = from;
		this.modifiedEdges = to;
	}

	public OmegaTrajectory getTrajectory() {
		return this.traj;
	}

	public final List<OmegaSegment> getOriginalEdges() {
		return this.originalEdges;
	}

	public final List<OmegaSegment> getModifiedEdges() {
		return this.modifiedEdges;
	}

	public void setHasBeenApplied(final boolean hasBeenApplied) {
		this.hasBeenApplied = hasBeenApplied;
	}

	public boolean hasBeenApplied() {
		return this.hasBeenApplied;
	}
}
