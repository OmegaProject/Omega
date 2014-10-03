package edu.umassmed.omega.commons.plugins.interfaces;

import edu.umassmed.omega.dataNew.OmegaData;

public interface OmegaMainDataConsumerPluginInterface {
	public void setMainData(final OmegaData omegaData);

	public OmegaData getMainData();
}
