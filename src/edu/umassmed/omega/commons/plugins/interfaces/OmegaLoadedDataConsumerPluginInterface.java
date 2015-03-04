package edu.umassmed.omega.commons.plugins.interfaces;

import edu.umassmed.omega.data.OmegaLoadedData;

public interface OmegaLoadedDataConsumerPluginInterface {
	public void setLoadedData(final OmegaLoadedData loadedData);

	public OmegaLoadedData getLoadedData();
}
