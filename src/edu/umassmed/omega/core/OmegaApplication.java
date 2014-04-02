package edu.umassmed.omega.core;

import java.util.HashMap;
import java.util.Map;

import edu.umassmed.omega.commons.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.eventSystem.OmegaBrowserPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaBrowserPluginListener;
import edu.umassmed.omega.commons.eventSystem.OmegaLoaderPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaLoaderPluginListener;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaPluginListener;
import edu.umassmed.omega.core.gui.OmegaGUIFrame;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserPlugin;
import edu.umassmed.omega.omeroPlugin.OmeroPlugin;

public class OmegaApplication implements OmegaPluginListener,
        OmegaLoaderPluginListener, OmegaBrowserPluginListener {

	private final OmegaGUIFrame gui;

	private final OmegaData omegaData;
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

		// TODO load data here
		this.omegaData = new OmegaData();
		this.gateway = null;

		this.registerCorePlugins();

		this.gui = new OmegaGUIFrame(this);
		this.gui.initialize(this.registeredPlugin);
		this.gui.setSize(1250, 750);
	}

	private void registerCorePlugins() {
		this.registerPlugin(new OmeroPlugin());
		this.registerPlugin(new OmegaDataBrowserPlugin());

		for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
			final String optionsCategory = plugin.getOptionsCategory();
			plugin.addPluginOptions(this.optionsFileManager
			        .getOptions(optionsCategory));
			plugin.setOmegaData(this.omegaData);
		}
	}

	private void registerPlugin(final OmegaPlugin plugin) {
		plugin.addOmegaPluginListener(this);
		if (plugin instanceof OmegaLoaderPlugin) {
			((OmegaLoaderPlugin) plugin).addOmegaLoaderPluginListener(this);
		}
		if (plugin instanceof OmegaBrowserPlugin) {
			((OmegaBrowserPlugin) plugin).addOmegaBrowserPluginListener(this);
		}
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

	protected Map<Long, OmegaPlugin> getPlugins() {
		return this.registeredPlugin;
	}

	@Override
	public void handleOmegaPluginEvent(final OmegaPluginEvent event) {

	}

	@Override
	public void handleOmegaLoaderPluginEvent(final OmegaLoaderPluginEvent event) {
		final OmegaLoaderPlugin loaderPlugin = (OmegaLoaderPlugin) event
		        .getSource();
		this.gateway = loaderPlugin.getGateway();

		// TODO integrare dati caricati
		// final OmegaData loadedData = event.getLoadedData();
		// this.omegaData.mergeData(loadedData);

		final boolean dataChanged = event.isDataChanged();

		if (dataChanged) {
			for (final OmegaPlugin plugin : this.registeredPlugin.values()) {
				if (plugin instanceof OmegaBrowserPlugin) {
					((OmegaBrowserPlugin) plugin).fireUpdate();
				}
			}
		}
	}

	@Override
	public void handleOmegaBrowserPluginEvent(
	        final OmegaBrowserPluginEvent event) {

	}

	public static void main(final String[] args) {
		final OmegaApplication instance = new OmegaApplication();
		instance.showGUI();
	}
}
