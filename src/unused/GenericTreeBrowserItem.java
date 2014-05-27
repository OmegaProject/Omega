package unused;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.tree.TreeNode;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;

public class GenericTreeBrowserItem implements TreeNode {

	private final JCheckBox checkbox;
	private final OmegaElement element;
	private final List<GenericTreeBrowserItem> children;
	private final GenericTreeBrowserItem parent;
	private Boolean selected;

	public GenericTreeBrowserItem() {
		this(null, null);
	}

	public GenericTreeBrowserItem(final GenericTreeBrowserItem parent,
	        final OmegaElement element) {
		this.element = element;
		this.parent = parent;
		this.selected = false;
		this.children = new ArrayList<GenericTreeBrowserItem>();

		this.checkbox = new JCheckBox();
		this.checkbox.setText(this.getText());
		this.checkbox.setSelected(this.selected);
	}

	public void addChild(final GenericTreeBrowserItem child) {
		this.children.add(child);
	}

	public Long getId() {
		if (this.element != null)
			return this.element.getElementID();
		return null;
	}

	public String getName() {
		if ((this.element != null)
		        && (this.element instanceof OmegaNamedElement))
			return ((OmegaNamedElement) this.element).getName();
		else
			return "";
	}

	public String getText() {
		if (this.element == null)
			return "Loaded data";
		final String s = "[" + this.getId() + "]";
		if (this.element instanceof OmegaNamedElement)
			return s + " " + this.getName();
		else
			return s;
	}

	public void select() {
		this.selected = true;
	}

	public void deselect() {
		this.selected = false;
	}

	public Boolean isSelected() {
		return this.selected;
	}

	public void setSelection(final boolean selected) {
		this.selected = selected;
	}

	@Override
	public TreeNode getChildAt(final int childIndex) {
		return this.children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return this.children.size();
	}

	@Override
	public TreeNode getParent() {
		return this.parent;
	}

	@Override
	public int getIndex(final TreeNode node) {
		return this.children.indexOf(node);
	}

	@Override
	public boolean getAllowsChildren() {
		if (this.element instanceof OmegaImage)
			return false;
		return true;
	}

	@Override
	public boolean isLeaf() {
		if (this.element instanceof OmegaImage)
			return true;
		return false;
	}

	@Override
	public Enumeration children() {
		return null;
	}
}
