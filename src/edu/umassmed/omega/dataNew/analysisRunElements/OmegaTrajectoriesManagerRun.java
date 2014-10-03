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
package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaTrajectory;

public class OmegaTrajectoriesManagerRun extends OmegaParticleLinkingRun {

	private boolean isSegmentationTypesModified, isSegmentsModified,
	        isTrajectoriesModified;
	private OmegaSegmentationTypes segmentationTypes;
	private final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments;

	public OmegaTrajectoriesManagerRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final List<OmegaTrajectory> resultingTrajectory,
	        final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
	        final OmegaSegmentationTypes segmentationTypes) {
		super(owner, algorithmSpec, resultingTrajectory);
		this.resultingSegments = resultingSegments;
		this.segmentationTypes = segmentationTypes;

		this.isSegmentationTypesModified = false;
		this.isSegmentsModified = false;
		this.isTrajectoriesModified = false;
	}

	public OmegaTrajectoriesManagerRun(final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final Date timeStamps, final String name,
	        final List<OmegaTrajectory> resultingTrajectories,
	        final Map<OmegaTrajectory, List<OmegaSegment>> resultingSegments,
	        final OmegaSegmentationTypes segmentationTypes) {
		super(owner, algorithmSpec, timeStamps, name, resultingTrajectories);
		this.resultingSegments = resultingSegments;
		this.segmentationTypes = segmentationTypes;

		this.isSegmentationTypesModified = false;
		this.isSegmentsModified = false;
		this.isTrajectoriesModified = false;
	}

	public boolean isSegmetationTypesModified() {
		return this.isSegmentationTypesModified;
	}

	public boolean isSegmentsModified() {
		return this.isSegmentsModified;
	}

	public boolean isTrajectoriesModified() {
		return this.isTrajectoriesModified;
	}

	public void resetSegmentationTypesModified() {
		this.isSegmentationTypesModified = false;
	}

	public void resetSegmentsModified() {
		this.isSegmentsModified = false;
	}

	public void resetTrajectoriesModified() {
		this.isTrajectoriesModified = false;
	}

	public OmegaSegmentationTypes getSegmentationTypes() {
		return this.segmentationTypes;
	}

	public void updateSegmentationTypes(
	        final OmegaSegmentationTypes segmentationTypes) {
		this.segmentationTypes = segmentationTypes;
		this.isSegmentationTypesModified = true;
	}

	public void updateSegments(
	        final Map<OmegaTrajectory, List<OmegaSegment>> segments) {
		this.resultingSegments.clear();
		this.resultingSegments.putAll(segments);
		this.isSegmentsModified = true;
	}

	public Map<OmegaTrajectory, List<OmegaSegment>> getResultingSegments() {
		return this.resultingSegments;
	}

	public void updateTrajectories(final List<OmegaTrajectory> trajectories) {
		this.getResultingTrajectories().clear();
		this.getResultingTrajectories().addAll(trajectories);
		this.isTrajectoriesModified = true;
	}

	public void updateTimeStamps() {
		this.getTimeStamps()
		        .setTime(Calendar.getInstance().getTime().getTime());
	}

}
