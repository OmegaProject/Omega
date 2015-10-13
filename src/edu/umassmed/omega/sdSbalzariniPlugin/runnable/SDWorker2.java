package edu.umassmed.omega.sdSbalzariniPlugin.runnable;

import ij.ImageStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import mosaic.core.detection.FeaturePointDetector;
import mosaic.core.detection.MyFrame;
import mosaic.core.detection.Particle;
import edu.umassmed.omega.commons.data.coreElements.OmegaFrame;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;

public class SDWorker2 implements SDRunnable {

	private final OmegaImagePixels pixels;
	private OmegaFrame frame;
	private final List<OmegaROI> particles;
	private final int frameIndex;
	private final Integer radius;
	private final Double cutoff;
	private final Float percentile;
	private final Boolean percAbs;
	private final Integer channel, zSection;
	private Float globalMin, globalMax;
	private final boolean isDebugMode;
	private boolean isJobCompleted, isTerminated;

	private final ImageStack is;

	private final Map<OmegaROI, Map<String, Object>> values;

	public SDWorker2(final ImageStack is, final OmegaImagePixels pixels,
	        final int frameIndex, final Integer radius, final Double cutoff,
	        final Float percentile, final Boolean percAbs,
			final Integer channel, final Integer zSection) {
		this.isDebugMode = false;
		this.is = is;
		this.pixels = pixels;
		this.frameIndex = frameIndex;
		this.radius = radius;
		this.cutoff = cutoff;
		this.percentile = percentile;
		this.percAbs = percAbs;
		this.channel = channel;
		this.zSection = zSection;
		this.globalMin = Float.MAX_VALUE;
		this.globalMax = 0F;
		this.particles = new ArrayList<OmegaROI>();
		this.values = new LinkedHashMap<OmegaROI, Map<String, Object>>();
	}

	@Override
	public void run() {
		Thread.currentThread().setName(
		        "Mosaic2D_SpotDetector_SDWorker_" + this.frameIndex);
		if (this.isDebugMode) {
			this.debugModeRun();
		} else {
			this.normalModeRun();
		}

		this.isJobCompleted = true;
	}

	private void normalModeRun() {

		this.normalProcessingModeRun();
	}

	private void normalProcessingModeRun() {
		final MyFrame mosaicFrame = new MyFrame(this.is, this.frameIndex, 0);
		final FeaturePointDetector fpd = new FeaturePointDetector(
		        this.globalMax, this.globalMin);
		fpd.setUserDefinedParameters(this.cutoff, this.percentile, this.radius,
				this.percentile * 100, this.percAbs);
		fpd.featurePointDetection(mosaicFrame);

		if (this.isTerminated)
			return;

		final Vector<Particle> mosaicParticles = mosaicFrame.getParticles();
		for (final Particle p : mosaicParticles) {
			final int fi = p.getFrame();
			final double x = p.getCoord_X();
			final double y = p.getCoord_Y();
			final double intensity = p.getIntensity();
			final float m0 = p.m0;
			final float m1 = p.m1;
			final float m2 = p.m2;
			final float m3 = p.m3;
			final float m4 = p.m4;
			final Map<String, Object> particleValues = new LinkedHashMap<String, Object>();
			particleValues.put("m0", m0);
			particleValues.put("m1", m1);
			particleValues.put("m2", m2);
			particleValues.put("m3", m3);
			particleValues.put("m4", m4);
			final OmegaROI particle = new OmegaParticle(fi, x, y, intensity);
			this.particles.add(particle);
			this.values.put(particle, particleValues);
		}
	}

	private void debugModeRun() {

	}

	@Override
	public boolean isJobCompleted() {
		return this.isJobCompleted;
	}

	@Override
	public void terminate() {
		this.isTerminated = true;
	}

	public OmegaFrame getFrame() {
		return this.frame;
	}

	public int getIndex() {
		return this.frameIndex;
	}

	public void setGlobalMin(final Float globalMin) {
		this.globalMin = globalMin;
	}

	public void setGlobalMax(final Float globalMax) {
		this.globalMax = globalMax;
	}

	public Map<OmegaROI, Map<String, Object>> getParticlesAdditionalValues() {
		return this.values;
	}

	public List<OmegaROI> getResultingParticles() {
		return this.particles;
	}
}
