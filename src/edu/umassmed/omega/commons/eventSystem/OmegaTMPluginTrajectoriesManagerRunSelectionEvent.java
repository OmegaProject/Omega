package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaTrajectoriesManagerRun;

public class OmegaTMPluginTrajectoriesManagerRunSelectionEvent extends
        OmegaPluginEvent {

	private final OmegaTrajectoriesManagerRun analysisRun;

	public OmegaTMPluginTrajectoriesManagerRunSelectionEvent(
	        final OmegaTrajectoriesManagerRun analysisRun) {
		this(null, analysisRun);
	}

	public OmegaTMPluginTrajectoriesManagerRunSelectionEvent(
	        final OmegaPlugin source,
	        final OmegaTrajectoriesManagerRun analysisRun) {
		super(source);
		this.analysisRun = analysisRun;
	}

	public OmegaTrajectoriesManagerRun getAnalysisRun() {
		return this.analysisRun;
	}
}
