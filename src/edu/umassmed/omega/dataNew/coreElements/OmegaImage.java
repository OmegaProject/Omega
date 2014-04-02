package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

public class OmegaImage extends OmegaNamedElement {

	private final List<OmegaImagePixels> pixelsList;

	private final OmegaExperimenter experimenter;

	// where
	// sizeX and sizeY = micron per pixel on axis
	// sizeZ = depth
	// sizeC = channels
	// sizeT = seconds per frames

	public OmegaImage(final Long elementID, final String name,
	        final OmegaExperimenter experimenter) {
		super(elementID, name);

		this.experimenter = experimenter;

		this.pixelsList = new ArrayList<OmegaImagePixels>();
	}

	public OmegaImage(final Long elementID, final String name,
	        final OmegaExperimenter experimenter,
	        final List<OmegaImagePixels> pixelsList) {
		super(elementID, name);

		this.experimenter = experimenter;

		this.pixelsList = pixelsList;
	}

	public OmegaExperimenter getExperimenter() {
		return this.experimenter;
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
