package edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.gui.GenericPanel;

public class OmegaDataBrowserOptionsPanel extends GenericPanel {

	private static final long serialVersionUID = -8855766053467978179L;

	public JCheckBox selectAllSubItemCheckbox;

	public OmegaDataBrowserOptionsPanel(final RootPaneContainer parent) {
		super(parent);

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		this.createAndAddWidgets();
	}

	public void createAndAddWidgets() {
		this.selectAllSubItemCheckbox = new JCheckBox("Select all sub items");

		this.add(this.selectAllSubItemCheckbox);
	}

	public boolean isSelectAllSubItems() {
		return this.selectAllSubItemCheckbox.isSelected();
	}

}
