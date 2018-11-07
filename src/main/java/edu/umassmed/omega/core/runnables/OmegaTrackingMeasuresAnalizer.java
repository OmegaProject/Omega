package edu.umassmed.omega.core.runnables;

import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.runnable.OmegaDiffusivityAnalyzer;
import edu.umassmed.omega.commons.runnable.OmegaIntensityAnalyzer;
import edu.umassmed.omega.commons.runnable.OmegaMobilityAnalyzer;
import edu.umassmed.omega.commons.runnable.OmegaVelocityAnalyzer;
import edu.umassmed.omega.core.OmegaApplication;

public class OmegaTrackingMeasuresAnalizer implements Runnable {
	
	private final OmegaApplication omegaApp;
	private final OmegaTrajectoriesSegmentationRun segmentationRun;
	
	private final OmegaIntensityAnalyzer intensityAnalizer;
	private final OmegaMobilityAnalyzer mobilityAnalizer;
	private final OmegaVelocityAnalyzer velocityAnalizer;
	private final OmegaDiffusivityAnalyzer diffusivityAnalizer;
	
	public OmegaTrackingMeasuresAnalizer(final OmegaApplication omegaApp,
			final OmegaTrajectoriesSegmentationRun segmentationRun,
			final double physicalT, final int tMax,
			final List<OmegaParameter> intParams,
			final List<OmegaParameter> diffParams) {
		this.omegaApp = omegaApp;
		this.segmentationRun = segmentationRun;
		final Map<OmegaTrajectory, List<OmegaSegment>> segments = segmentationRun
				.getResultingSegments();
		this.intensityAnalizer = new OmegaIntensityAnalyzer(segmentationRun,
				segments, intParams);
		this.mobilityAnalizer = new OmegaMobilityAnalyzer(physicalT, tMax,
				segmentationRun, segments);
		this.velocityAnalizer = new OmegaVelocityAnalyzer(physicalT, tMax,
				segmentationRun, segments);
		this.diffusivityAnalizer = new OmegaDiffusivityAnalyzer(physicalT,
				segmentationRun, segments, diffParams);
	}

	public List<OmegaParameter> getIntensityParameters() {
		return this.intensityAnalizer.getParameters();
	}

	public List<OmegaParameter> getDiffusivityParameters() {
		return this.diffusivityAnalizer.getParameters();
	}
	
	@Override
	public void run() {
		Thread t1 = null;
		if (this.intensityAnalizer != null) {
			t1 = new Thread(this.intensityAnalizer);
			t1.setName("IntensityAnalizer");
			t1.start();
		}
		
		final Thread t2 = new Thread(this.mobilityAnalizer);
		t2.setName("MobilityAnalizer");
		t2.start();
		
		final Thread t3 = new Thread(this.velocityAnalizer);
		t3.setName("VelocityAnalizer");
		t3.start();
		
		final Thread t4 = new Thread(this.diffusivityAnalizer);
		t4.setName("DiffusivityAnalizer");
		t4.start();
		
		try {
			if (this.intensityAnalizer != null) {
				t1.join();
			}
			t2.join();
			t3.join();
			t4.join();
		} catch (final InterruptedException ex) {
			OmegaLogFileManager.handleCoreException(ex, true);
		}
		
		// TODO to be changed somehow
		this.omegaApp.updateTrackingMeasuresAnalizerResults(
				this.segmentationRun,
				this.intensityAnalizer.getParameters(),
				this.intensityAnalizer.getPeakSignalsResults(),
				this.intensityAnalizer.getCentroidSignalsResults(),
				this.intensityAnalizer.getPeakSignalsLocalResults(),
				this.intensityAnalizer.getCentroidSignalsLocalResults(),
				this.intensityAnalizer.getBackgroundsResults(),
				this.intensityAnalizer.getNoisesResults(),
				this.intensityAnalizer.getSNRsResults(),
				this.intensityAnalizer.getAreasResults(),
				this.intensityAnalizer.getMeanSignalsResults(),
				this.intensityAnalizer.getBackgroundsLocalResults(),
				this.intensityAnalizer.getNoisesLocalResults(),
				this.intensityAnalizer.getSNRsLocalResults(),
				this.intensityAnalizer.getAreasLocalResults(),
				this.intensityAnalizer.getMeanSignalsLocalResults(),
				this.intensityAnalizer.getSNRRun(),
				this.mobilityAnalizer.getDistancesResults(),
				this.mobilityAnalizer.getDistancesFromOriginResults(),
				this.mobilityAnalizer.getDisplacementsFromOriginResults(),
				this.mobilityAnalizer.getMaxDisplacementsFromOriginResults(),
				this.mobilityAnalizer.getTotalTimeTraveledResults(),
				this.mobilityAnalizer.getConfinementRatioResults(),
				this.mobilityAnalizer.getAnglesAndDirectionalChangesResults(),
				this.velocityAnalizer.getLocalSpeedResults(),
				this.velocityAnalizer.getLocalSpeedFromOriginResults(),
				this.velocityAnalizer.getLocalVelocityFromOriginResults(),
				this.velocityAnalizer.getAverageCurvilinearSpeedResults(),
				this.velocityAnalizer.getAverageStraightLineVelocityResults(),
				this.velocityAnalizer.getForwardProgressionLinearityResults(),
				this.diffusivityAnalizer.getParameters(),
				this.diffusivityAnalizer.getNyResults(),
				this.diffusivityAnalizer.getMuResults(),
				this.diffusivityAnalizer.getLogMuResults(),
				this.diffusivityAnalizer.getDeltaTResults(),
				this.diffusivityAnalizer.getLogDeltaTResults(),
				this.diffusivityAnalizer.getGammaDResults(),
				this.diffusivityAnalizer.getGammaDFromLogResults(),
				// this.diffusivityAnalizer.getGammaResults(),
				// this.diffusivityAnalizer.getGammaFromLogResults(),
				// this.diffusivityAnalizer.getSmssResults(),
				this.diffusivityAnalizer.getSmssFromLogResults(),
				// this.diffusivityAnalizer.getErrors(),
				this.diffusivityAnalizer.getErrorsFromLog(),
				this.diffusivityAnalizer.getMinimumDetectableODC(),
				this.diffusivityAnalizer.getSNRRun(),
				this.diffusivityAnalizer.getTrackingMeasuresDiffusivityRun());
	}
}
