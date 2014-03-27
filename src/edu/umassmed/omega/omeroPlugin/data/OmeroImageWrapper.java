package edu.umassmed.omega.omeroPlugin.data;

import pojos.DatasetData;
import pojos.ImageData;
import pojos.ProjectData;

public class OmeroImageWrapper {

	private final Long imageID;
	private final String imageName;
	private final ImageData imageData;
	private final DatasetData datasetData;
	private final ProjectData projectData;

	public OmeroImageWrapper(final Long imageID, final String imageName,
	        final ProjectData projectData, final DatasetData datasetData,
	        final ImageData imageData) {
		this.imageID = imageID;
		this.projectData = projectData;
		this.datasetData = datasetData;
		this.imageData = imageData;
		this.imageName = imageName;
	}

	public Long getImageID() {
		return this.imageID;
	}

	public String getImageName() {
		return this.imageName;
	}

	public ProjectData getProjectData() {
		return this.projectData;
	}

	public DatasetData getDatasetData() {
		return this.datasetData;
	}

	public ImageData getImageData() {
		return this.imageData;
	}
}
