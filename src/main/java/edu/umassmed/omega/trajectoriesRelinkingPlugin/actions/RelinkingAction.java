package main.java.edu.umassmed.omega.trajectoriesRelinkingPlugin.actions;

import java.util.List;

import main.java.edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;

public class RelinkingAction {
	private final List<OmegaTrajectory> originalTrajs, modifiedTrajs;
	private boolean hasBeenApplied;

	public RelinkingAction(final List<OmegaTrajectory> from,
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
