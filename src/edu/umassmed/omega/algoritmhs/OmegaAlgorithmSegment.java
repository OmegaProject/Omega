package edu.umassmed.omega.algoritmhs;

public class OmegaAlgorithmSegment {
	private final OmegaAlgorithmPoint from, to;
	private final Integer segmentationType;

	public OmegaAlgorithmSegment(final OmegaAlgorithmPoint from,
	        final OmegaAlgorithmPoint to, final Integer segmentationType) {
		this.from = from;
		this.to = to;

		this.segmentationType = segmentationType;
	}

	public OmegaAlgorithmPoint getFrom() {
		return this.from;
	}

	public OmegaAlgorithmPoint getTo() {
		return this.to;
	}

	public Integer getSegmentationType() {
		return this.segmentationType;
	}
}
