package edu.umassmed.omega.commons.genericInterfaces;

import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaImage;

public interface OmegaImageConsumerPluginInterface {
	public List<OmegaImage> getLoadedImages();

	public void setLoadedImages(final List<OmegaImage> images);
}
