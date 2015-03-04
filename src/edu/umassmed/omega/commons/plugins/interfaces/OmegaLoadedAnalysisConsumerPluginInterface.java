package edu.umassmed.omega.commons.plugins.interfaces;

import java.util.List;

import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;

public interface OmegaLoadedAnalysisConsumerPluginInterface {
	public void setLoadedAnalysisRun(
	        final List<OmegaAnalysisRun> loadedAnalysisRuns);

	public List<OmegaAnalysisRun> getLoadedAnalysisRuns();
}
