package edu.umassmed.omega.omegaDataBrowserPlugin;

import java.util.List;

import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.OmegaBrowserPlugin;
import edu.umassmed.omega.commons.OmegaDataDisplayerPluginInterface;
import edu.umassmed.omega.commons.exceptions.OmegaMissingData;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.OmegaLoadedData;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.omegaDataBrowserPlugin.gui.OmegaDataBrowserPluginPanel;

public class OmegaDataBrowserPlugin extends OmegaBrowserPlugin implements
        OmegaDataDisplayerPluginInterface {

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
	        final int index) throws OmegaMissingData {

		final OmegaData omegaData = this.getMainData();
		final OmegaLoadedData loadedData = this.getLoadedData();
		final List<OmegaAnalysisRun> loadedAnalysisRuns = this
		        .getLoadedAnalysisRuns();
		if (omegaData == null)
			throw new OmegaMissingData(this);

		// TODO loadedData exception
		// TODO loadedAnalysisRuns exception

		this.panel = new OmegaDataBrowserPluginPanel(parent, this, omegaData,
		        loadedData, loadedAnalysisRuns, index);

		return this.panel;
	}

	@Override
	public void run() {
		//
	}

	@Override
	public void updateDisplayedData() {
		if (this.panel != null) {
			this.panel.updateTrees();
		}
	}
}
