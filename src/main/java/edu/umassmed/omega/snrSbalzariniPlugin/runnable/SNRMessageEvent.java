package main.java.edu.umassmed.omega.snrSbalzariniPlugin.runnable;

import main.java.edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public class SNRMessageEvent extends OmegaMessageEvent {

	private final SNRRunnable source;
	private final boolean isEnded;

	public SNRMessageEvent(final String msg, final SNRRunnable source,
	        final boolean isEnded) {
		super(msg);
		this.source = source;
		this.isEnded = isEnded;
	}

	public SNRRunnable getSource() {
		return this.source;
	}

	public boolean isEnded() {
		return this.isEnded;
	}
}
