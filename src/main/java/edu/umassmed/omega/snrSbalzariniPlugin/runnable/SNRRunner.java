/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School Alessandro
 * Rigano (Program in Molecular Medicine) Caterina Strambio De Castillia
 * (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo ErrorIndex and Mario Valle.
 *
 * Key contacts: Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.snrSbalzariniPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
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
	
	private final Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaParameter>>> particlesToProcess;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageAvgCenterSignal;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageAvgPeakSignal;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageAvgMeanSignal;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageBGR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageNoise;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageAvgSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageMinSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageMaxSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageAvgErrorIndexSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageMinErrorIndexSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> resultingImageMaxErrorIndexSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>> resultingLocalCenterSignals;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> resultingLocalMeanSignals;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>> resultingLocalSignalSizes;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>> resultingLocalPeakSignals;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> resultingLocalBackgrounds;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> resultingLocalNoises;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> resultingLocalSNRs;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> resultingLocalErrorIndexSNRs;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingAvgCenterSignal;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingAvgPeakSignal;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingAvgMeanSignal;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingBGR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingNoise;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingAvgSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingMinSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingMaxSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingAvgErrorIndexSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingMinErrorIndexSNR;
	private final Map<Integer, Map<OmegaParticleDetectionRun, Double>> resultingMaxErrorIndexSNR;
	
	private final OmegaGateway gateway;
	private final boolean isDebugMode;
	private boolean isJobCompleted, isTerminated;
	
	// private final Map<Thread, SNREstimator> workers, workersCompleted;
	
	public SNRRunner(final OmegaMessageDisplayerPanelInterface displayerPanel) {
		this.displayerPanel = displayerPanel;
		
		this.particlesToProcess = null;
		this.gateway = null;
		
		this.isDebugMode = true;
		
		this.isJobCompleted = false;
		this.isTerminated = false;
		
		this.resultingImageAvgCenterSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageAvgPeakSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageAvgMeanSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageBGR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageNoise = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageAvgSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageMinSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageMaxSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageAvgErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageMinErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageMaxErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingLocalCenterSignals = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>>();
		this.resultingLocalMeanSignals = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingLocalSignalSizes = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>>();
		this.resultingLocalPeakSignals = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>>();
		this.resultingLocalBackgrounds = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingLocalNoises = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingLocalSNRs = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingLocalErrorIndexSNRs = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingAvgCenterSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingAvgPeakSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingAvgMeanSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingBGR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingNoise = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingAvgSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingMinSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingMaxSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingAvgErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingMinErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingMaxErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		
		// this.workers = new LinkedHashMap<Thread, SNREstimator>();
		// this.workersCompleted = new LinkedHashMap<Thread, SNREstimator>();
	}
	
	public SNRRunner(
			final OmegaMessageDisplayerPanelInterface displayerPanel,
			final Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaParameter>>> particlesToProcess,
			final OmegaGateway gateway) {
		this.displayerPanel = displayerPanel;
		
		this.particlesToProcess = new LinkedHashMap<>(particlesToProcess);
		this.gateway = gateway;
		
		this.isDebugMode = false;
		
		this.isJobCompleted = false;
		
		this.resultingImageAvgCenterSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageAvgPeakSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageAvgMeanSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageBGR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageNoise = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageAvgSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageMinSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageMaxSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageAvgErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageMinErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingImageMaxErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>>();
		this.resultingLocalCenterSignals = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>>();
		this.resultingLocalMeanSignals = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingLocalSignalSizes = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>>();
		this.resultingLocalPeakSignals = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>>();
		this.resultingLocalBackgrounds = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingLocalNoises = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingLocalSNRs = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingLocalErrorIndexSNRs = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>>();
		this.resultingAvgCenterSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingAvgPeakSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingAvgMeanSignal = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingBGR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingNoise = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingAvgSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingMinSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingMaxSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingAvgErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingMinErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		this.resultingMaxErrorIndexSNR = new LinkedHashMap<Integer, Map<OmegaParticleDetectionRun, Double>>();
		
		// this.workers = new LinkedHashMap<>();
		// this.workersCompleted = new LinkedHashMap<>();
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
		for (final Integer index : this.particlesToProcess.keySet()) {
			for (final OmegaParticleDetectionRun spotDetRun : this.particlesToProcess
					.get(index).keySet()) {
				final List<OmegaParameter> parameters = this.particlesToProcess
						.get(index).get(spotDetRun);
				
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
				Integer z = null;
				Integer c = null;
				for (int i = 0; i < parameters.size(); i++) {
					final OmegaParameter param = parameters.get(i);
					if (param.getName().equals(
							OmegaConstantsAlgorithmParameters.PARAM_RADIUS)) {
						radius = (Integer) param.getValue();
					} else if (param.getName().equals(
							OmegaConstantsAlgorithmParameters.PARAM_THRESHOLD)) {
						threshold = (Double) param.getValue();
					} else if (param.getName().equals(
							SNRConstants.PARAM_SNR_METHOD)) {
						method = (String) param.getValue();
					} else if (param.getName().equals(
							OmegaConstantsAlgorithmParameters.PARAM_ZSECTION)) {
						z = (Integer) param.getValue();
					} else if (param.getName().equals(
							OmegaConstantsAlgorithmParameters.PARAM_CHANNEL)) {
						c = (Integer) param.getValue();
					} else
						return;
				}
				
				final List<SNREstimator> workers = new ArrayList<SNREstimator>();
				final List<SNREstimator> completedWorkers = new ArrayList<SNREstimator>();
				final ExecutorService loaderExecutor = Executors
						.newFixedThreadPool(10);
				
				for (final OmegaPlane frame : particles.keySet()) {
					final List<OmegaROI> rois = particles.get(frame);
					final SNREstimator estimator = new SNREstimator(
							this.displayerPanel, this.gateway, frame, rois,
							radius, threshold, method, c, z);
					// OmegaLogFileManager.appendToPluginLog(
					// this.plugin,
					// "Creating " + sizeT + " SDWorkers for "
					// + image.getName());
					workers.add(estimator);
					loaderExecutor.execute(estimator);
					// final Thread thread = new Thread(estimator);
					// this.workers.put(thread, estimator);
					// thread.start();
				}
				String name = "NA";
				if (pixels.getParentImage() != null) {
					name = pixels.getParentImage().getName();
				}
				
				this.waitForExecutor(loaderExecutor, workers, completedWorkers,
						name, t, false);
				
				this.orderList(completedWorkers);
				
				// int workersCounter = 0;
				// final int workersPrepared = this.workers.size();
				//
				// final Double increase = 100.0 / workersPrepared;
				// Double completed = 0.0;
				// while (workersCounter < workersPrepared) {
				// Thread threadFinished = null;
				// for (final Thread thread : this.workers.keySet()) {
				// final SNREstimator estimator = this.workers.get(thread);
				// if (estimator.isJobCompleted()) {
				// threadFinished = thread;
				// this.workersCompleted.put(thread, estimator);
				// break;
				// }
				// }
				// if (threadFinished != null) {
				// this.workers.remove(threadFinished);
				// workersCounter++;
				// completed += increase;
				// this.updateStatusSync(SNRRunner.RUNNER + " "
				// + completed + " completed.", false);
				// }
				// if (this.isTerminated)
				// return;
				// }
				
				// wait until the all threads are finished before process the
				// next element in queue
				// try {
				// for (final Thread thread : this.workersCompleted.keySet()) {
				// thread.join();
				// }
				// } catch (final Exception ex) {
				// OmegaLogFileManager.handleUncaughtException(ex, true);
				// }
				
				final Map<OmegaPlane, Double> imageAvgCenterSignalMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageAvgPeakSignalMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageAvgMeanSignalMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageBGRMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageNoiseMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageAvgSNRMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageMinSNRMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageMaxSNRMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageAvgErrorIndexSNRMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageMinErrorIndexSNRMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaPlane, Double> imageMaxErrorIndexSNRMap = new LinkedHashMap<OmegaPlane, Double>();
				final Map<OmegaROI, Integer> localCenterSignalsMap = new LinkedHashMap<OmegaROI, Integer>();
				final Map<OmegaROI, Double> localMeanSignalsMap = new LinkedHashMap<OmegaROI, Double>();
				final Map<OmegaROI, Integer> localSignalSizesMap = new LinkedHashMap<OmegaROI, Integer>();
				final Map<OmegaROI, Integer> localPeakSignalsMap = new LinkedHashMap<OmegaROI, Integer>();
				final Map<OmegaROI, Double> localBackgroundsMap = new LinkedHashMap<OmegaROI, Double>();
				final Map<OmegaROI, Double> localNoisesMap = new LinkedHashMap<OmegaROI, Double>();
				final Map<OmegaROI, Double> localSNRsMap = new LinkedHashMap<OmegaROI, Double>();
				final Map<OmegaROI, Double> localErrorIndexSNRsMap = new LinkedHashMap<OmegaROI, Double>();
				
				int counter = 0;
				double avgCenterSignal = 0.0;
				double avgPeakSignal = 0.0;
				double avgMeanSignal = 0.0;
				double bgr = 0.0;
				double noise = 0.0;
				double avgSNR = 0.0;
				double maxSNR = Double.MIN_VALUE;
				double minSNR = Double.MAX_VALUE;
				double avgIndexSNR = 0.0;
				double maxIndexSNR = Double.MIN_VALUE;
				double minIndexSNR = Double.MAX_VALUE;
				for (final SNREstimator estimator : completedWorkers) {
					avgCenterSignal += estimator.getAverageCenterSignal();
					avgPeakSignal += estimator.getAveragePeakSignal();
					avgMeanSignal += estimator.getAverageMeanSignal();
					bgr += estimator.getImageBackground();
					noise += estimator.getImageNoise();
					avgSNR += estimator.getAverageSNR();
					if (maxSNR < estimator.getMaximumSNR()) {
						maxSNR = estimator.getMaximumSNR();
					}
					// maxSNR += estimator.getMaximumSNR();
					if (minSNR > estimator.getMinimumSNR()) {
						minSNR = estimator.getMinimumSNR();
					}
					// minSNR += estimator.getMinimumSNR();
					avgIndexSNR += estimator.getAverageErrorIndexSNR();
					if (maxIndexSNR < estimator.getMaximumErrorIndexSNR()) {
						maxIndexSNR = estimator.getMaximumErrorIndexSNR();
					}
					// maxIndexSNR += estimator.getMaximumErrorIndexSNR();
					if (minIndexSNR < estimator.getMinimumErrorIndexSNR()) {
						minIndexSNR = estimator.getMinimumErrorIndexSNR();
					}
					// minIndexSNR += estimator.getMinimumErrorIndexSNR();
					imageAvgCenterSignalMap.put(estimator.getFrame(),
							estimator.getAverageCenterSignal());
					imageAvgPeakSignalMap.put(estimator.getFrame(),
							estimator.getAveragePeakSignal());
					imageAvgMeanSignalMap.put(estimator.getFrame(),
							estimator.getAverageMeanSignal());
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
					localCenterSignalsMap.putAll(estimator
							.getLocalCenterSignals());
					localMeanSignalsMap.putAll(estimator.getLocalMeanSignals());
					localSignalSizesMap.putAll(estimator.getLocalSignalSizes());
					localPeakSignalsMap.putAll(estimator.getLocalPeakSignals());
					localNoisesMap.putAll(estimator.getLocalNoises());
					localBackgroundsMap.putAll(estimator.getLocalBackgrounds());
					localSNRsMap.putAll(estimator.getLocalSNRs());
					localErrorIndexSNRsMap.putAll(estimator
							.getLocalErrorIndexSNRs());
					counter++;
				}
				
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageAvgCenterSignal = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageAvgCenterSignal.put(spotDetRun, imageAvgCenterSignalMap);
				this.resultingImageAvgCenterSignal.put(index,
						imageAvgCenterSignal);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageAvgPeakSignal = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageAvgPeakSignal.put(spotDetRun, imageAvgPeakSignalMap);
				this.resultingImageAvgPeakSignal.put(index, imageAvgPeakSignal);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageAvgMeanSignal = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageAvgMeanSignal.put(spotDetRun, imageAvgMeanSignalMap);
				this.resultingImageAvgMeanSignal.put(index, imageAvgMeanSignal);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageBgr = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageBgr.put(spotDetRun, imageBGRMap);
				this.resultingImageBGR.put(index, imageBgr);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageNoise = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageNoise.put(spotDetRun, imageNoiseMap);
				this.resultingImageNoise.put(index, imageNoise);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageAvgSNR = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageAvgSNR.put(spotDetRun, imageAvgSNRMap);
				this.resultingImageAvgSNR.put(index, imageAvgSNR);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageMinSNR = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageMinSNR.put(spotDetRun, imageMinSNRMap);
				this.resultingImageMinSNR.put(index, imageMinSNR);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageMaxSNR = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageMaxSNR.put(spotDetRun, imageMaxSNRMap);
				this.resultingImageMaxSNR.put(index, imageMaxSNR);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageAvgErrorIndexSNR = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageAvgErrorIndexSNR.put(spotDetRun, imageAvgErrorIndexSNRMap);
				this.resultingImageAvgErrorIndexSNR.put(index,
						imageAvgErrorIndexSNR);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageMinErrorIndexSNR = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageMinErrorIndexSNR.put(spotDetRun, imageMinErrorIndexSNRMap);
				this.resultingImageMinErrorIndexSNR.put(index,
						imageMinErrorIndexSNR);
				final Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>> imageMaxErrorIndexSNR = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>();
				imageMaxErrorIndexSNR.put(spotDetRun, imageMaxErrorIndexSNRMap);
				this.resultingImageMaxErrorIndexSNR.put(index,
						imageMaxErrorIndexSNR);
				
				final Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> localCenterSignals = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>();
				localCenterSignals.put(spotDetRun, localCenterSignalsMap);
				this.resultingLocalCenterSignals.put(index, localCenterSignals);
				final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> localMeanSignals = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaROI, Double>>();
				localMeanSignals.put(spotDetRun, localMeanSignalsMap);
				this.resultingLocalMeanSignals.put(index, localMeanSignals);
				final Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> localSignalSizes = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>();
				localSignalSizes.put(spotDetRun, localSignalSizesMap);
				this.resultingLocalSignalSizes.put(index, localSignalSizes);
				final Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>> localPeakSignals = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>();
				localPeakSignals.put(spotDetRun, localPeakSignalsMap);
				this.resultingLocalPeakSignals.put(index, localPeakSignals);
				final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> localBackgrounds = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaROI, Double>>();
				localBackgrounds.put(spotDetRun, localBackgroundsMap);
				this.resultingLocalBackgrounds.put(index, localBackgrounds);
				final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> localNoises = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaROI, Double>>();
				localNoises.put(spotDetRun, localNoisesMap);
				this.resultingLocalNoises.put(index, localNoises);
				final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> localSNRs = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaROI, Double>>();
				localSNRs.put(spotDetRun, localSNRsMap);
				this.resultingLocalSNRs.put(index, localSNRs);
				final Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>> localErrorIndexSNRs = new LinkedHashMap<OmegaParticleDetectionRun, Map<OmegaROI, Double>>();
				localErrorIndexSNRs.put(spotDetRun, localErrorIndexSNRsMap);
				this.resultingLocalErrorIndexSNRs.put(index,
						localErrorIndexSNRs);
				
				avgCenterSignal /= counter;
				avgPeakSignal /= counter;
				avgMeanSignal /= counter;
				bgr /= counter;
				noise /= counter;
				avgSNR /= counter;
				// maxSNR /= counter;
				// minSNR /= counter;
				avgIndexSNR /= counter;
				// maxIndexSNR /= counter;
				// minIndexSNR /= counter;
				
				final Map<OmegaParticleDetectionRun, Double> avgCenterSignalMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				avgCenterSignalMap.put(spotDetRun, avgCenterSignal);
				this.resultingAvgCenterSignal.put(index, avgCenterSignalMap);
				final Map<OmegaParticleDetectionRun, Double> avgPeakSignalMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				avgPeakSignalMap.put(spotDetRun, avgPeakSignal);
				this.resultingAvgPeakSignal.put(index, avgPeakSignalMap);
				final Map<OmegaParticleDetectionRun, Double> avgMeanSignalMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				avgMeanSignalMap.put(spotDetRun, avgMeanSignal);
				this.resultingAvgMeanSignal.put(index, avgMeanSignalMap);
				final Map<OmegaParticleDetectionRun, Double> bgrMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				bgrMap.put(spotDetRun, bgr);
				this.resultingBGR.put(index, bgrMap);
				final Map<OmegaParticleDetectionRun, Double> noiseMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				noiseMap.put(spotDetRun, noise);
				this.resultingNoise.put(index, noiseMap);
				final Map<OmegaParticleDetectionRun, Double> avgSNRMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				avgSNRMap.put(spotDetRun, avgSNR);
				this.resultingAvgSNR.put(index, avgSNRMap);
				final Map<OmegaParticleDetectionRun, Double> maxSNRMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				maxSNRMap.put(spotDetRun, maxSNR);
				this.resultingMaxSNR.put(index, maxSNRMap);
				final Map<OmegaParticleDetectionRun, Double> minSNRMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				minSNRMap.put(spotDetRun, minSNR);
				this.resultingMinSNR.put(index, minSNRMap);
				final Map<OmegaParticleDetectionRun, Double> avgIndexSNRMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				avgIndexSNRMap.put(spotDetRun, avgIndexSNR);
				this.resultingAvgErrorIndexSNR.put(index, avgIndexSNRMap);
				final Map<OmegaParticleDetectionRun, Double> minIndexSNRMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				minIndexSNRMap.put(spotDetRun, minIndexSNR);
				this.resultingMinErrorIndexSNR.put(index, minIndexSNRMap);
				final Map<OmegaParticleDetectionRun, Double> maxIndexSNRMap = new LinkedHashMap<OmegaParticleDetectionRun, Double>();
				maxIndexSNRMap.put(spotDetRun, maxIndexSNR);
				this.resultingMaxErrorIndexSNR.put(index, maxIndexSNRMap);
				// TODO get data and send
			}
		}
		try {
			this.gateway.cleanUpTemporaryData();
		} catch (final Exception ex) {
			// TODO fix exception handling
			ex.printStackTrace();
		}
	}
	
	private void debugModeRun() {
		
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaParameter>>> getParticleToProcess() {
		return this.particlesToProcess;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingAverageCenterSignal() {
		return this.resultingAvgCenterSignal;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingAveragePeakSignal() {
		return this.resultingAvgPeakSignal;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingAverageMeanSignal() {
		return this.resultingAvgMeanSignal;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingBackground() {
		return this.resultingBGR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingNoise() {
		return this.resultingNoise;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingAvgSNR() {
		return this.resultingAvgSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingMaxSNR() {
		return this.resultingMaxSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingMinSNR() {
		return this.resultingMinSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingAvgErrorIndexSNR() {
		return this.resultingAvgErrorIndexSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingMaxErrorIndexSNR() {
		return this.resultingMaxErrorIndexSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Double>> getResultingMinErrorIndexSNR() {
		return this.resultingMinErrorIndexSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageAverageCenterSignal() {
		return this.resultingImageAvgCenterSignal;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageAveragePeakSignal() {
		return this.resultingImageAvgPeakSignal;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageAverageMeanSignal() {
		return this.resultingImageAvgMeanSignal;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageBackground() {
		return this.resultingImageBGR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageAverageSNR() {
		return this.resultingImageAvgSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageMinimumSNR() {
		return this.resultingImageMinSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageMaximumSNR() {
		return this.resultingImageMaxSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageAverageErrorIndexSNR() {
		return this.resultingImageAvgErrorIndexSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageMinimumErrorIndexSNR() {
		return this.resultingImageMinErrorIndexSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageMaximumErrorIndexSNR() {
		return this.resultingImageMaxErrorIndexSNR;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaPlane, Double>>> getResultingImageNoise() {
		return this.resultingImageNoise;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>> getResultingLocalCentralSignals() {
		return this.resultingLocalCenterSignals;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> getResultingLocalMeanSignals() {
		return this.resultingLocalMeanSignals;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>> getResultingLocalSignalSizes() {
		return this.resultingLocalSignalSizes;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Integer>>> getResultingLocalPeakSignals() {
		return this.resultingLocalPeakSignals;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> getResultingLocalBackgrounds() {
		return this.resultingLocalBackgrounds;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> getResultingLocalNoises() {
		return this.resultingLocalNoises;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> getResultingLocalSNRs() {
		return this.resultingLocalSNRs;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, Map<OmegaROI, Double>>> getResultingLocalErrorIndexSNRs() {
		return this.resultingLocalErrorIndexSNRs;
	}
	
	private void waitForExecutor(final ExecutorService exec,
			final List<SNREstimator> workers,
			final List<SNREstimator> completedWorkers, final String imageName,
			final int sizeT, final boolean isLoading) {
		int completed = 0;
		while (!exec.isTerminated()) {
			if (this.isTerminated) {
				for (final SNREstimator runnable : workers) {
					runnable.terminate();
					// TODO to be fixed because if everything terminate
					// there are no results to get, find a solution
				}
			}
			if (workers.isEmpty()) {
				exec.shutdown();
			}
			for (final SNREstimator runnable : workers) {
				if (!runnable.isJobCompleted()) {
					continue;
				}
				completed++;
				final StringBuffer sb = new StringBuffer();
				sb.append(SNRRunner.RUNNER + " image " + imageName);
				if (isLoading) {
					sb.append(", frame(s) loaded ");
				} else {
					sb.append(", frame(s)  completed ");
				}
				sb.append(completed + "/" + sizeT);
				this.updateStatusSync(sb.toString(), false);
				completedWorkers.add(runnable);
			}
			workers.removeAll(completedWorkers);
		}
	}
	
	private void orderList(final List<SNREstimator> workers) {
		Collections.sort(workers, new Comparator<SNREstimator>() {
			@Override
			public int compare(final SNREstimator o1, final SNREstimator o2) {
				final int i1 = o1.getFrame().getIndex();
				final int i2 = o2.getFrame().getIndex();
				if (i1 < i2)
					return -1;
				else if (i1 > i2)
					return 1;
				return 0;
			}
		});
	}
	
	public void terminate() {
		this.isTerminated = true;
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
