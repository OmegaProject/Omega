package edu.umassmed.omega.dataNew.trajectoryElements;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaROI extends OmegaElement {

	private final int frameIndex;
	private final double x, y;

	public OmegaROI(final int frameIndex, final double x, final double y) {
		super();

		this.frameIndex = frameIndex;
		this.x = x;
		this.y = y;
	}

	public OmegaROI(final Long elementID, final int frameIndex, final double x,
	        final double y) {
		super(elementID);

		this.frameIndex = frameIndex;
		this.x = x;
		this.y = y;
	}

	public int getFrameIndex() {
		return this.frameIndex;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}
}
