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
package edu.umassmed.omega.sptPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.galliva.gallibrary.GLogManager;

import edu.umassmed.omega.commons.OmegaConstants;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaROI;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.sptPlugin.gui.SPTPluginPanel;

public class SPTRunner implements SPTRunnable {
	private final SPTPluginPanel pluginPanel;

	private final Map<OmegaImage, List<OmegaParameter>> imagesToProcess;

	final Map<OmegaImage, List<OmegaTrajectory>> resultingTrajectories;
	final Map<OmegaImage, Map<OmegaFrame, List<OmegaROI>>> resultingParticles;

	private final OmegaGateway gateway;

	private final boolean isDebugMode;

	private boolean isJobCompleted;

	public SPTRunner(final SPTPluginPanel pluginPanel) {
		this.pluginPanel = pluginPanel;

		this.imagesToProcess = null;
		this.gateway = null;

		this.isDebugMode = true;

		this.isJobCompleted = false;

		this.resultingTrajectories = new HashMap<OmegaImage, List<OmegaTrajectory>>();
		this.resultingParticles = new HashMap<OmegaImage, Map<OmegaFrame, List<OmegaROI>>>();
	}

	public SPTRunner(final SPTPluginPanel pluginPanel,
	        final Map<OmegaImage, List<OmegaParameter>> imagesToProcess,
	        final OmegaGateway gateway) {
		this.pluginPanel = pluginPanel;

		this.imagesToProcess = imagesToProcess;
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

		if (this.isDebugMode) {
			this.debugModeRun();
		} else {
			this.normalModeRun();
		}

		this.isJobCompleted = true;

		this.updateRunnerStatusAsync();
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

			defaultPixels.getPixelSizeX();
			defaultPixels.getPixelSizeY();
			this.gateway.getTotalT(pixelsID, z, t, c);

			// TODO Update SPT panel with parameter of the image
			// X Y T
			// JPanelSPT.this.width.getF2().setText(
			// totalWidth == 0.0 ? "-" : StringHelper.DoubleToString(
			// totalWidth, 3));
			// JPanelSPT.this.height.getF2().setText(
			// totalHeight == 0.0 ? "-" : StringHelper.DoubleToString(
			// totalHeight, 3));
			// JPanelSPT.this.time.getF2().setText(
			// totalTime == 0.0 ? "-" : StringHelper.DoubleToString(
			// totalTime, 3));
			//
			// JPanelSPT.this.width.getF3().setText(
			// widthSize == 0.0 ? "-" : StringHelper.DoubleToString(
			// widthSize, 3));
			// JPanelSPT.this.height.getF3().setText(
			// heightSize == 0.0 ? "-" : StringHelper.DoubleToString(
			// heightSize, 3));
			// JPanelSPT.this.time.getF3().setText(
			// avgFrameSize == 0.0 ? "-" : StringHelper.DoubleToString(
			// avgFrameSize, 3));

			// final String outputDir = this.trajectoriesOut
			// + System.getProperty("file.separator")
			// + StringHelper.removeFileExtension(image.getName());
			//
			// // empty the output directory if requested
			// if (FileUtilities.directoryExists(outputDir) &&
			// this.emptyDirectory) {
			// FileUtilities.emptyDirectory(outputDir);
			// } else {
			// // create the output dir for each image (if not exists)
			// FileUtilities.createDirectory(outputDir);
			// }

			int minPoints = -1;
			try {
				// init the Runner
				SPTDLLInvoker.callInitRunner();

				// set the output directory
				// SPTDLLInvoker.callSetOutputPath(outputDir);

				// set the Parameters
				// SPTDLLInvoker.callSetParameter("p0",
				// String.valueOf(jComboBoxRadius.getSelectedItem()));
				// SPTDLLInvoker.callSetParameter("p1",
				// String.valueOf(jComboBoxCutOff.getSelectedItem()));
				// SPTDLLInvoker.callSetParameter("p2",
				// String.valueOf(jComboBoxPercentile.getSelectedItem()));
				// SPTDLLInvoker.callSetParameter("p3",
				// String.valueOf(jComboBoxDisplacement.getSelectedItem()));
				// SPTDLLInvoker.callSetParameter("p4",
				// String.valueOf(jComboBoxLinkRange.getSelectedItem()));
				// p0 radius
				// p1 cutoff
				// p2 percentile
				// p3 displacement
				// p4 linkrange
				// p5 T
				// p6 X
				// p7 Y
				// p8 0.

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

				// start the Runner
				SPTDLLInvoker.callStartRunner();
			} catch (final Exception e) {
				JOptionPane.showMessageDialog(null,
				        OmegaConstants.ERROR_INIT_SPT_RUN,
				        OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
				GLogManager.log(String.format("%s: %s",
				        OmegaConstants.ERROR_INIT_SPT_RUN, e.toString()),
				        Level.SEVERE);
				return;
			}

			// TODO update panel with running image name and other available
			// infos
			// JPanelSPT.this.jLabelStatus.setText(String.format(
			// OmegaConstants.INFO_SPT_RUNNING,
			// imageDataHandler.getImageName()));

			final ArrayList<Thread> threads = new ArrayList<Thread>();

			// load the images into the SPT DLL
			final SPTLoaderRunner loader = new SPTLoaderRunner(image, z, c,
			        this.gateway);
			final Thread loaderT = new Thread(loader);
			loaderT.start();
			threads.add(loaderT);

			// write the results to file
			final SPTWriterRunner writer = new SPTWriterRunner();
			final Thread writerT = new Thread(writer);
			writerT.start();
			threads.add(writerT);

			while (!loader.isJobCompleted() || !writer.isJobCompleted()) {
				// TODO do something here, update gui
			}

			final List<OmegaTrajectory> trajectories = new ArrayList<OmegaTrajectory>();
			final Map<OmegaFrame, List<OmegaROI>> particles = new HashMap<OmegaFrame, List<OmegaROI>>();
			final Object trackList = writer.getTrackList();
			if (trackList != null) {
				final List<OmegaTrajectory> tracks = (List<OmegaTrajectory>) trackList;
				for (final OmegaTrajectory track : tracks) {
					for (final OmegaROI point : track.getROIs()) {
						final int frameIndex = point.getFrameIndex() - 1;
						final OmegaFrame frame = defaultPixels.getFrames().get(
						        frameIndex);
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
			} catch (final InterruptedException e) {
				// TODO gestire
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
		final SPTWriterRunner writer = new SPTWriterRunner();
		final Thread writerT = new Thread(writer);
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
		} catch (final InterruptedException e) {
			// TODO gestire
			e.printStackTrace();
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

	private void updateRunnerStatusSync() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					SPTRunner.this.pluginPanel.updateRunnerEnded();
				}
			});
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void updateRunnerStatusAsync() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SPTRunner.this.pluginPanel.updateRunnerEnded();
			}
		});
	}
}
