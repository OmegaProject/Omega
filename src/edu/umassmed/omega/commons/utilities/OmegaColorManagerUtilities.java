package edu.umassmed.omega.commons.utilities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
		// Random random = new Random();

		for (int i = 0; i < numberOfColors; i++) {
			// final float r = random.nextFloat();
			// final float g = random.nextFloat();
			// final float b = random.nextFloat();
			// final float hue = random.nextFloat();
			final Color c = Color.getHSBColor((float) i / numberOfColors, 1, 1);
			colors.add(c);
		}
		return colors;
	}

	// class ColorTestingPanel extends JPanel {
	// int numCols = 100;
	//
	// @Override
	// public void paint(final Graphics g) {
	//
	// float h = 0;
	// final float dh = (float) this.getHeight() / this.numCols;
	// final List<Color> colors = OmegaColorManagerUtilities
	// .generateRandomColors(this.numCols);
	//
	// for (int i = 0; i < this.numCols; i++) {
	// g.setColor(colors.get(i));
	// g.fillRect(0, (int) h, this.getWidth(), (int) (h += dh));
	// }
	// }
	// }
	//
	// public static ColorTestingPanel getColorTestingPanel() {
	// return new OmegaColorManagerUtilities().new ColorTestingPanel();
	// }
	//
	// public static void main(final String[] args) {
	// final JFrame f = new JFrame();
	// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// f.add(OmegaColorManagerUtilities.getColorTestingPanel());
	// f.setSize(400, 400);
	// f.setVisible(true);
	// }
}
