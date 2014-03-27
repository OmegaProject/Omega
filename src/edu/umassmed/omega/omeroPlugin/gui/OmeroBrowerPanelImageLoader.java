package edu.umassmed.omega.omeroPlugin.gui;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import pojos.DatasetData;
import pojos.ImageData;
import pojos.ProjectData;
import edu.umassmed.omega.commons.OmegaConstants;
import edu.umassmed.omega.omeroPlugin.OmeroGateway;
import edu.umassmed.omega.omeroPlugin.data.OmeroDatasetWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroImageWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroThumbnailImageInfo;

public class OmeroBrowerPanelImageLoader implements Runnable {

	private final OmeroBrowserPanel browserPanel;
	private final OmeroGateway gateway;
	private final List<ImageData> images;
	private final ProjectData projectData;
	private final DatasetData datasetData;
	private volatile ArrayList<OmeroThumbnailImageInfo> imageInfo;
	private final int imagesToLoad;
	private volatile int imagesLoaded;

	public OmeroBrowerPanelImageLoader(final OmeroBrowserPanel browserPanel,
	        final OmeroGateway gateway, final OmeroDatasetWrapper omeDataset) {
		this.browserPanel = browserPanel;
		this.gateway = gateway;

		this.projectData = omeDataset.getProject();
		this.datasetData = omeDataset.getDatasetData();
		this.images = new ArrayList<ImageData>(this.datasetData.getImages());

		this.imageInfo = new ArrayList<OmeroThumbnailImageInfo>();

		this.imagesToLoad = this.images.size();
		this.imagesLoaded = 0;
	}

	public int getImageToLoad() {
		return this.imagesToLoad;
	}

	public int getImageLoaded() {
		return this.imagesLoaded;
	}

	public ArrayList<OmeroThumbnailImageInfo> getImagesInfo() {
		return this.imageInfo;
	}

	@Override
	public void run() {
		boolean error = false;

		if (this.images == null)
			return;

		for (int i = 0; i < this.images.size(); i++) {
			final int currentlyLoading = 1 + this.imagesLoaded;
			this.updateLoadingStatus(currentlyLoading);
			final ImageData imageData = this.images.get(i);

			final Long pixelID = imageData.getDefaultPixels().getId();

			final List<Long> pixelIDs = new ArrayList<Long>();
			pixelIDs.add(pixelID);

			List<BufferedImage> bufferedImages = null;

			try {
				bufferedImages = this.gateway.getThumbnailSet(pixelIDs,
				        OmegaConstants.THUMBNAIL_SIZE);
				final OmeroImageWrapper image = new OmeroImageWrapper(pixelID,
				        imageData.getName(), this.projectData,
				        this.datasetData, imageData);
				this.imageInfo.add(new OmeroThumbnailImageInfo(image,
				        bufferedImages.get(0)));
			} catch (final Exception e) {
				error = true;
			}

			this.imagesLoaded++;
		}

		if (error) {
			JOptionPane.showMessageDialog(null,
			        OmegaConstants.ERROR_LOADING_THE_DS,
			        OmegaConstants.OMEGA_TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}

		this.updateLoadingStatus(this.imagesLoaded);
	}

	public void updateLoadingStatus(final int currentlyLoading) {
		final String loadingStatus = currentlyLoading + "/"
		        + OmeroBrowerPanelImageLoader.this.imagesToLoad
		        + "...loaded image(s) for" + this.datasetData.getName();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					OmeroBrowerPanelImageLoader.this.browserPanel
					        .updateLoadingStatus(loadingStatus);
					if (((OmeroBrowerPanelImageLoader.this.imagesLoaded % 10) == 0)
					        || (OmeroBrowerPanelImageLoader.this.imagesLoaded == OmeroBrowerPanelImageLoader.this.imagesToLoad)) {
						OmeroBrowerPanelImageLoader.this.browserPanel
						        .setImages(OmeroBrowerPanelImageLoader.this.imageInfo);
					}
				}
			});
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
