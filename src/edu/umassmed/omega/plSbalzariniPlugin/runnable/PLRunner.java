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
package edu.umassmed.omega.plSbalzariniPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.SwingUtilities;

import mosaic.core.detection.MyFrame;
import mosaic.core.detection.Particle;
import mosaic.core.particleLinking.ParticleLinker;
import mosaic.core.particleLinking.ParticleLinkerBestOnePerm;
import mosaic.core.particleLinking.ParticleLinkerHun;
import mosaic.core.particleLinking.linkerOptions;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.coreElements.OmegaFrame;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.plSbalzariniPlugin.PLConstants;

public class PLRunner implements PLRunnable {
	private static final String RUNNER = "Runner service: ";
	private final OmegaMessageDisplayerPanelInterface displayerPanel;

	private final Map<OmegaParticleDetectionRun, List<OmegaParameter>> particlesToProcess;
	private final Map<OmegaParticleDetectionRun, List<OmegaTrajectory>> resultingTrajectories;

	private final boolean isDebugMode;
	private boolean isJobCompleted, isTerminated;

	public PLRunner(final OmegaMessageDisplayerPanelInterface displayerPanel) {
		this.displayerPanel = displayerPanel;

		this.particlesToProcess = null;

		this.isDebugMode = true;

		this.isJobCompleted = false;
		this.isTerminated = false;

		this.resultingTrajectories = new LinkedHashMap<>();
	}

	public PLRunner(
			final OmegaMessageDisplayerPanelInterface displayerPanel,
			final Map<OmegaParticleDetectionRun, List<OmegaParameter>> particlesToProcess) {
		this.displayerPanel = displayerPanel;

		this.particlesToProcess = new LinkedHashMap<>(particlesToProcess);
		this.isDebugMode = false;

		this.isJobCompleted = false;

		this.resultingTrajectories = new LinkedHashMap<>();
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
		this.updateStatusSync(PLRunner.RUNNER + " started.", false);

		if (this.isDebugMode) {
			this.debugModeRun();
		} else {
			this.normalModeRun();
		}

		this.isJobCompleted = true;

		this.updateStatusAsync(PLRunner.RUNNER + " ended.", true);
	}

	private void normalModeRun() {
		for (final OmegaParticleDetectionRun spotDetRun : this.particlesToProcess
				.keySet()) {
			final List<OmegaParameter> parameters = this.particlesToProcess
					.get(spotDetRun);

			final Map<OmegaFrame, List<OmegaROI>> resultingParticles = spotDetRun
					.getResultingParticles();
			final Map<OmegaROI, Map<String, Object>> resultingParticlesValues = spotDetRun
					.getResultingParticlesValues();

			OmegaImagePixels pixels = null;
			for (final OmegaFrame frame : resultingParticles.keySet()) {
				pixels = frame.getParentPixels();
				break;
			}

			final int sizeT = pixels.getSizeT();

			if (sizeT < 2) {
				// TODO throw error and skip image or stop thread?
			}

			// this.gateway.getTotalT(pixelsID, z, sizeT, c);

			Float displacement = null;
			Integer linkrange = null;
			String movType = null;
			Float objectFeature = null;
			Float dynamics = null;
			String optimizer = null;
			for (int i = 0; i < parameters.size(); i++) {
				final OmegaParameter param = parameters.get(i);
				if (param.getName() == PLConstants.PARAM_DISPLACEMENT) {
					displacement = Float.valueOf(param.getStringValue());
				} else if (param.getName() == PLConstants.PARAM_LINKRANGE) {
					linkrange = Integer.valueOf(param.getStringValue());
				} else if (param.getName() == PLConstants.PARAM_MOVTYPE) {
					movType = param.getStringValue();
				} else if (param.getName() == PLConstants.PARAM_OBJFEATURE) {
					objectFeature = Float.valueOf(param.getStringValue());
				} else if (param.getName() == PLConstants.PARAM_DYNAMICS) {
					dynamics = Float.valueOf(param.getStringValue());
				} else if (param.getName() == PLConstants.PARAM_OPTIMIZER) {
					optimizer = param.getStringValue();
				}
			}

			final Map<Particle, OmegaROI> particlesMap = new LinkedHashMap<Particle, OmegaROI>();
			final MyFrame[] mosaicFrames = new MyFrame[sizeT];
			for (final OmegaFrame frame : resultingParticles.keySet()) {
				final List<OmegaROI> particles = resultingParticles.get(frame);
				final MyFrame mosaicFrame = new MyFrame();
				final int index = frame.getIndex();
				mosaicFrame.frame_number = index;
				// mosaicFrame.real_particles_number = particles.size();
				// final Integer radius = (Integer) spotDetRun
				// .getAlgorithmSpec()
				// .getParameter(
				// OmegaConstantsAlgorithmParameters.PARAM_RADIUS)
				// .getValue();
				// mosaicFrame.setParticleRadius(radius);
				final Vector<Particle> mosaicParticles = new Vector<Particle>();
				for (final OmegaROI particle : particles) {
					final float x = Float.valueOf(String.valueOf(particle
							.getX()));
					final float y = Float.valueOf(String.valueOf(particle
							.getY()));
					final Particle p = new Particle(x, y, 0, frame.getIndex(),
							linkrange);
					p.m0 = (Float) resultingParticlesValues.get(particle).get(
							"m0");
					// p.m1 = (Float)
					// resultingParticlesValues.get(particle).get(
					// "m1");
					p.m2 = (Float) resultingParticlesValues.get(particle).get(
							"m2");
					// p.m3 = (Float)
					// resultingParticlesValues.get(particle).get(
					// "m3");
					// p.m4 = (Float)
					// resultingParticlesValues.get(particle).get(
					// "m4");
					particlesMap.put(p, particle);
					mosaicParticles.add(p);
				}
				mosaicFrame.setParticles(mosaicParticles, particles.size());
				mosaicFrames[index] = mosaicFrame;
			}
			ParticleLinker linker = null;
			if (optimizer == PLConstants.PARAM_OPTIMIZER_GREEDY) {
				linker = new ParticleLinkerBestOnePerm();
			} else {
				linker = new ParticleLinkerHun();
			}
			final linkerOptions options = new linkerOptions();
			options.displacement = displacement;
			options.linkrange = linkrange;
			if (movType == PLConstants.PARAM_MOVTYPE_BROWNIAN) {
				options.force = false;
				options.straight_line = false;
			} else if (movType == PLConstants.PARAM_MOVTYPE_COSVEL) {
				options.force = true;
				options.straight_line = true;
			} else if (movType == PLConstants.PARAM_MOVTYPE_STRAIGHT) {
				options.force = false;
				options.straight_line = true;
			}
			options.l_s = 1f;
			options.l_f = objectFeature;
			options.l_d = dynamics;
			linker.linkParticles(mosaicFrames, sizeT, options);

			final List<List<Particle>> mosaicTracks = this
					.generateTrajectories(mosaicFrames, sizeT, linkrange);
			final List<OmegaTrajectory> tracks = new ArrayList<OmegaTrajectory>();
			for (final List<Particle> mosaicTrack : mosaicTracks) {
				final OmegaTrajectory track = new OmegaTrajectory(
						mosaicTrack.size());
				for (final Particle p : mosaicTrack) {
					final OmegaROI particle = particlesMap.get(p);
					track.addROI(particle);
				}
				tracks.add(track);
			}
			this.resultingTrajectories.put(spotDetRun, tracks);
		}
	}

	private List<List<Particle>> generateTrajectories(final MyFrame[] frames,
	        final int frames_number, final int linkrange) {
		int i, j, k;
		int found, n, m;
		// temporary vector to hold particles for current trajctory
		final List<List<Particle>> tracks = new ArrayList<List<Particle>>();
		final List<Particle> curr_track_particles = new ArrayList<Particle>();
		for (i = 0; i < frames_number; i++) {
			final Vector<Particle> particles = frames[i].getParticles();
			for (j = 0; j < particles.size(); j++) {
				if (!particles.get(j).special) {
					particles.get(j).special = true;
					found = -1;
					// go over all particles that this particle (particles[j])
					// is linked to
					for (n = 0; n < linkrange; n++) {
						// if it is NOT a dummy particle - stop looking
						if (particles.get(j).next[n] != -1) {
							found = n;
							break;
						}
					}
					// if this particle is not linked to any other
					// go to next particle and dont add a trajectory
					if (found == -1) {
						continue;
					}
					// Added by Guy Levy, 18.08.06 - A change form original
					// implementation
					// if this particle is linkd to a "real" paritcle that was
					// already linked
					// break the trajectory and start again from the next
					// particle. dont add a trajectory
					final Vector<Particle> particles2 = frames[i + n + 1]
					        .getParticles();
					if (particles2.get(particles.get(j).next[n]).special) {
						continue;
					}
					// this particle is linked to another "real" particle that
					// is not already linked so we have a trajectory
					curr_track_particles.add(particles.get(j));
					k = i;
					m = j;
					final Vector<Particle> particles3 = frames[k]
					        .getParticles();
					do {
						found = -1;
						for (n = 0; n < linkrange; n++) {
							if (particles3.get(m).next[n] != -1) {
								// If this particle is linked to a "real"
								// particle that
								// that is NOT already linked, continue with
								// building the trajectory
								final Vector<Particle> particles4 = frames[k
								        + n + 1].getParticles();
								if (particles4.get(particles3.get(m).next[n]).special == false) {
									found = n;
									break;
									// Added by Guy Levy, 18.08.06 - A change
									// form original implementation
									// If this particle is linked to a "real"
									// particle that
									// that is already linked, stop building the
									// trajectory
								} else {
									break;
								}
							}
						}
						if (found == -1) {
							break;
						}
						m = particles3.get(m).next[found];
						k += (found + 1);
						curr_track_particles.add(particles3.get(m));
						particles3.get(m).special = true;
					} while (m != -1);
					tracks.add(curr_track_particles);
					curr_track_particles.removeAll(curr_track_particles);
				}
			}
		}
		return tracks;
	}

	private void debugModeRun() {

	}

	public void terminate() {
		this.isTerminated = true;
	}

	public Map<OmegaParticleDetectionRun, List<OmegaParameter>> getParticleToProcess() {
		return this.particlesToProcess;
	}

	public Map<OmegaParticleDetectionRun, List<OmegaTrajectory>> getResultingTrajectories() {
		return this.resultingTrajectories;
	}

	private void updateStatusSync(final String msg, final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					PLRunner.this.displayerPanel
					.updateMessageStatus(new PLMessageEvent(msg,
							PLRunner.this, ended));
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
				PLRunner.this.displayerPanel
				.updateMessageStatus(new PLMessageEvent(msg,
						PLRunner.this, ended));
			}
		});
	}
}
