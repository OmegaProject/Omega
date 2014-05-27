package edu.umassmed.omega.commons;

import java.util.List;

import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;

public abstract class OmegaBrowserPlugin extends OmegaDataManagerPlugin {

	// private final OmegaGateway gateway;
	// TODO Loaded data
	private OmegaLoadedData loadedData;
	private List<OmegaAnalysisRun> loadedAnalysisRuns;

	public OmegaBrowserPlugin() {
		super();
	}

	public void setLoadedData(final OmegaLoadedData loadedData) {
		this.loadedData = loadedData;
	}

	public void setLoadedAnalysisRun(
	        final List<OmegaAnalysisRun> loadedAnalysisRuns) {
		this.loadedAnalysisRuns = loadedAnalysisRuns;
	}

	public OmegaLoadedData getLoadedData() {
		return this.loadedData;
	}

	public List<OmegaAnalysisRun> getLoadedAnalysisRuns() {
		return this.loadedAnalysisRuns;
	}
}
