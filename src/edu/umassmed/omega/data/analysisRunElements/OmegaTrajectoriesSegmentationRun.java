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
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaTrajectoriesSegmentationRun extends OmegaAnalysisRun {

	private final OmegaSegmentationTypes segmentationTypes;
	private final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments;

	public OmegaTrajectoriesSegmentationRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
	        final OmegaSegmentationTypes segmentationTypes) {
		super(owner, algorithmSpec);
		this.resultingSegments = resultingSegments;
		this.segmentationTypes = segmentationTypes;
	}

	public OmegaTrajectoriesSegmentationRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec, final String name,
	        final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
	        final OmegaSegmentationTypes segmentationTypes) {
		super(owner, algorithmSpec, name);
		this.resultingSegments = resultingSegments;
		this.segmentationTypes = segmentationTypes;
	}

	public OmegaTrajectoriesSegmentationRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Date timeStamps, final String name,
	        final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
	        final OmegaSegmentationTypes segmentationTypes) {
		super(owner, algorithmSpec, timeStamps, name);
		this.resultingSegments = resultingSegments;
		this.segmentationTypes = segmentationTypes;
	}

	public OmegaSegmentationTypes getSegmentationTypes() {
		return this.segmentationTypes;
	}

	public Map<OmegaTrajectory, List<OmegaSegment>> getResultingSegments() {
		return this.resultingSegments;
	}
}
