package edu.umassmed.omega.dataNew.analysisRunElements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;

public abstract class OmegaAnalysisRun extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private final Date timeStamps;

	private OmegaExperimenter experimenter;

	// TODO aggiungere OmegaExperimenterGroup permissions

	private OmegaAlgorithmSpecification algorithmSpec;

	private List<OmegaAnalysisRun> analysisRuns;

	public OmegaAnalysisRun(final Long elementID) {
		super(elementID);

		this.timeStamps = Calendar.getInstance().getTime();

		this.experimenter = null;

		this.algorithmSpec = null;

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaAnalysisRun(final Long elementID, final OmegaExperimenter owner) {
		this(elementID);

		this.experimenter = owner;
	}

	public OmegaAnalysisRun(final Long elementID,
	        final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec) {
		this(elementID, owner);

		this.algorithmSpec = algorithmSpec;
	}

	public OmegaAnalysisRun(final Long elementID,
	        final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this(elementID, owner, algorithmSpec);

		this.analysisRuns = analysisRuns;
	}

	public Date getTimeStamps() {
		return this.timeStamps;
	}

	public OmegaExperimenter getExperimenter() {
		return this.experimenter;
	}

	public OmegaAlgorithmSpecification AlgorithmSpec() {
		return this.algorithmSpec;
	}

	@Override
	public List<OmegaAnalysisRun> getAnalysisRuns() {
		return this.analysisRuns;
	}

	@Override
	public void addAnalysisRun(final OmegaAnalysisRun analysisRun) {
		this.analysisRuns.add(analysisRun);
	}
}
