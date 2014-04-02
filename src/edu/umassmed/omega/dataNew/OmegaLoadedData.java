package edu.umassmed.omega.dataNew;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
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

	public List<OmegaProject> getProjects() {
		return this.projects;
	}

	public void addProject(final OmegaProject project) {
		this.projects.add(project);
	}

	public List<OmegaDataset> getDatasets() {
		return this.datasets;
	}

	public void addDataset(final OmegaDataset dataset) {
		this.datasets.add(dataset);
	}

	public List<OmegaImage> getImages() {
		return this.images;
	}

	public void addImage(final OmegaImage image) {
		this.images.add(image);
	}

	public List<OmegaImagePixels> getImagePixels() {
		return this.pixels;
	}

	public void addImagePixels(final OmegaImagePixels pixels) {
		this.pixels.add(pixels);
	}

	public List<OmegaFrame> getFrames() {
		return this.frames;
	}

	public void addFrame(final OmegaFrame frame) {
		this.frames.add(frame);
	}
}
