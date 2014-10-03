package edu.umassmed.omega.commons.eventSystem;

import java.util.List;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaTMPluginTrajectoriesEvent extends OmegaPluginEvent {

	private final List<OmegaTrajectory> trajectories;

	private final boolean selection;

	public OmegaTMPluginTrajectoriesEvent(final List<OmegaTrajectory> trajs,
	        final boolean selection) {
		this(null, trajs, selection);
	}

	public OmegaTMPluginTrajectoriesEvent(final OmegaPlugin source,
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
