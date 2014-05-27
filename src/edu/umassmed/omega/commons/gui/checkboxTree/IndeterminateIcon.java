package edu.umassmed.omega.commons.gui.checkboxTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.UIManager;

public class IndeterminateIcon implements Icon {
	private final Color FOREGROUND = new Color(50, 20, 255, 200); // TEST:
	                                                              // UIManager.getColor("CheckBox.foreground");
	private final Icon icon = UIManager.getIcon("CheckBox.icon");
	private static final int a = 4;
	private static final int b = 2;

	@Override
	public void paintIcon(final Component c, final Graphics g, final int x,
	        final int y) {
		this.icon.paintIcon(c, g, x, y);
		final int w = this.getIconWidth();
		final int h = this.getIconHeight();
		final Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(this.FOREGROUND);
		g2.translate(x, y);
		g2.fillRect(IndeterminateIcon.a, (h - IndeterminateIcon.b) / 2, w
		        - IndeterminateIcon.a - IndeterminateIcon.a,
		        IndeterminateIcon.b);
		g2.translate(-x, -y);
	}

	@Override
	public int getIconWidth() {
		return this.icon.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return this.icon.getIconHeight();
	}
}