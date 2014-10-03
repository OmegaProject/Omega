package edu.umassmed.omega.omeroPlugin.runnable;

import java.util.List;

import edu.umassmed.omega.commons.eventSystem.OmegaMessageEvent;
import edu.umassmed.omega.omeroPlugin.data.OmeroImageWrapper;

public class OmeroWrapperMessageEvent extends OmegaMessageEvent {

	private final List<OmeroImageWrapper> wrappers;

	public OmeroWrapperMessageEvent(final String msg,
	        final List<OmeroImageWrapper> wrappers) {
		super(msg);
		this.wrappers = wrappers;
	}

	public List<OmeroImageWrapper> getWrappers() {
		return this.wrappers;
	}
}
