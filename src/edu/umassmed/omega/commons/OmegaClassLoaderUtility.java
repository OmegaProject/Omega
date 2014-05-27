package edu.umassmed.omega.commons;

import java.lang.reflect.Field;
import java.util.Arrays;

public class OmegaClassLoaderUtility {

	public static void addLibraryPath(final String pathToAdd)
	        throws NoSuchFieldException, SecurityException,
	        IllegalArgumentException, IllegalAccessException {
		final Field usrPathsField = ClassLoader.class
		        .getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		// get array of paths
		final String[] paths = (String[]) usrPathsField.get(null);

		// check if the path to add is already present
		for (final String path : paths) {
			if (path.equals(pathToAdd))
				return;
		}

		// add the new path
		final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length - 1] = pathToAdd;
		usrPathsField.set(null, newPaths);
	}
}
