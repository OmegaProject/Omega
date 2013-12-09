package edu.umassmed.omega.commons;

import java.awt.Dimension;

public class OmegaConstants {

	/**
	 * Build and info
	 */
	public static String OMEGA_TITLE = "OMEGA public alpha release";
	public static String OMEGA_BUILD = "build 20121220";
	public static String OMEGA_WEBSITE = "http://www.supsi.ch";
	public static String OMEGA_AUTHOR = "Supsi";
	public static String OMEGA_DESCRIPTION = "<html>Open Microscopy Environment inteGrated Analysis</html>";

	/**
	 * ERRORS
	 */
	public static String ERROR_PORT_IS_NUMBER = "Port must be a number!";
	public static String ERROR_CANNOT_CONNECT_TO_OMERO = "cannot connect to server";
	public static String ERROR_LOADING_THE_DS = "Unable to load the Dataset!";
	public static String ERROR_UNABLE_TO_DISPLAY_IMAGE = "Unable to display the image!";
	public static String ERROR_SAVE_IMAGE = "Unable to save the image!";

	public static String ERROR_C_Z_MUST_BE_NUMBERS = "C and Z must be numbers!";

	public static String ERROR_SPT_MAX_VALUE = "This value must be a number!";
	public static String ERROR_INIT_SPT_RUN = "Error during the initialization of the SPT algorithm!";
	public static String ERROR_DURING_SPT_RUN = "Error during the run of the SPT algorithm!";
	public static String ERROR_SPT_SAVE_RESULTS = "Error saving the SPT results!";

	public static String ERROR_NOTRAJECTORIES = "Unable to load any trajectory!";
	public static String ERROR_NO_SPT_INFORMATION = "Unable to load the image's information coming from the SPT module!";
	public static String ERROR_NOPATTERNS = "Unable to load any pattern!";
	public static String ERROR_LOADING_SEGMENTATION = "Error during the segmentation loading!";
	public static String ERROR_DRAWING = "Unable to draw the Trajectory!";
	public static String ERROR_SAVE_LABELS = "Unable to save the trajectories labels!";
	public static String ERROR_SAVE_CSV = "Unable to save the CSV file!";

	public static String ERROR_TS_NOT_ENOUGH_POINTS = "Each trajectory must have at least 100 points to be processed!";

	public static String ERROR_NODLL = "No Omega DLL found or DLL error: ";

	public static String ERROR_OPENBIS_CONNECTION_FAIL = "Unable to connect to openBIS, please check your settings!";
	public static String ERROR_OPENBIS_UPLOAD = "Unable to upload the dataset to openBIS!";
	public static String ERROR_OPENBIS_DOWNLOAD = "Unable to download the dataset from openBIS!";
	public static String ERROR_OPENBIS_LISTDATASETS = "Unable to load the dataset's list from openBIS!";

	public static String ERROR_STATISTICAL_CALCULATION = "Something went wrong during the statistical calculation.\nStats not available.";

	public static String ERROR_INTERPOLATION_CALCULATION = "Something went wrong during the bilinear interpolation.";
	public static String ERROR_INTERPOLATION_CALCULATION_SNR = "The SNR is out of range. Impossible to interpolate.";
	public static String ERROR_INTERPOLATION_CALCULATION_L = "The length is out of range. Impossible to interpolate.";

	/**
	 * LOGS
	 */
	public static String LOG_TRAIN_CALLED = "DLL train method called";
	public static String LOG_SEGMENT_CALLED = "DLL segment method called";
	public static String LOG_SET_INI_FAILED = "Cannot set the INI file";

	public static final int THUMBNAIL_SIZE = 100;
	public static final Dimension BUTTON_SIZE = new Dimension(120, 20);
	public static final Dimension TEXT_SIZE = new Dimension(200, 20);

}
