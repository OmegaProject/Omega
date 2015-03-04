package edu.umassmed.omega.commons.eventSystem.events;

import java.util.List;
import java.util.Map;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaPluginEventSelectionTrajectoriesSegmentationRun extends
        OmegaPluginEventSelectionAnalysisRun {

	private final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap;

	public OmegaPluginEventSelectionTrajectoriesSegmentationRun(
	        final OmegaAnalysisRun analysisRun,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		this(null, analysisRun, segmentsMap);
	}

	public OmegaPluginEventSelectionTrajectoriesSegmentationRun(
	        final OmegaPlugin source, final OmegaAnalysisRun analysisRun,
	        final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap) {
		super(source, null);
		this.segmentsMap = segmentsMap;
	}

	public Map<OmegaTrajectory, List<OmegaSegment>> getSegmentsMap() {
		return this.segmentsMap;
	}
}
