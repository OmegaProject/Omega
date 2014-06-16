package edu.umassmed.omega.dataNew.analysisRunElements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;

public abstract class OmegaAnalysisRun extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private final String name;

	private final Date timeStamps;

	private final OmegaExperimenter experimenter;

	// TODO aggiungere OmegaExperimenterGroup permissions

	private final OmegaAlgorithmSpecification algorithmSpec;

	private List<OmegaAnalysisRun> analysisRuns;

	public OmegaAnalysisRun(final Long elementID,
	        final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec) {
		super(elementID);

		this.timeStamps = Calendar.getInstance().getTime();

		this.experimenter = owner;

		this.algorithmSpec = algorithmSpec;

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();

		final StringBuffer nameBuf = new StringBuffer();
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd_hh-mm-aa");
		nameBuf.append(format.format(this.timeStamps));
		nameBuf.append("_");
		nameBuf.append(algorithmSpec.getAlgorithmInfo().getName());
		this.name = nameBuf.toString();
	}

	public OmegaAnalysisRun(final Long elementID,
	        final OmegaExperimenter owner,
	        final OmegaAlgorithmSpecification algorithmSpec,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this(elementID, owner, algorithmSpec);

		this.analysisRuns = analysisRuns;
	}

	public String getName() {
		return this.name;
	}

	public Date getTimeStamps() {
		return this.timeStamps;
	}

	public OmegaExperimenter getExperimenter() {
		return this.experimenter;
	}

	public OmegaAlgorithmSpecification getAlgorithmSpec() {
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
