package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaAlgorithmSpecification extends OmegaElement {

	private final AlgorithmInformation algorithmInfo;

	private List<OmegaParameter> parameters;

	public OmegaAlgorithmSpecification(final Long elementID,
	        final AlgorithmInformation algorithmInfo) {
		super(elementID);

		this.algorithmInfo = algorithmInfo;

		this.parameters = new ArrayList<OmegaParameter>();
	}

	public OmegaAlgorithmSpecification(final Long elementID,
	        final AlgorithmInformation algorithmInfo,
	        final List<OmegaParameter> parameters) {
		this(elementID, algorithmInfo);

		this.parameters = parameters;
	}

	public AlgorithmInformation getAlgorithmInfo() {
		return this.algorithmInfo;
	}

	public List<OmegaParameter> getParameters() {
		return this.parameters;
	}

	public void addParameters(final OmegaParameter parameter) {
		this.parameters.add(parameter);
	}
}
