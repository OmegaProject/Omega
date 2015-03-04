package edu.umassmed.omega.commons.eventSystem.events;


public class OmegaMessageEventTBLoader extends OmegaMessageEvent {

	private final boolean repaint;

	public OmegaMessageEventTBLoader(final String msg, final boolean repaint) {
		super(msg);
		this.repaint = repaint;
	}

	public boolean isRepaint() {
		return this.repaint;
	}
}
