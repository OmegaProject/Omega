package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaImage extends OmegaNamedElement implements
        OmegaAnalysisRunContainer {

	private final List<OmegaDataset> datasets;

	private final List<OmegaImagePixels> pixelsList;

	private final OmegaExperimenter experimenter;

	private final List<OmegaAnalysisRun> analysisRuns;

	// where
	// sizeX and sizeY = micron per pixel on axis
	// sizeZ = depth
	// sizeC = channels
	// sizeT = seconds per frames

	public OmegaImage(final Long elementID, final String name,
	        final OmegaExperimenter experimenter) {
		super(elementID, name);

		this.datasets = new ArrayList<OmegaDataset>();

		this.experimenter = experimenter;

		this.pixelsList = new ArrayList<OmegaImagePixels>();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImage(final Long elementID, final String name,
	        final OmegaExperimenter experimenter,
	        final List<OmegaImagePixels> pixelsList) {
		super(elementID, name);

		this.datasets = new ArrayList<OmegaDataset>();

		this.experimenter = experimenter;

		this.pixelsList = pixelsList;

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public void addParentDataset(final OmegaDataset dataset) {
		this.datasets.add(dataset);
	}

	public List<OmegaDataset> getParentDatasets() {
		return this.datasets;
	}

	public OmegaExperimenter getExperimenter() {
		return this.experimenter;
	}

	public List<OmegaImagePixels> getPixels() {
		return this.pixelsList;
	}

	public OmegaImagePixels getDefaultPixels() {
		final OmegaImagePixels defaultPixels = this.getPixels(0);
		if (defaultPixels == null) {
			// TODO throw error
		}
		return this.getPixels(0);
	}

	public OmegaImagePixels getPixels(final int index) {
		return this.pixelsList.get(index);
	}

	public boolean containsPixels(final Long id) {
		for (final OmegaImagePixels pixels : this.pixelsList) {
			if (pixels.getElementID() == id)
				return true;
		}
		return false;
	}

	public void addPixels(final OmegaImagePixels pixels) {
		this.pixelsList.add(pixels);
	}

	@Override
	public List<OmegaAnalysisRun> getAnalysisRuns() {
		return this.analysisRuns;
	}

	@Override
	public void addAnalysisRun(final OmegaAnalysisRun analysisRun) {
		analysisRun.addAnalysisRun(analysisRun);
	}
}
