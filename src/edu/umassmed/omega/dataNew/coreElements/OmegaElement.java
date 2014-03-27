package edu.umassmed.omega.dataNew.coreElements;

public class OmegaElement {

	// TODO: Pensare ad un modo per salvare gli id in modo da autogenerarli

	private final Long elementID;

	public OmegaElement(final Long elementID) {
		this.elementID = elementID;
	}

	public Long getElementID() {
		return this.elementID;
	}
}
