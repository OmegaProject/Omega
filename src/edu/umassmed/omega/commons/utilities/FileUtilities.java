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
package edu.umassmed.omega.commons.utilities;

import java.io.File;

public class FileUtilities {
	public static boolean directoryExists(final String dirName) {
		final File dir = new File(dirName);
		return dir.exists();
	}

	public static void createDirectory(final String dirName) {
		final File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdir();
		} else {
			// TODO throw error and print message
		}
		try {

		} catch (final Exception e) {
			// GLogManager.log(
			// String.format("%s: %s", "Cannot create the directory",
			// e.toString()), Level.WARNING);
		}
	}

	public static void emptyDirectory(final String directoryName) {
		try {
			final File dir = new File(directoryName);
			if (!dir.exists())
				// TODO throw error and print message
				return;

			final String[] info = dir.list();
			for (final String element : info) {
				final File n = new File(directoryName + File.separator
				        + element);
				if (n.isFile()) {
					n.delete();
				} else if (n.isDirectory()) {
					FileUtilities.deleteDirectory(n);
				}
			}
		} catch (final Exception e) {
			// GLogManager.log(
			// String.format("%s: %s", "Cannot empty the directory",
			// e.toString()), Level.WARNING);
			// TODO throw error
		}
	}

	private static boolean deleteDirectory(final File dir) {
		final String[] children = dir.list();
		for (final String element : children) {
			final boolean success = FileUtilities.deleteDirectory(new File(dir,
			        element));
			if (!success)
				return false;
		}
		return dir.delete();
	}
}
