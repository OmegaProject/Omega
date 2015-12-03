package main.java.edu.umassmed.omega.trackingMeasuresMobilityPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

import main.java.edu.umassmed.omega.commons.constants.OmegaConstantsMathSymbols;
import main.java.edu.umassmed.omega.commons.constants.OmegaGUIConstants;

public class TMMConstants {

	public static final String SELECT_IMAGE = OmegaGUIConstants.SELECT_IMAGE;
	public static final String SELECT_TRACKS_SPOT = OmegaGUIConstants.SELECT_TRACKS_SPOT;
	public static final String SELECT_TRACKS_LINKING = OmegaGUIConstants.SELECT_TRACKS_LINKING;
	public static final String SELECT_TRACKS_ADJ = OmegaGUIConstants.SELECT_TRACKS_ADJ;
	public static final String SELECT_TRACKS_SEGM = OmegaGUIConstants.SELECT_TRACKS_SEGM;
	public static final String SELECT_TRACK_MEASURES = OmegaGUIConstants.SELECT_TRACK_MEASURES;

	// MOBILITY
	// GLOBAL
	public static final String GRAPH_NAME_MAX_DISP = "Max Displacement";
	public static final String GRAPH_LAB_Y_MAX_DISP = "Delta max [pixel or"
	        + OmegaConstantsMathSymbols.MU + "m]";
	public static final String GRAPH_NAME_TOT_DISP = "Total Net Displacement";
	public static final String GRAPH_LAB_Y_TOT_DISP = "Delta net [pixel or"
	        + OmegaConstantsMathSymbols.MU + "m]";
	public static final String GRAPH_NAME_TOT_DIST = "Total Distance Traveled";
	public static final String GRAPH_LAB_Y_TOT_DIST = "D tot [pixel or"
	        + OmegaConstantsMathSymbols.MU + "m]";
	public static final String GRAPH_NAME_TOT_TIME = "Total Track Time";
	public static final String GRAPH_LAB_Y_TOT_TIME = "T tot [timepoint or s]";
	public static final String GRAPH_NAME_CONFRATIO = "Confinement Ratio";
	public static final String GRAPH_LAB_Y_CONFRATIO = "R con [a.u.]";
	// LOCAL
	public static final String GRAPH_NAME_ANGLES = "Instantaneous Angle";
	public static final String GRAPH_LAB_Y_ANGLES = "Angle alpha [rad]";
	public static final String GRAPH_NAME_ANGLES_LOCAL = "Direction change";
	public static final String GRAPH_LAB_Y_ANGLES_LOCAL = "Angle beta i [rad]";

	// TODO TO FIX
	public static final String PLUGIN_NAME = "OMEGA Trajectory Editor";
	public static final String PLUGIN_SNAME = "OMEGA TE";
	public static final String PLUGIN_DESC = "This is the default Trajectory Editor plugin provided by OMEGA. It utilizes the Trajectory Browser graphical user interface to facilitate the visualization and selection of individual trajectories in the context of the image of origin, their inspection and the editing of individual links when necessary.";
	public static final String PLUGIN_ALGO_DESC = "This is a simple manual trajectory re-linking algorithm designed to edit individual particle-particle links in cases in which linking errors are introduced by the Single Particle Tracking algorithm. Such errors are common and typically result from particle blinking, temporary focal plane shifts or the crossing of trajectories with temporary particle fusion.";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2014, 12, 1)
	.getTime();

}
