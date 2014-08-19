package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;

public class OmegaApplicationParticleDetectionRunSelectionEvent extends
        OmegaApplicationEvent {
	private final OmegaParticleDetectionRun analysisRun;

	public OmegaApplicationParticleDetectionRunSelectionEvent(
	        final OmegaParticleDetectionRun analysisRun) {
		this(-1, analysisRun);
	}

	public OmegaApplicationParticleDetectionRunSelectionEvent(final int source,
	        final OmegaParticleDetectionRun analysisRun) {
		super(source);
		this.analysisRun = analysisRun;
	}

	public OmegaParticleDetectionRun getAnalysisRun() {
		return this.analysisRun;
	}
}
