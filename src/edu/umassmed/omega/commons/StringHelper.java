package edu.umassmed.omega.commons;

import java.text.DecimalFormat;

public class StringHelper {
	public static String getImageName(final String imagePath) {
		try {
			if (imagePath.contains("/")) {
				final String[] splitted = imagePath.split("/");
				return splitted[splitted.length - 1];
			}
		} catch (final Exception e) {
			// nothing to do here...
		}
		return imagePath;
	}

	public static String removeFileExtension(final String fileName) {
		try {
			return fileName.substring(0, fileName.lastIndexOf('.'));
		} catch (final Exception e) {
			return fileName;
		}
	}

	/**
	 * Checks the widthSize and the heightSize of an image and sets the correct
	 * texts in the JTextFields.
	 * 
	 * @param widthSize
	 * @param heightSize
	 */
	public static String getPixelSizeString(final double size,
	        final int maxLenght) {
		String sizeString = (size == 0.0) ? "-" : String.valueOf(size);
		if (sizeString.length() > maxLenght) {
			sizeString = sizeString.substring(0, maxLenght);
		}
		return sizeString;
	}

	public static String doubleToString(final double d, final int decimalPlaces) {
		String dec = "";
		for (int i = 0; i < decimalPlaces; i++) {
			dec = dec + "#";
		}
		final DecimalFormat df = new DecimalFormat("#." + dec);
		return df.format(d);
	}

	/**
	 * Returns a splitted String[] from a String.
	 * 
	 * @param strLine
	 * @param separator
	 * @return
	 */
	public static String[] splitString(String strLine, final String separator) {
		try {
			strLine = strLine.trim();
			return strLine.split(separator);
		} catch (final Exception e) {
			return null;
		}
	}
}
