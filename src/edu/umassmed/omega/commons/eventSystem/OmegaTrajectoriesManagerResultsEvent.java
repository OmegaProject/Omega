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
package edu.umassmed.omega.commons.eventSystem;

import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaTrajectoriesManagerRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaTrajectoriesManagerResultsEvent extends
        OmegaParticleLinkingResultsEvent {

	private final OmegaTrajectoriesManagerRun original;
	private final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments;
	private final OmegaSegmentationTypes segmentationTypes;

	public OmegaTrajectoriesManagerResultsEvent(final OmegaElement element,
	        final List<OmegaTrajectory> resultingTrajectories,
	        final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
	        final OmegaSegmentationTypes segmentationTypes,
	        final OmegaTrajectoriesManagerRun original) {
		this(null, element, resultingTrajectories, resultingSegments,
		        segmentationTypes, original);
	}

	public OmegaTrajectoriesManagerResultsEvent(final OmegaPlugin source,
	        final OmegaElement element,
	        final List<OmegaTrajectory> resultingTrajectories,
	        final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
	        final OmegaSegmentationTypes segmentationTypes,
	        final OmegaTrajectoriesManagerRun original) {
		super(source, element, null, resultingTrajectories);
		this.resultingSegments = resultingSegments;
		this.segmentationTypes = segmentationTypes;
		this.original = original;
	}

	public OmegaTrajectoriesManagerResultsEvent(final OmegaElement element,
	        final List<OmegaTrajectory> resultingTrajectories,
	        final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
	        final OmegaSegmentationTypes segmentationTypes) {
		this(null, element, resultingTrajectories, resultingSegments,
		        segmentationTypes);
	}

	public OmegaTrajectoriesManagerResultsEvent(final OmegaPlugin source,
	        final OmegaElement element,
	        final List<OmegaTrajectory> resultingTrajectories,
	        final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
	        final OmegaSegmentationTypes segmentationTypes) {
		this(source, element, resultingTrajectories, resultingSegments,
		        segmentationTypes, null);
	}

	public OmegaTrajectoriesManagerRun getOriginal() {
		return this.original;
	}

	public Map<OmegaTrajectory, List<OmegaSegment>> getResultingSegments() {
		return this.resultingSegments;
	}

	public OmegaSegmentationTypes getSegmentationTypes() {
		return this.segmentationTypes;
	}
}
