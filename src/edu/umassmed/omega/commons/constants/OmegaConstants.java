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
package edu.umassmed.omega.commons.constants;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.UIDefaults;

public class OmegaConstants {

	/**
	 * Build and info
	 */
	public final static String OMEGA_TITLE = "OMEGA public alpha release";
	public final static String OMEGA_BUILD = "build 20121220";
	public final static String OMEGA_WEBSITE = "http://www.supsi.ch";
	public final static String OMEGA_AUTHOR = "Supsi";
	public final static String OMEGA_DESCRIPTION = "<html>Open Microscopy Environment inteGrated Analysis</html>";

	public final static String OMEGA_SPT_FOLDER = "sptPlugin_LibsAndDlls";
	public final static String OMEGA_SPT_DLL = "omega-spt-stats";

	/**
	 * ERRORS
	 */
	public final static String ERROR_PORT_IS_NUMBER = "Port must be a number!";
	public final static String ERROR_CANNOT_CONNECT_TO_OMERO = "cannot connect to server";
	public final static String ERROR_LOADING_THE_DS = "Unable to load the Dataset!";
	public final static String ERROR_UNABLE_TO_DISPLAY_IMAGE = "Unable to display the image!";
	public final static String ERROR_SAVE_IMAGE = "Unable to save the image!";

	public final static String ERROR_C_Z_MUST_BE_NUMBERS = "C and Z must be numbers!";

	public final static String ERROR_SPT_MAX_VALUE = "This value must be a number!";
	public final static String ERROR_INIT_SPT_RUN = "Error during the initialization of the SPT algorithm!";
	public final static String ERROR_DURING_SPT_RUN = "Error during the run of the SPT algorithm!";
	public final static String ERROR_SPT_SAVE_RESULTS = "Error saving the SPT results!";

	public final static String ERROR_NOTRAJECTORIES = "Unable to load any trajectory!";
	public final static String ERROR_NO_SPT_INFORMATION = "Unable to load the image's information coming from the SPT module!";
	public final static String ERROR_NOPATTERNS = "Unable to load any pattern!";
	public final static String ERROR_LOADING_SEGMENTATION = "Error during the segmentation loading!";
	public final static String ERROR_DRAWING = "Unable to draw the Trajectory!";
	public final static String ERROR_SAVE_LABELS = "Unable to save the trajectories labels!";
	public final static String ERROR_SAVE_CSV = "Unable to save the CSV file!";

	public final static String ERROR_TS_NOT_ENOUGH_POINTS = "Each trajectory must have at least 100 points to be processed!";

	public final static String ERROR_NODLL = "No Omega DLL found or DLL error: ";

	public final static String ERROR_OPENBIS_CONNECTION_FAIL = "Unable to connect to openBIS, please check your settings!";
	public final static String ERROR_OPENBIS_UPLOAD = "Unable to upload the dataset to openBIS!";
	public final static String ERROR_OPENBIS_DOWNLOAD = "Unable to download the dataset from openBIS!";
	public final static String ERROR_OPENBIS_LISTDATASETS = "Unable to load the dataset's list from openBIS!";

	public final static String ERROR_STATISTICAL_CALCULATION = "Something went wrong during the statistical calculation.\nStats not available.";

	public final static String ERROR_INTERPOLATION_CALCULATION = "Something went wrong during the bilinear interpolation.";
	public final static String ERROR_INTERPOLATION_CALCULATION_SNR = "The SNR is out of range. Impossible to interpolate.";
	public final static String ERROR_INTERPOLATION_CALCULATION_L = "The length is out of range. Impossible to interpolate.";

	/**
	 * LOGS
	 */
	public final static String LOG_TRAIN_CALLED = "DLL train method called";
	public final static String LOG_SEGMENT_CALLED = "DLL segment method called";
	public final static String LOG_SET_INI_FAILED = "Cannot set the INI file";

	public final static int THUMBNAIL_SIZE = 100;
	public final static int DRAWING_POINTSIZE = 4;
	public final static Dimension BUTTON_SIZE = new Dimension(120, 20);
	public final static Dimension TEXT_SIZE = new Dimension(200, 20);

	public final static String OMEGA_DATE_FORMAT = "yyyy-MM-dd_hh-mm-aa";

	public final static Color getDefaultSelectionBackgroundColor() {
		final UIDefaults defaults = javax.swing.UIManager.getDefaults();
		return defaults.getColor("List.selectionBackground");
	}

	public final static Color getDefaultSelectionForegroundColor() {
		final UIDefaults defaults = javax.swing.UIManager.getDefaults();
		return defaults.getColor("List.selectionForeground");
	}
}
