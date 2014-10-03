package edu.umassmed.omega.trajectoryManagerPlugin.runnable;

import edu.umassmed.omega.commons.eventSystem.OmegaMessageEvent;

public class TMLoaderMessage extends OmegaMessageEvent {

	private final boolean repaint;

	public TMLoaderMessage(final String msg, final boolean repaint) {
		super(msg);
		this.repaint = repaint;
	}

	public boolean isRepaint() {
		return this.repaint;
	}
}
