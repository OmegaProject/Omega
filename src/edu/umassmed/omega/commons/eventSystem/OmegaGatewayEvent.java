package edu.umassmed.omega.commons.eventSystem;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;

public class OmegaGatewayEvent extends OmegaPluginEvent {
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_DESTROYED = 2;
	public static final int STATUS_CONNECTED = 3;
	public static final int STATUS_DISCONNECTED = 4;

	private final int status;

	private final OmegaExperimenter experimenter;

	public OmegaGatewayEvent(final OmegaPlugin source, final int status) {
		this(source, status, null);
	}

	public OmegaGatewayEvent(final OmegaPlugin source, final int status,
	        final OmegaExperimenter experimenter) {
		super(source);
		this.status = status;

		this.experimenter = experimenter;
	}

	public int getStatus() {
		return this.status;
	}

	public OmegaExperimenter getExperimenter() {
		return this.experimenter;
	}
}
