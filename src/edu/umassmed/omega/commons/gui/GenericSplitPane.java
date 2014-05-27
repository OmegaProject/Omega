package edu.umassmed.omega.commons.gui;

import javax.swing.JSplitPane;
import javax.swing.RootPaneContainer;

public class GenericSplitPane extends JSplitPane implements GenericPanelInterface {
	private static final long serialVersionUID = 7177189735018949022L;

	private RootPaneContainer parent;

	public GenericSplitPane(final RootPaneContainer parent,
	        final int splitOrientation, final boolean continuousLayout) {
		super(splitOrientation, continuousLayout);
		this.parent = parent;
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		this.parent = parent;
	}

	protected RootPaneContainer getParentContainer() {
		return this.parent;
	}
}
