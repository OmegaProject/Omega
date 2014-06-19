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
package edu.umassmed.omega.core.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.gui.GenericPanel;

public class OmegaTopPanel extends GenericPanel {
	private static final long serialVersionUID = -2511349408103225400L;

	private final Map<Long, JButton> buttons;

	public OmegaTopPanel(final JFrame parent) {
		super(parent);

		this.buttons = new LinkedHashMap<Long, JButton>();
	}

	protected void initializePanel(final Map<Long, OmegaPlugin> registeredPlugin) {
		this.createAndAddWidgets(registeredPlugin);

		this.addListeners();
	}

	private void createAndAddWidgets(
	        final Map<Long, OmegaPlugin> registeredPlugin) {
		this.setLayout(new FlowLayout());

		for (final Long id : registeredPlugin.keySet()) {
			final OmegaPlugin plugin = registeredPlugin.get(id);

			final JButton butt = new JButton(plugin.getShortName());

			butt.setPreferredSize(new Dimension(120, 120));
			this.buttons.put(id, butt);
			this.add(butt);
		}
	}

	private void addListeners() {
		for (final Long id : this.buttons.keySet()) {
			final JButton butt = this.buttons.get(id);
			butt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					if (OmegaTopPanel.this.getParentContainer() instanceof JFrame) {
						final JFrame frame = (JFrame) OmegaTopPanel.this
						        .getParentContainer();
						frame.firePropertyChange(OmegaGUIFrame.PROP_PLUGIN, -1, id);
					} else {
						final JInternalFrame intFrame = (JInternalFrame) OmegaTopPanel.this
						        .getParentContainer();
						intFrame.firePropertyChange(OmegaGUIFrame.PROP_PLUGIN, -1,
						        id);
					}
				}
			});
		}
	}
}
