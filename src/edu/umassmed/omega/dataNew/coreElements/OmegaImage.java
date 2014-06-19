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
package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaImage extends OmegaNamedElement implements
        OmegaAnalysisRunContainer {

	private final List<OmegaDataset> datasets;

	private final List<OmegaImagePixels> pixelsList;

	private final OmegaExperimenter experimenter;

	private final List<OmegaAnalysisRun> analysisRuns;

	// where
	// sizeX and sizeY = micron per pixel on axis
	// sizeZ = depth
	// sizeC = channels
	// sizeT = seconds per frames

	public OmegaImage(final Long elementID, final String name,
	        final OmegaExperimenter experimenter) {
		super(elementID, name);

		this.datasets = new ArrayList<OmegaDataset>();

		this.experimenter = experimenter;

		this.pixelsList = new ArrayList<OmegaImagePixels>();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImage(final Long elementID, final String name,
	        final OmegaExperimenter experimenter,
	        final List<OmegaImagePixels> pixelsList) {
		super(elementID, name);

		this.datasets = new ArrayList<OmegaDataset>();

		this.experimenter = experimenter;

		this.pixelsList = pixelsList;

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public void addParentDataset(final OmegaDataset dataset) {
		this.datasets.add(dataset);
	}

	public List<OmegaDataset> getParentDatasets() {
		return this.datasets;
	}

	public OmegaExperimenter getExperimenter() {
		return this.experimenter;
	}

	public List<OmegaImagePixels> getPixels() {
		return this.pixelsList;
	}

	public OmegaImagePixels getDefaultPixels() {
		final OmegaImagePixels defaultPixels = this.getPixels(0);
		if (defaultPixels == null) {
			// TODO throw error
		}
		return this.getPixels(0);
	}

	public OmegaImagePixels getPixels(final int index) {
		return this.pixelsList.get(index);
	}

	public boolean containsPixels(final Long id) {
		for (final OmegaImagePixels pixels : this.pixelsList) {
			if (pixels.getElementID() == id)
				return true;
		}
		return false;
	}

	public void addPixels(final OmegaImagePixels pixels) {
		this.pixelsList.add(pixels);
	}

	@Override
	public List<OmegaAnalysisRun> getAnalysisRuns() {
		return this.analysisRuns;
	}

	@Override
	public void addAnalysisRun(final OmegaAnalysisRun analysisRun) {
		this.analysisRuns.add(analysisRun);
	}
}
