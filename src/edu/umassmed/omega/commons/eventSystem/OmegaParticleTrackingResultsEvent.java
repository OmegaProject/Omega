package edu.umassmed.omega.commons.eventSystem;

import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaParticleTrackingResultsEvent extends
        OmegaAlgorithmPluginEvent {

	private final Map<OmegaFrame, List<OmegaROI>> resultingParticles;
	private final List<OmegaTrajectory> resultingTrajectories;

	public OmegaParticleTrackingResultsEvent(final OmegaElement element,
	        final List<OmegaParameter> params,
	        final Map<OmegaFrame, List<OmegaROI>> resultingParticles,
	        final List<OmegaTrajectory> resultingTrajectories) {
		this(null, element, params, resultingParticles, resultingTrajectories);
	}

	public OmegaParticleTrackingResultsEvent(final OmegaPlugin source,
	        final OmegaElement element, final List<OmegaParameter> params,
	        final Map<OmegaFrame, List<OmegaROI>> resultingParticles,
	        final List<OmegaTrajectory> resultingTrajectories) {
		super(source, element, params);

		this.resultingParticles = resultingParticles;
		this.resultingTrajectories = resultingTrajectories;
	}

	public Map<OmegaFrame, List<OmegaROI>> getResultingParticles() {
		return this.resultingParticles;
	}

	public List<OmegaTrajectory> getResultingTrajectories() {
		return this.resultingTrajectories;
	}
}
