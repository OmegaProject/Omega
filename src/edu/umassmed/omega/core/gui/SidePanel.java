package edu.umassmed.omega.core.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.gui.GenericPanelInterface;

public class SidePanel extends JSplitPane implements GenericPanelInterface {

	private static final long serialVersionUID = -4565126277733287950L;

	private RootPaneContainer parent;
	private JPanel panel1, panel2;

	private boolean isAttached;

	// private JDesktopPane desktopPane;

	public SidePanel(final RootPaneContainer parent) {
		super(JSplitPane.VERTICAL_SPLIT, true);
		this.parent = parent;
		this.isAttached = true;
		this.setDividerLocation(0.75);
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		this.parent = parent;
	}

	protected void initializePanel() {
		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		// this.desktopPane = new JDesktopPane();
		// this.getViewport().add(this.desktopPane);

		this.panel1 = new JPanel();
		this.panel1.setBorder(new TitledBorder("Selected image"));
		// panel1.pack();
		// this.desktopPane.add(this.panel1);
		// this.add(this.panel1);
		this.setLeftComponent(this.panel1);
		// this.panel1.setLocation(new Point(0, 0));

		this.panel2 = new JPanel();
		this.panel2.setBorder(new TitledBorder("Image information"));
		// panel2.pack();
		this.panel2.setVisible(true);
		// this.desktopPane.add(this.panel2);
		// this.add(this.panel2);
		this.setRightComponent(this.panel2);
		// this.panel1.setLocation(new Point(0, 300));
	}

	private void addListeners() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				SidePanel.this.setDividerLocation(0.75);
			}
		});
	}

	public boolean isAttached() {
		return this.isAttached;
	}

	public void setAttached(final boolean tof) {
		this.isAttached = tof;
	}
}
