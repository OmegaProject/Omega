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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import omero.ServerError;

import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter;

import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.ProjectData;
import edu.umassmed.omega.commons.eventSystem.OmegaMessageEvent;
import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNode;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNodeEditor;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNodeRenderer;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxStatus;
import edu.umassmed.omega.dataNew.coreElements.OmegaDataset;
import edu.umassmed.omega.dataNew.coreElements.OmegaImage;
import edu.umassmed.omega.omeroPlugin.OmeroGateway;
import edu.umassmed.omega.omeroPlugin.data.OmeroDataWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroDatasetWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroExperimenterWrapper;
import edu.umassmed.omega.omeroPlugin.data.OmeroProjectWrapper;
import edu.umassmed.omega.omeroPlugin.runnable.OmeroListPanelProjectAndDatasetLoader;

public class OmeroTreePanel extends GenericPanel {

	private static final long serialVersionUID = -5868897435063007049L;

	private final List<OmeroDatasetWrapper> selectedDatasetList;
	private final List<OmeroExperimenterWrapper> expList;

	private final List<OmegaImage> loadedImages;

	// private final Map<ProjectData, List<DatasetData>> projDatasetsMap;
	// private final Map<DatasetData, List<ImageData>> datasetImagesMap;

	private final OmeroGateway gateway;
	private final OmeroPluginPanel pluginPanel;

	private final Map<String, OmeroDataWrapper> nodeMap;
	private final DefaultMutableTreeNode root;
	private CheckBoxNodeRenderer renderer;
	private CheckBoxNodeEditor editor;

	private JTree dataTree;

	private boolean adjusting = false;

	private OmeroDatasetWrapper actualSelection;

	public OmeroTreePanel(final RootPaneContainer parentContainer,
	        final OmeroPluginPanel pluginPanel, final OmeroGateway gateway) {
		super(parentContainer);

		this.actualSelection = null;
		this.selectedDatasetList = new ArrayList<OmeroDatasetWrapper>();

		this.expList = new ArrayList<OmeroExperimenterWrapper>();

		this.loadedImages = new ArrayList<OmegaImage>();
		// this.projDatasetsMap = new LinkedHashMap<ProjectData,
		// List<DatasetData>>();
		// this.datasetImagesMap = new LinkedHashMap<DatasetData,
		// List<ImageData>>();

		this.root = new DefaultMutableTreeNode();
		this.root.setUserObject("Loaded data");
		this.nodeMap = new HashMap<String, OmeroDataWrapper>();

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();
		this.addListeners();

		// this.updateTree();

		this.gateway = gateway;
		this.pluginPanel = pluginPanel;
		// this.setPreferredSize(new Dimension(300, 200));
		this.setLayout(new BorderLayout());
		this.createAndAddWidgets();
		this.addListeners();
	}

	public void createAndAddWidgets() {
		this.dataTree = new JTree(this.root);
		// this.dataTreeBrowser.setRootVisible(false);
		this.renderer = new CheckBoxNodeRenderer();
		this.editor = new CheckBoxNodeEditor();
		this.dataTree.setCellRenderer(this.renderer);
		this.dataTree.setCellEditor(this.editor);

		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.setEditable(true);

		final JScrollPane scrollPane = new JScrollPane(this.dataTree);
		scrollPane.setBorder(new TitledBorder("Loaded data"));

		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void addListeners() {
		this.dataTree.getModel().addTreeModelListener(new TreeModelAdapter() {
			@Override
			public void treeNodesChanged(final TreeModelEvent event) {
				if (OmeroTreePanel.this.adjusting)
					return;
				OmeroTreePanel.this.adjusting = true;
				final TreePath parent = event.getTreePath();
				final Object[] children = event.getChildren();
				final DefaultTreeModel model = (DefaultTreeModel) event
				        .getSource();

				DefaultMutableTreeNode node = null;
				CheckBoxNode c = null; // = (CheckBoxNode)node.getUserObject();
				if ((children != null) && (children.length == 1)) {
					node = (DefaultMutableTreeNode) children[0];
					c = (CheckBoxNode) node.getUserObject();
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) parent
					        .getLastPathComponent();
					if (true /* optionsPanel.isAutoSelectRelatives() */) {
						while (n != null) {
							OmeroTreePanel.this.updateParentUserObject(n);
							final DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) n
							        .getParent();
							if (tmp == null) {
								break;
							} else {
								n = tmp;
							}
						}
					}
					model.nodeChanged(n);
				} else {
					node = (DefaultMutableTreeNode) model.getRoot();
					// c = (CheckBoxNode) node.getUserObject();
				}
				if ((c != null) && true/* optionsPanel.isAutoSelectRelatives() */) {
					OmeroTreePanel.this.updateAllChildrenUserObject(node,
					        c.getStatus());
				}
				// model.nodeChanged(node);

				OmeroTreePanel.this.adjusting = false;

				if (c == null)
					return;

				OmeroTreePanel.this.updateSelectedData(node, c.getStatus());
			}
		});

		this.dataTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				final TreePath path = OmeroTreePanel.this.dataTree
				        .getPathForLocation(evt.getX(), evt.getY());
				if (path == null)
					return;
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				        .getLastPathComponent();
				final CheckBoxNode check = (CheckBoxNode) node.getUserObject();
				final String s = node.toString();
				final OmeroDataWrapper element = OmeroTreePanel.this.nodeMap
				        .get(s);
				if (element instanceof OmeroDatasetWrapper) {
					final OmeroDatasetWrapper datasetWrapper = (OmeroDatasetWrapper) element;
					if (OmeroTreePanel.this.actualSelection != datasetWrapper) {
						OmeroTreePanel.this.actualSelection = datasetWrapper;
						OmeroTreePanel.this.pluginPanel
						        .browseDataset(datasetWrapper);
					}
					OmeroTreePanel.this.pluginPanel.updateImagesSelection(check
					        .getStatus());
				}
			}
		});
	}

	private void updateParentUserObject(final DefaultMutableTreeNode parent) {
		if (parent.getUserObject() instanceof String)
			return;
		final String label = ((CheckBoxNode) parent.getUserObject()).getLabel();
		int selectedCount = 0;
		int indeterminateCount = 0;
		final Enumeration children = parent.children();
		while (children.hasMoreElements()) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) children
			        .nextElement();
			final CheckBoxNode check = (CheckBoxNode) node.getUserObject();
			if (check.getStatus() == CheckBoxStatus.INDETERMINATE) {
				indeterminateCount++;
				break;
			}
			if (check.getStatus() == CheckBoxStatus.SELECTED) {
				selectedCount++;
			}
		}
		if (indeterminateCount > 0) {
			parent.setUserObject(new CheckBoxNode(label));
		} else if (selectedCount == 0) {
			final CheckBoxStatus status = CheckBoxStatus.DESELECTED;
			parent.setUserObject(new CheckBoxNode(label, status));
			this.updateSelectedData(parent, status);
		} else if (selectedCount == parent.getChildCount()) {
			final CheckBoxStatus status = CheckBoxStatus.SELECTED;
			parent.setUserObject(new CheckBoxNode(label, status));
			this.updateSelectedData(parent, status);
		} else {
			parent.setUserObject(new CheckBoxNode(label));
		}
	}

	private void updateAllChildrenUserObject(final DefaultMutableTreeNode root,
	        final CheckBoxStatus status) {
		final Enumeration breadth = root.breadthFirstEnumeration();
		while (breadth.hasMoreElements()) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) breadth
			        .nextElement();
			if (root == node) {
				continue;
			}
			final CheckBoxNode check = (CheckBoxNode) node.getUserObject();
			node.setUserObject(new CheckBoxNode(check.getLabel(), status));

			this.updateSelectedData(node, status);

			final OmeroDataWrapper wrap = this.nodeMap.get(check.getLabel());
			if (wrap == this.actualSelection) {
				OmeroTreePanel.this.pluginPanel.updateImagesSelection(status);
			}
		}
	}

	private DefaultMutableTreeNode getActualNode() {
		final Enumeration children = this.root.breadthFirstEnumeration();
		while (children.hasMoreElements()) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) children
			        .nextElement();
			if (node.getUserObject() instanceof CheckBoxNode) {
				final CheckBoxNode check = (CheckBoxNode) node.getUserObject();
				if (check.getLabel().equals(
				        this.actualSelection.getStringRepresentation()))
					return node;
			}
		}
		return null;
	}

	private void updateSelectedData(final DefaultMutableTreeNode node,
	        final CheckBoxStatus status) {
		final String s = node.toString();
		final OmeroDataWrapper element = this.nodeMap.get(s);
		// TODO modify to consider different cases and loaded relative images
		// when needed
		if (element instanceof OmeroDatasetWrapper) {
			final OmeroDatasetWrapper datasetWrapper = (OmeroDatasetWrapper) element;
			if (status == CheckBoxStatus.SELECTED) {
				this.selectedDatasetList.add(datasetWrapper);
			} else if (status == CheckBoxStatus.DESELECTED) {
				this.selectedDatasetList.remove(datasetWrapper);
			}
		}
		// FIXME
		// this.browserPanel.fireDataChangedEvent();
	}

	public void resetExperimenterData() {
		this.expList.clear();
		// this.projDatasetsMap.clear();
		// this.datasetImagesMap.clear();
	}

	public void addExperimenterData(final ExperimenterData experimenterData)
	        throws ServerError {
		this.pluginPanel.updateMessageStatus(new OmegaMessageEvent(
		        "Loading projects and datasets"));
		final OmeroListPanelProjectAndDatasetLoader loader = new OmeroListPanelProjectAndDatasetLoader(
		        this.pluginPanel, this.gateway, experimenterData);
		final Thread t = new Thread(loader);
		t.start();
	}

	public void updateOmeData(final ExperimenterData expData,
	        final Map<ProjectData, List<DatasetData>> datas) {

		final OmeroExperimenterWrapper expWrapper = new OmeroExperimenterWrapper(
		        expData);

		expWrapper.setProjects(new ArrayList(datas.keySet()));

		for (final ProjectData proj : datas.keySet()) {
			expWrapper.setDatasets(proj, datas.get(proj));
		}

		this.expList.add(expWrapper);
	}

	public void removeExperimenterData(final ExperimenterData experimenterData) {
		OmeroExperimenterWrapper omeExpToRemove = null;
		for (final OmeroExperimenterWrapper omeExp : this.expList) {
			if (omeExp.getID() == experimenterData.getId()) {
				omeExpToRemove = omeExp;
				break;
			}
		}
		this.expList.remove(omeExpToRemove);
	}

	public void updateTree() {
		this.dataTree.setRootVisible(true);

		String s = null;
		CheckBoxStatus status = null;
		this.root.removeAllChildren();
		this.resetDisabledNodesList();

		((DefaultTreeModel) this.dataTree.getModel()).reload();
		this.nodeMap.clear();

		for (final OmeroExperimenterWrapper expWrapper : this.expList) {
			final int numOfProjects = expWrapper.getNumOfProjects();
			int projectsCounter = 0;
			final List<OmeroProjectWrapper> projects = expWrapper.getProjects();
			final DefaultMutableTreeNode expNode = new DefaultMutableTreeNode();
			for (final OmeroProjectWrapper projWrapper : projects) {
				final int numOfDatasets = projWrapper.getNumOfDatasets();
				int datasetsCounter = 0;
				final List<OmeroDatasetWrapper> datasets = projWrapper
				        .getDatasets();
				final DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode();
				for (final OmeroDatasetWrapper datasetWrapper : datasets) {
					final int numOfImages = datasetWrapper.getNumOfImages();
					final DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode();
					s = datasetWrapper.getStringRepresentation();
					this.nodeMap.put(s, datasetWrapper);
					if (this.isDatasetFullyLoaded(datasetWrapper.getID(),
					        numOfImages)) {
						status = CheckBoxStatus.SELECTED;
						this.addNodeToDisabledList(datasetNode);
						datasetsCounter++;
					} else {
						status = this.selectedDatasetList
						        .contains(datasetWrapper) ? CheckBoxStatus.SELECTED
						        : CheckBoxStatus.DESELECTED;
					}
					datasetNode.setUserObject(new CheckBoxNode(s, status));
					projectNode.add(datasetNode);
				}
				if (datasetsCounter == numOfDatasets) {
					status = CheckBoxStatus.SELECTED;
					this.addNodeToDisabledList(projectNode);
					projectsCounter++;
				} else {
					status = CheckBoxStatus.DESELECTED;
				}
				s = projWrapper.getStringRepresentation();
				this.nodeMap.put(s, projWrapper);
				projectNode.setUserObject(new CheckBoxNode(s, status));
				expNode.add(projectNode);
			}
			if (projectsCounter == numOfProjects) {
				status = CheckBoxStatus.SELECTED;
				this.addNodeToDisabledList(expNode);
			} else {
				status = CheckBoxStatus.DESELECTED;
			}
			s = expWrapper.getStringRepresentation();
			this.nodeMap.put(s, expWrapper);
			expNode.setUserObject(new CheckBoxNode(s, status));
			this.root.add(expNode);
		}

		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.repaint();
	}

	public List<OmeroDatasetWrapper> getSelectedDatasets() {
		return this.selectedDatasetList;
	}

	public void updateDatasetSelection(final int selectedImages) {
		final DefaultMutableTreeNode actualNode = this.getActualNode();
		if (actualNode == null)
			return;
		// TODO throw error here
		final CheckBoxNode check = (CheckBoxNode) actualNode.getUserObject();
		final int maxImages = this.actualSelection.getDatasetData().getImages()
		        .size();
		CheckBoxStatus status;
		if (selectedImages == 0) {
			status = CheckBoxStatus.DESELECTED;
		} else if (selectedImages == maxImages) {
			status = CheckBoxStatus.SELECTED;
		} else {
			status = CheckBoxStatus.INDETERMINATE;
		}
		actualNode.setUserObject(new CheckBoxNode(check.getLabel(), status));
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) actualNode
		        .getParent();
		while (parent != null) {
			this.updateParentUserObject(parent);
			parent = (DefaultMutableTreeNode) parent.getParent();
		}

		// this.updateAllChildrenUserObject(actualNode, status);
		this.repaint();
	}

	private void resetDisabledNodesList() {
		this.renderer.resetDisabledNodesList();
		this.editor.resetDisabledNodesList();
	}

	private void addNodeToDisabledList(final DefaultMutableTreeNode node) {
		this.renderer.addNodeToDisabledList(node);
		this.editor.addNodeToDisabledList(node);
	}

	private boolean isDatasetFullyLoaded(final long datasetID,
	        final int datasetSize) {
		int imagesLoaded = 0;
		for (final OmegaImage img : this.loadedImages) {
			for (final OmegaDataset dataset : img.getParentDatasets())
				if (dataset.getElementID() == datasetID) {
					imagesLoaded++;
					break;
				}
		}
		if (imagesLoaded == datasetSize)
			return true;
		return false;
	}

	public void updateLoadedElements(final List<OmegaImage> loadedImages) {
		this.loadedImages.clear();
		this.loadedImages.addAll(loadedImages);
		this.selectedDatasetList.clear();
		this.updateTree();
	}
}
