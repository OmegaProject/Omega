package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.Calendar;
import java.util.Date;

import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;

public class OmegaAlgorithmInformation extends OmegaNamedElement {

	private final double version;
	private final String description;
	private final Date publicationDate;
	private OmegaPerson author;

	public OmegaAlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description) {
		super(elementID, name);

		this.author = null;
		this.version = version;
		this.description = description;
		this.publicationDate = Calendar.getInstance().getTime();
	}

	public OmegaAlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description,
	        final OmegaPerson author) {
		this(elementID, name, version, description);

		this.author = author;
	}

	public OmegaAlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description,
	        final OmegaPerson author, final Date publicationDate) {
		super(elementID, name);

		this.version = version;
		this.description = description;

		this.author = author;

		this.publicationDate = publicationDate;
	}

	public String getDescription() {
		return this.description;
	}

	public double getVersion() {
		return this.version;
	}

	public Date getPublicationData() {
		return this.publicationDate;
	}

	public OmegaPerson getAuthor() {
		return this.author;
	}
}
