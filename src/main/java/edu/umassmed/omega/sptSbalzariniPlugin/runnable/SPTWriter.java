/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.sptSbalzariniPlugin.runnable;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaConstantsError;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;

public class SPTWriter implements SPTRunnable {
	private static final String RUNNER = "Writer service: ";
	private final OmegaMessageDisplayerPanelInterface displayerPanel;
	private boolean isJobCompleted, isKilled;
	private Object trackList;

	public SPTWriter(final OmegaMessageDisplayerPanelInterface displayerPanel) {
		this.displayerPanel = displayerPanel;
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
		this.updateStatusSync(SPTWriter.RUNNER + " started.", false);
		if (this.isKilled)
			return;
		try {
			this.trackList = SPTDLLInvoker.callWriteResults();
		} catch (final Exception ex) {
			JOptionPane.showMessageDialog(null,
					OmegaConstantsError.ERROR_SPT_SAVE_RESULTS,
					OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
			OmegaLogFileManager.handleUncaughtException(ex, true);
		}

		try {
			SPTDLLInvoker.callDisposeRunner();
		} catch (final Exception ex) {
			OmegaLogFileManager.handleUncaughtException(ex, true);
		}

		this.updateStatusAsync(SPTWriter.RUNNER + " ended.", true);
		this.isJobCompleted = true;
	}

	private void updateStatusSync(final String msg, final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					SPTWriter.this.displayerPanel
					.updateMessageStatus(new SPTMessageEvent(msg,
							SPTWriter.this, ended));
				}
			});
		} catch (final InvocationTargetException | InterruptedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex, true);
		}
	}

	private void updateStatusAsync(final String msg, final boolean ended) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SPTWriter.this.displayerPanel
				.updateMessageStatus(new SPTMessageEvent(msg,
						SPTWriter.this, ended));
			}
		});
	}

	public void kill() {
		this.isKilled = true;
	}
}