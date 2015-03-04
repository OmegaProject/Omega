package edu.umassmed.omega.commons.libraries;

import edu.umassmed.omega.commons.utilities.OmegaMathsUtilities;
import edu.umassmed.omega.data.trajectoryElements.OmegaSegment;

public class OmegaDiffusivityLibrary {

	public static final int MAX_NU = 6;

	public static int MAX_DELTA_T_DIV = 10;

	private static Double[][] computeEuclideanNorms(final Double[] x,
	        final Double[] y, final int windowDivisor) {
		final int m = x.length;
		final Double[][] norms = new Double[1 + (m / windowDivisor)][];
		for (int Delta_n = 1; Delta_n < norms.length; ++Delta_n) {
			final Double[] norms_Delta_n = new Double[m - Delta_n];
			for (int i = 0; i < norms_Delta_n.length; ++i) {
				norms_Delta_n[i] = StrictMath.hypot(x[i + Delta_n] - x[i], y[i
				        + Delta_n]
				        - y[i]);
			}
			norms[Delta_n] = norms_Delta_n;
		}
		return norms;
	}

	public static Double computeMu(final Double[][] euclideanNorms,
	        final int windowDivisor, final int nu, final int Delta_n,
	        final int from, final int to) {
		double tot = 0.0;
		for (int i = from; i <= (to - Delta_n); ++i) {
			tot += StrictMath.pow(euclideanNorms[Delta_n][i], nu);
		}
		return tot / ((to + 1) - from - Delta_n);
	}

	// public static Double computeMu(final Double[][] euclideanNorms,
	// final int windowDivisor, final int nu, final int Delta_n) {
	// return OmegaDiffusivityLibrary.computeMu(euclideanNorms, windowDivisor,
	// nu, Delta_n, 0, x.length - 1);
	// }

	public static double computeMu(final Double[][] euclideanNorms,
	        final int windowDivisor, final int nu, final int Delta_n,
	        final OmegaSegment segment) {
		// TODO check if its correct to use segments frame index in this
		// position
		// TODO check if segments endingROI is right or has to be -1
		final int from = segment.getStartingROI().getFrameIndex();
		final int to = segment.getEndingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeMu(euclideanNorms, windowDivisor,
		        nu, Delta_n, from, to);
	}

	public static Integer getMaxDeltaN(final int m, final int windowsDivisor) {
		final Integer max_Delta_n = StrictMath.max(m / windowsDivisor, 2);
		return max_Delta_n;
	}

	public static Double[] computeDeltaNDeltaT(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int Delta_t, final int from, final int to) {
		final Integer m = to - from;
		final int max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] delta_t = new Double[max_Delta_n + 1];
		for (int Delta_n = 1; Delta_n <= max_Delta_n; ++Delta_n) {
			delta_t[Delta_n] = (double) Delta_t * (double) Delta_n;
		}
		return delta_t;
	}

	public static Double[] computeDeltaNDeltaT(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int Delta_t) {
		return OmegaDiffusivityLibrary.computeDeltaNDeltaT(x, y, windowDivisor,
		        nu, Delta_t, 0, x.length - 1);
	}

	public static Double[] computeDeltaNDeltaT(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int Delta_t, final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeDeltaNDeltaT(x, y, windowDivisor,
		        nu, Delta_t, from, to);
	}

	public static Double[] computeDeltaNLogDeltaT(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int Delta_t, final int from, final int to) {
		final Integer m = to - from;
		final Double log_Delta_t = StrictMath.log(Delta_t);
		final int max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] log_delta_t = new Double[max_Delta_n + 1];
		for (int Delta_n = 1; Delta_n <= max_Delta_n; ++Delta_n) {
			log_delta_t[Delta_n] = log_Delta_t + StrictMath.log(Delta_n);
		}
		return log_delta_t;
	}

	public static Double[] computeDeltaNLogDeltaT(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int Delta_t) {
		return OmegaDiffusivityLibrary.computeDeltaNLogDeltaT(x, y,
		        windowDivisor, nu, Delta_t, 0, x.length - 1);
	}

	public static Double[] computeDeltaNLogDeltaT(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int Delta_t, final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeDeltaNLogDeltaT(x, y,
		        windowDivisor, nu, Delta_t, from, to);
	}

	public static Double[] computeDeltaNMu(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int from, final int to) {
		final Integer m = to - from;
		final Integer max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] mu = new Double[max_Delta_n];
		final Double[][] norms = OmegaDiffusivityLibrary.computeEuclideanNorms(
		        x, y, windowDivisor);
		for (int Delta_n = 1; Delta_n <= max_Delta_n; ++Delta_n) {
			mu[Delta_n] = OmegaDiffusivityLibrary.computeMu(norms,
			        windowDivisor, nu, Delta_n, from, to);
		}
		return mu;
	}

	public static Double[] computeDeltaNMu(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu) {
		return OmegaDiffusivityLibrary.computeDeltaNMu(x, y, windowDivisor, nu,
		        0, x.length - 1);
	}

	public static Double[] computeDeltaNMu(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeDeltaNMu(x, y, windowDivisor, nu,
		        from, to);
	}

	public static Double[] computeDeltaNLogMu(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int from, final int to) {
		final Integer m = to - from;
		final Integer max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] log_mu = new Double[max_Delta_n];
		final Double[][] norms = OmegaDiffusivityLibrary.computeEuclideanNorms(
		        x, y, windowDivisor);
		for (int Delta_n = 1; Delta_n <= max_Delta_n; ++Delta_n) {
			log_mu[Delta_n] = StrictMath.log(OmegaDiffusivityLibrary.computeMu(
			        norms, windowDivisor, nu, Delta_n, from, to));
		}
		return log_mu;
	}

	public static Double[] computeDeltaNLogMu(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu) {
		return OmegaDiffusivityLibrary.computeDeltaNLogMu(x, y, windowDivisor,
		        nu, 0, x.length - 1);
	}

	public static Double[] computeDeltaNLogMu(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeDeltaNLogMu(x, y, windowDivisor,
		        nu, from, to);
	}

	public static Double[] computeGamma(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int Delta_t,
	        final int from, final int to) {
		final int m = to - from;
		final Integer max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] delta_t = OmegaDiffusivityLibrary.computeDeltaNDeltaT(x,
		        y, windowDivisor, nu, Delta_t, from, to);
		final Double[] mu = OmegaDiffusivityLibrary.computeDeltaNMu(x, y,
		        windowDivisor, nu, from, to);
		final Double[] fit = OmegaMathsUtilities.linearFit(delta_t, mu, 1,
		        max_Delta_n);
		return new Double[] { fit[0], fit[1], fit[2] };
	}

	public static Double[] computeGamma(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int Delta_t) {
		return OmegaDiffusivityLibrary.computeGamma(x, y, windowDivisor, nu,
		        Delta_t, 0, x.length - 1);
	}

	public static Double[] computeGamma(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int Delta_t,
	        final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeGamma(x, y, windowDivisor, nu,
		        Delta_t, from, to);
	}

	public static Double[] computeGammaFromLog(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int Delta_t, final int from, final int to) {
		final int m = to - from;
		final Integer max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] log_delta_t = OmegaDiffusivityLibrary
		        .computeDeltaNLogDeltaT(x, y, windowDivisor, nu, Delta_t, from,
		                to);
		final Double[] log_mu = OmegaDiffusivityLibrary.computeDeltaNLogMu(x,
		        y, windowDivisor, nu, from, to);
		final Double[] fit = OmegaMathsUtilities.linearFit(log_delta_t, log_mu,
		        1, max_Delta_n);
		return new Double[] { fit[0], fit[1], fit[2] };
	}

	public static Double[] computeGammaFromLog(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int Delta_t) {
		return OmegaDiffusivityLibrary.computeGammaFromLog(x, y, windowDivisor,
		        nu, Delta_t, 0, x.length - 1);
	}

	public static Double[] computeGammaFromLog(final Double[] x,
	        final Double[] y, final int windowDivisor, final int nu,
	        final int Delta_t, final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeGammaFromLog(x, y, windowDivisor,
		        nu, Delta_t, from, to);
	}

	public static Double computeD(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int Delta_t,
	        final int from, final int to) {
		final int m = to - from;
		final Integer max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] delta_t = OmegaDiffusivityLibrary.computeDeltaNDeltaT(x,
		        y, windowDivisor, nu, Delta_t, from, to);
		final Double[] mu = OmegaDiffusivityLibrary.computeDeltaNMu(x, y,
		        windowDivisor, nu, from, to);
		final Double[] fit = OmegaMathsUtilities.linearFit(delta_t, mu, 1,
		        max_Delta_n);
		final Double D = StrictMath.exp(fit[1]) / (2.0 * nu);
		return D;
	}

	public static Double computeD(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int Delta_t) {
		return OmegaDiffusivityLibrary.computeDFromLog(x, y, windowDivisor, nu,
		        Delta_t, 0, x.length - 1);
	}

	public static Double computeD(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int Delta_t,
	        final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeDFromLog(x, y, windowDivisor, nu,
		        Delta_t, from, to);
	}

	public static Double computeDFromLog(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int Delta_t,
	        final int from, final int to) {
		final int m = to - from;
		final Integer max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] log_delta_t = OmegaDiffusivityLibrary
		        .computeDeltaNLogDeltaT(x, y, windowDivisor, nu, Delta_t, from,
		                to);
		final Double[] log_mu = OmegaDiffusivityLibrary.computeDeltaNLogMu(x,
		        y, windowDivisor, nu, from, to);
		final Double[] fit = OmegaMathsUtilities.linearFit(log_delta_t, log_mu,
		        1, max_Delta_n);
		final Double D = StrictMath.exp(fit[1]) / (2.0 * nu);
		return D;
	}

	public static Double computeDFromLog(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int Delta_t) {
		return OmegaDiffusivityLibrary.computeDFromLog(x, y, windowDivisor, nu,
		        Delta_t, 0, x.length - 1);
	}

	public static Double computeDFromLog(final Double[] x, final Double[] y,
	        final int windowDivisor, final int nu, final int Delta_t,
	        final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeDFromLog(x, y, windowDivisor, nu,
		        Delta_t, from, to);
	}

	public static Double[][] computeDeltaNDeltaT_DeltaNMu_GammaD(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int nu, final int Delta_t, final int from, final int to) {
		final int m = to - from;
		final int max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] delta_t = new Double[max_Delta_n + 1];
		final Double[] mu = new Double[delta_t.length];
		final Double[][] norms = OmegaDiffusivityLibrary.computeEuclideanNorms(
		        x, y, windowDivisor);
		for (int Delta_n = 1; Delta_n <= max_Delta_n; ++Delta_n) {
			mu[Delta_n] = OmegaDiffusivityLibrary.computeMu(norms,
			        windowDivisor, nu, Delta_n, from, to);
			delta_t[Delta_n] = (double) Delta_t * (double) Delta_n;
		}
		final Double[] fits = OmegaMathsUtilities.linearFit(delta_t, mu, 1,
		        max_Delta_n);
		final Double D = StrictMath.exp(fits[1]) / (2.0 * nu);
		final Double[] gammaAndD = new Double[] { fits[0], fits[1], fits[2], D };
		return new Double[][] { delta_t, mu, gammaAndD };
	}

	public static Double[][] computeDeltaNDeltaT_DeltaNMu_GammaD(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int nu, final int Delta_t) {
		return OmegaDiffusivityLibrary.computeDeltaNDeltaT_DeltaNMu_GammaD(x,
		        y, windowDivisor, nu, Delta_t, 0, x.length - 1);
	}

	public static Double[][] computeDeltaNDeltaT_DeltaNMu_GammaD(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int nu, final int Delta_t, final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeDeltaNDeltaT_DeltaNMu_GammaD(x,
		        y, windowDivisor, nu, Delta_t, from, to);
	}

	public static Double[][] computeDeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int nu, final int Delta_t, final int from, final int to) {
		final int m = to - from;
		final Double log_Delta_t = StrictMath.log(Delta_t);
		final int max_Delta_n = OmegaDiffusivityLibrary.getMaxDeltaN(m,
		        windowDivisor);
		final Double[] log_delta_t = new Double[max_Delta_n + 1];
		final Double[] log_mu = new Double[log_delta_t.length];
		final Double[][] norms = OmegaDiffusivityLibrary.computeEuclideanNorms(
		        x, y, windowDivisor);
		for (int Delta_n = 1; Delta_n <= max_Delta_n; ++Delta_n) {
			log_mu[Delta_n] = StrictMath.log(OmegaDiffusivityLibrary.computeMu(
			        norms, windowDivisor, nu, Delta_n, from, to));
			log_delta_t[Delta_n] = log_Delta_t + StrictMath.log(Delta_n);
		}
		final Double[] fits = OmegaMathsUtilities.linearFit(log_delta_t,
		        log_mu, 1, max_Delta_n);
		final Double D = StrictMath.exp(fits[1]) / (2.0 * nu);
		final Double[] gammaAndD = new Double[] { fits[0], fits[1], fits[2], D };
		return new Double[][] { log_delta_t, log_mu, gammaAndD };
	}

	public static Double[][] computeDeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int nu, final int Delta_t) {
		return OmegaDiffusivityLibrary
		        .computeDeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog(x, y,
		                windowDivisor, nu, Delta_t, 0, x.length - 1);
	}

	public static Double[][] computeDeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int nu, final int Delta_t, final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary
		        .computeDeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog(x, y,
		                windowDivisor, nu, Delta_t, from, to);
	}

	public static Double[] computeNu() {
		final Double[] nu = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		for (int ny = 0; ny <= OmegaDiffusivityLibrary.MAX_NU; ++ny) {
			nu[ny] = (double) ny;
		}
		return nu;
	}

	public static Double[] computeSMSS(final Double[] x, final Double[] y,
	        final int windowDivisor, final int Delta_t, final int from,
	        final int to) {
		final Double[] nu = OmegaDiffusivityLibrary.computeNu();
		final Double[] gamma = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		for (int ny = 0; ny <= OmegaDiffusivityLibrary.MAX_NU; ++ny) {
			gamma[ny] = OmegaDiffusivityLibrary.computeGamma(x, y,
			        windowDivisor, ny, Delta_t, from, to)[0];
		}
		final Double[] smss = OmegaMathsUtilities.linearFit(nu, gamma);
		return smss;
	}

	public static Double[] computeSMSS(final Double[] x, final Double[] y,
	        final int windowDivisor, final int Delta_t) {
		return OmegaDiffusivityLibrary.computeSMSS(x, y, windowDivisor,
		        Delta_t, 0, x.length - 1);
	}

	public static Double[] computeSMSS(final Double[] x, final Double[] y,
	        final int windowDivisor, final int Delta_t,
	        final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeSMSS(x, y, windowDivisor,
		        Delta_t, from, to);
	}

	public static Double[] computeSMSSFromLog(final Double[] x,
	        final Double[] y, final int windowDivisor, final int Delta_t,
	        final int from, final int to) {
		final Double[] nu = OmegaDiffusivityLibrary.computeNu();
		final Double[] gamma = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		for (int ny = 0; ny <= OmegaDiffusivityLibrary.MAX_NU; ++ny) {
			gamma[ny] = OmegaDiffusivityLibrary.computeGammaFromLog(x, y,
			        windowDivisor, ny, Delta_t, from, to)[0];
		}
		final Double[] smss = OmegaMathsUtilities.linearFit(nu, gamma);
		return smss;
	}

	public static Double[] computeSMSSFromLog(final Double[] x,
	        final Double[] y, final int windowDivisor, final int Delta_t) {
		return OmegaDiffusivityLibrary.computeSMSSFromLog(x, y, windowDivisor,
		        Delta_t, 0, x.length - 1);
	}

	public static Double[] computeSMSSFromLog(final Double[] x,
	        final Double[] y, final int windowDivisor, final int Delta_t,
	        final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeSMSSFromLog(x, y, windowDivisor,
		        Delta_t, from, to);
	}

	public static Double[][] computeNu_Gamma_SMSS(final Double[] x,
	        final Double[] y, final int windowDivisor, final int Delta_t,
	        final int from, final int to) {
		final Double[] nu = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		final Double[] gamma = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		for (int ny = 0; ny <= OmegaDiffusivityLibrary.MAX_NU; ++ny) {
			nu[ny] = (double) ny;
			gamma[ny] = OmegaDiffusivityLibrary.computeGamma(x, y,
			        windowDivisor, ny, Delta_t, from, to)[0];
		}
		final Double[] smss = OmegaMathsUtilities.linearFit(nu, gamma);
		return new Double[][] { nu, gamma, smss };
	}

	public static Double[][] computeNu_Gamma_SMSS(final Double[] x,
	        final Double[] y, final int windowDivisor, final int Delta_t) {
		return OmegaDiffusivityLibrary.computeNu_Gamma_SMSS(x, y,
		        windowDivisor, Delta_t, 0, x.length - 1);
	}

	public static Double[][] computeNu_Gamma_SMSS(final Double[] x,
	        final Double[] y, final int windowDivisor, final int Delta_t,
	        final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeNu_Gamma_SMSS(x, y,
		        windowDivisor, Delta_t, from, to);
	}

	public static Double[][] computeNu_GammaFromLog_SMSSFromLog(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int Delta_t, final int from, final int to) {
		final Double[] nu = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		final Double[] gamma = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		for (int ny = 0; ny <= OmegaDiffusivityLibrary.MAX_NU; ++ny) {
			nu[ny] = (double) ny;
			gamma[ny] = OmegaDiffusivityLibrary.computeGammaFromLog(x, y,
			        windowDivisor, ny, Delta_t, from, to)[0];
		}
		final Double[] smss = OmegaMathsUtilities.linearFit(nu, gamma);
		return new Double[][] { nu, gamma, smss };
	}

	public static Double[][] computeNu_GammaFromLog_SMSSFromLog(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int Delta_t) {
		return OmegaDiffusivityLibrary.computeNu_GammaFromLog_SMSSFromLog(x, y,
		        windowDivisor, Delta_t, 0, x.length - 1);
	}

	public static Double[][] computeNu_GammaFromLog_SMSSFromLog(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int Delta_t, final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary.computeNu_GammaFromLog_SMSSFromLog(x, y,
		        windowDivisor, Delta_t, from, to);
	}

	public static Double[][][] computeNu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int Delta_t, final int from, final int to) {
		final Double[] nu = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		final Double[] gamma = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		final Double[][] delta_t = new Double[OmegaDiffusivityLibrary.MAX_NU + 1][];
		final Double[][] mu = new Double[OmegaDiffusivityLibrary.MAX_NU + 1][];
		final Double[][] gammaAndD = new Double[OmegaDiffusivityLibrary.MAX_NU + 1][];
		for (int ny = 0; ny <= OmegaDiffusivityLibrary.MAX_NU; ++ny) {
			nu[ny] = (double) ny;
			final Double[][] deltaNDelta_deltaNMu_GammaD = OmegaDiffusivityLibrary
			        .computeDeltaNDeltaT_DeltaNMu_GammaD(x, y, windowDivisor,
			                ny, Delta_t, from, to);
			delta_t[ny] = deltaNDelta_deltaNMu_GammaD[0];
			mu[ny] = deltaNDelta_deltaNMu_GammaD[1];
			gammaAndD[ny] = deltaNDelta_deltaNMu_GammaD[2];
			gamma[ny] = gammaAndD[ny][0];
		}
		final Double[] smss = OmegaMathsUtilities.linearFit(nu, gamma);
		return new Double[][][] { { nu }, delta_t, mu, gammaAndD, { smss } };
	}

	public static Double[][][] computeNu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int Delta_t) {
		return OmegaDiffusivityLibrary
		        .computeNu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS(x, y,
		                windowDivisor, Delta_t, 0, x.length - 1);
	}

	public static Double[][][] computeNu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int Delta_t, final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary
		        .computeNu_DeltaNDeltaT_DeltaNMu_GammaD_SMSS(x, y,
		                windowDivisor, Delta_t, from, to);
	}

	public static Double[][][] computeNu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int Delta_t, final int from, final int to) {
		final Double[] nu = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		final Double[] gamma = new Double[OmegaDiffusivityLibrary.MAX_NU + 1];
		final Double[][] log_delta_t = new Double[OmegaDiffusivityLibrary.MAX_NU + 1][];
		final Double[][] log_mu = new Double[OmegaDiffusivityLibrary.MAX_NU + 1][];
		final Double[][] gammaAndD = new Double[OmegaDiffusivityLibrary.MAX_NU + 1][];
		for (int ny = 0; ny <= OmegaDiffusivityLibrary.MAX_NU; ++ny) {
			nu[ny] = (double) ny;
			final Double[][] deltaNLogDelta_deltaNLogMu_GammaDFromLog = OmegaDiffusivityLibrary
			        .computeDeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog(x, y,
			                windowDivisor, ny, Delta_t, from, to);
			log_delta_t[ny] = deltaNLogDelta_deltaNLogMu_GammaDFromLog[0];
			log_mu[ny] = deltaNLogDelta_deltaNLogMu_GammaDFromLog[1];
			gammaAndD[ny] = deltaNLogDelta_deltaNLogMu_GammaDFromLog[2];
			gamma[ny] = gammaAndD[ny][0];
		}
		final Double[] smss = OmegaMathsUtilities.linearFit(nu, gamma);
		return new Double[][][] { { nu }, log_delta_t, log_mu, gammaAndD,
		        { smss } };
	}

	public static Double[][][] computeNu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int Delta_t) {
		return OmegaDiffusivityLibrary
		        .computeNu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog(
		                x, y, windowDivisor, Delta_t, 0, x.length - 1);
	}

	public static Double[][][] computeNu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog(
	        final Double[] x, final Double[] y, final int windowDivisor,
	        final int Delta_t, final OmegaSegment segment) {
		final int from = segment.getEndingROI().getFrameIndex();
		final int to = segment.getStartingROI().getFrameIndex();
		return OmegaDiffusivityLibrary
		        .computeNu_DeltaNLogDeltaT_DeltaNLogMu_GammaDFromLog_SMSSFromLog(
		                x, y, windowDivisor, Delta_t, from, to);
	}
}
