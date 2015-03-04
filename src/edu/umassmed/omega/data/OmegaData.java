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
package edu.umassmed.omega.data;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaTrajectoriesSegmentationRun;
import edu.umassmed.omega.data.coreElements.OmegaDataset;
import edu.umassmed.omega.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.data.coreElements.OmegaExperimenterGroup;
import edu.umassmed.omega.data.coreElements.OmegaFrame;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.coreElements.OmegaProject;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationTypes;

public class OmegaData {

	private final List<OmegaProject> projects;
	private final List<OmegaExperimenter> experimenters;
	private final List<OmegaExperimenterGroup> groups;
	private final List<OmegaSegmentationTypes> segmTypesList;

	public OmegaData() {
		this.projects = new ArrayList<OmegaProject>();
		this.experimenters = new ArrayList<OmegaExperimenter>();
		this.groups = new ArrayList<OmegaExperimenterGroup>();
		this.segmTypesList = new ArrayList<OmegaSegmentationTypes>();
		this.segmTypesList.add(OmegaSegmentationTypes
		        .getDefaultSegmentationTypes());
	}

	private void checkSegmentationTypesListConsistency() {
		final List<OmegaSegmentationTypes> toRemove = new ArrayList<OmegaSegmentationTypes>();
		for (final OmegaSegmentationTypes segmTypes : this.segmTypesList) {
			if (toRemove.contains(segmTypes)) {
				continue;
			}
			for (final OmegaSegmentationTypes segmTypes2 : this.segmTypesList) {
				if (toRemove.contains(segmTypes2)) {
					continue;
				}
				if (segmTypes.getName().equals(segmTypes2.getName()))
					if (segmTypes.equals(segmTypes2)) {
						continue;
					}
				if (segmTypes.getElementID() == -1) {
					toRemove.add(segmTypes);
				} else if (segmTypes2.getElementID() == -1) {
					toRemove.add(segmTypes2);
				} else {
					System.out.println("Omega Data segm types error");
				}
			}
		}
		this.segmTypesList.removeAll(toRemove);
		OmegaSegmentationTypes defaultSegmTypes = null;
		for (final OmegaSegmentationTypes segmTypes : this.segmTypesList) {
			if (segmTypes.getName().equals(OmegaSegmentationTypes.DEFAULT_NAME)) {
				defaultSegmTypes = segmTypes;
			}
		}
		this.segmTypesList.remove(defaultSegmTypes);
		this.segmTypesList.add(0, defaultSegmTypes);
	}

	public void updateSegmentationTypes() {
		for (final OmegaProject proj : this.projects) {
			for (final OmegaDataset dataset : proj.getDatasets()) {
				for (final OmegaImage img : dataset.getImages()) {
					for (final OmegaAnalysisRun analysisRun : img
					        .getAnalysisRuns()) {
						this.checkAnalysisRunForSegmentationTypes(analysisRun);
					}
				}
			}
		}
		this.checkSegmentationTypesListConsistency();
	}

	private void checkAnalysisRunForSegmentationTypes(
	        final OmegaAnalysisRun analysisRun) {
		if (!(analysisRun instanceof OmegaTrajectoriesSegmentationRun))
			return;
		final OmegaTrajectoriesSegmentationRun tmRun = (OmegaTrajectoriesSegmentationRun) analysisRun;
		this.segmTypesList.add(tmRun.getSegmentationTypes());
		for (final OmegaAnalysisRun innerAnalysisRun : analysisRun
		        .getAnalysisRuns()) {
			this.checkAnalysisRunForSegmentationTypes(innerAnalysisRun);
		}
	}

	public List<OmegaSegmentationTypes> getSegmentationTypesList() {
		return this.segmTypesList;
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
						for (int c = 0; c < pixels.getSizeC(); c++) {
							for (int z = 0; z < pixels.getSizeZ(); z++) {
								pixels.containsFrame(c, z, id);
							}
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
						for (int c = 0; c < pixels.getSizeC(); c++) {
							for (int z = 0; z < pixels.getSizeZ(); z++) {
								pixels.getFrame(c, z, id);
							}
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
