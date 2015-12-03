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
package main.java.edu.umassmed.omega.omegaDataBrowserPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import main.java.edu.umassmed.omega.commons.constants.OmegaGUIConstants;
import main.java.edu.umassmed.omega.commons.data.OmegaData;
import main.java.edu.umassmed.omega.commons.data.OmegaLoadedData;
import main.java.edu.umassmed.omega.commons.data.analysisRunElements.OmegaAnalysisRunContainer;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaDataset;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaElement;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaImage;
import main.java.edu.umassmed.omega.commons.data.coreElements.OmegaProject;
import main.java.edu.umassmed.omega.commons.gui.GenericElementInformationPanel;
import main.java.edu.umassmed.omega.commons.gui.GenericPanel;
import main.java.edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNode;
import main.java.edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNodeEditor;
import main.java.edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNodeRenderer;
import main.java.edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxStatus;
import main.java.edu.umassmed.omega.omegaDataBrowserPlugin.OmegaDataBrowserConstants;

public class OmegaDataBrowserLoadedDataBrowserPanel extends GenericPanel {

	private static final long serialVersionUID = -7554854467725521545L;

	private final OmegaDataBrowserPluginPanel browserPanel;

	private final Map<String, OmegaElement> nodeMap;
	private final DefaultMutableTreeNode root;
	private final OmegaLoadedData loadedData;

	private OmegaDataBrowserLoadedDataOptionsPanel optionsPanel;

	private JTree dataTree;

	private boolean adjusting = false;

	private GenericElementInformationPanel infoPanel;
	private JScrollPane scrollPane;

	public OmegaDataBrowserLoadedDataBrowserPanel(

			final RootPaneContainer parentContainer,
			final OmegaDataBrowserPluginPanel browserPanel,
			final OmegaData data, final OmegaLoadedData loadedData) {
		super(parentContainer);

		this.loadedData = loadedData;
		this.browserPanel = browserPanel;

		this.root = new DefaultMutableTreeNode();
		this.root.setUserObject(OmegaDataBrowserConstants.LOADED_DATA);
		this.nodeMap = new HashMap<String, OmegaElement>();

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();
		this.addListeners();

		this.updateTree(data);
	}

	private void createAndAddWidgets() {
		this.optionsPanel = new OmegaDataBrowserLoadedDataOptionsPanel(
		        this.getParentContainer());

		this.add(this.optionsPanel, BorderLayout.NORTH);

		this.dataTree = new JTree(this.root);
		this.dataTree.getSelectionModel().setSelectionMode(
		        TreeSelectionModel.SINGLE_TREE_SELECTION);
		// this.dataTreeBrowser.setRootVisible(false);
		final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		this.dataTree.setCellRenderer(renderer);
		this.dataTree.setCellEditor(new CheckBoxNodeEditor());

		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.setEditable(true);

		this.scrollPane = new JScrollPane(this.dataTree);
		this.scrollPane.setBorder(new TitledBorder(
		        OmegaDataBrowserConstants.LOADED_DATA));

		this.add(this.scrollPane, BorderLayout.CENTER);

		this.infoPanel = new GenericElementInformationPanel(
		        this.getParentContainer());

		this.add(this.infoPanel, BorderLayout.SOUTH);

	}

	private void addListeners() {
		this.dataTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				OmegaDataBrowserLoadedDataBrowserPanel.this.handleMouseClicked(
						evt.getX(), evt.getY());
			}
		});
		this.dataTree.getModel().addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeNodesChanged(final TreeModelEvent evt) {
				OmegaDataBrowserLoadedDataBrowserPanel.this
				        .handleTreeNodesChanged(
						(DefaultTreeModel) evt.getSource(),
				                evt.getTreePath(), evt.getChildren());
			}

			@Override
			public void treeNodesInserted(final TreeModelEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void treeNodesRemoved(final TreeModelEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void treeStructureChanged(final TreeModelEvent e) {
				// TODO Auto-generated method stub
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				OmegaDataBrowserLoadedDataBrowserPanel.this.handleResize();
			}
		});
	}

	private void handleResize() {
		final int width = this.getWidth() - 20;
		final int height = this.getHeight() - 10;

		this.scrollPane.setPreferredSize(new Dimension(width, height / 2));
		this.infoPanel.resizePanel(width, height / 2);

		this.revalidate();
		this.repaint();
	}

	private void handleMouseClicked(final int x, final int y) {
		final TreePath path = OmegaDataBrowserLoadedDataBrowserPanel.this.dataTree
				.getPathForLocation(x, y);
		if (path == null)
			return;
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		final String s = node.toString();
		final OmegaElement element = OmegaDataBrowserLoadedDataBrowserPanel.this.nodeMap
				.get(s);
		if (element instanceof OmegaAnalysisRunContainer) {
			OmegaDataBrowserLoadedDataBrowserPanel.this.browserPanel
			.setSelectedAnalysisContainer((OmegaAnalysisRunContainer) element);
			this.infoPanel.update(element);
		}
	}

	private void handleTreeNodesChanged(final DefaultTreeModel model,
			final TreePath treePath, final Object[] children) {
		if (OmegaDataBrowserLoadedDataBrowserPanel.this.adjusting)
			return;
		OmegaDataBrowserLoadedDataBrowserPanel.this.adjusting = true;
		DefaultMutableTreeNode node = null;
		CheckBoxNode c = null; // = (CheckBoxNode)node.getUserObject();
		if ((children != null) && (children.length == 1)) {
			node = (DefaultMutableTreeNode) children[0];
			c = (CheckBoxNode) node.getUserObject();
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) treePath
					.getLastPathComponent();
			if (OmegaDataBrowserLoadedDataBrowserPanel.this.optionsPanel
					.isAutoSelectRelatives()) {
				while (n != null) {
					OmegaDataBrowserLoadedDataBrowserPanel.this
					.updateParentUserObject(n);
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
		if ((c != null)
				&& OmegaDataBrowserLoadedDataBrowserPanel.this.optionsPanel
				.isAutoSelectRelatives()) {
			OmegaDataBrowserLoadedDataBrowserPanel.this
			.updateAllChildrenUserObject(node, c.getStatus());
		}
		// model.nodeChanged(node);

		OmegaDataBrowserLoadedDataBrowserPanel.this.adjusting = false;

		if (c == null)
			return;

		OmegaDataBrowserLoadedDataBrowserPanel.this.updateLoadedData(node,
				c.getStatus());
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
			OmegaDataBrowserLoadedDataBrowserPanel.this.updateLoadedData(
					parent, status);
		} else if (selectedCount == parent.getChildCount()) {
			final CheckBoxStatus status = CheckBoxStatus.SELECTED;
			parent.setUserObject(new CheckBoxNode(label, status));
			OmegaDataBrowserLoadedDataBrowserPanel.this.updateLoadedData(
					parent, status);
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

			OmegaDataBrowserLoadedDataBrowserPanel.this.updateLoadedData(node,
					status);
		}
	}

	private void updateLoadedData(final DefaultMutableTreeNode node,
			final CheckBoxStatus status) {
		final String s = node.toString();
		final OmegaElement element = this.nodeMap.get(s);
		if (status == CheckBoxStatus.SELECTED) {
			this.loadedData.addElement(element);
		} else if (status == CheckBoxStatus.DESELECTED) {
			this.loadedData.removeElement(element);
		}
		this.browserPanel.fireDataChangedEvent();
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
		this.optionsPanel.updateParentContainer(parent);
		this.infoPanel.updateParentContainer(parent);
	}

	// private List<TreePath> getExpandedPaths(final TreePath currentPath) {
	// final List<TreePath> expandedPaths = new ArrayList<TreePath>();
	// final Enumeration<TreePath> paths = this.dataTree
	// .getExpandedDescendants(currentPath);
	// if (paths == null)
	// return expandedPaths;
	// TreePath path = null;
	// while (paths.hasMoreElements()) {
	// path = paths.nextElement();
	// expandedPaths.add(path);
	// // expandedPaths.addAll(this.getExpandedPaths(path));
	// }
	// return expandedPaths;
	// }
	//
	// private void expandPathsIfExist(final List<TreePath> expandedPaths) {
	// for (final TreePath path : expandedPaths) {
	// final int row = this.dataTree.getRowForPath(path);
	// this.dataTree.expandRow(row);
	// }
	// }

	public void updateTree(final OmegaData data) {
		this.dataTree.setRootVisible(true);
		// final TreePath root = this.dataTree.getPathForRow(0);
		// final List<TreePath> expandedPaths = this.getExpandedPaths(root);

		String s = null;
		CheckBoxStatus status = null;
		this.root.removeAllChildren();
		((DefaultTreeModel) this.dataTree.getModel()).reload();
		this.nodeMap.clear();
		for (final OmegaProject project : data.getProjects()) {
			final DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode();
			s = "[" + project.getElementID() + "] " + project.getName();
			this.nodeMap.put(s, project);
			status = this.loadedData.containsProject(project) ? CheckBoxStatus.SELECTED
					: CheckBoxStatus.DESELECTED;
			projectNode.setUserObject(new CheckBoxNode(s, status));
			for (final OmegaDataset dataset : project.getDatasets()) {
				final DefaultMutableTreeNode datasetNode = new DefaultMutableTreeNode();

				s = "[" + dataset.getElementID() + "] " + dataset.getName();
				this.nodeMap.put(s, dataset);
				status = this.loadedData.containsDataset(dataset) ? CheckBoxStatus.SELECTED
						: CheckBoxStatus.DESELECTED;
				datasetNode.setUserObject(new CheckBoxNode(s, status));
				for (final OmegaImage image : dataset.getImages()) {
					final DefaultMutableTreeNode imageNode = new DefaultMutableTreeNode();

					s = "[" + image.getElementID() + "] " + image.getName();
					this.nodeMap.put(s, image);
					status = this.loadedData.containsImage(image) ? CheckBoxStatus.SELECTED
							: CheckBoxStatus.DESELECTED;
					imageNode.setUserObject(new CheckBoxNode(s, status));
					datasetNode.add(imageNode);
				}
				projectNode.add(datasetNode);
			}
			this.root.add(projectNode);
		}
		final DefaultMutableTreeNode orphanedNode = new DefaultMutableTreeNode();
		s = OmegaGUIConstants.PLUGIN_ORPHANED_ANALYSES;
		this.nodeMap.put(s, data.getOrphanedContainer());
		orphanedNode.setUserObject(new CheckBoxNode(s,
		        CheckBoxStatus.DESELECTED));
		this.root.add(orphanedNode);
		this.dataTree.expandRow(0);
		// this.expandPathsIfExist(expandedPaths);
		this.dataTree.setRootVisible(false);
		this.dataTree.repaint();
	}
}
