/*******************************************************************************
 * C
opyright (C) 2014 University of Massachusetts Medical School
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
package edu.umassmed.omega.sptSbalzariniPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

import edu.umassmed.omega.commons.constants.OmegaConstantsAlgorithmParameters;
import edu.umassmed.omega.commons.constants.OmegaGUIConstants;

public class SPTConstants {
	public static final String PARAM_RADIUS = OmegaConstantsAlgorithmParameters.PARAM_RADIUS;
	public static final String PARAM_CUTOFF = OmegaConstantsAlgorithmParameters.PARAM_CUTOFF;
	public static final String PARAM_PERCENTILE = OmegaConstantsAlgorithmParameters.PARAM_PERCENTILE;

	public static final String PARAM_DISPLACEMENT = OmegaConstantsAlgorithmParameters.PARAM_DISPLACEMENT;
	public static final String PARAM_LINKRANGE = OmegaConstantsAlgorithmParameters.PARAM_LINKRANGE;

	public static final String PARAM_MINPOINTS = OmegaConstantsAlgorithmParameters.PARAM_MINPOINTS;

	public static final String PARAM_CHANNEL = OmegaConstantsAlgorithmParameters.PARAM_CHANNEL;
	public static final String PARAM_ZSECTION = OmegaConstantsAlgorithmParameters.PARAM_ZSECTION;

	public static final String RUN_QUEUE = OmegaGUIConstants.PLUGIN_RUN_QUEUE;
	public static final String LOADED_DATA = OmegaGUIConstants.PLUGIN_LOADED_DATA;
	public static final String RUN_DEFINITION = OmegaGUIConstants.PLUGIN_RUN_DEFINITION;

	public static final String PARAMETER_DETECTION = OmegaGUIConstants.PLUGIN_PARAMETERS_DETECTION;
	public static final String PARAMETER_LINKING = OmegaGUIConstants.PLUGIN_PARAMETERS_LINKING;
	public static final String PARAMETER_ADVANCED = OmegaGUIConstants.PLUGIN_PARAMETERS_GENERAL;

	public static final String EXECUTE_BUTTON = "Execute queue";

	public static final String PLUGIN_NAME = "MOSAIC 2D Particle Tracker";
	public static final String PLUGIN_SNAME = "MOSAIC 2D PT";
	public static final String PLUGIN_AUTHOR_FIRSTNAME = "Ivo";
	public static final String PLUGIN_AUTHOR_LASTNAME = "Sbalzarini";
	public static final String PLUGIN_DESC = "This plugin allows to run a multicore optimized version of the Mosaic 2D Particle Tracker algorithm and it performs both particle detection and linking. The plugin allows to launch multiple sequential runs on either the same and different images. The currently available version consists of a multi-threaded algorithm and it uses Java Native Interface (JNI) to call a Dynamic-link library (DLL) written in C/C++. The main limitation of this configuration is that it can only run under a Windows OS.";
	public static final String PLUGIN_ALGO_DESC = "This is a 2D single particle tracking tool that tracks bright spots over time. The method was designed for applications where large numbers of images of low Signal to Noise Ratio (SNR) need to be analyzed in the absence of prior knowledge about the motion type of the moving objects.";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2005, 6, 2)
	        .getTime();
}
