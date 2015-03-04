package edu.umassmed.omega.commons.gui.interfaces;

import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEvent;

public interface OmegaMessageDisplayerPanelInterface {
	public void updateMessageStatus(OmegaMessageEvent evt);
}
