package edu.umassmed.omega.core;

import java.util.HashMap;
import java.util.Map;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.core.gui.OmegaFrame;
import edu.umassmed.omega.omero.OmeroPlugin;

public class OmegaApplication {

	private OmegaFrame gui;

	private short pluginIndex;
	private final Map<String, Long> pluginIndexes;
	private final Map<Long, OmegaPlugin> registeredPlugin;

	private final OmegaOptionsFileManager optionsFileManager;

	public OmegaApplication() {
		this.pluginIndexes = new HashMap<String, Long>();
		this.registeredPlugin = new HashMap<Long, OmegaPlugin>();
		this.pluginIndex = 0;

		this.optionsFileManager = new OmegaOptionsFileManager();

		this.registerCorePlugins();
	}

	private void registerCorePlugins() {
		new OmeroPlugin();
		this.registerPlugin(new OmeroPlugin());

		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			final String optionsCategory = plugin.getOptionsCategory();
			plugin.addPluginOptions(this.optionsFileManager
			        .getOptions(optionsCategory));
		}
	}

	private void registerPlugin(final OmegaPlugin plugin) {
		final String name = plugin.getName();
		final long index = this.pluginIndex;
		this.pluginIndex++;
		this.registeredPlugin.put(index, plugin);
		this.pluginIndexes.put(name, index);
	}

	protected void initializeGUI() {
		this.gui = new OmegaFrame(this);
		this.gui.initialize();
		this.gui.setSize(1250, 750);
	}

	public OmegaPlugin getPlugin(final long pluginIndex) {
		return this.registeredPlugin.get(pluginIndex);
	}

	public void saveOptions() {
		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			this.optionsFileManager.addOptions(plugin.getOptionsCategory(),
			        plugin.getPluginOptions());
		}
		this.optionsFileManager.saveOptionsToFile();
	}

	public static void main(final String[] args) {
		final OmegaApplication instance = new OmegaApplication();
		instance.initializeGUI();
	}
}
