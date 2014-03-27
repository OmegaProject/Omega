package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.core.gui.OmegaFrame;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaImagePixels extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private List<OmegaFrame> frames;

	private List<OmegaAnalysisRun> analysisRuns;

	private int sizeX, sizeY, sizeZ, sizeC, sizeT;

	public OmegaImagePixels(final Long elementID) {
		super(elementID);

		this.sizeX = -1;
		this.sizeY = -1;
		this.sizeZ = -1;
		this.sizeC = -1;
		this.sizeT = -1;

		this.frames = new ArrayList<OmegaFrame>();

		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final int sizeX,
	        final int sizeY) {
		this(elementID);

		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	public OmegaImagePixels(final Long elementID, final int sizeX,
	        final int sizeY, final int sizeZ, final int sizeC, final int sizeT) {
		this(elementID, sizeX, sizeY);

		this.sizeZ = sizeZ;
		this.sizeC = sizeC;
		this.sizeT = sizeT;
	}

	public OmegaImagePixels(final Long elementID, final List<OmegaFrame> frames) {
		this(elementID);

		this.frames = frames;
	}

	public OmegaImagePixels(final Long elementID, final List<OmegaFrame> frames,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this(elementID);

		this.frames = frames;
		this.analysisRuns = analysisRuns;
	}

	public OmegaImagePixels(final Long elementID, final int sizeX,
	        final int sizeY, final List<OmegaFrame> frames) {
		this(elementID, sizeX, sizeY);

		this.frames = frames;
	}

	public OmegaImagePixels(final Long elementID, final int sizeX,
	        final int sizeY, final List<OmegaFrame> frames,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this(elementID, sizeX, sizeY);

		this.frames = frames;
		this.analysisRuns = analysisRuns;
	}

	public OmegaImagePixels(final Long elementID, final int sizeX,
	        final int sizeY, final int sizeZ, final int sizeC, final int sizeT,
	        final List<OmegaFrame> frames) {
		this(elementID, sizeX, sizeY, sizeZ, sizeC, sizeT);

		this.frames = frames;
	}

	public OmegaImagePixels(final Long elementID, final int sizeX,
	        final int sizeY, final int sizeZ, final int sizeC, final int sizeT,
	        final List<OmegaFrame> frames,
	        final List<OmegaAnalysisRun> analysisRuns) {
		this(elementID, sizeX, sizeY, sizeZ, sizeC, sizeT);

		this.frames = frames;
		this.analysisRuns = analysisRuns;
	}

	public int getSizeX() {
		return this.sizeX;
	}

	public int getSizeY() {
		return this.sizeY;
	}

	public int getSizeZ() {
		return this.sizeZ;
	}

	public int getSizeC() {
		return this.sizeC;
	}

	public int getSizeT() {
		return this.sizeT;
	}

	public List<OmegaFrame> getFrames() {
		return this.frames;
	}

	public void addFrame(final OmegaFrame frame) {
		this.frames.add(frame);
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
