package edu.umassmed.omega.commons;

import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public abstract class OmegaLoaderPlugin extends OmegaDataManagerPlugin {

	private final OmegaGateway gateway;

	public OmegaLoaderPlugin(final OmegaGateway gateway) {
		super();

		this.gateway = gateway;
	}

	public OmegaGateway getGateway() {
		return this.gateway;
	}
}
