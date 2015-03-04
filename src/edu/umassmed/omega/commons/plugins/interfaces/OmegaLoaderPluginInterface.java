package edu.umassmed.omega.commons.plugins.interfaces;

import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;

public interface OmegaLoaderPluginInterface {
	public OmegaGateway getGateway();

	public void setGateway(OmegaGateway gateway);
}
