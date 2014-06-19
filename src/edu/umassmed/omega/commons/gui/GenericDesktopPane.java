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
package edu.umassmed.omega.commons.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.beans.PropertyVetoException;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class GenericDesktopPane extends JDesktopPane {

	static final long serialVersionUID = 234252524343L;

	private static int FRAME_OFFSET = 20;
	private final MDIDesktopManager manager;

	public GenericDesktopPane() {
		this.manager = new MDIDesktopManager(this);
		this.setDesktopManager(this.manager);
		this.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	}

	@Override
	public void setBounds(final int x, final int y, final int w, final int h) {
		super.setBounds(x, y, w, h);
		this.checkDesktopSize();
	}

	public Component add(final JInternalFrame frame) {
		final JInternalFrame[] array = this.getAllFrames();
		Point p;

		final Component retval = super.add(frame);
		this.checkDesktopSize();
		if (array.length > 0) {
			p = array[0].getLocation();
			p.x = p.x + GenericDesktopPane.FRAME_OFFSET;
			p.y = p.y + GenericDesktopPane.FRAME_OFFSET;
		} else {
			p = new Point(0, 0);
		}
		frame.setLocation(p.x, p.y);
		this.moveToFront(frame);
		frame.setVisible(true);
		try {
			frame.setSelected(true);
		} catch (final PropertyVetoException e) {
			frame.toBack();
		}
		return retval;
	}

	@Override
	public void remove(final Component c) {
		super.remove(c);
		this.checkDesktopSize();
	}

	/**
	 * Cascade all internal frames
	 */
	public void cascadeFrames() {
		int x = 0;
		int y = 0;
		final JInternalFrame allFrames[] = this.getAllFrames();

		this.manager.setNormalSize();
		// int frameHeight = (getBounds().height - 5) - allFrames.length *
		// FRAME_OFFSET;
		// int frameWidth = (getBounds().width - 5) - allFrames.length *
		// FRAME_OFFSET;
		for (int i = allFrames.length - 1; i >= 0; i--) {
			// allFrames[i].setSize(frameWidth,frameHeight);
			allFrames[i].setLocation(x, y);
			x = x + GenericDesktopPane.FRAME_OFFSET;
			y = y + GenericDesktopPane.FRAME_OFFSET;
		}
	}

	/**
	 * Tile all internal frames
	 */
	public void tileFrames() {
		final java.awt.Component allFrames[] = this.getAllFrames();
		this.manager.setNormalSize();
		final int frameHeight = this.getBounds().height / allFrames.length;
		int y = 0;
		for (final Component allFrame : allFrames) {
			allFrame.setSize(this.getBounds().width, frameHeight);
			allFrame.setLocation(0, y);
			y = y + frameHeight;
		}
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the
	 * given dimension.
	 */
	public void setAllSize(final Dimension d) {
		this.setMinimumSize(d);
		this.setMaximumSize(d);
		this.setPreferredSize(d);
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the
	 * given width and height.
	 */
	public void setAllSize(final int width, final int height) {
		this.setAllSize(new Dimension(width, height));
	}

	private void checkDesktopSize() {
		if ((this.getParent() != null) && this.isVisible()) {
			this.manager.resizeDesktop();
		}
	}
}

/**
 * Private class used to replace the standard DesktopManager for JDesktopPane.
 * Used to provide scrollbar functionality.
 */
class MDIDesktopManager extends DefaultDesktopManager {

	static final long serialVersionUID = 23425252234L;

	private final GenericDesktopPane desktop;

	public MDIDesktopManager(final GenericDesktopPane desktop) {
		this.desktop = desktop;
	}

	@Override
	public void endResizingFrame(final JComponent f) {
		super.endResizingFrame(f);
		this.resizeDesktop();
	}

	@Override
	public void endDraggingFrame(final JComponent f) {
		super.endDraggingFrame(f);
		this.resizeDesktop();
	}

	public void setNormalSize() {
		final JScrollPane scrollPane = this.getScrollPane();
		final int x = 0;
		final int y = 0;
		final Insets scrollInsets = this.getScrollPaneInsets();

		if (scrollPane != null) {
			final Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(
				        d.getWidth() - scrollInsets.left - scrollInsets.right,
				        d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			d.setSize(d.getWidth() - 20, d.getHeight() - 20);
			this.desktop.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}

	private Insets getScrollPaneInsets() {
		final JScrollPane scrollPane = this.getScrollPane();
		if (scrollPane == null)
			return new Insets(0, 0, 0, 0);
		else
			return this.getScrollPane().getBorder().getBorderInsets(scrollPane);
	}

	private JScrollPane getScrollPane() {
		if (this.desktop.getParent() instanceof JViewport) {
			final JViewport viewPort = (JViewport) this.desktop.getParent();
			if (viewPort.getParent() instanceof JScrollPane)
				return (JScrollPane) viewPort.getParent();
		}
		return null;
	}

	protected void resizeDesktop() {
		int x = 0;
		int y = 0;
		final JScrollPane scrollPane = this.getScrollPane();
		final Insets scrollInsets = this.getScrollPaneInsets();

		if (scrollPane != null) {
			final JInternalFrame allFrames[] = this.desktop.getAllFrames();
			for (final JInternalFrame allFrame : allFrames) {
				if ((allFrame.getX() + allFrame.getWidth()) > x) {
					x = allFrame.getX() + allFrame.getWidth();
				}
				if ((allFrame.getY() + allFrame.getHeight()) > y) {
					y = allFrame.getY() + allFrame.getHeight();
				}
			}
			final Dimension d = scrollPane.getVisibleRect().getSize();
			if (scrollPane.getBorder() != null) {
				d.setSize(
				        d.getWidth() - scrollInsets.left - scrollInsets.right,
				        d.getHeight() - scrollInsets.top - scrollInsets.bottom);
			}

			if (x <= d.getWidth()) {
				x = ((int) d.getWidth()) - 20;
			}
			if (y <= d.getHeight()) {
				y = ((int) d.getHeight()) - 20;
			}
			this.desktop.setAllSize(x, y);
			scrollPane.invalidate();
			scrollPane.validate();
		}
	}
}
