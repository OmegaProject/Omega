package edu.umassmed.omega.commons;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.gui.GenericPluginPanel;

public interface OmegaPluginInterface {

	public String getName();

	public void run();

	public GenericPluginPanel createNewPanel(RootPaneContainer parent, int index);
}
