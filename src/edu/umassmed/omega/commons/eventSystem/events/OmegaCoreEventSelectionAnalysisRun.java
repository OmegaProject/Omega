package edu.umassmed.omega.commons.eventSystem.events;

import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;

public class OmegaCoreEventSelectionAnalysisRun extends
        OmegaCoreEvent {
	private final OmegaAnalysisRun analysisRun;

	public OmegaCoreEventSelectionAnalysisRun(
	        final OmegaAnalysisRun analysisRun) {
		this(-1, analysisRun);
	}

	public OmegaCoreEventSelectionAnalysisRun(final int source,
	        final OmegaAnalysisRun analysisRun) {
		super(source);
		this.analysisRun = analysisRun;
	}

	public OmegaAnalysisRun getAnalysisRun() {
		return this.analysisRun;
	}
}
