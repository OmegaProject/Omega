package edu.umassmed.omega.dataNew.coreElements;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.dataNew.analysisRunElements.OmegaAnalysisRunContainer;

public class OmegaImagePixels extends OmegaElement implements
        OmegaAnalysisRunContainer {

	private final String pixelsType;

	private final double sizeX, sizeY, sizeZ, sizeC, sizeT;

	private final List<OmegaFrame> frames;

	private final List<OmegaAnalysisRun> analysisRuns;

	public OmegaImagePixels(final Long elementID, final String pixelsType) {
		super(elementID);

		this.pixelsType = pixelsType;

		this.sizeX = -1;
		this.sizeY = -1;
		this.sizeZ = -1;
		this.sizeC = -1;
		this.sizeT = -1;

		this.frames = new ArrayList<OmegaFrame>();
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final double sizeX, final double sizeY) {
		super(elementID);

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;

		this.sizeZ = -1;
		this.sizeC = -1;
		this.sizeT = -1;

		this.frames = new ArrayList<OmegaFrame>();
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final double sizeX, final double sizeY, final double sizeZ,
	        final double sizeC, final double sizeT) {
		super(elementID);

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.sizeC = sizeC;
		this.sizeT = sizeT;

		this.frames = new ArrayList<OmegaFrame>();
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final List<OmegaFrame> frames) {
		super(elementID);

		this.pixelsType = pixelsType;

		this.sizeX = -1;
		this.sizeY = -1;
		this.sizeZ = -1;
		this.sizeC = -1;
		this.sizeT = -1;

		this.frames = frames;
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final List<OmegaFrame> frames,
	        final List<OmegaAnalysisRun> analysisRuns) {
		super(elementID);

		this.pixelsType = pixelsType;

		this.sizeX = -1;
		this.sizeY = -1;
		this.sizeZ = -1;
		this.sizeC = -1;
		this.sizeT = -1;

		this.frames = frames;
		this.analysisRuns = analysisRuns;
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final double sizeX, final double sizeY,
	        final List<OmegaFrame> frames) {
		super(elementID);

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;

		this.sizeZ = -1;
		this.sizeC = -1;
		this.sizeT = -1;

		this.frames = frames;
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final double sizeX, final double sizeY,
	        final List<OmegaFrame> frames,
	        final List<OmegaAnalysisRun> analysisRuns) {
		super(elementID);

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;

		this.sizeZ = -1;
		this.sizeC = -1;
		this.sizeT = -1;

		this.frames = frames;
		this.analysisRuns = analysisRuns;
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final double sizeX, final double sizeY, final double sizeZ,
	        final double sizeC, final double sizeT,
	        final List<OmegaFrame> frames) {
		super(elementID);

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.sizeC = sizeC;
		this.sizeT = sizeT;

		this.frames = frames;
		this.analysisRuns = new ArrayList<OmegaAnalysisRun>();
	}

	public OmegaImagePixels(final Long elementID, final String pixelsType,
	        final double sizeX, final double sizeY, final double sizeZ,
	        final double sizeC, final double sizeT,
	        final List<OmegaFrame> frames,
	        final List<OmegaAnalysisRun> analysisRuns) {
		super(elementID);

		this.pixelsType = pixelsType;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.sizeC = sizeC;
		this.sizeT = sizeT;

		this.frames = frames;
		this.analysisRuns = analysisRuns;
	}

	public String getPixelsType() {
		return this.pixelsType;
	}

	public double getSizeX() {
		return this.sizeX;
	}

	public double getSizeY() {
		return this.sizeY;
	}

	public double getSizeZ() {
		return this.sizeZ;
	}

	public double getSizeC() {
		return this.sizeC;
	}

	public double getSizeT() {
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
