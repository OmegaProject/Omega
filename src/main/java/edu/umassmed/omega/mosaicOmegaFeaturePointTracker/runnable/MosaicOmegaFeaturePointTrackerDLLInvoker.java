/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School Alessandro
 * Rigano (Program in Molecular Medicine) Caterina Strambio De Castillia
 * (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts: Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.mosaicOmegaFeaturePointTracker.runnable;

import java.io.File;

import javax.swing.JOptionPane;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.constants.OmegaErrorConstants;
import edu.umassmed.omega.commons.constants.OmegaGenericConstants;
import edu.umassmed.omega.commons.utilities.OmegaClassLoaderUtilities;
import edu.umassmed.omega.mosaicOmegaFeaturePointTracker.MosaicOmegaFeaturePointTrackerPluginConstants;

public class MosaicOmegaFeaturePointTrackerDLLInvoker {
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
		new MosaicOmegaFeaturePointTrackerDLLInvoker().setOutputPath(path);
	}
	
	public static void callInitRunner() throws UnsatisfiedLinkError {
		new MosaicOmegaFeaturePointTrackerDLLInvoker().initRunner();
	}
	
	public static void callSetParameter(final String pNumber,
			final String pValue) throws UnsatisfiedLinkError {
		new MosaicOmegaFeaturePointTrackerDLLInvoker().setParameter(pNumber,
				pValue);
	}
	
	public static void callSetMinPoints(final int minPoints)
			throws UnsatisfiedLinkError {
		new MosaicOmegaFeaturePointTrackerDLLInvoker().setMinPoints(minPoints);
		
	}
	
	public static void callStartRunner() throws UnsatisfiedLinkError {
		new MosaicOmegaFeaturePointTrackerDLLInvoker().startRunner();
	}
	
	public static void callLoadImage(final int[] imageData)
			throws UnsatisfiedLinkError {
		new MosaicOmegaFeaturePointTrackerDLLInvoker().loadImage(imageData);
	}
	
	public static Object callWriteResults() throws UnsatisfiedLinkError {
		return new MosaicOmegaFeaturePointTrackerDLLInvoker().writeResults();
	}
	
	public static void callDisposeRunner() throws UnsatisfiedLinkError {
		new MosaicOmegaFeaturePointTrackerDLLInvoker().disposeRunner();
	}
	
	/**
	 * Load the SPT DLL.
	 */
	static {
		// TODO try to use OmegaClassLoader methods
		// loadLibrary to load unpack dll libraries on the fly when needed
		// and deleting them at the end of execution
		// in this way they can be packed in the jar by maven
		try {
			final String dir = System.getProperty("user.dir")
					+ File.separator
					+ MosaicOmegaFeaturePointTrackerPluginConstants.OMEGA_SPT_FOLDER
					+ File.separator;
			// final String dir2 = System.getProperty("user.dir") +
			// File.separator
			// + "target" + File.separator
			// + OmegaGenericConstants.OMEGA_SPT_FOLDER + File.separator;
			// final String dir = System.getProperty("user.dir") +
			// File.separator;
			// OmegaClassLoaderUtilities.addLibraryPath(dir);
			// final File f = new File("pthreadVC2" + ".dll");
			// String dir;
			// if (f.exists()) {
			// dir = dir1;
			// } else {
			// dir = dir2;
			// }
			// System.out.println(dir);
			OmegaClassLoaderUtilities.addLibraryPath(dir);
			System.load(dir + "pthreadVC2" + ".dll");
			System.load(dir + "ParticleTracker_Statistics_Dll_VC2008-Release"
					+ ".dll");
			System.load(dir
					+ MosaicOmegaFeaturePointTrackerPluginConstants.OMEGA_SPT_DLL
					+ ".dll");
			// System.load(OmegaGenericConstants.OMEGA_SPT_FOLDER +
			// "//pthreadVC2"
			// + ".dll");
			// System.load(OmegaGenericConstants.OMEGA_SPT_FOLDER
			// + "//ParticleTracker_Statistics_Dll_VC2008-Release"
			// + ".dll");
			// System.load(OmegaGenericConstants.OMEGA_SPT_FOLDER + "//"
			// + OmegaGenericConstants.OMEGA_SPT_DLL + ".dll");
		} catch (final UnsatisfiedLinkError ex) {
			OmegaLogFileManager.handleUncaughtException(ex, true);
			JOptionPane.showMessageDialog(null, OmegaErrorConstants.ERROR_NODLL
					+ ex.toString(), OmegaGenericConstants.OMEGA_TITLE,
					JOptionPane.ERROR_MESSAGE);
		} catch (final SecurityException | IllegalArgumentException ex) {
			OmegaLogFileManager.handleUncaughtException(ex, true);
		}
	}
}
