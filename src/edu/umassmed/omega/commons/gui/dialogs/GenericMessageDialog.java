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
package edu.umassmed.omega.commons.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;

public class GenericMessageDialog extends GenericDialog {

	private static final long serialVersionUID = 7292941041310351113L;

	private JLabel lbl;
	private JButton close_btt;

	public GenericMessageDialog(final RootPaneContainer parentContainer,
	        final String title, final String label, final boolean modal) {
		super(parentContainer, title, modal);

		int lines = OmegaStringUtilities.countLines(label, "<br>");
		if (lines < 4) {
			lines = 4;
		}
		final Dimension textDim = OmegaStringUtilities.getStringSize(
		        this.lbl.getGraphics(), this.lbl.getFont(), label);
		int width = 400;
		final int height = (lines * textDim.height) + 50;
		if (width < textDim.width) {
			width = textDim.width;
		}

		final Dimension dim = new Dimension(width, height);
		this.setSize(dim);
		this.setPreferredSize(dim);

		this.lbl.setText(label);

		this.revalidate();
		this.repaint();
	}

	@Override
	protected void createAndAddWidgets() {
		this.lbl = new JLabel("");
		this.lbl.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(this.lbl, BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		this.close_btt = new JButton("Ok");
		this.close_btt.setEnabled(false);
		this.close_btt.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		this.close_btt.setSize(OmegaConstants.BUTTON_SIZE);
		bottomPanel.add(this.close_btt);
		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void addListeners() {
		this.close_btt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GenericMessageDialog.this.setVisible(false);
			}
		});
	}

	public void enableClose() {
		this.close_btt.setEnabled(true);
	}

	public void updateMessage(final String msg) {
		this.lbl.setText(msg);
		this.revalidate();
		this.repaint();
	}
}
