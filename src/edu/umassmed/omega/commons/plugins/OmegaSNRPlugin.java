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
package edu.umassmed.omega.commons.plugins;

import java.util.List;

import edu.umassmed.omega.commons.plugins.interfaces.OmegaImageConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoadedAnalysisConsumerPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaLoaderPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectParticleDetectionRunPluginInterface;
import edu.umassmed.omega.commons.plugins.interfaces.OmegaSelectSNRRunPluginInterface;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;

public abstract class OmegaSNRPlugin extends OmegaAlgorithmPlugin implements
        OmegaLoaderPluginInterface, OmegaImageConsumerPluginInterface,
        OmegaLoadedAnalysisConsumerPluginInterface,
        OmegaSelectParticleDetectionRunPluginInterface,
        OmegaSelectSNRRunPluginInterface {

	private OmegaGateway gateway;
	private List<OmegaAnalysisRun> loadedAnalysisRuns;
	private List<OmegaImage> loadedImages;

	public OmegaSNRPlugin() {
		this(1);
	}

	public OmegaSNRPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);

		this.gateway = null;
		this.loadedImages = null;
	}

	@Override
	public OmegaGateway getGateway() {
		return this.gateway;
	}

	@Override
	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public List<OmegaImage> getLoadedImages() {
		return this.loadedImages;
	}

	@Override
	public void setLoadedImages(final List<OmegaImage> images) {
		this.loadedImages = images;
	}

	@Override
	public void setLoadedAnalysisRun(
	        final List<OmegaAnalysisRun> loadedAnalysisRuns) {
		this.loadedAnalysisRuns = loadedAnalysisRuns;
	}

	@Override
	public List<OmegaAnalysisRun> getLoadedAnalysisRuns() {
		return this.loadedAnalysisRuns;
	}
}
