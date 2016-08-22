package edu.umassmed.omega.core.runnables;

import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.commons.data.coreElements.OmegaDataset;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import edu.umassmed.omega.commons.gui.dialogs.GenericProgressDialog;
import edu.umassmed.omega.core.OmegaApplication;
import edu.umassmed.omega.core.mysql.OmegaMySqlGateway;

public abstract class OmegaDBRunnable implements Runnable {
	
	static public int LOAD_TYPE_LOADED_IMAGE = 1;
	static public int LOAD_TYPE_ORPHANED_IMAGE = 2;

	private final OmegaApplication omegaApp;
	private final OmegaMySqlGateway gateway;
	private final GenericProgressDialog dialog;

	public OmegaDBRunnable(final OmegaApplication omegaApp,
	        final OmegaMySqlGateway gateway, final GenericProgressDialog dialog) {
		this.omegaApp = omegaApp;
		this.gateway = gateway;
		this.dialog = dialog;
	}

	protected void updateMessage(final OmegaElement element, final String s) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					if (element == null) {
						OmegaDBRunnable.this.dialog.updateTotalMessage(s);
					} else if (element instanceof OmegaAnalysisRun) {
						OmegaDBRunnable.this.dialog.updateAnalysisMessage(s);
					} else if (element instanceof OmegaImage) {
						OmegaDBRunnable.this.dialog.updateImageMessage(s);
					} else if (element instanceof OmegaDataset) {
						OmegaDBRunnable.this.dialog.updateDatasetMessage(s);
					} else if (element instanceof OmegaProject) {
						OmegaDBRunnable.this.dialog.updateProjectMessage(s);
					}

				}
			});
		} catch (final Exception ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO should I do something here?
		}
	}

	protected void updateMessage(final int index, final String s) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					if (index == 0) {
						OmegaDBRunnable.this.dialog.updateTotalMessage(s);
					} else if (index == 1) {
						OmegaDBRunnable.this.dialog.updateAnalysisMessage(s);
					} else if (index == 2) {
						OmegaDBRunnable.this.dialog.updateImageMessage(s);
					} else if (index == 3) {
						OmegaDBRunnable.this.dialog.updateDatasetMessage(s);
					} else if (index == 4) {
						OmegaDBRunnable.this.dialog.updateProjectMessage(s);
					}

				}
			});
		} catch (final Exception ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO should I do something here?
		}
	}

	protected void updateCurrentProgress(final OmegaElement element,
	        final int currentProgress) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					if (element == null) {
						OmegaDBRunnable.this.dialog
						        .setTotalProgressCurrent(currentProgress);
					} else if (element instanceof OmegaAnalysisRun) {
						OmegaDBRunnable.this.dialog
						        .setAnalysisProgressCurrent(currentProgress);
					} else if (element instanceof OmegaImage) {
						OmegaDBRunnable.this.dialog
						        .setImageProgressCurrent(currentProgress);
					} else if (element instanceof OmegaDataset) {
						OmegaDBRunnable.this.dialog
						        .setDatasetProgressCurrent(currentProgress);
					} else if (element instanceof OmegaProject) {
						OmegaDBRunnable.this.dialog
						        .setProjectProgressCurrent(currentProgress);
					}

				}
			});
		} catch (final Exception ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO should I do something here?
		}
	}

	protected void updateCurrentProgress(final int index, final int maxProgress) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					if (index == 0) {
						OmegaDBRunnable.this.dialog
						        .setTotalProgressCurrent(maxProgress);
					} else if (index == 1) {
						OmegaDBRunnable.this.dialog
						        .setAnalysisProgressCurrent(maxProgress);
					} else if (index == 2) {
						OmegaDBRunnable.this.dialog
						        .setImageProgressCurrent(maxProgress);
					} else if (index == 3) {
						OmegaDBRunnable.this.dialog
						        .setDatasetProgressCurrent(maxProgress);
					} else if (index == 4) {
						OmegaDBRunnable.this.dialog
						        .setProjectProgressCurrent(maxProgress);
					}
				}
			});
		} catch (final Exception ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO should I do something here?
		}
	}

	protected void updateMaxProgress(final OmegaElement element,
	        final int maxProgress) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					if (element == null) {
						OmegaDBRunnable.this.dialog
						        .setTotalProgressMax(maxProgress);
					} else if (element instanceof OmegaAnalysisRun) {
						OmegaDBRunnable.this.dialog
						        .setAnalysisProgressMax(maxProgress);
					} else if (element instanceof OmegaImage) {
						OmegaDBRunnable.this.dialog
						        .setImageProgressMax(maxProgress);
					} else if (element instanceof OmegaDataset) {
						OmegaDBRunnable.this.dialog
						        .setDatasetProgressMax(maxProgress);
					} else if (element instanceof OmegaProject) {
						OmegaDBRunnable.this.dialog
						        .setProjectProgressMax(maxProgress);
					}
				}
			});
		} catch (final Exception ex) {
			OmegaLogFileManager.handleCoreException(ex);
			// TODO should I do something here?
		}
	}

	protected void updateMaxProgress(final int index, final int maxProgress) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					if (index == 0) {
						OmegaDBRunnable.this.dialog
						        .setTotalProgressMax(maxProgress);
					} else if (index == 1) {
						OmegaDBRunnable.this.dialog
						        .setAnalysisProgressMax(maxProgress);
					} else if (index == 2) {
						OmegaDBRunnable.this.dialog
						        .setImageProgressMax(maxProgress);
					} else if (index == 3) {
						OmegaDBRunnable.this.dialog
						        .setDatasetProgressMax(maxProgress);
					} else if (index == 4) {
						OmegaDBRunnable.this.dialog
						        .setProjectProgressMax(maxProgress);
					}
				}
			});
		} catch (final Exception ex) {
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

	protected void setDialogClosable() {
		this.dialog.enableClose();
	}
}
