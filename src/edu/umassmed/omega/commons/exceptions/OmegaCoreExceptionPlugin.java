package edu.umassmed.omega.commons.exceptions;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;

public class OmegaCoreExceptionPlugin extends OmegaCoreException {
	private static final long serialVersionUID = 4298107617349997503L;

	private final OmegaPlugin plugin;

	public OmegaCoreExceptionPlugin(final OmegaPlugin plugin,
	        final String message) {
		super(plugin.getName() + " - " + message);
		this.plugin = plugin;
	}

	public OmegaPlugin getSource() {
		return this.plugin;
	}
}
