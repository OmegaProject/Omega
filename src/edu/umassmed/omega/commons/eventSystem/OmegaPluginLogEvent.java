package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;

public class OmegaPluginLogEvent extends OmegaPluginEvent {

	private final String message;
	private final Throwable t;

	public OmegaPluginLogEvent(final OmegaPlugin source, final String message,
	        final Throwable t) {
		super(source);
		this.message = message;
		this.t = t;
	}

	public OmegaPluginLogEvent(final OmegaPlugin source, final Throwable t) {
		this(source, null, t);
	}

	public String getMessage() {
		return this.message;
	}

	public Throwable getException() {
		return this.t;
	}
}
