package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaFrame extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private final Integer index;

	private List<OmegaAnalysisRun> analysisRuns;

	private Integer channel;
	private final Date timeStamps;

	public OmegaFrame(final Long elementID, final Integer index) {
		super(elementID);
		this.index = index;

		this.channel = -1;
		this.timeStamps = Calendar.getInstance().getTime();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaFrame(final Long elementID, final Integer index,
	        final Integer channel) {
		this(elementID, index);

		this.channel = channel;
	}

	public OmegaFrame(final Long elementID, final Integer index,
	        final Integer channel, final List<OmegaAnalysisRun> analysisRuns) {
		this(elementID, index, channel);
		this.analysisRuns = analysisRuns;
	}

	public Integer getIndex() {
		return this.index;
	}

	public Integer getChannel() {
		return this.channel;
	}

	public Date getTimeStamps() {
		return this.timeStamps;
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
