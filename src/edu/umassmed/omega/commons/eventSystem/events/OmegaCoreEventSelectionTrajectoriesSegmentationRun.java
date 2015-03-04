package edu.umassmed.omega.commons.eventSystem.events;

import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;

public class OmegaCoreEventSelectionTrajectoriesSegmentationRun extends
        OmegaCoreEventSelectionAnalysisRun {

	public OmegaCoreEventSelectionTrajectoriesSegmentationRun() {
		this(-1, null);
	}

	public OmegaCoreEventSelectionTrajectoriesSegmentationRun(final int source) {
		super(source, null);
	}

	public OmegaCoreEventSelectionTrajectoriesSegmentationRun(
	        final OmegaAnalysisRun analysisRun) {
		this(-1, analysisRun);
	}

	public OmegaCoreEventSelectionTrajectoriesSegmentationRun(final int source,
	        final OmegaAnalysisRun analysisRun) {
		super(source, analysisRun);
	}

}
