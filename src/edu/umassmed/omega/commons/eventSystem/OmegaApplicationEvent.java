package edu.umassmed.omega.commons.eventSystem;

public class OmegaApplicationEvent {

	public static final int SOURCE_MAIN_MENU = 0;
	public static final int SOURCE_WORKSPACE = 1;
	public static final int SOURCE_SIDE_BAR = 2;

	private final int source;

	public OmegaApplicationEvent() {
		this(-1);
	}

	public OmegaApplicationEvent(final int source) {
		this.source = source;
	}

	public int getSource() {
		return this.source;
	}
}
