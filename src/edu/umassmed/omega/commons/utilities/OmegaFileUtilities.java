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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionFileManager;

public class OmegaFileUtilities {

	private static void copyFile(final File f1, final File f2,
	        final boolean append) throws IOException {
		final FileWriter fw = new FileWriter(f2, append);
		final BufferedWriter bw = new BufferedWriter(fw);
		final FileReader fr = new FileReader(f1);
		final BufferedReader br = new BufferedReader(fr);

		String line = br.readLine();
		while (line != null) {
			bw.write(line);
			bw.write("\n");
			line = br.readLine();
		}

		bw.close();
		br.close();
		br.close();
		fr.close();
	}

	public static void copyFile(final File f1, final File f2)
	        throws IOException {
		OmegaFileUtilities.copyFile(f1, f2, false);
	}

	public static void appendFile(final File f1, final File f2)
	        throws IOException {
		OmegaFileUtilities.copyFile(f1, f2, true);
	}

	public static boolean directoryExists(final String dirName) {
		final File dir = new File(dirName);
		return dir.exists();
	}

	public static void createDirectory(final String dirName)
	        throws OmegaCoreExceptionFileManager {
		final File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdir();
		} else
			throw new OmegaCoreExceptionFileManager(
			        "createDirectory: directory " + dirName + " already exists");
	}

	public static void emptyDirectory(final String dirName)
	        throws OmegaCoreExceptionFileManager {
		final File dir = new File(dirName);
		if (!dir.exists())
			throw new OmegaCoreExceptionFileManager(
			        "emptyDirectory: directory " + dirName + " doesn't exist");
		final String[] info = dir.list();
		for (final String element : info) {
			final File n = new File(dirName + File.separator + element);
			if (n.isFile()) {
				n.delete();
			} else if (n.isDirectory()) {
				OmegaFileUtilities.deleteDirectory(n.getAbsolutePath());
			}
		}
	}

	private static void deleteDirectory(final String dirName)
	        throws OmegaCoreExceptionFileManager {
		final File dir = new File(dirName);
		final String[] children = dir.list();
		for (final String element : children) {
			OmegaFileUtilities.deleteDirectory(dir + File.separator + element);
		}
		final boolean deleted = dir.delete();
		if (!deleted)
			throw new OmegaCoreExceptionFileManager(
			        "deleteDirectory: it was not possible to delete " + dirName);
	}
}
