package edu.umassmed.omega.commons.genericPlugins;

import java.awt.image.BufferedImage;
import java.util.List;

import edu.umassmed.omega.commons.genericInterfaces.OmegaImageConsumerPluginInterface;
import edu.umassmed.omega.commons.genericInterfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.genericInterfaces.OmegaTrackingDataConsumerPluginInterface;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public abstract class OmegaTrajectoryManagerPlugin extends OmegaPlugin
        implements OmegaTrackingDataConsumerPluginInterface,
        OmegaLoadedAnalysisConsumerPluginInterface,
        OmegaImageConsumerPluginInterface {

	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	private List<OmegaImage> loadedImages;

	public OmegaTrajectoryManagerPlugin() {
		this(1);
	}

	public OmegaTrajectoryManagerPlugin(final int numOfPanels) {
		super(numOfPanels);

		this.loadedAnalysisRuns = null;
		this.loadedImages = null;
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

	public abstract void updateTrajectories(List<OmegaTrajectory> trajectories,
	        boolean selection);

	public abstract void setBufferedImage(BufferedImage bufferedImage);
}
