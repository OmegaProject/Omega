package edu.umassmed.omega.commons.utilities;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

import sun.awt.image.IntegerInterleavedRaster;

public class OmegaImageRenderingUtilities {

	/** The red mask. */
	private static final int RED_MASK = 0x00ff0000;
	/** The green mask. */
	private static final int GREEN_MASK = 0x0000ff00;
	/** The blue mask. */
	private static final int BLUE_MASK = 0x000000ff;
	/** The RGB masks. */
	private static final int[] RGB = { OmegaImageRenderingUtilities.RED_MASK,
	        OmegaImageRenderingUtilities.GREEN_MASK, OmegaImageRenderingUtilities.BLUE_MASK };

	/**
	 * Creates a buffer image from the specified <code>array</code> of integers.
	 * 
	 * @param buf
	 *            The array to handle.
	 * @param bits
	 *            The number of bits in the pixel values.
	 * @param sizeX
	 *            The width (in pixels) of the region of image data described.
	 * @param sizeY
	 *            The height (in pixels) of the region of image data described.
	 * @return See above.
	 */
	public static BufferedImage createImage(final int[] buf, final int bits,
	        final int sizeX, final int sizeY) {
		if (buf == null)
			return null;

		final DataBuffer j2DBuf = new DataBufferInt(buf, sizeX * sizeY);

		final SinglePixelPackedSampleModel sampleModel = new SinglePixelPackedSampleModel(
		        DataBuffer.TYPE_INT, sizeX, sizeY, sizeX, OmegaImageRenderingUtilities.RGB);

		final WritableRaster raster = new IntegerInterleavedRaster(sampleModel,
		        j2DBuf, new Point(0, 0));

		final ColorModel colorModel = new DirectColorModel(bits,
		        OmegaImageRenderingUtilities.RGB[0], OmegaImageRenderingUtilities.RGB[1],
		        OmegaImageRenderingUtilities.RGB[2]);
		final BufferedImage image = new BufferedImage(colorModel, raster,
		        false, null);
		image.setAccelerationPriority(1f);
		return image;
	}
}
