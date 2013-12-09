package edu.umassmed.omega.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.gui.GenericPluginPanel;

public abstract class OmegaPlugin implements OmegaPluginInterface {
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
	        final int startingIndex) {
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
		for (final String option : pluginOptions.keySet()) {
			this.pluginOptions.put(option, pluginOptions.get(option));
		}
		System.out.println(this.getOptionsCategory());
		for (final String s : pluginOptions.keySet()) {
			System.out.println(s + "\t" + pluginOptions.get(s));
		}
	}

	public Map<String, String> getPluginOptions() {
		return new LinkedHashMap<String, String>(this.pluginOptions);
	}
}
