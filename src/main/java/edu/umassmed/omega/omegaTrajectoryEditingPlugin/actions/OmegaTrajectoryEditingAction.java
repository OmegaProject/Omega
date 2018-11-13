package edu.umassmed.omega.omegaTrajectoryEditingPlugin.actions;

import java.util.List;

import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;

public class OmegaTrajectoryEditingAction {
	private final List<OmegaTrajectory> originalTrajs, modifiedTrajs;
	private boolean hasBeenApplied;

	public OmegaTrajectoryEditingAction(final List<OmegaTrajectory> from,
	        final List<OmegaTrajectory> to) {
		this.hasBeenApplied = false;
		this.originalTrajs = from;
		this.modifiedTrajs = to;
	}

	public final List<OmegaTrajectory> getOriginalTrajectories() {
		return this.originalTrajs;
	}

	public final List<OmegaTrajectory> getModifiedTrajectories() {
		return this.modifiedTrajs;
	}

	public void setHasBeenApplied(final boolean hasBeenApplied) {
		this.hasBeenApplied = hasBeenApplied;
	}

	public boolean hasBeenApplied() {
		return this.hasBeenApplied;
	}
}
