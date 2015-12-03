package main.java.edu.umassmed.omega.plSbalzariniPlugin.runnable;

import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public class PLMessageEvent extends OmegaMessageEvent {

	private final PLRunnable source;
	private final boolean isEnded;

	public PLMessageEvent(final String msg, final PLRunnable source,
	        final boolean isEnded) {
		super(msg);
		this.source = source;
		this.isEnded = isEnded;
	}

	public PLRunnable getSource() {
		return this.source;
	}

	public boolean isEnded() {
		return this.isEnded;
	}
}
