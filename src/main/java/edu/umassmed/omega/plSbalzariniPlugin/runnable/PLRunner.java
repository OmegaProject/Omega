/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School Alessandro
 * Rigano (Program in Molecular Medicine) Caterina Strambio De Castillia
 * (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo Sbalzarini and Mario Valle.
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
package edu.umassmed.omega.plSbalzariniPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.SwingUtilities;

import mosaic.core.detection.Particle;
import mosaic.core.particleLinking.LinkerOptions;
import mosaic.core.particleLinking.ParticleLinker;
import mosaic.core.particleLinking.ParticleLinkerGreedy;
import mosaic.core.particleLinking.ParticleLinkerHungarian;
import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaTrajectory;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.plSbalzariniPlugin.PLConstants;

public class PLRunner implements PLRunnable {
	private static final String RUNNER = "Runner service: ";
	private final OmegaMessageDisplayerPanelInterface displayerPanel;
	
	private final Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaParameter>>> particlesToProcess;
	private final Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaTrajectory>>> resultingTrajectories;
	
	private final boolean isDebugMode;
	private boolean isJobCompleted, isTerminated;
	
	public PLRunner(
			final OmegaMessageDisplayerPanelInterface displayerPanel,
			final Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaParameter>>> particlesToProcess) {
		this.displayerPanel = displayerPanel;
		
		this.particlesToProcess = new LinkedHashMap<>(particlesToProcess);
		this.isDebugMode = false;
		
		this.isJobCompleted = false;
		
		this.resultingTrajectories = new LinkedHashMap<>();
	}
	
	public PLRunner(
			final Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaParameter>>> particlesToProcess) {
		this.displayerPanel = null;
		
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
			this.isJobCompleted = true;
		} else {
			try {
				this.normalModeRun();
				this.isJobCompleted = true;
			} catch (final Exception e) {
				e.printStackTrace();
				this.isJobCompleted = false;
			}
		}
		
		this.updateStatusAsync(PLRunner.RUNNER + " ended.", true);
	}
	
	private void normalModeRun() throws Exception {
		for (final Integer index : this.particlesToProcess.keySet()) {
			for (final OmegaParticleDetectionRun spotDetRun : this.particlesToProcess
					.get(index).keySet()) {
				final List<OmegaParameter> parameters = this.particlesToProcess
						.get(index).get(spotDetRun);

				final Map<OmegaPlane, List<OmegaROI>> resultingParticles = spotDetRun
						.getResultingParticles();
				final Map<OmegaROI, Map<String, Object>> resultingParticlesValues = spotDetRun
						.getResultingParticlesValues();

				OmegaImagePixels pixels = null;
				for (final OmegaPlane frame : resultingParticles.keySet()) {
					if (frame.getParentPixels() == null) {
						continue;
					}
					pixels = frame.getParentPixels();
					break;
				}

				Integer sizeT = null;
				if (pixels == null) {
					int max = -1;
					for (final OmegaPlane frame : resultingParticles.keySet()) {
						if (frame.getIndex() > max) {
							max = frame.getIndex();
						}
					}
					sizeT = max;
				} else {
					sizeT = pixels.getSizeT();
				}

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
				Integer minLength = null;
				for (int i = 0; i < parameters.size(); i++) {
					final OmegaParameter param = parameters.get(i);
					if (param.getName() == OmegaConstantsAlgorithmParameters.PARAM_DISPLACEMENT) {
						displacement = Float.valueOf(param.getStringValue());
					} else if (param.getName() == OmegaConstantsAlgorithmParameters.PARAM_LINKRANGE) {
						linkrange = Integer.valueOf(param.getStringValue());
					} else if (param.getName() == OmegaConstantsAlgorithmParameters.PARAM_MOVTYPE) {
						movType = param.getStringValue();
					} else if (param.getName() == OmegaConstantsAlgorithmParameters.PARAM_OBJFEATURE) {
						objectFeature = Float.valueOf(param.getStringValue());
					} else if (param.getName() == OmegaConstantsAlgorithmParameters.PARAM_DYNAMICS) {
						dynamics = Float.valueOf(param.getStringValue());
					} else if (param.getName() == OmegaConstantsAlgorithmParameters.PARAM_OPTIMIZER) {
						optimizer = param.getStringValue();
					} else if (param.getName() == OmegaConstantsAlgorithmParameters.PARAM_MINPOINTS) {
						minLength = Integer.valueOf(param.getStringValue());
					}
				}

				this.updateStatusSync(PLRunner.RUNNER
						+ " rebuilding MOSAIC structures.", false);

				final Map<Particle, OmegaROI> particlesMap = new LinkedHashMap<Particle, OmegaROI>();
				// final MyFrame[] mosaicFrames = new MyFrame[sizeT];
				final List<Vector<Particle>> mosaicParticlesList = new ArrayList<Vector<Particle>>();
				final Map<Integer, Vector<Particle>> mosaicParticlesMap = new LinkedHashMap<Integer, Vector<Particle>>();
				for (final OmegaPlane frame : resultingParticles.keySet()) {
					final List<OmegaROI> particles = resultingParticles
							.get(frame);
					final int i = frame.getIndex();
					// final MyFrame mosaicFrame = new MyFrame(index);
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
						final Particle p = new Particle(x, y, 0,
								frame.getIndex());
						p.m0 = (Float) resultingParticlesValues.get(particle)
								.get("m0");
						// p.m1 = (Float)
						// resultingParticlesValues.get(particle).get(
						// "m1");
						p.m2 = (Float) resultingParticlesValues.get(particle)
								.get("m2");
						// p.m3 = (Float)
						// resultingParticlesValues.get(particle).get(
						// "m3");
						// p.m4 = (Float)
						// resultingParticlesValues.get(particle).get(
						// "m4");
						particlesMap.put(p, particle);
						mosaicParticles.add(p);
					}
					// mosaicFrame.setParticles(mosaicParticles);
					// mosaicFrames[index] = mosaicFrame;
					mosaicParticlesMap.put(i, mosaicParticles);
				}

				for (int i = 0; i < sizeT; i++) {
					final Vector<Particle> mosaicParticles = mosaicParticlesMap
							.get(i);
					mosaicParticlesList.add(mosaicParticles);
				}

				ParticleLinker linker = null;
				if (optimizer == PLConstants.PARAM_OPTIMIZER_GREEDY) {
					linker = new ParticleLinkerGreedy();
					// linker = new ParticleLinkerBestOnePerm();
				} else {
					linker = new ParticleLinkerHungarian();
					// linker = new ParticleLinkerHun();
				}
				final LinkerOptions options = new LinkerOptions();
				options.maxDisplacement = displacement;
				options.linkRange = linkrange;
				if (movType == PLConstants.PARAM_MOVTYPE_BROWNIAN) {
					options.force = false;
					options.straightLine = false;
				} else if (movType == PLConstants.PARAM_MOVTYPE_COSVEL) {
					options.force = true;
					options.straightLine = true;
				} else if (movType == PLConstants.PARAM_MOVTYPE_STRAIGHT) {
					options.force = false;
					options.straightLine = true;
				}
				options.lSpace = 1f;
				options.lFeature = objectFeature;
				options.lDynamic = dynamics;

				this.updateStatusSync(PLRunner.RUNNER
						+ " launching MOSAIC linker.", false);

				linker.linkParticles(mosaicParticlesList, options);

				this.updateStatusSync(PLRunner.RUNNER
						+ " generating trajectories.", false);

				final List<List<Particle>> mosaicTracks = this
						.generateTrajectories(mosaicParticlesList, sizeT,
								linkrange);
				final List<OmegaTrajectory> tracks = new ArrayList<OmegaTrajectory>();
				int counter = 0;
				for (final List<Particle> mosaicTrack : mosaicTracks) {
					final String trajName = OmegaTrajectory.DEFAULT_TRAJ_NAME
							+ "_" + counter;
					final OmegaTrajectory track = new OmegaTrajectory(
							mosaicTrack.size(), trajName, (double) counter);
					// track.setName(track.getName() + "_" + counter);
					counter++;
					for (final Particle p : mosaicTrack) {
						final OmegaROI particle = particlesMap.get(p);
						track.addROI(particle);
					}
					if (track.getLength() > minLength) {
						tracks.add(track);
					}
				}
				final Map<OmegaParticleDetectionRun, List<OmegaTrajectory>> detAndTracks = new LinkedHashMap<OmegaParticleDetectionRun, List<OmegaTrajectory>>();
				detAndTracks.put(spotDetRun, tracks);
				this.resultingTrajectories.put(index, detAndTracks);
			}
		}
	}
	
	private List<List<Particle>> generateTrajectories(
			final List<Vector<Particle>> mosaicParticlesList,
			final int frames_number, final int linkrange) {
		int i, j, k;
		int found, n, m;
		// temporary vector to hold particles for current trajctory
		final List<List<Particle>> tracks = new ArrayList<List<Particle>>();
		final List<Particle> curr_track_particles = new ArrayList<Particle>();
		for (i = 0; i < frames_number; i++) {
			for (j = 0; j < mosaicParticlesList.get(i).size(); j++) {
				if (!mosaicParticlesList.get(i).elementAt(j).special) {
					mosaicParticlesList.get(i).elementAt(j).special = true;
					found = -1;
					// go over all particles that this particle (particles[j])
					// is linked to
					for (n = 0; n < linkrange; n++) {
						// if it is NOT a dummy particle - stop looking
						if (mosaicParticlesList.get(i).elementAt(j).next[n] != -1) {
							found = n;
							break;
						}
					}
					// if this particle is not linked to any other go to next
					// particle and dont add a trajectory
					if (found == -1) {
						continue;
					}
					
					// Added by Guy Levy, 18.08.06 - A change form original
					// implementation if this particle is linkd to a "real"
					// paritcle that was already linked break the trajectory and
					// start again from the next particle. dont add a trajectory
					if (mosaicParticlesList.get(i + n + 1).elementAt(
							mosaicParticlesList.get(i).elementAt(j).next[n]).special) {
						continue;
					}
					
					curr_track_particles.add(mosaicParticlesList.get(i)
							.elementAt(j));
					k = i;
					m = j;
					do {
						found = -1;
						for (n = 0; n < linkrange; n++) {
							if (mosaicParticlesList.get(k).elementAt(m).next[n] != -1) {
								// If this particle is linked to a "real"
								// particle that that is NOT already linked,
								// continue with building the trajectory
								if (mosaicParticlesList.get(k + n + 1)
										.elementAt(
												mosaicParticlesList.get(k)
														.elementAt(m).next[n]).special == false) {
									found = n;
									break;
									// Added by Guy Levy, 18.08.06 - A change
									// form original implementation If this
									// particle is linked to a "real" particle
									// that that is already linked, stop
									// building the trajectory
								} else {
									break;
								}
							}
						}
						if (found == -1) {
							break;
						}
						m = mosaicParticlesList.get(k).elementAt(m).next[found];
						k += (found + 1);
						curr_track_particles.add(mosaicParticlesList.get(k)
								.elementAt(m));
						if ((k == 90) && (m == 6)) {
						}
						mosaicParticlesList.get(k).elementAt(m).special = true;
					} while (m != -1);
					
					tracks.add(new ArrayList<Particle>(curr_track_particles));
					curr_track_particles.clear();
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
	
	public Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaParameter>>> getParticleToProcess() {
		return this.particlesToProcess;
	}
	
	public Map<Integer, Map<OmegaParticleDetectionRun, List<OmegaTrajectory>>> getResultingTrajectories() {
		return this.resultingTrajectories;
	}
	
	private void updateStatusSync(final String msg, final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					if (PLRunner.this.displayerPanel == null)
						return;
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
				if (PLRunner.this.displayerPanel == null)
					return;
				PLRunner.this.displayerPanel
						.updateMessageStatus(new PLMessageEvent(msg,
								PLRunner.this, ended));
			}
		});
	}
}
