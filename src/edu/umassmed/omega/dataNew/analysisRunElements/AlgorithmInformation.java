package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.Calendar;
import java.util.Date;

import edu.umassmed.omega.dataNew.coreElements.OmegaNamedElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;

public class AlgorithmInformation extends OmegaNamedElement {

	private final double version;
	private final String description;
	private final Date publicationDate;
	private OmegaPerson author;

	public AlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description) {
		super(elementID, name);

		this.author = null;
		this.version = version;
		this.description = description;
		this.publicationDate = Calendar.getInstance().getTime();
	}

	public AlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description,
	        final OmegaPerson author) {
		this(elementID, name, version, description);

		this.author = author;
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
