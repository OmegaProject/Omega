/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.data.trajectoryElements;

public class OmegaParticle extends OmegaROI {

	private final Double intensity;
	private final Double probability;

	private final Double totalSignal;
	private final Integer numOfSignals;
	private final Double meanSignal;
	private final Double peakSignal;
	private final Double snr;
	private final Double meanBackground;
	private final Double meanNoise;
	private final Double m0;
	private final Double m2;

	public OmegaParticle(final int frameIndex, final double x, final double y) {
		super(frameIndex, x, y);

		this.intensity = null;
		this.probability = null;

		this.totalSignal = null;
		this.numOfSignals = null;
		this.meanSignal = null;
		this.peakSignal = null;
		this.snr = null;
		this.meanBackground = null;
		this.meanNoise = null;
		this.m0 = null;
		this.m2 = null;
	}

	public OmegaParticle(final int frameIndex, final double x, final double y,
	        final double intensity) {
		super(frameIndex, x, y);

		this.intensity = intensity;
		this.probability = null;

		this.totalSignal = null;
		this.numOfSignals = null;
		this.meanSignal = null;
		this.peakSignal = null;
		this.snr = null;
		this.meanBackground = null;
		this.meanNoise = null;
		this.m0 = null;
		this.m2 = null;
	}

	public OmegaParticle(final int frameIndex, final double x, final double y,
	        final double intensity, final double probability) {
		super(frameIndex, x, y);

		this.intensity = intensity;
		this.probability = probability;

		this.totalSignal = null;
		this.numOfSignals = null;
		this.meanSignal = null;
		this.peakSignal = null;
		this.snr = null;
		this.meanBackground = null;
		this.meanNoise = null;
		this.m0 = null;
		this.m2 = null;
	}

	public OmegaParticle(final int frameIndex, final double x, final double y,
	        final double totalSignal, final int numOfSignals,
	        final double meanSignal, final double peakSignal, final double snr,
	        final double meanBackground, final double meanNoise,
	        final double m0, final double m2) {
		super(frameIndex, x, y);

		this.intensity = null;
		this.probability = null;

		this.totalSignal = totalSignal;
		this.numOfSignals = numOfSignals;
		this.meanSignal = meanSignal;
		this.peakSignal = peakSignal;
		this.snr = snr;
		this.meanBackground = meanBackground;
		this.meanNoise = meanNoise;
		this.m0 = m0;
		this.m2 = m2;
	}

	public Double getIntensity() {
		return this.intensity;
	}

	public Double getProbability() {
		return this.probability;
	}

	public Double getTotalSignal() {
		return this.totalSignal;
	}

	public Integer getNumOfSignals() {
		return this.numOfSignals;
	}

	public Double getMeanSignal() {
		return this.meanSignal;
	}

	public Double getPeakSignal() {
		return this.peakSignal;
	}

	public Double getSNR() {
		return this.snr;
	}

	public Double getMeanBackground() {
		return this.meanBackground;
	}

	public Double getMeanNoise() {
		return this.meanNoise;
	}

	public Double getM0() {
		return this.m0;
	}

	public Double getM2() {
		return this.m2;
	}
}
