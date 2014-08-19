package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.genericPlugins.OmegaPlugin;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;

public class OmegaTMPluginImageSelectionEvent extends OmegaPluginEvent {

	private final OmegaImage img;

	public OmegaTMPluginImageSelectionEvent(final OmegaImage img) {
		this(null, img);
	}

	public OmegaTMPluginImageSelectionEvent(final OmegaPlugin source,
	        final OmegaImage img) {
		super(source);
		this.img = img;
	}

	public OmegaImage getImage() {
		return this.img;
	}
}
