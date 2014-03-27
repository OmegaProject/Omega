package edu.umassmed.omega.omeroPlugin.data;

import java.awt.image.BufferedImage;

import pojos.DatasetData;
import pojos.ImageData;
import pojos.ProjectData;

public class OmeroThumbnailImageInfo {

	private final OmeroImageWrapper image;
	private final BufferedImage buffImage;

	public OmeroThumbnailImageInfo(final OmeroImageWrapper image,
	        final BufferedImage buffImage) {
		this.image = image;
		this.buffImage = buffImage;
	}

	public OmeroImageWrapper getImage() {
		return this.image;
	}

	public BufferedImage getBufferedImage() {
		return this.buffImage;
	}

	public Long getImageID() {
		return this.image.getImageID();
	}

	public String getImageName() {
		return this.image.getImageName();
	}

	public ProjectData getProjectData() {
		return this.image.getProjectData();
	}

	public DatasetData getDatasetData() {
		return this.image.getDatasetData();
	}

	public ImageData getImageData() {
		return this.image.getImageData();
	}
}