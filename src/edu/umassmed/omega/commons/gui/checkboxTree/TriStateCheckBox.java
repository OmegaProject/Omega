package edu.umassmed.omega.commons.gui.checkboxTree;

import java.awt.EventQueue;

import javax.swing.Icon;
import javax.swing.JCheckBox;

public class TriStateCheckBox extends JCheckBox {

	private static final long serialVersionUID = -1959358561785514596L;

	private Icon currentIcon;

	@Override
	public void updateUI() {
		this.currentIcon = this.getIcon();
		this.setIcon(null);
		super.updateUI();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (TriStateCheckBox.this.currentIcon != null) {
					TriStateCheckBox.this.setIcon(new IndeterminateIcon());
				}
				TriStateCheckBox.this.setOpaque(false);
			}
		});
	}
}