/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo Sbalzarini and Mario Valle.
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
package edu.umassmed.omega.sdSbalzariniPlugin.runnable;

import ij.process.ColorProcessor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaConstantsError;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.commons.utilities.OmegaImageUtilities;
import edu.umassmed.omega.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.data.coreElements.OmegaFrame;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.sdSbalzariniPlugin.SDConstants;

public class SDRunner implements SDRunnable {
	private static final String RUNNER = "Runner service: ";
	private final OmegaMessageDisplayerPanelInterface displayerPanel;

	private final Map<OmegaImage, List<OmegaParameter>> imagesToProcess;
	private final Map<OmegaImage, Map<OmegaFrame, List<OmegaROI>>> resultingParticles;
	private final Map<OmegaImage, Map<OmegaROI, Map<String, Object>>> resultingParticlesValues;

	private final OmegaGateway gateway;
	private final boolean isDebugMode, isPreview;
	private boolean isJobCompleted, isTerminated;

	public SDRunner(final OmegaMessageDisplayerPanelInterface displayerPanel) {
		this.displayerPanel = displayerPanel;

		this.imagesToProcess = null;
		this.gateway = null;

		this.isDebugMode = true;
		this.isPreview = false;
		this.isJobCompleted = false;

		this.resultingParticles = new LinkedHashMap<OmegaImage, Map<OmegaFrame, List<OmegaROI>>>();
		this.resultingParticlesValues = new LinkedHashMap<OmegaImage, Map<OmegaROI, Map<String, Object>>>();
	}

	public SDRunner(final OmegaMessageDisplayerPanelInterface displayerPanel,
			final Map<OmegaImage, List<OmegaParameter>> imageToProcess,
			final OmegaGateway gateway, final boolean isPreview) {
		this.displayerPanel = displayerPanel;

		this.imagesToProcess = new LinkedHashMap<>(imageToProcess);
		this.gateway = gateway;

		this.isDebugMode = false;
		this.isPreview = isPreview;
		this.isJobCompleted = false;

		this.resultingParticles = new LinkedHashMap<OmegaImage, Map<OmegaFrame, List<OmegaROI>>>();
		this.resultingParticlesValues = new LinkedHashMap<OmegaImage, Map<OmegaROI, Map<String, Object>>>();
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
		this.updateStatusSync(SDRunner.RUNNER + " started.", false);

		if (this.isDebugMode) {
			this.debugModeRun();
		} else {
			this.normalModeRun();
		}

		this.isJobCompleted = true;

		if (this.isPreview) {
			this.updateStatusAsync(SDRunner.RUNNER + " preview completed.",
			        true);
		} else {
			this.updateStatusAsync(SDRunner.RUNNER + " batch completed.", true);
		}
	}

	private void normalModeRun() {
		for (final OmegaImage image : this.imagesToProcess.keySet()) {
			final List<OmegaParameter> parameters = this.imagesToProcess
					.get(image);
			final OmegaImagePixels defaultPixels = image.getDefaultPixels();
			final long pixelsID = defaultPixels.getElementID();
			final int sizeX = defaultPixels.getSizeX();
			final int sizeY = defaultPixels.getSizeY();
			final int sizeT = defaultPixels.getSizeT();
			final int byteWidth = this.gateway.getByteWidth(pixelsID);
			// number of frames for this image
			// final int byteWidth = this.gateway.getByteWidth(pixelsID);

			if (sizeT < 2) {
				// TODO throw error and skip image or stop thread?
			}

			Integer radius = null;
			Double cutoff = null;
			Float percentile = null;
			Boolean percAbs = null;
			Integer z = null, c = null;
			for (int i = 0; i < parameters.size(); i++) {
				final OmegaParameter param = parameters.get(i);
				if (param.getName().equals(SDConstants.PARAM_RADIUS)) {
					radius = (Integer) param.getValue();
				} else if (param.getName().equals(SDConstants.PARAM_CUTOFF)) {
					cutoff = (Double) param.getValue();
				} else if (param.getName().equals(SDConstants.PARAM_PERCENTILE)) {
					percentile = (Float) param.getValue() / 100;
				} else if (param.getName().equals(
						SDConstants.PARAM_PERCENTILE_ABS)) {
					percAbs = (Boolean) param.getValue();
				} else if (param.getName().equals(SDConstants.PARAM_ZSECTION)) {
					z = (Integer) param.getValue();
				} else if (param.getName().equals(SDConstants.PARAM_CHANNEL)) {
					c = (Integer) param.getValue();
				} else
					return;
			}

			if ((radius == null) || (cutoff == null) || (percentile == null))
				// TODO ERROR
				return;

			if ((z == null) || (c == null))
				// TODO ERROR
				return;

			final boolean error = false;

			final Map<OmegaFrame, List<OmegaROI>> frames = new LinkedHashMap<OmegaFrame, List<OmegaROI>>();

			this.updateStatusSync(SDRunner.RUNNER
			        + " calculating global values on image " + image.getName(),
			        false);

			Float globalMin = Float.MAX_VALUE, globalMax = 0F;
			for (int t = 0; t < sizeT; t++) {

				final byte[] pixels = this.gateway.getImageData(pixelsID, z, t,
				        c);
				final ColorProcessor bp = new ColorProcessor(sizeX, sizeY,
				        OmegaImageUtilities.convertByteToIntImage(byteWidth,
				                pixels));
				if (globalMin > bp.getMin()) {
					globalMin = Float.valueOf(String.valueOf(bp.getMin()));
				}
				if (globalMax < bp.getMax()) {
					globalMax = Float.valueOf(String.valueOf(bp.getMax()));
				}
			}

			this.updateStatusSync(SDRunner.RUNNER + " processing image "
			        + image.getName(), false);

			final List<SDWorker> workers = new ArrayList<SDWorker>(), completedWorkers = new ArrayList<SDWorker>();
			final ExecutorService executor = Executors.newFixedThreadPool(5);
			if (this.isPreview) {
				final SDWorker worker = new SDWorker(this.gateway,
						defaultPixels, 0, radius, cutoff, percentile, percAbs,
				        c, z, globalMin, globalMax);
				workers.add(worker);
				executor.execute(worker);
			} else {
				for (int t = 0; t < sizeT; t++) {
					final SDWorker worker = new SDWorker(this.gateway,
					        defaultPixels, t, radius, cutoff, percentile,
							percAbs, c, z, globalMin, globalMax);
					workers.add(worker);
					executor.execute(worker);
				}
			}

			int completed = 0;
			while (!executor.isTerminated()) {
				if (this.isTerminated) {
					for (final SDWorker worker : workers) {
						worker.terminate();
						// TODO to be fixed because if everything terminate
						// there are not results to get, find a solution
					}
				}
				if (workers.isEmpty()) {
					executor.shutdown();
				}
				for (final SDWorker worker : workers) {
					if (!worker.isJobCompleted()) {
						continue;
					}
					completed++;
					this.updateStatusSync(
							SDRunner.RUNNER + " image " + image.getName()
							+ ", completed frame(s) " + completed + "/"
							+ sizeT, false);
					completedWorkers.add(worker);
				}
				workers.removeAll(completedWorkers);
			}

			final Map<OmegaROI, Map<String, Object>> particleValues = new LinkedHashMap<OmegaROI, Map<String, Object>>();
			for (final SDWorker worker : completedWorkers) {
				final OmegaFrame frame = worker.getFrame();
				final List<OmegaROI> particles = worker.getResultingParticles();
				particleValues.putAll(worker.getParticlesAdditionalValues());
				frames.put(frame, particles);
			}

			this.resultingParticles.put(image, frames);
			this.resultingParticlesValues.put(image, particleValues);

			if (this.isTerminated)
				return;

			if (error) {
				JOptionPane.showMessageDialog(null,
						OmegaConstantsError.ERROR_DURING_SPT_RUN,
						OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void debugModeRun() {

	}

	public Map<OmegaImage, List<OmegaParameter>> getImageParameters() {
		return this.imagesToProcess;
	}

	public Map<OmegaImage, Map<OmegaFrame, List<OmegaROI>>> getImageResultingParticles() {
		return this.resultingParticles;
	}

	public Map<OmegaImage, Map<OmegaROI, Map<String, Object>>> getImageParticlesAdditionalValues() {
		return this.resultingParticlesValues;
	}

	public void terminate() {
		this.isTerminated = true;
	}

	private void updateStatusSync(final String msg, final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					SDRunner.this.displayerPanel
					.updateMessageStatus(new SDMessageEvent(msg,
							SDRunner.this, ended,
							SDRunner.this.isPreview));
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
				SDRunner.this.displayerPanel
				.updateMessageStatus(new SDMessageEvent(msg,
						SDRunner.this, ended, SDRunner.this.isPreview));
			}
		});
	}
}
