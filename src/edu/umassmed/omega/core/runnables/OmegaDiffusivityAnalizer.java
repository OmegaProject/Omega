package edu.umassmed.omega.core.runnables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.libraries.OmegaDiffusivityLibrary;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaDiffusivityAnalizer implements Runnable {

	private final List<OmegaTrajectory> trajectories;
	private final Map<OmegaTrajectory, Double[]> nyMap;
	private final Map<OmegaTrajectory, Double[]> gammaFromLogMap;
	private final Map<OmegaTrajectory, Double[]> gammaMap;
	private final Map<OmegaTrajectory, Double[][]> gammaDFromLogMap;
	private final Map<OmegaTrajectory, Double[][]> gammaDMap;
	private final Map<OmegaTrajectory, Double[][]> logMuMap;
	private final Map<OmegaTrajectory, Double[][]> muMap;
	private final Map<OmegaTrajectory, Double[][]> logDeltaTMap;
	private final Map<OmegaTrajectory, Double[][]> deltaTMap;
	private final Map<OmegaTrajectory, Double[]> smssFromLogMap;
	private final Map<OmegaTrajectory, Double[]> smssMap;

	public OmegaDiffusivityAnalizer(final List<OmegaTrajectory> trajectories) {
		this.trajectories = trajectories;
		this.nyMap = new LinkedHashMap<>();
		this.gammaFromLogMap = new LinkedHashMap<>();
		this.gammaMap = new LinkedHashMap<>();
		this.gammaDFromLogMap = new LinkedHashMap<>();
		this.gammaDMap = new LinkedHashMap<>();
		this.logMuMap = new LinkedHashMap<>();
		this.muMap = new LinkedHashMap<>();
		this.logDeltaTMap = new LinkedHashMap<>();
		this.deltaTMap = new LinkedHashMap<>();
		this.smssFromLogMap = new LinkedHashMap<>();
		this.smssMap = new LinkedHashMap<>();
	}

	@Override
	public void run() {
		for (final OmegaTrajectory track : this.trajectories) {
			final Double[] x = new Double[track.getLength()];
			final Double[] y = new Double[track.getLength()];
			for (int i = 0; i < track.getLength(); i++) {
				final OmegaROI roi = track.getROIs().get(i);
				x[i] = roi.getX();
				y[i] = roi.getY();
			}
			final int windowDivisor = 3;
			if ((x.length / 3) < 2) {
				System.out.println(track.getName() + " skipped because length="
				        + x.length + " divided by wd=" + windowDivisor
				        + " less than 2");
				continue;
			}
			final int Delta_t = 1; // Time between frames?
			final Double[][][] nu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS = OmegaDiffusivityLibrary
			        .computeNu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS(x, y,
			                windowDivisor, Delta_t);
			final Double[] ny = nu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS[0][0];
			final Double[][] deltaT = nu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS[1];
			final Double[][] mu = nu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS[2];
			final Double[][] gammaD = nu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS[3];
			final Double[] gamma = new Double[ny.length];
			for (int i = 0; i < gammaD.length; i++) {
				gamma[i] = gammaD[i][0];
			}
			final Double[] smss = nu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS[4][0];

			final Double[][][] nu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog = OmegaDiffusivityLibrary
			        .computeNu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog(
			                x, y, windowDivisor, Delta_t);
			// final Double[] ny = nu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS[0][0];
			final Double[][] logDeltaT = nu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog[1];
			final Double[][] logMu = nu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog[2];
			final Double[][] gammaDFromLog = nu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog[3];
			final Double[] gammaFromLog = new Double[ny.length];
			for (int i = 0; i < gammaD.length; i++) {
				gammaFromLog[i] = gammaDFromLog[i][0];
			}
			final Double[] smssFromLog = nu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog[4][0];

			this.nyMap.put(track, ny);
			this.gammaFromLogMap.put(track, gammaFromLog);
			this.gammaMap.put(track, gamma);
			this.logDeltaTMap.put(track, logDeltaT);
			this.deltaTMap.put(track, deltaT);
			this.logMuMap.put(track, logMu);
			this.muMap.put(track, mu);
			this.gammaDFromLogMap.put(track, gammaDFromLog);
			this.gammaDMap.put(track, gammaD);
			this.smssFromLogMap.put(track, smssFromLog);
			this.smssMap.put(track, smss);
		}
	}

	public Map<OmegaTrajectory, Double[]> getNyResults() {
		return this.nyMap;
	}

	public Map<OmegaTrajectory, Double[]> getGammaFromLogResults() {
		return this.gammaFromLogMap;
	}

	public Map<OmegaTrajectory, Double[]> getGammaResults() {
		return this.gammaMap;
	}

	public Map<OmegaTrajectory, Double[][]> getGammaDFromLogResults() {
		return this.gammaDFromLogMap;
	}

	public Map<OmegaTrajectory, Double[][]> getGammaDResults() {
		return this.gammaDMap;
	}

	public Map<OmegaTrajectory, Double[][]> getLogMuResults() {
		return this.logMuMap;
	}

	public Map<OmegaTrajectory, Double[][]> getMuResults() {
		return this.muMap;
	}

	public Map<OmegaTrajectory, Double[][]> getLogDeltaTResults() {
		return this.logDeltaTMap;
	}

	public Map<OmegaTrajectory, Double[][]> getDeltaTResults() {
		return this.deltaTMap;
	}

	public Map<OmegaTrajectory, Double[]> getSmssFromLogResults() {
		return this.smssFromLogMap;
	}

	public Map<OmegaTrajectory, Double[]> getSmssResults() {
		return this.smssMap;
	}
}
