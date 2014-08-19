package edu.umassmed.omega.commons.genericInterfaces;

import edu.umassmed.omega.dataNew.OmegaData;

public interface OmegaMainDataConsumerPluginInterface {
	public void setMainData(final OmegaData omegaData);

	public OmegaData getMainData();
}
