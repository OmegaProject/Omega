package edu.umassmed.omega.commons.plugins.interfaces;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaTrajectoriesManagerRun;

public interface OmegaSelectTrajectoriesManagerRunPluginInterface extends
        OmegaSelectParticleLinkingRunPluginInterface {
	public void selectTrajectoriesManagerRun(
	        OmegaTrajectoriesManagerRun analysisRun);
}
