package edu.umassmed.omega.commons.eventSystem.events;

import java.util.List;

import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaCoreEventTrajectories extends OmegaCoreEvent {
	private final List<OmegaTrajectory> trajectories;

	private final boolean selection;

	public OmegaCoreEventTrajectories(final List<OmegaTrajectory> trajs,
	        final boolean selection) {
		this(-1, trajs, selection);
	}

	public OmegaCoreEventTrajectories(final int source,
	        final List<OmegaTrajectory> trajs, final boolean selection) {
		super(source);
		this.trajectories = trajs;
		this.selection = selection;
	}

	public List<OmegaTrajectory> getTrajectories() {
		return this.trajectories;
	}

	public boolean isSelectionEvent() {
		return this.selection;
	}
}
