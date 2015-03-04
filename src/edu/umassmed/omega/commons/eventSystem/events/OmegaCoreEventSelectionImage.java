package edu.umassmed.omega.commons.eventSystem.events;

import edu.umassmed.omega.data.coreElements.OmegaImage;

public class OmegaCoreEventSelectionImage extends OmegaCoreEvent {
	private final OmegaImage image;

	public OmegaCoreEventSelectionImage(final OmegaImage img) {
		this(-1, img);
	}

	public OmegaCoreEventSelectionImage(final int source,
	        final OmegaImage img) {
		super(source);
		this.image = img;
	}

	public OmegaImage getImage() {
		return this.image;
	}
}
