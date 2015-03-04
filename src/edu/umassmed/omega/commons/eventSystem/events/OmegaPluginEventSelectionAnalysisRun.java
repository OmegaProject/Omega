package edu.umassmed.omega.commons.eventSystem.events;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;

public class OmegaPluginEventSelectionAnalysisRun extends OmegaPluginEvent {

	private final OmegaAnalysisRun analysisRun;

	public OmegaPluginEventSelectionAnalysisRun(
	        final OmegaAnalysisRun analysisRun) {
		this(null, analysisRun);
	}

	public OmegaPluginEventSelectionAnalysisRun(final OmegaPlugin source,
	        final OmegaAnalysisRun analysisRun) {
		super(source);
		this.analysisRun = analysisRun;
	}

	public OmegaAnalysisRun getAnalysisRun() {
		return this.analysisRun;
	}
}
