package edu.umassmed.omega.omero;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.omero.gui.OmeroPluginPanel;

public class OmeroPlugin extends OmegaPlugin {

	@Override
	public String getName() {
		return "Omero";
	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) {
		return new OmeroPluginPanel(parent, this, index);
	}

	@Override
	public void run() {
		//
	}
}
