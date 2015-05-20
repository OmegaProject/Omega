package edu.umassmed.omega.trackingMeasuresPlugin.gui;

import edu.umassmed.omega.commons.constants.OmegaConstantsMathSymbols;

public class TMConstants {

	public static final String GRAPH_TYPE = "Select Graph Type:";
	public static final String GRAPH_DRAW = "Draw";
	public static final String GRAPH_VAL_RANGE = "Max Value or Range";
	public static final String GRAPH_VAL_RANGE_TT = "Select a Maximum Value or a Range Interval for Y axis";

	public static final String GRAPH_TYPE_LINE = "Line";
	public static final String GRAPH_TYPE_BAR = "Bar";
	public static final String GRAPH_TYPE_HIST = "Frequency Distribution";
	public static final String GRAPH_TYPE_BOXP = "Box Plot";
	public static final String GRAPH_TYPE_SWARMP = "Swarm Plot";

	public static final String TAB_TRACK_BROWSER = "Track Browser";
	public static final String TAB_INTENSITY = "Intensity";
	public static final String TAB_MOBILITY = "Mobility";
	public static final String TAB_VELOCITY = "Velocity";
	public static final String TAB_DIFFUSIVITY = "Diffusivity";
	public static final String TAB_MTCLASS = "Motion Type Classification";

	// MOTION TYPE CLASSIFICATION
	public static final String GRAPH_MTC_NAME_TRACK = "Track";
	public static final String GRAPH_MTC_LAB_TRACK_X = "X ["
	        + OmegaConstantsMathSymbols.MU + "m]";
	public static final String GRAPH_MTC_LAB_TRACK_Y = "Y ["
	        + OmegaConstantsMathSymbols.MU + "m]";
	public static final String GRAPH_MTC_NAME_MSD = "MSD Plot";
	public static final String GRAPH_MTC_LAB_MSD_X = "Log(deltaT) [log s]";
	public static final String GRAPH_MTC_LAB_MSD_Y = "Log(MSD) [log pixel^2 or"
	        + OmegaConstantsMathSymbols.MU + "m^2]";
	public static final String GRAPH_MTC_NAME_MSS = "MSS Plot";
	public static final String GRAPH_MTC_LAB_MSS_X = "Order moment";
	public static final String GRAPH_MTC_LAB_MSS_Y = "Gamma [a.u.]";
	public static final String GRAPH_MTC_NAME_SMSS_D = "Phase Space";
	public static final String GRAPH_MTC_LAB_SMSS_D_X = "D2 ["
	        + OmegaConstantsMathSymbols.MU + "m^2/s]";
	public static final String GRAPH_MTC_LAB_SMSS_D_Y = "Slope MSS [a.u]";

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

	// VELOCITY
	public static final String GRAPH_NAME_SPEED_LOCAL = "Instantaneous Speed";
	public static final String GRAPH_LAB_Y_SPEED = "Speed [pixel or "
	        + OmegaConstantsMathSymbols.MU + "m /s]";
	public static final String GRAPH_NAME_VEL_LOCAL = "Instantaneous Velocity";
	public static final String GRAPH_LAB_Y_VEL = "Velocity [pixel or "
	        + OmegaConstantsMathSymbols.MU + "m /s]";
	public static final String GRAPH_NAME_SPEED = "Average Curvilinear Speed";
	public static final String GRAPH_NAME_VEL = "Average Straight-Line Velocity";

	// INTENSITY
	// GLOBAL
	public static final String GRAPH_NAME_MAX = "Max ";
	public static final String GRAPH_NAME_MIN = "Min ";
	public static final String GRAPH_NAME_AVG = "Average ";
	public static final String GRAPH_NAME_GBG = "Particle Background";
	public static final String GRAPH_NAME_GSNR = "Particle SNR";
	public static final String MAX_PEAK_INTENSITY = TMConstants.GRAPH_NAME_MAX
	        + " " + TMConstants.GRAPH_NAME_INT_PEAK;
	public static final String AVG_PEAK_INTENSITY = TMConstants.GRAPH_NAME_AVG
	        + " " + TMConstants.GRAPH_NAME_INT_PEAK;
	public static final String MIN_PEAK_INTENSITY = TMConstants.GRAPH_NAME_MIN
	        + " " + TMConstants.GRAPH_NAME_INT_PEAK;
	public static final String MAX_MEAN_INTENSITY = TMConstants.GRAPH_NAME_MAX
	        + " " + TMConstants.GRAPH_NAME_INT_MEAN;
	public static final String AVG_MEAN_INTENSITY = TMConstants.GRAPH_NAME_AVG
	        + " " + TMConstants.GRAPH_NAME_INT_MEAN;
	public static final String MIN_MEAN_INTENSITY = TMConstants.GRAPH_NAME_MIN
	        + " " + TMConstants.GRAPH_NAME_INT_MEAN;
	public static final String MAX_BACKGROUND = TMConstants.GRAPH_NAME_MAX
	        + " " + TMConstants.GRAPH_NAME_GBG;
	public static final String AVG_BACKGROUND = TMConstants.GRAPH_NAME_AVG
	        + " " + TMConstants.GRAPH_NAME_GBG;
	public static final String MIN_BACKGROUND = TMConstants.GRAPH_NAME_MIN
	        + " " + TMConstants.GRAPH_NAME_GBG;
	public static final String MAX_SNR = TMConstants.GRAPH_NAME_MAX + " "
	        + TMConstants.GRAPH_NAME_GSNR;
	public static final String AVG_SNR = TMConstants.GRAPH_NAME_AVG + " "
	        + TMConstants.GRAPH_NAME_GSNR;
	public static final String MIN_SNR = TMConstants.GRAPH_NAME_MIN + " "
	        + TMConstants.GRAPH_NAME_GSNR;
	// LOCAL
	public static final String GRAPH_NAME_INT_PEAK = "Peak Intensity";
	public static final String GRAPH_NAME_INT_CENT = "Centroid Intensity";
	public static final String GRAPH_NAME_INT_MEAN = "Mean Intensity";
	public static final String GRAPH_NAME_INT_BG = "Background";
	public static final String GRAPH_NAME_INT_SNR = "SNR";
	public static final String GRAPH_NAME_PROB = "Identification Probability";
	public static final String GRAPH_NAME_RADIUS = "Radius";
	public static final String GRAPH_NAME_AREA = "Area";
	public static final String GRAPH_LAB_Y_INT = "Flourescence Intensity [a.u.]";

	// DIFFUSIVITY
	// GLOBAL
	public static final String GRAPH_NAME_UNCERT_D = "D2 Uncertainty";
	public static final String GRAPH_LAB_Y_UNCERT_D = "D2 Uncertainty [a.u.]";
	public static final String GRAPH_NAME_UNCERT_SMSS = "Slope MSS Uncertainty";
	public static final String GRAPH_LAB_Y_UNCERT_SMSS = "Slope MSS Uncertainty [a.u.]";
	public static final String GRAPH_NAME_DIFF = "Diffusion Coefficient";
	public static final String GRAPH_LAB_Y_DIFF = "D2 ["
	        + OmegaConstantsMathSymbols.MU + "m^2/s]";
	public static final String GRAPH_NAME_MSD = "Slope of log-log MSD Plot";
	public static final String GRAPH_LAB_Y_MSD = "Slope of log(MSD) / log(deltaT) [a.u.]";
	public static final String GRAPH_NAME_MSS = "Slope MSS";
	public static final String GRAPH_LAB_Y_MSS = "Gamma [a.u.]";

	// GENERAL
	public static final String GRAPH_LAB_Y_FREQ = "Frequency";
	public static final String GRAPH_LAB_X_TRACK = "Track";
	public static final String GRAPH_LAB_X_TPT = "Timepoint or Time";
	public static final String GRAPH_LAB_X_TIME = "Timepoint[a.u] or Time [s]";
}
