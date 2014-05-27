package edu.umassmed.omega.omeroPlugin;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.eventSystem.OmegaGatewayEvent;
import edu.umassmed.omega.commons.exceptions.OmegaMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.omeroPlugin.gui.OmeroPluginPanel;

public class OmeroPlugin extends OmegaLoaderPlugin {

	private OmeroPluginPanel panel;

	public OmeroPlugin() {
		super(new OmeroGateway());
		this.panel = null;
	}

	@Override
	public String getName() {
		return "Omero Browser";
	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws OmegaMissingData {

		this.fireEvent(new OmegaGatewayEvent(this,
		        OmegaGatewayEvent.STATUS_CREATED));

		final OmegaData omegaData = this.getMainData();
		if (omegaData == null)
			throw new OmegaMissingData(this);

		this.panel = new OmeroPluginPanel(parent, this,
		        (OmeroGateway) this.getGateway(), omegaData, index);

		return this.panel;
	}

	@Override
	public void run() {
		//
	}
}
