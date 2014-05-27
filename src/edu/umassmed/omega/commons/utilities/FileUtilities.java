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
