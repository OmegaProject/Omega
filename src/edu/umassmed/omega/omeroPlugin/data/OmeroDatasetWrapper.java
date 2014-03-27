package edu.umassmed.omega.omeroPlugin.data;

import pojos.DatasetData;
import pojos.ProjectData;

public class OmeroDatasetWrapper {

	private final ProjectData project;
	private final DatasetData dataset;

	// private final List<OmeroDatasetWrapper> datasets;

	public OmeroDatasetWrapper(final ProjectData project,
	        final DatasetData dataset) {
		this.project = project;
		this.dataset = dataset;
		// this.datasets = new ArrayList<OmeroDatasetWrapper>();
	}

	// public void setDatasets(final List<DatasetData> datasets) {
	// for (final DatasetData dataset : datasets) {
	// final OmeroDatasetWrapper omeDataset = new OmeroDatasetWrapper(
	// dataset);
	// this.datasets.add(omeDataset);
	// }
	// }

	// public List<OmeroDatasetWrapper> getDatasets() {
	// return this.datasets;
	// }

	@Override
	public String toString() {
		return this.dataset.getName();
	}

	public long getID() {
		return this.dataset.getId();
	}

	public DatasetData getDatasetData() {
		return this.dataset;
	}

	public ProjectData getProject() {
		return this.project;
	}
}
