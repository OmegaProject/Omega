package edu.umassmed.omega.data.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;
import edu.umassmed.omega.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaTrajectoriesImporterUtilities {
	public static final String PARTICLE_FRAMEINDEX = "f";
	public static final String PARTICLE_XCOORD = "x";
	public static final String PARTICLE_YCOORD = "y";
	public static final String PARTICLE_INTENSITY = "i";
	public static final String PARTICLE_PROBABILITY = "p";

	// TODO change IllegalArgumentException with a custom exception
	public static List<OmegaTrajectory> importTrajectories(
	        final String fileNameIdentifier, final String trajectoryIdentifier,
	        final String particleIdentifier,
	        final String nonParticleIdentifier, final String particleSeparator,
	        final List<String> particleDataOrder, final File sourceFolder)
	        throws IOException, IllegalArgumentException {
		if (!sourceFolder.isDirectory())
			throw new IllegalArgumentException("The source folder: "
			        + sourceFolder + " has to be a valid directory");
		if (sourceFolder.listFiles().length == 0)
			throw new IllegalArgumentException("The source folder: "
			        + sourceFolder + " has to be not empty");
		boolean isValid = false;

		final List<OmegaTrajectory> trajectories = new ArrayList<OmegaTrajectory>();
		for (final File f : sourceFolder.listFiles()) {
			if (!f.getName().matches(fileNameIdentifier)) {
				continue;
			}
			isValid = true;
			final FileReader fr = new FileReader(f);
			final BufferedReader br = new BufferedReader(fr);
			trajectories.addAll(OmegaTrajectoriesImporterUtilities
			        .importTrajectories(f.getName(), trajectoryIdentifier,
			                particleIdentifier, nonParticleIdentifier,
			                particleSeparator, particleDataOrder, br));
			br.close();
			fr.close();
		}
		if (!isValid)
			throw new IllegalArgumentException(
			        "The source folder: "
			                + sourceFolder
			                + " has to contain at least 1 file containing the given file name identifier");
		return trajectories;
	}

	private static List<OmegaTrajectory> importTrajectories(
	        final String fileName, final String trajectoryIdentifier,
	        final String particleIdentifier,
	        final String nonParticleIdentifier, final String particleSeparator,
	        final List<String> particleDataOrder, final BufferedReader br)
	        throws IOException, IllegalArgumentException {
		final List<OmegaTrajectory> trajectories = new ArrayList<OmegaTrajectory>();
		final String name1 = fileName.substring(0, fileName.lastIndexOf("."));
		OmegaTrajectory trajectory = null;
		if (trajectoryIdentifier == null) {
			trajectory = new OmegaTrajectory(-1);
			trajectory.setName(name1);
			trajectories.add(trajectory);
		}
		String line = br.readLine();
		while (line != null) {
			if (line.isEmpty()) {
				line = br.readLine();
				continue;
			}
			if ((trajectoryIdentifier != null)
			        && line.startsWith(trajectoryIdentifier)) {
				final String name = OmegaStringUtilities.removeSymbols(line);
				String name2 = OmegaStringUtilities.replaceWhitespaces(name,
				        "_");
				if (name2.startsWith("_")) {
					name2 = name2.replaceFirst("_", "");
				}
				trajectory = new OmegaTrajectory(-1);
				trajectory.setName(name1 + "_" + name2);
				trajectories.add(trajectory);
			}

			if ((nonParticleIdentifier != null)
			        && line.startsWith(nonParticleIdentifier)) {
				line = br.readLine();
				continue;
			}

			if ((particleIdentifier != null)
			        && line.startsWith(particleIdentifier)) {
				line = line.replaceFirst(particleIdentifier, "");
				final OmegaParticle p = OmegaTrajectoriesImporterUtilities
				        .importParticle(particleSeparator, particleDataOrder,
				                line);
				trajectory.addROI(p);
			} else {
				final OmegaParticle p = OmegaTrajectoriesImporterUtilities
				        .importParticle(particleSeparator, particleDataOrder,
				                line);
				trajectory.addROI(p);
			}
			line = br.readLine();
		}

		for (final OmegaTrajectory t : trajectories) {
			t.recalculateLength();
		}

		return trajectories;
	}

	private static OmegaParticle importParticle(final String particleSeparator,
	        final List<String> particleDataOrder, final String particleToImport)
	        throws IllegalArgumentException {
		final String[] particleData = particleToImport.split(particleSeparator);
		Integer frameIndex = null;
		Double x = null, y = null, intensity = null, probability = null;
		for (int i = 0; i < particleDataOrder.size(); i++) {
			final String order = particleDataOrder.get(i);
			final String data = particleData[i];
			if (order
			        .equals(OmegaTrajectoriesImporterUtilities.PARTICLE_FRAMEINDEX)) {
				frameIndex = Integer.valueOf(data);
			} else if (order
			        .equals(OmegaTrajectoriesImporterUtilities.PARTICLE_XCOORD)) {
				x = Double.valueOf(data);
			} else if (order
			        .equals(OmegaTrajectoriesImporterUtilities.PARTICLE_YCOORD)) {

				y = Double.valueOf(data);
			} else if (order
			        .equals(OmegaTrajectoriesImporterUtilities.PARTICLE_INTENSITY)) {
				intensity = Double.valueOf(data);
			} else if (order
			        .equals(OmegaTrajectoriesImporterUtilities.PARTICLE_PROBABILITY)) {
				probability = Double.valueOf(data);
			}
		}
		if ((frameIndex == null) || (x == null) || (y == null))
			throw new IllegalArgumentException(
			        "The line: "
			                + particleToImport
			                + " doesn't contain enough information for identifying a particle");
		OmegaParticle p = null;
		if (intensity == null) {
			p = new OmegaParticle(frameIndex, x, y);
		} else if (probability == null) {
			p = new OmegaParticle(frameIndex, x, y, intensity);
		} else {
			p = new OmegaParticle(frameIndex, x, y, intensity, probability);
		}
		return p;
	}
}
