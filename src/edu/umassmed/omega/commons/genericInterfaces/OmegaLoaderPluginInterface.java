package edu.umassmed.omega.commons.genericInterfaces;

import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public interface OmegaLoaderPluginInterface {
	public OmegaGateway getGateway();

	public void setGateway(OmegaGateway gateway);
}
