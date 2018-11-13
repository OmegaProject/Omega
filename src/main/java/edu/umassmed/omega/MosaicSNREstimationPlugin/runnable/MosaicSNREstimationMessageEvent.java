package edu.umassmed.omega.MosaicSNREstimationPlugin.runnable;

import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public class MosaicSNREstimationMessageEvent extends OmegaMessageEvent {

	private final MosaicSNREstimationRunnable source;
	private final boolean isEnded;

	public MosaicSNREstimationMessageEvent(final String msg, final MosaicSNREstimationRunnable source,
	        final boolean isEnded) {
		super(msg);
		this.source = source;
		this.isEnded = isEnded;
	}

	public MosaicSNREstimationRunnable getSource() {
		return this.source;
	}

	public boolean isEnded() {
		return this.isEnded;
	}
}
