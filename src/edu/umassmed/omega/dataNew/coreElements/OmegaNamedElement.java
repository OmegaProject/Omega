package edu.umassmed.omega.dataNew.coreElements;

public class OmegaNamedElement extends OmegaElement {

	private final String name;

	public OmegaNamedElement(final long elementID, final String name) {
		super(elementID);
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
