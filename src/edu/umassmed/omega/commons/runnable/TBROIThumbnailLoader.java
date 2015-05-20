package edu.umassmed.omega.commons.runnable;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import edu.umassmed.omega.commons.OmegaLogFileManager;
import edu.umassmed.omega.commons.eventSystem.events.OmegaMessageEventTBLoader;
import edu.umassmed.omega.commons.gui.interfaces.OmegaMessageDisplayerPanelInterface;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.data.coreElements.OmegaImagePixels;
import edu.umassmed.omega.data.imageDBConnectionElements.OmegaGateway;

public class TBROIThumbnailLoader implements Runnable {

	private final OmegaMessageDisplayerPanelInterface displayerPanel;

	private final OmegaGateway gateway;
	private final OmegaImage img;
	private final List<BufferedImage> buffImages;

	private boolean killed;

	public TBROIThumbnailLoader(
	        final OmegaMessageDisplayerPanelInterface displayerPanel,
	        final OmegaGateway gateway, final OmegaImage img) {
		this.displayerPanel = displayerPanel;
		this.gateway = gateway;
		this.img = img;

		this.buffImages = new ArrayList<BufferedImage>();

		this.killed = false;
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
			if (this.killed) {
				break;
			}
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
		if (this.killed)
			return;
		final StringBuffer buffer = new StringBuffer();
		buffer.append("All frames loaded");
		this.updateStatus(buffer.toString(), true);
	}

	public void updateStatus(final String status, final boolean repaint) {
		if (this.killed)
			return;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					TBROIThumbnailLoader.this.displayerPanel
					        .updateMessageStatus(new OmegaMessageEventTBLoader(
					                status, repaint));
				}
			});
		} catch (final InvocationTargetException | InterruptedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	public List<BufferedImage> getImages() {
		return this.buffImages;
	}

	public void kill() {
		this.killed = true;
	}
}
