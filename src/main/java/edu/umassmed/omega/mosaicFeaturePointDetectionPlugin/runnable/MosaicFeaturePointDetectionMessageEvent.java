package edu.umassmed.omega.mosaicFeaturePointDetectionPlugin.runnable;

import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public class MosaicFeaturePointDetectionMessageEvent extends OmegaMessageEvent {

	private final MosaicFeaturePointDetectionRunnable source;
	private final boolean isEnded, isPreview;

	public MosaicFeaturePointDetectionMessageEvent(final String msg, final MosaicFeaturePointDetectionRunnable source,
			final boolean isEnded, final boolean isPreview) {
		super(msg);
		this.source = source;
		this.isEnded = isEnded;
		this.isPreview = isPreview;
	}

	public MosaicFeaturePointDetectionRunnable getSource() {
		return this.source;
	}

	public boolean isEnded() {
		return this.isEnded;
	}

	public boolean isPreview() {
		return this.isPreview;
	}
}
