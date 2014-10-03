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
package edu.umassmed.omega.omeroPlugin.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import edu.umassmed.omega.commons.constants.OmegaConstants;

public class OmeroBrowserSingleImagePanel extends JPanel {

	private static final long serialVersionUID = 7924490362486444828L;

	private final Long imageID;
	private BufferedImage image = null;

	public OmeroBrowserSingleImagePanel(final Long imageID,
	        final String imageName, final BufferedImage image) {
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