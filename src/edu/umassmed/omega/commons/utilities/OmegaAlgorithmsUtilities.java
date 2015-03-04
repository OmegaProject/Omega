package edu.umassmed.omega.commons.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umassmed.omega.data.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.data.analysisRunElements.OmegaAlgorithmSpecification;
import edu.umassmed.omega.data.coreElements.OmegaPerson;
import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegmentationTypes;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaAlgorithmsUtilities {

	private static OmegaAlgorithmSpecification DEFAULT_TRACKING_MEASURES_SPEC;
	private static String DEFAULT_TRACKING_MEASURES_ALGO_NAME = "Omega default tracking measures";
	private static Double DEFAULT_TRACKING_MEASURES_ALGO_VERSION = 1.0;
	private static String DEFAULT_TRACKING_MEASURES_ALGO_DESC = "Omega default tracking measures";
	private static Date DEFAULT_TRACKING_MEASURES_ALGO_DATE = new GregorianCalendar(
	        2014, 29, 1).getTime();

	private static OmegaAlgorithmSpecification DEFAULT_SEGMENTATIONS_SPEC;
	private static String DEFAULT_SEGMENTATION_ALGO_NAME = "Omega default segmentation";
	private static Double DEFAULT_SEGMENTATION_ALGO_VERSION = 1.0;
	private static String DEFAULT_SEGMENTATION_ALGO_DESC = "Omega default segmentation";
	private static Date DEFAULT_SEGMENTATION_ALGO_DATE = new GregorianCalendar(
	        2014, 12, 1).getTime();

	private static OmegaAlgorithmSpecification DEFAULT_RELINKING_SPEC;
	private static String DEFAULT_RELINKING_ALGO_NAME = "Omega default relinking";
	private static Double DEFAULT_RELINKING_ALGO_VERSION = 1.0;
	private static String DEFAULT_RELINKING_ALGO_DESC = "Omega default relinking";
	private static Date DEFAULT_RELINKING_ALGO_DATE = new GregorianCalendar(
	        2014, 12, 1).getTime();

	private static OmegaPerson DEFAULT_DEVELOPER;

	static {
		OmegaAlgorithmsUtilities.DEFAULT_DEVELOPER = new OmegaPerson("Alex",
		        "RIGANO");
		final OmegaAlgorithmInformation algoInfoRelinking = new OmegaAlgorithmInformation(
		        OmegaAlgorithmsUtilities.DEFAULT_RELINKING_ALGO_NAME,
		        OmegaAlgorithmsUtilities.DEFAULT_RELINKING_ALGO_VERSION,
		        OmegaAlgorithmsUtilities.DEFAULT_RELINKING_ALGO_DESC,
		        OmegaAlgorithmsUtilities.DEFAULT_DEVELOPER,
		        OmegaAlgorithmsUtilities.DEFAULT_RELINKING_ALGO_DATE);
		OmegaAlgorithmsUtilities.DEFAULT_RELINKING_SPEC = new OmegaAlgorithmSpecification(
		        algoInfoRelinking);
		final OmegaAlgorithmInformation algoInfoSegmentation = new OmegaAlgorithmInformation(
		        OmegaAlgorithmsUtilities.DEFAULT_SEGMENTATION_ALGO_NAME,
		        OmegaAlgorithmsUtilities.DEFAULT_SEGMENTATION_ALGO_VERSION,
		        OmegaAlgorithmsUtilities.DEFAULT_SEGMENTATION_ALGO_DESC,
		        OmegaAlgorithmsUtilities.DEFAULT_DEVELOPER,
		        OmegaAlgorithmsUtilities.DEFAULT_SEGMENTATION_ALGO_DATE);
		OmegaAlgorithmsUtilities.DEFAULT_SEGMENTATIONS_SPEC = new OmegaAlgorithmSpecification(
		        algoInfoSegmentation);
		final OmegaAlgorithmInformation algoInfoTrackingMeasures = new OmegaAlgorithmInformation(
		        OmegaAlgorithmsUtilities.DEFAULT_TRACKING_MEASURES_ALGO_NAME,
		        OmegaAlgorithmsUtilities.DEFAULT_TRACKING_MEASURES_ALGO_VERSION,
		        OmegaAlgorithmsUtilities.DEFAULT_TRACKING_MEASURES_ALGO_DESC,
		        OmegaAlgorithmsUtilities.DEFAULT_DEVELOPER,
		        OmegaAlgorithmsUtilities.DEFAULT_TRACKING_MEASURES_ALGO_DATE);
		OmegaAlgorithmsUtilities.DEFAULT_TRACKING_MEASURES_SPEC = new OmegaAlgorithmSpecification(
		        algoInfoTrackingMeasures);
	}

	public static OmegaAlgorithmSpecification getDefaultRelinkingAlgorithmSpecification() {
		return OmegaAlgorithmsUtilities.DEFAULT_RELINKING_SPEC;
	}

	public static OmegaAlgorithmSpecification getDefaultSegmentationAlgorithmSpecification() {
		return OmegaAlgorithmsUtilities.DEFAULT_SEGMENTATIONS_SPEC;
	}

	public static OmegaAlgorithmSpecification getDefaultTrackingMeasuresSpecification() {
		return OmegaAlgorithmsUtilities.DEFAULT_TRACKING_MEASURES_SPEC;
	}

	public static OmegaPerson getDefaultDeveloper() {
		return OmegaAlgorithmsUtilities.DEFAULT_DEVELOPER;
	}

	public static Map<OmegaTrajectory, List<OmegaSegment>> createDefaultSegmentation(
	        final List<OmegaTrajectory> trajectories) {
		final Map<OmegaTrajectory, List<OmegaSegment>> segmentsMap = new LinkedHashMap<>();
		for (final OmegaTrajectory trajectory : trajectories) {
			final List<OmegaSegment> segments = OmegaAlgorithmsUtilities
			        .createDefaultSegmentation(trajectory);
			segmentsMap.put(trajectory, segments);
		}
		return segmentsMap;
	}

	public static List<OmegaSegment> createDefaultSegmentation(
	        final OmegaTrajectory trajectory) {
		final List<OmegaSegment> segments = new ArrayList<OmegaSegment>();
		final OmegaROI startingPoint = trajectory.getROIs().get(0);
		final OmegaROI endingPoint = trajectory.getROIs().get(
		        trajectory.getLength() - 1);
		final OmegaSegment edge = new OmegaSegment(startingPoint, endingPoint);
		edge.setSegmentationType(OmegaSegmentationTypes.NOT_ASSIGNED_VAL);
		segments.add(edge);
		return segments;
	}
}
