package edu.umassmed.omega.commons.plugins;

import java.util.List;

import edu.umassmed.omega.commons.plugins.interfaces.OmegaImageConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoaderPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectImagePluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleDetectionRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleLinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesRelinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesSegmentationRunPluginInterface;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public abstract class OmegaTrajectoriesSegmentationPlugin extends
        OmegaAlgorithmPlugin implements OmegaSelectImagePluginInterface,
        OmegaSelectParticleDetectionRunPluginInterface,
        OmegaSelectParticleLinkingRunPluginInterface,
        OmegaSelectTrajectoriesRelinkingRunPluginInterface,
        OmegaSelectTrajectoriesSegmentationRunPluginInterface,
        OmegaLoadedAnalysisConsumerPluginInterface,
        OmegaImageConsumerPluginInterface, OmegaLoaderPluginInterface {

	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	private List<OmegaImage> loadedImages;
	private OmegaGateway gateway;

	private List<OmegaSegmentationTypes> segmTypesList;

	public OmegaTrajectoriesSegmentationPlugin() {
		this(1);
	}

	public OmegaTrajectoriesSegmentationPlugin(final int numOfPanels) {
		super(numOfPanels);

		this.loadedAnalysisRuns = null;
		this.loadedImages = null;
		this.gateway = null;

		this.segmTypesList = null;
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

	public List<OmegaSegmentationTypes> getSegmentationTypesList() {
		return this.segmTypesList;
	}

	public void setSegmentationTypesList(
	        final List<OmegaSegmentationTypes> segmTypesList) {
		this.segmTypesList = segmTypesList;
	}

	public abstract void updateSegmentationTypesList(
	        List<OmegaSegmentationTypes> segmTypesList);

	public abstract void updateTrajectories(List<OmegaTrajectory> trajectories,
	        boolean selection);

	public abstract void selectCurrentTrajectoriesSegmentationRun(
	        OmegaAnalysisRun analysisRun);
}
