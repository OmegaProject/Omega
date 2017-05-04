package edu.umassmed.omega.trackingMeasuresMobilityPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

public class TMMConstants {
	
	// TODO TO FIX
	public static final String PLUGIN_NAME = "OMEGA Mobility Measures";
	public static final String PLUGIN_SNAME = "OMEGA MM";
	public static final String PLUGIN_DESC = "This is the default OMEGA plugin to estimate and plot mobility measures associated with identified trajectories. It utilizes the Trajectory Manager graphical user interface to facilitate the visualization and selection of individual trajectories to be subjected to analysis.";
	public static final String PLUGIN_ALGO_DESC = "This algorithm implements calculation of intensity measures as described by Meijering et al. (in: E. Meijering, O. Dzyubachyk, and I. Smal.  2012. Methods for cell and particle tracking. Meth. Enzymol. 504:183–200. doi:10.1016/B978-0-12-391857-4.00009-4). In addition, it provides an easy to use graphical user interface to plot such measures in function of time and or trajectory number.";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2016, 12, 1)
			.getTime();
	public static final String PLUGIN_REFERENCE = "";
	
}
