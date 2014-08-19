package edu.umassmed.omega.commons.genericInterfaces;

import edu.umassmed.omega.dataNew.OmegaLoadedData;

public interface OmegaLoadedDataConsumerPluginInterface {
	public void setLoadedData(final OmegaLoadedData loadedData);

	public OmegaLoadedData getLoadedData();
}
