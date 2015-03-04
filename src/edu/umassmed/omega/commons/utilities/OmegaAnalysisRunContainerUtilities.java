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
package edu.umassmed.omega.commons.utilities;

import java.util.List;
import java.util.Map;

import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRunContainer;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaAnalysisRunContainerUtilities {

	public static int getAnalysisCount(
	        final OmegaAnalysisRunContainer analysisRunContainer) {
		int count = 0;
		for (final OmegaAnalysisRun analysisRun : analysisRunContainer
		        .getAnalysisRuns()) {
			count++;
			count += OmegaAnalysisRunContainerUtilities
			        .getAnalysisCount(analysisRun);
		}
		return count;
	}

	public static boolean isTrajectoriesListEqual(
	        final List<OmegaTrajectory> trajs,
	        final List<OmegaTrajectory> trajs2) {
		if (trajs.size() != trajs2.size())
			return false;
		for (final OmegaTrajectory traj : trajs) {
			boolean found = false;
			for (final OmegaTrajectory traj2 : trajs2) {
				if (!traj.isEqual(traj2)) {
					continue;
				}
				found = true;
				break;
			}
			if (!found)
				return false;
		}
		return true;
	}

	public static boolean isSegmentMapEqual(
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap2) {
		if (segmentsMap.size() != segmentsMap2.size())
			return false;
		for (final OmegaTrajectory traj : segmentsMap.keySet()) {
			final List<OmegaSegment> segments = segmentsMap.get(traj);
			boolean trajFound = false;
			for (final OmegaTrajectory traj2 : segmentsMap2.keySet()) {
				if (!traj.isEqual(traj2)) {
					continue;
				}
				final List<OmegaSegment> segments2 = segmentsMap2.get(traj2);
				for (final OmegaSegment segm : segments) {
					boolean segmFound = false;
					for (final OmegaSegment segm2 : segments2) {
						if (!segm.isEqual(segm2)) {
							continue;
						}
						segmFound = true;
						break;
					}
					if (!segmFound)
						return false;
				}
				trajFound = true;
				break;
			}
			if (!trajFound)
				return false;
		}
		return true;
	}
}
