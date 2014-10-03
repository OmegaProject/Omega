package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleDetectionRun;

public class OmegaTMPluginParticleDetectionRunSelectionEvent extends
        OmegaPluginEvent {

	private final OmegaParticleDetectionRun analysisRun;

	public OmegaTMPluginParticleDetectionRunSelectionEvent(
	        final OmegaParticleDetectionRun analysisRun) {
		this(null, analysisRun);
	}

	public OmegaTMPluginParticleDetectionRunSelectionEvent(
	        final OmegaPlugin source,
	        final OmegaParticleDetectionRun analysisRun) {
		super(source);
		this.analysisRun = analysisRun;
	}

	public OmegaParticleDetectionRun getAnalysisRun() {
		return this.analysisRun;
	}
}
