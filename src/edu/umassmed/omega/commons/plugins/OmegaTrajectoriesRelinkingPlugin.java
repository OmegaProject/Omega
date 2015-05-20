package edu.umassmed.omega.commons.plugins;

import java.util.List;

import edu.umassmed.omega.commons.plugins.interfaces.OmegaImageConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoaderPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaOrphanedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectImagePluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleDetectionRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleLinkingRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectTrajectoriesRelinkingRunPluginInterface;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OrphanedAnalysisContainer;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public abstract class OmegaTrajectoriesRelinkingPlugin extends
        OmegaAlgorithmPlugin implements OmegaSelectImagePluginInterface,
        OmegaSelectParticleDetectionRunPluginInterface,
        OmegaSelectParticleLinkingRunPluginInterface,
        OmegaSelectTrajectoriesRelinkingRunPluginInterface,
        OmegaLoadedAnalysisConsumerPluginInterface,
        OmegaImageConsumerPluginInterface,
        OmegaOrphanedAnalysisConsumerPluginInterface,
        OmegaLoaderPluginInterface {

	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	private OrphanedAnalysisContainer orphanedAnalysis;
	private List<OmegaImage> loadedImages;
	private OmegaGateway gateway;

	public OmegaTrajectoriesRelinkingPlugin() {
		this(1);
	}

	public OmegaTrajectoriesRelinkingPlugin(final int numOfPanels) {
		super(numOfPanels);

		this.loadedAnalysisRuns = null;
		this.orphanedAnalysis = null;
		this.loadedImages = null;
		this.gateway = null;
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
	public void setOrphanedAnalysis(
			final OrphanedAnalysisContainer orphanedAnalysis) {
		this.orphanedAnalysis = orphanedAnalysis;
	}

	@Override
	public OrphanedAnalysisContainer getOrphanedAnalysis() {
		return this.orphanedAnalysis;
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

	public abstract void selectCurrentTrajectoriesRelinkingRun(
	        OmegaAnalysisRun analysisRun);
}
