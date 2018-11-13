package edu.umassmed.omega.mosaicFeaturePointDetectionPlugin.runnable;

import ij.ImageStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import mosaic.core.detection.FeaturePointDetector;
import mosaic.core.detection.Particle;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;

public class MosaicFeaturePointDetectionWorker implements MosaicFeaturePointDetectionRunnable {
	
	private final OmegaImagePixels pixels;
	private OmegaPlane frame;
	private final List<OmegaROI> particles;
	private final int frameIndex;
	private final Integer radius;
	private final Double cutoff;
	private final Float percentile;
	private final Float threshold;
	private final Boolean percAbs;
	private final Integer channel, zSection;
	private Float globalMin, globalMax;
	private final boolean isDebugMode;
	private boolean isJobCompleted, isTerminated, frameAdded;
	
	private final ImageStack is;
	
	private final Map<OmegaROI, Map<String, Object>> values;

	public MosaicFeaturePointDetectionWorker(final ImageStack is, final int frameIndex,
			final Integer radius, final Double cutoff, final Float percentile,
			final Float threshold, final Boolean percAbs,
			final Integer channel, final Integer zSection) {
		this.isDebugMode = false;
		this.is = is;
		this.pixels = null;
		this.frame = null;
		this.frameIndex = frameIndex;

		this.radius = radius;
		this.cutoff = cutoff;
		this.threshold = threshold;
		this.percentile = percentile;
		this.percAbs = percAbs;
		this.channel = channel;
		this.zSection = zSection;
		this.globalMin = Float.MAX_VALUE;
		this.globalMax = 0F;
		this.particles = new ArrayList<OmegaROI>();
		this.values = new LinkedHashMap<OmegaROI, Map<String, Object>>();

		this.frameAdded = false;
	}
	
	public MosaicFeaturePointDetectionWorker(final ImageStack is, final OmegaImagePixels pixels,
			final int frameIndex, final Integer radius, final Double cutoff,
			final Float percentile, final Float threshold,
			final Boolean percAbs, final Integer channel, final Integer zSection) {
		this.isDebugMode = false;
		this.is = is;
		this.pixels = pixels;
		this.frame = null;
		this.frameIndex = frameIndex;

		this.radius = radius;
		this.cutoff = cutoff;
		this.threshold = threshold;
		this.percentile = percentile;
		this.percAbs = percAbs;
		this.channel = channel;
		this.zSection = zSection;
		this.globalMin = Float.MAX_VALUE;
		this.globalMax = 0F;
		this.particles = new ArrayList<OmegaROI>();
		this.values = new LinkedHashMap<OmegaROI, Map<String, Object>>();

		this.frameAdded = false;
	}

	public boolean isFrameAdded() {
		return this.frameAdded;
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
		if (this.pixels != null) {
			final List<OmegaPlane> frames = this.pixels.getFrames(this.channel,
					this.zSection);
			if (!frames.isEmpty() && (frames.size() > this.frameIndex)) {
				this.frame = frames.get(this.frameIndex);
			} else {
				this.frame = new OmegaPlane(this.frameIndex, this.channel,
						this.zSection);
				this.frame.setParentPixels(this.pixels);
				this.frameAdded = true;
			}
		} else {
			this.frame = new OmegaPlane(this.frameIndex, this.channel,
					this.zSection);
		}
		
		FeaturePointDetector fpd = new FeaturePointDetector(this.globalMax,
				this.globalMin);
		fpd.setDetectionParameters(this.cutoff, this.percentile, this.radius,
				this.threshold, this.percAbs);
		final Vector<Particle> mosaicParticles = fpd
				.featurePointDetection(this.is);
		
		if (this.isTerminated)
			return;
		
		for (final Particle p : mosaicParticles) {
			if (!p.special) {
				continue;
			}
			final int fi = this.frameIndex;
			final double x = p.getX();
			final double y = p.getY();
			final int x_i = (int) x;
			final int y_i = (int) y;
			final double intensity = this.is.getProcessor(1).get(x_i, y_i);
			final float m0 = p.m0;
			final float m1 = p.m1;
			final float m2 = p.m2;
			final float m3 = p.m3;
			final float m4 = p.m4;
			final float npscore = p.nonParticleDiscriminationScore;
			final Map<String, Object> particleValues = new LinkedHashMap<String, Object>();
			particleValues.put("m0", m0);
			particleValues.put("m1", m1);
			particleValues.put("m2", m2);
			particleValues.put("m3", m3);
			particleValues.put("m4", m4);
			particleValues.put("NPScore", npscore);
			Double physicalX = null;
			Double physicalY = null;
			if (this.pixels != null) {
				physicalX = this.pixels.getPhysicalSizeX();
				physicalY = this.pixels.getPhysicalSizeY();
			}
			Double realX = x, realY = y;
			if ((physicalX != null) && (physicalX != -1)) {
				realX *= physicalX;
			}
			if ((physicalY != null) && (physicalY != -1)) {
				realY *= physicalY;
			}
			final OmegaROI particle = new OmegaParticle(fi + 1, x, y, realX,
					realY, intensity);
			this.particles.add(particle);
			this.values.put(particle, particleValues);
		}
		fpd = null;
	}
	
	private void debugModeRun() {
		
	}

	public int getC() {
		return this.channel;
	}

	public int getZ() {
		return this.zSection;
	}
	
	@Override
	public boolean isJobCompleted() {
		return this.isJobCompleted;
	}
	
	@Override
	public void terminate() {
		this.isTerminated = true;
	}
	
	public OmegaPlane getFrame() {
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
