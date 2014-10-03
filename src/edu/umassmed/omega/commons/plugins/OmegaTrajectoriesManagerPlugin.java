package edu.umassmed.omega.commons.plugins;

import java.util.List;

import edu.umassmed.omega.commons.plugins.interfaces.OmegaImageConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoaderPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesManagerRunPluginInterface;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public abstract class OmegaTrajectoriesManagerPlugin extends
        OmegaAlgorithmPlugin implements
        OmegaSelectTrajectoriesManagerRunPluginInterface,
        OmegaLoadedAnalysisConsumerPluginInterface,
        OmegaImageConsumerPluginInterface, OmegaLoaderPluginInterface {

	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	private List<OmegaImage> loadedImages;
	private List<OmegaSegmentationTypes> segmTypesList;
	private OmegaGateway gateway;

	public OmegaTrajectoriesManagerPlugin() {
		this(1);
	}

	public OmegaTrajectoriesManagerPlugin(final int numOfPanels) {
		super(numOfPanels);

		this.loadedAnalysisRuns = null;
		this.loadedImages = null;
		this.segmTypesList = null;
	}

	public List<OmegaSegmentationTypes> getSegmentationTypesList() {
		return this.segmTypesList;
	}

	public void setSegmentationTypesList(
	        final List<OmegaSegmentationTypes> segmTypesList) {
		this.segmTypesList = segmTypesList;
	}

	@Override
	public List<OmegaImage> getLoadedImages() {
		return this.loadedImages;
	}

	@Override
	public void setLoadedImages(final List<OmegaImage> images) {
		this.loadedImages = images;
	}

	@Override
	public void setLoadedAnalysisRun(
	        final List<OmegaAnalysisRun> loadedAnalysisRuns) {
		this.loadedAnalysisRuns = loadedAnalysisRuns;
	}

	@Override
	public List<OmegaAnalysisRun> getLoadedAnalysisRuns() {
		return this.loadedAnalysisRuns;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public OmegaGateway getGateway() {
		return this.gateway;
	}

	public abstract void updateTrajectories(List<OmegaTrajectory> trajectories,
	        boolean selection);

	public abstract void updateSegmentationTypesList(
	        List<OmegaSegmentationTypes> segmTypesList);
}
