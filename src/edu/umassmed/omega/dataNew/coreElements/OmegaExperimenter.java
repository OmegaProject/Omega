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
package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

public class OmegaExperimenter extends OmegaPerson {

	private final List<OmegaExperimenterGroup> groups;

	private final Long omeroId;

	public OmegaExperimenter(final long omeroId, final String firstName,
	        final String lastName) {
		super(firstName, lastName);
		this.omeroId = omeroId;
		this.groups = new ArrayList<OmegaExperimenterGroup>();
	}

	public OmegaExperimenter(final long omegaId, final String firstName,
	        final String lastName, final List<OmegaExperimenterGroup> groups) {
		super(firstName, lastName);
		this.omeroId = omegaId;
		this.groups = groups;
	}

	public List<OmegaExperimenterGroup> getGroups() {
		return this.groups;
	}

	public void addGroup(final OmegaExperimenterGroup group) {
		this.groups.add(group);
	}

	public boolean containsGroup(final long id) {
		for (final OmegaExperimenterGroup group : this.groups) {
			if (group.getElementID() == id)
				return true;
		}
		return false;
	}

	public OmegaExperimenterGroup getGroup(final long id) {
		for (final OmegaExperimenterGroup group : this.groups) {
			if (group.getElementID() == id)
				return group;
		}
		return null;
	}

	public Long getOmeroId() {
		return this.omeroId;
	}
}
