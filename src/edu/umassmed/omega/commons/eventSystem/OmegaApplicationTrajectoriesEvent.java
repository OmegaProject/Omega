package edu.umassmed.omega.commons.eventSystem;

import java.util.List;

import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaApplicationTrajectoriesEvent extends OmegaApplicationEvent {
	private final List<OmegaTrajectory> trajectories;

	private final boolean selection;

	public OmegaApplicationTrajectoriesEvent(final List<OmegaTrajectory> trajs,
	        final boolean selection) {
		this(-1, trajs, selection);
	}

	public OmegaApplicationTrajectoriesEvent(final int source,
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
