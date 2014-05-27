package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaAlgorithmSpecification extends OmegaElement {

	private final OmegaAlgorithmInformation algorithmInfo;

	private List<OmegaParameter> parameters;

	public OmegaAlgorithmSpecification(final Long elementID,
	        final OmegaAlgorithmInformation algorithmInfo) {
		super(elementID);

		this.algorithmInfo = algorithmInfo;

		this.parameters = new ArrayList<OmegaParameter>();
	}

	public OmegaAlgorithmSpecification(final Long elementID,
	        final OmegaAlgorithmInformation algorithmInfo,
	        final List<OmegaParameter> parameters) {
		this(elementID, algorithmInfo);

		this.parameters = parameters;
	}

	public OmegaAlgorithmInformation getAlgorithmInfo() {
		return this.algorithmInfo;
	}

	public List<OmegaParameter> getParameters() {
		return this.parameters;
	}

	public void addParameters(final OmegaParameter parameter) {
		this.parameters.add(parameter);
	}
}
