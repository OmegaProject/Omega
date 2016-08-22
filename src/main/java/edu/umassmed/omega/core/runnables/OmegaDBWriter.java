package edu.umassmed.omega.core.runnables;

import edu.umassmed.omega.commons.gui.dialogs.GenericProgressDialog;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.core.mysql.OmegaMySqlWriter;

public abstract class OmegaDBWriter extends OmegaDBRunnable {

	private boolean error;

	public OmegaDBWriter(final OmegaApplication omegaApp,
			final OmegaMySqlWriter writer, final GenericProgressDialog dialog) {
		super(omegaApp, writer, dialog);
		this.error = false;
	}

	protected void setErrorOccured() {
		this.error = true;
	}

	public boolean isErrorOccured() {
		return this.error;
	}
}
