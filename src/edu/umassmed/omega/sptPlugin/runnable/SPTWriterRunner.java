package edu.umassmed.omega.sptPlugin.runnable;

import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.galliva.gallibrary.GLogManager;

import edu.umassmed.omega.commons.OmegaConstants;

public class SPTWriterRunner implements SPTRunnable {

	private boolean isJobCompleted;
	private Object trackList;

	public SPTWriterRunner() {
		this.isJobCompleted = false;
		this.trackList = null;
	}

	public Object getTrackList() {
		return this.trackList;
	}

	@Override
	public boolean isJobCompleted() {
		return this.isJobCompleted;
	}

	@Override
	public void run() {
		try {
			this.trackList = SPTDLLInvoker.callWriteResults();
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(null,
			        OmegaConstants.ERROR_SPT_SAVE_RESULTS,
			        OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
			GLogManager.log(
			        String.format("%s: %s", "Error writing the results",
			                e.toString()), Level.SEVERE);
		}

		try {
			SPTDLLInvoker.callDisposeRunner();
		} catch (final Exception e) {
			GLogManager.log(
			        String.format("%s: %s", "Error disposing the runner",
			                e.toString()), Level.SEVERE);
		}
		this.isJobCompleted = true;
	}
}