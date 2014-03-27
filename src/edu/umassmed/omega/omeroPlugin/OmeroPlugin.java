package edu.umassmed.omega.omeroPlugin;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaLoaderPlugin;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.omeroPlugin.gui.OmeroPluginPanel;

public class OmeroPlugin extends OmegaLoaderPlugin {

	private OmeroPluginPanel panel;

	final OmegaData omegaData;

	public OmeroPlugin() {
		super(new OmeroGateway());
		this.omegaData = new OmegaData();
		this.panel = null;
	}

	@Override
	public String getName() {
		return "Omero";
	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) {
		this.panel = new OmeroPluginPanel(parent, this,
		        (OmeroGateway) this.getGateway(), this.omegaData, index);

		return this.panel;
	}

	@Override
	public void run() {
		//
	}
}
