package edu.umassmed.omega.commons.utilities;

import java.util.ArrayList;
import java.util.List;

import loci.common.DataTools;

public class OmegaImageUtilities {

	public static Double[] normalizeImage(final Integer[] image) {
		final Integer[] minMax = OmegaMathsUtilities.getMinAndMax(image);
		final int min = minMax[0];
		final int max = minMax[1];
		final Double[] newImage = new Double[image.length];
		for (int i = 0; i < image.length; i++) {
			final int val = image[i];
			final double newVal = OmegaMathsUtilities.normalize(val, min, max);
			newImage[i] = newVal;
		}
		return newImage;
	}

	public static short[] convertBytesToShortImage(final byte[] pixels) {
		final short[] data = new short[pixels.length / 2];
		for (int j = 0; j < data.length; j++) {
			final byte[] val = new byte[2];
			val[0] = pixels[2 * j];
			val[1] = pixels[(2 * j) + 1];
			data[j] = DataTools.bytesToShort(val, true);
		}
		return data;
	}

	public static float[] convertBytesToFloatImage(final byte[] pixels) {
		final float[] data = new float[pixels.length / 3];
		for (int j = 0; j < data.length; j++) {
			final byte[] val = new byte[3];
			val[0] = pixels[3 * j];
			val[1] = pixels[(3 * j) + 1];
			val[2] = pixels[(3 * j) + 2];
			data[j] = DataTools.bytesToFloat(val, true);
		}
		return data;
	}

	public static int[] convertBytesToIntImage(final byte[] pixels) {
		final int[] data = new int[pixels.length / 4];
		for (int j = 0; j < data.length; j++) {
			final byte[] val = new byte[4];
			val[0] = pixels[4 * j];
			val[1] = pixels[(4 * j) + 1];
			val[2] = pixels[(4 * j) + 2];
			val[3] = pixels[(4 * j) + 3];
			data[j] = DataTools.bytesToInt(val, true);
		}
		return data;
	}

	public static double[] convertBytesToDoubleImage(final byte[] pixels) {
		final double[] data = new double[pixels.length / 4];
		for (int j = 0; j < data.length; j++) {
			final byte[] val = new byte[4];
			val[0] = pixels[4 * j];
			val[1] = pixels[(4 * j) + 1];
			val[2] = pixels[(4 * j) + 2];
			val[3] = pixels[(4 * j) + 3];
			data[j] = DataTools.bytesToDouble(val, true);
		}
		return data;
	}

	public static int[] convertByteToIntImage(final int byteWidth,
			final byte[] pixels) {
		int[] data = null;
		// Manage the right amount of byte per pixels
		switch (byteWidth) {
		case 1:
			// 8 bit image
			// System.out.println("Loading t: " + t + " 8 bit");
			data = new int[pixels.length];
			for (int j = 0; j < data.length; j++) {
				final int b0 = pixels[j] & 0xff;
				data[j] = b0 << 0;
			}
			break;
		case 2:
			// 16 bit image
			// System.out.println("Loading t: " + t + " 16 bit");
			data = new int[pixels.length / 2];
			for (int j = 0; j < data.length; j++) {
				final int b0 = pixels[2 * j] & 0xff;
				final int b1 = pixels[(2 * j) + 1] & 0xff;
				data[j] = (b0 << 8) | (b1 << 0);
			}
			break;
		case 3:
			// 24 bit image
			// System.out.println("Loading t: " + t + " 24 bit");
			data = new int[pixels.length / 3];
			for (int j = 0; j < data.length; j++) {
				final int b0 = pixels[3 * j] & 0xff;
				final int b1 = pixels[(3 * j) + 1] & 0xff;
				final int b2 = pixels[(3 * j) + 2] & 0xff;
				data[j] = (b0 << 16) | (b1 << 8) | (b2 << 0);
			}
			break;
		case 4:
			// 32 bit image
			// System.out.println("Loading t: " + t + " 32 bit");
			data = new int[pixels.length / 4];
			for (int j = 0; j < data.length; j++) {
				final int b0 = pixels[4 * j] & 0xff;
				final int b1 = pixels[(4 * j) + 1] & 0xff;
				final int b2 = pixels[(4 * j) + 2] & 0xff;
				final int b3 = pixels[(4 * j) + 3] & 0xff;
				data[j] = (b0 << 24) | (b1 << 16) | (b2 << 8) | (b3 << 0);
			}
			break;
		}
		return data;
	}

	public static Integer[] convertByteToIntegerImage(final int byteWidth,
			final byte[] pixels) {
		Integer[] data = null;

		// Manage the right amount of byte per pixels
		switch (byteWidth) {
		case 1:
			// 8 bit image
			// System.out.println("Loading t: " + t + " 8 bit");
			data = new Integer[pixels.length];
			for (int j = 0; j < data.length; j++) {
				final int b0 = pixels[j] & 0xff;
				data[j] = b0 << 0;
			}
			break;
		case 2:
			// 16 bit image
			// System.out.println("Loading t: " + t + " 16 bit");
			data = new Integer[pixels.length / 2];
			for (int j = 0; j < data.length; j++) {
				final int b0 = pixels[2 * j] & 0xff;
				final int b1 = pixels[(2 * j) + 1] & 0xff;
				data[j] = (b0 << 8) | (b1 << 0);
			}
			break;
		case 3:
			// 24 bit image
			// System.out.println("Loading t: " + t + " 24 bit");
			data = new Integer[pixels.length / 3];
			for (int j = 0; j < data.length; j++) {
				final int b0 = pixels[3 * j] & 0xff;
				final int b1 = pixels[(3 * j) + 1] & 0xff;
				final int b2 = pixels[(3 * j) + 2] & 0xff;
				data[j] = (b0 << 16) | (b1 << 8) | (b2 << 0);
			}
			break;
		case 4:
			// 32 bit image
			// System.out.println("Loading t: " + t + " 32 bit");
			data = new Integer[pixels.length / 4];
			for (int j = 0; j < data.length; j++) {
				final int b0 = pixels[4 * j] & 0xff;
				final int b1 = pixels[(4 * j) + 1] & 0xff;
				final int b2 = pixels[(4 * j) + 2] & 0xff;
				final int b3 = pixels[(4 * j) + 3] & 0xff;
				data[j] = (b0 << 24) | (b1 << 16) | (b2 << 8) | (b3 << 0);
			}
			break;
		}
		return data;
	}

	public static Integer[] getValueSmaller(final Integer[] image,
			final double threshold) {
		final List<Integer> smallerValues = new ArrayList<>();
		for (final int val : image) {
			if (val < threshold) {
				smallerValues.add(val);
			}
		}
		return (Integer[]) smallerValues.toArray();
	}

	public static Double[] getValueSmaller(final Double[] image,
			final double threshold) {
		final List<Double> smallerValues = new ArrayList<>();
		for (final double val : image) {
			if (val < threshold) {
				smallerValues.add(val);
			}
		}
		return (Double[]) smallerValues.toArray();
	}
}
