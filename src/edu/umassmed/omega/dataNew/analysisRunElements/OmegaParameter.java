package edu.umassmed.omega.dataNew.analysisRunElements;

import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;

public class OmegaParameter extends OmegaNamedElement {

	private final Class<?> clazz;
	private final Object value;

	public OmegaParameter(final Long elementID, final String name,
	        final Class<?> clazz, final Object value) {
		super(elementID, name);

		this.clazz = clazz;
		this.value = value;
	}

	public Class<?> getClazz() {
		return this.clazz;
	}

	public Object getValue() {
		return this.value;
	}

}
