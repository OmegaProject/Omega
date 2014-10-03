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

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaAlgorithmSpecification extends OmegaElement {

	private final OmegaAlgorithmInformation algorithmInfo;

	private List<OmegaParameter> parameters;

	public OmegaAlgorithmSpecification(
	        final OmegaAlgorithmInformation algorithmInfo) {
		super((long) -1);

		this.algorithmInfo = algorithmInfo;

		this.parameters = new ArrayList<OmegaParameter>();
	}

	public OmegaAlgorithmSpecification(
	        final OmegaAlgorithmInformation algorithmInfo,
	        final List<OmegaParameter> parameters) {
		this(algorithmInfo);

		this.parameters = parameters;
	}

	public OmegaAlgorithmInformation getAlgorithmInfo() {
		return this.algorithmInfo;
	}

	public OmegaParameter getParameter(final String name) {
		for (final OmegaParameter param : this.parameters) {
			if (param.getName().equals(name))
				return param;
		}
		return null;
	}

	public List<OmegaParameter> getParameters() {
		return this.parameters;
	}

	public void addParameter(final OmegaParameter parameter) {
		this.parameters.add(parameter);
	}

	public void addParameters(final List<OmegaParameter> parameters) {
		this.parameters.addAll(parameters);
	}
}
