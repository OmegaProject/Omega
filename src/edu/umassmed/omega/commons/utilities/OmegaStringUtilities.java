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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import edu.umassmed.omega.core.OmegaLogFileManager;

public class OmegaStringUtilities {

	public static int countLines(final JTextComponent comp,
	        final int totalCharacters) {
		int lineCount = (totalCharacters == 0) ? 1 : 0;
		try {
			int offset = totalCharacters;
			while (offset > 0) {
				offset = Utilities.getRowStart(comp, offset) - 1;
				lineCount++;
			}
		} catch (final BadLocationException ex) {
			OmegaLogFileManager.handleCoreException(ex);
		}
		return lineCount;
	}

	public static int countLines(final String s, final String divisor) {
		final String[] splits = s.split(divisor);
		return splits.length;
	}

	public static Dimension getStringSize(final Graphics g, final Font font,
	        final String text) {
		// get metrics from the graphics
		final FontMetrics metrics = g.getFontMetrics(font);
		// get the height of a line of text in this
		// font and render context
		final int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		final int adv = metrics.stringWidth(text);
		// calculate the size of a box to hold the
		// text with some padding.
		return new Dimension(adv + 2, hgt + 2);
	}

	public static String replaceSymbols(final String s, final String replacement) {
		return s.replaceAll("\\p{Punct}", replacement);
	}

	public static String removeSymbols(final String s) {
		return s.replaceAll("\\p{Punct}", "");
	}

	public static String replaceWhitespaces(final String s,
	        final String replacement) {
		return s.replace(" ", replacement);
	}

	public static String removeWhitespaces(final String s) {
		return s.replace(" ", "");
	}

	public static String getImageName(final String imagePath) {
		try {
			if (imagePath.contains("/")) {
				final String[] splitted = imagePath.split("/");
				return splitted[splitted.length - 1];
			}
		} catch (final Exception ex) {
			// TODO launch proper exception here
		}
		return imagePath;
	}

	public static String removeFileExtension(final String fileName) {
		try {
			return fileName.substring(0, fileName.lastIndexOf('.'));
		} catch (final Exception e) {
			// TODO launch proper exception here
		}
		return fileName;
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
			// TODO launch proper exception here
		}
		return null;
	}

	public static String getHtmlString(final String text, final String separator) {
		final StringBuffer buf = new StringBuffer();
		buf.append("<html><div style=\"text-align:center\">");
		final String[] tokens = text.split(separator);
		for (int i = 0; i < tokens.length; i++) {
			final StringBuffer currentBuf = new StringBuffer();
			buf.append(tokens[i]);
			if ((i != (tokens.length - 1))) {
				buf.append(" ");
			}
			buf.append(currentBuf.toString());
		}
		buf.append("</div></html>");
		return buf.toString();
	}

	public static String getHtmlMultiLineString(final String text,
	        final String separator, final JComponent comp) {
		System.out.println();
		final StringBuffer buf = new StringBuffer();
		buf.append("<html><div style=\"text-align:center\">");
		final Dimension dim = comp.getSize();
		final int usableWidth = dim.width;
		int restWidth = usableWidth;
		final String[] tokens = text.split(separator);
		int prev = -1, now = -1;
		for (int i = 0; i < tokens.length; i++) {
			final Dimension stringSize = OmegaStringUtilities.getStringSize(
			        comp.getGraphics(), comp.getFont(), tokens[i]);
			final int neededWidth = stringSize.width;
			final StringBuffer currentBuf = new StringBuffer();
			if (restWidth > neededWidth) {
				currentBuf.append(tokens[i]);
				restWidth -= neededWidth;
				now = 0;
			} else {
				restWidth = usableWidth;
				currentBuf
				        .append("</div><br><div style=\"text-align:center\">");
				currentBuf.append(tokens[i]);
				restWidth -= neededWidth;
				now = 1;
			}
			if ((i != (tokens.length)) && (now == prev)) {
				buf.append(" ");
			}
			buf.append(currentBuf.toString());
			prev = now;
		}
		buf.append("</div></html>");
		return buf.toString();
	}
}
