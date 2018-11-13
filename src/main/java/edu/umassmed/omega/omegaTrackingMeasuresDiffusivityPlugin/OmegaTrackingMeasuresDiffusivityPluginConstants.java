package edu.umassmed.omega.omegaTrackingMeasuresDiffusivityPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

import edu.umassmed.omega.commons.constants.OmegaMathSymbolConstants;

public class OmegaTrackingMeasuresDiffusivityPluginConstants {
	
	// TODO TO FIX
	public static final String PLUGIN_NAME = "OMEGA Diffusivity Measures";
	public static final String PLUGIN_SNAME = "OMEGA DM";
	public static final String PLUGIN_VERSION = "1.0";
	public static final String PLUGIN_DESC = "This is the default OMEGA plugin to perform diffusivity analysis on identified trajectories. It utilizes the Trajectory Browser graphical user interface to facilitate the visualization and selection of individual trajectories to be subjected to analysis. Results can then be used to classify trajectories on the basis of their motion type characteristics.";
	public static final String PLUGIN_ALGO_DESC = "This algorithm implements diffusivity measurements as described by Ewers et al. (in: Ewers, H., A.E. Smith, I.F. Sbalzarini, H. Lilie, P. Koumoutsakos, and A. Helenius. 2005. Single-particle tracking of murine polyoma virus-like particles on live cells and artificial membranes. Proc Natl Acad Sci USA. 102:15110–15115. doi:10.1073/pnas.0504407102). In addition, it provides an easy to use graphical user interface to plot such measures for each individual trajectory. Finally it utilizes a scatter plot to summarize the data and display the observed diffusion constant (ODC2) and the slope of the moment scaling spectrum (SMSS) of all trajectories in a dataset in a single plot. Individual trajectories are represented as single points in this ODC2 vs. SMSS phase space, allowing to globally evaluate the behavior of 'clouds' of trajectories and compare the effect of different experimental conditions on their shapes.";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2016, 12, 1)
			.getTime();
	public static final String PLUGIN_REFERENCE = "";
	
	public static final String LOCAL_RESULTS_TABNAME = "Moments of Displacement vs. "
			+ OmegaMathSymbolConstants.DELTA + "t plots results";
	public static final String GLOBAL_INTERVAL_RESULTS_TABNAME = "Moment Scaling Spectrum results";
	public static final String GLOBAL_RESULTS_TABNAME = "Phase space results";
	
}
