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
package edu.umassmed.omega.sptSbalzariniPlugin.tools;

import edu.umassmed.omega.data.coreElements.OmegaImage;

public class SPTExecutionInfoHandler {

	// private OmeroParametersHandler omeroParameters = null;
	private OmegaImage image = null;
	private String radius = "";
	private String cutOff = "";
	private String percentile = "";
	private String displacement = "";
	private String linkRange = "";

	public SPTExecutionInfoHandler(final OmegaImage image, final String radius,
	        final String cutOff, final String percentile,
	        final String displacement, final String linkRange) {
		this.image = image;
		this.radius = radius;
		this.cutOff = cutOff;
		this.percentile = percentile;
		this.displacement = displacement;
		this.linkRange = linkRange;
	}

	public OmegaImage getImage() {
		return this.image;
	}

	public void setImage(final OmegaImage image) {
		this.image = image;
	}

	public String getRadius() {
		return this.radius;
	}

	public void setRadius(final String radius) {
		this.radius = radius;
	}

	public String getCutOff() {
		return this.cutOff;
	}

	public void setCutOff(final String cutOff) {
		this.cutOff = cutOff;
	}

	public String getPercentile() {
		return this.percentile;
	}

	public void setPercentile(final String percentile) {
		this.percentile = percentile;
	}

	public String getDisplacement() {
		return this.displacement;
	}

	public void setDisplacement(final String displacement) {
		this.displacement = displacement;
	}

	public String getLinkRange() {
		return this.linkRange;
	}

	public void setLinkRange(final String linkRange) {
		this.linkRange = linkRange;
	}
}
