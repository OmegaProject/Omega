package edu.umassmed.omega.trackingMeasuresDiffusivityPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

public class TMDConstants {

	// TODO TO FIX
	public static final String PLUGIN_NAME = "OMEGA Diffusivity Measures";
	public static final String PLUGIN_SNAME = "OMEGA DM";
	public static final String PLUGIN_DESC = "This is the default OMEGA plugin to perform diffusivity analysis on identified trajectories. It utilizes the Trajectory Manager graphical user interface to facilitate the visualization and selection of individual trajectories to be subjected to analysis. Results can then be used to classify trajectories on the basis of their motion type characteristics.";
	public static final String PLUGIN_ALGO_DESC = "This algorithm implements diffusivity measurements as described by Ewers et al. (in: Ewers, H., A.E. Smith, I.F. Sbalzarini, H. Lilie, P. Koumoutsakos, and A. Helenius. 2005. Single-particle tracking of murine polyoma virus-like particles on live cells and artificial membranes. Proc Natl Acad Sci USA. 102:15110–15115. doi:10.1073/pnas.0504407102). In addition, it provides an easy to use graphical user interface to plot such measures for each individual trajectory. Finally it utilizes a scatter plot to summarize the data and display the diffusion coefficient (D2) and the slope of the moment scaling spectrum (SMSS) of all trajectories in a dataset in a single plot. Individual trajectories are represented as single points in this D2 vs. SMSS phase space, allowing to globally evaluate the behavior of 'clouds' of trajectories and compare the effect of different experimental conditions on their shapes.";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2016, 12, 1)
			.getTime();
	public static final String PLUGIN_REFERENCE = "";

}
