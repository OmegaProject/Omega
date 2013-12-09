package edu.umassmed.omega.commons.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class GenericFrame extends JFrame {

	private static final long serialVersionUID = -3768293722803137824L;

	public JFrame parent;

	public GenericFrame(final JFrame parent, final JComponent content,
	        final String title, final Point position, final Dimension dimension) {
		this.parent = parent;

		this.setTitle(title);
		// this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		this.getContentPane().setLayout(new BorderLayout());

		this.getContentPane().add(content, BorderLayout.CENTER);

		this.setLocation(position);
		this.setSize(dimension);

		// Display the window.
		// this.sidebarFrame.pack();
		this.setVisible(true);
		// this.pack();
	}

	public void doNothingOnClose() {
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	public void hideOnClose() {
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	public void disposeOnClose() {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	// TODO vedere se ne servono altri
}
