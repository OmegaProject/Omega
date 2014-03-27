package edu.umassmed.omega.omeroPlugin.data;

import java.util.ArrayList;
import java.util.List;

import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.ProjectData;

public class OmeroExperimenterWrapper {

	private final ExperimenterData exp;
	private final List<OmeroProjectWrapper> projects;

	public OmeroExperimenterWrapper(final ExperimenterData exp) {
		this.exp = exp;
		this.projects = new ArrayList<OmeroProjectWrapper>();
	}

	public void setProjects(final List<ProjectData> projects) {
		for (final ProjectData proj : projects) {
			final OmeroProjectWrapper omeProj = new OmeroProjectWrapper(proj);
			this.projects.add(omeProj);
		}
	}

	public void setDatasets(final ProjectData proj,
	        final List<DatasetData> datasets) {
		for (final OmeroProjectWrapper omeProj : this.projects) {
			if (omeProj.getID() == proj.getId()) {
				omeProj.setDatasets(datasets);
			}
		}
	}

	public List<OmeroProjectWrapper> getProjects() {
		return this.projects;
	}

	@Override
	public String toString() {
		return this.exp.getFirstName() + " " + this.exp.getLastName();
	}

	public long getID() {
		return this.exp.getId();
	}
}
