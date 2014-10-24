package edu.umassmed.omega.commons.utilities;

import java.util.Arrays;

public class OmegaMathsUtilities {
	public static Double mean(final Double[] values, final Integer from,
	        final Integer to) {
		Double sum = 0.0;
		for (int i = from; i <= to; ++i) {
			sum += values[i];
		}
		final Integer div = (to + 1) - from;
		final Double val = sum / div;
		return val;
	}

	public static Double mean(final Double[] values) {
		return OmegaMathsUtilities.mean(values, 0, values.length - 1);
	}

	public static Double median(final Double[] values, final Integer from,
	        final Integer to) {
		final Double[] newValues = new Double[(to + 1) - from];
		System.arraycopy(values, from, newValues, 0, newValues.length);
		Arrays.sort(newValues);
		final int ix0 = (newValues.length - 1) >> 1;
		return (newValues.length & 0x1) != 0 ? newValues[ix0]
		        : (newValues[ix0] + newValues[ix0 + 1]) / 2.0;
	}

	public static Double median(final Double[] values) {
		return OmegaMathsUtilities.median(values, 0, values.length - 1);
	}

	public static Double varianceN(final Double[] values, final Integer from,
	        final Integer to) {
		final Double mean = OmegaMathsUtilities.mean(values, from, to);
		double sum = 0.0;
		for (int i = from; i <= to; ++i) {
			sum += StrictMath.pow(values[i] - mean, 2);
		}
		final Integer div = (to + 1) - from;
		final Double val = sum / div;
		return val;
	}

	public static Double varianceN(final Double[] values) {
		return OmegaMathsUtilities.varianceN(values, 0, values.length - 1);
	}

	public static double varianceN1(final Double[] values, final Integer from,
	        final Integer to) {
		final Double mean = OmegaMathsUtilities.mean(values, from, to);
		double sum = 0.0;
		for (int i = from; i <= to; ++i) {
			sum += StrictMath.pow(values[i] - mean, 2);
		}
		final Integer div = to - from;
		final Double val = sum / div;
		return val;
	}

	public static Double varianceN1(final Double[] x) {
		return OmegaMathsUtilities.varianceN1(x, 0, x.length - 1);
	}

	public static Double standardDeviationN(final Double[] values,
	        final Integer from, final Integer to) {
		return StrictMath.sqrt(OmegaMathsUtilities.varianceN(values, from, to));
	}

	public static Double standardDeviationN(final Double[] values) {
		return OmegaMathsUtilities.standardDeviationN(values, 0,
		        values.length - 1);
	}

	public static Double standardDeviationN1(final Double[] values,
	        final Integer from, final Integer to) {
		return StrictMath
		        .sqrt(OmegaMathsUtilities.varianceN1(values, from, to));
	}

	public static double standardDeviationN1(final Double[] values) {
		return OmegaMathsUtilities.standardDeviationN1(values, 0,
		        values.length - 1);
	}

	public static Double tTest(final Double[] valuesX, final Integer fromX,
	        final Integer toX, final Double[] valuesY, final Integer fromY,
	        final Integer toY) {
		final Double meanX = OmegaMathsUtilities.mean(valuesX, fromX, toX);
		final Double meanY = OmegaMathsUtilities.mean(valuesY, fromY, toY);
		final Double varN1X = (OmegaMathsUtilities.varianceN1(valuesX, fromX,
		        toX) / ((toX + 1) - fromX));
		final Double varN1Y = (OmegaMathsUtilities.varianceN1(valuesY, fromY,
		        toY) / ((toY + 1) - fromY));
		final Double sqrtVarN1 = StrictMath.sqrt(varN1X + varN1Y);
		return (meanX - meanY) / sqrtVarN1;
	}

	public static Double tTest(final Double[] valuesX, final Double[] valuesY) {
		return OmegaMathsUtilities.tTest(valuesX, 0, valuesX.length - 1,
		        valuesY, 0, valuesY.length - 1);
	}

	// TODO check it again and possibly change neame in a meaningful way
	public static Double[] linearFit(final Double[] u, final Double[] v,
	        final Integer from, final Integer to) {
		final Double u_bar = OmegaMathsUtilities.mean(u, from, to);
		final Double v_bar = OmegaMathsUtilities.mean(v, from, to);
		double SigmaUV = 0.0;
		double SigmaU2 = 0.0;
		double SigmaV2 = 0.0;
		for (int i = from; i <= to; ++i) {
			SigmaUV += (u[i] - u_bar) * (v[i] - v_bar);
			SigmaU2 += StrictMath.pow(u[i] - u_bar, 2);
			SigmaV2 += StrictMath.pow(v[i] - v_bar, 2);
		}
		final double m = SigmaUV / SigmaU2;
		final double q = v_bar - (m * u_bar);
		final double r = SigmaUV / StrictMath.sqrt(SigmaU2 * SigmaV2);
		return new Double[] { m, q, r };
	}

	public static Double[] linearFit(final Double[] u, final Double[] v) {
		return OmegaMathsUtilities.linearFit(u, v, 0, u.length - 1);
	}
}
