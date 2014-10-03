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

import java.text.DecimalFormat;

public class StringUtilities {
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
