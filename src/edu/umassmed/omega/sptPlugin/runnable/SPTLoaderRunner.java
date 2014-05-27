package edu.umassmed.omega.sptPlugin.runnable;

import java.util.UUID;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.galliva.gallibrary.GLogManager;

import edu.umassmed.omega.commons.OmegaConstants;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public class SPTLoaderRunner implements SPTRunnable {
	private final OmegaImage image;
	private final OmegaGateway gateway;
	private final int z, c;

	private boolean isJobCompleted;

	public SPTLoaderRunner(final OmegaImage image, final int z, final int c,
	        final OmegaGateway gateway) {
		this.image = image;
		this.z = z;
		this.c = c;

		this.gateway = gateway;

		this.isJobCompleted = false;
	}

	@Override
	public boolean isJobCompleted() {
		return this.isJobCompleted;
	}

	@Override
	public void run() {
		final OmegaImagePixels defaultPixels = this.image.getDefaultPixels();
		// ID of the pixels
		final Long pixelsID = defaultPixels.getElementID();
		// number of frames for this image
		final int framesNumber = defaultPixels.getSizeT();
		// number of bytes of this image
		final int byteWidth = this.gateway.getByteWidth(pixelsID);

		boolean error = false;

		GLogManager.log(
		        String.format("processing %d byte per pixel", byteWidth),
		        Level.INFO);

		for (int t = 0; t < framesNumber; t++) {
			final OmegaFrame frame = new OmegaFrame(UUID.randomUUID()
			        .getMostSignificantBits(), t, this.c, this.z);
			frame.setParentPixels(defaultPixels);
			defaultPixels.addFrame(frame);
			// TODO update panel with loading frame number
			// JPanelSPT.this.jLabelStatusDetails.setText(String.format(
			// "loading frame %d / %d", i + 1, framesNumber));

			try {
				final byte[] pixels = this.gateway.getImageData(pixelsID,
				        this.z, t, this.c);

				int[] data = null;

				System.out.println("Byte width: " + byteWidth);
				// Manage the right amount of byte per pixels
				switch (byteWidth) {
				case 1:
					// 8 bit image
					data = new int[pixels.length];
					for (int j = 0; j < data.length; j++) {
						final int b0 = pixels[j] & 0xff;
						data[j] = b0 << 0;
					}
					break;
				case 2:
					// 16 bit image
					data = new int[pixels.length / 2];
					for (int j = 0; j < data.length; j++) {
						final int b0 = pixels[2 * j] & 0xff;
						final int b1 = pixels[(2 * j) + 1] & 0xff;
						data[j] = (b0 << 8) | (b1 << 0);
					}
					break;
				case 3:
					// 24 bit image
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
					data = new int[pixels.length / 4];
					for (int j = 0; j < data.length; j++) {
						final int b0 = pixels[4 * j] & 0xff;
						final int b1 = pixels[(4 * j) + 1] & 0xff;
						final int b2 = pixels[(4 * j) + 2] & 0xff;
						final int b3 = pixels[(4 * j) + 3] & 0xff;
						data[j] = (b0 << 24) | (b1 << 16) | (b2 << 8)
						        | (b3 << 0);
					}
					break;
				}
				SPTDLLInvoker.callLoadImage(data);
			} catch (final Exception e) {
				error = true;
				GLogManager.log(String.format("%s: %s",
				        OmegaConstants.ERROR_DURING_SPT_RUN, e.toString()),
				        Level.SEVERE);
			}
		}

		if (error) {
			JOptionPane.showMessageDialog(null,
			        OmegaConstants.ERROR_DURING_SPT_RUN,
			        OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
		}

		this.isJobCompleted = true;
	}
}