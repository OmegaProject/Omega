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
package edu.umassmed.omega.dataNew.analysisRunElements;

import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;

public class OmegaParameter extends OmegaNamedElement {

	private final Object value;

	public OmegaParameter(final String name, final String clazz,
	        final String value) {
		this(name, OmegaParameter.getObjectValue(value, clazz));
	}

	public OmegaParameter(final String name, final Object value) {
		super(-1, name);

		this.value = value;
	}

	public Object getValue() {
		return this.value;
	}

	public String getClazz() {
		return this.value.getClass().getName();
	}

	public static Object getObjectValue(final String value, final String clazz) {
		if (clazz.equals(Integer.class.getName()))
			return Integer.valueOf(value);
		else if (clazz.equals(Double.class.getName()))
			return Double.valueOf(value);
		else if (clazz.equals(String.class.getName()))
			return value;
		else
			return value.toString();
	}

	public String getStringValue() {
		if (this.value instanceof Integer)
			return Integer.toString((int) this.value);
		else if (this.value instanceof Double)
			return Double.toString((double) this.value);
		else if (this.value instanceof String)
			return (String) this.value;
		else
			return this.value.toString();
	}

}
