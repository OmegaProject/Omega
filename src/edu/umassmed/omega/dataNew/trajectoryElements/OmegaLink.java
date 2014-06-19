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
package edu.umassmed.omega.dataNew.trajectoryElements;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaLink extends OmegaElement {

	private final double speed, angle;
	private final OmegaROI startingROI, endingROI;

	public OmegaLink(final Long elementID, final OmegaROI startingROI,
	        final OmegaROI endingROI) {
		super(elementID);

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.speed = -1;
		this.angle = -1;
	}

	public OmegaLink(final Long elementID, final OmegaROI startingROI,
	        final OmegaROI endingROI, final double speed, final double angle) {
		super(elementID);

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.speed = speed;
		this.angle = angle;
	}

	public OmegaROI getStartingROI() {
		return this.startingROI;
	}

	public OmegaROI getEndingROI() {
		return this.endingROI;
	}

	public double getSpeed() {
		return this.speed;
	}

	public double getAngle() {
		return this.angle;
	}
}
