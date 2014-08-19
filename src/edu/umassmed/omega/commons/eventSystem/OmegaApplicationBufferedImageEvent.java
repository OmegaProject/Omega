package edu.umassmed.omega.commons.eventSystem;

import java.awt.image.BufferedImage;

public class OmegaApplicationBufferedImageEvent extends
        OmegaApplicationEvent {
	private final BufferedImage buffImage;

	public OmegaApplicationBufferedImageEvent(
	        final BufferedImage buffImg) {
		this(-1, buffImg);
	}

	public OmegaApplicationBufferedImageEvent(final int source,
	        final BufferedImage buffImg) {
		super(source);
		this.buffImage = buffImg;
	}

	public BufferedImage getBufferedImage() {
		return this.buffImage;
	}

}
