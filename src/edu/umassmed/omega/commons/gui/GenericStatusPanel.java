package edu.umassmed.omega.commons.gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import edu.umassmed.omega.commons.exceptions.OmegaPluginStatusPanelException;

public class GenericStatusPanel extends JPanel {

	private static final long serialVersionUID = 2082839769774952925L;

	private final int numStatus;

	private final List<JLabel> statusList;

	public GenericStatusPanel(final int numStatus) {
		this.numStatus = numStatus;
		this.statusList = new ArrayList<JLabel>();

		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setLayout(new GridLayout(numStatus, 1));

		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		for (int i = 0; i < this.numStatus; i++) {
			final JLabel status_lbl = new JLabel("");
			this.statusList.add(status_lbl);
			this.add(status_lbl);
		}
	}

	private void addListeners() {

	}

	public int getNumStatus() {
		return this.numStatus;
	}

	public void updateStatus(final int status, final String s)
	        throws OmegaPluginStatusPanelException {
		if (status >= this.numStatus)
			throw new OmegaPluginStatusPanelException("The status " + status
			        + " is not present, the maximum status index is "
			        + this.numStatus);
		this.statusList.get(status).setText(s);
		this.repaint();

	}
}
