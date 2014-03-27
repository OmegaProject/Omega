package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaFrame extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private final String name;

	private List<OmegaAnalysisRun> analysisRuns;

	private int channel;
	private final Date timeStamps;

	public OmegaFrame(final Long elementID, final String name) {
		super(elementID);
		this.name = name;

		this.channel = -1;
		this.timeStamps = Calendar.getInstance().getTime();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaFrame(final Long elementID, final String name, final int channel) {
		this(elementID, name);

		this.channel = channel;
	}

	public OmegaFrame(final Long elementID, final String name,
	        final int channel, final List<OmegaAnalysisRun> analysisRuns) {
		this(elementID, name, channel);
		this.analysisRuns = analysisRuns;
	}

	public String getName() {
		return this.name;
	}

	public int getChannel() {
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
