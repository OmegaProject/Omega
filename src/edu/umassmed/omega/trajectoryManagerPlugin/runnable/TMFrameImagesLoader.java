package edu.umassmed.omega.trajectoryManagerPlugin.runnable;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;

public class TMFrameImagesLoader implements Runnable {

	private final OmegaMessageDisplayerPanelInterface displayerPanel;

	private final OmegaGateway gateway;
	private final OmegaImage img;
	private final List<BufferedImage> buffImages;

	public TMFrameImagesLoader(
	        final OmegaMessageDisplayerPanelInterface displayerPanel,
	        final OmegaGateway gateway, final OmegaImage img) {
		this.displayerPanel = displayerPanel;
		this.gateway = gateway;
		this.img = img;

		this.buffImages = new ArrayList<BufferedImage>();
	}

	@Override
	public void run() {
		final OmegaGateway gateway = this.gateway;
		final OmegaImage img = this.img;
		if ((gateway == null) || (img == null))
			// TODO throw error
			return;
		final OmegaImagePixels pixels = img.getDefaultPixels();
		final int maxT = pixels.getSizeT();
		for (int x = 0; x < maxT; x++) {
			try {
				final byte[] values = gateway.renderCompressed(
				        pixels.getElementID(), x, pixels.getSelectedZ());
				final ByteArrayInputStream stream = new ByteArrayInputStream(
				        values);
				final BufferedImage bufferedImage = ImageIO.read(stream);
				bufferedImage.setAccelerationPriority(1f);
				this.buffImages.add(bufferedImage);
				final StringBuffer buffer = new StringBuffer();
				buffer.append("Frame ");
				buffer.append(x);
				buffer.append("/");
				buffer.append(maxT);
				buffer.append(" loaded");
				this.updateStatus(buffer.toString(), (x % 5) == 0);
			} catch (final IOException ex) {
				OmegaLogFileManager.handleUncaughtException(ex);
			}
		}
		final StringBuffer buffer = new StringBuffer();
		buffer.append("All frames loaded");
		this.updateStatus(buffer.toString(), true);
	}

	public void updateStatus(final String status, final boolean repaint) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TMFrameImagesLoader.this.displayerPanel
					        .updateMessageStatus(new TMLoaderMessage(status,
					                repaint));
				}
			});
		} catch (final InvocationTargetException | InterruptedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	public List<BufferedImage> getImages() {
		return this.buffImages;
	}
}
