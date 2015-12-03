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
package main.java.edu.umassmed.omega.snrSbalzariniPlugin.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import main.java.edu.umassmed.omega.commons.OmegaLogFileManager;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaFrame;
import main.java.edu.umassmed.omega.commons.data.imageDBConnectionElements.OmegaGateway;
import main.java.edu.umassmed.omega.commons.data.trajectoryElements.OmegaROI;
import main.java.edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import main.java.edu.umassmed.omega.commons.utilities.OmegaImageUtilities;
import main.java.edu.umassmed.omega.commons.utilities.OmegaMathsUtilities;
import main.java.edu.umassmed.omega.snrSbalzariniPlugin.SNRConstants;

public class SNREstimator implements SNRRunnable {

	private static final String RUNNER = "SNR estimator service: ";
	private final OmegaMessageDisplayerPanelInterface displayerPanel;
	private boolean isJobCompleted, isTerminated;

	private final OmegaGateway gateway;
	private final OmegaFrame frame;
	private final List<OmegaROI> rois;

	private final int radius;
	private final double threshold;
	private final String method;

	private Double imageBGR;
	private Double imageNoise;
	private Double avgSNR, minSNR, maxSNR;
	private final Map<OmegaROI, Integer> localCenterSignals;
	private final Map<OmegaROI, Double> localMeanSignals;
	private final Map<OmegaROI, Integer> localSignalSizes;
	private final Map<OmegaROI, Integer> localPeakSignals;
	private final Map<OmegaROI, Double> localNoises;
	private final Map<OmegaROI, Double> localSNRs;
	private final Map<OmegaROI, Double> localBattaPoissonSNRs;

	public SNREstimator(
			final OmegaMessageDisplayerPanelInterface displayerPanel,
			final OmegaGateway gateway, final OmegaFrame frame,
			final List<OmegaROI> rois, final int radius,
			final double threshold, final String method) {
		this.displayerPanel = displayerPanel;
		this.isJobCompleted = false;

		this.gateway = gateway;
		this.frame = frame;
		this.rois = rois;

		this.radius = radius;
		this.threshold = threshold;
		this.method = method;

		this.avgSNR = null;
		this.minSNR = null;
		this.maxSNR = null;
		this.imageBGR = null;
		this.imageNoise = null;
		this.localCenterSignals = new LinkedHashMap<>();
		this.localMeanSignals = new LinkedHashMap<>();
		this.localSignalSizes = new LinkedHashMap<>();
		this.localPeakSignals = new LinkedHashMap<>();
		this.localNoises = new LinkedHashMap<>();
		this.localSNRs = new LinkedHashMap<>();
		this.localBattaPoissonSNRs = new LinkedHashMap<OmegaROI, Double>();
	}

	@Override
	public boolean isJobCompleted() {
		return this.isJobCompleted;
	}

	@Override
	public void run() {
		if (this.isTerminated)
			return;
		final long pixelsID = this.frame.getParentPixels().getElementID();
		final int width = this.frame.getParentPixels().getSizeX();
		final int height = this.frame.getParentPixels().getSizeY();
		final int z = this.frame.getZPlane();
		final int t = this.frame.getIndex();
		final int c = this.frame.getChannel();

		final int byteWidth = this.gateway.getByteWidth(pixelsID);
		final byte[] pixels = this.gateway.getImageData(pixelsID, z, t, c);
		final Integer[] image = OmegaImageUtilities.convertByteToIntegerImage(
				byteWidth, pixels);
		// data = OmegaImageUtilities.normalizeImage(data);

		final Integer[] minMax = OmegaMathsUtilities.getMinAndMax(image);
		final int min = minMax[0];
		final int max = minMax[1];

		final double thresh = ((max - min) * this.threshold) + min;
		final Integer[] smallerValues = OmegaMathsUtilities.getSmallerValue(
				image, thresh);
		this.imageBGR = OmegaMathsUtilities.mean(smallerValues);
		this.imageNoise = OmegaMathsUtilities.standardDeviationN(smallerValues);

		this.avgSNR = 0.0;
		this.minSNR = Double.MAX_VALUE;
		this.maxSNR = Double.MIN_VALUE;
		for (final OmegaROI roi : this.rois) {
			if (this.isTerminated)
				return;
			Double localMeanSignal = 0.0;
			final int x = (int) roi.getX();
			final int y = (int) roi.getY();
			final List<Integer> values = new ArrayList<>();
			for (int xP = x - this.radius; xP <= (x + this.radius); xP++) {
				if ((xP < 0) || (xP >= width)) {
					continue;
				}
				for (int yP = y - this.radius; yP <= (y + this.radius); yP++) {
					if ((yP < 0) || (yP >= height)) {
						continue;
					}
					final Integer val = image[(yP * width) + xP];
					values.add(val);
					localMeanSignal += val;
				}
			}
			final int counter = values.size();
			final Integer[] vals = new Integer[counter];
			for (int i = 0; i < counter; i++) {
				vals[i] = values.get(i);
			}
			localMeanSignal /= counter;
			final int localMaxSignal = OmegaMathsUtilities.getMax(vals);
			this.localCenterSignals.put(roi, image[(y * width) + x]);
			this.localPeakSignals.put(roi, localMaxSignal);
			this.localMeanSignals.put(roi, localMeanSignal);
			this.localSignalSizes.put(roi, counter);
			final double localNoise = Math.sqrt(localMaxSignal
					* ((this.imageNoise * this.imageNoise) / this.imageBGR));
			this.localNoises.put(roi, localNoise);
			Double localSNR = null;
			final Double battaPoissonSNR = 2 * (Math.sqrt(localMaxSignal) - Math
			        .sqrt(this.imageBGR));
			if (this.method
			        .equals(SNRConstants.PARAM_SNR_METHOD_BHATTACHARYYA_POISSON)) {
				localSNR = battaPoissonSNR;
			} else if (this.method
			        .equals(SNRConstants.PARAM_SNR_METHOD_BHATTACHARYYA_GAUSSIAN)) {
				localSNR = (2 * (localMaxSignal - this.imageBGR))
				        / (Math.sqrt(localMaxSignal) + Math.sqrt(this.imageBGR));
			} else {
				localSNR = (localMaxSignal - this.imageBGR) / localNoise;
			}
			this.avgSNR += localSNR;
			if (this.minSNR > localSNR) {
				this.minSNR = localSNR;
			}
			if (this.maxSNR < localSNR) {
				this.maxSNR = localSNR;
			}
			this.localSNRs.put(roi, localSNR);
			this.localBattaPoissonSNRs.put(roi, battaPoissonSNR);
		}
		this.avgSNR /= this.rois.size();
		this.isJobCompleted = true;
	}

	private void updateStatusSync(final String msg, final boolean ended) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					SNREstimator.this.displayerPanel
					.updateMessageStatus(new SNRMessageEvent(msg,
							SNREstimator.this, ended));
				}
			});
		} catch (final InvocationTargetException | InterruptedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	private void updateStatusAsync(final String msg, final boolean ended) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SNREstimator.this.displayerPanel
				.updateMessageStatus(new SNRMessageEvent(msg,
						SNREstimator.this, ended));
			}
		});
	}

	public void terminate() {
		this.isTerminated = true;
	}

	public OmegaFrame getFrame() {
		return this.frame;
	}

	public Double getImageNoise() {
		return this.imageNoise;
	}

	public Double getImageBackground() {
		return this.imageBGR;
	}

	public Double getAverageSNR() {
		return this.avgSNR;
	}

	public Double getMinimumSNR() {
		return this.minSNR;
	}

	public Double getMaximumSNR() {
		return this.maxSNR;
	}

	public Map<OmegaROI, Integer> getLocalCenterSignals() {
		return this.localCenterSignals;
	}

	public Map<OmegaROI, Double> getLocalMeanSignals() {
		return this.localMeanSignals;
	}

	public Map<OmegaROI, Integer> getLocalSignalSizes() {
		return this.localSignalSizes;
	}

	public Map<OmegaROI, Integer> getLocalPeakSignals() {
		return this.localPeakSignals;
	}

	public Map<OmegaROI, Double> getLocalNoises() {
		return this.localNoises;
	}

	public Map<OmegaROI, Double> getLocalSNRs() {
		return this.localSNRs;
	}

	public Map<OmegaROI, Double> getLocalBhattacharyyaPoissonSNRs() {
		return this.localBattaPoissonSNRs;
	}
}