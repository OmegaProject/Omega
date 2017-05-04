/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School Alessandro
 * Rigano (Program in Molecular Medicine) Caterina Strambio De Castillia
 * (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team:
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli,
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban,
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts: Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.snrSbalzariniPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

public class SNRConstants {
	public static String PARAM_SNR_METHOD = "SNR Model";
	public static String PARAM_SNR_METHOD_SBALZARINI = "Cheezum";
	public static String PARAM_SNR_METHOD_BHATTACHARYYA_POISSON = "Bhattacharyya Poisson";
	public static String PARAM_SNR_METHOD_BHATTACHARYYA_GAUSSIAN = "Bhattacharyya Gaussian";

	public static final String EXECUTE_BUTTON = "Execute queue";

	public static final String PLUGIN_AUTHOR_FIRSTNAME = "I. F.";
	public static final String PLUGIN_AUTHOR_LASTNAME = "Sbalzarini";
	public static final String PLUGIN_SHORTNAME = "MOSAIC SE";
	public static final String PLUGIN_NAME = "MOSAIC SNR Estimation";
	public static final String PLUGIN_DESC = "This plugin control the execution of a simple local Signal to Noise ratio (SNR) estimation routine developed by Ivo Sbalzarini.The plugin allows to launch multiple sequential runs on either the same and different images. This facilitates parameter optimization and trouble shooting by allowing the user to easily associate the parameters used for each run with the analysis results.";
	public static final String PLUGIN_ALGO_DESC = "This algorithm estimates the local signal to noise ratio associated with previously detected individual bright spots. It works by [MISSING INFO] ";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2005, 6, 2)
			.getTime();
	public static final String PLUGIN_REFERENCE = "";

	public static final String TAB_ROI = "ROI results";
	public static final String TAB_PLANE = "Plane results";
	public static final String TAB_IMAGE = "Image results";

}
