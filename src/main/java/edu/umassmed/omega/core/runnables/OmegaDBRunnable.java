package main.java.edu.umassmed.omega.core.runnables;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import main.java.edu.umassmed.omega.commons.OmegaLogFileManager;
import main.java.edu.umassmed.omega.commons.gui.dialogs.GenericMessageDialog;
import main.java.edu.umassmed.omega.core.OmegaApplication;
import main.java.edu.umassmed.omega.core.OmegaMySqlGateway;

public abstract class OmegaDBRunnable implements Runnable {

	private final OmegaApplication omegaApp;
	private final OmegaMySqlGateway gateway;
	private final GenericMessageDialog dialog;

	public OmegaDBRunnable(final OmegaApplication omegaApp,
	        final OmegaMySqlGateway gateway, final GenericMessageDialog dialog) {
		this.omegaApp = omegaApp;
		this.gateway = gateway;
		this.dialog = dialog;
	}

	protected void updateMessage(final String s) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					OmegaDBRunnable.this.dialog.updateMessage(s);
				}
			});
		} catch (final InvocationTargetException | InterruptedException ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO should I do something here?
		}
	}

	protected OmegaMySqlGateway getGateway() {
		return this.gateway;
	}

	protected void notifyProcessEndToApplication() {
		this.omegaApp.handleRunnableProcessTermination(this);
	}
}
