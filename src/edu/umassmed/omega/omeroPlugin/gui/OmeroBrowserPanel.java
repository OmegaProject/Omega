/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.omeroPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.RootPaneContainer;
import javax.swing.border.EmptyBorder;

import pojos.ImageData;
import edu.umassmed.omega.commons.constants.OmegaConstants;
import edu.umassmed.omega.commons.eventSystem.OmegaMessageEvent;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxStatus;
import edu.umassmed.omega.commons.utilities.OmegaStringUtilities;
import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.omeroPlugin.OmeroGateway;
import edu.umassmed.omega.omeroPlugin.data.OmeroDatasetWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroImageWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroThumbnailImageInfo;
import edu.umassmed.omega.omeroPlugin.runnable.OmeroBrowerPanelImageLoader;

/**
 * JPanel to display the images in the OMERO datasets thumbnailed.
 * 
 * @author galliva
 */
public class OmeroBrowserPanel extends GenericPanel {
	private static final long serialVersionUID = 7625488987526070516L;

	private boolean isListView;

	private final OmeroPluginPanel pluginPanel;
	private final OmeroGateway gateway;

	private JPanel mainPanel;
	private JRadioButton gridView_butt, listView_butt;

	private int numberOfImages;
	private Dimension panelSize;
	private OmeroDatasetWrapper datasetWrapper;
	private CheckBoxStatus datasetStatus;

	private final List<JCheckBox> checkboxList;

	private boolean updating;

	private final Map<OmeroDatasetWrapper, List<OmeroImageWrapper>> imageToBeLoadedList;
	private final List<OmegaImage> loadedImages;

	private List<OmeroThumbnailImageInfo> imagesInfo;

	public Map<OmeroDatasetWrapper, List<OmeroImageWrapper>> getImagesToBeLoaded() {
		return this.imageToBeLoadedList;
	}

	/**
	 * Create a new instance of this JPanel.
	 */
	public OmeroBrowserPanel(final RootPaneContainer parentContainer,
	        final OmeroPluginPanel pluginPanel, final OmeroGateway gateway) {
		super(parentContainer);

		this.isListView = false;
		this.updating = false;

		this.datasetStatus = CheckBoxStatus.DESELECTED;

		this.datasetWrapper = null;
		this.imageToBeLoadedList = new LinkedHashMap<OmeroDatasetWrapper, List<OmeroImageWrapper>>();
		this.loadedImages = new ArrayList<OmegaImage>();

		this.checkboxList = new ArrayList<JCheckBox>();

		this.pluginPanel = pluginPanel;

		this.gateway = gateway;
		// final Dimension d = new Dimension(400, 400);
		// this.setSize(d);
		// this.setPreferredSize(d);

		this.setLayout(new BorderLayout());
		// this.setBackground(Color.white);

		this.createAndAddWidgets();

		this.addListeners();

		this.numberOfImages = 0;
	}

	private void createAndAddWidgets() {
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		final ButtonGroup group = new ButtonGroup();
		this.gridView_butt = new JRadioButton("Grid view");
		this.gridView_butt.setSelected(true);
		this.listView_butt = new JRadioButton("List view");
		group.add(this.gridView_butt);
		group.add(this.listView_butt);

		topPanel.add(this.gridView_butt);
		topPanel.add(this.listView_butt);

		this.add(topPanel, BorderLayout.NORTH);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		// this.mainPanel.setDoubleBuffered(true);
		this.mainPanel.setBackground(Color.white);

		final JScrollPane scrollPaneBrowser = new JScrollPane(this.mainPanel);
		this.add(scrollPaneBrowser, BorderLayout.CENTER);
	}

	private void addListeners() {
		this.gridView_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmeroBrowserPanel.this.isListView = false;

				OmeroBrowserPanel.this.checkForResize();
				OmeroBrowserPanel.this.redrawImagePanels();
			}
		});
		this.listView_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OmeroBrowserPanel.this.isListView = true;
				OmeroBrowserPanel.this.checkForResize();
				OmeroBrowserPanel.this.redrawImagePanels();
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				OmeroBrowserPanel.this.checkForResize();
				OmeroBrowserPanel.this.redrawImagePanels();
			}
		});
	}

	private void setCheckboxStatus(final JPanel panel,
	        final OmeroThumbnailImageInfo temp, final JCheckBox checked) {
		if (this.isImageLoaded(temp.getImageID())) {
			checked.setSelected(true);
			checked.setEnabled(false);
			panel.setEnabled(false);
		} else {
			if (this.datasetStatus == CheckBoxStatus.SELECTED) {
				List<OmeroImageWrapper> imageWrapperList;
				if (this.imageToBeLoadedList.containsKey(this.datasetWrapper)) {
					imageWrapperList = this.imageToBeLoadedList
					        .get(this.datasetWrapper);
					if (!imageWrapperList.contains(temp.getImage())) {
						imageWrapperList.add(temp.getImage());
					}
				} else {
					imageWrapperList = new ArrayList<OmeroImageWrapper>();
					imageWrapperList.add(temp.getImage());
				}
				checked.setSelected(true);
				this.imageToBeLoadedList.put(this.datasetWrapper,
				        imageWrapperList);
			} else if (this.datasetStatus == CheckBoxStatus.DESELECTED) {
				if (this.imageToBeLoadedList.containsKey(this.datasetWrapper)) {
					this.imageToBeLoadedList.remove(this.datasetWrapper);
				}
			} else {
				if (this.imageToBeLoadedList.containsKey(this.datasetWrapper)) {
					final List<OmeroImageWrapper> imageWrapperList = this.imageToBeLoadedList
					        .get(this.datasetWrapper);
					if (imageWrapperList.contains(temp.getImage())) {
						checked.setSelected(true);
					}
				}
			}
		}
	}

	private void addCheckboxListener(final OmeroThumbnailImageInfo temp,
	        final JCheckBox checked) {
		checked.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent itemEvent) {
				if (OmeroBrowserPanel.this.updating)
					return;
				final int state = itemEvent.getStateChange();
				List<OmeroImageWrapper> imageWrapperList;
				if (OmeroBrowserPanel.this.imageToBeLoadedList
				        .containsKey(OmeroBrowserPanel.this.datasetWrapper)) {
					imageWrapperList = OmeroBrowserPanel.this.imageToBeLoadedList
					        .get(OmeroBrowserPanel.this.datasetWrapper);
				} else {
					imageWrapperList = new ArrayList<OmeroImageWrapper>();
				}
				if (state == ItemEvent.SELECTED) {
					imageWrapperList.add(temp.getImage());
				} else if (state == ItemEvent.DESELECTED) {
					imageWrapperList.remove(temp.getImage());
				}
				OmeroBrowserPanel.this.imageToBeLoadedList
				        .put(OmeroBrowserPanel.this.datasetWrapper,
				                imageWrapperList);
				OmeroBrowserPanel.this.pluginPanel
				        .updateDatasetSelection(imageWrapperList.size());
			}
		});
	}

	private void createAndAddListHeaderPanel() {
		final int height = 20;
		final int width = this.mainPanel.getWidth() - 20;
		final int usableWidth = width - OmegaConstants.THUMBNAIL_SIZE;
		final int nameDim = usableWidth - 150 - (50 * 6) - 10;
		final int dateDim = 150 - 10;
		final int fieldDim = 50 - 10;

		final Font font = new Font("Tahoma", 0, 10);
		final JPanel listHeaderPanel = new JPanel();
		listHeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		// listHeaderPanel.setBackground(Color.white);
		listHeaderPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		final Dimension listHeaderDim = new Dimension(width, height);
		listHeaderPanel.setSize(listHeaderDim);
		listHeaderPanel.setPreferredSize(listHeaderDim);

		final JLabel selectedLbl = new JLabel("Selected");
		final Dimension selectedLblDim = new Dimension(fieldDim, height);
		selectedLbl.setSize(selectedLblDim);
		selectedLbl.setPreferredSize(selectedLblDim);
		selectedLbl.setFont(font);

		final JLabel thumbnailLbl = new JLabel("Thumbnail");
		final Dimension thumbnailLblDim = new Dimension(
		        OmegaConstants.THUMBNAIL_SIZE, height);
		thumbnailLbl.setSize(thumbnailLblDim);
		thumbnailLbl.setPreferredSize(thumbnailLblDim);
		thumbnailLbl.setFont(font);

		final JLabel nameLbl = new JLabel("Name");
		final Dimension nameLblDim = new Dimension(nameDim, height);
		nameLbl.setSize(nameLblDim);
		nameLbl.setPreferredSize(nameLblDim);
		nameLbl.setFont(font);

		final JLabel dateLbl = new JLabel("Created");
		final Dimension dateLblDim = new Dimension(dateDim, height);
		dateLbl.setSize(dateLblDim);
		dateLbl.setPreferredSize(dateLblDim);
		dateLbl.setFont(font);

		final JLabel sizeXLbl = new JLabel("Size X");
		final Dimension sizeXLblDim = new Dimension(fieldDim, height);
		sizeXLbl.setSize(sizeXLblDim);
		sizeXLbl.setPreferredSize(sizeXLblDim);
		sizeXLbl.setFont(font);

		final JLabel sizeYLbl = new JLabel("Size Y");
		final Dimension sizeYLblDim = new Dimension(fieldDim, height);
		sizeYLbl.setSize(sizeYLblDim);
		sizeYLbl.setPreferredSize(sizeYLblDim);
		sizeYLbl.setFont(font);

		final JLabel sizeZLbl = new JLabel("Size Z");
		final Dimension sizeZLblDim = new Dimension(fieldDim, height);
		sizeZLbl.setSize(sizeZLblDim);
		sizeZLbl.setPreferredSize(sizeZLblDim);
		sizeZLbl.setFont(font);

		final JLabel sizeTLbl = new JLabel("Size T");
		final Dimension sizeTLblDim = new Dimension(fieldDim, height);
		sizeTLbl.setSize(sizeTLblDim);
		sizeTLbl.setPreferredSize(sizeTLblDim);
		sizeTLbl.setFont(font);

		final JLabel sizeCLbl = new JLabel("Size C");
		final Dimension sizeCLblDim = new Dimension(fieldDim, height);
		sizeCLbl.setSize(sizeCLblDim);
		sizeCLbl.setPreferredSize(sizeCLblDim);
		sizeCLbl.setFont(font);

		listHeaderPanel.add(selectedLbl);
		listHeaderPanel.add(thumbnailLbl);
		listHeaderPanel.add(nameLbl);
		listHeaderPanel.add(dateLbl);
		listHeaderPanel.add(sizeXLbl);
		listHeaderPanel.add(sizeYLbl);
		listHeaderPanel.add(sizeZLbl);
		listHeaderPanel.add(sizeTLbl);
		listHeaderPanel.add(sizeCLbl);
		this.mainPanel.add(listHeaderPanel);
	}

	private void createAndAddSingleImageListPanel(
	        final OmeroThumbnailImageInfo temp) {
		final int width = this.mainPanel.getWidth() - 20;
		final int usableWidth = width - OmegaConstants.THUMBNAIL_SIZE;
		final int nameDim = usableWidth - 150 - (50 * 6) - 10;
		final int dateDim = 150 - 10;
		final int fieldDim = 50 - 10;

		final Font font = new Font("Tahoma", 0, 10);
		final ImageData imgData = temp.getImage().getImageData();
		final JPanel imageInfoPanel = new JPanel();
		imageInfoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		imageInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		imageInfoPanel.setBackground(Color.white);
		final int imgWidth = width;
		final int imgHeight = OmegaConstants.THUMBNAIL_SIZE;
		final Dimension panelDim = new Dimension(imgWidth, imgHeight);
		imageInfoPanel.setSize(panelDim);
		imageInfoPanel.setPreferredSize(panelDim);

		// image
		final OmeroBrowserSingleImagePanel singleImagePanel = new OmeroBrowserSingleImagePanel(
		        temp.getImageID(), temp.getImageName(), temp.getBufferedImage());

		// image name lbl
		final String imageName = OmegaStringUtilities.getImageName(temp
		        .getImageName());
		final JLabel imageNameLbl = new JLabel(imageName);
		final Dimension nameLblDim = new Dimension(nameDim, imgHeight);
		imageNameLbl.setSize(nameLblDim);
		imageNameLbl.setPreferredSize(nameLblDim);
		imageNameLbl.setFont(font);

		// date lbl
		final DateFormat format = new SimpleDateFormat(
		        OmegaConstants.OMEGA_DATE_FORMAT);
		String date = "No date saved";
		try {
			final Timestamp ts = imgData.getCreated();
			date = format.format(ts);
		} catch (final IllegalStateException ex) {
			// TODO manage ex

		}
		final JLabel dateLbl = new JLabel(date);
		final Dimension dateLblDim = new Dimension(dateDim, imgHeight);
		dateLbl.setSize(dateLblDim);
		dateLbl.setPreferredSize(dateLblDim);
		dateLbl.setFont(font);

		final String sizeX = String.valueOf(imgData.getDefaultPixels()
		        .getSizeX());
		final JLabel sizeXLbl = new JLabel(sizeX);
		final Dimension sizeXLblDim = new Dimension(fieldDim, imgHeight);
		sizeXLbl.setSize(sizeXLblDim);
		sizeXLbl.setPreferredSize(sizeXLblDim);
		sizeXLbl.setFont(font);

		final String sizeY = String.valueOf(imgData.getDefaultPixels()
		        .getSizeY());
		final JLabel sizeYLbl = new JLabel(sizeY);
		final Dimension sizeYLblDim = new Dimension(fieldDim, imgHeight);
		sizeYLbl.setSize(sizeYLblDim);
		sizeYLbl.setPreferredSize(sizeYLblDim);
		sizeYLbl.setFont(font);

		final String sizeZ = String.valueOf(imgData.getDefaultPixels()
		        .getSizeZ());
		final JLabel sizeZLbl = new JLabel(sizeZ);
		final Dimension sizeZLblDim = new Dimension(fieldDim, imgHeight);
		sizeZLbl.setSize(sizeZLblDim);
		sizeZLbl.setPreferredSize(sizeZLblDim);
		sizeZLbl.setFont(font);

		final String sizeT = String.valueOf(imgData.getDefaultPixels()
		        .getSizeT());
		final JLabel sizeTLbl = new JLabel(sizeT);
		final Dimension sizeTLblDim = new Dimension(fieldDim, imgHeight);
		sizeTLbl.setSize(sizeTLblDim);
		sizeTLbl.setPreferredSize(sizeTLblDim);
		sizeTLbl.setFont(font);

		final String sizeC = String.valueOf(imgData.getDefaultPixels()
		        .getSizeC());
		final JLabel sizeCLbl = new JLabel(sizeC);
		final Dimension sizeCLblDim = new Dimension(fieldDim, imgHeight);
		sizeCLbl.setSize(sizeCLblDim);
		sizeCLbl.setPreferredSize(sizeZLblDim);
		sizeCLbl.setFont(font);

		// SPT check
		final JPanel sptCheckPanel = new JPanel();
		sptCheckPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		sptCheckPanel.setBackground(Color.white);
		final Dimension sptDim = new Dimension(fieldDim, imgHeight);
		sptCheckPanel.setSize(sptDim);
		sptCheckPanel.setPreferredSize(sptDim);
		final JCheckBox checked = new JCheckBox();
		checked.setBackground(Color.white);
		// checked.setSelected(omegaData.containsImage(temp.getImageID()));
		sptCheckPanel.add(checked);
		this.checkboxList.add(checked);

		this.setCheckboxStatus(imageInfoPanel, temp, checked);
		this.addCheckboxListener(temp, checked);

		imageInfoPanel.add(sptCheckPanel);
		imageInfoPanel.add(singleImagePanel);
		imageInfoPanel.add(imageNameLbl);
		imageInfoPanel.add(dateLbl);
		imageInfoPanel.add(sizeXLbl);
		imageInfoPanel.add(sizeYLbl);
		imageInfoPanel.add(sizeZLbl);
		imageInfoPanel.add(sizeTLbl);
		imageInfoPanel.add(sizeCLbl);
		this.mainPanel.add(imageInfoPanel);
	}

	private void createAndAddSingleImagePreviewPanel(
	        final OmeroThumbnailImageInfo temp) {
		final Font font = new Font("Tahoma", 0, 10);

		final JPanel imageInfoPanel = new JPanel();
		imageInfoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		imageInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		imageInfoPanel.setBackground(Color.white);
		final int imgWidth = OmegaConstants.THUMBNAIL_SIZE + 20;
		final int imgHeight = OmegaConstants.THUMBNAIL_SIZE + 60;
		imageInfoPanel.setSize(new Dimension(imgWidth, imgHeight));
		imageInfoPanel.setPreferredSize(new Dimension(imgWidth, imgHeight));

		final Dimension d = new Dimension(OmegaConstants.THUMBNAIL_SIZE, 20);

		// image
		final OmeroBrowserSingleImagePanel singleImagePanel = new OmeroBrowserSingleImagePanel(
		        temp.getImageID(), temp.getImageName(), temp.getBufferedImage());

		// image name
		final String imageName = OmegaStringUtilities.getImageName(temp
		        .getImageName());
		final JLabel imageNameLbl = new JLabel(imageName);
		imageNameLbl.setSize(d);
		imageNameLbl.setPreferredSize(d);
		imageNameLbl.setFont(font);

		// SPT check
		final JPanel sptCheckPanel = new JPanel();
		sptCheckPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		sptCheckPanel.setBackground(Color.white);
		sptCheckPanel.setSize(d);
		sptCheckPanel.setPreferredSize(d);
		final JCheckBox checked = new JCheckBox();
		checked.setBackground(Color.white);
		// checked.setSelected(omegaData.containsImage(temp.getImageID()));

		this.setCheckboxStatus(imageInfoPanel, temp, checked);
		this.addCheckboxListener(temp, checked);

		sptCheckPanel.add(checked);
		this.checkboxList.add(checked);
		imageInfoPanel.add(singleImagePanel);
		imageInfoPanel.add(imageNameLbl);
		imageInfoPanel.add(sptCheckPanel);
		this.mainPanel.add(imageInfoPanel);
	}

	private void createAndAddSingleImagePanels() {
		if (this.imagesInfo == null) {
			this.numberOfImages = 0;
			return;
		}

		final Iterator<OmeroThumbnailImageInfo> iterator = this.imagesInfo
		        .iterator();

		if (this.isListView) {
			// this.mainPanel.setLayout(new GridLayout(this.numberOfImages, 1));
			this.createAndAddListHeaderPanel();
		} else {
			// final int width = this.mainPanel.getWidth();
			// final int imgPerRow = width / (OmegaConstants.THUMBNAIL_SIZE +
			// 25);
			// final int imgPerCol = this.numberOfImages / imgPerRow;
			// this.mainPanel.setLayout(new GridLayout(imgPerCol, imgPerRow));
		}

		while (iterator.hasNext()) {
			final OmeroThumbnailImageInfo temp = iterator.next();
			if (!this.isListView) {
				this.createAndAddSingleImagePreviewPanel(temp);
			} else {
				this.createAndAddSingleImageListPanel(temp);
			}
		}
	}

	/**
	 * Sets the images to display.
	 * 
	 * @param images
	 *            the images to display.
	 */
	public void setImagesAndRecreatePanels(
	        final List<OmeroThumbnailImageInfo> imageInfo// ,
	/* final OmegaData omegaData */) {
		// this.imageToBeLoadedList.clear();
		this.imagesInfo = imageInfo;
		this.setNumberOfImages();
		this.checkForResize();
		this.redrawImagePanels();
	}

	public void redrawImagePanels() {
		this.checkboxList.clear();
		this.mainPanel.removeAll();
		this.createAndAddSingleImagePanels();
		this.mainPanel.revalidate();
		this.mainPanel.repaint();
	}

	private void setNumberOfImages() {
		if (this.imagesInfo != null) {
			this.numberOfImages = this.imagesInfo.size();
		} else {
			this.numberOfImages = 0;
		}
	}

	protected void checkForResize() {
		int numOfImagesPerRow = 1;
		final int width = this.mainPanel.getWidth() - 25;
		final int height = this.mainPanel.getHeight() - 25;
		if (!this.isListView) {
			final BigDecimal widthReal = new BigDecimal(width);
			final BigDecimal thumbWidth = new BigDecimal(
			        OmegaConstants.THUMBNAIL_SIZE + 20);
			numOfImagesPerRow = widthReal.divide(thumbWidth, 0,
			        RoundingMode.DOWN).intValue();
		}
		int numOfImagesPerCol = 0;
		if (numOfImagesPerRow != 0) {
			final BigDecimal val1 = new BigDecimal(this.numberOfImages);
			final BigDecimal val2 = new BigDecimal(numOfImagesPerRow);
			numOfImagesPerCol = val1.divide(val2, 0, RoundingMode.UP)
			        .intValue();
		}

		int dimX = width;
		int offset = 20;
		if (this.isListView) {
			final int tempDimX = 900 + 20;
			if (dimX < tempDimX) {
				dimX = tempDimX;
			}
		} else {
			if (numOfImagesPerRow != 0) {
				dimX = numOfImagesPerRow
				        * (OmegaConstants.THUMBNAIL_SIZE + offset);
			}
			offset += 40;
		}

		int dimY = height;
		if (numOfImagesPerCol != 0) {
			dimY = (numOfImagesPerCol * (OmegaConstants.THUMBNAIL_SIZE + offset)) + 25;
		}
		final Dimension dim = new Dimension(dimX, dimY);
		this.mainPanel.setSize(dim);
		this.mainPanel.setPreferredSize(dim);
		// if (this.getParentContainer() instanceof JInternalFrame) {
		// final JInternalFrame intFrame = (JInternalFrame) this
		// .getParentContainer();
		// intFrame.repaint();
		// } else {
		// final JFrame frame = (JFrame) this.getParentContainer();
		// frame.repaint();
		// }
	}

	/**
	 * Browses the specified datasets.
	 * 
	 * @param id
	 *            The identifier of the dataset.
	 */
	public void browseDataset(final OmeroDatasetWrapper datasetWrapper) {
		this.datasetWrapper = datasetWrapper;
		this.setImagesAndRecreatePanels(null/* , null */);
		// this.createAndAddSingleImagePanels();
		final OmeroBrowerPanelImageLoader loader = new OmeroBrowerPanelImageLoader(
		        this.pluginPanel, this.gateway, datasetWrapper, true);
		final String loadingStatus = "Loading...1/" + loader.getImageToLoad();
		this.pluginPanel.updateMessageStatus(new OmegaMessageEvent(
		        loadingStatus));
		final Thread t = new Thread(loader);
		t.setName(loader.getClass().getSimpleName());
		OmegaLogFileManager.registerAsExceptionHandlerOnThread(t);
		t.start();
	}

	public void setCompSize(final Dimension size) {
		this.panelSize = size;
	}

	public void updateImagesSelection(final CheckBoxStatus datasetStatus) {
		this.datasetStatus = datasetStatus;
		this.updating = true;
		if (datasetStatus == CheckBoxStatus.INDETERMINATE)
			return;
		for (final JCheckBox checkbox : this.checkboxList) {
			if (datasetStatus == CheckBoxStatus.SELECTED) {
				checkbox.setSelected(true);
			} else {
				checkbox.setSelected(false);
			}
		}
		this.updating = false;
	}

	private boolean isImageLoaded(final long imageID) {
		for (final OmegaImage img : this.loadedImages) {
			if (img.getElementID() == imageID)
				return true;
		}
		return false;
	}

	public void updateLoadedElements(final List<OmegaImage> loadedImages) {
		this.loadedImages.clear();
		this.loadedImages.addAll(loadedImages);
		this.imageToBeLoadedList.clear();
		this.redrawImagePanels();
	}
}
