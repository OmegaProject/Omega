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

import java.awt.Color;
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

		final Dimension singleImagePanelDim = new Dimension(
		        OmegaConstants.THUMBNAIL_SIZE, OmegaConstants.THUMBNAIL_SIZE);
		this.setSize(singleImagePanelDim);
		this.setPreferredSize(singleImagePanelDim);

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

		final int width = this.getWidth();
		final int height = this.getHeight();
		// layout the images
		// g2D.setColor(this.getBackground());
		g2D.setColor(Color.white);
		g2D.fillRect(0, 0, width, height);

		int x = 0, y = 0;
		final int imgWidth = this.image.getWidth();
		final int imgHeight = this.image.getHeight();

		if (width > imgWidth) {
			final int diffW = width - imgWidth;
			x = diffW / 2;
		}
		if (height > imgHeight) {
			final int diffH = height - imgHeight;
			y = diffH / 2;
		}

		g2D.drawImage(this.image, x, y, null);
	}
}