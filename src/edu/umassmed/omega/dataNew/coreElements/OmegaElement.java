package edu.umassmed.omega.dataNew.coreElements;

public class OmegaElement {

	// TODO: Pensare ad un modo per salvare gli id in modo da autogenerarli

	private Long elementID;

	public OmegaElement() {
		this.elementID = null;
	}

	public OmegaElement(final Long elementID) {
		this.elementID = elementID;
	}

	public Long getElementID() {
		return this.elementID;
	}

	public void setElementID(final Long elementID) {
		this.elementID = elementID;
	}
}
