package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaParticleLinkingRun extends OmegaAnalysisRun {
	private final List<OmegaTrajectory> resultingTrajectory;

	public OmegaParticleLinkingRun(final Long elementID,
	        final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final List<OmegaTrajectory> resultingTrajectory) {
		super(elementID, owner, algorithmSpec);

		this.resultingTrajectory = resultingTrajectory;
	}

	public List<OmegaTrajectory> getResultingTrajectory() {
		return this.resultingTrajectory;
	}
}
