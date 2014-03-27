package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaDataset extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private final String name;

	private List<OmegaImage> images;

	private List<OmegaAnalysisRun> analysisRuns;

	public OmegaDataset(final Long elementID, final String name) {
		super(elementID);
		this.name = name;

		this.images = new ArrayList<OmegaImage>();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaDataset(final Long elementID, final String name,
	        final List<OmegaImage> images) {
		this(elementID, name);

		this.images = images;
	}

	public OmegaDataset(final Long elementID, final String name,
	        final List<OmegaImage> images,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this(elementID, name);

		this.images = images;
		this.analysisRuns = analysisRuns;
	}

	public String getName() {
		return this.name;
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
