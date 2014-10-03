package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaTrajectoriesManagerRun;

public class OmegaApplicationTrajectoriesManagerRunSelectionEvent extends
        OmegaApplicationEvent {
	private final OmegaTrajectoriesManagerRun analysisRun;

	public OmegaApplicationTrajectoriesManagerRunSelectionEvent(
	        final OmegaTrajectoriesManagerRun analysisRun) {
		this(-1, analysisRun);
	}

	public OmegaApplicationTrajectoriesManagerRunSelectionEvent(
	        final int source, final OmegaTrajectoriesManagerRun analysisRun) {
		super(source);
		this.analysisRun = analysisRun;
	}

	public OmegaTrajectoriesManagerRun getAnalysisRun() {
		return this.analysisRun;
	}
}
