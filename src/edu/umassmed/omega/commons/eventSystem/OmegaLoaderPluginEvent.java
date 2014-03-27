package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.dataNew.OmegaData;

public class OmegaLoaderPluginEvent extends OmegaPluginEvent {

	private final OmegaData loadedData;

	public OmegaLoaderPluginEvent() {
		this(null, null);
	}

	public OmegaLoaderPluginEvent(final OmegaPlugin source,
	        final OmegaData loadedData) {
		super(source);
		this.loadedData = loadedData;
	}

	public OmegaData getLoadedData() {
		return this.loadedData;
	}
}
