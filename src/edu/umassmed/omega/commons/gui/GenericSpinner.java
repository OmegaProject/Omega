package edu.umassmed.omega.commons.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.SpinnerUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.plaf.synth.SynthSpinnerUI;

import com.sun.java.swing.plaf.windows.WindowsSpinnerUI;

public class GenericSpinner extends JSpinner {
	private static final long serialVersionUID = 268748763066384390L;

	public GenericSpinner() {
		super();
		final SpinnerUI spiUI = this.getUI();
		if (spiUI instanceof WindowsSpinnerUI) {
			this.setUI(new OmegaWindowsSpinnerUI());
		} else if (spiUI instanceof SynthSpinnerUI) {
			this.setUI(new OmegaSynthSpinnerUI());
		} else if (spiUI instanceof BasicSpinnerUI) {
			this.setUI(new OmegaBasicSpinnerUI());
		}
	}

	class OmegaBasicSpinnerUI extends BasicSpinnerUI {

		@Override
		protected Component createNextButton() {
			final Component c = this.createArrowButton(SwingConstants.EAST);
			c.setName("Spinner.nextButton");
			this.installNextButtonListeners(c);
			return c;
		}

		@Override
		protected Component createPreviousButton() {
			final Component c = this.createArrowButton(SwingConstants.WEST);
			c.setName("Spinner.previousButton");
			this.installPreviousButtonListeners(c);
			return c;
		}

		// copied from BasicSpinnerUI
		private Component createArrowButton(final int direction) {
			final JButton b = new BasicArrowButton(direction);
			final Border buttonBorder = UIManager
			        .getBorder("Spinner.arrowButtonBorder");
			if (buttonBorder instanceof UIResource) {
				b.setBorder(new CompoundBorder(buttonBorder, null));
			} else {
				b.setBorder(buttonBorder);
			}
			b.setInheritsPopupMenu(true);
			return b;
		}

		@Override
		public void installUI(final JComponent c) {
			super.installUI(c);
			c.removeAll();
			c.setLayout(GenericSpinner.getSpinnerUIBorderLayout());
			c.add(this.createNextButton(), BorderLayout.EAST);
			c.add(this.createPreviousButton(), BorderLayout.WEST);
			c.add(this.createEditor(), BorderLayout.CENTER);
		}
	}

	class OmegaSynthSpinnerUI extends SynthSpinnerUI {
		@Override
		protected Component createNextButton() {
			final Component c = this.createArrowButton(SwingConstants.EAST);
			c.setName("Spinner.nextButton");
			this.installNextButtonListeners(c);
			return c;
		}

		@Override
		protected Component createPreviousButton() {
			final Component c = this.createArrowButton(SwingConstants.WEST);
			c.setName("Spinner.previousButton");
			this.installPreviousButtonListeners(c);
			return c;
		}

		// copied from BasicSpinnerUI
		private Component createArrowButton(final int direction) {
			final JButton b = new BasicArrowButton(direction);
			final Border buttonBorder = UIManager
			        .getBorder("Spinner.arrowButtonBorder");
			if (buttonBorder instanceof UIResource) {
				b.setBorder(new CompoundBorder(buttonBorder, null));
			} else {
				b.setBorder(buttonBorder);
			}
			b.setInheritsPopupMenu(true);
			return b;
		}

		@Override
		public void installUI(final JComponent c) {
			super.installUI(c);
			c.removeAll();
			c.setLayout(GenericSpinner.getSpinnerUIBorderLayout());
			c.add(this.createNextButton(), BorderLayout.EAST);
			c.add(this.createPreviousButton(), BorderLayout.WEST);
			c.add(this.createEditor(), BorderLayout.CENTER);
		}
	}

	class OmegaWindowsSpinnerUI extends WindowsSpinnerUI {
		@Override
		protected Component createNextButton() {
			final Component c = this.createArrowButton(SwingConstants.EAST);
			c.setName("Spinner.nextButton");
			this.installNextButtonListeners(c);
			return c;
		}

		@Override
		protected Component createPreviousButton() {
			final Component c = this.createArrowButton(SwingConstants.WEST);
			c.setName("Spinner.previousButton");
			this.installPreviousButtonListeners(c);
			return c;
		}

		// copied from BasicSpinnerUI
		private Component createArrowButton(final int direction) {
			final JButton b = new BasicArrowButton(direction);
			final Border buttonBorder = UIManager
			        .getBorder("Spinner.arrowButtonBorder");
			if (buttonBorder instanceof UIResource) {
				b.setBorder(new CompoundBorder(buttonBorder, null));
			} else {
				b.setBorder(buttonBorder);
			}
			b.setInheritsPopupMenu(true);
			return b;
		}

		@Override
		public void installUI(final JComponent c) {
			super.installUI(c);
			c.removeAll();
			c.setLayout(GenericSpinner.getSpinnerUIBorderLayout());
			c.add(this.createNextButton(), BorderLayout.EAST);
			c.add(this.createPreviousButton(), BorderLayout.WEST);
			c.add(this.createEditor(), BorderLayout.CENTER);
		}
	}

	private static BorderLayout getSpinnerUIBorderLayout() {
		return new BorderLayout() {
			private static final long serialVersionUID = -1540280557847798231L;

			@Override
			public void addLayoutComponent(final Component comp,
			        Object constraints) {
				if (constraints.equals("Editor")) {
					constraints = BorderLayout.CENTER;
				}
				super.addLayoutComponent(comp, constraints);
			}
		};
	}
}
