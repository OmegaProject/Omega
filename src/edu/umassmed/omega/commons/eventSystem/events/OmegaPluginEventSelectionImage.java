package edu.umassmed.omega.commons.eventSystem.events;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaPluginEventSelectionImage extends OmegaPluginEvent {

	private final OmegaAnalysisRunContainer img;

	public OmegaPluginEventSelectionImage(final OmegaAnalysisRunContainer img) {
		this(null, img);
	}

	public OmegaPluginEventSelectionImage(final OmegaPlugin source,
			final OmegaAnalysisRunContainer img) {
		super(source);
		this.img = img;
	}

	public OmegaAnalysisRunContainer getImage() {
		return this.img;
	}
}
