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
package edu.umassmed.omega.sdSbalzariniPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

public class SDConstants {
	
	public static final String PREVIEW_BUTTON = "Show Preview";
	public static final String EXECUTE_BUTTON = "Execute Queue";
	
	public static final String PLUGIN_NAME = "MOSAIC 2D Particle Detector";
	public static final String PLUGIN_SNAME = "MOSAIC 2D PD";
	public static final String PLUGIN_AUTHOR_FIRSTNAME = "I. F.";
	public static final String PLUGIN_AUTHOR_LASTNAME = "Sbalzarini";
	public static final String PLUGIN_DESC = "This plugin allows to run the Mosaic (http://mosaic.mpi-cbg.de/?q=downloads/imageJ) particle detection algorithm. The plugin allows to launch multiple sequential runs on either the same and different images. This facilitates parameter optimization and trouble shooting by allowing the user to easily associate the parameters used for each run with the analysis results. This version implements the ImageJ plugin version of the algorithm.";
	public static final String PLUGIN_ALGO_DESC = "This is a particle detection tool that detects bright spots in images and estimates the coordinates of their center. It simply is the particle detection part of the Mosaic particle tracker, without the linking.  It handles time-series and video data and can be handy in an image-processing pipeline or macro where only detection is needed. Alternatively, it can be compared with other spot detection algorithms or combined with different linkers to optimize a tracking workflow.";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2005, 6, 2)
			.getTime();
	public static final String PLUGIN_REFERENCE = "I. F. Sbalzarini and P. Koumoutsakos. Feature Point Tracking and Trajectory Analysis for Video Imaging in Cell Biology, Journal of Structural Biology 151(2):182-195, 2005.";
}
