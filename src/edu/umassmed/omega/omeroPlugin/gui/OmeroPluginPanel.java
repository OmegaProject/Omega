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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.RootPaneContainer;
import javax.swing.ScrollPaneConstants;

import omero.ServerError;
import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.GroupData;
import pojos.ImageData;
import pojos.PixelsData;
import pojos.ProjectData;
import edu.umassmed.omega.commons.OmegaEvents;
import edu.umassmed.omega.commons.eventSystem.OmegaDataChangedEvent;
import edu.umassmed.omega.commons.eventSystem.OmegaGatewayEvent;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.dataNew.OmegaData;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaElement;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenter;
import edu.umassmed.omega.dataNew.coreElements.OmegaExperimenterGroup;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.dataNew.coreElements.OmegaImagePixels;
import edu.umassmed.omega.dataNew.coreElements.OmegaProject;
import edu.umassmed.omega.omeroPlugin.OmeroGateway;
import edu.umassmed.omega.omeroPlugin.OmeroPlugin;
import edu.umassmed.omega.omeroPlugin.data.OmeroImageWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroThumbnailImageInfo;

public class OmeroPluginPanel extends GenericPluginPanel {

	private static final long serialVersionUID = -5740459087763362607L;

	private JSplitPane mainPanel;
	private JMenu connectionMenu, loadableUserMenu;
	private JMenuItem connectMItem, notLoggedVisualMItem;

	private OmeroListPanel projectListPanel;
	private OmeroBrowserPanel browserPanel;
	private final OmeroConnectionDialog connectionDialog;

	private JButton loadImages_butt, loadAndSelectImages_butt, close_butt;

	private final OmeroGateway gateway;

	private final OmegaData omegaData;

	// the collection of already loaded datasets
	// TODO implement a caching system of loaded dataset to avoid loading each
	// time
	private final Map<Long, ArrayList<OmeroThumbnailImageInfo>> loadedDatasets = new HashMap<Long, ArrayList<OmeroThumbnailImageInfo>>();

	public OmeroPluginPanel(final RootPaneContainer parent,
	        final OmeroPlugin plugin, final OmeroGateway gateway,
	        final OmegaData omegaData, final int index) {
		super(parent, plugin, index);

		this.gateway = gateway;
		this.omegaData = omegaData;
		this.connectionDialog = new OmeroConnectionDialog(this, gateway);

		this.setPreferredSize(new Dimension(750, 500));
		this.setLayout(new BorderLayout());
		this.createMenu();
		this.createAndAddWidgets();
		this.addListeners();
	}

	private void createMenu() {
		final JMenuBar menu = super.getMenu();

		this.connectionMenu = new JMenu("Connection");
		this.connectMItem = new JMenuItem("Manage server connection");
		this.connectionMenu.add(this.connectMItem);

		this.notLoggedVisualMItem = new JMenuItem(
		        "Login to load visualization option");
		this.loadableUserMenu = new JMenu("Data");
		this.loadableUserMenu.add(this.notLoggedVisualMItem);

		menu.add(this.connectionMenu);
		menu.add(this.loadableUserMenu);
	}

	protected void updateVisualizationMenu() throws ServerError {
		if (!this.gateway.isConnected()) {
			this.loadableUserMenu.add(this.notLoggedVisualMItem);
			return;
		}
		this.loadableUserMenu.removeAll();
		final ExperimenterData loggedUser = this.gateway.getExperimenter();
		final List<GroupData> groups = this.gateway.getGroups();

		for (final GroupData group : groups) {
			final JMenu menuItem = new JMenu(group.getName());
			final List<ExperimenterData> exps = this.gateway
			        .getExperimenters(group);
			for (final ExperimenterData exp : exps) {
				final JCheckBoxMenuItem subMenuItem = new JCheckBoxMenuItem(
				        exp.getFirstName() + " " + exp.getLastName());
				subMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent evt) {
						if (subMenuItem.isSelected()) {
							try {
								OmeroPluginPanel.this.projectListPanel
								        .addExperimenterData(exp);
							} catch (final ServerError e) {
								e.printStackTrace();
							}
						} else {
							OmeroPluginPanel.this.projectListPanel
							        .removeExperimenterData(exp);
						}
						OmeroPluginPanel.this.checkSameUserInOtherGroups(exp,
						        subMenuItem.isSelected());
					}
				});
				if (exp.getId() == loggedUser.getId()) {
					subMenuItem.setSelected(true);
				}
				menuItem.add(subMenuItem);
			}
			this.loadableUserMenu.add(menuItem);
		}
		this.projectListPanel.resetExperimenterData();
		this.projectListPanel.addExperimenterData(loggedUser);
	}

	private void checkSameUserInOtherGroups(final ExperimenterData exp,
	        final boolean selected) {
		final String name = exp.getFirstName() + " " + exp.getLastName();
		for (int i = 0; i < this.loadableUserMenu.getItemCount(); i++) {
			final JMenuItem menuItem = this.loadableUserMenu.getItem(i);
			if (!(menuItem instanceof JMenu))
				return;
			final JMenu menu = (JMenu) menuItem;
			for (int k = 0; k < menu.getItemCount(); k++) {
				final JMenuItem subMenuItem = menu.getItem(k);
				if (subMenuItem.getText().equals(name)) {
					subMenuItem.setSelected(selected);
				}
			}
		}
	}

	public void createAndAddWidgets() {
		this.browserPanel = new OmeroBrowserPanel(this.getParentContainer(),
		        this.gateway);
		final JScrollPane scrollPaneBrowser = new JScrollPane(this.browserPanel);
		scrollPaneBrowser
		        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneBrowser
		        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.projectListPanel = new OmeroListPanel(this.getParentContainer(),
		        this.browserPanel, this.gateway);
		final JScrollPane scrollPaneList = new JScrollPane(
		        this.projectListPanel);
		scrollPaneList
		        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneList
		        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.mainPanel.setDividerLocation(0.25);
		this.mainPanel.setLeftComponent(scrollPaneList);
		this.mainPanel.setRightComponent(scrollPaneBrowser);
		this.add(this.mainPanel, BorderLayout.CENTER);

		// TODO add button to open isSelected images
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		this.loadImages_butt = new JButton("Load images");
		buttonPanel.add(this.loadImages_butt);

		this.loadAndSelectImages_butt = new JButton("Load and select images");
		buttonPanel.add(this.loadAndSelectImages_butt);

		this.close_butt = new JButton("Close");
		buttonPanel.add(this.close_butt);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void addListeners() {
		this.loadImages_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					OmeroPluginPanel.this.loadDataAndFireEvent(false);
				} catch (final ServerError err) {
					err.printStackTrace();
					return;
				}
			}
		});
		this.loadAndSelectImages_butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					OmeroPluginPanel.this.loadDataAndFireEvent(true);
				} catch (final ServerError err) {
					err.printStackTrace();
					return;
				}
			}
		});
		this.connectMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				OmeroPluginPanel.this.showConnectionPanel();
			}
		});
		this.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(
				        OmegaEvents.PROPERTY_CONNECTION)) {
					try {
						OmeroPluginPanel.this.updateVisualizationMenu();
					} catch (final ServerError e) {
						e.printStackTrace();
					}
					if (OmeroPluginPanel.this.gateway.isConnected()) {
						OmegaExperimenter experimenter = null;
						try {
							final ExperimenterData experimenterData = OmeroPluginPanel.this.gateway
							        .getExperimenter();
							experimenter = new OmegaExperimenter(
							        experimenterData.getId(), experimenterData
							                .getFirstName(), experimenterData
							                .getLastName());
						} catch (final ServerError e) {
							// TODO Gestire errore
							e.printStackTrace();
						}
						OmeroPluginPanel.this.getPlugin().fireEvent(
						        new OmegaGatewayEvent(OmeroPluginPanel.this
						                .getPlugin(),
						                OmegaGatewayEvent.STATUS_CONNECTED,
						                experimenter));
					} else {
						OmeroPluginPanel.this.getPlugin().fireEvent(
						        new OmegaGatewayEvent(OmeroPluginPanel.this
						                .getPlugin(),
						                OmegaGatewayEvent.STATUS_DISCONNECTED));
					}
				}
			}
		});
		this.mainPanel.addPropertyChangeListener(
		        JSplitPane.DIVIDER_LOCATION_PROPERTY,
		        new PropertyChangeListener() {

			        @Override
			        public void propertyChange(final PropertyChangeEvent evt) {
				        OmeroPluginPanel.this.browserPanel
				                .setCompSize(OmeroPluginPanel.this.mainPanel
				                        .getRightComponent().getSize());
				        OmeroPluginPanel.this.browserPanel.checkForResize();
			        }
		        });
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.browserPanel.updateParentContainer(parent);
		this.projectListPanel.updateParentContainer(parent);
	}

	public void showConnectionPanel() {
		final RootPaneContainer parent = this.getParentContainer();

		Point parentLocOnScren = null;
		Dimension parentSize = null;
		if (parent instanceof JInternalFrame) {
			final JInternalFrame intFrame = (JInternalFrame) parent;
			parentLocOnScren = intFrame.getLocationOnScreen();
			parentSize = intFrame.getSize();
		} else {
			final JFrame frame = (JFrame) parent;
			parentLocOnScren = frame.getLocationOnScreen();
			parentSize = frame.getSize();
		}
		final int x = parentLocOnScren.x;
		final int y = parentLocOnScren.y;
		final int xOffset = (parentSize.width / 2)
		        - (this.connectionDialog.getSize().width / 2);
		final int yOffset = (parentSize.height / 2)
		        - (this.connectionDialog.getSize().height / 2);
		final Point dialogPos = new Point(x + xOffset, y + yOffset);
		this.connectionDialog.setLocation(dialogPos);
		this.connectionDialog.validate();
		this.connectionDialog.repaint();
		this.connectionDialog.setVisible(true);
	}

	@Override
	public void onCloseOperation() {
		this.connectionDialog.setVisible(false);
	}

	private boolean loadGroups(final ExperimenterData experimenterData,
	        final OmegaExperimenter experimenter) {
		boolean dataChanged = false;
		List<GroupData> groupsData;
		try {
			groupsData = this.gateway.getGroups();
		} catch (final ServerError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dataChanged;
		}// experimenterData.getGroups();

		for (final GroupData groupData : groupsData) {
			OmegaExperimenterGroup group = experimenter.getGroup(groupData
			        .getId());

			if (group != null) {
				if (!experimenter.containsGroup(groupData.getId())) {
					experimenter.addGroup(group);
					dataChanged = true;
				}
				continue;
			}

			dataChanged = true;

			final Set<ExperimenterData> leadersData = groupData.getLeaders();
			final List<OmegaExperimenter> leaders = new ArrayList<OmegaExperimenter>();
			for (final ExperimenterData leaderData : leadersData) {
				final OmegaExperimenter leader = new OmegaExperimenter(
				        leaderData.getId(), leaderData.getFirstName(),
				        leaderData.getLastName());
				leaders.add(leader);
			}

			group = new OmegaExperimenterGroup(groupData.getId(), leaders);
			this.omegaData.addExperimenterGroup(group);
			experimenter.addGroup(group);

			for (final OmegaExperimenter leader : leaders) {
				leader.addGroup(group);
				this.omegaData.addExperimenter(leader);
			}
		}
		return dataChanged;
	}

	private void loadExperimenterAndGroups(
	        final ExperimenterData experimenterData) {
		// Create all groups for the actual user
		// Create leaders for the groups
		// Add everything to the main data
		List<GroupData> groupsData;
		try {
			groupsData = this.gateway.getGroups();
		} catch (final ServerError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}// experimenterData.getGroups();
		final List<OmegaExperimenterGroup> groups = new ArrayList<OmegaExperimenterGroup>();
		for (final GroupData groupData : groupsData) {

			OmegaExperimenterGroup group = this.omegaData
			        .getExperimenterGroup(groupData.getId());

			final Set<ExperimenterData> leadersData = groupData.getLeaders();

			if (group != null) {
				for (final ExperimenterData leaderData : leadersData) {
					if (!group.containsLeader(leaderData.getId())) {
						final OmegaExperimenter leader = new OmegaExperimenter(
						        leaderData.getId(), leaderData.getFirstName(),
						        leaderData.getLastName());
						group.addLeader(leader);
					}
				}
				groups.add(group);
				continue;
			}

			final List<OmegaExperimenter> leaders = new ArrayList<OmegaExperimenter>();
			for (final ExperimenterData leaderData : leadersData) {
				final OmegaExperimenter leader = new OmegaExperimenter(
				        leaderData.getId(), leaderData.getFirstName(),
				        leaderData.getLastName());
				leaders.add(leader);
			}

			group = new OmegaExperimenterGroup(groupData.getId(), leaders);
			groups.add(group);
			this.omegaData.addExperimenterGroup(group);

			for (final OmegaExperimenter leader : leaders) {
				leader.addGroup(group);
				this.omegaData.addExperimenter(leader);
			}
		}

		// Create the actual user with his groups
		// Add it to the main data
		final OmegaExperimenter experimenter = new OmegaExperimenter(
		        experimenterData.getId(), experimenterData.getFirstName(),
		        experimenterData.getLastName(), groups);
		this.omegaData.addExperimenter(experimenter);
	}

	private void loadDataAndFireEvent(final boolean hasToSelect)
	        throws ServerError {
		boolean dataChanged = false;
		final List<OmegaElement> loadedElements = new ArrayList<OmegaElement>();

		// TODO add all checks and sub checks
		final ExperimenterData experimenterData = this.gateway
		        .getExperimenter();

		final OmegaExperimenter experimenter = this.omegaData
		        .getExperimenter(experimenterData.getId());
		if (experimenter != null) {
			dataChanged = this.loadGroups(experimenterData, experimenter);
		} else {
			this.loadExperimenterAndGroups(experimenterData);
			dataChanged = true;
		}

		// Create pixels, image, dataset and project for the actual images to
		// load
		// Add it to the main data
		for (final OmeroImageWrapper imageWrapper : OmeroPluginPanel.this.browserPanel
		        .getToBeOpened()) {
			final ProjectData projectData = imageWrapper.getProjectData();
			final DatasetData datasetData = imageWrapper.getDatasetData();
			final ImageData imageData = imageWrapper.getImageData();

			// TODO introdurre controlli se project/dataset/image gia presenti

			OmegaProject project = this.omegaData.getProject(projectData
			        .getId());
			OmegaDataset dataset = this.omegaData.getDataset(datasetData
			        .getId());
			OmegaImage image = this.omegaData.getImage(imageData.getId());

			// final List<Long> ids = new ArrayList<Long>();
			// ids.add(imageData.getId());
			// final ImageData dlImage = this.gateway.getImages(datasetData,
			// ids)
			// .get(0);

			// Create pixels
			List<OmegaImagePixels> pixelsList;
			if (image == null) {
				pixelsList = new ArrayList<OmegaImagePixels>();
			} else {
				pixelsList = image.getPixels();
			}

			for (final PixelsData pixelsData : imageData.getAllPixels()) {
				if ((image != null) && image.containsPixels(pixelsData.getId())) {
					continue;
				}
				final OmegaImagePixels pixels = new OmegaImagePixels(
				        pixelsData.getId(), pixelsData.getPixelType(),
				        pixelsData.getSizeX(), pixelsData.getSizeY(),
				        pixelsData.getSizeZ(), pixelsData.getSizeC(),
				        pixelsData.getSizeT(), pixelsData.getPixelSizeX(),
				        pixelsData.getPixelSizeY(), pixelsData.getPixelSizeZ());
				final int defaultZ = this.gateway.getDefaultZ(pixelsData
				        .getId());
				pixels.setSelectedZ(defaultZ);
				final int defaultC = pixelsData.getSizeC() - 1;
				pixels.setSelectedC(defaultC);
				pixelsList.add(pixels);
			}

			// Create image
			if (image == null) {
				image = new OmegaImage(imageData.getId(), imageData.getName(),
				        experimenter, pixelsList);
				dataChanged = true;
			} else {
				for (final OmegaImagePixels pixels : pixelsList) {
					if (!image.containsPixels(pixels.getElementID())) {
						image.addPixels(pixels);
					}
				}
			}

			for (final OmegaImagePixels pixels : pixelsList) {
				pixels.setParentImage(image);
			}

			if (hasToSelect && !loadedElements.contains(image)) {
				loadedElements.add(image);
			}

			// Create dataset
			if (dataset == null) {
				final List<OmegaImage> images = new ArrayList<OmegaImage>();
				images.add(image);
				dataset = new OmegaDataset(datasetData.getId(),
				        datasetData.getName(), images);
				image.addParentDataset(dataset);
				dataChanged = true;
			} else {
				if (!image.getParentDatasets().contains(dataset)) {
					image.addParentDataset(dataset);
				}
				if (!dataset.containsImage(image.getElementID())) {
					dataset.addImage(image);
					dataChanged = true;
				}
			}

			if (hasToSelect && !loadedElements.contains(dataset)) {
				loadedElements.add(dataset);
			}

			if (project == null) {
				// Create project
				final List<OmegaDataset> datasets = new ArrayList<OmegaDataset>();
				datasets.add(dataset);
				project = new OmegaProject(projectData.getId(),
				        projectData.getName(), datasets);
				this.omegaData.addProject(project);
				dataChanged = true;
			} else {
				if (!project.containsDataset(dataset.getElementID())) {
					project.addDataset(dataset);
					dataChanged = true;
				}
			}
			dataset.setParentProject(project);

			if (hasToSelect && !loadedElements.contains(project)) {
				loadedElements.add(project);
			}
		}

		if (dataChanged) {
			this.getPlugin()
			        .fireEvent(
			                new OmegaDataChangedEvent(this.getPlugin(),
			                        loadedElements));
		}
	}
}
