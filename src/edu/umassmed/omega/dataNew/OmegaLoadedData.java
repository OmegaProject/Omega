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

import edu.umassmed.omega.commons.exceptions.OmegaCoreLoadedElementNotFound;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;

public class OmegaLoadedData {

	private final List<OmegaProject> projects;
	private final List<OmegaDataset> datasets;
	private final List<OmegaImage> images;
	private final List<OmegaImagePixels> pixels;
	private final List<OmegaFrame> frames;

	public OmegaLoadedData() {
		this.projects = new ArrayList<OmegaProject>();
		this.datasets = new ArrayList<OmegaDataset>();
		this.images = new ArrayList<OmegaImage>();
		this.pixels = new ArrayList<OmegaImagePixels>();
		this.frames = new ArrayList<OmegaFrame>();
	}

	public int getLoadedDataSize() {
		int sum = 0;
		sum += this.projects.size();
		sum += this.datasets.size();
		sum += this.images.size();
		sum += this.pixels.size();
		sum += this.frames.size();
		return sum;
	}

	public int getElementIndex(final OmegaElement element)
	        throws OmegaCoreLoadedElementNotFound {
		int index = 1;
		if (element instanceof OmegaProject) {
			index += this.projects.indexOf(element);
			return index;
		}
		index += this.projects.size();
		if (element instanceof OmegaDataset) {
			index += this.datasets.indexOf(element);
			return index;
		}
		index += this.datasets.size();
		if (element instanceof OmegaImage) {
			index += this.images.indexOf(element);
			return index;
		}
		index += this.images.size();
		if (element instanceof OmegaImagePixels) {
			index += this.pixels.indexOf(element);
			return index;
		}
		index += this.pixels.size();
		if (element instanceof OmegaFrame) {
			index += this.frames.indexOf(element);
			return index;
		}
		throw new OmegaCoreLoadedElementNotFound("Element not found");
	}

	public OmegaElement getElement(final int index)
	        throws OmegaCoreLoadedElementNotFound {
		int newIndex = index - 1;
		if ((this.projects.size() > 0) && (newIndex < this.projects.size()))
			return this.projects.get(newIndex);
		newIndex -= this.projects.size();
		if ((this.datasets.size() > 0) && (newIndex < this.datasets.size()))
			return this.datasets.get(newIndex);
		newIndex -= this.datasets.size();
		if ((this.images.size() > 0) && (newIndex < this.images.size()))
			return this.images.get(newIndex);
		newIndex -= this.images.size();
		if ((this.pixels.size() > 0) && (newIndex < this.pixels.size()))
			return this.pixels.get(newIndex);
		newIndex -= this.pixels.size();
		if ((this.frames.size() > 0) && (newIndex < this.frames.size()))
			return this.frames.get(newIndex);

		throw new OmegaCoreLoadedElementNotFound("Index: " + index + " Total: "
		        + this.getLoadedDataSize());
	}

	public List<OmegaProject> getProjects() {
		return this.projects;
	}

	public void addProject(final OmegaProject project) {
		this.projects.add(project);
	}

	public void removeProject(final OmegaProject project) {
		this.projects.remove(project);
	}

	public boolean containsProject(final OmegaProject project) {
		return this.projects.contains(project);
	}

	public List<OmegaDataset> getDatasets() {
		return this.datasets;
	}

	public void addDataset(final OmegaDataset dataset) {
		this.datasets.add(dataset);
	}

	public void removeDataset(final OmegaDataset dataset) {
		this.datasets.remove(dataset);
	}

	public boolean containsDataset(final OmegaDataset dataset) {
		return this.datasets.contains(dataset);
	}

	public List<OmegaImage> getImages() {
		return this.images;
	}

	public void addImage(final OmegaImage image) {
		this.images.add(image);
	}

	public void removeImage(final OmegaImage image) {
		this.images.remove(image);
	}

	public boolean containsImage(final OmegaImage image) {
		return this.images.contains(image);
	}

	public List<OmegaImagePixels> getImagePixels() {
		return this.pixels;
	}

	public void addImagePixels(final OmegaImagePixels pixels) {
		this.pixels.add(pixels);
	}

	public void removeImagePixels(final OmegaImagePixels pixels) {
		this.pixels.remove(pixels);
	}

	public boolean containsImagePixels(final OmegaImagePixels pixels) {
		return this.pixels.contains(pixels);
	}

	public List<OmegaFrame> getFrames() {
		return this.frames;
	}

	public void addFrame(final OmegaFrame frame) {
		this.frames.add(frame);
	}

	public void removeFrame(final OmegaFrame frame) {
		this.frames.remove(frame);
	}

	public boolean containsFrame(final OmegaFrame frame) {
		return this.frames.contains(frame);
	}

	public void addElement(final OmegaElement element) {
		if (element instanceof OmegaProject) {
			this.addProject((OmegaProject) element);
		} else if (element instanceof OmegaDataset) {
			this.addDataset((OmegaDataset) element);
		} else if (element instanceof OmegaImage) {
			this.addImage((OmegaImage) element);
		} else if (element instanceof OmegaImagePixels) {
			this.addImagePixels((OmegaImagePixels) element);
		} else if (element instanceof OmegaFrame) {
			this.addFrame((OmegaFrame) element);
		} else {
			// TODO THROW EXCEPTION
		}
	}

	public void removeElement(final OmegaElement element) {
		if (element instanceof OmegaProject) {
			this.removeProject((OmegaProject) element);
		} else if (element instanceof OmegaDataset) {
			this.removeDataset((OmegaDataset) element);
		} else if (element instanceof OmegaImage) {
			this.removeImage((OmegaImage) element);
		} else if (element instanceof OmegaImagePixels) {
			this.removeImagePixels((OmegaImagePixels) element);
		} else if (element instanceof OmegaFrame) {
			this.removeFrame((OmegaFrame) element);
		} else {
			// TODO THROW EXCEPTION
		}
	}
}
