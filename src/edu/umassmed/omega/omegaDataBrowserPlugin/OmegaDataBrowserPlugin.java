package edu.umassmed.omega.omegaDataBrowserPlugin;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.exceptions.MissingOmegaData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.omegaDataBrowserPlugin.gui.OmegaDataBrowserPluginPanel;

public class OmegaDataBrowserPlugin extends OmegaBrowserPlugin {

	private OmegaDataBrowserPluginPanel panel;

	public OmegaDataBrowserPlugin() {
		super();
	}

	@Override
	public String getName() {
		return "Omega Browser";
	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) throws MissingOmegaData {

		final OmegaData omegaData = this.getOmegaData();
		if (omegaData == null)
			throw new MissingOmegaData(this);

		this.panel = new OmegaDataBrowserPluginPanel(parent, this, omegaData,
		        index);

		return this.panel;
	}

	@Override
	public void run() {
		//
	}

	@Override
	public void fireUpdate() {
		if (this.panel != null) {
			this.panel.fireUpdate();
		}
	}
}
