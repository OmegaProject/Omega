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
package edu.umassmed.omega.sptPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.galliva.gallibrary.GLogManager;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;

public class SPTWriter implements SPTRunnable {
	private static final String RUNNER = "Writer service: ";
	private final OmegaMessageDisplayerPanelInterface displayerPanel;
	private boolean isJobCompleted;
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
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
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
}