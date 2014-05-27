package edu.umassmed.omega.commons;

import edu.umassmed.omega.dataNew.OmegaData;

public abstract class OmegaDataManagerPlugin extends OmegaPlugin {
	private OmegaData omegaData;

	public OmegaDataManagerPlugin() {
		this(1);
	}

	public OmegaDataManagerPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);
	}

	public void setMainData(final OmegaData omegaData) {
		this.omegaData = omegaData;
	}

	protected OmegaData getMainData() {
		return this.omegaData;
	}
}
