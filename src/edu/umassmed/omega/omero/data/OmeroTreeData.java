package edu.umassmed.omega.omero.data;

import java.util.ArrayList;
import java.util.List;

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
