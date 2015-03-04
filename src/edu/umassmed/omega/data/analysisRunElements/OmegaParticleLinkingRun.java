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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import edu.umassmed.omega.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaParticleLinkingRun extends OmegaAnalysisRun {
	private final List<OmegaTrajectory> resultingTrajectories;

	public OmegaParticleLinkingRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final List<OmegaTrajectory> resultingTrajectory) {
		super(owner, algorithmSpec);

		this.resultingTrajectories = resultingTrajectory;

		this.reorderParticles();
	}

	public OmegaParticleLinkingRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec, final String name,
	        final List<OmegaTrajectory> resultingTrajectories) {
		super(owner, algorithmSpec, name);

		this.resultingTrajectories = resultingTrajectories;

		this.reorderParticles();
	}

	public OmegaParticleLinkingRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Date timeStamps, final String name,
	        final List<OmegaTrajectory> resultingTrajectories) {
		super(owner, algorithmSpec, timeStamps, name);

		this.resultingTrajectories = resultingTrajectories;

		this.reorderParticles();
	}

	private void reorderParticles() {
		for (final OmegaTrajectory traj : this.resultingTrajectories) {
			final List<OmegaROI> particles = traj.getROIs();
			Collections.sort(particles, new Comparator<OmegaROI>() {
				@Override
				public int compare(final OmegaROI o1, final OmegaROI o2) {
					if (o1.getFrameIndex() == o2.getFrameIndex())
						return 0;
					else if (o1.getFrameIndex() < o2.getFrameIndex())
						return -1;
					return 1;
				};
			});
		}
	}

	public List<OmegaTrajectory> getResultingTrajectories() {
		return this.resultingTrajectories;
	}
}
