package edu.umassmed.omega.omeroPlugin.gui;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.gui.GenericPanel;

public class OmeroOptionsPanel extends GenericPanel {

	private static final long serialVersionUID = -8855766053467978179L;

	private JCheckBox autoSelectRelatives;

	public OmeroOptionsPanel(final RootPaneContainer parent) {
		super(parent);

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		this.createAndAddWidgets();
	}

	public void createAndAddWidgets() {
		this.autoSelectRelatives = new JCheckBox("Auto select parents/children");

		this.add(this.autoSelectRelatives);
	}

	public boolean isAutoSelectRelatives() {
		return this.autoSelectRelatives.isSelected();
	}
}
