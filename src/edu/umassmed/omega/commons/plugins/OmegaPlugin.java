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
package edu.umassmed.omega.commons.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.eventSystem.OmegaPluginEventListener;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEvent;
import edu.umassmed.omega.commons.exceptions.OmegaCoreExceptionPluginMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;

public abstract class OmegaPlugin {
	private final List<OmegaPluginEventListener> listeners = new ArrayList<OmegaPluginEventListener>();

	private final int maximumNumberOfPanels;

	private final List<GenericPluginPanel> panels;
	private final List<Integer> indexes;

	private final Map<String, String> pluginOptions;

	public OmegaPlugin() {
		this(1);
	}

	public OmegaPlugin(final int maxNumOfPanels) {
		this.maximumNumberOfPanels = maxNumOfPanels;
		this.panels = new ArrayList<GenericPluginPanel>();
		this.indexes = new ArrayList<Integer>();

		this.pluginOptions = new LinkedHashMap<String, String>();
	}

	public GenericPluginPanel getNewPanel(final RootPaneContainer parent,
	        final int startingIndex) throws OmegaCoreExceptionPluginMissingData {
		if (this.panels.size() >= this.maximumNumberOfPanels)
			return null;

		final int index = startingIndex + this.panels.size();
		final GenericPluginPanel panel = this.createNewPanel(parent, index);
		this.indexes.add(index);
		this.panels.add(panel);
		return panel;
	}

	public boolean maximumReached() {
		return this.panels.size() >= this.maximumNumberOfPanels;
	}

	public List<Integer> getIndexes() {
		return Collections.unmodifiableList(this.indexes);
	}

	public String getOptionsCategory() {
		return "CATEGORY " + this.getName().toUpperCase() + " PLUGIN";
	}

	public void addPluginOptions(final Map<String, String> pluginOptions) {
		for (final String optionKey : pluginOptions.keySet()) {
			this.pluginOptions.put(optionKey, pluginOptions.get(optionKey));
		}
	}

	public Map<String, String> getPluginOptions() {
		return new LinkedHashMap<String, String>(this.pluginOptions);
	}

	public abstract String getName();

	public String getShortName() {
		return this.getName();
	}

	// TODO check if needed
	public abstract void run();

	public abstract GenericPluginPanel createNewPanel(RootPaneContainer parent,
	        int index) throws OmegaCoreExceptionPluginMissingData;

	public synchronized void addOmegaPluginListener(
	        final OmegaPluginEventListener listener) {
		this.listeners.add(listener);
	}

	public synchronized void removeOmegaPluginEventListener(
	        final OmegaPluginEventListener listener) {
		this.listeners.remove(listener);
	}

	public synchronized void fireEvent() {
		final OmegaPluginEvent event = new OmegaPluginEvent(null);
		final Iterator<OmegaPluginEventListener> i = this.listeners.iterator();
		while (i.hasNext()) {
			i.next().handlePluginEvent(event);
		}
	}

	public synchronized void fireEvent(final OmegaPluginEvent event) {
		final Iterator<OmegaPluginEventListener> i = this.listeners.iterator();
		while (i.hasNext()) {
			i.next().handlePluginEvent(event);
		}
	}

	protected List<GenericPluginPanel> getPanels() {
		return this.panels;
	}

	public abstract String getDescription();

	public Icon getIcon() {
		return null;
	}
}
