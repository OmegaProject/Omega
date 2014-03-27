package edu.umassmed.omega.core;

import java.util.HashMap;
import java.util.Map;

import edu.umassmed.omega.commons.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.eventSystem.OmegaLoaderPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaLoaderPluginListener;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginListener;
import edu.umassmed.omega.core.gui.OmegaFrame;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.connection.OmegaGateway;
import edu.umassmed.omega.omeroPlugin.OmeroPlugin;

public class OmegaApplication implements OmegaPluginListener,
        OmegaLoaderPluginListener {

	private final OmegaFrame gui;

	private final OmegaData data;
	private OmegaGateway gateway;

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

		this.gui = new OmegaFrame(this);
		this.gui.initialize();
		this.gui.setSize(1250, 750);

		// TODO load data here
		this.data = new OmegaData();
		this.gateway = null;
	}

	private void registerCorePlugins() {
		this.registerPlugin(new OmeroPlugin());

		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			final String optionsCategory = plugin.getOptionsCategory();
			plugin.addPluginOptions(this.optionsFileManager
			        .getOptions(optionsCategory));
		}
	}

	private void registerPlugin(final OmegaPlugin plugin) {
		plugin.addOmegaPluginListener(this);
		final String name = plugin.getName();
		final long index = this.pluginIndex;
		this.pluginIndex++;
		this.registeredPlugin.put(index, plugin);
		this.pluginIndexes.put(name, index);
	}

	protected void showGUI() {
		this.gui.setVisible(true);
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
		instance.showGUI();
	}

	@Override
	public void handleOmegaPluginEvent(final OmegaPluginEvent event) {

	}

	@Override
	public void handleOmegaLoaderPluginEvent(final OmegaLoaderPluginEvent event) {
		if (!(event.getSource() instanceof OmegaLoaderPlugin))
			return;

		final OmegaLoaderPlugin plugin = (OmegaLoaderPlugin) event.getSource();
		this.gateway = plugin.getGateway();

		// TODO integrare dati caricati
		final OmegaData loadedData = event.getLoadedData();
		this.data.mergeData(loadedData);
	}
}
