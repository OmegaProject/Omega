package edu.umassmed.omega.dataNew.analysisRunElements;

import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;

public class OmegaParameter extends OmegaNamedElement {

	private final Object value;

	public OmegaParameter(final Long elementID, final String name,
	        final Object value) {
		super(elementID, name);

		this.value = value;
	}

	public Object getValue() {
		return this.value;
	}

	public String getStringValue() {
		if (this.value instanceof Integer)
			return Integer.toString((int) this.value);
		else if (this.value instanceof Double)
			return Double.toString((double) this.value);
		else if (this.value instanceof String)
			return (String) this.value;
		else
			return this.value.toString();
	}

}
