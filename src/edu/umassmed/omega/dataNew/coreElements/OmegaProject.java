package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaProject extends OmegaNamedElement implements
        OmegaAnalysisRunContainer {

	private final List<OmegaDataset> datasets;

	private final List<OmegaAnalysisRun> analysisRuns;

	public OmegaProject(final Long elementID, final String name) {
		super(elementID, name);

		this.datasets = new ArrayList<OmegaDataset>();
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaProject(final Long elementID, final String name,
	        final List<OmegaDataset> datasets) {
		super(elementID, name);

		this.datasets = datasets;
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaProject(final Long elementID, final String name,
	        final List<OmegaDataset> datasets,
	        final List<OmegaAnalysisRun> analysisRuns) {
		super(elementID, name);

		this.datasets = datasets;
		this.analysisRuns = analysisRuns;
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
