package edu.umassmed.omega.omero.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import edu.umassmed.omega.commons.OmegaConstants;

public class OmeroBrowserSingleImagePanel extends JPanel {

	private static final long serialVersionUID = 7924490362486444828L;

	private Long imageID = 0L;
	private BufferedImage image = null;

	public OmeroBrowserSingleImagePanel(final Long imageID, final String imageName,
	        final BufferedImage image) {
		super(null);
		this.imageID = imageID;
		this.image = image;

		this.setSize(new Dimension(image.getWidth(),
		        OmegaConstants.THUMBNAIL_SIZE));
		this.setPreferredSize(new Dimension(image.getWidth(),
		        OmegaConstants.THUMBNAIL_SIZE));

		this.setToolTipText(imageName);
	}

	@Override
	public void paintComponent(final Graphics g) {
		final Graphics2D g2D = (Graphics2D) g;

		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
		        RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		// layout the images
		g2D.setColor(this.getBackground());
		g2D.fillRect(0, 0, this.getWidth(), this.getHeight());

		int x = 0;
		int y = 0;
		int w = 0;
		int h = 0;
		final int width = this.getWidth();
		int maxY = 0;
		final int gap = 2;

		h = this.image.getHeight();
		w = this.image.getWidth();

		if (maxY < h) {
			maxY = h;
		}

		if (x != 0) {
			if ((x + w) > width) {
				x = 0;
				y += maxY;
				y += gap;
				maxY = 0;
			}
		}

		g2D.drawImage(this.image, x, y, null);
		x += w;
		x += gap;
	}
}