package edu.umassmed.omega.trajectoryManagerPlugin.actions;


public class TMAction {

	private boolean hasBeenApplied;

	public TMAction() {

		this.hasBeenApplied = false;
	}

	public void setHasBeenApplied(final boolean hasBeenApplied) {
		this.hasBeenApplied = hasBeenApplied;
	}

	public boolean hasBeenApplied() {
		return this.hasBeenApplied;
	}
}
