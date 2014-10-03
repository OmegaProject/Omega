package edu.umassmed.omega.commons.eventSystem;

import java.util.List;

import edu.umassmed.omega.commons.plugins.OmegaPlugin;
import edu.umassmed.omega.dataNew.trajectoryElements.OmegaSegmentationTypes;

public class OmegaTMPluginSegmentationTypesEvent extends OmegaPluginEvent {

	private final List<OmegaSegmentationTypes> segmTypesList;

	public OmegaTMPluginSegmentationTypesEvent(
	        final List<OmegaSegmentationTypes> segmTypesList) {
		this(null, segmTypesList);
	}

	public OmegaTMPluginSegmentationTypesEvent(final OmegaPlugin source,
	        final List<OmegaSegmentationTypes> segmTypesList) {
		super(source);
		this.segmTypesList = segmTypesList;
	}

	public List<OmegaSegmentationTypes> getSegmentationTypesList() {
		return this.segmTypesList;
	}
}
