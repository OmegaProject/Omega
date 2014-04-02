package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.dataNew.OmegaData;

public class OmegaBrowserPluginEvent extends OmegaPluginEvent {

	private final OmegaData loadedData;

	public OmegaBrowserPluginEvent() {
		this(null, null);
	}

	public OmegaBrowserPluginEvent(final OmegaPlugin source,
	        final OmegaData loadedData) {
		super(source);
		this.loadedData = loadedData;
	}

	public OmegaData getLoadedData() {
		return this.loadedData;
	}
}
