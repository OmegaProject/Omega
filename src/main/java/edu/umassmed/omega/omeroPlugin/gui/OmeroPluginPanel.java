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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.RootPaneContainer;

import edu.umassmed.omega.commons.constants.OmegaConstantsEvent;
import edu.umassmed.omega.commons.data.OmegaData;
import edu.umassmed.omega.commons.data.coreElements.OmegaDataset;
import edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import edu.umassmed.omega.commons.data.coreElements.OmegaExperimenter;
import edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventDataChanged;
import edu.umassmed.omega.commons.eventSystem.events.OmegaPluginEventGateway;
import edu.umassmed.omega.commons.gui.dialogs.GenericConfirmationDialog;
import edu.umassmed.omega.omero.commons.OmeroGateway;
import edu.umassmed.omega.omero.commons.OmeroImporterUtilities;
import edu.umassmed.omega.omero.commons.data.OmeroImageWrapper;
import edu.umassmed.omega.omero.commons.gui.OmeroAbstractBrowserInterface;
import edu.umassmed.omega.omero.commons.gui.OmeroPanel;
import edu.umassmed.omega.omero.commons.gui.OmeroPluginGUIConstants;
import edu.umassmed.omega.omeroPlugin.OmeroPlugin;

public class OmeroPluginPanel extends OmeroPanel implements
OmeroAbstractBrowserInterface {

	public static final String LOADING_WARNING_OMEGA = "You are trying to load more than 5 images, this could slow down the application, are you sure?";
	private static final long serialVersionUID = -5740459087763362607L;

	private JMenu connectionMenu;
	private JMenuItem connectMItem;

	private final OmeroConnectionDialog connectionDialog;
	private final GenericConfirmationDialog confirmDialog;

	private final OmegaData omegaData;

	public OmeroPluginPanel(final RootPaneContainer parent,
			final OmeroPlugin plugin, final OmeroGateway gateway,
			final OmegaData omegaData, final int index) {
		super(parent, plugin, index, gateway);

		this.omegaData = omegaData;
		this.connectionDialog = new OmeroConnectionDialog(
				this.getParentContainer(), this, gateway);
		this.confirmDialog = new GenericConfirmationDialog(
				this.getParentContainer(), "Image loading warning",
				OmeroPluginPanel.LOADING_WARNING_OMEGA, true);

		this.setPreferredSize(new Dimension(750, 500));
		// this.setLayout(new BorderLayout());
		this.createMenu();
		this.addListeners();
	}

	private void createMenu() {
		final JMenuBar menu = super.getMenu();

		this.connectionMenu = new JMenu(OmeroPluginGUIConstants.MENU_CONNECTION);
		this.connectMItem = new JMenuItem(
				OmeroPluginGUIConstants.MENU_CONNECTION_MANAGER);
		this.connectionMenu.add(this.connectMItem);

		menu.add(this.connectionMenu);
	}

	private void addListeners() {
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
						OmegaConstantsEvent.PROPERTY_CONNECTION)) {
					OmeroPluginPanel.this.handlePropertyConnection();
				}
			}
		});
	}

	private void handlePropertyConnection() {
		if (this.getGateway().isConnected()) {
			final OmegaExperimenter experimenter = OmeroImporterUtilities
					.getExperimenter(this.getGateway());
			this.getPlugin().fireEvent(
			        new OmegaPluginEventGateway(this.getPlugin(),
			                OmegaPluginEventGateway.STATUS_CONNECTED,
			                experimenter));
		} else {
			OmeroPluginPanel.this.getPlugin().fireEvent(
			        new OmegaPluginEventGateway(this.getPlugin(),
			                OmegaPluginEventGateway.STATUS_DISCONNECTED));
		}
		try {
			this.updateVisualizationMenu();
		} catch (final ServerError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.connectionDialog.updateParentContainer(parent);
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

	@Override
	protected void loadData(final boolean hasToSelect) throws ServerError {
		boolean dataChanged = false;

		final List<OmeroImageWrapper> imgWrappers = this
				.getImageWrapperToBeLoadedList();
		if (imgWrappers.size() > 5) {
			this.confirmDialog.setVisible(true);
			if (!this.confirmDialog.getConfirmation()) {
				this.setLoadingCanceled();
				return;
			}
		}

		final List<OmegaElement> loadedElements = new ArrayList<OmegaElement>();
		// TODO add all checks and sub checks
		OmegaExperimenter exp = OmeroImporterUtilities.loadAndAddExperimenter(
		        this.getGateway(), this.omegaData);
		if (exp != null) {
			dataChanged = OmeroImporterUtilities.loadAndAddGroups(exp,
			        this.getGateway(), this.omegaData);
		} else {
			exp = OmeroImporterUtilities.loadAndAddExperimenterAndGroups(
			        this.getGateway(), this.omegaData);
			dataChanged = true;
		}
		// Create pixels, image, dataset and project for the actual images
		// to load and add it to the main data
		for (final OmeroImageWrapper imageWrapper : this
				.getImageWrapperToBeLoadedList()) {
			dataChanged = OmeroImporterUtilities.loadAndAddData(imageWrapper,
			        this.getGateway(), this.omegaData, hasToSelect,
			        loadedElements, dataChanged);
		}

		if (dataChanged) {
			this.getPlugin().fireEvent(
					new OmegaPluginEventDataChanged(this.getPlugin(),
							loadedElements));
		}
		final List<OmegaImage> loadedImages = this.getLoadedImages();
		this.getProjectPanel().updateLoadedElements(loadedImages);
		this.getBrowserPanel().updateLoadedElements(loadedImages);
		this.getImageWrapperToBeLoadedList().clear();
	}

	private List<OmegaImage> getLoadedImages() {
		final List<OmegaImage> loadedImages = new ArrayList<OmegaImage>();
		for (final OmegaProject proj : this.omegaData.getProjects()) {
			for (final OmegaDataset dataset : proj.getDatasets()) {
				for (final OmegaImage img : dataset.getImages()) {
					loadedImages.add(img);
				}
			}
		}
		return loadedImages;
	}
}
