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
package edu.umassmed.omega.dataNew;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenterGroup;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;

public class OmegaData {

	private final List<OmegaProject> projects;
	private final List<OmegaExperimenter> experimenters;
	private final List<OmegaExperimenterGroup> groups;

	public OmegaData() {
		this.projects = new ArrayList<OmegaProject>();
		this.experimenters = new ArrayList<OmegaExperimenter>();
		this.groups = new ArrayList<OmegaExperimenterGroup>();
	}

	public List<OmegaProject> getProjects() {
		return this.projects;
	}

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

	public boolean containsPixels(final long id) {
		for (final OmegaProject project : this.projects) {
			for (final OmegaDataset dataset : project.getDatasets()) {
				for (final OmegaImage image : dataset.getImages()) {
					for (final OmegaImagePixels pixels : image.getPixels()) {
						if (pixels.getElementID() == id)
							return true;
					}
				}
			}
		}
		return false;
	}

	public OmegaImagePixels getPixels(final long id) {
		for (final OmegaProject project : this.projects) {
			for (final OmegaDataset dataset : project.getDatasets()) {
				for (final OmegaImage image : dataset.getImages()) {
					for (final OmegaImagePixels pixels : image.getPixels()) {
						if (pixels.getElementID() == id)
							return pixels;
					}
				}
			}
		}
		return null;
	}

	public boolean containsFrame(final long id) {
		for (final OmegaProject project : this.projects) {
			for (final OmegaDataset dataset : project.getDatasets()) {
				for (final OmegaImage image : dataset.getImages()) {
					for (final OmegaImagePixels pixels : image.getPixels()) {
						for (final OmegaFrame frame : pixels.getFrames()) {
							if (frame.getElementID() == id)
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	public OmegaFrame getFrame(final long id) {
		for (final OmegaProject project : this.projects) {
			for (final OmegaDataset dataset : project.getDatasets()) {
				for (final OmegaImage image : dataset.getImages()) {
					for (final OmegaImagePixels pixels : image.getPixels()) {
						for (final OmegaFrame frame : pixels.getFrames()) {
							if (frame.getElementID() == id)
								return frame;
						}
					}
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
