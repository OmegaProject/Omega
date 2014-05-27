package edu.umassmed.omega.commons.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class GenericImageCanvasListener extends MouseAdapter {
	private GenericImageCanvas imageCanvas = null;
	private final JPopupMenu menu = new JPopupMenu();

	public GenericImageCanvasListener(final GenericImageCanvas imageCanvas) {
		this.imageCanvas = imageCanvas;
		this.createPopupMenu();
	}

	private void createPopupMenu() {
		JMenuItem item = new JMenuItem("Zoom IN");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericImageCanvasListener.this.imageCanvas
				        .setScale(GenericImageCanvasListener.this.imageCanvas
				                .getScale() * 2.0);

				GenericImageCanvasListener.this.imageCanvas.callRevalidate();

				// GenericImageCanvasListeners.this.imageCanvas.scaleTrajectories();
				GenericImageCanvasListener.this.imageCanvas.repaint();
			}
		});
		this.menu.add(item);

		item = new JMenuItem("Zoom OUT");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericImageCanvasListener.this.imageCanvas
				        .setScale(GenericImageCanvasListener.this.imageCanvas
				                .getScale() / 2.0);

				GenericImageCanvasListener.this.imageCanvas.callRevalidate();

				// GenericImageCanvasListeners.this.imageCanvas.scaleTrajectories();
				GenericImageCanvasListener.this.imageCanvas.repaint();
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
