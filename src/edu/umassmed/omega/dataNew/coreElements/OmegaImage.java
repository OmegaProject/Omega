package edu.umassmed.omega.dataNew.coreElements;

import java.util.List;

public class OmegaImage extends OmegaElement {

	private final String name;

	private final List<OmegaImagePixels> pixelsList;

	// where
	// sizeX and sizeY = micron per pixel on axis
	// sizeZ = depth
	// sizeC = channels
	// sizeT = seconds per frames

	public OmegaImage(final Long elementID, final String name,
	        final List<OmegaImagePixels> pixels) {
		super(elementID);
		this.name = name;

		this.pixelsList = pixels;
	}

	public String getName() {
		return this.name;
	}

	public List<OmegaImagePixels> getPixels() {
		return this.pixelsList;
	}

	public OmegaImagePixels getPixels(final int index) {
		return this.pixelsList.get(index);
	}

	public boolean containsPixels(final Long id) {
		for (final OmegaImagePixels pixels : this.pixelsList) {
			if (pixels.getElementID() == id)
				return true;
		}
		return false;
	}

	public void addPixels(final OmegaImagePixels pixels) {
		this.pixelsList.add(pixels);
	}
}
