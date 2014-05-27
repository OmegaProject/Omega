package edu.umassmed.omega.sptPlugin.runnable;

import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.galliva.gallibrary.GLogManager;

import edu.umassmed.omega.commons.OmegaConstants;

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

	public static void callSetOutputPath(final String path) {
		try {
			new SPTDLLInvoker().setOutputPath(path);
		} catch (final UnsatisfiedLinkError e) {
			GLogManager.log(OmegaConstants.ERROR_NODLL + e.toString(),
			        Level.SEVERE);
		}
	}

	public static void callInitRunner() {
		try {
			new SPTDLLInvoker().initRunner();
		} catch (final UnsatisfiedLinkError e) {
			GLogManager.log(OmegaConstants.ERROR_NODLL + e.toString(),
			        Level.SEVERE);
		}
	}

	public static void callSetParameter(final String pNumber,
	        final String pValue) {
		try {
			new SPTDLLInvoker().setParameter(pNumber, pValue);
		} catch (final UnsatisfiedLinkError e) {
			GLogManager.log(OmegaConstants.ERROR_NODLL + e.toString(),
			        Level.SEVERE);
		}
	}

	public static void callSetMinPoints(final int minPoints) {
		try {
			new SPTDLLInvoker().setMinPoints(minPoints);
		} catch (final UnsatisfiedLinkError e) {
			GLogManager.log(OmegaConstants.ERROR_NODLL + e.toString(),
			        Level.SEVERE);
		}
	}

	public static void callStartRunner() {
		try {
			new SPTDLLInvoker().startRunner();
		} catch (final UnsatisfiedLinkError e) {
			GLogManager.log(OmegaConstants.ERROR_NODLL + e.toString(),
			        Level.SEVERE);
		}
	}

	public static void callLoadImage(final int[] imageData) {
		try {
			new SPTDLLInvoker().loadImage(imageData);
		} catch (final UnsatisfiedLinkError e) {
			GLogManager.log(OmegaConstants.ERROR_NODLL + e.toString(),
			        Level.SEVERE);
		}
	}

	public static Object callWriteResults() {
		Object obj = null;
		try {
			obj = new SPTDLLInvoker().writeResults();
		} catch (final UnsatisfiedLinkError e) {
			GLogManager.log(OmegaConstants.ERROR_NODLL + e.toString(),
			        Level.SEVERE);
		}
		return obj;
	}

	public static void callDisposeRunner() {
		try {
			new SPTDLLInvoker().disposeRunner();
		} catch (final UnsatisfiedLinkError e) {
			GLogManager.log(OmegaConstants.ERROR_NODLL + e.toString(),
			        Level.SEVERE);
		}
	}

	/**
	 * Load the SPT DLL.
	 */
	static {
		try {
			// OmegaClassLoaderUtility
			// .addLibraryPath(OmegaConstants.OMEGA_SPT_FOLDER);
			System.loadLibrary(OmegaConstants.OMEGA_SPT_DLL);
		} catch (final UnsatisfiedLinkError e) {
			GLogManager.log(OmegaConstants.ERROR_NODLL + e.toString(),
			        Level.SEVERE);
			JOptionPane.showMessageDialog(null,
			        OmegaConstants.ERROR_NODLL + e.toString(),
			        OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (final SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
