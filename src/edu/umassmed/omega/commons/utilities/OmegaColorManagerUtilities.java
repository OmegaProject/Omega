package edu.umassmed.omega.commons.utilities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

public class OmegaColorManagerUtilities {

	public static Color openPaletteColor(final JComponent parent,
	        final String title, final Color originalColor) {

		final Color c = JColorChooser.showDialog(parent, title, originalColor);

		return c;
	}

	public static List<Color> generateRandomColors(final int numberOfColors) {
		final List<Color> colors = new ArrayList<Color>();
		final Random random = new Random();

		for (int i = 0; i < numberOfColors; i++) {
			final float fr = (random.nextFloat() / 2.0f) + 0.5f;
			final float fg = (random.nextFloat() / 2.0f) + 0.5f;
			final float fb = (random.nextFloat() / 2.0f) + 0.5f;
			final Color c = new Color(fr, fg, fb);
			colors.add(c);
		}
		return colors;
	}
}
