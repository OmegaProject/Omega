package edu.umassmed.omega.commons.gui;

import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

public class GenericImagePanel extends JPanel implements GenericPanelInterface {

	private static final long serialVersionUID = 7177189735018949022L;

	private RootPaneContainer parent;

	public GenericImagePanel(final RootPaneContainer parent) {
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
