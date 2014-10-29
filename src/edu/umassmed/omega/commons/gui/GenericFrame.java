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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class GenericFrame extends JFrame {

	private static final long serialVersionUID = -3768293722803137824L;

	private final JFrame parent;

	public GenericFrame(final JFrame parent, final JComponent content,
	        final String title, final Point position, final Dimension dimension) {
		this.parent = parent;

		this.setTitle(title);
		// this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		this.getContentPane().setLayout(new BorderLayout());

		this.getContentPane().add(content, BorderLayout.CENTER);

		this.setLocation(position);
		this.setSize(dimension);

		// Display the window.
		// this.sidebarFrame.pack();
		this.setVisible(true);
		// this.pack();
	}

	public void doNothingOnClose() {
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	public void hideOnClose() {
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	public void disposeOnClose() {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	@Override
	public JFrame getParent() {
		return this.parent;
	}
}
