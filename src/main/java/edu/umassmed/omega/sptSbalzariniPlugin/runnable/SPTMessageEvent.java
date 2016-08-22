package edu.umassmed.omega.sptSbalzariniPlugin.runnable;

import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public class SPTMessageEvent extends OmegaMessageEvent {

	private final SPTRunnable source;
	private final boolean isEnded;

	public SPTMessageEvent(final String msg, final SPTRunnable source,
	        final boolean isEnded) {
		super(msg);
		this.source = source;
		this.isEnded = isEnded;
	}

	public SPTRunnable getSource() {
		return this.source;
	}

	public boolean isEnded() {
		return this.isEnded;
	}
}
