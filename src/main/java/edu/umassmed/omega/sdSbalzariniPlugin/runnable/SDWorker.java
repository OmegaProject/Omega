package edu.umassmed.omega.sdSbalzariniPlugin.runnable;

import ij.ImageStack;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import mosaic.core.detection.FeaturePointDetector;
import mosaic.core.detection.Particle;
import edu.umassmed.omega.commons.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.commons.data.coreElements.OmegaPlane;
import edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaParticle;
import edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import edu.umassmed.omega.commons.utilities.OmegaImageUtilities;

public class SDWorker implements SDRunnable {
	
	private final OmegaGateway gateway;
	private final OmegaImagePixels pixels;
	private OmegaPlane frame;
	private final List<OmegaROI> particles;
	private final int frameIndex;
	private final Integer radius;
	private final Double cutoff;
	private final Float percentile;
	private final Boolean percAbs;
	private final Integer channel, zSection;
	private Float globalMin, globalMax;
	private final boolean isDebugMode;
	private boolean isJobCompleted, isTerminated, isLoading;
	
	private ImageProcessor ip;
	
	private final Map<OmegaROI, Map<String, Object>> values;
	
	public SDWorker(final OmegaGateway gateway, final OmegaImagePixels pixels,
			final int frameIndex, final Integer radius, final Double cutoff,
			final Float percentile, final Boolean percAbs,
			final Integer channel, final Integer zSection) {
		this.isDebugMode = false;
		this.isLoading = true;
		this.gateway = gateway;
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
		if (this.isLoading) {
			this.normalLoadingModeRun();
		} else {
			this.normalProcessingModeRun();
		}
	}
	
	private void normalLoadingModeRun() {
		final Long pixelsID = this.pixels.getOmeroId();
		final int sizeX = this.pixels.getSizeX();
		final int sizeY = this.pixels.getSizeY();
		final int byteWidth = this.gateway.getByteWidth(pixelsID);
		final List<OmegaPlane> frames = this.pixels.getFrames(this.channel,
				this.zSection);
		if (!frames.isEmpty() && (frames.size() > this.frameIndex)) {
			this.frame = frames.get(this.frameIndex);
		} else {
			this.frame = new OmegaPlane(this.frameIndex, this.channel,
					this.zSection);
			this.frame.setParentPixels(this.pixels);
			// this.pixels.addFrame(this.channel, this.zSection, this.frame);
		}
		final byte[] pixels = this.gateway.getImageData(pixelsID,
				this.zSection, this.frameIndex, this.channel);
		final int[] values = OmegaImageUtilities.convertByteToIntImage(
				byteWidth, pixels);
		this.ip = new ColorProcessor(sizeX, sizeY, values);
	}
	
	private void normalProcessingModeRun() {
		final Long pixelsID = this.pixels.getOmeroId();
		final int sizeX = this.pixels.getSizeX();
		final int sizeY = this.pixels.getSizeY();
		this.gateway.getByteWidth(pixelsID);
		final ImageStack is = new ImageStack(sizeX, sizeY);
		is.addSlice(this.ip);
		
		if (this.isTerminated)
			return;
		
		final FeaturePointDetector fpd = new FeaturePointDetector(
				this.globalMax, this.globalMin);
		fpd.setDetectionParameters(this.cutoff, this.percentile, this.radius,
				this.percentile * 100, this.percAbs);
		final Vector<Particle> mosaicParticles = fpd.featurePointDetection(is);
		
		if (this.isTerminated)
			return;

		for (final Particle p : mosaicParticles) {
			final int fi = this.frameIndex;
			final double x = p.getX();
			final double y = p.getY();
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
	
	public OmegaPlane getFrame() {
		return this.frame;
	}
	
	public int getIndex() {
		return this.frameIndex;
	}
	
	public ImageProcessor getProcessor() {
		return this.ip;
	}
	
	public void setGlobalMin(final Float globalMin) {
		this.globalMin = globalMin;
	}
	
	public void setGlobalMax(final Float globalMax) {
		this.globalMax = globalMax;
	}
	
	public void setLoadingEnded() {
		this.isLoading = false;
	}
	
	public Map<OmegaROI, Map<String, Object>> getParticlesAdditionalValues() {
		return this.values;
	}
	
	public List<OmegaROI> getResultingParticles() {
		return this.particles;
	}
}
