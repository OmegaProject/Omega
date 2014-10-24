package edu.umassmed.omega.algoritmhs;

public class OmegaAlgorithmPoint {

	private final Double x, y;
	private final Double intensity;
	private final Double probability;
	private final Double snr;
	private final Integer frameIndex;

	public OmegaAlgorithmPoint(final Double x, final Double y,
	        final Integer frameIndex) {
		this.x = x;
		this.y = y;
		this.frameIndex = frameIndex;
		this.intensity = null;
		this.probability = null;
		this.snr = null;
	}

	public OmegaAlgorithmPoint(final Double x, final Double y,
	        final Integer frameIndex, final Double intensity,
	        final Double probability, final Double snr) {
		this.x = x;
		this.y = y;
		this.frameIndex = frameIndex;
		this.intensity = intensity;
		this.probability = probability;
		this.snr = snr;
	}

	public Double getX() {
		return this.x;
	}

	public Double getY() {
		return this.y;
	}

	public Integer getFrameIndex() {
		return this.frameIndex;
	}

	public Double getIntensity() {
		return this.intensity;
	}

	public Double getProbability() {
		return this.probability;
	}

	public Double getSNR() {
		return this.snr;
	}
}
