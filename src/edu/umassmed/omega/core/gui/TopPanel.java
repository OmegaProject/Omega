package edu.umassmed.omega.core.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import edu.umassmed.omega.commons.gui.GenericPanel;

public class TopPanel extends GenericPanel {
	private static final long serialVersionUID = -2511349408103225400L;

	private JButton omeroButt;
	private JButton imageViewer, b3, b4, b5;

	public TopPanel(final JFrame parent) {
		super(parent);

	}

	protected void initializePanel() {
		this.createAndAddWidgets();

		this.addListeners();
	}

	private void createAndAddWidgets() {
		this.setLayout(new FlowLayout());

		this.omeroButt = new JButton("Omero");
		this.omeroButt.setPreferredSize(new Dimension(120, 120));
		this.imageViewer = new JButton("Image viewer");
		this.imageViewer.setPreferredSize(new Dimension(120, 120));
		this.b3 = new JButton("Button2");
		this.b3.setPreferredSize(new Dimension(120, 120));
		this.b4 = new JButton("Button2");
		this.b4.setPreferredSize(new Dimension(120, 120));
		this.b5 = new JButton("Button2");
		this.b5.setPreferredSize(new Dimension(120, 120));

		this.add(this.omeroButt);
		this.add(this.imageViewer);
		this.add(this.b3);
		this.add(this.b4);
		this.add(this.b5);
	}

	private void addListeners() {
		this.omeroButt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				if (TopPanel.this.getParentContainer() instanceof JFrame) {
					final JFrame frame = (JFrame) TopPanel.this
					        .getParentContainer();
					frame.firePropertyChange(OmegaFrame.PROP_PLUGIN, -1, 0);
				} else {
					final JInternalFrame intFrame = (JInternalFrame) TopPanel.this
					        .getParentContainer();
					intFrame.firePropertyChange(OmegaFrame.PROP_PLUGIN,
					        (long) -1, (long) 0);
				}
			}
		});
	}
}
