package edu.umassmed.omega.sdSbalzariniPlugin.runnable;

import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public class SDMessageEvent extends OmegaMessageEvent {

	private final SDRunnable source;
	private final boolean isEnded, isPreview;

	public SDMessageEvent(final String msg, final SDRunnable source,
			final boolean isEnded, final boolean isPreview) {
		super(msg);
		this.source = source;
		this.isEnded = isEnded;
		this.isPreview = isPreview;
	}

	public SDRunnable getSource() {
		return this.source;
	}

	public boolean isEnded() {
		return this.isEnded;
	}

	public boolean isPreview() {
		return this.isPreview;
	}
}
