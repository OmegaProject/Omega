package edu.umassmed.omega.commons.gui.checkboxTree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

public class CheckBoxNodeEditor extends TriStateCheckBox implements
        TreeCellEditor {

	private static final long serialVersionUID = 2410966108128999421L;

	private final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	private final JPanel panel = new JPanel(new BorderLayout());
	private String str = null;

	public CheckBoxNodeEditor() {
		super();
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				// System.out.println("actionPerformed: stopCellEditing");
				CheckBoxNodeEditor.this.stopCellEditing();
			}
		});
		this.panel.setFocusable(false);
		this.panel.setRequestFocusEnabled(false);
		this.panel.setOpaque(false);
		this.panel.add(this, BorderLayout.WEST);
		this.setOpaque(false);
	}

	@Override
	public Component getTreeCellEditorComponent(final JTree tree,
	        final Object value, final boolean isSelected,
	        final boolean expanded, final boolean leaf, final int row) {
		// JLabel l = (JLabel)renderer.getTreeCellRendererComponent(tree, value,
		// selected, expanded, leaf, row, hasFocus);
		final JLabel l = (JLabel) this.renderer.getTreeCellRendererComponent(
		        tree, value, true, expanded, leaf, row, true);
		l.setFont(tree.getFont());
		if (value instanceof DefaultMutableTreeNode) {
			this.setEnabled(tree.isEnabled());
			this.setFont(tree.getFont());
			final Object userObject = ((DefaultMutableTreeNode) value)
			        .getUserObject();
			if (userObject instanceof CheckBoxNode) {
				final CheckBoxNode node = (CheckBoxNode) userObject;
				if (node.getStatus() == CheckBoxStatus.INDETERMINATE) {
					this.setIcon(new IndeterminateIcon());
				} else {
					this.setIcon(null);
				}
				l.setText(node.getLabel());
				this.setSelected(node.getStatus() == CheckBoxStatus.SELECTED);
				this.str = node.getLabel();
			}
			// panel.add(this, BorderLayout.WEST);
			this.panel.add(l);
			return this.panel;
		}
		return l;
	}

	@Override
	public Object getCellEditorValue() {
		return new CheckBoxNode(this.str,
		        this.isSelected() ? CheckBoxStatus.SELECTED
		                : CheckBoxStatus.DESELECTED);
	}

	@Override
	public boolean isCellEditable(final EventObject event) {
		if ((event instanceof MouseEvent)
		        && (event.getSource() instanceof JTree)) {
			final MouseEvent me = (MouseEvent) event;
			final JTree tree = (JTree) event.getSource();
			final TreePath path = tree.getPathForLocation(me.getX(), me.getY());
			final Rectangle r = tree.getPathBounds(path);
			if (r == null)
				return false;
			final Dimension d = this.getPreferredSize();
			r.setSize(new Dimension(d.width, r.height));
			if (r.contains(me.getX(), me.getY())) {
				if ((this.str == null)
				        && System.getProperty("java.version").startsWith(
				                "1.7.0")) {
					System.out.println("XXX: Java 7, only on first run\n"
					        + this.getBounds());
					this.setBounds(new Rectangle(0, 0, d.width, r.height));
				}
				// System.out.println(getBounds());
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateUI() {
		super.updateUI();
		this.setName("Tree.cellEditor");
		if (this.panel != null) {
			// panel.removeAll(); //??? Change to Nimbus LnF, JDK 1.6.0
			this.panel.updateUI();
			// panel.add(this, BorderLayout.WEST);
		}
		// ???#1: JDK 1.6.0 bug??? @see 1.7.0 DefaultTreeCellRenderer#updateUI()
		// if(System.getProperty("java.version").startsWith("1.6.0")) {
		// renderer = new DefaultTreeCellRenderer();
		// }
	}

	// Copid from AbstractCellEditor
	// protected EventListenerList listenerList = new EventListenerList();
	// transient protected ChangeEvent changeEvent = null;
	@Override
	public boolean shouldSelectCell(final EventObject anEvent) {
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		this.fireEditingStopped();
		return true;
	}

	@Override
	public void cancelCellEditing() {
		this.fireEditingCanceled();
	}

	@Override
	public void addCellEditorListener(final CellEditorListener l) {
		this.listenerList.add(CellEditorListener.class, l);
	}

	@Override
	public void removeCellEditorListener(final CellEditorListener l) {
		this.listenerList.remove(CellEditorListener.class, l);
	}

	public CellEditorListener[] getCellEditorListeners() {
		return this.listenerList.getListeners(CellEditorListener.class);
	}

	protected void fireEditingStopped() {
		// Guaranteed to return a non-null array
		final Object[] listeners = this.listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (this.changeEvent == null) {
					this.changeEvent = new ChangeEvent(this);
				}
				((CellEditorListener) listeners[i + 1])
				        .editingStopped(this.changeEvent);
			}
		}
	}

	protected void fireEditingCanceled() {
		// Guaranteed to return a non-null array
		final Object[] listeners = this.listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (this.changeEvent == null) {
					this.changeEvent = new ChangeEvent(this);
				}
				((CellEditorListener) listeners[i + 1])
				        .editingCanceled(this.changeEvent);
			}
		}
	}
}