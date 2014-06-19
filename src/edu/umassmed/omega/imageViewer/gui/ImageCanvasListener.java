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
package edu.umassmed.omega.imageViewer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ImageCanvasListener extends MouseAdapter {
	private ImageViewerCanvasPanel imageCanvas = null;
	private final JPopupMenu menu = new JPopupMenu();

	public ImageCanvasListener(final ImageViewerCanvasPanel imageCanvas) {
		this.imageCanvas = imageCanvas;
		this.createPopupMenu();
	}

	private void createPopupMenu() {
		JMenuItem item = new JMenuItem("Zoom IN");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ImageCanvasListener.this.imageCanvas
				        .setScale(ImageCanvasListener.this.imageCanvas
				                .getScale() * 2.0);

				ImageCanvasListener.this.imageCanvas.callRevalidate();

				// GenericImageCanvasListeners.this.imageCanvas.scaleTrajectories();
				ImageCanvasListener.this.imageCanvas.repaint();
			}
		});
		this.menu.add(item);

		item = new JMenuItem("Zoom OUT");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ImageCanvasListener.this.imageCanvas
				        .setScale(ImageCanvasListener.this.imageCanvas
				                .getScale() / 2.0);

				ImageCanvasListener.this.imageCanvas.callRevalidate();

				// GenericImageCanvasListeners.this.imageCanvas.scaleTrajectories();
				ImageCanvasListener.this.imageCanvas.repaint();
			}
		});
		this.menu.add(item);

		item = new JMenuItem("Save image...");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				// GenericImageCanvasListeners.this.imageCanvas.saveImage();
			}
		});
		this.menu.add(item);

		// item = new JMenuItem("Save movie...");
		// item.addActionListener(new ActionListener()
		// {
		// public void actionPerformed(ActionEvent e)
		// {
		// imageCanvas.saveMovie();
		// }
		// });
		// menu.add(item);
	}

	/**
	 * Manages the mouse clicks.
	 */
	@Override
	public void mousePressed(final MouseEvent ev) {
		// // left click: back to the browser
		// if ((ev.getButton() == 1) & (ev.getClickCount() == 2)) {
		// this.imageCanvas.getjPanelViewer().getReviewFrame()
		// .displayBrowser();
		// this.imageCanvas.setTrajectories(null);
		// this.imageCanvas.setTrajectoriesScaled(null);
		// this.imageCanvas.setScale(1.0);
		// }
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		// // right click
		// if (e.isPopupTrigger()) {
		// this.menu.show(e.getComponent(), e.getX(), e.getY());
		// }
	}
}
