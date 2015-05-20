package edu.umassmed.omega.commons.plugins.interfaces;

import edu.umassmed.omega.data.analysisRunElements.OrphanedAnalysisContainer;

public interface OmegaOrphanedAnalysisConsumerPluginInterface {
	public void setOrphanedAnalysis(
			final OrphanedAnalysisContainer orphanedAnalysis);

	public OrphanedAnalysisContainer getOrphanedAnalysis();
}
