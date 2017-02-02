package edu.umassmed.omega.trackingMeasuresIntensityPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;

public class TMIConstants {

	public static final String PARAMETER_ERROR_SNR = OmegaConstants.PARAMETER_ERROR_SNR;
	public static final String PARAMETER_ERROR_SNR_USE = "Enable SNR estimation";
	
	public static final String SELECT_IMAGE = OmegaGUIConstants.SELECT_IMAGE;
	public static final String SELECT_TRACKS_SPOT = OmegaGUIConstants.SELECT_TRACKS_SPOT;
	public static final String SELECT_TRACKS_LINKING = OmegaGUIConstants.SELECT_TRACKS_LINKING;
	public static final String SELECT_TRACKS_ADJ = OmegaGUIConstants.SELECT_TRACKS_ADJ;
	public static final String SELECT_TRACKS_SEGM = OmegaGUIConstants.SELECT_TRACKS_SEGM;
	public static final String SELECT_TRACK_MEASURES = OmegaGUIConstants.SELECT_TRACK_MEASURES;

	// INTENSITY
	// GLOBAL
	public static final String GRAPH_NAME_MAX = "Max ";
	public static final String GRAPH_NAME_MIN = "Min ";
	public static final String GRAPH_NAME_AVG = "Average ";
	// public static final String GRAPH_NAME_GBG = "Particle Background";
	// public static final String GRAPH_NAME_GSNR = "Particle SNR";

	public static final String MAX_PEAK_INTENSITY = TMIConstants.GRAPH_NAME_MAX
			+ " " + TMIConstants.GRAPH_NAME_INT_PEAK;
	public static final String AVG_PEAK_INTENSITY = TMIConstants.GRAPH_NAME_AVG
			+ " " + TMIConstants.GRAPH_NAME_INT_PEAK;
	public static final String MIN_PEAK_INTENSITY = TMIConstants.GRAPH_NAME_MIN
			+ " " + TMIConstants.GRAPH_NAME_INT_PEAK;
	public static final String MAX_CENTROID_INTENSITY = TMIConstants.GRAPH_NAME_MAX
			+ " " + TMIConstants.GRAPH_NAME_INT_CENT;
	public static final String AVG_CENTROID_INTENSITY = TMIConstants.GRAPH_NAME_AVG
			+ " " + TMIConstants.GRAPH_NAME_INT_CENT;
	public static final String MIN_CENTROID_INTENSITY = TMIConstants.GRAPH_NAME_MIN
			+ " " + TMIConstants.GRAPH_NAME_INT_CENT;
	public static final String MAX_MEAN_INTENSITY = TMIConstants.GRAPH_NAME_MAX
			+ " " + TMIConstants.GRAPH_NAME_INT_MEAN;
	public static final String AVG_MEAN_INTENSITY = TMIConstants.GRAPH_NAME_AVG
			+ " " + TMIConstants.GRAPH_NAME_INT_MEAN;
	public static final String MIN_MEAN_INTENSITY = TMIConstants.GRAPH_NAME_MIN
			+ " " + TMIConstants.GRAPH_NAME_INT_MEAN;
	public static final String MAX_NOISE = TMIConstants.GRAPH_NAME_MAX + " "
	        + TMIConstants.GRAPH_NAME_NOISE;
	public static final String AVG_NOISE = TMIConstants.GRAPH_NAME_AVG + " "
	        + TMIConstants.GRAPH_NAME_NOISE;
	public static final String MIN_NOISE = TMIConstants.GRAPH_NAME_MIN + " "
	        + TMIConstants.GRAPH_NAME_NOISE;
	public static final String MAX_AREA = TMIConstants.GRAPH_NAME_MAX + " "
	        + TMIConstants.GRAPH_NAME_AREA;
	public static final String AVG_AREA = TMIConstants.GRAPH_NAME_AVG + " "
	        + TMIConstants.GRAPH_NAME_AREA;
	public static final String MIN_AREA = TMIConstants.GRAPH_NAME_MIN + " "
	        + TMIConstants.GRAPH_NAME_AREA;
	public static final String MAX_SNR = TMIConstants.GRAPH_NAME_MAX + " "
	        + TMIConstants.GRAPH_NAME_SNR;
	public static final String AVG_SNR = TMIConstants.GRAPH_NAME_AVG + " "
	        + TMIConstants.GRAPH_NAME_SNR;
	public static final String MIN_SNR = TMIConstants.GRAPH_NAME_MIN + " "
	        + TMIConstants.GRAPH_NAME_SNR;
	// public static final String MAX_BACKGROUND = TMIConstants.GRAPH_NAME_MAX
	// + " " + TMIConstants.GRAPH_NAME_GBG;
	// public static final String AVG_BACKGROUND = TMIConstants.GRAPH_NAME_AVG
	// + " " + TMIConstants.GRAPH_NAME_GBG;
	// public static final String MIN_BACKGROUND = TMIConstants.GRAPH_NAME_MIN
	// + " " + TMIConstants.GRAPH_NAME_GBG;
	// public static final String MAX_SNR = TMIConstants.GRAPH_NAME_MAX + " "
	// + TMIConstants.GRAPH_NAME_GSNR;
	// public static final String AVG_SNR = TMIConstants.GRAPH_NAME_AVG + " "
	// + TMIConstants.GRAPH_NAME_GSNR;
	// public static final String MIN_SNR = TMIConstants.GRAPH_NAME_MIN + " "
	// + TMIConstants.GRAPH_NAME_GSNR;
	// LOCAL
	public static final String GRAPH_NAME_INT_PEAK = "Peak Intensity";
	public static final String GRAPH_NAME_INT_CENT = "Centroid Intensity";
	public static final String GRAPH_NAME_INT_MEAN = "Mean Intensity";
	public static final String GRAPH_NAME_AREA = "Area";
	public static final String GRAPH_NAME_NOISE = "Noise";
	public static final String GRAPH_NAME_SNR = "SNR";
	public static final String GRAPH_NAME_PROB = "Identification Probability";
	public static final String GRAPH_NAME_RADIUS = "Radius";
	public static final String GRAPH_LAB_Y_INT = "Flourescence Intensity [a.u.]";

	// TODO TO FIX
	public static final String PLUGIN_NAME = "OMEGA Trajectory Editor";
	public static final String PLUGIN_SNAME = "OMEGA TE";
	public static final String PLUGIN_DESC = "This is the default Trajectory Editor plugin provided by OMEGA. It utilizes the Trajectory Browser graphical user interface to facilitate the visualization and selection of individual trajectories in the context of the image of origin, their inspection and the editing of individual links when necessary.";
	public static final String PLUGIN_ALGO_DESC = "This is a simple manual trajectory re-linking algorithm designed to edit individual particle-particle links in cases in which linking errors are introduced by the Single Particle Tracking algorithm. Such errors are common and typically result from particle blinking, temporary focal plane shifts or the crossing of trajectories with temporary particle fusion.";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2014, 12, 1)
	.getTime();

}
