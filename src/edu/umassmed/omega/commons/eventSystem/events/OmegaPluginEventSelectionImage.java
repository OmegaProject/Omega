package edu.umassmed.omega.commons.eventSystem.events;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.data.coreElements.OmegaImage;

public class OmegaPluginEventSelectionImage extends OmegaPluginEvent {

	private final OmegaImage img;

	public OmegaPluginEventSelectionImage(final OmegaImage img) {
		this(null, img);
	}

	public OmegaPluginEventSelectionImage(final OmegaPlugin source,
	        final OmegaImage img) {
		super(source);
		this.img = img;
	}

	public OmegaImage getImage() {
		return this.img;
	}
}
