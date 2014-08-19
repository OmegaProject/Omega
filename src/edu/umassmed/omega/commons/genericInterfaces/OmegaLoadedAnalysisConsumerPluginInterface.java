package edu.umassmed.omega.commons.genericInterfaces;

import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;

public interface OmegaLoadedAnalysisConsumerPluginInterface {
	public void setLoadedAnalysisRun(
	        final List<OmegaAnalysisRun> loadedAnalysisRuns);

	public List<OmegaAnalysisRun> getLoadedAnalysisRuns();
}
