package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.genericPlugins.OmegaPlugin;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;

public class OmegaTMPluginParticleLinkingRunSelectionEvent extends
        OmegaPluginEvent {

	private final OmegaParticleLinkingRun analysisRun;

	public OmegaTMPluginParticleLinkingRunSelectionEvent(
	        final OmegaParticleLinkingRun analysisRun) {
		this(null, analysisRun);
	}

	public OmegaTMPluginParticleLinkingRunSelectionEvent(
	        final OmegaPlugin source, final OmegaParticleLinkingRun analysisRun) {
		super(source);
		this.analysisRun = analysisRun;
	}

	public OmegaParticleLinkingRun getAnalysisRun() {
		return this.analysisRun;
	}
}
