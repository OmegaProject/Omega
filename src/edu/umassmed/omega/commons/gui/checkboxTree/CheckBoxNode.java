package edu.umassmed.omega.commons.gui.checkboxTree;

public class CheckBoxNode {
	private final String label;
	private final CheckBoxStatus status;

	public CheckBoxNode(final String label) {
		this.label = label;
		this.status = CheckBoxStatus.INDETERMINATE;
	}

	public CheckBoxNode(final String label, final CheckBoxStatus status) {
		this.label = label;
		this.status = status;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public String getLabel() {
		return this.label;
	}

	public String getStatusString() {
		return this.status.name();
	}

	public CheckBoxStatus getStatus() {
		return this.status;
	}
}