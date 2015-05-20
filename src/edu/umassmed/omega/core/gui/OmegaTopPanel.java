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
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;

import edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.plugins.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.plugins.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.plugins.OmegaParticleTrackingPlugin;
import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.commons.plugins.OmegaSNRPlugin;
import edu.umassmed.omega.commons.plugins.OmegaTrackingMeasuresPlugin;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesRelinkingPlugin;
import edu.umassmed.omega.commons.plugins.OmegaTrajectoriesSegmentationPlugin;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;

public class OmegaTopPanel extends GenericPanel {
	private static final long serialVersionUID = -2511349408103225400L;

	private static Dimension button_dim = new Dimension(120, 60);

	// private final Map<Long, JButton> buttons;
	private final Map<String, Map<Long, OmegaPlugin>> pluginsCategories;
	private final Map<String, JButton> buttons;
	private final Map<String, OmegaPluginLauncherDialog> pluginsLauncher;

	public OmegaTopPanel(final JFrame parent) {
		super(parent);

		// this.buttons = new LinkedHashMap<Long, JButton>();
		this.pluginsLauncher = new LinkedHashMap<>();
		this.pluginsCategories = new LinkedHashMap<>();
		this.buttons = new LinkedHashMap<>();
	}

	private void addPluginToCategoryMap(final String s, final long id,
	        final OmegaPlugin plugin) {
		Map<Long, OmegaPlugin> pluginsMap = null;
		if (this.pluginsCategories.keySet().contains(s)) {
			pluginsMap = this.pluginsCategories.get(s);
		} else {
			pluginsMap = new LinkedHashMap<>();
		}
		pluginsMap.put(id, plugin);
		this.pluginsCategories.put(s, pluginsMap);
	}

	protected void initializePanel(final Map<Long, OmegaPlugin> registeredPlugin) {
		for (final Long id : registeredPlugin.keySet()) {
			final OmegaPlugin plugin = registeredPlugin.get(id);
			if (plugin instanceof OmegaSNRPlugin) {
				this.addPluginToCategoryMap(
						OmegaGUIConstants.TOPPANEL_PLUGINMENU_SNR_ESTIMATOR,
				        id, plugin);
			} else if (plugin instanceof OmegaTrackingMeasuresPlugin) {
				this.addPluginToCategoryMap(
				        OmegaGUIConstants.TOPPANEL_PLUGINMENU_TRACK_MEASURES,
				        id, plugin);
			} else if ((plugin instanceof OmegaTrajectoriesRelinkingPlugin)
			        || (plugin instanceof OmegaTrajectoriesSegmentationPlugin)) {
				this.addPluginToCategoryMap(
				        OmegaGUIConstants.TOPPANEL_PLUGINMENU_TRACK_MANAGER,
				        id, plugin);
			} else if (plugin instanceof OmegaParticleTrackingPlugin) {
				this.addPluginToCategoryMap(
				        OmegaGUIConstants.TOPPANEL_PLUGINMENU_PARTICLE_TRACKER,
				        id, plugin);
			} else if (plugin instanceof OmegaBrowserPlugin) {
				this.addPluginToCategoryMap(
				        OmegaGUIConstants.TOPPANEL_PLUGINMENU_DATA_BROWSER, id,
						plugin);
			} else if (plugin instanceof OmegaLoaderPlugin) {
				this.addPluginToCategoryMap(
				        OmegaGUIConstants.TOPPANEL_PLUGINMENU_IMAGE_BROWSER,
				        id, plugin);
			}
		}
		this.createAndAddWidgets();

		this.addListeners();
	}

	public void reinitializeStrings() {
		for (final String s : this.buttons.keySet()) {
			final JButton butt = this.buttons.get(s);
			final String name = OmegaStringUtilities.getHtmlString(s, " ",
					SwingConstants.CENTER);
			butt.setText(name);
		}
		for (final OmegaPluginLauncherDialog pluginLauncher : this.pluginsLauncher
				.values()) {
			pluginLauncher.reinitializeStrings();
		}
	}

	private void createAndAddWidgets() {
		this.setLayout(new FlowLayout());
		for (final String s : this.pluginsCategories.keySet()) {
			final JButton butt = new JButton(s);
			butt.setPreferredSize(OmegaTopPanel.button_dim);
			this.buttons.put(s, butt);
			this.add(butt);

			final String title = "Select " + s + " plugin to launch";

			this.pluginsLauncher.put(s,
			        new OmegaPluginLauncherDialog(this.getParentContainer(),
			                title, this.pluginsCategories.get(s)));
		}

		// for (final Long id : registeredPlugin.keySet()) {
		// final OmegaPlugin plugin = registeredPlugin.get(id);
		//
		// final JButton butt = new JButton(plugin.getShortName());
		//
		// butt.setPreferredSize(new Dimension(120, 120));
		// this.buttons.put(id, butt);
		// this.add(butt);
		// }
	}

	private void addListeners() {
		for (final String s : this.buttons.keySet()) {
			final JButton butt = this.buttons.get(s);
			butt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					OmegaTopPanel.this.showFrame(s);
				}
			});
		}
	}

	private void showFrame(final String s) {
		final OmegaPluginLauncherDialog dialog = this.pluginsLauncher.get(s);
		dialog.setVisible(true);
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		for (final OmegaPluginLauncherDialog panel : this.pluginsLauncher
		        .values()) {
			panel.updateParentContainer(parent);
		}
	}
}
