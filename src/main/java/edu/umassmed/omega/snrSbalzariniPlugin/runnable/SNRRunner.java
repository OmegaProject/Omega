/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo ErrorIndex and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.snrSbalzariniPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.snrSbalzariniPlugin.SNRConstants;

public class SNRRunner implements SNRRunnable {
	private static final String RUNNER = "Runner service: ";
	private final OmegaMessageDisplayerPanelInterface displayerPanel;

	private final Map<OmegaParticleDetectionRun, List<OmegaParameter>> particlesToProcess;
	private final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> resultingImageBGR;
	private final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> resultingImageNoise;
	private final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> resultingImageAvgSNR;
	private final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> resultingImageMinSNR;
	private final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> resultingImageMaxSNR;
	private final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> resultingImageAvgErrorIndexSNR;
	private final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> resultingImageMinErrorIndexSNR;
	private final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> resultingImageMaxErrorIndexSNR;
	private final Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> resultingLocalCenterSignals;
	private final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> resultingLocalMeanSignals;
	private final Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> resultingLocalSignalSizes;
	private final Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> resultingLocalPeakSignals;
	private final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> resultingLocalNoises;
	private final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> resultingLocalSNRs;
	private final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> resultingLocalErrorIndexSNRs;

	private final OmegaGateway gateway;
	private final boolean isDebugMode;
	private boolean isJobCompleted, isTerminated;

	private final Map<Thread, SNREstimator> workers, workersCompleted;

	public SNRRunner(final OmegaMessageDisplayerPanelInterface displayerPanel) {
		this.displayerPanel = displayerPanel;

		this.particlesToProcess = null;
		this.gateway = null;

		this.isDebugMode = true;

		this.isJobCompleted = false;
		this.isTerminated = false;

		this.resultingImageBGR = new LinkedHashMap<>();
		this.resultingImageNoise = new LinkedHashMap<>();
		this.resultingImageAvgSNR = new LinkedHashMap<>();
		this.resultingImageMinSNR = new LinkedHashMap<>();
		this.resultingImageMaxSNR = new LinkedHashMap<>();
		this.resultingImageAvgErrorIndexSNR = new LinkedHashMap<>();
		this.resultingImageMinErrorIndexSNR = new LinkedHashMap<>();
		this.resultingImageMaxErrorIndexSNR = new LinkedHashMap<>();
		this.resultingLocalCenterSignals = new LinkedHashMap<>();
		this.resultingLocalMeanSignals = new LinkedHashMap<>();
		this.resultingLocalSignalSizes = new LinkedHashMap<>();
		this.resultingLocalPeakSignals = new LinkedHashMap<>();
		this.resultingLocalNoises = new LinkedHashMap<>();
		this.resultingLocalSNRs = new LinkedHashMap<>();
		this.resultingLocalErrorIndexSNRs = new LinkedHashMap<>();

		this.workers = new LinkedHashMap<>();
		this.workersCompleted = new LinkedHashMap<>();
	}

	public SNRRunner(
			final OmegaMessageDisplayerPanelInterface displayerPanel,
			final Map<OmegaParticleDetectionRun, List<OmegaParameter>> particlesToProcess,
			final OmegaGateway gateway) {
		this.displayerPanel = displayerPanel;

		this.particlesToProcess = new LinkedHashMap<>(particlesToProcess);
		this.gateway = gateway;

		this.isDebugMode = false;

		this.isJobCompleted = false;

		this.resultingImageBGR = new LinkedHashMap<>();
		this.resultingImageNoise = new LinkedHashMap<>();
		this.resultingImageAvgSNR = new LinkedHashMap<>();
		this.resultingImageMinSNR = new LinkedHashMap<>();
		this.resultingImageMaxSNR = new LinkedHashMap<>();
		this.resultingImageAvgErrorIndexSNR = new LinkedHashMap<>();
		this.resultingImageMinErrorIndexSNR = new LinkedHashMap<>();
		this.resultingImageMaxErrorIndexSNR = new LinkedHashMap<>();
		this.resultingLocalCenterSignals = new LinkedHashMap<>();
		this.resultingLocalMeanSignals = new LinkedHashMap<>();
		this.resultingLocalSignalSizes = new LinkedHashMap<>();
		this.resultingLocalPeakSignals = new LinkedHashMap<>();
		this.resultingLocalNoises = new LinkedHashMap<>();
		this.resultingLocalSNRs = new LinkedHashMap<>();
		this.resultingLocalErrorIndexSNRs = new LinkedHashMap<>();

		this.workers = new LinkedHashMap<>();
		this.workersCompleted = new LinkedHashMap<>();
	}

	@Override
	public boolean isJobCompleted() {
		return this.isJobCompleted;
	}

	@Override
	public void run() {
		// TODO move the call in the panel action listeners that setup the
		// thread
		// JPanelSPT.this.switchControlsStatus();
		// JPanelSPT.this.jButtonDisplayTracks.setEnabled(false);

		// ==============================
		// for each image to be processed
		// ==============================
		// final ArrayList<ImageDataHandler> images =
		// JPanelSPT.this.sptParametersHandler
		// .getImages();
		// final Iterator<ImageDataHandler> it = images.iterator();
		this.updateStatusSync(SNRRunner.RUNNER + " started.", false);

		if (this.isDebugMode) {
			this.debugModeRun();
		} else {
			this.normalModeRun();
		}

		this.isJobCompleted = true;

		this.updateStatusAsync(SNRRunner.RUNNER + " ended.", true);
	}

	private void normalModeRun() {
		for (final OmegaParticleDetectionRun spotDetRun : this.particlesToProcess
				.keySet()) {
			final List<OmegaParameter> parameters = this.particlesToProcess
					.get(spotDetRun);

			final Map<OmegaPlane, List<OmegaROI>> particles = spotDetRun
					.getResultingParticles();

			OmegaImagePixels pixels = null;
			for (final OmegaPlane frame : particles.keySet()) {
				pixels = frame.getParentPixels();
				break;
			}

			final int t = pixels.getSizeT();

			if (t < 2) {
				// TODO throw error and skip image or stop thread?
			}

			Integer radius = null;
			Double threshold = null;
			String method = null;
			for (int i = 0; i < parameters.size(); i++) {
				final OmegaParameter param = parameters.get(i);
				if (param.getName() == SNRConstants.PARAM_RADIUS) {
					radius = (Integer) param.getValue();
				} else if (param.getName() == SNRConstants.PARAM_THRESHOLD) {
					threshold = (Double) param.getValue();
				} else if (param.getName() == SNRConstants.PARAM_SNR_METHOD) {
					method = (String) param.getValue();
				} else
					return;
			}

			for (final OmegaPlane frame : particles.keySet()) {
				final List<OmegaROI> rois = particles.get(frame);
				final SNREstimator estimator = new SNREstimator(
						this.displayerPanel, this.gateway, frame, rois, radius,
						threshold, method);
				final Thread thread = new Thread(estimator);
				this.workers.put(thread, estimator);
				thread.start();
			}

			int workersCounter = 0;
			final int workersPrepared = this.workers.size();

			final Double increase = 100.0 / workersPrepared;
			Double completed = 0.0;
			while (workersCounter < workersPrepared) {
				Thread threadFinished = null;
				for (final Thread thread : this.workers.keySet()) {
					final SNREstimator estimator = this.workers.get(thread);
					if (estimator.isJobCompleted()) {
						threadFinished = thread;
						this.workersCompleted.put(thread, estimator);
						break;
					}
				}
				if (threadFinished != null) {
					this.workers.remove(threadFinished);
					workersCounter++;
					completed += increase;
					this.updateStatusSync(SNRRunner.RUNNER + " " + completed
							+ " completed.", false);
				}
				if (this.isTerminated)
					return;
			}

			// wait until the all threads are finished before process the
			// next element in queue
			try {
				for (final Thread thread : this.workersCompleted.keySet()) {
					thread.join();
				}
			} catch (final Exception ex) {
				OmegaLogFileManager.handleUncaughtException(ex);
			}

			final Map<OmegaPlane, Double> imageBGRMap = new LinkedHashMap<>();
			final Map<OmegaPlane, Double> imageNoiseMap = new LinkedHashMap<>();
			final Map<OmegaPlane, Double> imageAvgSNRMap = new LinkedHashMap<>();
			final Map<OmegaPlane, Double> imageMinSNRMap = new LinkedHashMap<>();
			final Map<OmegaPlane, Double> imageMaxSNRMap = new LinkedHashMap<>();
			final Map<OmegaPlane, Double> imageAvgErrorIndexSNRMap = new LinkedHashMap<>();
			final Map<OmegaPlane, Double> imageMinErrorIndexSNRMap = new LinkedHashMap<>();
			final Map<OmegaPlane, Double> imageMaxErrorIndexSNRMap = new LinkedHashMap<>();
			final Map<OmegaROI, Integer> localCenterSignals = new LinkedHashMap<>();
			final Map<OmegaROI, Double> localMeanSignals = new LinkedHashMap<>();
			final Map<OmegaROI, Integer> localSignalSizes = new LinkedHashMap<>();
			final Map<OmegaROI, Integer> localPeakSignals = new LinkedHashMap<>();
			final Map<OmegaROI, Double> localNoises = new LinkedHashMap<>();
			final Map<OmegaROI, Double> localSNRs = new LinkedHashMap<>();
			final Map<OmegaROI, Double> localErrorIndexSNRs = new LinkedHashMap<>();
			for (final Thread thread : this.workersCompleted.keySet()) {
				final SNREstimator estimator = this.workersCompleted
						.get(thread);
				imageBGRMap.put(estimator.getFrame(),
						estimator.getImageBackground());
				imageNoiseMap.put(estimator.getFrame(),
						estimator.getImageNoise());
				imageAvgSNRMap.put(estimator.getFrame(),
				        estimator.getAverageSNR());
				imageMinSNRMap.put(estimator.getFrame(),
						estimator.getMinimumSNR());
				imageMaxSNRMap.put(estimator.getFrame(),
						estimator.getMaximumSNR());
				imageAvgErrorIndexSNRMap.put(estimator.getFrame(),
				        estimator.getAverageErrorIndexSNR());
				imageMinErrorIndexSNRMap.put(estimator.getFrame(),
						estimator.getMinimumErrorIndexSNR());
				imageMaxErrorIndexSNRMap.put(estimator.getFrame(),
						estimator.getMaximumErrorIndexSNR());
				localCenterSignals.putAll(estimator.getLocalCenterSignals());
				localMeanSignals.putAll(estimator.getLocalMeanSignals());
				localSignalSizes.putAll(estimator.getLocalSignalSizes());
				localPeakSignals.putAll(estimator.getLocalPeakSignals());
				localNoises.putAll(estimator.getLocalNoises());
				localSNRs.putAll(estimator.getLocalSNRs());
				localErrorIndexSNRs.putAll(estimator.getLocalErrorIndexSNRs());
			}

			this.resultingImageBGR.put(spotDetRun, imageBGRMap);
			this.resultingImageNoise.put(spotDetRun, imageNoiseMap);
			this.resultingImageAvgSNR.put(spotDetRun, imageAvgSNRMap);
			this.resultingImageMinSNR.put(spotDetRun, imageMinSNRMap);
			this.resultingImageMaxSNR.put(spotDetRun, imageMaxSNRMap);
			this.resultingImageAvgErrorIndexSNR.put(spotDetRun,
					imageAvgErrorIndexSNRMap);
			this.resultingImageMinErrorIndexSNR.put(spotDetRun,
					imageMinErrorIndexSNRMap);
			this.resultingImageMaxErrorIndexSNR.put(spotDetRun,
					imageMaxErrorIndexSNRMap);
			this.resultingLocalCenterSignals
			.put(spotDetRun, localCenterSignals);
			this.resultingLocalMeanSignals.put(spotDetRun, localMeanSignals);
			this.resultingLocalSignalSizes.put(spotDetRun, localSignalSizes);
			this.resultingLocalPeakSignals.put(spotDetRun, localPeakSignals);
			this.resultingLocalNoises.put(spotDetRun, localNoises);
			this.resultingLocalSNRs.put(spotDetRun, localSNRs);
			this.resultingLocalErrorIndexSNRs.put(spotDetRun,
					localErrorIndexSNRs);
			// TODO get data and send
		}
	}

	private void debugModeRun() {

	}

	public Map<OmegaParticleDetectionRun, List<OmegaParameter>> getParticleToProcess() {
		return this.particlesToProcess;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> getResultingImageBackground() {
		return this.resultingImageBGR;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> getResultingImageAverageSNR() {
		return this.resultingImageAvgSNR;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> getResultingImageMinimumSNR() {
		return this.resultingImageMinSNR;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> getResultingImageMaximumSNR() {
		return this.resultingImageMaxSNR;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> getResultingImageAverageErrorIndexSNR() {
		return this.resultingImageAvgErrorIndexSNR;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> getResultingImageMinimumErrorIndexSNR() {
		return this.resultingImageMinErrorIndexSNR;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> getResultingImageMaximumErrorIndexSNR() {
		return this.resultingImageMaxErrorIndexSNR;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> getResultingImageNoise() {
		return this.resultingImageNoise;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> getResultingLocalCentralSignals() {
		return this.resultingLocalCenterSignals;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> getResultingLocalMeanSignals() {
		return this.resultingLocalMeanSignals;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> getResultingLocalSignalSizes() {
		return this.resultingLocalSignalSizes;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> getResultingLocalPeakSignals() {
		return this.resultingLocalPeakSignals;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> getResultingLocalNoises() {
		return this.resultingLocalNoises;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> getResultingLocalSNRs() {
		return this.resultingLocalSNRs;
	}

	public Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> getResultingLocalErrorIndexSNRs() {
		return this.resultingLocalErrorIndexSNRs;
	}

	public void terminate() {
		this.isTerminated = true;
		for (final Thread t : this.workers.keySet()) {
			this.workers.get(t).terminate();
		}
		for (final Thread t : this.workersCompleted.keySet()) {
			this.workersCompleted.get(t).terminate();
		}
	}

	private void updateStatusSync(final String msg, final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					SNRRunner.this.displayerPanel
					.updateMessageStatus(new SNRMessageEvent(msg,
							SNRRunner.this, ended));
				}
			});
		} catch (final InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (final InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	private void updateStatusAsync(final String msg, final boolean ended) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SNRRunner.this.displayerPanel
				.updateMessageStatus(new SNRMessageEvent(msg,
						SNRRunner.this, ended));
			}
		});
	}
}
