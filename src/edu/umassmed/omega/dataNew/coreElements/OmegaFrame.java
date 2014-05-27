package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaFrame extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private OmegaImagePixels pixels;

	private final Integer index;

	private final List<OmegaAnalysisRun> analysisRuns;

	private Integer zPlane, channel;
	private final Date timeStamps;

	public OmegaFrame(final Long elementID, final Integer index) {
		super(elementID);

		this.pixels = null;

		this.index = index;

		this.zPlane = -1;
		this.channel = -1;

		this.timeStamps = Calendar.getInstance().getTime();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaFrame(final Long elementID, final Integer index,
	        final Integer channel, final Integer zPlane) {
		super(elementID);

		this.pixels = null;

		this.index = index;

		this.zPlane = zPlane;
		this.channel = channel;

		this.timeStamps = Calendar.getInstance().getTime();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaFrame(final Long elementID, final Integer index,
	        final Integer channel, final List<OmegaAnalysisRun> analysisRuns) {
		super(elementID);

		this.pixels = null;

		this.index = index;

		this.channel = channel;

		this.timeStamps = Calendar.getInstance().getTime();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public void setParentPixels(final OmegaImagePixels pixels) {
		this.pixels = pixels;
	}

	public OmegaImagePixels getParentPixels() {
		return this.pixels;
	}

	public Integer getIndex() {
		return this.index;
	}

	public Integer getChannel() {
		return this.channel;
	}

	public void setChannel(final Integer channel) {
		this.channel = channel;
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
