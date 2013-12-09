package edu.umassmed.omega.omero.data;

import java.util.ArrayList;
import java.util.List;

import pojos.DatasetData;
import pojos.ProjectData;

public class OmeroProjectWrapper {

	private final ProjectData proj;
	private final List<OmeroDatasetWrapper> datasets;

	public OmeroProjectWrapper(final ProjectData proj) {
		this.proj = proj;
		this.datasets = new ArrayList<OmeroDatasetWrapper>();
	}

	public void setDatasets(final List<DatasetData> datasets) {
		for (final DatasetData dataset : datasets) {
			final OmeroDatasetWrapper omeDataset = new OmeroDatasetWrapper(
			        dataset);
			this.datasets.add(omeDataset);
		}
	}

	public List<OmeroDatasetWrapper> getDatasets() {
		return this.datasets;
	}

	@Override
	public String toString() {
		return this.proj.getName();
	}

	public long getID() {
		return this.proj.getId();
	}

	public List<DatasetData> getDatasetsData() {
		final List<DatasetData> datasets = new ArrayList<DatasetData>();
		for (final OmeroDatasetWrapper omeDataset : this.datasets) {
			datasets.add(omeDataset.getDatasetData());
		}
		return datasets;
	}
}
