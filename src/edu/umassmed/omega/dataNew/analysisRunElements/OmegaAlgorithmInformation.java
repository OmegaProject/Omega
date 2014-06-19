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

import java.util.Calendar;
import java.util.Date;

import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;

public class OmegaAlgorithmInformation extends OmegaNamedElement {

	private final double version;
	private final String description;
	private final Date publicationDate;
	private OmegaPerson author;

	public OmegaAlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description) {
		super(elementID, name);

		this.author = null;
		this.version = version;
		this.description = description;
		this.publicationDate = Calendar.getInstance().getTime();
	}

	public OmegaAlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description,
	        final OmegaPerson author) {
		this(elementID, name, version, description);

		this.author = author;
	}

	public OmegaAlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description,
	        final OmegaPerson author, final Date publicationDate) {
		super(elementID, name);

		this.version = version;
		this.description = description;

		this.author = author;

		this.publicationDate = publicationDate;
	}

	public String getDescription() {
		return this.description;
	}

	public double getVersion() {
		return this.version;
	}

	public Date getPublicationData() {
		return this.publicationDate;
	}

	public OmegaPerson getAuthor() {
		return this.author;
	}
}
