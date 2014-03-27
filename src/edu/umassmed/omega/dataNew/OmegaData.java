package edu.umassmed.omega.dataNew;

import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenterGroup;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;

public class OmegaData {

	private List<OmegaProject> projects;
	private List<OmegaExperimenter> experimenters;
	private List<OmegaExperimenterGroup> groups;

	public void addProject(final OmegaProject project) {
		this.projects.add(project);
	}

	public void addExperimenter(final OmegaExperimenter experimenter) {
		this.experimenters.add(experimenter);
	}

	public void addExperimenterGroup(final OmegaExperimenterGroup group) {
		this.groups.add(group);
	}

	public void mergeData(final OmegaData omegaDataToMerge) {
		// TODO rivedere questo punto in modo da evitare di caricare roba gia
		// caricata
		// TODO capire se necessario o meno
		for (final OmegaProject project : omegaDataToMerge.projects) {
			this.addProjectIfNotLoaded(project);
		}
		for (final OmegaExperimenter experimenter : omegaDataToMerge.experimenters) {
			this.addExperimenterIfNotLoaded(experimenter);
		}
		for (final OmegaExperimenterGroup group : omegaDataToMerge.groups) {
			this.addExperimenterGroupIfNotLoaded(group);
		}
	}

	public boolean containsExperimenterGroup(final long id) {
		for (final OmegaExperimenterGroup group : this.groups) {
			if (group.getElementID() == id)
				return true;
		}
		return false;
	}

	public OmegaExperimenterGroup getExperimenterGroup(final long id) {
		for (final OmegaExperimenterGroup group : this.groups) {
			if (group.getElementID() == id)
				return group;
		}
		return null;
	}

	public boolean containsExperimenter(final long id) {
		for (final OmegaExperimenter experimenter : this.experimenters) {
			if (experimenter.getElementID() == id)
				return true;
		}
		return false;
	}

	public OmegaExperimenter getExperimenter(final long id) {
		for (final OmegaExperimenter experimenter : this.experimenters) {
			if (experimenter.getElementID() == id)
				return experimenter;
		}
		return null;
	}

	public boolean containsProject(final long id) {
		for (final OmegaProject project : this.projects) {
			if (project.getElementID() == id)
				return true;
		}
		return false;
	}

	public OmegaProject getProject(final long id) {
		for (final OmegaProject project : this.projects) {
			if (project.getElementID() == id)
				return project;
		}
		return null;
	}

	public boolean containsDataset(final long id) {
		for (final OmegaProject project : this.projects) {
			for (final OmegaDataset dataset : project.getDatasets())
				if (dataset.getElementID() == id)
					return true;
		}
		return false;
	}

	public OmegaDataset getDataset(final long id) {
		for (final OmegaProject project : this.projects) {
			for (final OmegaDataset dataset : project.getDatasets())
				if (dataset.getElementID() == id)
					return dataset;
		}
		return null;
	}

	public boolean containsImage(final long id) {
		for (final OmegaProject project : this.projects) {
			for (final OmegaDataset dataset : project.getDatasets()) {
				for (final OmegaImage image : dataset.getImages()) {
					if (image.getElementID() == id)
						return true;
				}
			}
		}
		return false;
	}

	public OmegaImage getImage(final long id) {
		for (final OmegaProject project : this.projects) {
			for (final OmegaDataset dataset : project.getDatasets()) {
				for (final OmegaImage image : dataset.getImages()) {
					if (image.getElementID() == id)
						return image;
				}
			}
		}
		return null;
	}

	private void addProjectIfNotLoaded(final OmegaProject project) {
		boolean toAdd = true;
		for (final OmegaProject proj : this.projects) {
			if (proj.getElementID() == project.getElementID()) {
				toAdd = false;
				break;
			}
		}
		if (toAdd) {
			this.projects.add(project);
		}
	}

	private void addExperimenterIfNotLoaded(final OmegaExperimenter experimenter) {
		boolean toAdd = true;
		for (final OmegaExperimenter exper : this.experimenters) {
			if (exper.getElementID() == experimenter.getElementID()) {
				toAdd = false;
				break;
			}
		}
		if (toAdd) {
			this.experimenters.add(experimenter);
		}
	}

	private void addExperimenterGroupIfNotLoaded(
	        final OmegaExperimenterGroup group) {
		boolean toAdd = true;
		for (final OmegaExperimenter gr : this.experimenters) {
			if (gr.getElementID() == group.getElementID()) {
				toAdd = false;
				break;
			}
		}
		if (toAdd) {
			this.groups.add(group);
		}
	}
}
