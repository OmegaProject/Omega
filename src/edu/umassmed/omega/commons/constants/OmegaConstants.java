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

	// ***BUILD AND INFO
	public final static String OMEGA_TITLE = "OMEGA pre beta release";
	public final static String OMEGA_BUILD = "build 0.02";
	public final static String OMEGA_WEBSITE = "http://omega.umassmed.edu/";
	public final static String OMEGA_AUTHOR = "UMass Medical School";
	public final static String OMEGA_DESCRIPTION = "<html>Open Microscopy Environment inteGrated Analysis</html>";

	// ***PATHS AND FILENAMES
	public final static String OMEGA_IMGS_FOLDER = "imgs";
	public final static String OMEGA_SPT_FOLDER = "sptPlugin_LibsAndDlls";
	public final static String OMEGA_SPT_DLL = "omega-spt-stats";

	// ***LOGS***
	public final static String LOG_TRAIN_CALLED = "DLL train method called";
	public final static String LOG_SEGMENT_CALLED = "DLL segment method called";
	public final static String LOG_SET_INI_FAILED = "Cannot set the INI file";

	// ***SIZES***
	public final static int THUMBNAIL_SIZE = 100;
	public final static int DRAWING_POINTSIZE = 4;
	public final static Dimension BUTTON_SIZE = new Dimension(120, 20);
	public final static Dimension BUTTON_SIZE_LARGE = new Dimension(180, 20);
	public final static Dimension TEXT_SIZE = new Dimension(200, 20);

	// COMBO BOX
	public final static int COMBOBOX_MAX_OPTIONS = 5;

	// ***DATE FORMATTING***
	public final static String OMEGA_DATE_FORMAT = "yyyy-MM-dd_hh-mm-aa";
	public final static String OMEGA_DATE_FORMAT_LBL = "yyyy";

	// ***RELINKING***
	public final static String OMEGA_RELINKING_CURRENT = "Unsaved relinking";

	// ***SEGMENTATION***
	public final static String OMEGA_SEGMENTATION_CURRENT = "Unsaved segmentation";

	// ***COLORS***
	/**
	 * 
	 * @return
	 */
	public final static Color getDefaultSelectionBackgroundColor() {
		final UIDefaults defaults = javax.swing.UIManager.getDefaults();
		return defaults.getColor("List.selectionBackground");
	}

	/**
	 * 
	 * @return
	 */
	public final static Color getDefaultSelectionForegroundColor() {
		final UIDefaults defaults = javax.swing.UIManager.getDefaults();
		return defaults.getColor("List.selectionForeground");
	}

	public static Color getDefaultBackgroundColor() {
		final UIDefaults defaults = javax.swing.UIManager.getDefaults();
		return defaults.getColor("List.background");
	}
}
