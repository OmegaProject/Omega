package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;

public class OmegaApplicationParticleLinkingRunSelectionEvent extends
        OmegaApplicationEvent {
	private final OmegaParticleLinkingRun analysisRun;

	public OmegaApplicationParticleLinkingRunSelectionEvent(
	        final OmegaParticleLinkingRun analysisRun) {
		this(-1, analysisRun);
	}

	public OmegaApplicationParticleLinkingRunSelectionEvent(final int source,
	        final OmegaParticleLinkingRun analysisRun) {
		super(source);
		this.analysisRun = analysisRun;
	}

	public OmegaParticleLinkingRun getAnalysisRun() {
		return this.analysisRun;
	}
}
