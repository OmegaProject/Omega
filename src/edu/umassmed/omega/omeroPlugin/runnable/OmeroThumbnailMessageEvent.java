package edu.umassmed.omega.omeroPlugin.runnable;

import java.util.List;

import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;
import edu.umassmed.omega.omeroPlugin.data.OmeroThumbnailImageInfo;

public class OmeroThumbnailMessageEvent extends OmegaMessageEvent {
	private final List<OmeroThumbnailImageInfo> thumbnails;

	public OmeroThumbnailMessageEvent(final String msg,
	        final List<OmeroThumbnailImageInfo> thumbnails) {
		super(msg);
		this.thumbnails = thumbnails;
	}

	public List<OmeroThumbnailImageInfo> getThumbnails() {
		return this.thumbnails;
	}
}
