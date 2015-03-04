package edu.umassmed.omega.commons.libraries;

import java.util.List;

import edu.umassmed.omega.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.data.trajectoryElements.OmegaTrajectory;

public class OmegaMobilityLibrary {

	public static Double computeTotalDistanceTraveled(
	        final OmegaTrajectory track, final int endT) {
		final List<OmegaROI> rois = track.getROIs();
		return OmegaMobilityLibrary.computeTotalDistanceTraveled(rois, endT);
	}

	public static Double computeTotalDistanceTraveled(
	        final OmegaTrajectory track) {
		final List<OmegaROI> rois = track.getROIs();
		final int endT = rois.get(rois.size() - 1).getFrameIndex();
		return OmegaMobilityLibrary.computeTotalDistanceTraveled(rois, endT);
	}

	public static Double computeTotalDistanceTraveled(
	        final List<OmegaROI> rois, final int endT) {
		final int maxT = rois.get(rois.size() - 1).getFrameIndex();
		if (endT > maxT)
			return null;
		double totalDistanceTraveled = 0.0;
		for (int i = 0; i < (rois.size() - 1); i++) {
			final OmegaROI roi1 = rois.get(i);
			final OmegaROI roi2 = rois.get(i + 1);
			totalDistanceTraveled += OmegaMobilityLibrary.computeDistance(
			        roi1.getX(), roi1.getY(), roi2.getX(), roi2.getY());
			if (roi2.getFrameIndex() == endT) {
				break;
			} else if (roi2.getFrameIndex() > endT)
				return null;
		}
		return totalDistanceTraveled;
	}

	public static Double computeTotalNetDisplacement(final OmegaTrajectory track) {
		final List<OmegaROI> rois = track.getROIs();
		final OmegaROI roi1 = rois.get(0);
		final OmegaROI roi2 = rois.get(rois.size() - 1);
		return OmegaMobilityLibrary.computeDistance(roi1.getX(), roi1.getY(),
		        roi2.getX(), roi2.getY());
	}

	public static Double computeTotalNetDisplacement(
	        final OmegaTrajectory track, final int endT) {
		final List<OmegaROI> rois = track.getROIs();
		final OmegaROI roi1 = rois.get(0);
		final OmegaROI roi2 = OmegaMobilityLibrary.findROI(rois, endT);
		if ((roi2 == null) || (roi1 == roi2))
			return null;
		return OmegaMobilityLibrary.computeDistance(roi1.getX(), roi1.getY(),
		        roi2.getX(), roi2.getY());
	}

	public static Double computeTotalNetDisplacement(
	        final OmegaTrajectory track, final int startT, final int endT) {
		final List<OmegaROI> rois = track.getROIs();
		final OmegaROI roi1 = OmegaMobilityLibrary.findROI(rois, startT);
		final OmegaROI roi2 = OmegaMobilityLibrary.findROI(rois, endT);
		if ((roi2 == null) || (roi1 == roi2))
			return null;
		return OmegaMobilityLibrary.computeDistance(roi1.getX(), roi1.getY(),
		        roi2.getX(), roi2.getY());
	}

	public static Double[] computeDirectionalChange(
	        final OmegaTrajectory track, final Double prevAngle) {
		final List<OmegaROI> rois = track.getROIs();
		final OmegaROI endROI = rois.get(rois.size() - 1);
		return OmegaMobilityLibrary.computeDirectionalChange(track,
		        prevAngle, endROI.getFrameIndex());
	}

	public static Double[] computeDirectionalChange(
	        final OmegaTrajectory track, final Double prevAngle, final int t) {
		final List<OmegaROI> rois = track.getROIs();
		final Double[] angleAndDirectionalChange = new Double[2];
		angleAndDirectionalChange[0] = null;
		angleAndDirectionalChange[1] = null;
		final OmegaROI roi1 = OmegaMobilityLibrary.findROI(rois, t);
		final OmegaROI roi2 = OmegaMobilityLibrary.findNextROI(rois, t);
		if ((roi1 == null) || (roi2 == null))
			return angleAndDirectionalChange;
		angleAndDirectionalChange[0] = OmegaMobilityLibrary.computeAngle(
		        roi1.getX(), roi1.getY(), roi2.getX(), roi2.getY());
		angleAndDirectionalChange[1] = null;
		if (prevAngle != null) {
			angleAndDirectionalChange[1] = prevAngle
			        - angleAndDirectionalChange[0];
		}
		// if (roi2.getFrameIndex() == endT) {
		// break;
		// } else if (roi2.getFrameIndex() > endT)
		// return null;
		return angleAndDirectionalChange;
	}

	public static Double[] computeDirectionalChangeWithRefPoint(
	        final OmegaTrajectory track, final Double prevAngle) {
		final List<OmegaROI> rois = track.getROIs();
		final OmegaROI startROI = rois.get(0);
		final OmegaROI endROI = rois.get(rois.size() - 1);
		return OmegaMobilityLibrary.computeDirectionalChangeWithRefPoint(
		        track, prevAngle, startROI.getX(), startROI.getY(),
		        endROI.getFrameIndex());
	}

	public static Double[] computeDirectionalChangeWithRefPoint(
	        final OmegaTrajectory track, final Double prevAngle,
	        final double x, final double y, final int t) {
		final List<OmegaROI> rois = track.getROIs();
		final Double[] angleAndDirectionalChange = new Double[2];
		angleAndDirectionalChange[0] = null;
		angleAndDirectionalChange[1] = null;
		final OmegaROI roi1 = OmegaMobilityLibrary.findROI(rois, t);
		if (roi1 == null)
			return angleAndDirectionalChange;
		angleAndDirectionalChange[0] = OmegaMobilityLibrary.computeAngle(x,
		        y, roi1.getX(), roi1.getY());
		angleAndDirectionalChange[1] = null;
		if (prevAngle != null) {
			angleAndDirectionalChange[1] = prevAngle
			        - angleAndDirectionalChange[0];

		}
		return angleAndDirectionalChange;
	}

	private static OmegaROI findROI(final List<OmegaROI> rois, final int t) {
		for (final OmegaROI roi : rois) {
			if (roi.getFrameIndex() == t)
				return roi;
		}
		return null;
	}

	private static OmegaROI findNextROI(final List<OmegaROI> rois, final int t) {
		for (final OmegaROI roi : rois) {
			if (roi.getFrameIndex() > t)
				return roi;
		}
		return null;
	}

	public static Double computeAngle(final double x1, final double y1,
	        final double x2, final double y2) {
		// Vector B
		final double bX = x2 - x1;
		final double bY = y2 - y1;
		final double bX2 = Math.pow(bX, 2);
		final double bY2 = Math.pow(bY, 2);
		final double modB = Math.sqrt(bX2 + bY2);
		// Vector A
		final double aX = bX;
		final double aY = 0;
		final double aX2 = Math.pow(aX, 2);
		final double aY2 = Math.pow(aY, 2);
		final double modA = Math.sqrt(aX2 + aY2);
		// Cross product
		final double aCrossB = (aX * bX) + (aY * bY);
		// Modules product
		final double modAB = modA * modB;
		// Division result
		final double div = aCrossB / modAB;
		// Return angle
		return StrictMath.acos(div);
	}

	public static Double computeDistance(final double x1, final double y1,
	        final double x2, final double y2) {
		final double distX = x2 - x1;
		final double distY = y2 - y1;
		final double distX2 = Math.pow(distX, 2);
		final double distY2 = Math.pow(distY, 2);
		return Math.sqrt(distX2 + distY2);
	}
}
