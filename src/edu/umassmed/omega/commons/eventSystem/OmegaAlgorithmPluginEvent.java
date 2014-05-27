package edu.umassmed.omega.commons.eventSystem;

import java.util.List;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParameter;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaAlgorithmPluginEvent extends OmegaPluginEvent {

	private final OmegaElement element;
	private final List<OmegaParameter> params;

	public OmegaAlgorithmPluginEvent(final OmegaElement element,
	        final List<OmegaParameter> params) {
		this(null, element, params);
	}

	public OmegaAlgorithmPluginEvent(final OmegaPlugin source,
	        final OmegaElement element, final List<OmegaParameter> params) {
		super(source);

		this.element = element;

		this.params = params;
	}

	public OmegaElement getElement() {
		return this.element;
	}

	public List<OmegaParameter> getParameters() {
		return this.params;
	}
}
