package edu.umassmed.omega.dataNew.analysisRunElements;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaParameter extends OmegaElement {

	private final String name;
	private final Object value;
	private final Class<?> clazz;

	public OmegaParameter(final Long elementID, final String name,
	        final Class<?> clazz, final Object value) {
		super(elementID);

		this.name = name;
		this.clazz = clazz;
		this.value = value;
	}

}
