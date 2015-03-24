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
package edu.umassmed.omega.sptSbalzariniPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.data.coreElements.OmegaFrame;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class SPTRunner implements SPTRunnable {
	private static final String RUNNER = "Runner service: ";
	private final OmegaMessageDisplayerPanelInterface displayerPanel;

	private final Map<OmegaImage, List<OmegaParameter>> imagesToProcess;
	final Map<OmegaImage, List<OmegaTrajectory>> resultingTrajectories;
	final Map<OmegaImage, Map<OmegaFrame, List<OmegaROI>>> resultingParticles;

	private final OmegaGateway gateway;
	private final boolean isDebugMode;
	private boolean isJobCompleted, isKilled;

	private SPTLoader loader;
	private SPTWriter writer;

	public SPTRunner(final OmegaMessageDisplayerPanelInterface displayerPanel) {
		this.displayerPanel = displayerPanel;

		this.imagesToProcess = null;
		this.gateway = null;

		this.isDebugMode = true;

		this.isJobCompleted = false;
		this.isKilled = false;

		this.resultingTrajectories = new HashMap<OmegaImage, List<OmegaTrajectory>>();
		this.resultingParticles = new HashMap<OmegaImage, Map<OmegaFrame, List<OmegaROI>>>();
	}

	public SPTRunner(final OmegaMessageDisplayerPanelInterface displayerPanel,
	        final Map<OmegaImage, List<OmegaParameter>> imagesToProcess,
	        final OmegaGateway gateway) {
		this.displayerPanel = displayerPanel;

		this.imagesToProcess = new LinkedHashMap<>(imagesToProcess);
		this.gateway = gateway;

		this.isDebugMode = false;

		this.isJobCompleted = false;

		this.resultingTrajectories = new HashMap<OmegaImage, List<OmegaTrajectory>>();
		this.resultingParticles = new HashMap<OmegaImage, Map<OmegaFrame, List<OmegaROI>>>();
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
		this.updateStatusSync(SPTRunner.RUNNER + " started.", false);

		if (this.isDebugMode) {
			this.debugModeRun();
		} else {
			this.normalModeRun();
		}

		this.isJobCompleted = true;

		this.updateStatusAsync(SPTRunner.RUNNER + " ended.", true);
		// TODO Update panel at the end of the process
		// JPanelSPT.this.jLabelStatus.setText("done");
		// JPanelSPT.this.switchControlsStatus();
		// JPanelSPT.this.jButtonDisplayTracks.setEnabled(true);
	}

	private void normalModeRun() {
		for (final OmegaImage image : this.imagesToProcess.keySet()) {
			final List<OmegaParameter> parameters = this.imagesToProcess
			        .get(image);
			// while (it.hasNext()) {
			// final ImageDataHandler imageDataHandler = it.next();

			// TODO Update SPT panel with parameter of the image
			// X Y T

			// JPanelSPT.this.width.getF1().setText(
			// String.valueOf(imageDataHandler.getX()));
			// JPanelSPT.this.height.getF1().setText(
			// String.valueOf(imageDataHandler.getY()));
			// JPanelSPT.this.time.getF1().setText(
			// String.valueOf(imageDataHandler.getT()));

			// check and add the pixels sizes
			final OmegaImagePixels defaultPixels = image.getDefaultPixels();

			final Long pixelsID = defaultPixels.getElementID();

			final int x = defaultPixels.getSizeX();
			final int y = defaultPixels.getSizeY();
			final int z = defaultPixels.getSelectedZ();
			final int t = defaultPixels.getSizeT();
			final int c = defaultPixels.getSelectedC();

			if (t < 2) {
				// TODO throw error and skip image or stop thread?
			}
			if ((c == 0) || (c > 1)) {
				// TODO throw error and skip image or stop thread?
			}

			// TODO check these data?
			defaultPixels.getPixelSizeX();
			defaultPixels.getPixelSizeY();
			this.gateway.getTotalT(pixelsID, z, t, c);

			boolean dllInit = true;
			int minPoints = -1;
			try {
				// init the Runner
				SPTDLLInvoker.callInitRunner();

				for (int i = 0; i < parameters.size(); i++) {
					final OmegaParameter param = parameters.get(i);

					if (param.getName() == "minPoints") {
						minPoints = (Integer) param.getValue();
					} else {
						final String value = param.getStringValue();
						if (value != "") {
							SPTDLLInvoker.callSetParameter("p" + i, value);
						} else {
							// TODO throw error
						}

					}
				}

				SPTDLLInvoker.callSetParameter("p5", String.valueOf(t));
				SPTDLLInvoker.callSetParameter("p6", String.valueOf(x));
				SPTDLLInvoker.callSetParameter("p7", String.valueOf(y));

				// Min val
				SPTDLLInvoker.callSetParameter("p8", "0.");

				// Max val
				final int bits = (int) Math.pow(2,
				        this.gateway.getByteWidth(pixelsID) * 8);

				SPTDLLInvoker
				        .callSetParameter("p9", String.format("%s.", bits));
				// SPTDLLInvoker.callSetParameter("p9", "255.");

				// set the minimun number of points
				// SPTDLLInvoker.callSetMinPoints(minPoints);

				this.updateStatusSync(SPTRunner.RUNNER
				        + " correctly initialized.", false);

				// start the Runner
				SPTDLLInvoker.callStartRunner();
			} catch (final Exception ex) {
				OmegaLogFileManager.handleUncaughtException(ex);
				dllInit = false;
			}

			if (!dllInit) {
				this.updateStatusSync(SPTRunner.RUNNER
				        + " unable to initialize dll.", false);
				return;
			}
			// TODO update panel with running image name and other available
			// infos
			// JPanelSPT.this.jLabelStatus.setText(String.format(
			// OmegaConstants.INFO_SPT_RUNNING,
			// imageDataHandler.getImageName()));

			this.updateStatusSync(SPTRunner.RUNNER + " process.", false);

			final ArrayList<Thread> threads = new ArrayList<Thread>();

			// load the images into the SPT DLL
			this.loader = new SPTLoader(this.displayerPanel, image, z, c,
			        this.gateway);
			final Thread loaderT = new Thread(this.loader);
			loaderT.setName(this.loader.getClass().getSimpleName());
			OmegaLogFileManager.registerAsExceptionHandlerOnThread(loaderT);
			loaderT.start();
			threads.add(loaderT);

			// write the results to file
			this.writer = new SPTWriter(this.displayerPanel);
			final Thread writerT = new Thread(this.writer);
			writerT.setName(this.writer.getClass().getSimpleName());
			OmegaLogFileManager.registerAsExceptionHandlerOnThread(writerT);
			writerT.start();
			threads.add(writerT);

			while (!this.loader.isJobCompleted()
			        || !this.writer.isJobCompleted()) {
				this.updateStatusSync(SPTRunner.RUNNER + " waiting results.",
				        false);
				if (this.isKilled)
					return;
			}

			final List<OmegaTrajectory> trajectories = new ArrayList<OmegaTrajectory>();
			final Map<OmegaFrame, List<OmegaROI>> particles = new HashMap<OmegaFrame, List<OmegaROI>>();
			final Object trackList = this.writer.getTrackList();
			if (trackList != null) {
				final List<OmegaTrajectory> tracks = (List<OmegaTrajectory>) trackList;
				int counter = 0;
				for (final OmegaTrajectory track : tracks) {
					track.setName(track.getName() + "_" + counter);
					// System.out.println("Track: " + track.getName());
					counter++;
					for (final OmegaROI point : track.getROIs()) {
						final int frameIndex = point.getFrameIndex() - 1;
						point.setFrameIndex(frameIndex);
						final OmegaFrame frame = defaultPixels.getFrames(c, z)
						        .get(frameIndex);
						// System.out.println("FI: " + frame.getIndex() + " X: "
						// + point.getX() + " Y: " + point.getY());
						List<OmegaROI> framePoints;
						if (particles.containsKey(frame)) {
							framePoints = particles.get(frame);
						} else {
							framePoints = new ArrayList<OmegaROI>();
						}
						framePoints.add(point);
						particles.put(frame, framePoints);
					}
					if (track.getLength() < minPoints) {
						continue;
					}
					trajectories.add(track);
				}
				this.resultingParticles.put(image, particles);
				this.resultingTrajectories.put(image, trajectories);
			} else {
				// TODO gestire eccezione
			}

			// wait until the two threads are finished before process the
			// next image
			try {
				loaderT.join();
				writerT.join();
			} catch (final Exception ex) {
				OmegaLogFileManager.handleUncaughtException(ex);
			}

			// when done, write SPT information on file (for each image)

			// final String infoFile = outputDir
			// + System.getProperty("file.separator")
			// + OmegaConstants.SPT_INFORMATION_FILE;
			// final SPTInformationWriter trackingInfoWriter = new
			// SPTInformationFileWriter(
			// infoFile);
			// trackingInfoWriter.initWriter();
			// final String temp1 = trackingInfoWriter
			// .writeInformation(executionInfo);
			// trackingInfoWriter.closeWriter();

			// write stats
			// final SPTStatsFileWriter sptStatsFileWriter = new
			// SPTStatsFileWriter(
			// JPanelSPT.this.mainFrame, outputDir,
			// imageDataHandler.getT());
			// sptStatsFileWriter.initWriter();
			// final String temp2 =
			// sptStatsFileWriter.calculateAndWriteStats();
			// sptStatsFileWriter.closeWriter();

			// display the stats JFrame
			// TODO output
			// final String statsString = temp1 + "\n" + temp2;
			//
			// final JFrameStats jfs = new JFrameStats();
			// jfs.getjTextArea1().setText(statsString);
			// jfs.setLocation(
			// JPanelSPT.this.mainFrame.getX() + 300,
			// (JPanelSPT.this.mainFrame.getY() + (JPanelSPT.this.mainFrame
			// .getHeight() / 2)) - 150);
			// jfs.setVisible(true);
		}
	}

	private void debugModeRun() {
		final SPTWriter writer = new SPTWriter(this.displayerPanel);
		final Thread writerT = new Thread(writer);
		writerT.setName(writer.getClass().getSimpleName());
		OmegaLogFileManager.registerAsExceptionHandlerOnThread(writerT);
		writerT.start();

		// wait until the two threads are finished before process the
		// next
		// image
		while (!writer.isJobCompleted()) {
			// TODO do something here, update gui
		}

		final Object trackList = writer.getTrackList();
		if (trackList != null) {
			System.out.println(trackList.getClass());
		}
		try {
			writerT.join();
		} catch (final InterruptedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	public Map<OmegaImage, List<OmegaParameter>> getImageParameters() {
		return this.imagesToProcess;
	}

	public Map<OmegaImage, Map<OmegaFrame, List<OmegaROI>>> getImageResultingParticles() {
		return this.resultingParticles;
	}

	public Map<OmegaImage, List<OmegaTrajectory>> getImageResultingTrajectories() {
		return this.resultingTrajectories;
	}

	public void kill() {
		this.isKilled = true;
		this.loader.kill();
		this.writer.kill();
	}

	private void updateStatusSync(final String msg, final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					SPTRunner.this.displayerPanel
					        .updateMessageStatus(new SPTMessageEvent(msg,
					                SPTRunner.this, ended));
				}
			});
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void updateStatusAsync(final String msg, final boolean ended) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SPTRunner.this.displayerPanel
				        .updateMessageStatus(new SPTMessageEvent(msg,
				                SPTRunner.this, ended));
			}
		});
	}
}
