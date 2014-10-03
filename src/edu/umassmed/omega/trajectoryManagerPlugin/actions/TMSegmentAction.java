package edu.umassmed.omega.trajectoryManagerPlugin.actions;

import java.util.List;

import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class TMSegmentAction extends TMAction {
	private final OmegaTrajectory traj;
	private final List<OmegaSegment> originalEdges, modifiedEdges;

	public TMSegmentAction(final OmegaTrajectory traj,
	        final List<OmegaSegment> from, final List<OmegaSegment> to) {
		super();
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
}
