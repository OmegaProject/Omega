package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaProject extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private final String name;

	private List<OmegaDataset> datasets;

	private List<OmegaAnalysisRun> analysisRuns;

	public OmegaProject(final Long elementID, final String name) {
		super(elementID);
		this.name = name;

		this.datasets = new ArrayList<OmegaDataset>();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaProject(final Long elementID, final String name,
	        final List<OmegaDataset> datasets) {
		this(elementID, name);

		this.datasets = datasets;
	}

	public OmegaProject(final Long elementID, final String name,
	        final List<OmegaDataset> datasets,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this(elementID, name);

		this.datasets = datasets;
		this.analysisRuns = analysisRuns;
	}

	public String getName() {
		return this.name;
	}

	public List<OmegaDataset> getDatasets() {
		return this.datasets;
	}

	public boolean containsDataset(final long id) {
		for (final OmegaDataset dataset : this.datasets) {
			if (dataset.getElementID() == id)
				return true;
		}
		return false;
	}

	public void addDataset(final OmegaDataset dataset) {
		this.datasets.add(dataset);
	}

	@Override
	public List<OmegaAnalysisRun> getAnalysisRuns() {
		return this.analysisRuns;
	}

	@Override
	public void addAnalysisRun(final OmegaAnalysisRun analysisRun) {
		this.analysisRuns.add(analysisRun);
	}
}
