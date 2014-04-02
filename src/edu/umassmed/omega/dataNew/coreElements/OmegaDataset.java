package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaDataset extends OmegaNamedElement implements
        OmegaAnalysisRunContainer {

	private final List<OmegaImage> images;

	private final List<OmegaAnalysisRun> analysisRuns;

	public OmegaDataset(final Long elementID, final String name) {
		super(elementID, name);

		this.images = new ArrayList<OmegaImage>();
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaDataset(final Long elementID, final String name,
	        final List<OmegaImage> images) {
		super(elementID, name);

		this.images = images;
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaDataset(final Long elementID, final String name,
	        final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns) {
		super(elementID, name);

		this.images = images;
		this.analysisRuns = analysisRuns;
	}

	public List<OmegaImage> getImages() {
		return this.images;
	}

	public boolean containsImage(final Long id) {
		for (final OmegaImage image : this.images) {
			if (image.getElementID() == id)
				return true;
		}
		return false;
	}

	public void addImage(final OmegaImage image) {
		this.images.add(image);
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
