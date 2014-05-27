package edu.umassmed.omega.commons;

import java.util.Date;

import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;

public abstract class OmegaAlgorithmPlugin extends OmegaPlugin {

	public abstract String getAlgorithmDescription();

	public abstract String getAlgorithmName();

	public abstract OmegaPerson getAlgorithmAuthor();

	public abstract Double getAlgorithmVersion();

	public abstract Date getAlgorithmPublicationDate();
}
