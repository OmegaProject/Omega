package edu.umassmed.omega.commons.eventSystem;

import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;

public class OmegaParticleDetectionResultsEvent extends
        OmegaAlgorithmPluginEvent {

	private final Map<OmegaFrame, List<OmegaROI>> resultingParticles;

	public OmegaParticleDetectionResultsEvent(final OmegaElement element,
	        final List<OmegaParameter> params,
	        final Map<OmegaFrame, List<OmegaROI>> resultingParticles) {
		this(null, element, params, resultingParticles);
	}

	public OmegaParticleDetectionResultsEvent(final OmegaPlugin source,
	        final OmegaElement element, final List<OmegaParameter> params,
	        final Map<OmegaFrame, List<OmegaROI>> resultingParticles) {
		super(source, element, params);

		this.resultingParticles = resultingParticles;
	}

	public Map<OmegaFrame, List<OmegaROI>> getResultingParticles() {
		return this.resultingParticles;
	}
}
