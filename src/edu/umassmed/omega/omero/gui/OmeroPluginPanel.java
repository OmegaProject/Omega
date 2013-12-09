package edu.umassmed.omega.omero.gui;

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
import java.util.logging.Level;

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
import omero.api.RenderingEnginePrx;
import pojos.ExperimenterData;
import pojos.GroupData;
import pojos.ImageData;

import com.galliva.gallibrary.GLogManager;

import edu.umassmed.omega.commons.OmegaEvents;
import edu.umassmed.omega.commons.gui.GenericPluginPanel;
import edu.umassmed.omega.omero.OmeroGateway;
import edu.umassmed.omega.omero.OmeroPlugin;
import edu.umassmed.omega.omero.data.OmeroThumbnailImageInfo;

public class OmeroPluginPanel extends GenericPluginPanel {

	private static final long serialVersionUID = -5740459087763362607L;

	private OmeroPlugin plugin;

	private JSplitPane mainPanel;
	private JMenu connectionMenu, loadableUserMenu;
	private JMenuItem connectMItem, notLoggedVisualMItem;

	private OmeroListPanel projectListPanel;
	private OmeroBrowserPanel browserPanel;
	private final OmeroConnectionDialog connectionDialog;

	private JButton openSelectedImageButt, closeButt;

	// entry point to access the various services
	private final OmeroGateway gateway;

	// reference to the Rendering Engine
	private RenderingEnginePrx engine;

	// all the images owned by the user currently logged on
	private List<ImageData> images = null;
	// the collection of already loaded datasets
	private final Map<Long, ArrayList<OmeroThumbnailImageInfo>> loadedDatasets = new HashMap<Long, ArrayList<OmeroThumbnailImageInfo>>();

	// the current browsed dataset
	private final String currentBrowsedDataset = "";

	public OmeroPluginPanel(final RootPaneContainer parent,
	        final OmeroPlugin plugin, final int index) {
		super(parent, plugin, index);

		this.gateway = new OmeroGateway();

		this.connectionDialog = new OmeroConnectionDialog(this, this.gateway);

		try {
			this.images = this.gateway.getImages();
		} catch (final Exception e) {
			GLogManager.log(
			        "unable to load the images from the gateway: "
			                + e.toString(), Level.SEVERE);
			// TODO Manage exception
		}

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
								// TODO Auto-generated catch block
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

		// TODO add button to open selected images
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		this.openSelectedImageButt = new JButton("Open selected images");
		buttonPanel.add(this.openSelectedImageButt);

		this.closeButt = new JButton("Close");
		buttonPanel.add(this.closeButt);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void addListeners() {
		this.openSelectedImageButt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				for (final ImageData imageData : OmeroPluginPanel.this.browserPanel
				        .getToBeProcessed()) {
					System.out.println(imageData.getName());
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
						// TODO Auto-generated catch block
						e.printStackTrace();
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

	public OmeroGateway getGateway() {
		return this.gateway;
	}

	public RenderingEnginePrx getEngine() {
		return this.engine;
	}
}
