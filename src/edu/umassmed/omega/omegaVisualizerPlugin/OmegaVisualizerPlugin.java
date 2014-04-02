package edu.umassmed.omega.omegaVisualizerPlugin;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;

public class OmegaVisualizerPlugin extends OmegaPlugin {

	public OmegaVisualizerPlugin() {
		super(25);
	}

	@Override
	public String getName() {
		return "Omega Visualizer";
	}

	@Override
	public GenericPluginPanel createNewPanel(final RootPaneContainer parent,
	        final int index) {
		return null;
	}

	@Override
	public void run() {
		//
	}
}
