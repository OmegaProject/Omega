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
package edu.umassmed.omega.omeroPlugin.data;

import java.util.ArrayList;
import java.util.List;

import pojos.DatasetData;
import pojos.ProjectData;

public class OmeroProjectWrapper extends OmeroDataWrapper {

	private final ProjectData proj;
	private final List<OmeroDatasetWrapper> datasets;

	public OmeroProjectWrapper(final ProjectData proj) {
		this.proj = proj;
		this.datasets = new ArrayList<OmeroDatasetWrapper>();
	}

	public int getNumOfDatasets() {
		return this.datasets.size();
	}

	public void setDatasets(final List<DatasetData> datasets) {
		for (final DatasetData dataset : datasets) {
			final OmeroDatasetWrapper omeDataset = new OmeroDatasetWrapper(
			        dataset, this.proj);
			this.datasets.add(omeDataset);
		}
	}

	public List<OmeroDatasetWrapper> getDatasets() {
		return this.datasets;
	}

	@Override
	public String getStringRepresentation() {
		return "[" + this.getID() + "] " + this.proj.getName();
	}

	@Override
	public Long getID() {
		return this.proj.getId();
	}

	public List<DatasetData> getDatasetsData() {
		final List<DatasetData> datasets = new ArrayList<DatasetData>();
		for (final OmeroDatasetWrapper omeDataset : this.datasets) {
			datasets.add(omeDataset.getDatasetData());
		}
		return datasets;
	}
}
