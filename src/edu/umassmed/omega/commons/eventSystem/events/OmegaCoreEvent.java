package edu.umassmed.omega.commons.eventSystem.events;

public class OmegaCoreEvent {

	public static final int SOURCE_MAIN_MENU = 0;
	public static final int SOURCE_WORKSPACE = 1;
	public static final int SOURCE_SIDE_BAR = 2;
	public static final int SOURCE_APP = 3;

	private final int source;

	public OmegaCoreEvent() {
		this(-1);
	}

	public OmegaCoreEvent(final int source) {
		this.source = source;
	}

	public int getSource() {
		return this.source;
	}
}
