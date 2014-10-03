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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.constants.OmegaConstants;

public class GenericConfirmationDialog extends GenericDialog {

	private static final long serialVersionUID = -9143470688994233600L;

	private JLabel lbl;
	private JButton confirm, cancel;
	private boolean confirmation;

	public GenericConfirmationDialog(final RootPaneContainer parentContainer,
	        final String title, final String label, final boolean modal) {
		super(parentContainer, title, modal);

		this.confirmation = false;
		this.lbl.setText(label);
		this.revalidate();
		this.repaint();
		this.pack();
	}

	@Override
	protected void createAndAddWidgets() {
		this.lbl = new JLabel("");
		this.add(this.lbl, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		this.confirm = new JButton("Confirm");
		this.confirm.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.confirm);

		this.cancel = new JButton("Cancel");
		this.cancel.setPreferredSize(OmegaConstants.BUTTON_SIZE);
		buttonPanel.add(this.cancel);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void addListeners() {
		this.confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				GenericConfirmationDialog.this.confirmation = true;
				GenericConfirmationDialog.this.setVisible(false);
			}
		});
		this.cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				GenericConfirmationDialog.this.confirmation = false;
				GenericConfirmationDialog.this.setVisible(false);
			}
		});
	}

	public boolean getConfirmation() {
		return this.confirmation;
	}
}
