package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.List;


public interface OmegaAnalysisRunContainer {
	public List<OmegaAnalysisRun> getAnalysisRuns();

	public void addAnalysisRun(final OmegaAnalysisRun analysisRun);
}
