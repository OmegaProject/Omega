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

import javax.swing.JOptionPane;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.core.OmegaLogFileManager;

public class SPTDLLInvoker {
	// native methods
	private native void setOutputPath(String path);

	private native void initRunner();

	private native void setParameter(String pNum, String pValue);

	private native void setMinPoints(int num);

	private native void startRunner();

	private native void loadImage(int[] imageBytes);

	private native Object writeResults();

	private native void disposeRunner();

	public static void callSetOutputPath(final String path)
	        throws UnsatisfiedLinkError {
		new SPTDLLInvoker().setOutputPath(path);
	}

	public static void callInitRunner() throws UnsatisfiedLinkError {
		new SPTDLLInvoker().initRunner();
	}

	public static void callSetParameter(final String pNumber,
	        final String pValue) throws UnsatisfiedLinkError {
		new SPTDLLInvoker().setParameter(pNumber, pValue);
	}

	public static void callSetMinPoints(final int minPoints)
	        throws UnsatisfiedLinkError {
		new SPTDLLInvoker().setMinPoints(minPoints);

	}

	public static void callStartRunner() throws UnsatisfiedLinkError {
		new SPTDLLInvoker().startRunner();
	}

	public static void callLoadImage(final int[] imageData)
	        throws UnsatisfiedLinkError {
		new SPTDLLInvoker().loadImage(imageData);
	}

	public static Object callWriteResults() throws UnsatisfiedLinkError {
		return new SPTDLLInvoker().writeResults();
	}

	public static void callDisposeRunner() throws UnsatisfiedLinkError {
		new SPTDLLInvoker().disposeRunner();
	}

	/**
	 * Load the SPT DLL.
	 */
	static {
		try {
			// OmegaClassLoaderUtilities
			// .addLibraryPath(OmegaConstants.OMEGA_SPT_FOLDER);
			System.loadLibrary(OmegaConstants.OMEGA_SPT_DLL);
		} catch (final UnsatisfiedLinkError ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
			JOptionPane.showMessageDialog(null,
			        OmegaConstants.ERROR_NODLL + ex.toString(),
			        OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
		} catch (final SecurityException | IllegalArgumentException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}
}
