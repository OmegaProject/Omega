package edu.umassmed.omega.commons.eventSystem;

import java.util.List;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaParticleLinkingResultsEvent extends OmegaAlgorithmPluginEvent {

	private final List<OmegaTrajectory> resultingTrajectories;

	public OmegaParticleLinkingResultsEvent(final OmegaElement element,
	        final List<OmegaParameter> params,
	        final List<OmegaTrajectory> resultingTrajectories) {
		this(null, element, params, resultingTrajectories);
	}

	public OmegaParticleLinkingResultsEvent(final OmegaPlugin source,
	        final OmegaElement element, final List<OmegaParameter> params,
	        final List<OmegaTrajectory> resultingTrajectories) {
		super(source, element, params);

		this.resultingTrajectories = resultingTrajectories;
	}

	public List<OmegaTrajectory> getResultingTrajectories() {
		return this.resultingTrajectories;
	}
}
