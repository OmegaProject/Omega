package edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;

import edu.umassmed.omega.commons.OmegaPlugin;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaFrame;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;

public class OmegaDataBrowserPluginPanel extends GenericPluginPanel {

	private static final long serialVersionUID = 4804154980131328463L;

	private JMenu visualizationMenu;
	private JMenuItem refreshMItem;

	private OmegaDataBrowserOptionsPanel optionsPanel;

	private GenericPanel projectBrowserPanel, datasetBrowserPanel,
	        imageBrowserPanel, pixelsBrowserPanel, frameBrowserPanel;
	private GenericBrowserModel projectBrowserModel, datasetBrowserModel,
	        imageBrowserModel, pixelsBrowserModel, frameBrowserModel;
	private GenericBrowserTable projectBrowserList, datasetBrowserList,
	        imageBrowserList, pixelsBrowserList, frameBrowserList;

	private final OmegaData omegaData;

	private OmegaProject selectedProject;
	private OmegaDataset selectedDataset;
	private OmegaImage selectedImage;
	private final OmegaImagePixels selectedPixels;

	public OmegaDataBrowserPluginPanel(final RootPaneContainer parent,
	        final OmegaPlugin plugin, final OmegaData omegaData, final int index) {
		super(parent, plugin, index);

		this.omegaData = omegaData;
		this.selectedProject = null;
		this.selectedDataset = null;
		this.selectedImage = null;
		this.selectedPixels = null;

		this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		this.createMenu();
		this.createAndAddWidgets();
		this.addListeners();
	}

	private void createMenu() {
		final JMenuBar menu = super.getMenu();

		this.visualizationMenu = new JMenu("Visualization");
		this.refreshMItem = new JMenuItem("Refresh data");
		this.visualizationMenu.add(this.refreshMItem);

		menu.add(this.visualizationMenu);
	}

	public void createAndAddWidgets() {
		this.optionsPanel = new OmegaDataBrowserOptionsPanel(
		        this.getParentContainer());

		this.add(this.optionsPanel, BorderLayout.NORTH);

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		final JScrollPane pane = new JScrollPane(mainPanel);

		// TODO finish here

		// Project browser
		this.createProjectBrowserPanel();
		this.createDatasetBrowserPanel();
		this.createImageBrowserPanel();

		this.pixelsBrowserPanel = new GenericPanel(this.getParentContainer());
		this.frameBrowserPanel = new GenericPanel(this.getParentContainer());

		mainPanel.add(this.projectBrowserPanel);
		mainPanel.add(this.datasetBrowserPanel);
		mainPanel.add(this.imageBrowserPanel);
		mainPanel.add(this.pixelsBrowserPanel);
		mainPanel.add(this.frameBrowserPanel);

		// this.projectListPanel = new OmeroListPanel(this.getParentContainer(),
		// this.browserPanel, this.gateway);
		// final JScrollPane scrollPaneList = new JScrollPane(
		// this.projectListPanel);
		// scrollPaneList
		// .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// scrollPaneList
		// .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		//
		// this.mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		// this.mainPanel.setDividerLocation(0.25);
		// this.mainPanel.setLeftComponent(scrollPaneList);
		// this.mainPanel.setRightComponent(scrollPaneBrowser);
		// this.add(this.mainPanel, BorderLayout.CENTER);
		//
		// // TODO add button to open selected images
		// final JPanel buttonPanel = new JPanel();
		// buttonPanel.setLayout(new FlowLayout());
		//
		// this.openSelectedImageButt = new JButton("Open selected images");
		// buttonPanel.add(this.openSelectedImageButt);
		//
		// this.closeButt = new JButton("Close");
		// buttonPanel.add(this.closeButt);
		//
		// this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(pane, BorderLayout.CENTER);
	}

	private void createProjectBrowserPanel() {
		this.projectBrowserPanel = new GenericPanel(this.getParentContainer());
		// this.projectBrowserPanel.setLayout(new BoxLayout(
		// this.projectBrowserPanel, BoxLayout.PAGE_AXIS));
		this.projectBrowserPanel.setLayout(new BorderLayout());
		this.projectBrowserPanel.setBorder(new TitledBorder("Projects"));

		this.projectBrowserList = new GenericBrowserTable();
		this.projectBrowserList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.projectBrowserModel = new GenericBrowserModel(
		        this.projectBrowserList);
		this.projectBrowserList.setModel(this.projectBrowserModel);
		this.updateProjectsModel();

		// final JScrollPane listScroller = new JScrollPane(
		// this.projectBrowserList);

		this.projectBrowserPanel.add(this.projectBrowserList,
		        BorderLayout.CENTER);
		this.projectBrowserList.resizeContainer();
	}

	private void createDatasetBrowserPanel() {
		this.datasetBrowserPanel = new GenericPanel(this.getParentContainer());
		// this.datasetBrowserPanel.setLayout(new BoxLayout(
		// this.datasetBrowserPanel, BoxLayout.PAGE_AXIS));
		this.datasetBrowserPanel.setLayout(new BorderLayout());
		this.datasetBrowserPanel.setBorder(new TitledBorder("Datasets"));

		this.datasetBrowserList = new GenericBrowserTable();
		this.datasetBrowserList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.datasetBrowserModel = new GenericBrowserModel(
		        this.datasetBrowserList);
		this.datasetBrowserList.setModel(this.datasetBrowserModel);
		// this.updateDatasetsModel();

		// final JScrollPane listScroller = new JScrollPane(
		// this.datasetBrowserList);

		this.datasetBrowserPanel.add(this.datasetBrowserList,
		        BorderLayout.CENTER);
		this.datasetBrowserList.resizeContainer();
	}

	private void createImageBrowserPanel() {
		this.imageBrowserPanel = new GenericPanel(this.getParentContainer());

		// this.imageBrowserPanel.setLayout(new
		// BoxLayout(this.imageBrowserPanel,
		// BoxLayout.PAGE_AXIS));
		this.imageBrowserPanel.setLayout(new BorderLayout());
		this.imageBrowserPanel.setBorder(new TitledBorder("Images"));

		this.imageBrowserList = new GenericBrowserTable();
		this.imageBrowserList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.imageBrowserModel = new GenericBrowserModel(this.imageBrowserList);
		this.imageBrowserList.setModel(this.imageBrowserModel);
		// this.updateImagesModel();

		// final JScrollPane listScroller = new
		// JScrollPane(this.imageBrowserList);

		this.imageBrowserPanel.add(this.imageBrowserList, BorderLayout.CENTER);
		this.imageBrowserList.resizeContainer();
	}

	private void updateProjectsModel() {
		final Map<Long, String> projectElements = new LinkedHashMap<Long, String>();
		for (final OmegaProject project : this.omegaData.getProjects()) {
			projectElements.put(project.getElementID(), project.getName());
		}
		this.projectBrowserModel.update(projectElements);
	}

	private void updateDatasetsModel(final boolean isSelected) {
		final boolean selectAllItems = this.optionsPanel.isSelectAllSubItems();
		final Map<Long, String> datasetElements = new LinkedHashMap<Long, String>();
		for (final OmegaDataset dataset : this.selectedProject.getDatasets()) {
			datasetElements.put(dataset.getElementID(), dataset.getName());
		}
		this.datasetBrowserModel.update(datasetElements, isSelected,
		        selectAllItems);
	}

	private void updateImagesModel(final boolean isSelected) {
		final boolean selectAllItems = this.optionsPanel.isSelectAllSubItems();
		final Map<Long, String> imageElements = new LinkedHashMap<Long, String>();
		for (final OmegaImage image : this.selectedDataset.getImages()) {
			imageElements.put(image.getElementID(), image.getName());
		}
		this.imageBrowserModel
		        .update(imageElements, isSelected, selectAllItems);
	}

	private void updatePixelsModel(final boolean isSelected) {
		final boolean selectAllItems = this.optionsPanel.isSelectAllSubItems();
		final Map<Long, String> pixelsElements = new LinkedHashMap<Long, String>();
		for (final OmegaImagePixels pixels : this.selectedImage.getPixels()) {
			pixelsElements.put(pixels.getElementID(), pixels.getPixelsType());
		}
		this.pixelsBrowserModel.update(pixelsElements, isSelected,
		        selectAllItems);
	}

	private void updateFramesModel(final boolean isSelected) {
		final boolean selectAllItems = this.optionsPanel.isSelectAllSubItems();
		final Map<Long, String> frameElements = new LinkedHashMap<Long, String>();
		for (final OmegaFrame frame : this.selectedPixels.getFrames()) {
			frameElements
			        .put(frame.getElementID(), frame.getIndex().toString());
		}
		this.pixelsBrowserModel.update(frameElements, isSelected,
		        selectAllItems);
	}

	public void fireUpdate() {
		this.updateProjectsModel();
		if (this.selectedProject != null) {
			this.projectBrowserModel.selectId(this.selectedProject
			        .getElementID());
			this.updateDatasetsModel(this.projectBrowserModel.getSelectedItem()
			        .isSelected());
			if (this.selectedDataset != null) {
				this.datasetBrowserModel.selectId(this.selectedDataset
				        .getElementID());
				this.updateImagesModel(this.datasetBrowserModel
				        .getSelectedItem().isSelected());
				if (this.selectedImage != null) {
					this.imageBrowserModel.selectId(this.selectedImage
					        .getElementID());
				}
			}
		}
	}

	private void addListeners() {
		this.projectBrowserList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				final GenericBrowserItem item = OmegaDataBrowserPluginPanel.this.projectBrowserModel
				        .getSelectedItem();
				if (item == null)
					return;
				final long id = item.getId();
				OmegaDataBrowserPluginPanel.this.selectedProject = OmegaDataBrowserPluginPanel.this.omegaData
				        .getProject(id);
				OmegaDataBrowserPluginPanel.this.updateDatasetsModel(item
				        .isSelected());
				if (OmegaDataBrowserPluginPanel.this.selectedDataset != null) {
					OmegaDataBrowserPluginPanel.this.datasetBrowserModel
					        .selectId(OmegaDataBrowserPluginPanel.this.selectedDataset
					                .getElementID());
					OmegaDataBrowserPluginPanel.this
					        .updateImagesModel(OmegaDataBrowserPluginPanel.this.datasetBrowserModel
					                .getSelectedItem().isSelected());
				}
			}
		});
		this.datasetBrowserList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				final GenericBrowserItem item = OmegaDataBrowserPluginPanel.this.datasetBrowserModel
				        .getSelectedItem();
				if (item == null)
					return;
				final long id = item.getId();
				OmegaDataBrowserPluginPanel.this.selectedDataset = OmegaDataBrowserPluginPanel.this.omegaData
				        .getDataset(id);
				OmegaDataBrowserPluginPanel.this.updateImagesModel(item
				        .isSelected());
				if (OmegaDataBrowserPluginPanel.this.selectedImage != null) {
					OmegaDataBrowserPluginPanel.this.imageBrowserModel
					        .selectId(OmegaDataBrowserPluginPanel.this.selectedImage
					                .getElementID());
				}
			}
		});
		this.imageBrowserList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				final GenericBrowserItem item = OmegaDataBrowserPluginPanel.this.imageBrowserModel
				        .getSelectedItem();
				if (item == null)
					return;
				final long id = item.getId();
				OmegaDataBrowserPluginPanel.this.selectedImage = OmegaDataBrowserPluginPanel.this.omegaData
				        .getImage(id);
				System.out.println("Selected: " + item.isSelected());
				System.out
				        .println(OmegaDataBrowserPluginPanel.this.selectedImage
				                .getName());
			}
		});
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.projectBrowserPanel.updateParentContainer(parent);
		this.datasetBrowserPanel.updateParentContainer(parent);
		this.imageBrowserPanel.updateParentContainer(parent);
		this.pixelsBrowserPanel.updateParentContainer(parent);
		this.frameBrowserPanel.updateParentContainer(parent);
	}

	@Override
	public void onCloseOperation() {

	}

}
