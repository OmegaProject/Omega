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
package edu.umassmed.omega.sptSbalzariniPlugin.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter;

import edu.umassmed.omega.commons.gui.GenericPanel;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxNode;
import edu.umassmed.omega.commons.gui.checkboxTree.CheckBoxStatus;
import edu.umassmed.omega.data.analysisRunElements.OmegaAnalysisRun;
import edu.umassmed.omega.data.analysisRunElements.OmegaParticleDetectionRun;
import edu.umassmed.omega.data.coreElements.OmegaElement;
import edu.umassmed.omega.data.coreElements.OmegaImage;
import edu.umassmed.omega.sptSbalzariniPlugin.SPTConstants;

public class SPTLoadedDataBrowserPanel extends GenericPanel {

	private static final long serialVersionUID = -7554854467725521545L;

	private final SPTPluginPanel sptPanel;

	private final Map<String, OmegaElement> nodeMap;
	private final DefaultMutableTreeNode root;

	private JTree dataTree;

	private boolean adjusting = false;

	public SPTLoadedDataBrowserPanel(final RootPaneContainer parentContainer,
			final SPTPluginPanel sptPanel) {
		super(parentContainer);

		this.sptPanel = sptPanel;

		this.root = new DefaultMutableTreeNode();
		this.root.setUserObject(SPTConstants.LOADED_DATA);
		this.nodeMap = new HashMap<String, OmegaElement>();
		// this.updateTree(images);

		this.setLayout(new BorderLayout());

		this.createAndAddWidgets();
		this.addListeners();
	}

	private void createAndAddWidgets() {

		this.dataTree = new JTree(this.root);
		this.dataTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// this.dataTreeBrowser.setRootVisible(false);
		// final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		// this.dataTree.setCellRenderer(renderer);
		// this.dataTree.setCellEditor(new CheckBoxNodeEditor());

		this.dataTree.setEditable(false);

		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		// this.dataTree.setEditable(true);

		final JScrollPane scrollPane = new JScrollPane(this.dataTree);
		scrollPane.setBorder(new TitledBorder(SPTConstants.LOADED_DATA));

		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void addListeners() {
		this.dataTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent event) {
				SPTLoadedDataBrowserPanel.this.handleMouseClick(event
						.getPoint());
			}
		});
		this.dataTree.getModel().addTreeModelListener(new TreeModelAdapter() {
			@Override
			public void treeNodesChanged(final TreeModelEvent event) {
				final TreePath parent = event.getTreePath();
				final Object[] children = event.getChildren();
				final DefaultTreeModel model = (DefaultTreeModel) event
						.getSource();
				SPTLoadedDataBrowserPanel.this.handleTreeNodeChanged(parent,
						children, model);
			}
		});
	}

	private void handleMouseClick(final Point clickP) {
		final TreePath path = SPTLoadedDataBrowserPanel.this.dataTree
				.getPathForLocation(clickP.x, clickP.y);
		if (path == null) {
			SPTLoadedDataBrowserPanel.this.sptPanel.updateSelectedImage(null);
			SPTLoadedDataBrowserPanel.this.deselect();
			return;
		}
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		final String s = node.toString();
		final OmegaElement element = SPTLoadedDataBrowserPanel.this.nodeMap
				.get(s);
		if (element instanceof OmegaImage) {
			SPTLoadedDataBrowserPanel.this.sptPanel
			.updateSelectedImage((OmegaImage) element);
		} else if (element instanceof OmegaAnalysisRun) {
			final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node
					.getParent();
			final String parentString = parentNode.toString();
			final OmegaElement parentElement = SPTLoadedDataBrowserPanel.this.nodeMap
					.get(parentString);
			SPTLoadedDataBrowserPanel.this.sptPanel
			.updateSelectedImage((OmegaImage) parentElement);
			SPTLoadedDataBrowserPanel.this.sptPanel
			.updateSelectedAnalysisRun((OmegaAnalysisRun) element);
		}
	}

	private void handleTreeNodeChanged(final TreePath parent,
			final Object[] children, final DefaultTreeModel model) {
		if (SPTLoadedDataBrowserPanel.this.adjusting)
			return;
		SPTLoadedDataBrowserPanel.this.adjusting = true;

		DefaultMutableTreeNode node;
		CheckBoxNode c; // = (CheckBoxNode)node.getUserObject();
		if ((children != null) && (children.length == 1)) {
			node = (DefaultMutableTreeNode) children[0];
			c = (CheckBoxNode) node.getUserObject();
			final DefaultMutableTreeNode n = (DefaultMutableTreeNode) parent
					.getLastPathComponent();

			model.nodeChanged(n);
		} else {
			node = (DefaultMutableTreeNode) model.getRoot();
			c = (CheckBoxNode) node.getUserObject();
		}

		model.nodeChanged(node);

		SPTLoadedDataBrowserPanel.this.adjusting = false;

		c.getStatus();
		// TODO update something here
	}

	@Override
	public void updateParentContainer(final RootPaneContainer parent) {
		super.updateParentContainer(parent);
	}

	public void updateTree(final List<OmegaImage> images) {
		this.dataTree.setRootVisible(true);

		String s = null;
		final CheckBoxStatus status = CheckBoxStatus.DESELECTED;
		this.root.removeAllChildren();
		((DefaultTreeModel) this.dataTree.getModel()).reload();
		this.nodeMap.clear();
		if (images != null) {
			for (final OmegaImage image : images) {
				final DefaultMutableTreeNode imageNode = new DefaultMutableTreeNode();
				s = "[" + image.getElementID() + "] " + image.getName();
				this.nodeMap.put(s, image);
				// status = this.loadedData.containsImage(image) ?
				// CheckBoxStatus.SELECTED
				// : CheckBoxStatus.DESELECTED;
				imageNode.setUserObject(new CheckBoxNode(s, status));

				for (final OmegaAnalysisRun analysisRun : image
						.getAnalysisRuns()) {
					if (analysisRun instanceof OmegaParticleDetectionRun) {
						final OmegaParticleDetectionRun particleDetectionRun = (OmegaParticleDetectionRun) analysisRun;
						// TODO pensare se questo e' il sistema migliore per
						// verificare il corretto funzionamento!
						if (!this.sptPanel
								.checkIfThisAlgorithm(particleDetectionRun)) {
							continue;
						}
						final DefaultMutableTreeNode analysisNode = new DefaultMutableTreeNode();
						s = "[" + particleDetectionRun.getElementID() + "] "
								+ particleDetectionRun.getName();
						this.nodeMap.put(s, particleDetectionRun);
						analysisNode.setUserObject(new CheckBoxNode(s, status));
						imageNode.add(analysisNode);
					}
				}

				this.root.add(imageNode);
			}
		}
		this.dataTree.expandRow(0);
		this.dataTree.setRootVisible(false);
		this.dataTree.repaint();
	}

	public void deselect() {
		this.dataTree.setSelectionRow(-1);
	}
}
