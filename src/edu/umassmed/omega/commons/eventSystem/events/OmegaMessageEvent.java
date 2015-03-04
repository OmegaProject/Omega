package edu.umassmed.omega.commons.eventSystem.events;


public class OmegaMessageEvent {

	private final String message;

	public OmegaMessageEvent(final String msg) {
		this.message = msg;
	}

	public String getMessage() {
		return this.message;
	}
}
