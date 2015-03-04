package edu.umassmed.omega.commons.eventSystem.events;

import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;

public class OmegaCoreEventSelectionTrajectoriesRelinkingRun extends
        OmegaCoreEventSelectionAnalysisRun {

	public OmegaCoreEventSelectionTrajectoriesRelinkingRun() {
		this(-1, null);
	}

	public OmegaCoreEventSelectionTrajectoriesRelinkingRun(final int source) {
		super(source, null);
	}

	public OmegaCoreEventSelectionTrajectoriesRelinkingRun(
	        final OmegaAnalysisRun analysisRun) {
		this(-1, analysisRun);
	}

	public OmegaCoreEventSelectionTrajectoriesRelinkingRun(final int source,
	        final OmegaAnalysisRun analysisRun) {
		super(source, analysisRun);
	}
}
