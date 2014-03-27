package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.OmegaPlugin;

public class OmegaPluginEvent {

	private final OmegaPlugin source;

	public OmegaPluginEvent() {
		this(null);
	}

	public OmegaPluginEvent(final OmegaPlugin source) {
		this.source = source;
	}

	public OmegaPlugin getSource() {
		return this.source;
	}
}
