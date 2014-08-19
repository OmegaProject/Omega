package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.dataNew.coreElements.OmegaImage;

public class OmegaApplicationImageSelectionEvent extends OmegaApplicationEvent {
	private final OmegaImage image;

	public OmegaApplicationImageSelectionEvent(final OmegaImage img) {
		this(-1, img);
	}

	public OmegaApplicationImageSelectionEvent(final int source,
	        final OmegaImage img) {
		super(source);
		this.image = img;
	}

	public OmegaImage getImage() {
		return this.image;
	}
}
