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
package unused;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.omeroPlugin.data.OmeroExperimenterWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroProjectWrapper;

import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.ProjectData;

public class OmeroTreeData {

	private final List<OmeroExperimenterWrapper> exps;

	public OmeroTreeData(final List<ExperimenterData> data) {
		this.exps = new ArrayList<OmeroExperimenterWrapper>();
	}

	public List<OmeroExperimenterWrapper> getExperimenters() {
		return this.exps;
	}

	public void setExperimenters(final List<ExperimenterData> exps) {
		for (final ExperimenterData exp : exps) {
			final OmeroExperimenterWrapper omeExp = new OmeroExperimenterWrapper(
			        exp);
			this.exps.add(omeExp);
		}
	}

	public void addExperimenter(final ExperimenterData exp) {
		final OmeroExperimenterWrapper omeExp = new OmeroExperimenterWrapper(
		        exp);
		this.exps.add(omeExp);
	}

	public void removeExperimenter(final ExperimenterData exp) {
		OmeroExperimenterWrapper omeExpToRemove = null;
		for (final OmeroExperimenterWrapper omeExp : this.exps) {
			if (omeExp.getID() == exp.getId()) {
				omeExpToRemove = omeExp;
			}
		}
		this.exps.remove(omeExpToRemove);
	}

	public void setProjects(final ExperimenterData exp,
	        final List<ProjectData> projects) {
		for (final OmeroExperimenterWrapper omeExp : this.exps) {
			if (omeExp.getID() == exp.getId()) {
				omeExp.setProjects(projects);
			}
		}
	}

	public void setDatasets(final ExperimenterData exp, final ProjectData proj,
	        final List<DatasetData> datasets) {
		for (final OmeroExperimenterWrapper omeExp : this.exps) {
			if (omeExp.getID() == exp.getId()) {
				for (final OmeroProjectWrapper omeProj : omeExp.getProjects()) {
					if (omeProj.getID() == proj.getId()) {
						omeProj.setDatasets(datasets);
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Omero server";
	}
}
