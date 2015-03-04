package unused.trajectoryManagerPlugin.actions;

import java.util.List;

import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class TMAdjustAction extends TMAction {
	private final List<OmegaTrajectory> originalTrajs, modifiedTrajs;

	public TMAdjustAction(final List<OmegaTrajectory> from,
	        final List<OmegaTrajectory> to) {
		super();
		this.originalTrajs = from;
		this.modifiedTrajs = to;
	}

	public final List<OmegaTrajectory> getOriginalTrajectories() {
		return this.originalTrajs;
	}

	public final List<OmegaTrajectory> getModifiedTrajectories() {
		return this.modifiedTrajs;
	}
}
