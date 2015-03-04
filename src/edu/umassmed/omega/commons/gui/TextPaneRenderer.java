package edu.umassmed.omega.commons.gui;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

/* 
 **  Tabs are harder to use in a JTextPane, but much more flexible 
 */
public class TextPaneRenderer extends JTextPane implements ListCellRenderer {
	private static final long serialVersionUID = -1185380383832803026L;

	public TextPaneRenderer(final int tabColumn) {
		this.setMargin(new Insets(0, 0, 0, 0));

		final FontMetrics fm = this.getFontMetrics(this.getFont());
		final int width = fm.charWidth('w') * tabColumn;

		final TabStop[] tabs = new TabStop[1];
		tabs[0] = new TabStop(width, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
		final TabSet tabSet = new TabSet(tabs);

		final SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setTabSet(attributes, tabSet);
		this.getStyledDocument()
		        .setParagraphAttributes(0, 0, attributes, false);
	}

	@Override
	public Component getListCellRendererComponent(final JList list,
	        final Object value, final int index, final boolean isSelected,
	        final boolean cellHasFocus) {
		final String item = (String) value;

		this.setText(item);
		// if (index == -1) {
		// this.setText(item.getDescription());
		// } else {
		// this.setText(item.getId() + "\t" + item.getDescription());
		// }

		this.setBackground(isSelected ? list.getSelectionBackground() : null);
		this.setForeground(isSelected ? list.getSelectionForeground() : null);
		return this;
	}
}