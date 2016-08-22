package edu.umassmed.omega.core.runnables;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
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
	        final int tMax, final boolean hasIntensities) {
		this.omegaApp = omegaApp;
		this.segmentationRun = segmentationRun;
		final Map<OmegaTrajectory, List<OmegaSegment>> segments = segmentationRun
		        .getResultingSegments();
		if (hasIntensities) {
			this.intensityAnalizer = new OmegaIntensityAnalyzer(segments);
		} else {
			this.intensityAnalizer = null;
		}
		this.mobilityAnalizer = new OmegaMobilityAnalyzer(tMax, segments);
		this.velocityAnalizer = new OmegaVelocityAnalyzer(tMax, segments);
		final List<OmegaParameter> diffParams = new ArrayList<OmegaParameter>();
		diffParams.add(new OmegaParameter(
				OmegaConstants.PARAMETER_DIFFUSIVITY_WINDOW,
				OmegaConstants.PARAMETER_DIFFUSIVITY_WINDOW_3));
		diffParams
		.add(new OmegaParameter(
				OmegaConstants.PARAMETER_DIFFUSIVITY_LOG_OPTION,
				OmegaConstants.PARAMETER_DIFFUSIVITY_LOG_OPTION_LOG_AND_LINEAR));
		diffParams.add(new OmegaParameter(
				OmegaConstants.PARAMETER_ERROR_OPTION,
				OmegaConstants.PARAMETER_ERROR_OPTION_DISABLED));
		this.diffusivityAnalizer = new OmegaDiffusivityAnalyzer(segments,
				diffParams);
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
			OmegaLogFileManager.handleCoreException(ex);
		}

		Map<OmegaSegment, Double[]> peakSignals = null;
		Map<OmegaSegment, Double[]> meanSignals = null;
		Map<OmegaSegment, Double[]> localBackgrounds = null;
		Map<OmegaSegment, Double[]> localSNRs = null;
		if (this.intensityAnalizer != null) {
			peakSignals = this.intensityAnalizer.getPeakSignalsResults();
			meanSignals = this.intensityAnalizer.getMeanSignalsResults();
			localBackgrounds = this.intensityAnalizer
			        .getLocalBackgroundsResults();
			localSNRs = this.intensityAnalizer.getLocalSNRsResults();
		} else {
			peakSignals = new LinkedHashMap<OmegaSegment, Double[]>();
			meanSignals = new LinkedHashMap<OmegaSegment, Double[]>();
			localBackgrounds = new LinkedHashMap<OmegaSegment, Double[]>();
			localSNRs = new LinkedHashMap<OmegaSegment, Double[]>();
		}
		// TODO to be changed somehow
		this.omegaApp.updateTrackingMeasuresAnalizerResults(
		        this.segmentationRun,
		        peakSignals,
		        meanSignals,
		        localBackgrounds,
		        localSNRs,
		        this.mobilityAnalizer.getDistancesResults(),
		        this.mobilityAnalizer.getDisplacementsResults(),
		        this.mobilityAnalizer.getMaxDisplacementsResults(),
		        this.mobilityAnalizer.getTotalTimeTraveledResults(),
		        this.mobilityAnalizer.getConfinementRatioResults(),
		        this.mobilityAnalizer.getAnglesAndDirectionalChangesResults(),
		        this.velocityAnalizer.getLocalSpeedResults(),
		        this.velocityAnalizer.getLocalVelocityResults(),
		        this.velocityAnalizer.getAverageCurvilinearSpeedResults(),
		        this.velocityAnalizer.getAverageStraightLineVelocityResults(),
		        this.velocityAnalizer.getForwardProgressionLinearityResults(),
		        this.diffusivityAnalizer.getNyResults(),
		        this.diffusivityAnalizer.getMuResults(),
		        this.diffusivityAnalizer.getLogMuResults(),
		        this.diffusivityAnalizer.getDeltaTResults(),
		        this.diffusivityAnalizer.getLogDeltaTResults(),
		        this.diffusivityAnalizer.getGammaDResults(),
		        this.diffusivityAnalizer.getGammaDFromLogResults(),
		        // this.diffusivityAnalizer.getGammaResults(),
		        this.diffusivityAnalizer.getGammaFromLogResults(),
		        // this.diffusivityAnalizer.getSmssResults(),
		        this.diffusivityAnalizer.getSmssFromLogResults(),
				// this.diffusivityAnalizer.getErrors(),
				this.diffusivityAnalizer.getErrorsFromLog(),
				this.diffusivityAnalizer.getSNRRun(),
		        this.diffusivityAnalizer.getTrackiMeasuresDiffusivityRun());
	}
}
