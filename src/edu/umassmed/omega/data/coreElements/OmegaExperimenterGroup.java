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
package edu.umassmed.omega.data.coreElements;

import java.util.ArrayList;
import java.util.List;

public class OmegaExperimenterGroup extends OmegaElement {

	private final List<OmegaExperimenter> leaders;

	private List<OmegaExperimenter> associates;

	public OmegaExperimenterGroup(final Long elementID,
	        final List<OmegaExperimenter> leaders) {
		super(elementID);

		this.leaders = leaders;

		this.associates = new ArrayList<OmegaExperimenter>();
	}

	public OmegaExperimenterGroup(final Long elementID,
	        final List<OmegaExperimenter> leaders,
	        final List<OmegaExperimenter> associates) {
		this(elementID, leaders);

		this.associates = associates;
	}

	public List<OmegaExperimenter> getLeaders() {
		return this.leaders;
	}

	public boolean containsLeader(final long id) {
		for (final OmegaExperimenter leader : this.leaders) {
			if (leader.getElementID() == id)
				return true;
		}
		return false;
	}

	public void addLeader(final OmegaExperimenter leader) {
		this.leaders.add(leader);
	}

	public List<OmegaExperimenter> getAssociates() {
		return this.associates;
	}

	public void addAssociate(final OmegaExperimenter associate) {
		this.associates.add(associate);
	}
}
