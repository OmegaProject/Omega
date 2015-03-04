package edu.umassmed.omega.data.analysisRunElements;

import java.util.Date;
import java.util.List;

import edu.umassmed.omega.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaTrajectoriesRelinkingRun extends OmegaParticleLinkingRun {

	public OmegaTrajectoriesRelinkingRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final List<OmegaTrajectory> resultingTrajectory) {
		super(owner, algorithmSpec, resultingTrajectory);
	}

	public OmegaTrajectoriesRelinkingRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec, final String name,
	        final List<OmegaTrajectory> resultingTrajectories) {
		super(owner, algorithmSpec, name, resultingTrajectories);
	}

	public OmegaTrajectoriesRelinkingRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Date timeStamps, final String name,
	        final List<OmegaTrajectory> resultingTrajectories) {
		super(owner, algorithmSpec, timeStamps, name, resultingTrajectories);
	}
}
