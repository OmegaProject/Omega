package edu.umassmed.omega.dataNew.trajectoryElements;

public class OmegaParticle extends OmegaROI {

	private final double intesity;
	private final double probability;

	public OmegaParticle(final int frameIndex, final double x, final double y) {
		super(frameIndex, x, y);

		this.intesity = -1;
		this.probability = -1;
	}

	public OmegaParticle(final int frameIndex, final double x, final double y,
	        final double intesity, final double probability) {
		super(frameIndex, x, y);

		this.intesity = intesity;
		this.probability = probability;
	}

	public OmegaParticle(final Long elementID, final int frameIndex,
	        final double x, final double y) {
		super(elementID, frameIndex, x, y);

		this.intesity = -1;
		this.probability = -1;
	}

	public OmegaParticle(final Long elementID, final int frameIndex,
	        final double x, final double y, final double intesity,
	        final double probability) {
		super(elementID, frameIndex, x, y);

		this.intesity = intesity;
		this.probability = probability;
	}

	public double getIntesity() {
		return this.intesity;
	}

	public double getProbability() {
		return this.probability;
	}
}
