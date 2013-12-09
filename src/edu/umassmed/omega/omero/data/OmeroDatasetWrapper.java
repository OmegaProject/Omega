package edu.umassmed.omega.omero.data;

import pojos.DatasetData;

public class OmeroDatasetWrapper {

	private final DatasetData dataset;

	// private final List<OmeroDatasetWrapper> datasets;

	public OmeroDatasetWrapper(final DatasetData dataset) {
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
}
