package edu.umassmed.omega.commons.plugins.interfaces;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaParticleLinkingRun;

public interface OmegaSelectParticleLinkingRunPluginInterface extends
        OmegaSelectParticleDetectionRunPluginInterface {
	public void selectParticleLinkingRun(OmegaParticleLinkingRun analysisRun);
}
