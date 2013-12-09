package edu.umassmed.omega.omero.data;

import java.awt.image.BufferedImage;

import pojos.ImageData;

public class OmeroThumbnailImageInfo {
	private final Long imageID;
	private final ImageData imageData;
	private final String imageName;
	private final BufferedImage image;

	public OmeroThumbnailImageInfo(final Long imageID, final ImageData imageData,
	        final String imageName, final BufferedImage image) {
		this.imageID = imageID;
		this.imageData = imageData;
		this.imageName = imageName;
		this.image = image;
	}

	public Long getImageID() {
		return this.imageID;
	}

	public ImageData getImageData() {
		return this.imageData;
	}

	public String getImageName() {
		return this.imageName;
	}

	public BufferedImage getImage() {
		return this.image;
	}
}