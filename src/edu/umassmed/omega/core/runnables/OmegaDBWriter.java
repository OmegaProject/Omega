package edu.umassmed.omega.core.runnables;

import edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.core.OmegaMySqlGateway;

public abstract class OmegaDBWriter extends OmegaDBRunnable {

	private boolean error;

	public OmegaDBWriter(final OmegaApplication omegaApp,
	        final OmegaMySqlGateway gateway, final GenericMessageDialog dialog) {
		super(omegaApp, gateway, dialog);
		this.error = false;
	}

	protected void setErrorOccured() {
		this.error = true;
	}

	public boolean isErrorOccured() {
		return this.error;
	}
}
