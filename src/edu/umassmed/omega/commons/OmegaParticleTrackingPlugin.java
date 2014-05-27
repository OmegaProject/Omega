package edu.umassmed.omega.commons;

import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public abstract class OmegaParticleTrackingPlugin extends OmegaAlgorithmPlugin {

	private OmegaGateway gateway;

	private List<OmegaImage> loadedImages;

	public OmegaParticleTrackingPlugin() {
		super();

		this.gateway = null;
		this.loadedImages = null;
	}

	public OmegaGateway getGateway() {
		return this.gateway;
	}

	public void setGateway(final OmegaGateway gateway) {
		this.gateway = gateway;
	}

	public List<OmegaImage> getLoadedImages() {
		return this.loadedImages;
	}

	public void setLoadedImages(final List<OmegaImage> images) {
		this.loadedImages = images;
	}
}
