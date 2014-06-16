package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.List;
import java.util.Map;

import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;

public class OmegaParticleDetectionRun extends OmegaAnalysisRun {

	private final Map<OmegaFrame, List<OmegaROI>> resultingParticles;

	public OmegaParticleDetectionRun(final Long elementID,
	        final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Map<OmegaFrame, List<OmegaROI>> resultingParticles) {
		super(elementID, owner, algorithmSpec);

		this.resultingParticles = resultingParticles;
	}

	public Map<OmegaFrame, List<OmegaROI>> getResultingParticles() {
		return this.resultingParticles;
	}
}
