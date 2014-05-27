package edu.umassmed.omega.commons.eventSystem;

import java.util.List;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;

public class OmegaDataChangedEvent extends OmegaPluginEvent {

	private final List<OmegaElement> selectedData;

	public OmegaDataChangedEvent() {
		this(null);
	}

	public OmegaDataChangedEvent(final OmegaPlugin source) {
		super(source);

		this.selectedData = null;
	}

	public OmegaDataChangedEvent(final OmegaPlugin source,
	        final List<OmegaElement> selectedData) {
		super(source);
		this.selectedData = selectedData;
	}

	public List<OmegaElement> getSelectedData() {
		return this.selectedData;
	}

}
