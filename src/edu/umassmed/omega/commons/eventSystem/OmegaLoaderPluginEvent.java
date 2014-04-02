package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.OmegaPlugin;

public class OmegaLoaderPluginEvent extends OmegaPluginEvent {

	private final boolean dataChanged;

	public OmegaLoaderPluginEvent() {
		this(null, false);
	}

	public OmegaLoaderPluginEvent(final OmegaPlugin source,
	        final boolean dataChanged) {
		super(source);
		this.dataChanged = dataChanged;
	}

	public boolean isDataChanged() {
		return this.dataChanged;
	}
}
