package unused.trajectoryManagerPlugin.runnable;

import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public class TBLoaderMessage extends OmegaMessageEvent {

	private final boolean repaint;

	public TBLoaderMessage(final String msg, final boolean repaint) {
		super(msg);
		this.repaint = repaint;
	}

	public boolean isRepaint() {
		return this.repaint;
	}
}
