package edu.umassmed.omega.dataNew.coreElements;

public class OmegaPerson extends OmegaElement {

	private final String firstName, lastName;

	public OmegaPerson(final Long elementID, final String firstName,
	        final String lastName) {
		super(elementID);
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}
}
