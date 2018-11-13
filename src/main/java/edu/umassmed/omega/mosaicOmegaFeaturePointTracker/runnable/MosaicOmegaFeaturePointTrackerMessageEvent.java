package edu.umassmed.omega.mosaicOmegaFeaturePointTracker.runnable;

import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public class MosaicOmegaFeaturePointTrackerMessageEvent extends OmegaMessageEvent {

	private final MosaicOmegaFeaturePointTrackerRunnable source;
	private final boolean isEnded;

	public MosaicOmegaFeaturePointTrackerMessageEvent(final String msg, final MosaicOmegaFeaturePointTrackerRunnable source,
	        final boolean isEnded) {
		super(msg);
		this.source = source;
		this.isEnded = isEnded;
	}

	public MosaicOmegaFeaturePointTrackerRunnable getSource() {
		return this.source;
	}

	public boolean isEnded() {
		return this.isEnded;
	}
}
