package edu.umassmed.omega.data.trajectoryElements;

import edu.umassmed.omega.data.coreElements.OmegaElement;

public class OmegaSegment extends OmegaElement {
	private final OmegaROI from, to;
	private int segmentationType;

	public OmegaSegment(final OmegaROI startingROI, final OmegaROI endingROI) {
		super((long) -1);
		this.from = startingROI;
		this.to = endingROI;
	}

	public OmegaROI getStartingROI() {
		return this.from;
	}

	public OmegaROI getEndingROI() {
		return this.to;
	}

	public int getSegmentationType() {
		return this.segmentationType;
	}

	public void setSegmentationType(final int segmentationType) {
		this.segmentationType = segmentationType;
	}

	public boolean isEqual(final OmegaSegment segment) {
		if ((this.from.getFrameIndex() == segment.from.getFrameIndex())
		        && (this.to.getFrameIndex() == segment.to.getFrameIndex())
		        && (this.segmentationType == segment.segmentationType))
			return true;
		return false;
	}
}
