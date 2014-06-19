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
package edu.umassmed.omega.commons.gui.checkboxTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.UIManager;

public class IndeterminateIcon implements Icon {
	private final Color FOREGROUND = new Color(50, 20, 255, 200); // TEST:
	                                                              // UIManager.getColor("CheckBox.foreground");
	private final Icon icon = UIManager.getIcon("CheckBox.icon");
	private static final int a = 4;
	private static final int b = 2;

	@Override
	public void paintIcon(final Component c, final Graphics g, final int x,
	        final int y) {
		this.icon.paintIcon(c, g, x, y);
		final int w = this.getIconWidth();
		final int h = this.getIconHeight();
		final Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(this.FOREGROUND);
		g2.translate(x, y);
		g2.fillRect(IndeterminateIcon.a, (h - IndeterminateIcon.b) / 2, w
		        - IndeterminateIcon.a - IndeterminateIcon.a,
		        IndeterminateIcon.b);
		g2.translate(-x, -y);
	}

	@Override
	public int getIconWidth() {
		return this.icon.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return this.icon.getIconHeight();
	}
}