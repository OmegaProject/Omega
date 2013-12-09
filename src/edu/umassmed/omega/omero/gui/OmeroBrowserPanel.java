package edu.umassmed.omega.omero.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import pojos.ImageData;
import edu.umassmed.omega.commons.OmegaConstants;
import edu.umassmed.omega.commons.StringHelper;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.omero.OmeroGateway;
import edu.umassmed.omega.omero.data.OmeroDatasetWrapper;
import edu.umassmed.omega.omero.data.OmeroThumbnailImageInfo;

/**
 * JPanel to display the images in the OMERO datasets thumbnailed.
 * 
 * @author galliva
 */
public class OmeroBrowserPanel extends GenericPanel {
	private static final long serialVersionUID = 7625488987526070516L;

	private final OmeroGateway gateway;

	private JPanel mainPanel;
	private JLabel loadingStatus;

	private int numberOfImages;

	private Dimension panelSize;

	private ArrayList<ImageData> toBeProcessed = null;

	public ArrayList<ImageData> getToBeProcessed() {
		return this.toBeProcessed;
	}

	/**
	 * Create a new instance of this JPanel.
	 */
	public OmeroBrowserPanel(final RootPaneContainer parentContainer,
	        final OmeroGateway gateway) {
		super(parentContainer);
		this.gateway = gateway;
		// final Dimension d = new Dimension(400, 400);
		// this.setSize(d);
		// this.setPreferredSize(d);

		this.setLayout(new BorderLayout());
		this.createAndAddWidgets();
		this.addListeners();

		this.numberOfImages = 0;
	}

	public void createAndAddWidgets() {
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		this.loadingStatus = new JLabel("Nothing to load");
		topPanel.add(this.loadingStatus);

		this.add(topPanel, BorderLayout.NORTH);

		this.mainPanel = new JPanel();
		this.mainPanel.setDoubleBuffered(true);
		this.mainPanel.setBackground(Color.white);
		this.mainPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.add(this.mainPanel, BorderLayout.CENTER);
	}

	public void updateLoadingStatus(final String loadingStatus) {
		this.loadingStatus.setText(loadingStatus);
	}

	public void addListeners() {
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent evt) {
				OmeroBrowserPanel.this.checkForResize();
			}
		});
	}

	/**
	 * Sets the images to display.
	 * 
	 * @param images
	 *            the images to display.
	 */
	public void setImages(final List<OmeroThumbnailImageInfo> imageInfo) {
		this.toBeProcessed = new ArrayList<ImageData>();
		this.mainPanel.removeAll();
		if (imageInfo == null) {
			this.repaint();
			this.validate();
			this.checkForResize();
			return;
		}

		this.numberOfImages = imageInfo.size();
		final Iterator<OmeroThumbnailImageInfo> iterator = imageInfo.iterator();

		while (iterator.hasNext()) {
			final OmeroThumbnailImageInfo temp = iterator.next();
			final String imageName = StringHelper.getImageName(temp
			        .getImageName());

			final JPanel imageInfoPanel = new JPanel();
			imageInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			final int imgWidth = OmegaConstants.THUMBNAIL_SIZE;
			final int imgHeight = OmegaConstants.THUMBNAIL_SIZE + 40;
			imageInfoPanel.setSize(new Dimension(imgWidth, imgHeight));
			imageInfoPanel.setPreferredSize(new Dimension(imgWidth, imgHeight));

			final Dimension d = new Dimension(imgWidth, 20);

			// image
			final OmeroBrowserSingleImagePanel singleImagePanel = new OmeroBrowserSingleImagePanel(
			        temp.getImageID(), temp.getImageName(), temp.getImage());

			// image name
			final JLabel imageNameLbl = new JLabel();
			imageNameLbl.setSize(d);
			imageNameLbl.setPreferredSize(d);
			imageNameLbl.setText(imageName);
			imageNameLbl.setFont(new Font("Tahoma", 0, 10));

			// SPT check
			final JPanel sptCheckPanel = new JPanel();
			sptCheckPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			sptCheckPanel.setSize(d);
			sptCheckPanel.setPreferredSize(d);
			final JCheckBox checked = new JCheckBox();
			checked.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(final ItemEvent itemEvent) {
					final int state = itemEvent.getStateChange();
					if (state == ItemEvent.SELECTED) {
						OmeroBrowserPanel.this.toBeProcessed.add(temp
						        .getImageData());
					} else if (state == ItemEvent.DESELECTED) {
						OmeroBrowserPanel.this.toBeProcessed.remove(temp
						        .getImageData());
					}
				}
			});
			sptCheckPanel.add(checked);
			imageInfoPanel.add(singleImagePanel);
			imageInfoPanel.add(imageNameLbl);
			imageInfoPanel.add(sptCheckPanel);
			this.mainPanel.add(imageInfoPanel);
		}

		this.repaint();
		this.validate();
		this.checkForResize();
	}

	protected void checkForResize() {
		final Dimension windowSize = this.panelSize;
		final BigDecimal width = new BigDecimal(windowSize.width - 25);
		final BigDecimal thumWidth = new BigDecimal(
		        OmegaConstants.THUMBNAIL_SIZE + 10);
		final int numOfImagesPerRow = width.divide(thumWidth, 0,
		        RoundingMode.DOWN).intValue();
		final BigDecimal val1 = new BigDecimal(this.numberOfImages);
		final BigDecimal val2 = new BigDecimal(numOfImagesPerRow);
		final int numOfImagesPerCol = val1.divide(val2, 0, RoundingMode.UP)
		        .intValue();

		final int dimX = numOfImagesPerRow
		        * (OmegaConstants.THUMBNAIL_SIZE + 12);
		final int dimY = numOfImagesPerCol
		        * (OmegaConstants.THUMBNAIL_SIZE + 40 + 12);
		this.setPreferredSize(new Dimension(dimX, dimY));

		if (this.getParentContainer() instanceof JInternalFrame) {
			final JInternalFrame intFrame = (JInternalFrame) this
			        .getParentContainer();
			intFrame.validate();
			intFrame.repaint();
		} else {
			final JFrame frame = (JFrame) this.getParentContainer();
			frame.validate();
			frame.repaint();
		}
	}

	/**
	 * Browses the specified datasets.
	 * 
	 * @param id
	 *            The identifier of the dataset.
	 */
	public void browseDataset(final OmeroDatasetWrapper omeDataset) {
		this.setImages(null);
		final OmeroBrowerPanelImageLoader loader = new OmeroBrowerPanelImageLoader(
		        this, this.gateway, omeDataset);
		final String loadingStatus = "Loading...1/" + loader.getImageToLoad();
		this.updateLoadingStatus(loadingStatus);
		final Thread t = new Thread(loader);
		t.start();
	}

	public void setCompSize(final Dimension size) {
		this.panelSize = size;
	}
}
