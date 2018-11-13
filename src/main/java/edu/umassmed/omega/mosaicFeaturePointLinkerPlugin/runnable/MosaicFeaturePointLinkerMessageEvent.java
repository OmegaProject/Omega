package edu.umassmed.omega.mosaicFeaturePointLinkerPlugin.runnable;

import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public class MosaicFeaturePointLinkerMessageEvent extends OmegaMessageEvent {

	private final MosaicFeaturePointLinkerRunnable source;
	private final boolean isEnded;

	public MosaicFeaturePointLinkerMessageEvent(final String msg, final MosaicFeaturePointLinkerRunnable source,
	        final boolean isEnded) {
		super(msg);
		this.source = source;
		this.isEnded = isEnded;
	}

	public MosaicFeaturePointLinkerRunnable getSource() {
		return this.source;
	}

	public boolean isEnded() {
		return this.isEnded;
	}
}
