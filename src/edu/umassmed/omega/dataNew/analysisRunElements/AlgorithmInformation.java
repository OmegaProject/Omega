package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.Calendar;
import java.util.Date;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;

public class AlgorithmInformation extends OmegaElement {

	private final String name, description;
	private final double version;
	private OmegaPerson author;
	private final Date publicationDate;

	public AlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description) {
		super(elementID);

		this.name = name;
		this.author = null;
		this.version = version;
		this.publicationDate = Calendar.getInstance().getTime();
		this.description = description;
	}

	public AlgorithmInformation(final Long elementID, final String name,
	        final double version, final String description,
	        final OmegaPerson author) {
		this(elementID, name, version, description);

		this.author = author;
	}
}
