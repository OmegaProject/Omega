package edu.umassmed.omega.commons;

import java.util.Date;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAlgorithmInformation;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.coreElements.OmegaPerson;

public abstract class OmegaAlgorithmPlugin extends OmegaPlugin {

	public OmegaAlgorithmPlugin(final int maxNumOfPanels) {
		super(maxNumOfPanels);
	}

	public abstract String getAlgorithmName();

	public abstract String getAlgorithmDescription();

	public abstract OmegaPerson getAlgorithmAuthor();

	public abstract Double getAlgorithmVersion();

	public abstract Date getAlgorithmPublicationDate();

	public boolean checkIfThisAlgorithm(final OmegaAnalysisRun analysisRun) {
		final OmegaAlgorithmInformation algoInfo = analysisRun
		        .getAlgorithmSpec().getAlgorithmInfo();
		final boolean tof1 = this.getAlgorithmName().equals(algoInfo.getName());
		final boolean tof2 = this.getAlgorithmDescription().equals(
		        algoInfo.getDescription());
		final boolean tof3 = this.getAlgorithmAuthor().isSamePersonAs(
		        algoInfo.getAuthor());
		final boolean tof4 = this.getAlgorithmVersion().equals(
		        algoInfo.getVersion());
		final boolean tof5 = this.getAlgorithmPublicationDate().equals(
		        algoInfo.getPublicationData());
		return tof1 && tof2 && tof3 && tof4 && tof5;
	}
}
