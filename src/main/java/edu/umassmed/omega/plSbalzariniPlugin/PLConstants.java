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
package edu.umassmed.omega.plSbalzariniPlugin;

import java.util.Date;
import java.util.GregorianCalendar;

public class PLConstants {
	
	public static final String PARAM_MOVTYPE_BROWNIAN = "Brownian";
	public static final String PARAM_MOVTYPE_STRAIGHT = "Straight lines";
	public static final String PARAM_MOVTYPE_COSVEL = "Costant velocity";
	
	public static final String PARAM_OPTIMIZER_GREEDY = "Greedy";
	public static final String PARAM_OPTIMIZER_HUNGARIAN = "Hungarian";
	
	public static final String EXECUTE_BUTTON = "Execute Queue";
	
	public static final String PLUGIN_NAME = "MOSAIC 2D Particle Linking";
	public static final String PLUGIN_SNAME = "MOSAIC 2D PL";
	public static final String PLUGIN_AUTHOR_FIRSTNAME = "Ivo";
	public static final String PLUGIN_AUTHOR_LASTNAME = "Sbalzarini";
	public static final String PLUGIN_DESC = "To be defined";
	public static final String PLUGIN_ALGO_DESC = "To be defined";
	public static final Date PLUGIN_PUBL = new GregorianCalendar(2005, 6, 2)
			.getTime();
}
