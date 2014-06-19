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
package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;

public class OmegaGatewayEvent extends OmegaPluginEvent {
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_DESTROYED = 2;
	public static final int STATUS_CONNECTED = 3;
	public static final int STATUS_DISCONNECTED = 4;

	private final int status;

	private final OmegaExperimenter experimenter;

	public OmegaGatewayEvent(final OmegaPlugin source, final int status) {
		this(source, status, null);
	}

	public OmegaGatewayEvent(final OmegaPlugin source, final int status,
	        final OmegaExperimenter experimenter) {
		super(source);
		this.status = status;

		this.experimenter = experimenter;
	}

	public int getStatus() {
		return this.status;
	}

	public OmegaExperimenter getExperimenter() {
		return this.experimenter;
	}
}
