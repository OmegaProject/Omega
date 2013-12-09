package edu.umassmed.omega.core.data;

import java.sql.Timestamp;
import java.util.Set;

import pojos.ImageData;
import pojos.PixelsData;

public class ImageInfo {
	private final Set datasets;

	private final Long imageID;
	private final ImageData imageData;
	private final String imageName;
	private final PixelsData pixels;
	private final int width;
	private final int height;
	private final int deepth;
	private final int channels;
	private final int time;

	private final Timestamp acquisitionDate;
	private final Set annotations;

	public ImageInfo(final Long imageID, final ImageData imageData,
	        final String imageName) {
		this.imageID = imageID;
		this.imageData = imageData;
		this.acquisitionDate = imageData.getAcquisitionDate();
		this.annotations = imageData.getAnnotations();
		this.datasets = imageData.getDatasets();
		this.imageName = imageName;
		this.pixels = imageData.getDefaultPixels();
		this.width = this.pixels.getSizeX();
		this.height = this.pixels.getSizeY();
		this.deepth = this.pixels.getSizeZ();
		this.channels = this.pixels.getSizeC();
		this.time = this.pixels.getSizeT();

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

	public PixelsData getPixels() {
		return this.pixels;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getDeepth() {
		return this.deepth;
	}

	public int getChannels() {
		return this.channels;
	}

	public int getTime() {
		return this.time;
	}
}
