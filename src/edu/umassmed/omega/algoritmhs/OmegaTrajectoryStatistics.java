package edu.umassmed.omega.algoritmhs;

import java.util.ArrayList;
import java.util.List;

import edu.umassmed.omega.commons.utilities.OmegaMathsUtilities;

public class OmegaTrajectoryStatistics {
	public static final Integer MAX_NU = 6;

	private final List<OmegaAlgorithmPoint> points;
	private final List<OmegaAlgorithmSegment> segments;
	private final Double deltaT;

	private final Integer windowDivisor;

	private Integer[] bounds = null;
	private final Double[][] d;

	public OmegaTrajectoryStatistics(final List<OmegaAlgorithmPoint> points,
	        final Double deltaT, final Integer windowDivisor) {
		this.points = new ArrayList<OmegaAlgorithmPoint>(points);
		this.segments = null;
		this.bounds = null;
		this.deltaT = deltaT;
		this.windowDivisor = windowDivisor;
		this.d = new Double[1 + ((this.getM()) / this.windowDivisor)][];
		this.norms();
	}

	public OmegaTrajectoryStatistics(final List<OmegaAlgorithmPoint> points,
	        final Double deltaT, final Integer windowDivisor,
	        final List<OmegaAlgorithmSegment> segments) {
		this.points = new ArrayList<OmegaAlgorithmPoint>(points);
		this.segments = new ArrayList<OmegaAlgorithmSegment>(segments);
		this.bounds = new Integer[segments.size() + 1];
		this.calculateBounds();
		this.deltaT = deltaT;
		this.windowDivisor = windowDivisor;
		this.d = new Double[1 + ((this.getM()) / this.windowDivisor)][];
		this.norms();
	}

	private void calculateBounds() {
		this.bounds[0] = 0;
		for (int i = 0; i < this.segments.size(); i++) {
			final OmegaAlgorithmSegment segm = this.segments.get(i);
			this.bounds[i + 1] = segm.getTo().getFrameIndex();
		}
	}

	// TODO name meaningful enough?
	private void norms() {
		for (int deltaN = 1; deltaN < this.d.length; ++deltaN) {
			final Double[] normsDeltaN = new Double[this.getM() - deltaN];
			for (int i = 0; i < normsDeltaN.length; ++i) {
				final OmegaAlgorithmPoint p0 = this.points.get(i);
				final OmegaAlgorithmPoint p1 = this.points.get(i + deltaN);

				normsDeltaN[i] = StrictMath.hypot(p1.getX() - p0.getX(),
				        p1.getY() - p0.getY());
			}
			this.d[deltaN] = normsDeltaN;
		}
	}

	private Double getDeltaT() {
		return this.deltaT;
	}

	private Integer getM() {
		return this.points.size();
	}

	private Integer getM(final Integer segment) {
		return this.intervals(segment) + 1;
	}

	private Integer intervals() {
		return this.getM() - 1;
	}

	private Integer intervals(final Integer segment) {
		return this.to(segment) - this.from(segment);
	}

	private Double duration() {
		return this.intervals() * this.deltaT;
	}

	private Double duration(final Integer segment) {
		return this.intervals(segment) * this.deltaT;
	}

	private Integer from(final Integer segment) {
		return this.bounds[segment];
	}

	private Integer to(final Integer segment) {
		return this.bounds[segment + 1];
	}

	public double getMu(final Integer nu, final Integer deltaN,
	        final Integer from, final Integer to) {
		Double sum = 0.0;
		for (int i = from; i <= (to - deltaN); ++i) {
			sum += StrictMath.pow(this.d[deltaN][i], nu);
		}
		final Integer div = (to + 1) - from - deltaN;
		final Double val = sum / div;
		return val;
	}

	public Double getMu(final Integer nu, final Integer deltaN) {
		return this.getMu(nu, deltaN, 0, this.intervals());
	}

	public Double getMu(final Integer nu, final Integer deltaN,
	        final Integer segment) {
		return this.getMu(nu, deltaN, this.from(segment), this.to(segment));
	}

	public Double[] getInstantVelocities() {
		final Double[] v = new Double[this.points.size()];

		v[0] = this.d[1][0] / this.deltaT;
		v[v.length - 1] = this.d[1][v.length - 2] / this.deltaT;

		for (int i = 1; i < (v.length - 1); i++) {
			v[i] = (this.d[1][i] + this.d[1][i - 1]) / (2.0 * this.deltaT);
		}

		return v;
	}

	// TODO double check everything down here

	public Double[] getLogDeltaT(final Integer from, final Integer to) {
		final Integer M = (to + 1) - from;
		final Double logDeltaT = StrictMath.log(this.deltaT);
		final Integer maxDeltaN = StrictMath.max(M / 3, 2);
		final Double[] logDeltaTValues = new Double[maxDeltaN + 1];
		for (Integer deltaN = 1; deltaN <= maxDeltaN; ++deltaN) {
			logDeltaTValues[deltaN] = logDeltaT + StrictMath.log(deltaN);
		}
		return logDeltaTValues;
	}

	public Double[] getLogDeltaT() {
		return this.getLogDeltaT(0, this.intervals());
	}

	public Double[] getLogDeltaT(final Integer segment) {
		return this.getLogDeltaT(this.from(segment), this.to(segment));
	}

	public Double[] getLogMu(final Integer nu, final Integer from,
	        final Integer to) {
		final Integer M = (to + 1) - from;
		StrictMath.log(this.deltaT);
		final Integer maxDeltaN = StrictMath.max(M / 3, 2);
		final Double[] logMu = new Double[maxDeltaN + 1];
		for (Integer deltaN = 1; deltaN <= maxDeltaN; ++deltaN) {
			logMu[deltaN] = StrictMath.log(this.getMu(nu, deltaN, from, to));
		}

		return logMu;
	}

	public Double[] getLogMu(final Integer nu) {
		return this.getLogMu(nu, 0, this.intervals());
	}

	public Double[] getLogMu(final Integer nu, final Integer segment) {
		return this.getLogMu(nu, this.from(segment), this.to(segment));
	}

	public Double[] getGammaAndD(final Integer nu, final Integer from,
	        final Integer to) {
		final Double[] logDeltaT = this.getLogDeltaT(from, to);
		final Double[] logMu = this.getLogMu(nu, from, to);

		final Integer M = (to + 1) - from;
		final Integer maxDeltaN = StrictMath.max(M / 3, 2);
		final Double[] fit = OmegaMathsUtilities.linearFit(logDeltaT, logMu, 1,
		        maxDeltaN);
		final Double D = StrictMath.exp(fit[1]) / (2.0 * nu);

		return new Double[] { fit[0], fit[1], fit[2], D };
	}

	public Double[] getGammaAndD(final Integer nu) {
		return this.getGammaAndD(nu, 0, this.intervals());
	}

	public Double[] getGammaAndD(final Integer nu, final Integer segment) {
		return this.getGammaAndD(nu, this.from(segment), this.to(segment));
	}

	public Double[][] getNuAndGamma(final Integer from, final Integer to) {
		final Double[] gamma = new Double[OmegaTrajectoryStatistics.MAX_NU + 1];
		final Double[] nu = new Double[OmegaTrajectoryStatistics.MAX_NU + 1];
		for (Integer ny = 0; ny <= OmegaTrajectoryStatistics.MAX_NU; ++ny) {
			nu[ny] = Double.valueOf(ny);
			gamma[ny] = this.getGammaAndD(ny, from, to)[0];
		}
		return new Double[][] { nu, gamma };
	}

	public Double[] getSMSS(final Double[][] nuAndGamma) {
		return OmegaMathsUtilities.linearFit(nuAndGamma[0], nuAndGamma[1]);
	}

	public Double[] getSMSS(final Integer from, final Integer to) {
		final Double[][] nuAndGamma = this.getNuAndGamma(from, to);
		return OmegaMathsUtilities.linearFit(nuAndGamma[0], nuAndGamma[1]);
	}

	public Double[] getSMSS() {
		final Double[][] nuAndGamma = this.getNuAndGamma(0, this.intervals());
		return OmegaMathsUtilities.linearFit(nuAndGamma[0], nuAndGamma[1]);
	}

	public Double[] getSMSS(final Integer segment) {
		final Double[][] nuAndGamma = this.getNuAndGamma(this.from(segment),
		        this.to(segment));
		return OmegaMathsUtilities.linearFit(nuAndGamma[0], nuAndGamma[1]);
	}
}
