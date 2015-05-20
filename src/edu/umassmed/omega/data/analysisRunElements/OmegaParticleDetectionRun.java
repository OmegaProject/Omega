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
package edu.umassmed.omega.data.analysisRunElements;

import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.data.coreElements.OmegaFrame;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;

public class OmegaParticleDetectionRun extends OmegaAnalysisRun {

	private final Map<OmegaFrame, List<OmegaROI>> resultingParticles;
	private final Map<OmegaROI, Map<String, Object>> resultingParticlesValues;

	public OmegaParticleDetectionRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Map<OmegaFrame, List<OmegaROI>> resultingParticles,
			final Map<OmegaROI, Map<String, Object>> resultingParticlesValues) {
		super(owner, algorithmSpec);

		this.resultingParticles = resultingParticles;
		this.resultingParticlesValues = resultingParticlesValues;
	}

	public OmegaParticleDetectionRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Date timeStamps, final String name,
	        final Map<OmegaFrame, List<OmegaROI>> resultingParticles,
			final Map<OmegaROI, Map<String, Object>> resultingParticlesValues) {
		super(owner, algorithmSpec, timeStamps, name);

		this.resultingParticles = resultingParticles;
		this.resultingParticlesValues = resultingParticlesValues;
	}

	public Map<OmegaFrame, List<OmegaROI>> getResultingParticles() {
		return this.resultingParticles;
	}

	public Map<OmegaROI, Map<String, Object>> getResultingParticlesValues() {
		return this.resultingParticlesValues;
	}
}
