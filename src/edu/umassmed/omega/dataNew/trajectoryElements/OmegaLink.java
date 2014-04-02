package edu.umassmed.omega.dataNew.trajectoryElements;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaLink extends OmegaElement {

	private final double speed, angle;
	private final OmegaROI startingROI, endingROI;

	public OmegaLink(final Long elementID, final OmegaROI startingROI,
	        final OmegaROI endingROI) {
		super(elementID);

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.speed = -1;
		this.angle = -1;
	}

	public OmegaLink(final Long elementID, final OmegaROI startingROI,
	        final OmegaROI endingROI, final double speed, final double angle) {
		super(elementID);

		this.startingROI = startingROI;
		this.endingROI = endingROI;

		this.speed = speed;
		this.angle = angle;
	}

	public OmegaROI getStartingROI() {
		return this.startingROI;
	}

	public OmegaROI getEndingROI() {
		return this.endingROI;
	}

	public double getSpeed() {
		return this.speed;
	}

	public double getAngle() {
		return this.angle;
	}
}
