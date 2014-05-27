package unused;

public class GenericListBrowserItem {

	private final Long id;
	private final String name;
	private Boolean selected;

	public GenericListBrowserItem(final Long id, final String name) {
		this.id = id;
		this.name = name;
		this.selected = false;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
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
}
