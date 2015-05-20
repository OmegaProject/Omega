package edu.umassmed.omega.core.runnables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleLinkingRun;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaTrackingMeasuresAnalizer implements Runnable {

	private final OmegaApplication omegaApp;
	private final OmegaParticleLinkingRun particleLinkingRun;

	private final OmegaIntensityAnalizer intensityAnalizer;
	private final OmegaMobilityAnalizer mobilityAnalizer;
	private final OmegaVelocityAnalizer velocityAnalizer;
	private final OmegaDiffusivityAnalizer diffusivityAnalizer;

	public OmegaTrackingMeasuresAnalizer(final OmegaApplication omegaApp,
	        final OmegaParticleLinkingRun particleLinkingRun, final int tMax,
			final boolean hasIntensities) {
		this.omegaApp = omegaApp;
		this.particleLinkingRun = particleLinkingRun;
		final List<OmegaTrajectory> trajectories = particleLinkingRun
		        .getResultingTrajectories();
		if (hasIntensities) {
			this.intensityAnalizer = new OmegaIntensityAnalizer(trajectories);
		} else {
			this.intensityAnalizer = null;
		}
		this.mobilityAnalizer = new OmegaMobilityAnalizer(tMax, trajectories);
		this.velocityAnalizer = new OmegaVelocityAnalizer(tMax, trajectories);
		this.diffusivityAnalizer = new OmegaDiffusivityAnalizer(trajectories);
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

		Map<OmegaTrajectory, Double[]> peakSignals = null;
		Map<OmegaTrajectory, Double[]> meanSignals = null;
		Map<OmegaTrajectory, Double[]> localBackgrounds = null;
		Map<OmegaTrajectory, Double[]> localSNRs = null;
		if (this.intensityAnalizer != null) {
			peakSignals = this.intensityAnalizer.getPeakSignalsResults();
			meanSignals = this.intensityAnalizer.getMeanSignalsResults();
			localBackgrounds = this.intensityAnalizer
			        .getLocalBackgroundsResults();
			localSNRs = this.intensityAnalizer.getLocalSNRsResults();
		} else {
			peakSignals = new LinkedHashMap<OmegaTrajectory, Double[]>();
			meanSignals = new LinkedHashMap<OmegaTrajectory, Double[]>();
			localBackgrounds = new LinkedHashMap<OmegaTrajectory, Double[]>();
			localSNRs = new LinkedHashMap<OmegaTrajectory, Double[]>();
		}
		// TODO to be changed somehow
		this.omegaApp.updateTrackingMeasuresAnalizerResults(
		        this.particleLinkingRun, peakSignals, meanSignals,
		        localBackgrounds, localSNRs,
		        this.mobilityAnalizer.getDistancesResults(),
		        this.mobilityAnalizer.getDisplacementsResults(),
		        this.mobilityAnalizer.getMaxDisplacementsResults(),
		        this.mobilityAnalizer.getTotalTimeTraveledResults(),
		        this.mobilityAnalizer.getConfinementRatioResults(),
		        this.mobilityAnalizer.getAnglesAndDirectionalChangesResults(),
		        this.velocityAnalizer.getLocalSpeedResults(),
		        this.velocityAnalizer.getLocalVelocityResults(),
		        this.velocityAnalizer.getMeanSpeedResults(),
		        this.velocityAnalizer.getMeanVelocityResults(),
		        this.diffusivityAnalizer.getNyResults(),
		        this.diffusivityAnalizer.getMuResults(),
		        this.diffusivityAnalizer.getLogMuResults(),
		        this.diffusivityAnalizer.getDeltaTResults(),
		        this.diffusivityAnalizer.getLogDeltaTResults(),
		        this.diffusivityAnalizer.getGammaDResults(),
		        this.diffusivityAnalizer.getGammaDFromLogResults(),
		        this.diffusivityAnalizer.getGammaResults(),
		        this.diffusivityAnalizer.getGammaFromLogResults(),
		        this.diffusivityAnalizer.getSmssResults(),
		        this.diffusivityAnalizer.getSmssFromLogResults());
	}
}
