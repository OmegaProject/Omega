package edu.umassmed.omega.commons.gui;

import java.awt.Rectangle;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.RootPaneContainer;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.plaf.synth.SynthComboBoxUI;

import com.sun.java.swing.plaf.motif.MotifComboBoxUI;
import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;

public class GenericComboBox<T> extends JComboBox<T> {
	private static final long serialVersionUID = -4643314968663559081L;

	private RootPaneContainer parent;

	public GenericComboBox(final RootPaneContainer parent) {
		super();
		this.parent = parent;
		this.setMaximumRowCount(OmegaConstants.COMBOBOX_MAX_OPTIONS);
		final ComboBoxUI cbUI = this.getUI();
		if (cbUI instanceof WindowsComboBoxUI) {
			this.setUI(new OmegaWindowsComboBoxUI());
		} else if (cbUI instanceof SynthComboBoxUI) {
			this.setUI(new OmegaSynthComboBoxUI());
		} else if (cbUI instanceof MotifComboBoxUI) {
			this.setUI(new OmegaMotifComboBoxUI());
		} else if (cbUI instanceof MetalComboBoxUI) {
			this.setUI(new OmegaMetalComboBoxUI());
		} else if (cbUI instanceof BasicComboBoxUI) {
			this.setUI(new OmegaBasicComboBoxUI());
		}
	}

	class OmegaBasicComboBoxUI extends BasicComboBoxUI {
		@Override
		protected ComboPopup createPopup() {
			final BasicComboPopup popup = new BasicComboPopup(this.comboBox) {
				private static final long serialVersionUID = -3152844361519119052L;

				@Override
				protected Rectangle computePopupBounds(final int px,
				        final int py, final int pw, final int ph) {
					final int width = GenericComboBox.this
					        .computePopupWidth(pw);
					final int startPoint = GenericComboBox.this
					        .computePopupStartPoint(px, width);
					return super.computePopupBounds(startPoint, py, width, ph);
				}
			};
			popup.getAccessibleContext().setAccessibleParent(this.comboBox);
			return popup;
		}
	}

	class OmegaMetalComboBoxUI extends MetalComboBoxUI {
		@Override
		protected ComboPopup createPopup() {
			final BasicComboPopup popup = new BasicComboPopup(this.comboBox) {
				private static final long serialVersionUID = -3152844361519119052L;

				@Override
				protected Rectangle computePopupBounds(final int px,
				        final int py, final int pw, final int ph) {
					final int width = GenericComboBox.this
					        .computePopupWidth(pw);
					final int startPoint = GenericComboBox.this
					        .computePopupStartPoint(px, width);
					return super.computePopupBounds(startPoint, py, width, ph);
				}
			};
			popup.getAccessibleContext().setAccessibleParent(this.comboBox);
			return popup;
		}
	}

	class OmegaMotifComboBoxUI extends MotifComboBoxUI {
		private static final long serialVersionUID = -2066085574045454227L;

		@Override
		protected ComboPopup createPopup() {
			final BasicComboPopup popup = new BasicComboPopup(this.comboBox) {
				private static final long serialVersionUID = -3152844361519119052L;

				@Override
				protected Rectangle computePopupBounds(final int px,
				        final int py, final int pw, final int ph) {
					final int width = GenericComboBox.this
					        .computePopupWidth(pw);
					final int startPoint = GenericComboBox.this
					        .computePopupStartPoint(px, width);
					return super.computePopupBounds(startPoint, py, width, ph);
				}
			};
			popup.getAccessibleContext().setAccessibleParent(this.comboBox);
			return popup;
		}
	}

	class OmegaSynthComboBoxUI extends SynthComboBoxUI {
		@Override
		protected ComboPopup createPopup() {
			final BasicComboPopup popup = new BasicComboPopup(this.comboBox) {
				private static final long serialVersionUID = -3152844361519119052L;

				@Override
				protected Rectangle computePopupBounds(final int px,
				        final int py, final int pw, final int ph) {
					final int width = GenericComboBox.this
					        .computePopupWidth(pw);
					final int startPoint = GenericComboBox.this
					        .computePopupStartPoint(px, width);
					return super.computePopupBounds(startPoint, py, width, ph);
				}
			};
			popup.getAccessibleContext().setAccessibleParent(this.comboBox);
			return popup;
		}
	}

	class OmegaWindowsComboBoxUI extends WindowsComboBoxUI {
		@Override
		protected ComboPopup createPopup() {
			final BasicComboPopup popup = new BasicComboPopup(this.comboBox) {
				private static final long serialVersionUID = -3152844361519119052L;

				@Override
				protected Rectangle computePopupBounds(final int px,
				        final int py, final int pw, final int ph) {
					final int width = GenericComboBox.this
					        .computePopupWidth(pw);
					final int startPoint = GenericComboBox.this
					        .computePopupStartPoint(px, width);
					return super.computePopupBounds(startPoint, py, width, ph);
				}
			};
			popup.getAccessibleContext().setAccessibleParent(this.comboBox);
			return popup;
		}
	}

	private int computePopupWidth(final int pw) {
		int maxWidth = pw;
		for (int i = 0; i < GenericComboBox.this.getItemCount(); i++) {
			final Object obj = GenericComboBox.this.getItemAt(i);
			final String s = obj.toString();
			final int width = OmegaStringUtilities.getStringSize(
			        GenericComboBox.this.getGraphics(),
			        GenericComboBox.this.getFont(), s).width;
			if (maxWidth < width) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	private int computePopupStartPoint(final int px, final int neededWidth) {
		final int startX = this.getLocationOnScreen().x;
		final int width = this.getWidth();
		final int endX = startX + width;
		if (neededWidth <= width)
			return px;
		int parentStartX, parentEndX;
		if (this.parent instanceof JInternalFrame) {
			final JInternalFrame specificParent = (JInternalFrame) this.parent;
			parentStartX = specificParent.getLocationOnScreen().x;
			parentEndX = parentStartX + specificParent.getWidth();
		} else if (this.parent instanceof JFrame) {
			final JFrame specificParent = (JFrame) this.parent;
			parentStartX = specificParent.getLocationOnScreen().x;
			parentEndX = parentStartX + specificParent.getWidth();
		} else
			return px;
		final int spaceBefore = Math.abs(parentStartX - startX);
		final int spaceAfter = Math.abs(parentEndX - endX);
		final int extraWidth = Math.abs(neededWidth - width);
		if ((spaceAfter < (extraWidth / 2)) && (spaceBefore >= extraWidth))
			return px - extraWidth;
		else if ((spaceBefore < (extraWidth / 2)) && (spaceAfter >= extraWidth))
			return px;
		else
			return px - (extraWidth / 2);
	}

	public void updateParentContainer(final RootPaneContainer parent) {
		this.parent = parent;
	}
}
